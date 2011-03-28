package Java3D.SkeletMaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import CSM.CSMHeader;
import Misc.StaticTools;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;

/*
 * Diese Klasse repraesentiert die Datenstruktur Skelett,
 * sie verwaltet die assoziationen von Messpunkten in gruppen von 
 * Koerperteilen und Verbindungspunkten bzw. Gelenken
 */
public class Java3DSkelet {

	// Model Data
	SkeletConnections connections = new SkeletConnections();
	CSMHeader csm_header;

	// Viewing Data
	BranchGroup skeletRoot;
	BranchGroup pointsGroup;
	BranchGroup bones;
	public Sphere[] points;
	TransformGroup[] pointTransforms;
	float scaleFactor = 0.01f;
	
	
	public boolean[] printNodeName = new boolean[100];

	//public BranchGroup bg = new BranchGroup();
	
	public Java3DSkelet(CSMHeader header) {
		csm_header = header;
		connections.setHeader(header);
		points = new Sphere[csm_header.order.length];
		pointTransforms = new TransformGroup[csm_header.order.length];
		init();
		setupInitialPointsPositions();
	}
	
	private void setupInitialPointsPositions() {
		for (int i = 0; i < points.length; i++) {
			//points[i] = new Sphere(0.5f);
			//points[i].setAppearance(Simple.green());
			Transform3D transform =  new Transform3D();
			transform.setTranslation(new Vector3f(i,0,0));
			//System.out.println(tpose[i]);
			pointTransforms[i] = new TransformGroup(transform);
			pointTransforms[i].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			pointTransforms[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//			pointTransforms[i].addChild(points[i]);
			pointTransforms[i].addChild(StaticTools.createSphereWithText(new Point3f(0,0,0), 0.5f, csm_header.order[i]));
			pointTransforms[i].setUserData(csm_header.order[i]);
			pointsGroup.addChild(pointTransforms[i]);
		}
	}
	
	public void connect(String a, String b, boolean store)
	{
		if(store)
			connections.connect(a, b);
		int indexA = csm_header.getPos(a);
		int indexB = csm_header.getPos(b);
		if (indexA < 0 || indexB < 0 || indexA > csm_header.order.length ||
				indexB > csm_header.order.length )
			return;
		TransformGroup angle = new TransformGroup();
		angle.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		angle.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		TransformGroup stretch = new TransformGroup();
		stretch.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		stretch.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		
		angle.addChild(stretch);
		stretch.addChild(new Cylinder(0.4f, 1));
		BoneConnectBehavior aim = new BoneConnectBehavior(pointTransforms[indexA],
												pointTransforms[indexB],angle,stretch);
		aim.setSchedulingBounds(StaticTools.defaultBounds);
		BranchGroup bg = new BranchGroup();
		bg.addChild(angle);
		bg.addChild(aim);
		bones.addChild(bg);
		System.out.println("Java3DSkelet: connectiong: " +a + " and " + b  );
	}
	
	
	public void loadFrame(Point3f[] frame)
	{
		if (frame.length != points.length)
		{
			System.err.println("Skelett: Load Frame: points Mismatch!");
			return;
		}
	// Setting new Translation for Spheres (points)
		Point3f p ;
		for (int i = 0; i < points.length; i++) {
			p = (Point3f) frame[i].clone();
			float z = p.z;
			p.z = p.y;
			p.y = z;
			p.scale(scaleFactor);
			Transform3D t = new  Transform3D();
			t.setTranslation(new Vector3f(p));
			pointTransforms[i].setTransform(t) ;
			p = null;
		}
		
		/*
		// Setting new Translation for Bones
		for (int i = 0; i < connectlist.size()/2; i++) {
			Point3f a = (Point3f) frame[connectlist.get(i*2)].clone();
			float z = a.z;
			a.z = a.y;
			a.y = z;
			Point3f b = (Point3f) frame[connectlist.get(i*2 + 1)].clone();
			 z = b.z;
			b.z = b.y;
			b.y = z;
			a.scale(0.01f);
			b.scale(0.01f);
			moveBone(a, b, boneTransformsLenth.get(i), boneTransformsAngle.get(i));
		}
		 */
		
	}

	public BranchGroup getBG()
	{
			return	skeletRoot;

	}
	
	
	public BranchGroup getBoneGroups()
	{
		return	bones;
		
	}
	
	
	void init()
	{
		skeletRoot = new BranchGroup();
		skeletRoot.setCapability(BranchGroup.ALLOW_DETACH);
		skeletRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		skeletRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		
		pointsGroup = new BranchGroup();
		pointsGroup.setCapability(BranchGroup.ALLOW_DETACH);
		pointsGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		pointsGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		skeletRoot.addChild(pointsGroup);
		
		bones = new BranchGroup();
		bones.setCapability(BranchGroup.ALLOW_PICKABLE_READ);
		bones.setCapability(BranchGroup.ALLOW_PICKABLE_WRITE);
		bones.setCapability(BranchGroup.ALLOW_DETACH);
		bones.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		bones.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);	
		skeletRoot.addChild(bones);

	}


	public void save(String string) {
		File f = new File(string);
		try {
			FileOutputStream fo = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(fo);
			out.writeObject(connections);
		} catch ( IOException e) {
			e.printStackTrace();
		}
		
	}

	public void loadFrame(File file) {

		try {
			FileInputStream fi = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fi);
			this.connections =(SkeletConnections)in.readObject();
			
			for (int i = 0; i < connections.connection_count; i++) {
				String a = connections.connectlist.get(i*2);
				String b = connections.connectlist.get(i*2 +1);
				connect(a, b, false);
			}
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	
}
