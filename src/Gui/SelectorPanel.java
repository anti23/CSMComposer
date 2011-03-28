package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.ScrollPane;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import Java3D.Java3DCSMPlayer;

public class SelectorPanel extends JPanel implements DragGestureListener{
	private static final long serialVersionUID = 1178082234788869818L;
	JTree fs_tree, project_tree ;
	ScrollPane fs_scrollpane, project_scrollpane;
	JTabbedPane tabs;
	JPanel control_bttns;
	Java3DCSMPlayer player;

	public SelectorPanel() {
		
		tabs = new JTabbedPane(JTabbedPane.BOTTOM);
		init_fs_tree();
		init_control_bttns();
		
		setLayout(new BorderLayout(3,3));
		//scrollpane.setSize(200, 500);
		add(tabs,BorderLayout.CENTER);
		add(control_bttns, BorderLayout.SOUTH);
		
	}
	
	 private void init_control_bttns() {
		 control_bttns = new JPanel(new FlowLayout(FlowLayout.LEFT));
		 control_bttns.add(new JButton("Load"));
		 control_bttns.add(new JButton("Play"));
		 control_bttns.add(new JButton("Test"));
		 control_bttns.add(new JButton("Foo"));
		 
		 for (Component bttn : control_bttns.getComponents()) {
			 if (bttn instanceof JButton)
			 {
				 ((JButton)bttn).addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String bttn_text = ((JButton)arg0.getSource()).getText();
						if(bttn_text.compareTo("Load") == 0 )
						{
							DefaultMutableTreeNode path = (DefaultMutableTreeNode)fs_tree.getLastSelectedPathComponent();
							if(path != null)
							{
								System.out.println(path.toString());
								if(path.toString().endsWith("csm"))									
									{
									player.stop();
									//Animation anim = new Animation(".\\"+path.toString());
									player.loadAnimation(".\\"+path.toString());
									}
							}
						}
							
					}
				});
			 }
			
		}
	}

	private void init_fs_tree() {
		 fs_tree =  new JTree(addNodes(null, new File(".")));
		 fs_scrollpane = new ScrollPane();
		 fs_scrollpane.setBackground(Color.red.brighter().brighter());
		 fs_scrollpane.add(fs_tree);
		 tabs.addTab("File System", fs_scrollpane);
		// TODO Auto-generated method stub
		
	}
	
	public void setPlayer(Java3DCSMPlayer s)
	{
		this.player = s;
	}

	/** Add nodes from under "dir" into curTop. Highly recursive. */
	DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
	    String curPath = dir.getPath();
	    DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curPath);
	    if (curTop != null) { // should only be null at root
	      curTop.add(curDir);
	    }
	    Vector<String > ol = new Vector<String >();
	    String[] tmp = dir.list();
	    for (int i = 0; i < tmp.length; i++)
	    {
	    //	if (tmp[i].endsWith("csm"))
	    		ol.add(tmp[i]);
	    }
	    Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
	    File f;
	    Vector<String> files = new Vector<String>() ;
	    
	    // Make two passes, one for Dirs and one for Files. This is #1.
	    for (int i = 0; i < ol.size(); i++) {
	      String thisObject = (String) ol.elementAt(i);
	      String newPath;
	      if (curPath.equals("."))
	        newPath = thisObject;
	      else
	        newPath = curPath + File.separator + thisObject;
	      if ((f = new File(newPath)).isDirectory())
	        addNodes(curDir, f);
	      else 
	        files.addElement(thisObject);
	    }
	    // Pass two: for files.
	    for (int fnum = 0; fnum < files.size(); fnum++)
	    	if (files.elementAt(fnum).endsWith("csm"))
	    		curDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));
	    return curDir;
	  }

	public void dragGestureRecognized(DragGestureEvent arg0) {
		TreePath path = fs_tree.getSelectionPath();
		  if ((path == null) || (path.getPathCount() <= 1)) {
		      // We can't move the root node or an empty selection
		      return;
		  }
		
	}
}
