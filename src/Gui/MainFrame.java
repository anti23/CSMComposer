package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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

import javax.print.attribute.standard.Finishings;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
	Config config = new Config();

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
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		setTitle("CSM Composer Version " + config.version);
		setSize(800, 900);
		setBackground(Color.darkGray);
		addWindowListener(new WindowAdapter() {
		      public void windowClosing(WindowEvent e) {
			       try {
			    	   config.saveConfig();
			    	   System.exit(1);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			      }
		});
		setBackground(Color.green.brighter());
		
		
		initComponents();

		
		//selector.setPlayer(player);
	//	controll2.setPlayerToControll(player);
		
		//player.slider = slider;
//		addComponentsGridBagLayout();
		addComponentsMulipleSimpleLayouts();
		setVisible(true);
	}
	
	private void initComponents() {
		try {
			player = new Java3DCSMPlayer(false,false);
		} catch (IOException e) {
			System.err.println(e);
		}
		menu.setPlayer(player);
		//selector = new SelectorPanel();
		projectPanel = new ProjectPanel(project);
		menu.setProjectPanel(projectPanel);
		projectPanel.setPlayer(player);
		projectPanel.setHeaderView(headerView);
		playerControllBar = new CSMPlayerControllBar();
		playerControllBar.setPlayerToControll(player);
		playerControllBar.setArrangerPane(arrangePane);
		playerControllBar.setProjectPanel(projectPanel);
		
		

		splitPane.add(player, -1);
		splitPane.add(headerView,-1);
		splitpaneFix(splitPane);
		splitPane.setPreferredSize(new Dimension(300, 300));
		splitPane.setDividerLocation(600);
		splitPane.setOneTouchExpandable(true);
		
		arrangePane.setProjectPanel(projectPanel);
		projectPanel.arranger = arrangePane;
	//	controll2 = new SimpleControllBar();
	}

	void splitpaneFix(JSplitPane pane)
	{
		pane.getLeftComponent().setMaximumSize(new Dimension(0,0));
		pane.getLeftComponent().setMinimumSize(new Dimension(0,0));
		pane.getRightComponent().setMaximumSize(new Dimension(0,0));
		pane.getRightComponent().setMinimumSize(new Dimension(0,0));
	}
	
	private void addComponentsMulipleSimpleLayouts()
	{
		setLayout(new BorderLayout());
		add(projectPanel,BorderLayout.WEST);
//		add(player, BorderLayout.CENTER);
//		add(headerView,BorderLayout.EAST);
		splitPane = new JSplitPane(splitPane.HORIZONTAL_SPLIT,true);
		splitPane.add(player,0);
		splitPane.add(headerView,1);
		splitPane.setDividerLocation(500);
		add(splitPane,BorderLayout.CENTER);
//		JPanel cutting = new JPanel(new GridLayout(2, 1));
		JPanel cutting = new JPanel(new BorderLayout());
		cutting.add(playerControllBar,BorderLayout.NORTH);
		cutting.add(arrangePane,BorderLayout.CENTER);
		add(cutting,BorderLayout.SOUTH);
		
	}
	
	private void addComponentsGridBagLayout() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		add(projectPanel,c);
		
		
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 4;
		add(splitPane,c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 4;
		add(playerControllBar,c);

		c.gridx = 0;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 4;
		c.gridheight = 5;
		add(arrangePane,c);
		
		//pack();
	}

	// Constructor
	public MainFrame() {
		config.loadConfig();
		init();
	}
	

	
	
	public static void main(String[] args) throws Throwable {
		MainFrame mf = new MainFrame();
	}
	

}
