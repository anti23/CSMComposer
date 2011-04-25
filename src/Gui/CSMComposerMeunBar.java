package Gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;

import datastructure.Animation;
import datastructure.Project;

import CSM.CSMHeader;
import CSM.CSMPoints;
import CSM.CSMWriter;
import Gui.ProjectPanel.AnimaitonComponent;
import Java3D.Java3DCSMPlayer;
import Java3D.SkeletMaker.Java3DSkeletMaker;
import Java3D.SkeletMaker.SkeletConnections;
import Misc.StaticTools;

public class CSMComposerMeunBar extends JMenuBar{

	private static final long serialVersionUID = -1110212839733111422L;
	
	// Controller objects
	Java3DCSMPlayer player;
	ProjectPanel projectPanel;
	Java3DSkeletMaker skeletonMaker;
	JFrame skeletonMakerFrame;
	
	//Menus
	JMenu mFile = new JMenu("File");
	JMenu mPlayer = new JMenu("Player");
	JMenu mProject = new JMenu("Project");
	JMenu mAnimation = new JMenu("Animation");
	JMenu mSkelet = new JMenu("Skelet");
	
	// File Menu
	JMenuItem miNewProject = new JMenuItem("New Project");
	JMenuItem miOpenProject = new JMenuItem("Open Project");
	JMenuItem miExportProject = new JMenuItem("Export Project");
	JMenuItem miExit = new JMenuItem("Exit");
	
	// Player Menu
	JMenuItem miRenderingOptions = new JMenuItem("Redering Options");
	JCheckBoxMenuItem  miDisplayEnviroment = new JCheckBoxMenuItem ("Display Enviroment",true);
	JCheckBoxMenuItem  miDisplayOrigin = new JCheckBoxMenuItem ("Display Origin",true);
	JCheckBoxMenuItem  miDisplayBG = new JCheckBoxMenuItem ("Display Background",true);
	JMenuItem miTakeScreenShot = new JMenuItem("Take Screenshot");
	JCheckBoxMenuItem  miToggleFullscreen = new JCheckBoxMenuItem ("Toggle Fullscreen");
	// Project Menu
	JMenuItem miAddCSMFile = new JMenuItem("Add CSM File");
	JMenuItem miSaveToCSM = new JMenuItem("Save To CSM File");
	JMenuItem miPathSettings = new JMenuItem("Path Settings");
	JMenu	 mPreviewCameraSettings = new JMenu("Preview CameraSettings");
		// Preview CameraSettings
		JMenuItem miSetFromPlayer = new JMenuItem("Set From Player");
		JMenuItem miSetDefault = new JMenuItem("Set Default");
		JMenuItem miSetPreviewCount = new JMenuItem("Set Preview Count");
		JMenuItem miRecalcPreviews = new JMenuItem("Recalculate Previews");
	// Animation
	JMenu mOperations = new JMenu("Operations");
		// Operations
		JMenuItem miStrechTrimSpeed = new JMenuItem("Stretch Or Trim Speed");
		JMenuItem miReverse = new JMenuItem("Reverse");
	JMenuItem miCut = new JMenuItem("Cut");
	JMenuItem miCopy = new JMenuItem("Copy");
	JMenuItem miPaste = new JMenuItem("Paste");
	
	//Skelet Menu
	JMenuItem miSkeleteEditor = new JMenuItem("Open Skelet Editor");
	JMenuItem miLoadSkelet = new JMenuItem("Load Skelet");
	
	
	private void initMenu()
	{
		add(mFile);
		add(mPlayer);
		add(mProject);
		add(mAnimation);
		add(mSkelet);
		
		//File Menu
		mFile.add(miNewProject);
		mFile.add(miOpenProject);
		mFile.add(miExportProject);
		mFile.add(miExit);

		//Player Menu
		mPlayer.add(miRenderingOptions);
		mPlayer.add(miDisplayEnviroment);
		mPlayer.add(miDisplayOrigin);
		mPlayer.add(miDisplayBG);
		mPlayer.add(miTakeScreenShot);
		mPlayer.add(miToggleFullscreen);
		// Project Menu
		
		mProject.add(miAddCSMFile);
		mProject.add(miSaveToCSM);
		mProject.add(miPathSettings);
		mProject.add(mPreviewCameraSettings);
			// Preview CameraSettings
			mPreviewCameraSettings.add(miSetFromPlayer);
			mPreviewCameraSettings.add(miSetPreviewCount);
			mPreviewCameraSettings.add(miRecalcPreviews);
			
		// Animation Menu
		mAnimation.add(mOperations);
		mOperations.add(miStrechTrimSpeed);
		mOperations.add(miReverse);
		mAnimation.add(miCut);
		mAnimation.add(miCopy);
		mAnimation.add(miPaste);
	

		// Skelet Menu
		
		mSkelet.add(miSkeleteEditor);
		mSkelet.add(miLoadSkelet);
	}
	
