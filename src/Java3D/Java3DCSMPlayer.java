package Java3D;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.PickInfo;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Texture;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import CustomSwingComponent.JFilmStripSlider;
import Java3D.CSMPlayer.PlayerControllStatus;
import Java3D.CSMPlayer.PlayerControlls;
import Misc.StaticTools;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.pickfast.PickCanvas;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import datastructure.*;

public class Java3DCSMPlayer extends JPanel implements KeyListener,ChangeListener, PlayerControlls{
	private static final long serialVersionUID = 1L;
	public Animation animation;
	
	JFrame fullscreen;
	boolean isFullScreen = false;
    GraphicsDevice device;
    JPanel content = new JPanel();
	
    Canvas3D c3d;
	SimpleUniverse u;
	BranchGroup objectBranch = new BranchGroup();
	BranchGroup root = new BranchGroup();
	Transform3D rootTransform = new Transform3D();
	TransformGroup rootTransformtGroup = new TransformGroup();
  	
	BranchGroup ballGroups =new BranchGroup();
	BranchGroup staticGroup =new BranchGroup();
	Thread animationThread ;
	EventListenerList listenerList = new EventListenerList();
	int SelectedItem = -1;
	boolean isStandAlone = false;
	
	//Marker
	int markerMin = -1,markerMax = -1;
	
	// Visibility Attributes
	boolean showEnviroment = true;
	boolean showOrigin = true;
	Switch enviromentSwitch = new Switch();
	Switch originSwitch = new Switch();
	
	private SimpleUniverse u_offscreen;
	
	
	public Java3DCSMPlayer(boolean isStandAlone,boolean withFileChooser) throws IOException {
		File file = null;
		Animation a;
		this.isStandAlone = isStandAlone;
		if(withFileChooser)
		{
//			JFileChooser chooser  = new JFileChooser(".");
//			FileFilter ff = new FileFilter() {
//				@Override
//				public String getDescription() {
//					return "*.csm";
//				}
//				@Override
//				public boolean accept(File arg0) {
//					if (arg0.getName().endsWith("csm"))
//						return true;
//					return false;
//				}
//			};
//			chooser.setFileFilter(ff);
//			chooser.showOpenDialog(null);
//			file = chooser.getSelectedFile();
//			
			file = StaticTools.openDialog("csm", false);
			System.out.println(file.getPath());
			a = new Animation(file.getCanonicalPath());	
		}else 
		{
			a = new Animation();	
		}
		
		initFrame();
		init3D();
	 	loadAnimation(a);
	 	if(isStandAlone)
	 		play();
	}
	
	public Animation getMarkedAnimation()
	{
		if (markerMin> -1 && markerMax >-1)
			return animation.getSubSequentAnimation(markerMin, markerMax);
		return null;
	}
	
	
	public void captureScreen()
	{
		Canvas3D c3d_offscreen;
		c3d_offscreen =  new Canvas3D(SimpleUniverse.getPreferredConfiguration(), true);
		u_offscreen = new SimpleUniverse(c3d_offscreen);
		Transform3D viewTrans = new Transform3D();
		u.getViewingPlatform().getViewPlatformTransform().getTransform(viewTrans);
		u_offscreen.getViewingPlatform().getViewPlatformTransform().setTransform( viewTrans	);
		String filename = "textIMG.jpg";
		
		c3d_offscreen.getScreen3D().setSize(500, 500);
		c3d_offscreen.getScreen3D().setPhysicalScreenWidth(0.0254/90.0 * 500);
		c3d_offscreen.getScreen3D().setPhysicalScreenHeight(0.0254/90.0 * 500);

		//BranchGroup new_root = (BranchGroup) ((BranchGroup) u.getLocale().getAllBranchGraphs().nextElement()).cloneTree();
		c3d.postSwap();
		root.detach();
		root.compile();
		u_offscreen.getLocale().addBranchGraph(root);

		ImageComponent2D imgC = new ImageComponent2D(ImageComponent2D.FORMAT_RGB,new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB));
		c3d_offscreen.setOffScreenBuffer(imgC);
		c3d_offscreen.renderOffScreenBuffer();
		c3d_offscreen.waitForOffScreenRendering();

		BufferedImage bImage = imgC.getImage();
		
		try {
			ImageIO.write(bImage, "jpg", new File(filename));
		} catch (IOException e) {e.printStackTrace();}
		
