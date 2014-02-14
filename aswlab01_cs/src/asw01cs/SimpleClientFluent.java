package asw01cs;


import org.apache.http.client.fluent.Request;


public class SimpleClientFluent {

    public final static void main(String[] args) throws Exception {
    	
    	/* Insert code for Task #5 here */
    	
    	System.out.println(Request.Get("http://localhost:8080/aswlab01_ss").execute().returnContent());
    }
}

