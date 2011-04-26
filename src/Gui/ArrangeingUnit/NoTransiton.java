package Gui.ArrangeingUnit;

import java.awt.Color;

import javax.vecmath.Point3f;

import CSM.CSMPoints;

public class NoTransiton extends Transition {

	
	public NoTransiton() {
		super();
	}
	@Override
	public Color getBackGroundColor() {
		
		return Color.GREEN;
	}

	@Override
	public String getTranstionName() {
		return "No Transition";
	}

	@Override
	public String getTranstionShortName() {
		return "No T.";
	}
	@Override
	public void setFrameCount(int frameCount) {
		// No FrameCount for No Transition purposes!
		super.setFrameCount(0);
	}
	@Override
	public CSMPoints[] getTransition(CSMPoints start, CSMPoints end) {
		return new CSMPoints[0];
	}

	 

}
