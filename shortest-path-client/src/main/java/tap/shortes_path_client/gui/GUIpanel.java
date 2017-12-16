package tap.shortes_path_client.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;




public class GUIpanel extends JPanel {

	private EnablingPoint[][] GRID;


	private int GRIDSIZE;
	private int act_x;
	private int act_y;
	private LinkedList<Point> track;
	private String last_mov;
	public GUIpanel(int MAX_SIZE) {
		setBackground(Color.WHITE);
		GRIDSIZE=MAX_SIZE;
		act_y=GRIDSIZE/10;
		act_x=GRIDSIZE/10;
		initGrid(MAX_SIZE);
		this.last_mov="";
		track=new LinkedList<Point>();
	}
	private void initGrid(int MAX_SIZE) {
		int distance=(int)(10*Math.sqrt(MAX_SIZE));
		GRID=new EnablingPoint[MAX_SIZE][MAX_SIZE];
		for(int i=0; i<MAX_SIZE;i++) {
			for(int j=0; j<MAX_SIZE;j++)
			{
				GRID[j][i]=new EnablingPoint(i*distance,j*distance);
			}
		}
		
	}
	
		
	
	public void enablePoint(String toPrintInPoint) {
		
		if(GRID[act_x][act_y].isEnabled()){
			//throw new IllegalArgumentException("Point in ("+act_x+","+act_y+") is still enabled");
		}
		GRID[act_y][act_x].enable();
		GRID[act_y][act_x].setName(toPrintInPoint);
		track.add(GRID[act_y][act_x].getPoint());

		repaint();
		
	}

	private void drawGrid(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		for(int i=0; i<GRIDSIZE;i++) {
			EnablingPoint[] actLine=GRID[i];
			g.drawLine(actLine[0].getX(), actLine[0].getY(), actLine[GRIDSIZE-1].getX(), actLine[0].getY());
		}
		int start_y=GRID[0][0].getY();
		int end_y= GRID[GRIDSIZE-1][0].getY();
		for(int i=0; i<GRIDSIZE;i++) {
			int act_x=GRID[0][i].getX();
			
			g.drawLine(act_x, start_y, act_x, end_y);
		}
		
		g.setColor(Color.BLACK);
	}
	public void paintComponent(Graphics g) {
		drawGrid(g);
		for(int i=0; i<GRIDSIZE;i++)
		{
			for(int j=0; j<GRIDSIZE;j++)
			{
				if(GRID[i][j].isEnabled())
				{
					GRID[i][j].draw(g, 8, 8);
				}
			}
		}
		for(int i=0; i<track.size()-1;i++)
		{
			Point p1,p2;
			p1=track.get(i);
			p2=track.get(i+1);
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
		
	}
	public void moveLeft() {
		this.act_x--;

		this.last_mov="left";
	}
	public void moveRight() {
		this.act_x++;

		this.last_mov="right";
	}
	public void moveUp() {
		this.last_mov="up";

		this.act_y--;
	}
	public void moveDown() {
		this.last_mov="down";

		this.act_y++;
	}
	public int getActX() {
		return act_x;
	}
	public int getActY() {
		return act_y;
	}
	public void undoLast() {
		if(!last_mov.equals(""))
		{
			switch(last_mov) {
			case "left":{
				moveRight();
				break;
			}
			case "right":{
				moveLeft();
				break;
			}
			case "up":{
				moveDown();
				break;
			}
			case "down":{
				moveUp();
				break;
			}
			}
		}
		
	}
	
		
		
	

}
