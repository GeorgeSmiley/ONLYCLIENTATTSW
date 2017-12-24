package uitests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.AWTException;
import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.JComboBox;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import com.google.gson.JsonSyntaxException;

import tap.shortes_path_client.gui.GUI;
import tap.shortes_path_client.gui.GUIpanel;
import tap.shortes_path_client.gui.Messages;
import tap.shortest_path_client.GridFromServer;
import tap.shortest_path_client.IClient;
import tap.shortest_path_client.IRestServiceClient;

@RunWith(JUnit4.class)
public class GUITest  {
	private FrameFixture window;
	private IClient cl;
	private GUI frame;
	@Before
	public void setUp() throws AWTException {
		frame = GuiActionRunner.execute(()-> new GUI());
		frame.mockClient(cl=Mockito.mock(IClient.class));
		window=new FrameFixture(frame);
		window.show();
		
	}
	//NO-INTERNET SCENARIO TESTS START
	@Test
	public void testInitialization() {
		window.requireTitle(Messages.getString("GUI.APP_TITLE"));
		window.textBox(Messages.getString("GUI.NAMES.SERVERFIELD")).requireText(Messages.getString("GUI.LOCALHOST"));
		window.textBox(Messages.getString("GUI.NAMES.PORTFIELD")).requireText(Messages.getString("GUI.DEFAULT_PORT"));
		window.textBox(Messages.getString("GUI.NAMES.PASSFIELD")).requireText(Messages.getString("GUI.DEFAULT_PASSWORD"));
		window.textBox(Messages.getString("GUI.NAMES.USERFIELD")).requireText(Messages.getString("GUI.DEFAULT_USERNAME"));
		window.textBox(Messages.getString("GUI.NAMES.APIFIELD")).requireText(Messages.getString("GUI.DEFAULT_API_PATH"));
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText("");
		window.textBox(Messages.getString("GUI.NAMES.SINKFIELD")).requireText("");
		window.textBox(Messages.getString("GUI.NAMES.SOURCEFIELD")).requireText("");
		@SuppressWarnings("unchecked")
		JComboBox<String> tgt1=window.comboBox(Messages.getString("GUI.NAMES.GRIDCOMBO")).target();
		@SuppressWarnings("unchecked")
		JComboBox<String> tgt2=window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).target();
		String item1=window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(0).selectedItem();
		String item2=window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(1).selectedItem();
		String item3=window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(2).selectedItem();
		assertEquals(Messages.getString("GUI.SHOW_GRIDS"),item1);
		assertEquals(Messages.getString("GUI.REQUEST_GRID"),item2);
		assertEquals(Messages.getString("GUI.REQUEST_PATH"),item3);
		assertEquals(0,tgt1.getItemCount());
		assertEquals(3,tgt2.getItemCount());
	}
	
	private GUIpanel getGuiPanel() {
		return (GUIpanel)(window.panel(Messages.getString("GUI.NAMES.GUIPANEL")).target());
	}
	
	//NO-INTERNET SCENARIO TESTS END
	
	//CONNECTION SCENARIO TESTS START
	@Test
	public void testConnectionOK() throws RuntimeException, IOException {
		Mockito.doNothing().when(cl).doLogin();
		window.button(Messages.getString("GUI.NAMES.BTNCONNECT")).click();
		Mockito.verify(cl,times(1)).doLogin();
		Mockito.verify(cl,times(1)).setRestServiceClient(Mockito.any(IRestServiceClient.class));
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.LOGIN_OK"));
	}
	@Test
	public void testConnectionNotOKWhenWrongUserPass() throws RuntimeException, IOException {
		
		Mockito.doThrow(new RuntimeException()).when(cl).doLogin();
		window.button(Messages.getString("GUI.NAMES.BTNCONNECT")).click();
		Mockito.verify(cl,times(1)).doLogin();
		Mockito.verify(cl,times(1)).setRestServiceClient(null);
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.LOGIN_NOK"));
	}
	@Test
	public void testConnectionNotOKWhenServerUnavailable() throws RuntimeException, IOException {
		Mockito.doThrow(new IOException()).when(cl)
		.doLogin();
		window.button(Messages.getString("GUI.NAMES.BTNCONNECT")).click();
		Mockito.verify(cl,times(1)).doLogin();
		Mockito.verify(cl,times(1)).setRestServiceClient(null);
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.SERVER_CONN_REFUSED"));
	}
	
	@Test
	public void testConnectionEmptyUsername() throws RuntimeException, IOException {
		window.textBox(Messages.getString("GUI.NAMES.USERFIELD")).setText("");
		window.button(Messages.getString("GUI.NAMES.BTNCONNECT")).click();
		verify(cl,times(0)).doLogin();
		verify(cl,times(0)).setRestServiceClient(Mockito.any());
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.INSERT_USERNAME"));
	}
	@Test
	public void testConnectionEmptyPassword() throws RuntimeException, IOException {
		window.textBox(Messages.getString("GUI.NAMES.PASSFIELD")).setText("");
		window.button(Messages.getString("GUI.NAMES.BTNCONNECT")).click();
		verify(cl,times(0)).doLogin();
		verify(cl,times(0)).setRestServiceClient(Mockito.any());
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.INSERT_PASSWORD"));
	}
	//CONNECTION SCENARIO TESTS END
	
	//RETRIEVE ALL GRIDS SCENARIO TESTS START
	@Test
	public void testRetrieveAllGridsWhenOK() throws JsonSyntaxException, IOException {
		int expected=5;
		retrieveAllGrids(expected);
		verify(cl,times(1)).getAllTables();
		@SuppressWarnings("unchecked")
		JComboBox<String> target=window.comboBox(Messages.getString("GUI.NAMES.GRIDCOMBO")).target();
		int size=target.getItemCount();
		assertEquals(5,size);
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.QUERY_OK"));
		for(int i=0; i<size;i++) {
			assertEquals(""+i,target.getItemAt(i));
		}
	}
	@Test
	public void testRetrieveAllGridsWhenUserIsNotConnected() throws JsonSyntaxException, IOException {
		when(cl.getAllTables()).thenThrow(new NullPointerException());
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(0);
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.NO_CONNECTOR"));
		
		
	}
	@Test
	public void testRetrieveAllGridsWhenServerIsUnreacheable() throws JsonSyntaxException, IOException {
		when(cl.getAllTables()).thenThrow(new IOException());
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(0);
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.SERVER_CONN_REFUSED"));
	}
	
	@Test
	public void testRetrieveAllGridsWhenServerCannotPerformOperation() throws JsonSyntaxException, IOException {
		when(cl.getAllTables()).thenThrow(new JsonSyntaxException(""));
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(0);
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.SERVER_CANNOT_PERFORM_OP"));
	}
	
	private void retrieveAllGrids(int expected) throws JsonSyntaxException, IOException {
		String[] array=new String[expected];
		for(int i=0; i<expected;i++) {
			array[i]=""+i;
		}
		when(cl.getAllTables()).thenReturn(Arrays.asList(array));
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(0);
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		
	}
	//RETRIEVE ALL GRIDS SCENARIO TESTS END
	
	//RETRIEVE ONE GRID SCENARIO TESTS START
	@Test
	public void testRequestGridWhenNotRetrievedAll() throws JsonSyntaxException, IOException {
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(1);
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.GRIDS_FIRST"));
		
	}
	@Test
	public void testRequestGridWhenServerIsUnreacheable() throws JsonSyntaxException, IOException {
		retrieveAllGrids(1);
		when(cl.retrieveGrid("0")).thenThrow(new IOException());
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(1);
		window.comboBox(Messages.getString("GUI.NAMES.GRIDCOMBO")).selectItem(0);
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.SERVER_CONN_REFUSED"));
	}
	@Test
	public void testRequestGridWhenServerCannotPerformOperation() throws JsonSyntaxException, IOException {
		retrieveAllGrids(1);
		when(cl.retrieveGrid("0")).thenThrow(new JsonSyntaxException(""));
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(1);
		window.comboBox(Messages.getString("GUI.NAMES.GRIDCOMBO")).selectItem(0);
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.SERVER_CANNOT_PERFORM_OP"));
	}
	@Test
	public void testRequestGridWhenNotConnected() throws JsonSyntaxException, IOException{
		frame.mockClient(null);
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(1);
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.NO_CONNECTOR"));
	}
	@Test
	public void testRequestGridWhenOK() throws JsonSyntaxException, IOException {
		retrieveAllGrids(1);
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(1);
		window.comboBox(Messages.getString("GUI.NAMES.GRIDCOMBO")).selectItem(0);
		when(cl.retrieveGrid("0")).thenReturn(createGridWithId(0));
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.QUERY_OK"));
		GUIpanel pan=getGuiPanel();
		assertEquals(Color.RED, pan.getColorInPoint(0, 0));
		assertEquals(Color.BLACK, pan.getColorInPoint(0, 1));
		assertEquals(Color.BLACK, pan.getColorInPoint(0, 2));
		
		assertEquals(Color.RED, pan.getColorInPoint(1, 0));
		assertEquals(Color.RED, pan.getColorInPoint(1, 1));
		assertEquals(Color.BLACK, pan.getColorInPoint(1, 2));
		
		assertEquals(Color.RED, pan.getColorInPoint(2, 0));
		assertEquals(Color.RED, pan.getColorInPoint(2, 1));
		assertEquals(Color.RED, pan.getColorInPoint(2, 2));
		for(int i=0; i<3;i++) {
			for(int j=0; j<3;j++) {
				String expected_name="";
				if(pan.getColorInPoint(i, j).equals(Color.RED)) 
					expected_name=String.format("%d_%d", i,j);
				assertEquals(expected_name,pan.getPrintedNameIn(i, j));
				
			}
		}
		window.comboBox(Messages.getString("GUI.NAMES.GRIDCOMBO")).requireDisabled();
		verify(cl,times(1)).getAllTables();
	}
	
	private GridFromServer createGridWithId(int id) {
		int[][] matrix=new int[][] {
			{1,0,0},
			{1,1,0},
			{1,1,1},
			
		};
		return new GridFromServer(matrix,id);
	}
	private void requestAGrid(int id) throws JsonSyntaxException, IOException {
		retrieveAllGrids(id+1);
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(1);
		window.comboBox(Messages.getString("GUI.NAMES.GRIDCOMBO")).selectItem(id);
		when(cl.retrieveGrid("0")).thenReturn(createGridWithId(id));
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		
	}
	//RETRIEVE ONE GRID SCENARIO TESTS END
	
	//REQUEST SHORTEST PATH SCENARIO TESTS START
	@Test
	public void testRequestShortestPathWhenNotConnected() throws JsonSyntaxException, IOException  {
		frame.mockClient(null);
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(2);
		window.textBox(Messages.getString("GUI.NAMES.SOURCEFIELD")).setText("test");
		window.textBox(Messages.getString("GUI.NAMES.SINKFIELD")).setText("test");
		when(cl.getShortestPath(Mockito.anyString(),Mockito.anyString() ,Mockito.anyString())).thenThrow(new NullPointerException());
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.NO_CONNECTOR"));
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
	}
	
	@Test
	public void testRequestShortestPathWhenServerCannotPerformOperation() throws JsonSyntaxException, IOException  {
		requestAGrid(0);
		window.textBox(Messages.getString("GUI.NAMES.SOURCEFIELD")).setText("test");
		window.textBox(Messages.getString("GUI.NAMES.SINKFIELD")).setText("test");
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(2);
		when(cl.getShortestPath(Mockito.anyString(),Mockito.anyString() ,Mockito.anyString())).thenThrow(new JsonSyntaxException(""));
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.SERVER_CANNOT_PERFORM_OP"));
		
		
	}
	
	@Test
	public void testRequestShortestPathWhenServerUnreacheable() throws JsonSyntaxException, IOException  {
		requestAGrid(0);
		window.textBox(Messages.getString("GUI.NAMES.SOURCEFIELD")).setText("test");
		window.textBox(Messages.getString("GUI.NAMES.SINKFIELD")).setText("test");
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(2);
		when(cl.getShortestPath(Mockito.anyString(),Mockito.anyString() ,Mockito.anyString())).thenThrow(new IOException());
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.SERVER_CONN_REFUSED"));
		
		
	}
	
	@Test
	public void testRequestShortestPathWhenNotRetrievedAllGrids() throws JsonSyntaxException, IOException {
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(2);
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.REQAGRID_FIRST"));
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void testRequestShortestPathWhenNotRetrievedTargetGridButOnlyAllGrids() throws JsonSyntaxException,IOException{
		retrieveAllGrids(1);
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(2);
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.REQAGRID_FIRST"));
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void testRequestShortestPathWhenNotSpecifiedSource() throws JsonSyntaxException, IOException {
		requestAGrid(0);
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(2);
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.INSERT_SOURCE"));
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	
	@Test
	public void testRequestShortestPathWhenNotSpecifiedSink() throws JsonSyntaxException, IOException {
		requestAGrid(0);
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(2);
		window.textBox(Messages.getString("GUI.NAMES.SOURCEFIELD")).setText("test");
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.INSERT_SINK"));
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void testRequestShortestPathWhenServerCannotFindSinkNode() throws JsonSyntaxException, IOException {
		requestAGrid(0);
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(2);
		window.textBox(Messages.getString("GUI.NAMES.SOURCEFIELD")).setText("0_0");
		window.textBox(Messages.getString("GUI.NAMES.SINKFIELD")).setText("test");
		when(cl.getShortestPath(Mockito.eq("test"),Mockito.eq("0_0"), Mockito.anyString())).thenReturn(new LinkedList<String>());
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.NO_PATHS_FOUND"));
		verify(cl,times(1)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void requestShortestPathWhenServerCannotFindSourceNode() throws JsonSyntaxException, IOException {
		requestAGrid(0);
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(2);
		window.textBox(Messages.getString("GUI.NAMES.SOURCEFIELD")).setText("test");
		window.textBox(Messages.getString("GUI.NAMES.SINKFIELD")).setText("0_0");
		when(cl.getShortestPath(Mockito.eq("test"),Mockito.eq("0_0") , Mockito.anyString())).thenReturn(new LinkedList<String>());
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.NO_PATHS_FOUND"));
		verify(cl,times(1)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
	}
	
	@Test
	public void requestShortestPathOK() throws JsonSyntaxException, IOException {
		shortestPathOkScenario();
		window.label(Messages.getString("GUI.NAMES.OUTPUTLBL")).requireText(Messages.getString("GUI.QUERY_OK"));
		verify(cl,times(1)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		GUIpanel pan=getGuiPanel();
		assertEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(0, 0));
		assertEquals(Color.BLACK,pan.getColorInPoint(0, 1));
		assertEquals(Color.BLACK,pan.getColorInPoint(0, 2));
		
		assertEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(1, 0));
		assertEquals(Color.RED,pan.getColorInPoint(1, 1));
		assertEquals(Color.BLACK,pan.getColorInPoint(1, 2));
		
		assertEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(2, 0));
		assertEquals(Color.RED,pan.getColorInPoint(2, 1));
		assertEquals(Color.RED,pan.getColorInPoint(2, 2));
		
		
	}
	
	@Test
	public void testReset() throws IOException {
		shortestPathOkScenario();
		GUIpanel pan=getGuiPanel();
		window.button(Messages.getString("GUI.NAMES.BTNRESET")).click();
		for(int i=0; i<3;i++) {
			for(int j=0; j<3;j++) {
				assertEquals(pan.getBackground(),pan.getColorInPoint(i, j));
			}
		}
		window.comboBox(Messages.getString("GUI.NAMES.GRIDCOMBO")).requireEnabled();
	}
	
	private void shortestPathOkScenario() throws IOException {
		requestAGrid(0);
		window.comboBox(Messages.getString("GUI.NAMES.ACTIONSCOMBO")).selectItem(2);
		window.textBox(Messages.getString("GUI.NAMES.SOURCEFIELD")).setText("0_0");
		window.textBox(Messages.getString("GUI.NAMES.SINKFIELD")).setText("2_0");
		when(cl.getShortestPath(Mockito.eq("0_0"),Mockito.eq("2_0") , Mockito.anyString())).thenReturn(Arrays.asList("0_0","1_0","2_0"));
		window.button(Messages.getString("GUI.NAMES.BTNPERFORM")).click();
		
		
	}
	
	//REQUEST SHORTEST PATH SCENARIO TESTS END
	@After
	public void tearDown() {
		window.cleanUp();
		
	}
	
	

}
