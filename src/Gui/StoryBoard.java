package Gui;

import java.awt.Color;
import java.awt.Scrollbar;

import javax.swing.GroupLayout;
import javax.swing.JFrame;

public class StoryBoard extends JFrame {
	private static final long serialVersionUID = -1331313431622229122L;

	public StoryBoard() {
		this.setBackground(Color.gray);
		Scrollbar bars;
		bars = new Scrollbar(Scrollbar.HORIZONTAL);
		setLayout(new GroupLayout(rootPane));
		add(bars);
	}
}
