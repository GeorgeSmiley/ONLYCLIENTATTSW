package tap.shortest_path_client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import sun.misc.BASE64Encoder;




public class RestServiceClient implements IRestServiceClient {

	private String urlToAll,urlToPath,urlToGrid,username,password;
	
	public RestServiceClient() {
		
	}
	public RestServiceClient(String urlToAll, String urlToPath, String urlToGrid, String user, String password) throws RuntimeException, IOException {
		this.urlToAll=urlToAll;
		this.urlToPath=urlToPath;
		this.urlToGrid=urlToGrid;
		this.username=user;
		this.password=password;
		doLogin();
	}
	
	
	private void doLogin() throws RuntimeException, IOException {
		getConnection(urlToAll);
		
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
	
	private HttpURLConnection getConnection(String url) throws IOException,RuntimeException {
		
		URL _url = new URL(url);
	      HttpURLConnection connection = (HttpURLConnection)_url.openConnection();
	      connection.setRequestMethod("GET");
	      BASE64Encoder enc = new sun.misc.BASE64Encoder();
	      String userpassword = username + ":" + password;
	      String encodedAuthorization = enc.encode( userpassword.getBytes() );
	      connection.setRequestProperty("Authorization", "Basic "+
	            encodedAuthorization);
	      int resp=connection.getResponseCode();
	      if(resp==401) throw new RuntimeException("Wrong username or password");
	      if(resp!=200) throw new RuntimeException("Cannot comunicate with server");
	      return connection;
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
