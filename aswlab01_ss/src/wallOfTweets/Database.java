package wallOfTweets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class Database {

	private static SimpleDateFormat mySQLTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Connection dbCon;
	
	public static void setConnection(Connection con) {
		dbCon = con;
	}

	public static Vector<Tweet> getTweets() throws SQLException 
	{
		Vector<Tweet> result = new Vector<Tweet>();
		Statement stmt = dbCon.createStatement();
		String query = "select * from tweets order by twTIME desc";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			Tweet tt = new Tweet();
			tt.setDate(mySQLTimeStamp.parse(rs.getString("twTIME"), new ParsePosition(0)));
			tt.setAuthor(rs.getString("twAUTHOR"));
			tt.setText(rs.getString("twTEXT"));
			result.add(tt);
		}
		rs.close();
		stmt.close();
		return result;
	}

	public static void insertTweet(String author, String text) throws SQLException
	{
		if (text != null && !text.equals(""))
		{		
			if (author == null || author.equals("")) author ="Anonymous";
			String insert = "insert into tweets(twAUTHOR, twTEXT) values (?, ?)";
			PreparedStatement pst = dbCon.prepareStatement(insert);
			pst.setString(1, author);
			pst.setString(2, text);
			pst.executeUpdate();
			pst.close();
		}
	}
}
