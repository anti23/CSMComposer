package CustomSwingComponent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultFilmStripModel implements FilmStripModel {

	EventListenerList listenerList = new EventListenerList();
	private int markedFrame = -1 ;
	private boolean frameMarked ;
	private int maxFrames = -1;
	
	int selectorCursor; // Mousemotion
	int playerCursor; // Playback Pos ()
	
	//Zoom
	int minimumShownFrame;
	int maximumShownFrame;

	// Marking
	int[] markedArea = {0,0};
	

	protected void fireStateChanged() {
		ChangeEvent event = new ChangeEvent(this);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				((ChangeListener) listeners[i + 1]).stateChanged(event);
			}
		}
	}

	public boolean isAreaMarked() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAreaMarked(boolean marked) {
		// TODO Auto-generated method stub

	}

	public void setMarkedArea(int startFrame, int endFrame) {
		markedArea[0] = startFrame;
		markedArea[1] = endFrame;
	}

	public int getMarkedFramesCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int[] getMarkedArea() {
		return markedArea;
	}

	public void addChangeListener(ChangeListener cl) {
		listenerList.add(ChangeListener.class, cl);
	}

	public void removeChangeListener(ChangeListener x) {
		listenerList.remove(ChangeListener.class, x);
	}

	
	public ChangeListener[] getChangeListeners() {
		return (ChangeListener[]) listenerList
				.getListeners(ChangeListener.class);
	}

	public void setFrameMarked(boolean frameMarked) {
		this.frameMarked = frameMarked;
	}

	public boolean isFrameMarked() {
		return frameMarked;
	}

	public void setMarkedFrame(int markedFrame) {
		this.markedFrame = markedFrame;
	}

	public int getMarkedFrame() {
		return markedFrame;
	}

	public void setMaxFrames(int maxFrames) {
		//Setting new Max Frames, automatically zooms out to full animation
		this.maxFrames = maxFrames;
		setMinimumShownFrame(0);
		setMaximumShownFrame(maxFrames);
	}

	public int getMaxFrames() {
		return maxFrames;
	}

	public void setPlayerCurssr(int framePos) {
		// TODO Auto-generated method stub
		
	}

	public int getPlayerCursor() {
		return playerCursor;
	}

	public void setSelectorCursor(int framePos) {
		selectorCursor = framePos;
	}

	public int getSelectorCursor() {
		return selectorCursor;
	}

	public void setPlayerCursor(int playerCursor) {
		this.playerCursor = playerCursor;
	}

	public void setMinimumShownFrame(int minimumShownFrame) {
		this.minimumShownFrame = minimumShownFrame;
	}

	public void setMaximumShownFrame(int maximumShownFrame) {
		this.maximumShownFrame = maximumShownFrame;
	}

	public int getMinimumShownFrame() {
		return minimumShownFrame;
	}

	public int getMaximumShownFrame() {
		return maximumShownFrame;
	}


}
