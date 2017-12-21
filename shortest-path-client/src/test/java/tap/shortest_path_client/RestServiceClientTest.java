package tap.shortest_path_client;


import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

public class RestServiceClientTest {

	private RestServiceClient client;
	
	@Before
	public void setUp() throws MalformedURLException {
		client=new RestServiceClient();

	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testWrongPostRequest() {
		client.doGet(-1, "");
		
	}
	@Test
	public void testRequestAllNodes() {
		assertEquals("CONNECTION REFUSED",client.doGet(Request.REQUEST_ALL, null));
	}
	@Test
	public void testRequestPath() {
		assertEquals("CONNECTION REFUSED",client.doGet(Request.REQUEST_PATH, null));
	}
}
