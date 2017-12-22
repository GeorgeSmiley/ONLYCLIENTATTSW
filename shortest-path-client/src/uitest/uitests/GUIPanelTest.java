package uitests;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;



import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.swing.fixture.Containers.showInFrame;

import tap.shortes_path_client.gui.GUIpanel;
import tap.shortes_path_client.gui.GraphBuilder;
import tap.shortest_path_client.GridFromServer;

public class GUIPanelTest {

	private FrameFixture framecontainer;
	private GUIpanel pan;
	private int GRIDSIZE;
	@Before
	public void setup() {
		GRIDSIZE=15;
		pan=new GUIpanel(GRIDSIZE);
		framecontainer=showInFrame(pan);
		framecontainer.show();
	}
	@After
	public void teardown() {
		framecontainer.cleanUp();
	}
	@Test
	public void testAllHidden() {
		allHiddenProcedure(0,0);
	}
	private void allHiddenProcedure(int start_i, int start_j) {
		for(int i=start_i; i<GRIDSIZE;i++) {
			for(int j=start_j; j<GRIDSIZE;j++) {
				assertHidden(i,j);
			}
		}
	}
	private void assertColorInPoint(Color expected, int i, int j) {
		assertEquals(expected,pan.getColorInPoint(i, j));
	}
	@Test
	public void testAccessingColorOfExistingPoint() {
		assertNotNull(pan.getColorInPoint(0, 0));
	}
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void testAccessingColorOfUnexistingPoint() {
		pan.getColorInPoint(GRIDSIZE,0);
		
	}
	
