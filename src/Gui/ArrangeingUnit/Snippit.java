package Gui.ArrangeingUnit;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import datastructure.Animation;

public class Snippit implements Comparable<Snippit>, Serializable
{
	private static final long serialVersionUID = 5974019425165965513L;
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
		String s_id = "ID:" + String.valueOf(id);
		g.drawString(s_id, bounds.x, bounds.y);
		g.setFont(oldFont);
		FontMetrics metrics = g.getFontMetrics(idFont);
		g.drawString("Frames : " + frameCnt , bounds.x  + metrics.stringWidth(s_id), bounds.y);
		
		String s_start = "S: " + startFrame;
		String s_end = "E: " + (startFrame + frameCnt) ;
		//draw start string
		FontMetrics fm = g.getFontMetrics();
		g.drawString(s_start, bounds.x, bounds.y + bounds.height + fm.getHeight());

		g.drawString(s_end, bounds.x + bounds.width - fm.stringWidth(s_end), bounds.y + bounds.height + fm.getHeight());
		
		if (name != null)
		{
			g.setColor(Color.BLACK);
			g.drawString(name, bounds.x, bounds.y + bounds.height + 2 * fm.getHeight());
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