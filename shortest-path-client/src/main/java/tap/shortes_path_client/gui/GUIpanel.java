package tap.shortes_path_client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;




public class GUIpanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ColoredPoint[][] GRID;
	private int GRIDSIZE;
	private int offset_x,offset_y,distance;
	private static Color DARKGREEN=Color.decode("#0e7810");
	public GUIpanel(int MAX_SIZE) {
		setPreferredSize(new Dimension(1024,600));
		setMinimumSize(new Dimension(1024,600));
		setSize(new Dimension(1024,600));
		GRIDSIZE=MAX_SIZE;
		this.distance=(int)(8*Math.sqrt(MAX_SIZE));
		setBackground(Color.WHITE);
		initGrid(MAX_SIZE);
		
		
	}
	private void initGrid(int MAX_SIZE) {
		GRID=new ColoredPoint[MAX_SIZE][MAX_SIZE];
		offset_x=getWidth()/2-(MAX_SIZE*distance/4);
		offset_y=getHeight()/2-(MAX_SIZE*distance/4);
		for(int i=0; i<MAX_SIZE;i++) {
			for(int j=0; j<MAX_SIZE;j++)
			{
				GRID[j][i]=new ColoredPoint(offset_x+i*distance,offset_y+j*distance,getBackground());
			}
		}
		
	}
	private void resizeGrid() {
		for(int i=0; i<GRIDSIZE;i++) {
			for(int j=0; j<GRIDSIZE;j++)
			{
				GRID[j][i].setX(offset_x+i*distance);
				GRID[j][i].setY(offset_y+j*distance);
				
			}
		}
		repaint();
	}
	public void enlargeCoordinatesOfPoints() {
		distance++;
		if(offset_x>0) offset_x--;
		offset_y++;
		resizeGrid();
	}
	public void reduceCoordinatesOfPoints() {
		if(distance>0) 
		{
			distance--;
			offset_x++;
			if(offset_y>0)
			offset_y--;
			resizeGrid();
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
		super.paintComponent(g);
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
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
					if(GRID[i][j].getCol().equals(DARKGREEN)) {
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
				GRID[i][j].setColor(DARKGREEN);
			}
		}
		setVisible(false);
		repaint();
		setVisible(true);
	}
	
	

	
		

	

}
