package tap.shortes_path_client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;




public class GUIpanel extends JPanel {

	private ColoredPoint[][] GRID;
	private int GRIDSIZE;
	private int offset_x,offset_y;
	public GUIpanel(int MAX_SIZE, int offset_x, int offset_y) {
		setPreferredSize(new Dimension(1024,600));
		GRIDSIZE=MAX_SIZE;
		this.offset_x=offset_x;
		this.offset_y=offset_y;
		initGrid(MAX_SIZE);
		
	}
	private void initGrid(int MAX_SIZE) {
		int distance=(int)(7*Math.sqrt(MAX_SIZE));
		GRID=new ColoredPoint[MAX_SIZE][MAX_SIZE];
		for(int i=0; i<MAX_SIZE;i++) {
			for(int j=0; j<MAX_SIZE;j++)
			{
				GRID[j][i]=new ColoredPoint(offset_x+i*distance,offset_y+j*distance,getBackground());
			}
		}
		
	}
	
		
	public void reset() {
		for(int i=0; i<GRIDSIZE; i++) {
			for(int j=0; j<GRIDSIZE;j++) {
				GRID[i][j].setColor(getBackground());
			}
		}
		setVisible(false);
		repaint();
		setVisible(true);
		
	}
	public void enablePoint(String toPrintInPoint, int i, int j, Color col) {
		
		
		GRID[i][j].setName(toPrintInPoint);
		GRID[i][j].setColor(col);
		setVisible(false);
		repaint();
		setVisible(true);
		
		
	}
	
	public void paintComponent(Graphics g) {

		for(int i=0; i<GRIDSIZE;i++)
		{
			for(int j=0; j<GRIDSIZE;j++)
			{
				GRID[i][j].draw(g, 8, 8);
			}
		}
	
		
		
	}
	public void highlightPath(List<String> path) {
		
		if(path==null) {
			for(int i=0; i<GRIDSIZE;i++) {
				for(int j=0; j<GRIDSIZE;j++) {
					if(GRID[i][j].getCol().equals(Color.GREEN)) {
						GRID[i][j].setColor(Color.RED);
					}
				}
			}
		}
		else
		{
			for(String e:path) {
				int i=e.charAt(0)-48;
				int j=e.charAt(2)-48;
				GRID[i][j].setColor(Color.GREEN);
			}
		}
		setVisible(false);
		repaint();
		setVisible(true);
	}
	
	

	
		

	

}
