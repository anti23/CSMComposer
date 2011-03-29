package Gui.ArrangeingUnit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Arragnge extends JPanel implements MouseListener, MouseMotionListener{

	private static final long serialVersionUID = 1699180473798936300L;


	ArrayList<Snippit> snippits = new ArrayList<Snippit>();
	
	Snippit[] hits;
	
	Dimension size = new Dimension(700,150);
	
	public Arragnge() {
		init();
	}
	
	void init()
	{
		setPreferredSize(size);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		paintSnippits(g);
		validate();
	}
	
	public  Dimension getPreferredSize()
	{
		if(size == null)
			return super.getPreferredSize();
		return size;
	}
	
	void paintSnippits(Graphics g)
	{
		// Calc PanelSize
		int maxEndFrame = 0;
		Snippit sni = null;
		if (snippits.size() > 0 )
			sni= snippits.get(0);
		if (sni != null)
			maxEndFrame= sni.endFrame;
		
		for (Snippit s : snippits) {
		if (s.endFrame> maxEndFrame)
			maxEndFrame = s.endFrame;
			s.paint(g);
		}
		if (sni != null)
			size.width = maxEndFrame;
	}
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		if(hits != null )
		{
			for (Snippit s : hits)
			{
				s.moveTo(x-s.delta);
			}
		}
		repaint();
	}
	public void mouseMoved(MouseEvent e) {
	//	System.out.println("Mouse mmoved");
	}
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			add(new Snippit( (int) (Math.random()*200),e.getX())	);
			System.out.println("Adding Snippit" );
		}
		repaint();
	}
	public void add(Snippit snippit) {
		snippits.add(snippit);
		setVisible(false);
		setVisible(true);
	}



	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
		hits = null;
	}
	public void mousePressed(MouseEvent e) {
		hits = checkForSnippitHit(e);
		System.out.println("Got " + hits.length + " Hits");
	}
	
	private Snippit[] checkForSnippitHit(MouseEvent e) {
		ArrayList<Snippit> hits = new ArrayList<Snippit>();
		
		int framex = e.getX();
		for (Snippit s : snippits) {
			if(s.checkInside(framex))
			{
				s.delta = s.getDelta(e.getX());
				hits.add(s);
				
			}
		}
		if (hits.size() == 0)
			return new Snippit[0];
		
		Snippit[] result = new Snippit[hits.size()];
		hits.toArray(result);
		hits.clear();
		return result; 
		
	}

	public void mouseReleased(MouseEvent e) {
	}
}
