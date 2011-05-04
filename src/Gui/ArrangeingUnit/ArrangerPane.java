package Gui.ArrangeingUnit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Gui.ProjectPanel;

import datastructure.Animation;

public class ArrangerPane extends JPanel implements Serializable {
	private static final long serialVersionUID = 3473339410000435173L;
	Arranger arranger = new Arranger();
	JScrollPane scrollpane = new JScrollPane(arranger);
	JPanel comandPanel = new JPanel(new GridLayout(1, 3));
	ProjectPanel projectPanel = null;
	int compositionCounter = 1;
	public ArrangerPane() {
		init();
	}
	
	public void setProjectPanel(ProjectPanel pp)
	{
		this.projectPanel = pp;
	}
	
	/**
	 * @param a - animation to add into Arranger
	 * @return id of created snippit
	 */
	public int add(Animation a)
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
			//System.out.println("Snippit has got a filename: " + a.header.filename);
		}
		this.arranger.add(s);
		scrollpane.setVisible(false);
		scrollpane.setVisible(true);
		return s.id;
		
	}
	
	private void initCommandPanel() {
		comandPanel.removeAll();
		JButton buildAnimation = new JButton("Build");
		buildAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Animation a = arranger.generateTransitonsAnimation();
				if (a != null && projectPanel != null)
				{
					String index_filename  = "Composition "+compositionCounter+" of "+ a.filename;
					String display_filename = "Composition " + compositionCounter++ + " from:" + a.header.filename;
					a.filename = index_filename;
					a.header.filename = display_filename;
					projectPanel.addAnimation(a);
				}
			}
		});
		comandPanel.add(buildAnimation);
		
		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				arranger.clear();
			}
		});
		comandPanel.add(reset);
		
	}
	void init(){
		initCommandPanel();
		setLayout(new BorderLayout());
		JLabel heading = new JLabel("Storyboard");
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
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ArrangerPane arr = new ArrangerPane();
		Arranger.debug = true;
		f.add(arr);
		f.setSize(arr.getSize());
		f.pack();
		f.setVisible(true);
	}
	
	public void writeObject(java.io.ObjectOutputStream out)
	throws IOException
	{
		arranger.writeObject(out);
	}
	
	public void readObject(java.io.ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		
		arranger.readObject(in);
		arranger.repaint();
		scrollpane = new JScrollPane(arranger);
		this.removeAll();
		init();
		validate();
		repaint();
	}
}
