package Java3D.SkeletMaker;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.media.j3d.Appearance;
import javax.media.j3d.BadTransformException;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Group;
import javax.media.j3d.PickInfo;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.View;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import CSM.CSMHeader;
import CSM.CSMParser;
import CSM.CSMPoints;
import Misc.StaticTools;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.pickfast.PickCanvas;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import datastructure.*;

public class Java3DSkeletMaker extends JPanel implements KeyListener,MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	// FullScreen
	JFrame fullscreen;
	boolean isFullScreen = false;
    GraphicsDevice device;
    JPanel content = new JPanel();
	
    //Java 3d
    Canvas3D c3d;
	PickCanvas pickCanvasPoints;
	SimpleUniverse u;
	BranchGroup objectBranch = new BranchGroup();
	BranchGroup root = new BranchGroup();
	Transform3D rootTransform = new Transform3D();
	TransformGroup rootTransformtGroup = new TransformGroup();
	BranchGroup pickGroup =new BranchGroup();
	BranchGroup staticGroup =new BranchGroup();
	OrbitBehavior orbit;
	
	// Selection and Marker Data
	int SelectedItem = -1;
	Sphere markerSphere ;
	TransformGroup markerTransformGroup = new TransformGroup();
	//Cylinder 
	Cylinder markerBoneCylinder ;
	TransformGroup markerBoneAngle = new TransformGroup();
	TransformGroup markerBoneStretchZ = new TransformGroup();
	// Selection Text
	String markerText = "Drag and drop with middle Mousebutton";
	private String markerStart;
	private String markerEnd;
	BranchGroup markerTextGroup = new BranchGroup();
	
	// Frame 
	private boolean isStandAlone =false;
	
	//Options Panel
	JPanel commandPannel = new JPanel(new GridLayout(5, 1));
	JButton setConnection = new JButton("Set Connection");
	JButton removeConnection = new JButton("Remove Connection");
	JButton removeAllConnections = new JButton("Remove All Connections");
	JButton save = new JButton("Save");
	JButton load = new JButton("Load");
	
	// Skelet Data
	CSMHeader header ;
	CSMPoints points ;
	CSMParser parser ;
	Java3DSkelet skelet;

	private boolean curserMode = false;

	
	public Java3DSkeletMaker(CSMHeader header, CSMPoints frame, SkeletConnections connections) throws IOException
	{
		this(false,false);
		loadSkeleton(header, frame, connections);

	}
	
	public Java3DSkeletMaker(boolean isStandAlone,boolean withFileChooser) throws IOException {
		File file = null;
		this.isStandAlone = isStandAlone;
		if(withFileChooser)
		{
			file = StaticTools.openDialog("csm", false);
			System.out.println(file.getPath());
			parser = new CSMParser();
			parser.scanFile(file.getCanonicalPath());
			
			header = parser.getHeader();
			points = parser.parseFrame();
			
			skelet = new Java3DSkelet(header);
			skelet.loadFrame(points.points);
		}
		
		initFrame();
		init3D();
	}
	
	
 
	
	void initStaticGraph()
	{
		// 3D Measure Coordinates and Origin
		staticGroup.addChild(StaticTools.createSphereWithText(new Point3f(0,0,0), 0.5f, "Origin"));
		staticGroup.addChild(StaticTools.createSphereWithText(new Point3f(5,0,0), 0.4f, "X=5"));
		staticGroup.addChild(StaticTools.createSphereWithText(new Point3f(0,5,0), 0.4f, "Y=5"));
		staticGroup.addChild(StaticTools.createSphereWithText(new Point3f(0,0,5), 0.4f, "Z=5"));
		
		//Cylinders
		staticGroup.addChild(StaticTools.cylinder(new Point3f(0,0,0),
				new Point3f(5,0,0),
				0.1f,StaticTools.yellow()));
		
		staticGroup.addChild(StaticTools.cylinder(new Point3f(0,0,0),
				new Point3f(0,5,0),
				0.1f,StaticTools.yellow()));
		
		staticGroup.addChild(StaticTools.cylinder(new Point3f(0,0,0),
				new Point3f(0,0,5),
				0.1f,StaticTools.yellow()));
	
		
		// Boden
		float a = 50.0f;
		LitQuad lq = new LitQuad(
				new Point3f(-a,0, a),
				new Point3f(a ,0, a),
				new Point3f( a,0,-a),
				new Point3f(-a,0,-a)
		);
	
		try {
			
			Texture texture =
				new TextureLoader("470.jpg","LUMINANCE", new Container()).getTexture();
			lq.getAppearance().setTexture(texture);
			
		} catch (Exception e) {
			System.out.println("Java3dCSMPlayer: initStatic Graph: Texture Poading Problem!");
		} finally
		{
			staticGroup.addChild(lq);
		}
	      
	      
	      // Cameras
	      
	      float r = 30;
	      float h = 22;

	      for (float i =  (float) -Math.PI; i <= Math.PI ; i+= Math.PI/3)
	      {
	    	  
	    	  float x = (float) (Math.sin(i) * r);
	    	  float z = (float) (Math.cos(i) * r);
	    	  
	    	  staticGroup.addChild(StaticTools.cylinder(new Point3f(x,h/4,z), 
					new Point3f(x,h,z),
					0.5f));
			
	    	  staticGroup.addChild(StaticTools.cylinder(new Point3f(x,h,z), 
					new Point3f(x- x/10,h-1 ,z- z/10),
					0.7f));
			for (float j = 0; j <= Math.PI * 2 ; j+= Math.PI/1.5f) 
			{
				float r2  = 3.3f;
				float x2 = (float) (Math.sin(j) * r2);
				float z2 = (float) (Math.cos(j) * r2);
				
				staticGroup.addChild(StaticTools.cylinder(
						new Point3f(x  + x2,0,z + z2),
						new Point3f(x,h/4,z), 
						0.5f));
			}
		}  
	}

	private void initOrbitBehavoir()
	{
		orbit = 
			new OrbitBehavior(c3d, OrbitBehavior.REVERSE_ALL);
//		new OrbitBehavior(c3d, OrbitBehavior.REVERSE_ALL|OrbitBehavior.DISABLE_ZOOM);
			orbit.setSchedulingBounds(StaticTools.defaultBounds);
			ViewingPlatform vp = u.getViewingPlatform();
			vp.setViewPlatformBehavior(orbit);
			
	}
	
	private void initFrame()
	{
		this.setLayout(new BorderLayout());
		content.setLayout(new BorderLayout());
		//content.setPreferredSize(new Dimension(500,500));
		setSize(500,500);
		if (isStandAlone)
		{
			final Java3DSkeletMaker simple = this;
		}
		this.add(content,BorderLayout.CENTER);
		initCommandPanel();
		setVisible(true);
	}
	
	private void initCommandPanel()
	{
		commandPannel.add(setConnection);
		commandPannel.add(removeConnection);
		commandPannel.add(removeAllConnections);
		commandPannel.add(save);
		commandPannel.add(load);
		content.add(commandPannel, BorderLayout.WEST);
		
		setCommadActions();
	}
	
	void setCommadActions()
	{
		setConnection.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				skelet.connect(markerStart, markerEnd,true);
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File file = StaticTools.openDialog("sklt",true);
				
				try {
					skelet.save(file.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File file = StaticTools.openDialog("sklt",false);
				skelet.loadFrame(file);
				
			}
		});
		removeConnection.addActionListener(new  ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				skelet.disconnect(markerStart, markerEnd);
			}
		});
		
		removeAllConnections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				skelet.cleanUp();
			}
		});
	}
	
	private void setSkelet(Java3DSkelet s)
	{
		if(s != null)
		{
			pickGroup.removeChild(skelet.getBG());
			skelet = s;
			pickGroup.addChild(skelet.getBG());
		}
	}
	
	private void init3D() {
		root = new BranchGroup();
		root.setCapability(BranchGroup.ALLOW_DETACH);
		
		objectBranch.setCapability(BranchGroup.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
		objectBranch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		objectBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		objectBranch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		
		pickGroup.setCapability(BranchGroup.ALLOW_DETACH);
		pickGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		pickGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		pickGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		objectBranch.addChild(pickGroup);
		setSkelet(skelet);
		
		c3d = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		c3d.addMouseListener(this);
		c3d.addMouseMotionListener(this);
		u  = new SimpleUniverse(c3d);
		content.add(BorderLayout.CENTER,c3d);
		
		//Setting the Camera and View Properties
		View view = u.getViewingPlatform().getViewers()[0].getView();
		view.setBackClipDistance(1000);
		view.setFrontClipDistance(0.1);
		c3d.setDoubleBufferEnable(true);
		u.getViewingPlatform().getViewPlatformTransform().setTransform(
				StaticTools.getDefaultCameraPos());
		StaticTools.lights(root);

		//view.setMinimumFrameCycleTime(100);
	
		root.addChild(rootTransformtGroup);
		rootTransformtGroup.addChild(objectBranch);
		rootTransformtGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		rootTransformtGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		u.getLocale().addBranchGraph(root);
		c3d.addKeyListener(this);
		
		//add objects
		initPickCanvas();
		initStaticGraph();
		initOrbitBehavoir();
		initMarkerSphere();
		objectBranch.addChild(staticGroup);
	}
	
	void initMarkerSphere()
	{
		markerSphere = new Sphere(0.6F);
		Appearance app = StaticTools.blue();
		if (!app.isLive())
			app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		TransparencyAttributes ta = new TransparencyAttributes();
		ta.setTransparency(0.5f);
		ta.setTransparencyMode(TransparencyAttributes.BLENDED);
		app.setTransparencyAttributes(ta);
		markerSphere.setAppearance(app);
		markerTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		markerTransformGroup.addChild(markerSphere);
		staticGroup.addChild(markerTransformGroup);
		
		markerBoneCylinder = new Cylinder(0.6f, 1);
		markerBoneCylinder.setAppearance(app);
		markerBoneAngle = new TransformGroup();
		markerBoneAngle.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		markerBoneStretchZ = new TransformGroup();
		markerBoneStretchZ.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		markerBoneStretchZ.addChild(markerBoneCylinder);
		markerBoneAngle.addChild(markerBoneStretchZ);
		staticGroup.addChild(markerBoneAngle);
		
		
		markerTextGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		markerTextGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		markerBoneAngle.addChild(markerTextGroup);
	}
	
	void initPickCanvas()
	{
		// Init Picking
		pickCanvasPoints = new PickCanvas(c3d, pickGroup);
		pickCanvasPoints.setMode(PickInfo.PICK_GEOMETRY); 
		pickCanvasPoints.setFlags(PickInfo.NODE | PickInfo.CLOSEST_INTERSECTION_POINT);
		pickCanvasPoints.setTolerance(4.0f);
	}
	
	public Transform3D getViewingTransform()
	{
		Transform3D viewTransDummy = new Transform3D();
		u.getViewingPlatform().getViewPlatformTransform().getTransform(viewTransDummy);
		return viewTransDummy;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
	
		if(args.length > 0)
			System.out.println("We got some Parameters ");
		for (String string : args) {
			System.out.println(string);
		}
		/*
		 * 
		Map <String,String> map =System.getenv();
		for (String string : map.keySet()) {
			System.out.println(string + " :  " + map.get(string));
		}
		System.out.println();
		 */
 
	Java3DSkeletMaker s = 	null; 
	 s=  new Java3DSkeletMaker(true,true);
	
	JFrame frame;
	frame = new JFrame("Frame of Fame");
	frame.addWindowListener(new WindowAdapter() {
	      public void windowClosing(WindowEvent e) {
		        System.exit(0);
		  }
	});
	frame.setSize(500,500);
	frame.add(s);
	frame.setVisible(true);
	} // end main
	
	
	public void toggleFullScreen()
	{
		if (fullscreen == null)
		{
			GraphicsEnvironment env = GraphicsEnvironment.
			getLocalGraphicsEnvironment();
			device = env.getDefaultScreenDevice();
			GraphicsConfiguration gc = device.getDefaultConfiguration();
			fullscreen = new JFrame(gc);
			Toolkit tk = Toolkit.getDefaultToolkit();
			int xSize = ((int) tk.getScreenSize().getWidth());
			int ySize = ((int) tk.getScreenSize().getHeight());
			fullscreen.setSize(xSize, ySize);
			fullscreen.setUndecorated(true);
			fullscreen.setLayout(new BorderLayout());
			
		}

		if(!isFullScreen)
		{
	    fullscreen.setIgnoreRepaint(true);
	    fullscreen.add(content );
		isFullScreen = true;
		//device.setFullScreenWindow(fullscreen);
		fullscreen.setVisible(true);
		}
		else
		{
			this.add(content);
			this.setSize(getSize());
			this.validate();
			c3d.requestFocus();
			fullscreen.dispose();
			fullscreen = null;
			device.setFullScreenWindow(null);
			isFullScreen = false;
			
		}
		
    
	}
	

	Transform3D getTransformFromPickCanvas(PickInfo pickInfo)
	{

		if (pickInfo != null)
		{
			//for (int i = 0; i < picks.length; i++)
			{
				//String s = pickInfo.getNode().getClass().getName();
				//System.out.println(s);
				Shape3D shape = (Shape3D) pickInfo.getNode();
				System.out.println(shape.getUserData());
				String userData = (String) shape.getUserData();
				if (userData != null)
				{
					// if this is a shap with userdata, it could be a sphere
					System.out.println(shape.getParent().toString());
				//	if (shape.getParent().getClass().toString().startsWith("com.sun.j3d.utils.geometry.Sphere") )
					{
						// if its a sphere, it has a transformgroup as parent
						TransformGroup  tg = (TransformGroup) shape.getParent().getParent();
						Transform3D t = new Transform3D() ;
						tg.getTransform(t);
						tg.getLocalToVworld(t);
						return t;
						/*
						System.out.println(t);
						Transform3D t2 = new Transform3D();
						t2.setTranslation(new Vector3d(0,0,1));
						t.mul(t2);
						tg.setTransform(t);
						 */
					}
				}
			}
		}
		return null;
	}
	String getStringFromSphere(PickInfo pickInfo)
	{
		if (pickInfo != null)
		{
			Object node = pickInfo.getNode();
			if (node == null)
				return null;
			if(node.getClass() == Shape3D.class)
			{
				Shape3D shape = (Shape3D) pickInfo.getNode();
				if (shape.getUserData() == null)
					return null;
				if (shape.getUserData().getClass() == String.class)
				{
					String userData = (String) shape.getUserData();
					System.out.println("It's a String User Data: " + userData);
					return userData;
				}
			}
			
		}
		return null;
	}

	public void keyPressed(KeyEvent arg0) {
		int code = arg0.getKeyCode();
		switch(code)
		{
		case KeyEvent.VK_UP: 	
			points = parser.parseFrame();
			skelet.loadFrame(points.points);
			; break;
		case KeyEvent.VK_DOWN:    ; break;
		case KeyEvent.VK_LEFT:   ; break;
		case KeyEvent.VK_RIGHT:   ; break;
		case KeyEvent.VK_W:   ; break;
		case KeyEvent.VK_S:   ; break;
		case KeyEvent.VK_A:    ; break;
		case KeyEvent.VK_D:  ; break;
		case KeyEvent.VK_R:   ; break;
		
		case KeyEvent.VK_CONTROL: 
			curserMode  = true;
			orbit.setEnable(false);
		break;
		
		case KeyEvent.VK_ENTER:
			skelet.connect(markerStart, markerEnd,true); 
			break;
		
		case KeyEvent.VK_BACK_SPACE:
			skelet.disconnect(markerStart,markerEnd);
			break;
			
		case KeyEvent.VK_F: 
			toggleFullScreen();
			break;
		}
		System.out.println("Simple: keyPressed: Keycode: "+arg0.getKeyCode());
		//getDefaultCameraPos();
	//	System.out.println("Key: " + code);
	}




	public void keyReleased(KeyEvent arg0) {
		int code = arg0.getKeyCode();
		switch(code)
		{
			case KeyEvent.VK_CONTROL: 
				curserMode = false;
				orbit.setEnable(true);
			break;
		}
		
	}

	public void keyTyped(KeyEvent arg0) {
		
	}

	public void mouseClicked(MouseEvent arg0) {
		
		pickCanvasPoints.setShapeLocation(arg0);
		
		Transform3D trafo = getTransformFromPickCanvas(pickCanvasPoints.pickClosest());
		if(trafo != null && curserMode)
		{
			System.out.println("Trafo: " + trafo);
			markerTransformGroup.setTransform(trafo);
		}
/*
 * 		
		PickInfo picks =  pickCanvas.pickClosest();
		if (picks != null)
		{
			//for (int i = 0; i < picks.length; i++)
			{
				PickInfo pickInfo = picks;//[i];
				//String s = pickInfo.getNode().getClass().getName();
				//System.out.println(s);
				Shape3D shape = (Shape3D) pickInfo.getNode();
				System.out.println(shape.getUserData());
				String userData = (String) shape.getUserData();
				if (userData != null)
				{
					// if this is a shap with userdata, it could be a sphere
					System.out.println(shape.getParent().toString());
				//	if (shape.getParent().getClass().toString().startsWith("com.sun.j3d.utils.geometry.Sphere") )
					{
						// if its a sphere, it has a transformgroup as parent
						TransformGroup  tg = (TransformGroup) shape.getParent().getParent();
						Transform3D t = new Transform3D() ;
						tg.getTransform(t);
						System.out.println(t);
						Transform3D t2 = new Transform3D();
						t2.setTranslation(new Vector3d(0,0,1));
						t.mul(t2);
						tg.setTransform(t);
					}

				}
			}
		}
 */
		
	} //END MOUSE CLICKED

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
		
		pickCanvasPoints.setShapeLocation(arg0);
		PickInfo pick = pickCanvasPoints.pickClosest();
		if(pick != null)
		{
			Transform3D trafo = getTransformFromPickCanvas(pick);
			markerStart = getStringFromSphere(pick);
			if(trafo != null)
			{
				markerTransformGroup.setTransform(trafo);
			}
		}
		
		
//		pickCanvas.setShapeLocation(arg0);
//		PickInfo pickInfo =  pickCanvas.pickClosest();
//		if (pickInfo != null)
//		{
//			String s = (String) pickInfo.getNode().getUserData();
//			System.out.println("Pressed on " + s);
//			if (s != null)
//				System.out.println("Seems we hit somoething that has a String as Userdata");
//			
//			System.out.println(pickInfo.getNode());
//		}
//		
	}

	public void mouseReleased(MouseEvent arg0) {
		pickCanvasPoints.setShapeLocation(arg0);
		PickInfo pickInfo =  pickCanvasPoints.pickClosest();
		if (pickInfo != null)
		{
			String s = (String) pickInfo.getNode().getUserData();
			if (s != null)
			{
				
			}
		}
	}
 
	public void mouseDragged(MouseEvent arg0) {
		pickCanvasPoints.setShapeLocation(arg0);
		PickInfo pick = pickCanvasPoints.pickClosest();
		if (pick != null && curserMode )
		{
			markerEnd = getStringFromSphere(pick);
			setMarkerBoneRotScale(pick);
		}
		
	}

