package tap.shortest_path_client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;




public class RestServiceClient implements IRestServiceClient {

	private String urlToAll,urlToPath,urlToGrid;

	public RestServiceClient() {
		
	}
	public RestServiceClient(String urlToAll, String urlToPath, String urlToGrid) {
		this.urlToAll=urlToAll;
		this.urlToPath=urlToPath;
		this.urlToGrid=urlToGrid;
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
			case Request.REQUEST_GRID:{
				return manageGrid(args);
			}
			default:{
				throw new IllegalArgumentException("Unvalid request");
				
			}
		}
	}
	private String manage(String url, String args) {
		try {
			HttpURLConnection conn=getConnection(url+args);
			
			return read(conn);
		}catch(IOException exc) {
			exc.printStackTrace();
			return "CONNECTION REFUSED";
		}
	}
	private String manageGrid(String args) {
		return manage(urlToGrid,args);
	}
	private String managePath(String args)  {
		return manage(urlToPath,args);
		
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
		return manage(urlToAll,"");
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
	public String getUrlToGrid() {
		return urlToGrid;
	}
	public void setUrlToGrid(String urlToGrid) {
		this.urlToGrid = urlToGrid;
	}

	
}
