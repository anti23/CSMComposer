package Gui.ArrangeingUnit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Snippit implements Comparable<Snippit>
{
	int startFrame;
	int endFrame;
	int frameCnt;
	ImageIcon icon;
	String name;
	
	// Visual Managment
	Rectangle bounds = new Rectangle(100,70);
	public int delta;
	
	public Snippit(int frameCnt) {
		this.frameCnt = frameCnt;
		startFrame = endFrame  = 0;
	}
	public Snippit(int frameCnt, int start) {
		this.frameCnt = frameCnt;
		startFrame = start; 
		endFrame  = start + frameCnt;
	}
	
	public void move(int deltaFrames)
	{
		startFrame += deltaFrames;
		endFrame += deltaFrames;
	}
	public void moveTo(int FramePos)
	{
		startFrame = FramePos;
		endFrame = startFrame + frameCnt;
		updateBoundingRectangle();
	}
	public void paint(Graphics g)
	{
		updateBoundingRectangle();
		drawPicture(g);
		drawBorder(g);
	}
	private void updateBoundingRectangle() {
		bounds.width = frameCnt;
		bounds.x = startFrame;
	}
	private void drawBorder(Graphics g) {
		Color old = g.getColor();
		g.setColor(Color.DARK_GRAY);
		g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		g.setColor(old);
	}
	private void drawPicture(Graphics g) {
		if (icon != null)
		{
			g.drawImage(icon.getImage(), startFrame,0,100,70, null);
		}
		if (name != null)
		{
			g.drawString(name, startFrame + 100, 60);
			System.out.println("drawing name : " + name);
		}
	}
	public int compareTo(Snippit s) {
		return s.startFrame - startFrame;
	}
	public boolean checkInside(int framex) {
		return framex >= startFrame && framex <= endFrame;
	}
	public int getDelta(int x) {
		
		return  x -startFrame;
	}
	
}