void setMarkerBoneRotScale(PickInfo target)
{
	Transform3D trafo = getTransformFromPickCanvas(target);
	if(trafo != null)
	{
		System.out.println("Trafo: " + trafo);
		Vector3f a = new Vector3f();
		Vector3f b = new Vector3f();
		Transform3D markerTrans = new Transform3D();
		markerTransformGroup.getTransform(markerTrans);
		markerTrans.get(a);
		trafo.get(b);
		Point3f aa = new Point3f(a);
		Point3f bb = new Point3f(b);
		Transform3D bonetrans=  StaticTools.getTransform(aa,bb);
		try {
			markerBoneAngle.setTransform(bonetrans);
		} catch (BadTransformException e) {
			System.out.println(e.getMessage());
			System.out.println(bonetrans);
		}
		float length = aa.distance(bb);
		Transform3D stretchZ = new Transform3D();
		stretchZ.setScale(new Vector3d(1,length,1));
		markerBoneStretchZ.setTransform(stretchZ);
		
		
		//add Text
		markerTextGroup.removeAllChildren();
		markerTextGroup.addChild(
				StaticTools.createBillBoradText3D(markerStart + " to " + markerEnd, 1));
	}
}


	public void mouseMoved(MouseEvent arg0) {
	}

	public void loadSkeleton(CSMHeader h, CSMPoints frame, SkeletConnections sc) {
		skelet = new Java3DSkelet(h);
		header = h;
		this.points = frame;
		skelet.connections = sc;
		skelet.loadFrame(points.points);
		skelet.reloadConnections();
		setSkelet(skelet);
	}
}// end class
