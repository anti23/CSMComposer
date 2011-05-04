package Gui.ArrangeingUnit;

import java.awt.Color;

import CSM.CSMPoints;

public class NoTransiton extends Transition {

	private static final long serialVersionUID = -6693659926770791438L;
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
