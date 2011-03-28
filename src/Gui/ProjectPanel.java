package Gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import Java3D.CSMPlayer.PlayerControlls;
import Misc.StaticTools;

import datastructure.Animation;
import datastructure.Project;

public class ProjectPanel extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = -3748839943336526052L;

	Project project;
	PlayerControlls player;
	CSMHeaderView headerView;
	
	JPanel buttonBar = new JPanel();
	
	//Project Tree
	DefaultMutableTreeNode root = new DefaultMutableTreeNode("Project Root");
	JTree projectTree = new JTree(root);
	JScrollPane projectTreeScrollPane = new JScrollPane(projectTree);
	
	public ProjectPanel() 
	{
		initLayout();
	}

	public ProjectPanel(Project p) 
	{
		initLayout();
		setProject(p);
	}
	
	public void setPlayer(PlayerControlls player) {
		this.player = player;
	}

	public void setProject(Project p )
	{
		project = p;
		updateProjectTree();
	}
	
	
	void updateProjectTree()
	{
		//fill in Key values of project animation list
		if (project != null)
		{
			
			root = new DefaultMutableTreeNode("Project Tree");
			Set<String> keys = project.getAnimations().keySet();
			int ctr= 0 ;
			for (String string : keys) {
				root.add(new DefaultMutableTreeNode(string));
				System.out.println(string);
			}
		}
		projectTree.setModel(new DefaultTreeModel(root));
		projectTree.expandRow(0);
	}
	
	void initLayout()
	{
		setLayout(new BorderLayout());
//		projectTree.getSelectionModel().setSelectionMode
//		(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setPreferredSize(new Dimension(300,500));
		projectTree.setPreferredSize(new Dimension(500,500));
		projectTree.addTreeSelectionListener(this);
//		projectTreeScrollPane.add(projectTree);
		buttonBar.setLayout(new FlowLayout());
//		add(projectTreeScrollPane,BorderLayout.CENTER);
		projectTreeScrollPane.setPreferredSize(new Dimension(300,500));
	//	projectTreeScrollPane.add(projectTree);
		
		add(projectTreeScrollPane);
		add(buttonBar,BorderLayout.SOUTH);
		initButtons();
	}

	private void initButtons() {
		JButton play = new JButton("Load");
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Project Panel: Playing --> " + 
						projectTree.getLastSelectedPathComponent());
				Animation a = project.getAnimation(projectTree.getLastSelectedPathComponent().toString());
				System.out.println(" " + a);
				if (a != null)
					player.loadAnimation(a);
			}
		});
		JButton delete = new JButton("Remove");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				String fileName = projectTree.getLastSelectedPathComponent().toString();  
				System.out.println("Project Panel: Deleting --> " + 
						fileName

				);
				if (fileName != null)
					project.removeAnimation(fileName);
			}
		});
		buttonBar.add(play);
		buttonBar.add(delete);
		
	}

	public void valueChanged(TreeSelectionEvent tse) {
		System.out.println("Project Panel : TreeSelection Listener: value Changed");
		System.out.println(tse);
		Object o =  projectTree.getLastSelectedPathComponent();
		String file = null;
		Animation a = null;
		if (o != null)
			file = o.toString();
		if (file != null)
			a = project.getAnimation(file);
		if (a != null)
		{
			headerView.setHeader(a.header);
			player.loadAnimation(a);
		}
	}

	public void addAnimation(String fileName, Animation animation) {
		project.addAnimation(fileName, animation);
		updateProjectTree();		
	}
	
	public void saveProject()
	{
		File f = StaticTools.openDialog("csmPrjct", true);
		try {
			FileOutputStream fo = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fo);
			oos.writeObject(project);
			
			fo.flush();
			fo.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void loadProject()
	{
		File f = StaticTools.openDialog("csmPrjct", false);
		try {
			FileInputStream fi = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fi);
			project = (Project) ois.readObject();
			updateProjectTree();
			
			fi.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void setHeaderView(CSMHeaderView headerView) {
		this.headerView = headerView;
	}

}
