package tap.shortest_path_client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


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
	private void whenRequest(int request,String args, String thenReturn) throws IOException {
		when(service.doGet(request, args)).thenReturn(thenReturn);
	}
	@Test
	public void testGetPathWhenIsNull() throws IOException {
		whenRequest(Request.REQUEST_PATH,Mockito.anyString()+"TO"+Mockito.anyString(),"null");
		assertNull(client.getShortestPath("", ""));
		
	}
	@Test
	public void testGetPathWhenExists() throws IOException {
		whenRequest(Request.REQUEST_PATH,"fromTOdest","{\"name\":\"path\"}");
		Node actual=client.getShortestPath("from", "dest");
		assertEquals(new Node("path"),actual);
	}
	@Test
	public void testGetAllWhenNoNodeAreInTheServer() throws IOException{
		whenRequest(Request.REQUEST_ALL,null,"null");
		assertNull(client.getAllNodes());
		
	}
	@Test
	public void testGetAllNodesWhenServerCanProvideThem() throws IOException{
		whenRequest(Request.REQUEST_ALL,null,"{\"name\":\"root\"}");
		Node actual=client.getAllNodes();
		assertEquals(new Node("root"),actual);
	}
	
	
	

}
