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
import java.util.Arrays;
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
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import Gui.ArrangeingUnit.ArrangerPane;
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
	ArrangerPane arranger;
	
	JPanel buttonBar = new JPanel();
	
	//Project Tree
	DefaultMutableTreeNode projectRoot = new DefaultMutableTreeNode("Project Root");
	DefaultMutableTreeNode snippitRoot = new DefaultMutableTreeNode("Snippit Root");
	JTabbedPane tabsPane = new JTabbedPane(JTabbedPane.BOTTOM);
	JTree projectTree = new JTree(projectRoot);
	JTree snippitsTree = new JTree(snippitRoot);
	
	JScrollPane projectTreeScrollPane = new JScrollPane(projectTree);
	JScrollPane snippitsTreeScrollPane = new JScrollPane(snippitsTree);
	
	
	@Override
	public void repaint() {
		super.repaint();
		if(project == null)
			return;
		
		updateProjectTree();
		updateSnippitsTree();
	}
	
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
		updateSnippitsTree();
	}
	
	
	void updateProjectTree()
	{
		//fill in Key values of project animation list
		if (project != null)
		{
			
			projectRoot = new DefaultMutableTreeNode("Project Tree");
			Set<String> keys = project.getAnimations().keySet();
			String[] sortedKeys = new String[keys.size()];
			keys.toArray(sortedKeys);
			Arrays.sort(sortedKeys);
			for (String string : sortedKeys) {
				//root.add(new DefaultMutableTreeNode(string));
				projectRoot.add(new AnimationComponent(project.getAnimation(string), string));
				System.out.println(string);
			}
		}
		projectTree.setModel(new DefaultTreeModel(projectRoot));
		projectTree.expandRow(0);
	}
	
	void updateSnippitsTree()
	{
		//fill in Key values of project animation list
		if (project != null)
		{
			
			snippitRoot = new DefaultMutableTreeNode("Snippits Tree");
			Set<String> keys = project.snippits.keySet();
			String[] sortedKeys = new String[keys.size()];
			keys.toArray(sortedKeys);
			Arrays.sort(sortedKeys);
			for (String string : sortedKeys) {
				//root.add(new DefaultMutableTreeNode(string));
				snippitRoot.add(new AnimationComponent(project.snippits.get(string), string));
				System.out.println(string);
			}
		}
		snippitsTree.setModel(new DefaultTreeModel(snippitRoot));
		snippitsTree.expandRow(0);
	}
	
	void initLayout()
	{
		setLayout(new BorderLayout());
		projectTree.getSelectionModel().setSelectionMode
		(TreeSelectionModel.SINGLE_TREE_SELECTION);
//		setPreferredSize(new Dimension(300,500));
//		projectTree.setPreferredSize(new Dimension(500,500));
		projectTree.addTreeSelectionListener(this);
		snippitsTree.addTreeSelectionListener(this);
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
//		//Play
//		JButton load = new JButton("Load");
//		load.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				System.out.println("Project Panel: Playing --> " + 
//						projectTree.getLastSelectedPathComponent());
//				Animation a = project.getAnimation(projectTree.getLastSelectedPathComponent().toString());
//				System.out.println(" " + a);
//				if (a != null)
//					player.loadAnimation(a);
//			}
//		});
		// Delete
		JButton delete = new JButton("Remove");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				String fileName = getSelectedAnimation().filename;
				if (fileName != null)
					project.removeAnimation(fileName);
				updateProjectTree();
			}
		});
		
		JButton toStoryborad = new JButton("to Storyboard");
		toStoryborad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(arranger != null)
				{
					Animation a = getSelectedAnimation();
					if (a != null)
					{
						arranger.add(a);
					}
				}
			}
		});
//		buttonBar.add(load);
		buttonBar.add(delete);
		buttonBar.add(toStoryborad);
		
	}
	
	public Animation getSelectedAnimation()
	{
		int panel = tabsPane.getSelectedIndex();
		Object o = null;
		switch (panel) {
		case 0:
			o = projectTree.getLastSelectedPathComponent();
			break;
		case 1:
			o = snippitsTree.getLastSelectedPathComponent();
			break;
		}
		
		if(o != null && o.getClass() == AnimationComponent.class)
		{
			return ((AnimationComponent)o).animation;
		}
		else
		{
			System.out.println("Project Panel: No AnimationComponent Selected");
			return null;
		}
	}

	public void valueChanged(TreeSelectionEvent tse) {
		//System.out.println("Project Panel : TreeSelection Listener: value Changed");
		//System.out.println(tse);
		Animation a = getSelectedAnimation();
		if (a != null)
		{
			headerView.setHeader(a.header);
			player.loadAnimation(a);
		}
	}

	public void addAnimation(Animation animation) {
		project.addAnimation(animation.filename, animation);
		updateProjectTree();		
		selectACinProjectTree( animation.filename);
	}
//	public void addAnimation(String fileName, Animation animation) {
//		project.addAnimation(fileName, animation);
//		updateProjectTree();		
//		selectACinProjectTree( fileName);
//	}

	
	void selectACinProjectTree(String fileName)
	{
		DefaultMutableTreeNode treeRoot =  (DefaultMutableTreeNode) projectTree.getModel().getRoot();
		DefaultMutableTreeNode child;
		for (int i = 0; i < treeRoot.getChildCount(); i++) {
			child = (DefaultMutableTreeNode) treeRoot.getChildAt(i);
			if(child.getClass() == AnimationComponent.class)
			{
				AnimationComponent ac = (AnimationComponent) child;
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
			if(arranger != null)
			{
				oos.flush();
				System.out.println("PP: SAVING Arranger is there !");
				arranger.writeObject(oos);
			}
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
			arranger.readObject(ois);
		
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

	public void addSnippit(Animation a) {
		project.snippits.put(a.filename, a);
		updateSnippitsTree();
	}

	class AnimationComponent extends DefaultMutableTreeNode
	{
		public ImageIcon icon;
		public String filename; // for project to find;
		public Animation animation;
		private static final long serialVersionUID = -7054399594182919315L;
		public AnimationComponent(Animation a, String physicalFileName) {
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
