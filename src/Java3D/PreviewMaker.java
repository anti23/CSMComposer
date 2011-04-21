package Java3D;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import CSM.CSMHeader;
import CSM.CSMPoints;
import Misc.StaticTools;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import datastructure.LitQuad;
import datastructure.Skelett;


/**
 *  Diese Klasse Erzeugt mittels Java3D kleine vorschau Bilder einer <code>datasrtucture.Animation</code>
 * @author Johannes
 */
public class PreviewMaker {
	
	private BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

	static int instanceCounter = 0; 
	SimpleUniverse u_offscreen;
	Canvas3D c3d_offscreen;
	BranchGroup root = new BranchGroup();
	BranchGroup dynamicGroup = new BranchGroup();
	Skelett skelett ;
	
	int width = 0;
	int height = 0;
	
	// Maps FrameNumber and resulting Image
	public Map<Integer,Image> results = new HashMap<Integer, Image>();

	private boolean withGroundPlate = true;
	private boolean withOrigin = false;
	
	
	public PreviewMaker(CSMHeader header) {
		
		instanceCounter ++;
		System.out.println("instancing PreviewMaker  i: " + instanceCounter);
		if (instanceCounter <= 32)
		{
			
		initUniverse();
		initDefaults();
		skelett = new Skelett(header);
		}else
		{
			finalize();
		}
	}
	
	
	static PreviewMaker instance;
	public static PreviewMaker getInstance(CSMHeader header)
	{
		if (instance == null)
		{
			instance = new PreviewMaker(header);
		}else if (!instance.skelett.getHeader().equals(header))
		{
			System.out.println("PreviewMaker: getInstance: SkeletHeaders Not matiching");
			instance.finalize();
			instance = null;
			instance = new PreviewMaker(header);
		}
		
		return instance;
	}
	

	private void initDefaults() {
		width = 100;
		height = 70;
		//skelett.loadFrame(CSMPoints.defaultTPose().points);
	}



	private void initUniverse() {
		// Group Capabilities
		root.setCapability(BranchGroup.ALLOW_DETACH);
		root.addChild(createStaticEnviroment());
		dynamicGroup.setCapability(BranchGroup.ALLOW_DETACH);
		dynamicGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		dynamicGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		dynamicGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		
		root.addChild(dynamicGroup);

		// Java 3D Offscreen
		c3d_offscreen = new Canvas3D(SimpleUniverse.getPreferredConfiguration(), true);
		u_offscreen = new SimpleUniverse(c3d_offscreen);
		u_offscreen.getLocale().addBranchGraph(root);
			// Set View 
		u_offscreen.getViewingPlatform().getViewPlatformTransform().setTransform(
												StaticTools.getDefaultCameraPos());
		
		setOffscreenWidthHeight(500, 500);
	}
	
	public void setOffscreenWidthHeight(int width,int height)
	{
		if(c3d_offscreen != null)
		{
			c3d_offscreen.getScreen3D().setSize(width, height);
			c3d_offscreen.getScreen3D().setPhysicalScreenWidth(0.0254/90.0 * width);
			c3d_offscreen.getScreen3D().setPhysicalScreenHeight(0.0254/90.0 * height);
		}
		else System.err.println("PreviewMaker: c3d_offscreen is null");
		
	}
	
	
	public Image getImage(Point3f[] points)
	{
	//	System.out.println("PreviewMaker: getImage: givePoints Size: " + points.length +" Skeket PointsSize: " + skelett.points.length);
		skelett.loadFrame(points);
		dynamicGroup.removeAllChildren();
		dynamicGroup.addChild(skelett.getBG());
		ImageComponent2D imgC =null ;
		imgC =  new ImageComponent2D(ImageComponent2D.FORMAT_RGB,new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
		c3d_offscreen.setOffScreenBuffer(imgC);
		c3d_offscreen.renderOffScreenBuffer();
		c3d_offscreen.waitForOffScreenRendering();


		//ImageComponent2D imgC2 = c3d_offscreen.getOffScreenBuffer();
		BufferedImage bImage = imgC.getImage();
		Image image = bImage.getScaledInstance(width, height, BufferedImage.SCALE_FAST);
		return image;
	}

	
	BranchGroup createStaticEnviroment()
	{
		BranchGroup staticGroup = new BranchGroup();
		// 3D Measure Coordinates and Origin
		
		if(withOrigin )
		{
			staticGroup.addChild(StaticTools.createSphereWithText(new Point3f(0,0,0), 0.5f, "Origin"));
			staticGroup.addChild(StaticTools.createSphereWithText(new Point3f(5,0,0), 0.4f, "X=5"));
			staticGroup.addChild(StaticTools.createSphereWithText(new Point3f(0,5,0), 0.4f, "Y=5"));
			staticGroup.addChild(StaticTools.createSphereWithText(new Point3f(0,0,5), 0.4f, "Z=5"));
		}
		
		
		// Boden
		float a = 30.0f;
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
			System.out.println("PreviewMaker: initStatic Graph: Trouble Loading Texture");
		}finally
		{
			if(withGroundPlate)
				staticGroup.addChild(lq);
		}
	
	      
	      staticGroup.addChild(createStaticLights());
	      return staticGroup;
	}
	
