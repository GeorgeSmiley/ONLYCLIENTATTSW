package uitests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JOptionPane;

import org.assertj.swing.annotation.RunsInEDT;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
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
import tap.shortest_path_client.Client;
import tap.shortest_path_client.GridFromServer;
import tap.shortest_path_client.IClient;
import tap.shortest_path_client.IRestServiceClient;
import tap.shortest_path_client.RestServiceClient;

@RunWith(JUnit4.class)
public class GUITest  {
	private FrameFixture window;
	private IClient cl;
	private Robot r;
	private GUI frame;
	@Before
	public void setUp() throws AWTException {
		frame = GuiActionRunner.execute(()-> new GUI());
		frame.mockClient(cl=Mockito.mock(IClient.class));
		window=new FrameFixture(frame);
		window.show();
		r=new Robot();
	}
	//NO-INTERNET SCENARIO TESTS START
	@Test
	public void testInitialization() {
		window.requireTitle(Messages.getString("GUI.APP_TITLE"));
		window.textBox("serverField").requireText(Messages.getString("GUI.LOCALHOST"));
		window.textBox("portField").requireText(Messages.getString("GUI.DEFAULT_PORT"));
		window.textBox("passField").requireText(Messages.getString("GUI.1"));
		window.textBox("userField").requireText(Messages.getString("GUI.0"));
		window.textBox("urlToApiField").requireText(Messages.getString("GUI.DEFAULT_API_PATH"));
		window.label("outputLabel").requireText("");
		window.textBox("sinkField").requireText("");
		window.textBox("sourceField").requireText("");
		window.comboBox("gridComboBox").requireItemCount(0);
		window.comboBox("actionsComboBox").requireItemCount(3);
		
	}
	
	private GUIpanel getGuiPanel() {
		return (GUIpanel)(window.panel("Gui Panel").target());
	}
	private Point getGuiPanelLocation() {
		Point loc= getGuiPanel().getLocationOnScreen();
		int dx=getGuiPanel().getWidth()/2;
		int dy=getGuiPanel().getHeight()/2;
		loc.translate(dx, dy);
		return loc;
	}
	@Test
	public void testEnlargeGrid() {
		moveMouseToPanel();
		int offset_x=getGuiPanel().getOffsetX();
		int offset_y=getGuiPanel().getOffsetY();
		int distance=getGuiPanel().getDistance();
		r.mouseWheel(-10);
		
		assertTrue(offset_x>getGuiPanel().getOffsetX());
		assertTrue(offset_y<getGuiPanel().getOffsetY());
		assertTrue(distance<getGuiPanel().getDistance());
	}
	private void moveMouseToPanel() {
		Point p=getGuiPanelLocation();
		int x=p.x;
		int y=p.y;
		r.mouseMove(x, y);
	}
	@Test
	public void testReduceGrid() {
		moveMouseToPanel();
		int offset_x=getGuiPanel().getOffsetX();
		int offset_y=getGuiPanel().getOffsetY();
		int distance=getGuiPanel().getDistance();
		r.mouseWheel(+10);
		
		assertTrue(offset_x<getGuiPanel().getOffsetX());
		assertTrue(offset_y>getGuiPanel().getOffsetY());
		assertTrue(distance>getGuiPanel().getDistance());
	}
	//NO-INTERNET SCENARIO TESTS END
	
	//CONNECTION SCENARIO TESTS START
	@Test
	public void testConnectionOK() throws RuntimeException, IOException {
		Mockito.doNothing().when(cl).doLogin();
		window.button("createConnectorButton").click();
		Mockito.verify(cl,times(1)).doLogin();
		Mockito.verify(cl,times(1)).setRestServiceClient(Mockito.any(IRestServiceClient.class));
		window.label("outputLabel").requireText(Messages.getString("GUI.LOGIN_OK"));
	}
	@Test
	public void testConnectionNotOKWhenWrongUserPass() throws RuntimeException, IOException {
		
		Mockito.doThrow(new RuntimeException()).when(cl).doLogin();
		window.button("createConnectorButton").click();
		Mockito.verify(cl,times(1)).doLogin();
		Mockito.verify(cl,times(1)).setRestServiceClient(null);
		window.label("outputLabel").requireText(Messages.getString("GUI.LOGIN_NOK"));
	}
	@Test
	public void testConnectionNotOKWhenServerUnavailable() throws RuntimeException, IOException {
		Mockito.doThrow(new IOException()).when(cl)
		.doLogin();
		window.button("createConnectorButton").click();
		Mockito.verify(cl,times(1)).doLogin();
		Mockito.verify(cl,times(1)).setRestServiceClient(null);
		window.label("outputLabel").requireText(Messages.getString("GUI.SERVER_CONN_REFUSED"));
	}
	
