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
	private Gson gson;
	private IRestServiceClient service;
	@Before
	public void setUp() {
		client=new Client();
		client.setRestServiceClient(service=Mockito.mock(IRestServiceClient.class));
		gson=new Gson();
	}
	@Test
	public void testAddNode() throws IOException {
		Node n;
		client.addNode(n=new Node());
		verify(service,times(1)).connect();
		verify(service,times(1)).doPost(Request.REQUEST_ADD,gson.toJson(n));
	
		
	}
	
	@Test
	public void testGetPath() throws IOException {
		Node from,to;
		client.getShortestPath(from=new Node(),to=new Node());
		verify(service,times(1)).connect();
		verify(service,times(1)).doPost(Request.REQUEST_PATH, gson.toJson(from)+"->"+gson.toJson(to));
	
	}
	
	@Test
	public void testGetAllNodes() throws IOException {
		
		client.getAllNodes();
		verify(service,times(1)).connect();
		verify(service,times(1)).doPost(Request.REQUEST_ALL, null);
		
	}
	
	@Test
	public void testRemNode() throws IOException {
		client.remNode("node1");
		verify(service,times(1)).connect();
		verify(service,times(1)).doPost(Request.REQUEST_REMOVE, "node1");
		
	}
	
	

}
