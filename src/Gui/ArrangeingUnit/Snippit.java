package Gui.ArrangeingUnit;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import datastructure.Animation;

public class Snippit implements Comparable<Snippit>
{
	static int idCtr = 0;
	int id;
	int pos; // x start value, give by  Arrager, to draw itself
	int targetPos = -1; // x pos where to go, for animating
	int startFrame;
	int frameCnt;
	ImageIcon icon;
	String name = null;
	
	// Visual Managment
	Rectangle bounds = new Rectangle(Arranger.snippitsWidth,Arranger.snippitsHeight);
	public int delta;
	
	Animation animation;
	public boolean isBeeingArranged = false;
	
	public Snippit(int frameCnt) {
		this.frameCnt = frameCnt;
		pos = 0;
		id = idCtr++;
	}
	public Snippit(int frameCnt, int pos) {
		this(frameCnt);
		this.pos = pos; 
	}
	public Snippit(Animation a ) {
		this(a.header.lastFrame - a.header.firstFrame );
		this.animation = a;
		this.pos = 0; 
	}
	
	public void move(int deltaFrames)
	{
		pos += deltaFrames;
	}
	public void moveTo(Point p)
	{
		pos = p.x;
		bounds.setLocation(p);
		updateBoundingRectangle();
	}
	public void paint(Graphics g)
	{
		updateBoundingRectangle();
		drawPicture(g);
		drawText(g);
		drawBorder(g);
	}
	private void drawText(Graphics g) {
		Color old = g.getColor();
		Font oldFont = g.getFont();
		Font idFont = new Font(g.getFont().getFamily(), Font.BOLD, g.getFont().getSize());
		g.setFont(idFont);
		String s_id = String.valueOf(id);
		g.drawString(s_id, bounds.x, bounds.y);
		g.setFont(oldFont);
		g.drawString("Frames : " + frameCnt , bounds.x  + s_id.length() * s_id.length() * idFont.getSize() , bounds.y);
		g.drawString("End: " + (startFrame + frameCnt) , bounds.x + bounds.width, 90);
		
		if (name != null)
		{
			g.setColor(Color.BLACK);
			g.drawString(name, pos + 100, 100);
		}
		g.setColor(old);
	}
	private void updateBoundingRectangle() {
		bounds.x = pos;
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
			g.drawImage(icon.getImage(), bounds.x,bounds.y,100,70, null);
		}
	}
	public int compareTo(Snippit s) {
		return s.startFrame - startFrame;
	}
	public boolean checkInside(Point p) {
		return bounds.contains(p);
	}
	public int getDelta(int x) {
		
		return  x -startFrame;
	}
	public int getId() {
		return id;
	}
}