    BranchGroup createStaticLights()
	{
    	BranchGroup lightsGroup = new BranchGroup();
		 Color3f ambientLightColour = new Color3f(0.9f, 0.9f, 0.9f);
	        AmbientLight ambientLight = new AmbientLight(ambientLightColour);
	        ambientLight.setInfluencingBounds(bounds);
	        Color3f directionLightColour = new Color3f(1.0f, 1.0f, 1.0f);
	        Vector3f directionLightDir = new Vector3f(-1.0f, -1.0f, -1.0f);
	        DirectionalLight directionLight = new DirectionalLight(directionLightColour, directionLightDir);
	        directionLight.setInfluencingBounds(bounds);
	        lightsGroup.addChild(ambientLight);
	        lightsGroup.addChild(directionLight);
	        Background background =new Background(new Color3f(0.0f,0.1f,0.4f));
	        background.setApplicationBounds(bounds);
	        lightsGroup.addChild(background);
	        
	        return lightsGroup;
	}
    
    
    
    
    public ImgPanel newImgPanel(Image img)
    {
    	return new ImgPanel(img);
    }
    
    class ImgPanel extends Canvas
    {
	  private static final long serialVersionUID = 7834586769410045313L;
		Image i;
    	public ImgPanel(Image img) {
    		i = img;
    		setPreferredSize(new Dimension(i.getWidth(null),i.getHeight(null)));
    	}
    	@Override
    	public void paint(Graphics g) {
    		//super.paint(g);
    		g.drawImage(i, 0,0,null);
    	}
    }
    public static void main(String[] args) {
    	System.out.println("PreviewMaker Text Prgramm Starting!");

    	//Defualt Frame shitt!
    	Frame f = new Frame("Displaying image");
    	f.setSize(550, 550);
    	f.setLayout(new BorderLayout());
    	f.addWindowListener(new WindowAdapter() {
    		public void windowClosing(WindowEvent e) {
    			System.exit(0);
    		}
    	});
    	ScrollPane sp = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
 		Panel panel = new Panel(new GridLayout(5,100));
    	sp.add(panel);
    	sp.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    	f.add(sp,BorderLayout.CENTER);
    	
    	// The Wow Lisnterner Feature
		for (int i = 0; i < 33; i++) {
			PreviewMaker pm = new PreviewMaker(CSMHeader.defaultHeader());
			if (pm!= null )
				panel.add(pm.newImgPanel(pm.getImage(CSMPoints.defaultTPose().points)));
			//pm.finalize();
		}
		
		f.setVisible(true);
		
		
	}
    
    protected void finalize ()
    {
    	if(u_offscreen != null)
    		u_offscreen.cleanup();
    	else 
    		System.out.println("Finalize Called from Constructor!");
    	instanceCounter --;
    	System.out.println("Finalizing PreviewMaker i: " + instanceCounter);
    }
}