	void initActions()
	{
		miAddCSMFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File file = StaticTools.openDialog("csm", false);
				if (file != null && player != null)
				{
					//player.loadAnimation(file.getAbsolutePath());
					try {
						projectPanel.addAnimation(file.getCanonicalPath(),new Animation(file.getCanonicalPath() ) );
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		miExportProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				projectPanel.saveProject();
			}
		});
		miOpenProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				projectPanel.loadProject();
			}
		});
		
		
		miSkeleteEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					CSMHeader h = player.animation.header;
					CSMPoints frame = player.animation.getCurrentFrame();
					SkeletConnections sc = player.animation.getSkelett().connectlist;
					if (h == null || frame == null || sc == null)
					{
						System.out.println("CSMComposerMenuBar: miSkeletEditor: " + h + " " + FRAMEBITS + " " + sc);
					}
					if(skeletonMaker == null)
					{
						skeletonMaker = new Java3DSkeletMaker(h,frame,sc);
						skeletonMakerFrame = StaticTools.HidingFrame(skeletonMaker, "Skeleton Editor");
					}else 
					{
						skeletonMaker.loadSkeleton(h,frame,sc);
						skeletonMakerFrame.setVisible(true);
						
					}
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		miDisplayEnviroment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.toggleEnvirment();
			}
		});
		
		miDisplayOrigin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.toggleOrigin();
			}
		});
		miDisplayBG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.toggleBackground();
			}
		});
		
		miToggleFullscreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.toggleFullScreen();
			}
		});
		miTakeScreenShot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.captureScreen();
			}
		});
		miLoadSkelet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File f = StaticTools.openDialog("sklt", false);
				FileInputStream fi;
				try {
					fi = new FileInputStream(f);
					ObjectInputStream ois = new ObjectInputStream(fi);
					SkeletConnections sc = (SkeletConnections) ois.readObject();
					if (sc != null)
					{
						player.animation.getSkelett().setConnections(sc);
					}
					ois.close();
					fi.close();
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				} catch (ClassNotFoundException e) {
				}
				
			}
		});
		
		miSaveToCSM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int index = projectPanel.tabsPane.getSelectedIndex();
				Object selected = null;
				switch (index)
				{
				case 0 : 
					
					selected = projectPanel.projectTree.getLastSelectedPathComponent();
				break;
				case 1 : 
					selected = projectPanel.snippitsTree.getLastSelectedPathComponent();
				break;
				}
				
				if (selected != null && selected.getClass() == AnimaitonComponent.class)
				{
					Animation a = ((ProjectPanel.AnimaitonComponent)selected).animation;
					File file = StaticTools.openDialog("csm", true);
					CSMWriter csmw = new CSMWriter(a, file.getName());
					csmw.writeOutCSM();
				}else 
					System.out.println("CSMComposerMenuBar: MenueItem SaveToCSM: no AnimationComponent is selected!");
				
			}
		});
		
	} // end init actions
	
	public CSMComposerMeunBar() {
		initMenu();
		initActions();
		// Menue disapearin behind Java 3d Canvas FIX, at cost of performance
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		this.setDoubleBuffered(true);
	}


	public void setPlayer(Java3DCSMPlayer player) {
		this.player = player;
	}

	public void setProjectPanel(ProjectPanel projectPanel) {
		this.projectPanel = projectPanel;
	}


}