	@Test
	public void testConnectionEmptyUsername() throws RuntimeException, IOException {
		window.textBox("userField").setText("");
		window.button("createConnectorButton").click();
		verify(cl,times(0)).doLogin();
		verify(cl,times(0)).setRestServiceClient(Mockito.any());
		window.label("outputLabel").requireText(Messages.getString("GUI.INSERT_USERNAME"));
	}
	@Test
	public void testConnectionEmptyPassword() throws RuntimeException, IOException {
		window.textBox("passField").setText("");
		window.button("createConnectorButton").click();
		verify(cl,times(0)).doLogin();
		verify(cl,times(0)).setRestServiceClient(Mockito.any());
		window.label("outputLabel").requireText(Messages.getString("GUI.INSERT_PASSWORD"));
	}
	//CONNECTION SCENARIO TESTS END
	
	//RETRIEVE ALL GRIDS SCENARIO TESTS START
	@Test
	public void testRetrieveAllGridsWhenOK() throws JsonSyntaxException, IOException {
		int expected=5;
		retrieveAllGrids(expected);
		verify(cl,times(1)).getAllTables();
		window.comboBox("gridComboBox").requireItemCount(expected);
		window.label("outputLabel").requireText(Messages.getString("GUI.QUERY_OK"));
	}
	@Test
	public void testRetrieveAllGridsWhenUserIsNotConnected() throws JsonSyntaxException, IOException {
		when(cl.getAllTables()).thenThrow(new NullPointerException());
		window.comboBox("actionsComboBox").selectItem(0);
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.NO_CONNECTOR"));
		
		
	}
	@Test
	public void testRetrieveAllGridsWhenServerIsUnreacheable() throws JsonSyntaxException, IOException {
		when(cl.getAllTables()).thenThrow(new IOException());
		window.comboBox("actionsComboBox").selectItem(0);
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.SERVER_CONN_REFUSED"));
	}
	
	@Test
	public void testRetrieveAllGridsWhenServerCannotPerformOperation() throws JsonSyntaxException, IOException {
		when(cl.getAllTables()).thenThrow(new JsonSyntaxException(""));
		window.comboBox("actionsComboBox").selectItem(0);
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.SERVER_CANNOT_PERFORM_OP"));
	}
	
	private void retrieveAllGrids(int expected) throws JsonSyntaxException, IOException {
		String[] array=new String[expected];
		for(int i=0; i<expected;i++) {
			array[i]=""+i;
		}
		when(cl.getAllTables()).thenReturn(Arrays.asList(array));
		window.comboBox("actionsComboBox").selectItem(0);
		window.button("performButton").click();
		
	}
	//RETRIEVE ALL GRIDS SCENARIO TESTS END
	
	//RETRIEVE ONE GRID SCENARIO TESTS START
	@Test
	public void testRequestGridWhenNotRetrievedAll() throws JsonSyntaxException, IOException {
		window.comboBox("actionsComboBox").selectItem(1);
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.GRIDS_FIRST"));
		
	}
	@Test
	public void testRequestGridWhenServerIsUnreacheable() throws JsonSyntaxException, IOException {
		retrieveAllGrids(1);
		when(cl.retrieveGrid("0")).thenThrow(new IOException());
		window.comboBox("actionsComboBox").selectItem(1);
		window.comboBox("gridComboBox").selectItem(0);
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.SERVER_CONN_REFUSED"));
	}
	@Test
	public void testRequestGridWhenServerCannotPerformOperation() throws JsonSyntaxException, IOException {
		retrieveAllGrids(1);
		when(cl.retrieveGrid("0")).thenThrow(new JsonSyntaxException(""));
		window.comboBox("actionsComboBox").selectItem(1);
		window.comboBox("gridComboBox").selectItem(0);
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.SERVER_CANNOT_PERFORM_OP"));
	}
	@Test
	public void testRequestGridWhenNotConnected() throws JsonSyntaxException, IOException{
		frame.mockClient(null);
		window.comboBox("actionsComboBox").selectItem(1);
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.NO_CONNECTOR"));
	}
	@Test
	public void testRequestGridWhenOK() throws JsonSyntaxException, IOException {
		retrieveAllGrids(1);
		window.comboBox("actionsComboBox").selectItem(1);
		window.comboBox("gridComboBox").selectItem(0);
		when(cl.retrieveGrid("0")).thenReturn(createGridWithId(0));
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.QUERY_OK"));
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
		window.comboBox("gridComboBox").requireDisabled();
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
		window.comboBox("actionsComboBox").selectItem(1);
		window.comboBox("gridComboBox").selectItem(id);
		when(cl.retrieveGrid("0")).thenReturn(createGridWithId(id));
		window.button("performButton").click();
		
	}
	//RETRIEVE ONE GRID SCENARIO TESTS END
	
