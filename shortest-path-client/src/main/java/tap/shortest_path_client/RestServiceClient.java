package tap.shortest_path_client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import sun.misc.BASE64Encoder;




public class RestServiceClient implements IRestServiceClient {

	private String urlToAll,urlToPath,urlToGrid,username,password;
	
	public RestServiceClient() {
		
	}
	public RestServiceClient(String urlToAll, String user, String password) {
		this.urlToAll=urlToAll;
		this.urlToPath=urlToAll+Messages.getString("RestServiceClient.DEFAULT_PPATH"); 
		this.urlToGrid=urlToAll+Messages.getString("RestServiceClient.DEFAULT_GPATH"); 
		this.username=user;
		this.password=password;
		
		
		
	}
	
	
	public void doLogin() throws RuntimeException, IOException {
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
				throw new IllegalArgumentException(Messages.getString("RestServiceClient.UNVALID_REQUEST")); 
				
			}
		}
	}
	
	private String manage(String url, String args) {
		try {
			HttpURLConnection conn=getConnection(url+args);
			
			return read(conn);
		}catch(IOException exc) {

			return Messages.getString("RestServiceClient.CONNECTION_REFUSED_EXCEPTION"); 
		}
	}
	private String manageGrid(String args) {
		return manage(urlToGrid,args);
	}
	private String managePath(String args)  {
		return manage(urlToPath,args);
		
	}
	
	private HttpURLConnection getConnection(String url) throws IOException,RuntimeException {
		
		HttpURLConnection connection = createConnection(url); 
	    String encodedAuthorization = encodeUserPass();
	    setRequest(connection, encodedAuthorization);
	    checkServerResponse(connection); 
	    return connection;
	}
	private void checkServerResponse(HttpURLConnection connection) throws IOException {
		int resp=connection.getResponseCode();
	    if(resp==401) throw new RuntimeException(Messages.getString("RestServiceClient.WRONG_USERPASS_EXCEPTION")); 
	    if(resp!=200) throw new RuntimeException(Messages.getString("RestServiceClient.SERVER_RETURNED_ERROR_EXCEPTION")+resp);
	}
	private void setRequest(HttpURLConnection connection, String encodedAuthorization) {
		connection.setRequestProperty(Messages.getString("RestServiceClient.AUTH"), Messages.getString("RestServiceClient.ENCODE_BASIC")+ 
	            encodedAuthorization);
	}
	@SuppressWarnings("restriction")
	private String encodeUserPass() {
		BASE64Encoder enc = new sun.misc.BASE64Encoder();
	    String userpassword = username + ":" + password; 
	    String encodedAuthorization = enc.encode( userpassword.getBytes() );
		return encodedAuthorization;
	}
	private HttpURLConnection createConnection(String url)
			throws MalformedURLException, IOException, ProtocolException {
		URL _url = new URL(url);
	    HttpURLConnection connection = (HttpURLConnection)_url.openConnection();
	    connection.setRequestMethod(Messages.getString("RestServiceClient.GET_METHOD"));
		return connection;
	}
	
	private String manageAll()  {
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
