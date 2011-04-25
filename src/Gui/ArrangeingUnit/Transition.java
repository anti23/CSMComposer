package Gui.ArrangeingUnit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;

public abstract class Transition {

	Rectangle bounds = new Rectangle(Arranger.spaceBetweenSnippits,Arranger.snippitsHeight);
	Polygon p;
	public void paint(Graphics g)
	{
		int[] xpoints = {bounds.x					, bounds.x + bounds.width		,bounds.x};
		int[] ypoints = { bounds.y + bounds.height	, bounds.y + bounds.height/2 	, bounds.y};
		
		p = new Polygon(xpoints, ypoints, 3);
		Color old_color = g.getColor();
		g.setColor(Color.GREEN);
		g.fillPolygon(p);
		g.setColor(old_color);
	}
}
