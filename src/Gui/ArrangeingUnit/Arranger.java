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
import java.util.HashMap;
import java.util.Timer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import CSM.CSMPoints;

import datastructure.Animation;

public class Arranger extends JPanel implements MouseListener, MouseMotionListener{

	private static final long serialVersionUID = 1699180473798936300L;


	public static final int snippitsYOffset = 10;
	public static final int snippitsHeight = 70;
	public static final int  snippitsWidth = 110;
	public static final int  spaceBetweenSnippits = 30; // later used for interleaving
	Thread animateSnippits;
	// The snippits array is beeing reoreder, when users change the order
	ArrayList<Snippit> snippits = new ArrayList<Snippit>();
	// this is a secon list of the same snippits, but the order will the the creating order
	ArrayList<Snippit> snippitsForThreadedAnimaiton = new ArrayList<Snippit>();
	int accesCtr = 0; // access counter for threaded snippits 
	Snippit hit;
	
	Rectangle snippitArea = new Rectangle(0,snippitsYOffset,snippitsWidth,snippitsHeight);
	
	Rectangle size = new Rectangle(700,130);


	private boolean draggingSnippit = false;
	
	public Arranger() {
		init();
	}
	
	void init()
	{
		animateSnippits = new Thread(new SnippitAnimator()) ;
		animateSnippits.start();
		setPreferredSize(size.getSize());
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintSnippitsArea(g);
		paintSnippits(g);
		validate();
	}
	
	void updateSnippitArea()
	{
		snippitArea.setSize(snippits.size() * (2* spaceBetweenSnippits + snippitsWidth),snippitsHeight);
		size.width = snippitArea.width;
		this.setBounds(size);
	}
	
	public  Dimension getPreferredSize()
	{
		if(size == null)
			return super.getPreferredSize();
		return size.getSize();
	}
	
	void paintSnippits(Graphics g)
	{
		// Calc PanelSize
		int maxEndFrame = 0;
		Snippit sni = null;
		
		for (Snippit s : snippits) {
			s.paint(g);
		}
	}
	void paintSnippitsArea(Graphics g)
	{
		Color old = g.getColor();
		g.setColor(Color.gray.brighter());
		g.fillRect(snippitArea.x, snippitArea.y, snippitArea.width, snippitArea.height);
		
		Rectangle space = new Rectangle(snippitsWidth, snippitsHeight);
		g.setColor(Color.gray);
		
		for (int i = 0; i < snippits.size(); i++)
		{
			space.setLocation(i * (2* spaceBetweenSnippits + snippitsWidth), snippitsYOffset);
			g.fillRect(space.x, space.y, space.width, space.height);
			NoTransiton nt = new NoTransiton();
			nt.bounds.x = space.x + snippitsWidth;
			nt.bounds.y = space.y;
			nt.paint(g);
		}
		g.setColor(old);
	}
	public void mouseDragged(MouseEvent e) {
		if (hit != null && !snippitArea.contains(e.getPoint()))
		{
			hit.isBeeingArranged = false;
			hit = null;
		}
		
		if(hit != null)
		{
			hit.moveTo(e.getPoint());
			int index = translateXtoIndex(e.getX());
			snippits.remove(hit);
			snippits.add(index, hit);
		}
		updateSnippitPos();
		repaint();
	}
	
	/*
	 * Sets the targe position, so that the animation thread can rearange the Snippits
	 * AND recalculates the FrameNumberings
	 */
	private void updateSnippitPos() {
		int frameNr = 0; 
		for (Snippit s : snippits) {
			s.startFrame = frameNr;
			frameNr+= s.frameCnt;
			int pos = s.pos; 
			int shouldPos = snippits.indexOf(s) * (2*spaceBetweenSnippits + snippitsWidth);
			if (s.targetPos != shouldPos)
			{
				s.targetPos = shouldPos;
			}
			//System.out.println("Id: "+ s.id + "Pos: " +pos+ " shouldPos: " + shouldPos);
		}
	}

	int translateXtoIndex(int xPos)
	{
		return xPos / (snippitsWidth +2 * spaceBetweenSnippits);
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
		snippit.pos = snippits.size() * (snippitsWidth + 2*spaceBetweenSnippits);
		snippits.add(snippit);
		while(accesCtr != 0)
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		accesCtr ++;
		snippitsForThreadedAnimaiton.add(snippit);
		accesCtr --;
		updateSnippitArea();
		setVisible(false);
		setVisible(true);
	}



	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
		hit = null;
	}
	public void mousePressed(MouseEvent e) {
		hit = checkForSnippitHit(e);
		if(hit != null)
			hit.isBeeingArranged = true;
	}
	
	public void mouseReleased(MouseEvent e) {
		if(hit != null)
			hit.isBeeingArranged = false;
	}

	private Snippit  checkForSnippitHit(MouseEvent e)
	{
		ArrayList<Snippit> hits = new ArrayList<Snippit>();
		
		for (Snippit s : snippits) {
			if(s.checkInside(e.getPoint()))
			{
				s.delta = s.getDelta(e.getX());
				hits.add(s);
			}
		}
		if (hits.size() == 0)
			return null;
		
		Snippit  result = hits.get(0);
		return result; 
		
	}
	
 
	public Animation simpleGenerateAnimation()
	{
		Animation result = null;
		if(snippits.size() > 0 )
			result = snippits.get(0).animation;
		else
		{
			System.out.println("Arranger: simpleGenerateAnimation: nosnippits");
		}
		boolean skipfirst = true;
		for (Snippit s : snippits) {
			if(skipfirst)
			{
				skipfirst = false;
			}else 
			result.concat(s.animation);
		}
		return result;
	}
	
	public Animation generateAnimation()
	{
		int frameCount = countFrames();
		boolean headersMatch = compareHeaderOrders();
		Animation result = new Animation();
		return null;
	}

	private boolean compareHeaderOrders() {
		HashMap<String, Integer> orderCounter = new HashMap<String, Integer>(); 
		Integer val;
		for (Snippit s : snippits) {
			for (String  marker : s.animation.header.order) {
				val = orderCounter.get(marker);
				if(val == null)
					val = 0;
				orderCounter.put(marker, val+1);
			}
		}
		System.out.println("Arranger: orderMap: " + orderCounter);
	return false;
		
	}

	private int countFrames() {
		int count = 0;
		for (Snippit s : snippits) {
			count += s.frameCnt;
		}
		return count;
	}

	class SnippitAnimator implements Runnable
	{
		public void run() {
			int dir,delta ;
			int stepping = 5;
			while (true)
			{
				if (accesCtr == 0 )
				{
					accesCtr++;
					for (Snippit s : snippitsForThreadedAnimaiton) {
						if(s.isBeeingArranged)
							continue;
						delta = s.targetPos-s.pos;
						if(s.targetPos >=0)
						{
							dir= (int) Math.signum(delta);
							s.pos+= (dir * stepping);
							if (Math.abs(delta) < stepping)
								s.pos = s.targetPos;
						}
						
						delta = Arranger.snippitsYOffset - s.bounds.y;
						if(s.bounds.y != Arranger.snippitsYOffset)
						{
							dir= (int) Math.signum(delta);
							s.bounds.y += (dir * stepping);
							if (Math.abs(delta) < stepping)
								s.bounds.y = Arranger.snippitsYOffset;
						}
					}
				accesCtr --;
				try {Thread.sleep(10);} catch (InterruptedException e) {}
				repaint();
				}
			}
		}
	}
}