		root.detach();
		u.getLocale().addBranchGraph(root);
	}

	private void fireChangeEvenet(PlayerControllStatus pcs)
	{
		
		ChangeListener[] changeListeners = listenerList.getListeners(ChangeListener.class);
		for (ChangeListener changeListener : changeListeners) {
			changeListener.stateChanged(new ChangeEvent(pcs));
		}
	}
	
 
	public void loadAnimation(Animation new_anim)
	{
		if(animation != null)
		{
			stop();
			animation = null;
		}
		animation = new_anim;

		ballGroups.removeAllChildren();
		ballGroups.addChild(animation.initPlayerGroup());
		System.out.println("Simple Player: Animation thread startet!");
		
		animation.addChangeListener(this);
		animation.fireChangeListenerUpdateEvents();
		
	}

	public void loadAnimation(String filename) {
		Animation a = new Animation();
		a.addChangeListener(this);
		
		try {
			a.loadFile(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadAnimation(a);
	}

	public void toggleEnvirment()
	{
		showEnviroment = !showEnviroment; 
		if (showEnviroment)
			enviromentSwitch.setWhichChild(0);
		else 
			enviromentSwitch.setWhichChild(999);
	}

	public void toggleOrigin()
	{
		showOrigin = !showOrigin; 
		if (showOrigin)
			originSwitch.setWhichChild(0);
		else 
			originSwitch.setWhichChild(999);
	}
	
	
	void initStaticGraph()
	{
		originSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
		originSwitch.setCapability(Switch.ALLOW_SWITCH_READ);
		enviromentSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
		enviromentSwitch.setCapability(Switch.ALLOW_SWITCH_READ);
		originSwitch.setWhichChild(0);
		enviromentSwitch.setWhichChild(0);
		
		BranchGroup origin = new BranchGroup();
		// 3D Measure Coordinates and Origin
		origin.addChild(StaticTools.createSphereWithText(new Point3f(0,0,0), 0.5f, "Origin"));
		origin.addChild(StaticTools.createSphereWithText(new Point3f(5,0,0), 0.4f, "X=5"));
		origin.addChild(StaticTools.createSphereWithText(new Point3f(0,5,0), 0.4f, "Y=5"));
		origin.addChild(StaticTools.createSphereWithText(new Point3f(0,0,5), 0.4f, "Z=5"));
		
		//Cylinders
		origin.addChild(StaticTools.cylinder(new Point3f(0,0,0),
				new Point3f(5,0,0),
				0.1f,StaticTools.yellow()));
		
		origin.addChild(StaticTools.cylinder(new Point3f(0,0,0),
				new Point3f(0,5,0),
				0.1f,StaticTools.yellow()));
		
		origin.addChild(StaticTools.cylinder(new Point3f(0,0,0),
				new Point3f(0,0,5),
				0.1f,StaticTools.yellow()));
	
		originSwitch.addChild(origin);
		staticGroup.addChild(originSwitch);
		
		BranchGroup enviroment = new BranchGroup();
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
			enviroment.addChild(lq);
		}
	      
	      
	      // Cameras
	      
	      float r = 30;
	      float h = 22;

	      for (float i =  (float) -Math.PI; i <= Math.PI ; i+= Math.PI/3)
	      {
	    	  
	    	  float x = (float) (Math.sin(i) * r);
	    	  float z = (float) (Math.cos(i) * r);
	    	  
	    	  enviroment.addChild(StaticTools.cylinder(new Point3f(x,h/4,z), 
					new Point3f(x,h,z),
					0.5f));
			
	    	  enviroment.addChild(StaticTools.cylinder(new Point3f(x,h,z), 
					new Point3f(x- x/10,h-1 ,z- z/10),
					0.7f));
			for (float j = 0; j <= Math.PI * 2 ; j+= Math.PI/1.5f) 
			{
				float r2  = 3.3f;
				float x2 = (float) (Math.sin(j) * r2);
				float z2 = (float) (Math.cos(j) * r2);
				
				enviroment.addChild(StaticTools.cylinder(
						new Point3f(x  + x2,0,z + z2),
						new Point3f(x,h/4,z), 
						0.5f));
			}
		}  
	     enviromentSwitch.addChild(enviroment);
	     staticGroup.addChild(enviromentSwitch);
	}

	private void initOrbitBehavoir()
	{
		OrbitBehavior orbit = 
			new OrbitBehavior(c3d, OrbitBehavior.REVERSE_ALL);
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
			final Java3DCSMPlayer simple = this;
			final JFilmStripSlider slider = new JFilmStripSlider();
			slider.setPlayerToControll(this);
			content.add(slider,BorderLayout.SOUTH);

			addChangeListener(slider.getUI());
			/*
			Timer timer = new Timer(1000, new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					slider.setPlayerToControll(simple);
					slider.setZoom(0, animation.header.lastFrame);
				}
			});
			timer.setRepeats(false);
			timer.start();
			 */
		}
		
		this.add(content,BorderLayout.CENTER);
		setVisible(true);

	}
	
	private void init3D() {
		root = new BranchGroup();
		root.setCapability(BranchGroup.ALLOW_DETACH);
		
		objectBranch.setCapability(BranchGroup.ALLOW_AUTO_COMPUTE_BOUNDS_READ);
		objectBranch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		objectBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		objectBranch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		
		ballGroups.setCapability(BranchGroup.ALLOW_DETACH);
		ballGroups.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		ballGroups.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		ballGroups.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		objectBranch.addChild(ballGroups);
		c3d = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		//c3d.addMouseListener(this);
		
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
		
	//	view.setMinimumFrameCycleTime(100);

	//	objectBranch.addChild(new ColorCube());
		root.addChild(rootTransformtGroup);
		rootTransformtGroup.addChild(objectBranch);
		//rootTransformtGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		rootTransformtGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		rootTransformtGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		u.getLocale().addBranchGraph(root);
		c3d.addKeyListener(this);
		//f.add(c3d);
		
		//add objects
		initStaticGraph();
		initOrbitBehavoir();
		objectBranch.addChild(staticGroup);
	}
	
	public Transform3D getViewingTransform()
	{
		Transform3D viewTransDummy = new Transform3D();
		u.getViewingPlatform().getViewPlatformTransform().getTransform(viewTransDummy);
		return viewTransDummy;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
	
		System.out.println("Staring Simple CMS Renderer ... ");
		
		if(args.length > 0)
			System.out.println("We got some Parameters ");
		for (String string : args) {
			System.out.println(string);
		}
 
	Java3DCSMPlayer s = 	null; 
	if (args.length > 0)
	{
		s = new Java3DCSMPlayer(true,false);
		s.loadAnimation(args[0]);
		s.play();
	}else 
	 s=  new Java3DCSMPlayer(true,true);
	
	JFrame frame;
	frame = new JFrame("Java 3D CSM Viewer");
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
	

	public void keyPressed(KeyEvent arg0) {
		int code = arg0.getKeyCode();
		switch(code)
		{
		case KeyEvent.VK_UP: 	  ; break;
		case KeyEvent.VK_DOWN:    ; break;
		case KeyEvent.VK_LEFT:   ; break;
		case KeyEvent.VK_RIGHT:   ; break;
		case KeyEvent.VK_W:   ; break;
		case KeyEvent.VK_S:   ; break;
		case KeyEvent.VK_A:    ; break;
		case KeyEvent.VK_D:  ; break;
		case KeyEvent.VK_R:   ; break;
		
		case KeyEvent.VK_SPACE: animation.isAnimating = !animation.isAnimating; break;
		case KeyEvent.VK_F: 
			toggleFullScreen();
			break;
		case KeyEvent.VK_ENTER : captureScreen();
			break;
		}
		System.out.println("Simple: keyPressed: Keycode: "+arg0.getKeyCode());
		//getDefaultCameraPos();
	//	System.out.println("Key: " + code);
	}

	public void keyReleased(KeyEvent arg0) {
		
	}

	public void keyTyped(KeyEvent arg0) {
		
	}


	public void stop() {
		animation.stop();
	}



	public void play() {
		animation.play();
		animation.isAnimating = true;
	}



	public void pause() {
		animation.isAnimating = false;
	}



	public void jumpto(int frame) {
		animation.setFrame(frame);
	}

	public void jumpto(float timeInSecs) {
		
	}

	public void relativeJump(int deltaFrames) {
		
	}



	public void relativeJump(float deltaSecs) {
		
	}



	public void changeSpeed(float playbackFactor) {
		animation.playbackSpeed = playbackFactor;
	}



	public void changePlayingDirection(playingDirection fwd) {
		
	}



	public int getFrameCount() {
		 if (animation != null)
		return animation.header.lastFrame - animation.header.firstFrame;
		 else return 0;
	}



	public float getDurration() {
		if (animation != null)
			if (animation.header != null )
			{
				float rate = animation.header.framerate;
				int frameCount = animation.header.lastFrame - 
									animation.header.firstFrame;
				float timeInSecs = frameCount/rate;
				return timeInSecs;
			}
		return 0;
	}


	public Animation getAnimation()
	{
		if (animation != null)
			return animation;
		else 
			System.out.println("Java3dCSMPlayer: getAnimation: no animation Loaded");
		return null;
	}
	

	public void addChangeListener(ChangeListener cl) {
		System.out.println("Java3dCSMPLayer: addChangeListener ");
		listenerList.add(ChangeListener.class, cl);
		System.out.println(listenerList);
	}



	public void removeChangeListener(ChangeListener cl) {
		listenerList.remove(ChangeListener.class, cl);
		
	}


	private void fireChangeEnvent(PlayerControllStatus pcs)
	{
		for (ChangeListener cl : listenerList.getListeners(ChangeListener.class)) {
			cl.stateChanged(new ChangeEvent(pcs));
		}
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource().getClass() == PlayerControllStatus.class)
		{
			PlayerControllStatus pcs = (PlayerControllStatus) ce.getSource();
			fireChangeEnvent(pcs);
		}
	}



	public float getSpeed() {
		return animation.playbackSpeed ;
	}



	public void setMarker(int markerFrame) {
		if (markerFrame > markerMax)
		{
			markerMin = markerMax;
			markerMax = markerFrame;
		}else
		{
			markerMin = markerFrame;
		}
		System.out.println("min: " + markerMin + " max: " + markerMax );
		
	}



	public int getMinMarker() {
		return markerMin;
	}



	public int getMaxMarker() {
		return markerMax;
	}







	
}// end class
