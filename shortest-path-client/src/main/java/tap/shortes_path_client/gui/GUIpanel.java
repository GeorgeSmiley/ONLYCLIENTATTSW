package tap.shortes_path_client.gui;

import java.awt.Color;
import java.awt.Dimension;
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
		setPreferredSize(new Dimension(1024,600));
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
				GRID[i][j]=new EnablingPoint(i*distance,j*distance);
			}
		}
		
	}
	
		
	
	public void enablePoint(String toPrintInPoint, int i, int j, Color col) {
		
		if(GRID[act_y][act_x].isEnabled()){
			switch(last_mov) {
			case "left": moveLeft(); break;
			case "right": moveRight(); break;
			case "down": moveDown(); break;
			case "up": moveUp(); break;
			
			}
		}
		GRID[5+j][5+i].enable();
		GRID[j+5][i+5].setName(toPrintInPoint);
		GRID[j+5][i+5].setColor(col);
		track.add(GRID[act_y][act_x].getPoint());

		repaint();
		
	}

	
	public void paintComponent(Graphics g) {

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
