package tap.shortes_path_client.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;


public class EnablingPoint {
	private int x,y;
	private String name;
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	private boolean isEnabled;
	private Color col;
	public EnablingPoint(int x, int y) {
		this.x=x;
		this.y=y;
		this.isEnabled=false;
	}
	public void enable() {
		isEnabled=true;
	}
	public void disable() {
		isEnabled=false;
	}
	public boolean isEnabled() {
		return isEnabled;
	}
	public void draw(Graphics g,int width,int height) {
		g.setColor(col);
		g.fillOval(this.x-width/2, this.y-height/2, width, height);
		g.drawString(name, x, y);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Point getPoint() {
		return new Point(x,y);
	}
	public void setColor(Color col) {
		this.col=col;
		
	}

}
