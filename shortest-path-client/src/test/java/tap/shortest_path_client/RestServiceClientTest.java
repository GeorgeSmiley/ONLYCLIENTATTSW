package tap.shortest_path_client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

 


public class RestServiceClientTest {
	
	private RestServiceClient fixture;
	private WireMockServer mockedServer;
	@Before
	public void setup() {
		this.mockedServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8000));
	      WireMock.configureFor("localhost", 8000);
	      mockedServer.start();
	      this.fixture=new RestServiceClient("http://localhost:8000/api/","user","password");
	}
	@After
	public void teardown() {
		this.mockedServer.stop();
	}
	private void stubResponse(String url,String body, int responseCode) {
		 stubFor(get(urlEqualTo(url))
		          .willReturn(aResponse()
		              .withStatus(responseCode)
		              .withHeader("Content-Type", "application/json")
		              .withBody(body)));
	}
	@Test
	public void testDoLoginOK() throws RuntimeException, IOException {
		
	      stubResponse("/api/","{\"success\" : true, \"error\" : {\"type\" : \"NotAnError\" , \"description\" : \"NA\"}}",200);
	      fixture.doLogin();
	      assertEquals(200,fixture.getLastResponse());
	}
		
	@Test
	public void testdoLoginWhenWrongUserPass() throws RuntimeException, IOException {
		stubResponse("/api/","{\"success\" : false, \"error\" : {\"type\" : \"Wrong UserPass\" , \"description\" : \"NA\"}}",401);
		fixture.doLogin();
		assertEquals(401,fixture.getLastResponse());
		
	}
	@Test(expected=IOException.class)
	public void testdoLoginWhenServerUnreacheable() throws RuntimeException, IOException {
		mockedServer.stop();
		fixture.doLogin();
		assertEquals(0,fixture.getLastResponse());
	}
	@Test(expected=IllegalArgumentException.class)
	public void testDoGetWhenWrongRequest() throws IOException {
		String resp=fixture.doGet(-1, null);
		assertEquals(0,fixture.getLastResponse());
		assertNull(resp);
	}
	@Test
	public void testDoGetRequestAllOKEmptyList() throws IOException {
		stubResponse("/api/","[]",200);
		String response=fixture.doGet(Request.REQUEST_ALL, null);
		assertEquals(200,fixture.getLastResponse());
		assertEquals("[]",response);
	}
	@Test
	public void testDoGetRequestAllOKSingleElementList() throws IOException {
		stubResponse("/api/","[1]",200);
		String response=fixture.doGet(Request.REQUEST_ALL, null);
		assertEquals(200,fixture.getLastResponse());
		assertEquals("[1]",response);
	}
	@Test
	public void testDoGetRequestAllOKRegularList() throws IOException {
		stubResponse("/api/","[1,2,3,4,5]",200);
		String response=fixture.doGet(Request.REQUEST_ALL, null);
		assertEquals(200,fixture.getLastResponse());
		assertEquals("[1,2,3,4,5]",response);
		
	}
	@Test(expected=IOException.class)
	public void testDoGetRequestAllWrongWhenServerBecameUnreachable() throws IOException {
		mockedServer.stop();
		String response=fixture.doGet(Request.REQUEST_ALL, null);
		assertEquals(0,fixture.getLastResponse());
		assertNull(response);
	}
	@Test(expected=IOException.class)
	public void testDoGetRequestAllWrongWhenServerCannotPerformOperation() throws IOException {
		stubResponse("/api/","An error",500);
		String response=fixture.doGet(Request.REQUEST_ALL, null);
		assertEquals(500,fixture.getLastResponse());
		assertNull(response);
	}
	@Test
	public void testDoGetRequestAGridOK() throws IOException {
		String jsongrid="{\"n\":5,\"matrix\":[[1,0,1,1,0],[1,1,1,1,1],[1,1,1,0,1],[1,0,1,1,0],[1,1,1,1,1]],\"id\":1}";
		stubResponse("/api/grid1",jsongrid,200);
		String received=fixture.doGet(Request.REQUEST_GRID,"1");
		assertEquals(200,fixture.getLastResponse());
		assertEquals(jsongrid,received);
	}
	@Test(expected=IOException.class)
	public void testDoGetRequestAGridWrongWhenServerBecameUnreachable() throws IOException {
		mockedServer.stop();
		String received=fixture.doGet(Request.REQUEST_GRID,"1");
		assertEquals(0,fixture.getLastResponse());
		assertNull(received);
		
	}
	@Test(expected=IOException.class)
	public void testDoGetRequestGridWrongWhenServerCannotPerformOperation() throws IOException {
		stubResponse("/api/grid2","Error",500);
		String received=fixture.doGet(Request.REQUEST_GRID, "2");
		assertEquals(500,fixture.getLastResponse());
		assertNull(received);
	}
	@Test
	public void testDoGetRequestPathOK() throws IOException {
		String expected="[(A),(B)]";
		stubResponse("/api/pathATOBINgrid2",expected,200);
		String received=fixture.doGet(Request.REQUEST_PATH,"ATOBINgrid2");
		assertEquals(200,fixture.getLastResponse());
		assertEquals(expected,received);
	}
	@Test(expected=IOException.class)
	public void testDoGetRequestPathWrongWhenServerBecameUnreachable() throws IOException {
		mockedServer.stop();
		String received=fixture.doGet(Request.REQUEST_PATH,"ATOBINgrid2");
		assertEquals(0,fixture.getLastResponse());
		assertNull(received);
	}
	@Test(expected=IOException.class)
	public void testDoGetRequestPathWrongWhenServerCannotPerformOperation() throws IOException {
		stubResponse("/api/pathATOBINgrid2","Error",500);
		String received=fixture.doGet(Request.REQUEST_PATH,"ATOBINgrid2");
		assertEquals(500,fixture.getLastResponse());
		assertNull(received);
	}
	@Test
	public void testDoGetRequestPathOkWhenServerReturnsAnEmptyList() throws IOException {
		stubResponse("/api/pathATOBINgrid2","[]",200);
		String received=fixture.doGet(Request.REQUEST_PATH,"ATOBINgrid2");
		assertEquals(200,fixture.getLastResponse());
		assertEquals("[]",received);
	}
	@Test
	public void testDoGetRequestPathOkWhenServerReturnsAnSingleElementList() throws IOException {
		stubResponse("/api/pathATOBINgrid2","[1]",200);
		String received=fixture.doGet(Request.REQUEST_PATH,"ATOBINgrid2");
		assertEquals(200,fixture.getLastResponse());
		assertEquals("[1]",received);
	}
	

}
