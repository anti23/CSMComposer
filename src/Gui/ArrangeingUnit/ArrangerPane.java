package Gui.ArrangeingUnit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Gui.ProjectPanel;

import datastructure.Animation;

public class ArrangerPane extends JPanel implements MouseMotionListener{
	private static final long serialVersionUID = 3473339410000435173L;
	Arranger arranger = new Arranger();
	JScrollPane scrollpane = new JScrollPane(arranger);
	JPanel comandPanel = new JPanel(new GridLayout(1, 3));
	ProjectPanel projectPanel = null;
	
	public ArrangerPane() {
		init();
	}
	
	public void setProjectPanel(ProjectPanel pp)
	{
		this.projectPanel = pp;
	}
	
	public void add(Animation a)
	{
		Set<Integer > set = a.previews.keySet();
		Integer[] intset = new Integer[set.size()];
		set.toArray(intset);
		Arrays.sort(intset);
		Snippit s = new Snippit(a);
		if(intset.length > 0)
			s.icon = a.previews.get(intset[0]);
		if(a.header != null)
		{
			s.name =a.header.filename;
			System.out.println("Snippit has got a filename: " + a.header.filename);
		}
		this.arranger.add(s);
		scrollpane.setVisible(false);
		scrollpane.setVisible(true);
		
	}
	
	private void initCommandPanel() {
		JButton buildAnimation = new JButton("Build");
		buildAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Animation a = arranger.simpleGenerateAnimation();
				if (projectPanel != null)
				{
					projectPanel.addAnimation("Compsition" , a);
				}
			}
		});
		comandPanel.add(buildAnimation);
	}
	void init(){
		initCommandPanel();
		setLayout(new BorderLayout());
		JLabel heading = new JLabel("Arranging Unit");
		heading.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		add(heading,BorderLayout.NORTH);
		add(scrollpane,BorderLayout.CENTER);
		add(comandPanel,BorderLayout.WEST);
		scrollpane.setSize(500, 160);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollpane.setPreferredSize(scrollpane.getSize());
		setSize(500, 160);
		setPreferredSize(getSize());
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("Snippits Arranger Tester");
		ArrangerPane arr = new ArrangerPane();
		
		f.add(arr);
		f.setSize(arr.getSize());
		f.pack();
		f.setVisible(true);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (r != null)
			g.drawRect(r.x, r.y, r.width, r.height);
	}

	Rectangle r;
	public void mouseDragged(MouseEvent e) {
		r = (Rectangle) scrollpane.getBounds();
		int autoscrollBorderSpace = 10;
		r.x += autoscrollBorderSpace;
		r.width -= 2* autoscrollBorderSpace;	
		System.out.println("Arranger Pane : Dragging");
	}

	public void mouseMoved(MouseEvent e) {

		
	}

 
}
