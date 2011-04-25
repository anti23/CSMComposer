package Gui;

import java.awt.BorderLayout;
import java.awt.Component;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import Gui.ArrangeingUnit.Snippit;
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
	DefaultMutableTreeNode projectRoot = new DefaultMutableTreeNode("Project Root");
	DefaultMutableTreeNode snippitRoot = new DefaultMutableTreeNode("Snippit Root");
	JTabbedPane tabsPane = new JTabbedPane(JTabbedPane.BOTTOM);
	JTree projectTree = new JTree(projectRoot);
	JTree snippitsTree = new JTree(snippitRoot);
	
	JScrollPane projectTreeScrollPane = new JScrollPane(projectTree);
	JScrollPane snippitsTreeScrollPane = new JScrollPane(snippitsTree);
	
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

	public Project getProject()
	{
		return project;
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
			
			projectRoot = new DefaultMutableTreeNode("Project Tree");
			Set<String> keys = project.getAnimations().keySet();
			int ctr= 0 ;
			for (String string : keys) {
				//root.add(new DefaultMutableTreeNode(string));
				projectRoot.add(new AnimaitonComponent(project.getAnimation(string), string));
				System.out.println(string);
			}
		}
		projectTree.setModel(new DefaultTreeModel(projectRoot));
		projectTree.expandRow(0);
	}
	
	void initLayout()
	{
		setLayout(new BorderLayout());
		projectTree.getSelectionModel().setSelectionMode
		(TreeSelectionModel.SINGLE_TREE_SELECTION);
//		setPreferredSize(new Dimension(300,500));
//		projectTree.setPreferredSize(new Dimension(500,500));
		projectTree.addTreeSelectionListener(this);
//		projectTreeScrollPane.add(projectTree);
		buttonBar.setLayout(new FlowLayout());
//		add(projectTreeScrollPane,BorderLayout.CENTER);
		projectTreeScrollPane.setPreferredSize(new Dimension(300,500));
		snippitsTreeScrollPane.setPreferredSize(new Dimension(300,500));
	//	projectTreeScrollPane.add(projectTree);
		tabsPane.add("Project",projectTreeScrollPane);
		tabsPane.add("Snippits",snippitsTreeScrollPane);
		add(tabsPane);
		add(buttonBar,BorderLayout.SOUTH);
		initButtons();
	}

	private void initButtons() {
		//Play
		JButton load = new JButton("Load");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Project Panel: Playing --> " + 
						projectTree.getLastSelectedPathComponent());
				Animation a = project.getAnimation(projectTree.getLastSelectedPathComponent().toString());
				System.out.println(" " + a);
				if (a != null)
					player.loadAnimation(a);
			}
		});
		// Delete
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
		buttonBar.add(load);
		buttonBar.add(delete);
		
	}

	public void valueChanged(TreeSelectionEvent tse) {
		System.out.println("Project Panel : TreeSelection Listener: value Changed");
		System.out.println(tse);
		Object o =  projectTree.getLastSelectedPathComponent();
		AnimaitonComponent ac = null;
		Animation a = null;
		if (o != null && o.getClass() == AnimaitonComponent.class)
			ac = (AnimaitonComponent) o;
		if (ac != null)
			a = ac.animation;
		if (a != null)
		{
			headerView.setHeader(a.header);
			player.loadAnimation(a);
		}
	}

	public void addAnimation(String fileName, Animation animation) {
		project.addAnimation(fileName, animation);
		updateProjectTree();		
		selectACinProjectTree( fileName);
	}

	
	void selectACinProjectTree(String fileName)
	{
		DefaultMutableTreeNode treeRoot =  (DefaultMutableTreeNode) projectTree.getModel().getRoot();
		DefaultMutableTreeNode child;
		for (int i = 0; i < treeRoot.getChildCount(); i++) {
			child = (DefaultMutableTreeNode) treeRoot.getChildAt(i);
			if(child.getClass() == AnimaitonComponent.class)
			{
				AnimaitonComponent ac = (AnimaitonComponent) child;
				if (ac.filename.compareTo(fileName)== 0)
				{
					TreePath treePath = new TreePath(child.getPath());
					 projectTree.setSelectionPath(treePath);
					 projectTree.scrollPathToVisible(treePath);
				}
			}
		}
	}
	public void addSnippit(Snippit snippit) {
		snippitRoot.add(new DefaultMutableTreeNode(snippit));
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
			setProject( (Project) ois.readObject() );
			
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

	class AnimaitonComponent extends DefaultMutableTreeNode
	{
		public ImageIcon icon;
		public String filename; // for project to find;
		public Animation animation;
		private static final long serialVersionUID = -7054399594182919315L;
		public AnimaitonComponent(Animation a, String physicalFileName) {
			this.animation = a;
			this.filename = physicalFileName;
			if (animation.previews.size() > 0)
			{
				icon = a.previews.get(a.previews.keySet().iterator());
				
			}
			DefaultMutableTreeNode child1_fullpath = new DefaultMutableTreeNode(physicalFileName);
			add(child1_fullpath);
		}
		@Override
		public boolean isLeaf()
		{
			return false;
		}
		
		@Override
		public String toString() 
		{
			return animation.header.filename;
		}
			
	}
}
