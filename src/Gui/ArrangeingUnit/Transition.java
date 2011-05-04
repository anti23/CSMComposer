package Gui.ArrangeingUnit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import CSM.CSMPoints;

public abstract class Transition implements Serializable{

	private static final long serialVersionUID = -5521107459851046770L;
	Rectangle bounds = new Rectangle(Arranger.spaceBetweenSnippits,Arranger.snippitsHeight);
	Polygon p;
	protected int framecount = 0;
	public static Map<String, Class<? extends Transition>> transitions = new HashMap<String, Class<? extends Transition>>();
	public abstract Color getBackGroundColor();
	
	public abstract String getTranstionName();
	public abstract String getTranstionShortName();
	
	public abstract CSMPoints[] getTransition(CSMPoints start, CSMPoints end);
	
	public Transition()
	{
		transitions.put(getTranstionName(), this.getClass());
	}
	
	public void paint(Graphics g)
	{
		int[] xpoints = {bounds.x					, bounds.x + bounds.width		, bounds.x};
		int[] ypoints = {bounds.y + bounds.height	, bounds.y + bounds.height/2 	, bounds.y};
		
		p = new Polygon(xpoints, ypoints, 3);
		Color old_color = g.getColor();
		g.setColor(this.getBackGroundColor());
		g.fillPolygon(p);
		g.setColor(Color.BLACK);
		g.drawPolygon(p);
		g.drawString(getTranstionShortName(), bounds.x, bounds.y + Arranger.snippitsHeight);
		g.drawString(Integer.toString(framecount), bounds.x , bounds.y);
		g.setColor(old_color);
	}
	public void setFrameCount(int frameCount )
	{
		this.framecount = frameCount;
	}
	public int getFrameCount()
	{
		return framecount;
	}
	
	public static void instanciateAllTransitionClasses()
	{
		new NoTransiton();
		new LinearTransition();
	}
	
	
}// end abstract calss Transition
