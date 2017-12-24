package tap.shortest_path_client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;







public class RestServiceClient implements IRestServiceClient {

	private String urlToAll,urlToPath,urlToGrid,username,password;
	private int resp;
	public RestServiceClient() {
		
	}
	public RestServiceClient(String urlToAll, String user, String password) {
		this.urlToAll=urlToAll;
		this.urlToPath=urlToAll+Messages.getString("RestServiceClient.DEFAULT_PPATH"); 
		this.urlToGrid=urlToAll+Messages.getString("RestServiceClient.DEFAULT_GPATH"); 
		this.username=user;
		this.password=password;
		
		
		
	}
	
	public int getLastResponse() {
		return this.resp;
	}
	public void doLogin() throws IOException {
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
	
	public String doGet(int request, String args) throws IOException {
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
				throw new IllegalArgumentException(Messages.getString("RestServiceClient.UNVALID_REQUEST")); 
				
			}
		}
	}
	
	private String manage(String url, String args) throws IOException {
		
			HttpURLConnection conn=getConnection(url+args);
			if(conn!=null)
				return read(conn);
			return null;
		
	}
	private String manageGrid(String args) throws IOException {
		return manage(urlToGrid,args);
	}
	private String managePath(String args) throws IOException  {
		return manage(urlToPath,args);
		
	}
	
	private HttpURLConnection getConnection(String url) throws IOException,RuntimeException {
		
		HttpURLConnection connection = createConnection(url); 
	    String encodedAuthorization = encodeUserPass();
	    setRequest(connection, encodedAuthorization);
	    this.resp=connection.getResponseCode();
	    return connection;
	}
	
	
	private void setRequest(HttpURLConnection connection, String encodedAuthorization) {
		connection.setRequestProperty(Messages.getString("RestServiceClient.AUTH"), Messages.getString("RestServiceClient.ENCODE_BASIC")+ 
	            encodedAuthorization);
	}

	private String encodeUserPass() {
		Base64 encoder=new Base64();
	    String userpassword = username + ":" + password; 
	    byte[] encodeduserpass= encoder.encode( userpassword.getBytes() );
		return new String(encodeduserpass);
	}
	private HttpURLConnection createConnection(String url)
			throws MalformedURLException, IOException, ProtocolException {
		URL _url = new URL(url);
	    HttpURLConnection connection = (HttpURLConnection)_url.openConnection();
	    connection.setRequestMethod(Messages.getString("RestServiceClient.GET_METHOD"));
		return connection;
	}
	
	private String manageAll() throws IOException  {
		return manage(urlToAll,Messages.getString("RestServiceClient.EMPTY_STRING")); 
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
