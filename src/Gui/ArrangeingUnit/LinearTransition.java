package Gui.ArrangeingUnit;

import java.awt.Color;

import javax.vecmath.Point3f;

import CSM.CSMPoints;

public class LinearTransition extends Transition {

	private static final long serialVersionUID = -4792846773228858762L;

	@Override
	public Color getBackGroundColor() {
		return Color.BLUE.brighter();
	}

	@Override
	public String getTranstionName() {
		return "LinearTransition";
	}

	@Override
	public String getTranstionShortName() {
		return "Linear T.";
	}
	
	

	@Override
	public CSMPoints[] getTransition(CSMPoints start, CSMPoints end) {
		if (start.points.length != end.points.length)
		{
			System.out.println("LinearTransition: start end CSMPointsMismatch");
			return null;
		}
		
		CSMPoints[] result = new CSMPoints[getFrameCount()];
		Point3f[] frame;
		float t = 0;
		for (int i = 0; i < getFrameCount(); i++) 
		{	
			frame = new Point3f[start.points.length];
			for (int j = 0; j < frame.length; j++) {
				frame[j] = linearTrans(start.points[j], end.points[j], t);
			}
			result[i] = new CSMPoints(frame.clone());
			t+= (1.0f/getFrameCount());
		}
		return result;
	}
	
	public Point3f linearTrans(Point3f start, Point3f end,float t) {
			float x = start.x * (1-t) + end.x * (t);
			float y = start.y * (1-t) + end.y * (t);
			float z = start.z * (1-t) + end.z * (t);
			return new Point3f(x,y,z);
	}

}
