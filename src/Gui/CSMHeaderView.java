package Gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

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
		if (header != null)
		{
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
			add(table , BorderLayout.CENTER);
		}
	}
	

	public void setHeader(CSMHeader header)
	{
		this.header = header;
		initComponents();
	}
	
	
	void initComponents()
	{
		this.removeAll();
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
		initTable();
		setVisible(false);
		setVisible(true);
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
