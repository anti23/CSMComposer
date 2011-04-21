package Gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.plaf.SliderUI;

import CSM.CSMHeader;

public class CSMHeaderView extends JPanel {
	private static final long serialVersionUID = 760009001370573981L;
	JTable table ;
	CSMHeader header;
	
	public CSMHeaderView()
	{
		initComponents();
	}

	public CSMHeaderView(CSMHeader header)
	{
		this.header = header;
		setPreferredSize(new Dimension(100,100));
		initComponents();
	}
	
	void initTable()
	{
		if (header == null)
			return;
		String [] colNames = {"Key","Value"};
		Set<String> set =  header.getHeaderMap().keySet();
		String[][] data = new String[set.size()][2];
		int rowCtr= 0;
		for (String string : set) {
			String [] row = new String[2];
			row[0] = string;
			row[1] = header.getHeaderMap().get(string);
			data[rowCtr] = row;
			rowCtr++;
		}
		table = new JTable(data,colNames);
		table.setGridColor(getBackground());
		table.doLayout();
		JScrollPane scroller = new JScrollPane(table);
		scroller.setPreferredSize(new Dimension(200, 150));
		splitPane.add(scroller);
		//add(table);
	}
	

	public void setHeader(CSMHeader header)
	{
		this.header = header;
		initComponents();
	}
	
	JSplitPane splitPane ;
	JTextArea commentTextArea;

	void initComponents()
	{
		this.removeAll();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);
		//setLayout(new GridLayout(3, 1)); // we have a grid layout
		setLayout(new BorderLayout());
		add(splitPane,BorderLayout.CENTER);
		setBackground(Color.BLACK);
		initTable(); // first comes the table
		initComment();
		splitPane.setDividerLocation(150);
		setVisible(false);
		setVisible(true);
	}
	private void initComment() {
		if (header == null)
			return;
		JPanel pane = new JPanel(new BorderLayout());
		
		commentTextArea = new JTextArea(header.Comments, 10, 20);
		commentTextArea.setEditable(true);
		JScrollPane scroller = new JScrollPane(commentTextArea);
	//	add(scroller);
		pane.add(scroller,BorderLayout.CENTER);
		
		JButton writeCommment = new JButton("Save Comment");
		writeCommment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				header.Comments = commentTextArea.getText();
				header.getHeaderMap().put("Comments", commentTextArea.getText());
			}
		});
		pane.add(writeCommment,BorderLayout.SOUTH);
		
		JLabel heading = new JLabel("------Comments------");
		heading.setHorizontalTextPosition(JLabel.CENTER);

		heading.setAlignmentX(0.5f);
		pane.add(heading,BorderLayout.NORTH);
		splitPane.add(pane);
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("Tabel Test Frame");
		f.setLayout(new BorderLayout());
		f.setBackground(Color.blue);
		f.setSize(new Dimension(300,500));
		CSMHeaderView csmhv =new CSMHeaderView(CSMHeader.defaultHeader()); 
	//	f.add(csmhv);
		f.add(csmhv,BorderLayout.CENTER);
		f.setVisible(true);
	}
}
