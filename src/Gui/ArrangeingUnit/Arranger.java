package Gui.ArrangeingUnit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JPanel;
import CSM.CSMHeader;
import datastructure.Animation;

public class Arranger extends JPanel implements MouseListener, MouseMotionListener,Serializable{

	private static final long serialVersionUID = 1699180473798936300L;
	public static final int snippitsYOffset = 10;
	public static final int snippitsHeight = 70;
	public static final int  snippitsWidth = 100;
	public static final int  spaceBetweenSnippits = 45; // later used for interleaving
	static boolean debug =false;
	
	Thread animateSnippits;
	// The snippits array is beeing reoreder, when users change the order
	ArrayList<Snippit> snippits = new ArrayList<Snippit>();
	// this is a secon list of the same snippits, but the order will the the creating order
	ArrayList<Snippit> snippitsForThreadedAnimaiton = new ArrayList<Snippit>();
	int accesCtr = 0; // access counter for threaded snippits 
	Snippit hit;
	
	ArrayList<Transition> transitions = new ArrayList<Transition>();
	Rectangle snippitArea = new Rectangle(0,snippitsYOffset,snippitsWidth,snippitsHeight);
	Rectangle size = new Rectangle(700,130);


	//private boolean draggingSnippit = false;
	
	public Arranger() {
		init();
	}
	
	
	void init()
	{
		Transition.instanciateAllTransitionClasses();
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
		paintSnippitsAndTransitions(g);
		validate();
	}
	
	void updateSnippitArea()
	{
		snippitArea.setSize(snippits.size() * (spaceBetweenSnippits + snippitsWidth),snippitsHeight);
		size.width = snippitArea.width;
		this.setBounds(size);
	}
	
	public  Dimension getPreferredSize()
	{
		if(size == null)
			return super.getPreferredSize();
		return size.getSize();
	}
	
	void paintSnippitsAndTransitions(Graphics g)
	{
		for (Snippit s : snippits) {
			s.paint(g);
		}
		for (Transition t : transitions) {
			t.paint(g);
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
			space.setLocation(i * (spaceBetweenSnippits + snippitsWidth), snippitsYOffset);
			g.fillRect(space.x, space.y, space.width, space.height);
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
		for (int i = 0; i < snippits.size(); i++) 
		{
			Snippit s = snippits.get(i);
			Transition t = transitions.get(i);
			s.startFrame = frameNr;
			frameNr+= s.frameCnt + t.getFrameCount();
			int shouldPos = snippits.indexOf(s) * (spaceBetweenSnippits + snippitsWidth);
			if (s.targetPos != shouldPos)
			{
				s.targetPos = shouldPos;
			}
			//System.out.println("Id: "+ s.id + "Pos: " +pos+ " shouldPos: " + shouldPos);
		}
	}

	int translateXtoIndex(int xPos)
	{
		return xPos / (snippitsWidth + spaceBetweenSnippits);
	}
	
	public void mouseMoved(MouseEvent e) {
	//	System.out.println("Mouse mmoved");
	}
	public void mouseClicked(MouseEvent e) {
		int mouseButton =e.getButton(); 
		if( mouseButton== MouseEvent.BUTTON3)
		{
			if (Arranger.debug ) add(new Snippit( (int) (Math.random()*200),e.getX())	);
			System.out.println("Adding Snippit" );
			
		}else if (mouseButton == MouseEvent.BUTTON1)
		{
			int transition_index = checkForTranitionHit(e);
			if (transition_index >= 0)
			{
				
				Point p  = new Point(snippits.get(transition_index).pos + snippitsWidth,
						snippitsYOffset);
				TransitionChooseDialog diag = new TransitionChooseDialog(e.getLocationOnScreen(),transitions,transition_index,p);
				diag.setVisible(true);
			}
		}
		updateSnippitPos();
		repaint();
	}
	

	private int checkForTranitionHit(MouseEvent e) {
		Point p = e.getPoint();
		for (Transition t : transitions) {
			if (t.bounds.contains(p))
				return transitions.indexOf(t);
		}
		
		return -1;
	}

	public void add(Snippit snippit) {
		int Xpos = snippits.size() * (snippitsWidth + spaceBetweenSnippits);
		snippit.pos = Xpos;
		snippits.add(snippit);
			Transition trans = new LinearTransition();
			//Transition trans = new NoTransiton();
			trans.bounds.x = Xpos + snippitsWidth;
			trans.bounds.y = snippitsYOffset;
			trans.setFrameCount(120);
			transitions.add(trans);
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
		updateSnippitPos();
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
	
 
	public Animation generateTransitonsAnimation()
	{
		
		if (snippits.size() == 0)
		{
			System.out.println("Arranger: generateTransitonsAnimation: no Snippits here to combine");
			return null;
		}
		CSMHeader head = snippits.get(0).animation.header.clone();
		head.firstFrame = 0;
		head.lastFrame = 0;
		
		Animation result = new Animation(head);
		//add first snippits animation to the result 
		result.concat(snippits.get(0).animation);
		
		for (int i = 1; i < snippits.size(); i++) {
			Snippit s = snippits.get(i);
			Transition t = transitions.get(i-1);
			Animation transition = new Animation(result.header.clone(),
												t.getTransition(result.getLastFrame(),
														s.animation.getFirstFrame()));
			//add following Transition, and animation to the result
			result.concat(transition);
			result.concat(s.animation);
		}
		
		StringBuffer sb =  new StringBuffer(" ");
		for (Snippit s : snippits) {
			sb.append(Integer.toString(s.id) + "->");
		}
		result.filename += sb.toString();
		
		return result;
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

	public void clear() {
		snippits.clear();
		snippitsForThreadedAnimaiton.clear();
		transitions.clear();
	}
	
	public void writeObject(java.io.ObjectOutputStream out)
	throws IOException
	{
		System.out.println("Arranger Writing Objekt");
		out.writeObject(snippits);
		out.writeObject(transitions);
		out.writeObject(new Integer(Snippit.idCtr));
	}
	
	public void readObject(java.io.ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		snippits.clear();
		snippits = (ArrayList<Snippit>) in.readObject();
		snippitsForThreadedAnimaiton = new ArrayList<Snippit>();
		for (Snippit snippit : snippits) {
			snippitsForThreadedAnimaiton.add(snippit);		
		}
		transitions.clear();
		transitions = (ArrayList<Transition>) in.readObject();
		Integer i = (Integer) in.readObject();
		Snippit.idCtr = i;
		this.removeAll();
		this.removeMouseListener(this);
		this.removeMouseMotionListener(this);
		init();
	}
	
	
}
