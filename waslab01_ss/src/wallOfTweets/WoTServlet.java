
package wallOfTweets;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WoTServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5207702297571272100L;
	Locale currentLocale = new Locale("en");
	String ENCODING = "ISO-8859-1";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			Vector<Tweet> tweets = Database.getTweets();
			if (req.getHeader("Accept").equals("text/plain")) {
				printPLAINresult(tweets, req, res);
			} else {
				printHTMLresult(tweets, req, res);
			}
		}

		catch (SQLException ex) {
			throw new ServletException(ex);
		}
	}

	private void printPLAINresult(Vector<Tweet> tweets, HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		res.setContentType("text/plain");

		PrintWriter out = res.getWriter();

		for (Tweet tweet : tweets) {
			out.println("tweet #" + tweet.getTwid() + ": " + tweet.getAuthor() + ": " + tweet.getText() + " ["
					+ tweet.getDate() + "]");
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String param_tweet_id = req.getParameter("tweet_id");
		Long tweet_id = null;
		if (param_tweet_id == null) {
			String tweet = req.getParameter("tweet_text");
			String author = req.getParameter("author");

			try {
				MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				messageDigest.update(author.getBytes());
				byte[] sum = messageDigest.digest();
				BigInteger bigInteger = new BigInteger(1,sum);
				String hash = bigInteger.toString(64);
				tweet_id = Database.insertTweet(author, tweet);
				res.addCookie(new Cookie("author", hash));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				Cookie[] cookies = req.getCookies();
				String cookie_author = "I'm not the author";
				tweet_id = Long.parseLong(param_tweet_id);
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("author")) {
						cookie_author = cookie.getValue();
					}
				}
				Vector<Tweet> tweets = Database.getTweets();
				for (Tweet tweet : tweets) {
					if (tweet.getTwid() == tweet_id) {
						messageDigest.update(tweet.getAuthor().getBytes());
						byte[] sum = messageDigest.digest();
						String hash = new BigInteger(1,sum).toString(64);
						if (hash.equals(cookie_author)) {
							Database.deleteTweet(tweet_id);
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (req.getHeader("Accept").equals("text/plain")) {
			PrintWriter out = res.getWriter();
			out.print(tweet_id);
		} else {
			// This method does NOTHING but to redirect to the main page
			res.sendRedirect("wot");
		}

	}

	private void printHTMLresult(Vector<Tweet> tweets, HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, currentLocale);
		DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT, currentLocale);
		String currentDate = dateFormatter.format(new java.util.Date());
		res.setContentType("text/html");
		res.setCharacterEncoding(ENCODING);
		PrintWriter out = res.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<html>");
		out.println("<head><title>Wall of Tweets</title>");
		out.println("<link href=\"wallstyle.css\" rel=\"stylesheet\" type=\"text/css\" />");
		out.println("</head>");
		out.println("<body class=\"wallbody\">");
		out.println("<h1>Wall of Tweets</h1>");
		out.println("<div class=\"walltweet\">");
		out.println("<form action=\"wot\" method=\"post\">");
		out.println("<table border=0 cellpadding=2>");
		out.println("<tr><td>Your name:</td><td><input name=\"author\" type=\"text\" size=70></td><td/></tr>");
		out.println(
				"<tr><td>Your tweet:</td><td><textarea name=\"tweet_text\" rows=\"2\" cols=\"70\" wrap></textarea></td>");
		out.println("<td><input type=\"submit\" name=\"action\" value=\"Tweet!\"></td></tr>");
		out.println("</table></form></div>");
		for (Tweet tweet : tweets) {
			String messDate = dateFormatter.format(tweet.getDate());
			if (!currentDate.equals(messDate)) {
				out.println("<br><h3>...... " + messDate + "</h3>");
				currentDate = messDate;
			}
			out.println("<div class=\"wallitem\">");
			out.println("<h4><em>" + tweet.getAuthor() + "</em> @ " + timeFormatter.format(tweet.getDate()) + "</h4>");
			out.println("<form action=\"wot\" method=\"post\">");
			out.println("<input type=\"hidden\" name=\"tweet_id\" value=" + tweet.getTwid() + ">");
			out.println("<input type=\"submit\" name=\"action\" value=\"Delete\">");
			out.println("</form>");
			out.println("<p>" + tweet.getText() + "</p>");
			out.println("</div>");
		}
		out.println("</body></html>");
	}
}
