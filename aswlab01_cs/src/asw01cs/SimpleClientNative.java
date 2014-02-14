package asw01cs;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class SimpleClientNative {

	public final static void main(String[] args) throws Exception {


		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		/* Insert code for Task #5 here */

		HttpGet httpget = new HttpGet("http://localhost:8080/aswlab01_ss");
		CloseableHttpResponse response1 = httpclient.execute(httpget);
		try {
			HttpEntity entity1 = response1.getEntity();
			entity1.writeTo(System.out);
			EntityUtils.consume(entity1);
		} 
		finally {
			response1.close();
		}


	}

}