	private void assertStringInPoint(String expected, int i, int j) {
		assertEquals(expected,pan.getPrintedNameIn(0,0));
	}
	@Test
	public void testEnableOnePoint() {
		assertEnablingOnePoint(0,0,"test",Color.RED);
	}
	private void assertEnablingOnePoint(int i, int j, String expectedname, Color expectedcol) {
		pan.enablePoint(expectedname, 0, 0);
		assertStringInPoint(expectedname,0,0);
		assertColorInPoint(expectedcol,0,0);
	}
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void assertEnableUnexistingPoint() {
		pan.enablePoint("", GRIDSIZE, GRIDSIZE);
	}
	@Test
	public void testHighlightEmptyPath() {
		pan.highlightPath(new LinkedList<String>());
		for(int i=0; i<GRIDSIZE;i++) {
			for(int j=0; j<GRIDSIZE;j++) {
				assertNotEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(i, j));
			}
		}
	}
	@Test
	public void testHighlightSinglePath() {
		pan.enablePoint("", 0, 1);
		pan.highlightPath(Arrays.asList("0_1"));
		assertColorInPoint(GUIpanel.DARKGREEN,0,1);
		for(int i=0; i<GRIDSIZE; i++) {
			for(int j=0; j<GRIDSIZE; j++) {
				if(i==0 && j==1) continue;
				assertNotEquals(GUIpanel.DARKGREEN,pan.getColorInPoint(i, j));
			}
		}
	}
	@Test
	public void testHighlightPath() {
		pan.enablePoint("", 0, 0);
		pan.enablePoint("", 0, 1);
		List<String> path=Arrays.asList("0_0","0_1");
		pan.highlightPath(path);
		for(int i=0;i<2;i++) {
			assertColorInPoint(GUIpanel.DARKGREEN,0,i);
		}
	}
	@Test(expected=IllegalArgumentException.class)
	public void testWrongPath() {
		List<String> path=Arrays.asList("10","1-1","0_1","abc");
		pan.highlightPath(path);
	}
	@Test
	public void testResetHighlight() {
		pan.enablePoint("", 0, 0);
		pan.enablePoint("", 0, 1);
		pan.enablePoint("", 0, 2);
		
		List<String> path=Arrays.asList("0_0","0_1","0_2");
		pan.highlightPath(path);
		for(int i=0; i<3;i++) {
			assertColorInPoint(GUIpanel.DARKGREEN,0,i);
		}
		pan.highlightPath(null);
		for(int i=0; i<3;i++) {
			assertColorInPoint(Color.RED,0,i);
		}
	}
	@Test
	public void testEnablePointNotHighlightable() {
		pan.enableNotHighlightablePoint("", 0, 0);
		assertColorInPoint(Color.BLACK,0,0);
		
	}
	@Test
	public void testReset() {
		pan.enablePoint("test", 0, 0);
		pan.reset();
		allHiddenProcedure(0,0);
	}
	private int[][] createMatrix(int row, int col) {
		return new int[row][col];
	}
	@Test
	public void testGraphBuilderWhenGraphIsEmpty() {
		int[][] matrix=createMatrix(0,0);
		GridFromServer fixture=new GridFromServer(matrix,0);
		GraphBuilder.makeGraph(fixture, pan);
		allHiddenProcedure(0,0);
	}
	@Test
	public void testGraphBuilderWhenGraphHasOnlyOneNodeAt1() {
		createGridOfDimensionOneAndAssertCoerentDrawing(1);
		assertColorInPoint(Color.RED,0,0);
	}
	private void createGridOfDimensionOneAndAssertCoerentDrawing(int value_in_matrix ) {
		int[][]matrix=createMatrix(1,1);
		matrix[0][0]=value_in_matrix;
		GridFromServer fixture=new GridFromServer(matrix,0);
		GraphBuilder.makeGraph(fixture, pan);
		
	}
	@Test
	public void testGraphBuilderWhenGraphHasOnlyOneNodeAt0() {
		createGridOfDimensionOneAndAssertCoerentDrawing(0);
		assertColorInPoint(Color.BLACK,0,0);
		assertOtherHiddens(0,0);
	}
	@Test
	public void testGraphBuilderWhenNotSquareMatrixAndRowsGTCol() {
		GraphBuilder.makeGraph(new GridFromServer(createMatrix(3,1),0), pan);
		for(int i=0; i<2;i++) {
			for(int j=0; j<2;j++) {
				assertColorInPoint(Color.BLACK,i,j);
			}
		}
		assertOtherHiddens(2,2);
	}
	private void assertOtherHiddens(int i, int j) {
		allHiddenProcedure(0,j+1);
		allHiddenProcedure(i+1,0);
		
	}
	private void assertHidden(int i, int j) {
		assertEquals(pan.getBackground(),pan.getColorInPoint(i,j));
	}
	@Test
	public void testGraphBuilderWhenNotSquareMatrixAndColGTRows() {
		GraphBuilder.makeGraph(new GridFromServer(createMatrix(7,10),0), pan);
		for(int i=0; i<7;i++) {
			for(int j=0; j<7;j++) {
				assertColorInPoint(Color.BLACK,i,j);
			}
		}
		assertOtherHiddens(7,7);
	}
	@Test
	public void testGraphBuilderInACommonScenario() {
		
		int[][] matrix= new int[][] {
			{1,0,1,1},
			{1,1,0,0},
			{1,1,1,1},
			{1,1,0,1}
		};
		GridFromServer fixture=new GridFromServer(matrix,0);
		GraphBuilder.makeGraph(fixture, pan);
		for(int i=0; i<4;i++) {
			for(int j=0; j<4;j++) {
				if(matrix[i][j]==1) {
					assertColorInPoint(Color.RED,i,j);
				}
				else
				{
					assertColorInPoint(Color.BLACK,i,j);
				}
			}
		}
		assertOtherHiddens(4,4);
	}
	@Test
	public void testEnlargeCoordinates() {
		int dist=pan.getDistance();
		int offset_x=pan.getOffsetX();
		int offset_y=pan.getOffsetY();
		pan.enlargeCoordinatesOfPoints();
		
		assertEquals(dist+1,pan.getDistance());
		assertEquals(offset_x-1,pan.getOffsetX());
		assertEquals(offset_y+1,pan.getOffsetY());
		
	}
	@Test
	public void testReduceCoordinates() {
		int dist=pan.getDistance();
		int offset_x=pan.getOffsetX();
		int offset_y=pan.getOffsetY();
		pan.reduceCoordinatesOfPoints();
		
		assertEquals(dist-1,pan.getDistance());
		assertEquals(offset_x+1,pan.getOffsetX());
		assertEquals(offset_y-1,pan.getOffsetY());
		
	}
	@Test
	public void testDistanceNeverGoBeyondZero() {
		for(int i=0; i<1000;i++) {
			pan.reduceCoordinatesOfPoints();
		}
		assertEquals(0,pan.getDistance());
		
	}
	public void testAbnormalEnlargeCoordinatesXthatMustBeZero() {
		for(int i=0; i<1000;i++) {
			pan.enlargeCoordinatesOfPoints();
		}
		assertEquals(0,pan.getOffsetX());
	}
	

}
