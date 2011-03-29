package Gui.ArrangeingUnit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import datastructure.Animation;

public class ArrangerPane extends JPanel{
	private static final long serialVersionUID = 3473339410000435173L;
	Arragnge a = new Arragnge();
	JScrollPane scrollpane = new JScrollPane(a);
	int lastAddedFrameMax = 0;
	public ArrangerPane() {
		init();
	}
	
	public void add(Animation a)
	{
		Set<Integer > set = a.previews.keySet();
		Integer[] intset = new Integer[set.size()];
		set.toArray(intset);
		Arrays.sort(intset);
		ImageIcon icon = a.previews.get(intset[0]);
		Snippit s = new Snippit(a.header.lastFrame - a.header.firstFrame, lastAddedFrameMax);
		lastAddedFrameMax += s.frameCnt;
		if(icon != null)
		{
			s.icon = icon;
		}
		if(a.header != null)
		{
			s.name =a.header.filename;
			System.out.println("Snippit has got a filename");
		}
		this.a.add(s);
		scrollpane.setVisible(false);
		scrollpane.setVisible(true);
		
	}
	
	void init(){
		setLayout(new BorderLayout());
		add(scrollpane,BorderLayout.CENTER);
		scrollpane.setSize(500, 100);
		scrollpane.setPreferredSize(scrollpane.getSize());
		setSize(500, 100);
		setPreferredSize(getSize());
	}
	public static void main(String[] args) {
		JFrame f = new JFrame("Snippits Arragen Tester");
		ArrangerPane arr = new ArrangerPane();
		f.add(arr);
		f.setSize(new Dimension(300,500));
		f.pack();
		f.setVisible(true);
	}
}
