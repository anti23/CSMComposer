package Java3D.CSMPlayer;

import javax.swing.event.ChangeListener;

import datastructure.Animation;


public interface PlayerControlls {

	enum playingDirection  {FWD,BKWD};
	
	void play();
	void pause();
	void jumpto(int frame);
	void jumpto(float timeInSecs);
	void relativeJump(int deltaFrames);
	void relativeJump(float deltaSecs);
	void changeSpeed(float playbackFactor); // 1 normal 2 twice as fast 0.5 half speed ....
	void changePlayingDirection(playingDirection fwd);
	int getFrameCount();
	float getDurration();
	void addChangeListener(ChangeListener cl);
	void removeChangeListener(ChangeListener cl);
	void toggleFullScreen();
	void togglePlaySelection();
	
	// CSM Specific
	public void loadAnimation(Animation new_anim);
	public void loadAnimation(String filename);
	public Animation getAnimation();
	
	// additionals
	public float getSpeed();
	public void setMinMarker(int min);
	public void setMaxMarker(int max);
	public int getMinMarker();
	public int getMaxMarker();
	
}
