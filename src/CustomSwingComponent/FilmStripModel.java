package CustomSwingComponent;

import javax.swing.event.ChangeListener;

public interface FilmStripModel {

	// Position Marking
	
	public int getMarkedFrame();
	public void setMarkedFrame(int markedFrame);
	public boolean isFrameMarked();
	public void setFrameMarked(boolean frameMarked);
	// Area Marking
	public boolean isAreaMarked();
	public void setAreaMarked(boolean marked);
	public void setSelectedArea(int startFrame, int endFrame);
	public int getMarkedFramesCount();
	public int[] getSelectedArea();
	
	
	//AreaViewing
	void setMinimumShownFrame(int framePos);
	void setMaximumShownFrame(int framePos);
	int  getMinimumShownFrame();
	int  getMaximumShownFrame();
	
	//PlayerCurser
	void setPlayerCursor(int framePos);
	int getPlayerCursor();
	
	//SelectorCurser  (mouse Motion Curser : a click makes it to Marked Frame)
	void setSelectorCursor(int framePos);
	int getSelectorCursor();
	
	// Frames settings
	public void setMaxFrames(int maxFrames);
	public int getMaxFrames();
	
	// Events
	void addChangeListener(ChangeListener cl);
	void removeChangeListener(ChangeListener x);
	
	//String info Data
	public String toString();
}
