package tap.shortest_path_client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonElement;

public class RestServiceClient implements IRestServiceClient {

	private URL url;
	private HttpURLConnection conn;
	public RestServiceClient(String host, int port) throws MalformedURLException {
		url=new URL("http://"+host+":"+port);
		
	}
	public void connect() throws IOException {
		conn=(HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		
	}

	public JsonElement doPost(int request, String args) {
		switch(request) {
		case Request.REQUEST_ADD:{
			
			break;
		}
		case Request.REQUEST_ALL:{
			break;
		}
		case Request.REQUEST_PATH:{
			break;
		}
		case Request.REQUEST_REMOVE:{
			break;
		}
		default:{
			return null;
		}
		}
		return null;
		
	}

	public void disconnect() {
		this.conn=null;
		
	}

}
