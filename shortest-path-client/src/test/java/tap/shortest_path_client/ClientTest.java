package tap.shortest_path_client;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;


public class ClientTest {
	private Client client;
	private IRestServiceClient service;
	@Before
	public void setUp() {
		client=new Client();
		client.setRestServiceClient(service=Mockito.mock(IRestServiceClient.class));
		
	}
	
	
	@Test
	public void testGetPath() throws IOException {
		String fromName="node1";
		String toName="node2";
		client.getShortestPath(fromName,toName);
		verify(service,times(1)).doGet(Request.REQUEST_PATH, fromName+"TO"+toName);
	
	}
	
	@Test
	public void testGetAllNodes() throws IOException {
		
		client.getAllNodes();
		verify(service,times(1)).doGet(Request.REQUEST_ALL, null);
		
	}
	
	
	
	

}
