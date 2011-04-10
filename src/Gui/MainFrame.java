package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import datastructure.Config;
import datastructure.Project;
import CSM.CSMHeader;
import CustomSwingComponent.JFilmStripSlider;
import Gui.ArrangeingUnit.Arranger;
import Gui.ArrangeingUnit.ArrangerPane;
import Java3D.Java3DCSMPlayer;
import Java3D.CSMPlayer.SimpleControllBar;

public class MainFrame extends JFrame{

	private static final long serialVersionUID = 4185989615854832714L;

	CSMComposerMeunBar menu = new CSMComposerMeunBar();
	Config config;
	File configFile;
	// Data Components
	Project project = new Project();
	
	// Gui Components
	Java3DCSMPlayer player;
	// SelectorPanel selector;
	ProjectPanel projectPanel;
	CSMPlayerControllBar playerControllBar;
	SimpleControllBar controll2;
	CSMHeaderView headerView = new CSMHeaderView();
	ArrangerPane arrangePane = new ArrangerPane();
	
	JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
	
	// Init
	private void init() {
		
		setJMenuBar(menu);
		setTitle("CSM Editor");
		setSize(600, 800);
		setBackground(Color.darkGray);
		addWindowListener(new WindowAdapter() {
		      public void windowClosing(WindowEvent e) {
			        System.exit(0);
			      }
		});
		setBackground(Color.green.brighter());
		setLayout(new GridBagLayout());
		
		initComponents();
		menu.setPlayer(player);
		projectPanel.setPlayer(player);
		projectPanel.setHeaderView(headerView);
		menu.setProjectPanel(projectPanel);
		
		//selector.setPlayer(player);
	//	controll2.setPlayerToControll(player);
		
		//player.slider = slider;
		addComponents();
		setVisible(true);
	}
	
	private void initComponents() {
		try {
			player = new Java3DCSMPlayer(false,false);
		} catch (IOException e) {
			System.err.println(e);
		}
		//selector = new SelectorPanel();
		projectPanel = new ProjectPanel(project);
		playerControllBar = new CSMPlayerControllBar();
		playerControllBar.setPlayerToControll(player);
		playerControllBar.setArrangerPane(arrangePane);
		
	//	controll2 = new SimpleControllBar();
	}

	void splitpaneFix(JSplitPane pane)
	{
		pane.getLeftComponent().setMaximumSize(new Dimension(0,0));
		pane.getLeftComponent().setMinimumSize(new Dimension(0,0));
		pane.getRightComponent().setMaximumSize(new Dimension(0,0));
		pane.getRightComponent().setMinimumSize(new Dimension(0,0));
	}
	private void addComponents() {
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		//add(selector,c);
		add(projectPanel,c);
		
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		splitPane.add(player, -1);
		splitPane.add(headerView,-1);
		splitpaneFix(splitPane);
		splitPane.setPreferredSize(new Dimension(300, 300));
		splitPane.setDividerLocation(500);
		splitPane.setOneTouchExpandable(true);
		add(splitPane,c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.gridx = 0;
		c.gridwidth = 3;
		c.gridy = 1;
		add(playerControllBar,c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.gridx = 0;
		c.gridwidth = 3;
		c.gridy = 2;
		//add(controll2,c);
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 3;
		c.gridheight = 5;
		add(arrangePane,c);
		
	//	pack();
	}

	// Constructor
	public MainFrame() {
		loadConfig();
		init();
	}
	


	private void loadConfig() {
		File programDir = new File(".");
		
		for (File  s : programDir.listFiles(new FileFilter() {
			public boolean accept(File arg0) {
				return arg0.toString().endsWith("CSMCConf.xml");
			}
		})) {
			System.out.println(s);
			
			if(s.isFile())
				configFile = s;
		}
		if(configFile != null) // we have a configfile in root dir
		{
			// we parse it
			try {
				FileInputStream in = new FileInputStream(configFile);
				XMLDecoder decoder = new XMLDecoder(in);
				config = (Config) decoder.readObject();
			} catch (FileNotFoundException e) {
			}
			
		}else
		{
			//we create one
			configFile = new File("./CSMConf.xml");
			
		}
	}
	
	private void saveConfig()
	{
		if(config != null && configFile != null)
		{
			try {
				FileOutputStream fo = new FileOutputStream(configFile);
				XMLEncoder encoder = new XMLEncoder(fo);
				encoder.writeObject(config);
			} catch (FileNotFoundException e) {
			}
			
		}else 
		{
			System.out.println("Error Saving Config File");
		}
	}

	
	public static void main(String[] args) throws Throwable {
		MainFrame mf = new MainFrame();
	}
	

}
