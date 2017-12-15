package tap.shortest_path_client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;




public class RestServiceClient implements IRestServiceClient {

	private String urlToAll,urlToPath;

	public RestServiceClient() {
		
	}
	public RestServiceClient(String urlToAll, String urlToPath) {
		this.urlToAll=urlToAll;
		this.urlToPath=urlToPath;
		
	}
	
	
	private String read(HttpURLConnection conn) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
		StringBuilder sb=new StringBuilder();
		String line=br.readLine();
		while(line!=null) {
			sb.append(line);
			line=br.readLine();
		}
		return sb.toString();
	}
	public String doGet(int request, String args) {
		
		
		
		
		switch(request) {
		
		case Request.REQUEST_ALL:{
			
			return manageAll();
		}
		case Request.REQUEST_PATH:{
			
			return managePath(args);
			
		}
		
		default:{
			throw new IllegalArgumentException("Unvalid request");
			
		}
		}
		
		
		
	}
	private String managePath(String args)  {
		try {
			HttpURLConnection conn=getConnection(urlToPath+args);
			return read(conn);
		}catch(IOException exc) {
			return "PATH: CONNECTION REFUSED";
		}
	}
	private HttpURLConnection getConnection(String url) throws IOException {
		
		URL _url = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		return conn;

	}
	private String manageAll()  {
		try {
		HttpURLConnection conn=getConnection(urlToAll);
		return read(conn);
		}catch(IOException exc) {
			return "ALL: CONNECTION REFUSED";
		}
	}
	
	public String getUrlToPath() {
		return urlToPath;
	}
	public void setUrlToPath(String urlToPath) {
		this.urlToPath = urlToPath;
	}
	public String getUrlToAll() {
		return urlToAll;
	}
	public void setUrlToAll(String urlToAll) {
		this.urlToAll = urlToAll;
	}

	
}
