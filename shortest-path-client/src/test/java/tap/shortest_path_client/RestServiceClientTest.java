package tap.shortest_path_client;


import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

public class RestServiceClientTest {

	private RestServiceClient client;
	private URL url;
	@Before
	public void setUp() throws MalformedURLException {
		client=new RestServiceClient();
		url=new URL("http://localhost:8080");
		
	}
	@Test
	public void testConnect() throws IOException {
		client.setUrl(url);
		client.connect();
		
	}
	@Test(expected=IllegalArgumentException.class)
	public void testWrongPostRequest() throws IOException {
		client.doPost(-1, "");
		
	}
	@Test
	public void testAddNodePostRequest() throws IOException {
		client.doPost(Request.REQUEST_ADD, "node1");
		fail("Not yet implemented");
	}
	@Test
	public void testRemoveNodePostRequest() throws IOException {
		client.doPost(Request.REQUEST_REMOVE, "node1");
		fail("Not yet implemented");
	}
	@Test
	public void testPathPostRequest() throws IOException {
		client.doPost(Request.REQUEST_PATH, "node1");
		fail("Not yet implemented");
	}
	@Test
	public void testAllNodesPostRequest() throws IOException {
		client.doPost(Request.REQUEST_ALL, "node1");
		fail("Not yet implemented");
	}

}