	//REQUEST SHORTEST PATH SCENARIO TESTS START
	@Test
	public void testRequestShortestPathWhenNotConnected() throws JsonSyntaxException, IOException  {
		frame.mockClient(null);
		window.comboBox("actionsComboBox").selectItem(2);
		window.textBox("sourceField").setText("test");
		window.textBox("sinkField").setText("test");
		when(cl.getShortestPath(Mockito.anyString(),Mockito.anyString() ,Mockito.anyString())).thenThrow(new NullPointerException());
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.NO_CONNECTOR"));
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
	}
	
	@Test
	public void testRequestShortestPathWhenServerCannotPerformOperation() throws JsonSyntaxException, IOException  {
		requestAGrid(0);
		window.textBox("sourceField").setText("test");
		window.textBox("sinkField").setText("test");
		window.comboBox("actionsComboBox").selectItem(2);
		when(cl.getShortestPath(Mockito.anyString(),Mockito.anyString() ,Mockito.anyString())).thenThrow(new JsonSyntaxException(""));
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.SERVER_CANNOT_PERFORM_OP"));
		
		
	}
	
	@Test
	public void testRequestShortestPathWhenServerUnreacheable() throws JsonSyntaxException, IOException  {
		requestAGrid(0);
		window.textBox("sourceField").setText("test");
		window.textBox("sinkField").setText("test");
		window.comboBox("actionsComboBox").selectItem(2);
		when(cl.getShortestPath(Mockito.anyString(),Mockito.anyString() ,Mockito.anyString())).thenThrow(new IOException());
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.SERVER_CONN_REFUSED"));
		
		
	}
	
	@Test
	public void testRequestShortestPathWhenNotRetrievedAllGrids() throws JsonSyntaxException, IOException {
		window.comboBox("actionsComboBox").selectItem(2);
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.REQAGRID_FIRST"));
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void testRequestShortestPathWhenNotRetrievedTargetGridButOnlyAllGrids() throws JsonSyntaxException,IOException{
		retrieveAllGrids(1);
		window.comboBox("actionsComboBox").selectItem(2);
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.REQAGRID_FIRST"));
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void testRequestShortestPathWhenNotSpecifiedSource() throws JsonSyntaxException, IOException {
		requestAGrid(0);
		window.comboBox("actionsComboBox").selectItem(2);
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.INSERT_SOURCE"));
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	
	@Test
	public void testRequestShortestPathWhenNotSpecifiedSink() throws JsonSyntaxException, IOException {
		requestAGrid(0);
		window.comboBox("actionsComboBox").selectItem(2);
		window.textBox("sourceField").setText("test");
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.INSERT_SINK"));
		verify(cl,times(0)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void testRequestShortestPathWhenServerCannotFindSinkNode() throws JsonSyntaxException, IOException {
		requestAGrid(0);
		window.comboBox("actionsComboBox").selectItem(2);
		window.textBox("sourceField").setText("0_0");
		window.textBox("sinkField").setText("test");
		when(cl.getShortestPath(Mockito.eq("test"),Mockito.eq("0_0"), Mockito.anyString())).thenReturn(new LinkedList<String>());
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.NO_PATHS_FOUND"));
		verify(cl,times(1)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	@Test
	public void requestShortestPathWhenServerCannotFindSourceNode() throws JsonSyntaxException, IOException {
		requestAGrid(0);
		window.comboBox("actionsComboBox").selectItem(2);
		window.textBox("sourceField").setText("test");
		window.textBox("sinkField").setText("0_0");
		when(cl.getShortestPath(Mockito.eq("test"),Mockito.eq("0_0") , Mockito.anyString())).thenReturn(new LinkedList<String>());
		window.button("performButton").click();
		window.label("outputLabel").requireText(Messages.getString("GUI.NO_PATHS_FOUND"));
		verify(cl,times(1)).getShortestPath(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
	}
	
	@Test
	public void requestShortestPathOK() throws JsonSyntaxException, IOException {
		shortestPathOkScenario();
		window.label("outputLabel").requireText(Messages.getString("GUI.QUERY_OK"));
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
		window.button("resetButton").click();
		for(int i=0; i<3;i++) {
			for(int j=0; j<3;j++) {
				assertEquals(pan.getBackground(),pan.getColorInPoint(i, j));
			}
		}
		window.comboBox("gridComboBox").requireEnabled();
	}
	
	private void shortestPathOkScenario() throws IOException {
		requestAGrid(0);
		window.comboBox("actionsComboBox").selectItem(2);
		window.textBox("sourceField").setText("0_0");
		window.textBox("sinkField").setText("2_0");
		when(cl.getShortestPath(Mockito.eq("0_0"),Mockito.eq("2_0") , Mockito.anyString())).thenReturn(Arrays.asList("0_0","1_0","2_0"));
		window.button("performButton").click();
		
		
	}
	
	//REQUEST SHORTEST PATH SCENARIO TESTS END
	@After
	public void tearDown() {
		window.cleanUp();
		
	}
	
	

}
