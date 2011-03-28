package Java3D.SkeletMaker;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnTransformChange;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import Misc.StaticTools;

public class BoneConnectBehavior extends Behavior {

	TransformGroup a,b,targetRotate,targetStretch;


	protected WakeupCondition  m_WakeupCondition= null; 
	
	public BoneConnectBehavior(TransformGroup from, TransformGroup to, TransformGroup targetRotate, TransformGroup targetStretch) {
		a= from;
		b= to;
		this.targetRotate = targetRotate;
		this.targetStretch = targetStretch;
		doIT();
	}

	@Override
	public void initialize() {
		WakeupCriterion criterionArray[] = new WakeupCriterion[2];
		criterionArray[0] =new WakeupOnTransformChange(a);
		criterionArray[1]  = new WakeupOnTransformChange(b);
		m_WakeupCondition  = new WakeupOr( criterionArray );
		wakeupOn(m_WakeupCondition);
	}

	void doIT()
	{
		Transform3D transA = new Transform3D();
		Transform3D transB = new Transform3D();
		a.getTransform(transA);
		b.getTransform(transB);
		
		Vector3f vecA = new Vector3f();
		Vector3f vecB = new Vector3f();
		transA.get(vecA);
		transB.get(vecB);
		Point3f posA = new Point3f(vecA);
		Point3f posB = new Point3f(vecB);
		try {
			targetRotate.setTransform(StaticTools.getTransform(posA, posB));
		} catch (Exception e) {
			System.out.println(posA + " and " + posB);
		}
		
		float distance = posA.distance(posB);
		Transform3D stretch = new Transform3D();
		stretch.setScale(new Vector3d(1,distance,1));
		targetStretch.setTransform(stretch);
	}
	
	@Override
	public void processStimulus(Enumeration criteria) {
		doIT();
		wakeupOn(m_WakeupCondition);
	}

}
