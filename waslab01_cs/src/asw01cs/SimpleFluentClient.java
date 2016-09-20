package asw01cs;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
//This code uses the Fluent API

public class SimpleFluentClient {

	public final static void main(String[] args) throws Exception {
		/* Insert code for Task #4 here */

		System.out.println(Request.Post("http://localhost:8080/waslab01_ss/wot").addHeader("Accept", "text/plain")
				.bodyForm(Form.form().add("author", "Unicorn").add("tweet_text", "I Love rainbows").build()).execute()
				.returnContent());

		System.out.println(Request.Get("http://localhost:8080/waslab01_ss").addHeader("Accept", "text/plain").execute()
				.returnContent());

		/* Insert code for Task #5 here */
	}
}
