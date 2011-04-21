package UnUsed;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import datastructure.Animation;

import Java3D.CSMPlayer.PlayerControllStatus;
import Java3D.CSMPlayer.PlayerControlls;
import Java3D.CSMPlayer.PlayerControllStatus.State;
import Java3D.CSMPlayer.PlayerControlls.playingDirection;

public class CSMSimpleRenderdPlayer extends JPanel implements PlayerControlls, Runnable{
	private static final long serialVersionUID = 4680355138781736151L;

	EventListenerList listenerList  = new EventListenerList();
	
	String status = "Nothing Done Yet";
	int frame = 0 ; 
	Thread runnigThread;
	boolean paused = false;
	float speed = 0;
	private int maxFrames = 1000;
	
	public CSMSimpleRenderdPlayer() {
		setPreferredSize(new Dimension(500,500));
		
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		int w = getWidth();
		int h = getHeight();
		
		g.drawString(status, w/2, h/2);
		g.drawString("Frame: " + frame, w/4, h/4);
		
	}
	
	public void addChangeListener(ChangeListener cl)
	{
		
		ChangeListener[] list  = listenerList.getListeners(ChangeListener.class);
		
		boolean inList = false;
		for (ChangeListener changeListener : list) {
			if (changeListener.equals(cl))
			{
				// allready in list
				inList = true;
			}
		}
		if (!inList)
		{
			System.out.println("CSMRenderdPayer : Adding ChangeListener");
			listenerList.add(ChangeListener.class, cl);
		}
	}
	
	public void removeChangeListener(ChangeListener cl) {
		ChangeListener[] list  = listenerList.getListeners(ChangeListener.class);
		for (ChangeListener changeListener : list) {
			if (changeListener.equals(cl))
				listenerList.remove(ChangeListener.class, cl);
		}
	}
	
	// Interface PlayerControllable
	public void play() {
		/*
		 */
		paused = false;
		if (runnigThread != null)
		{
			if (runnigThread.isAlive())
			{
				status = "Already Running";
			}else
			{
				runnigThread = new Thread(this);
				runnigThread.start();
			}
		}else
		{
		runnigThread = new Thread(this);
		runnigThread.start();
		status =  "Playing" + frame;
		}
		repaint();
	}

	public void pause() {
		status = "Pause";
		paused = true;
		repaint();
	}

	public void jumpto(int frame) {
		status = "JumptoFrame " + (frame -1);
		this.frame = frame -1;
		incFrame();
		repaint();
		
	}

	public void jumpto(float timeInSecs) {
		// TODO Auto-generated method stub
		
	}

	public void relativeJump(int deltaFrames) {
		// TODO Auto-generated method stub
		
	}

	public void relativeJump(float deltaSecs) {
		// TODO Auto-generated method stub
		
	}

	public void changeSpeed(float playbackFactor) {
		speed = playbackFactor;
		PlayerControllStatus pcs = new  PlayerControllStatus(State.PlaybackSpeedChanged);
		pcs.speed = playbackFactor;
		fireChangeEvent(pcs);
	}

	public void changePlayingDirection(playingDirection fwd) {
		// TODO Auto-generated method stub
		
	}

	public int getFrameCount() {
		return maxFrames ;
	}

	public float getDurration() {
		// TODO Auto-generated method stub
		return 0;
	}

	private synchronized int getFrame()
	{
		return frame;
	}
	
	private void fireChangeEvent(PlayerControllStatus pcs)
	{
		ChangeListener[] list = listenerList.getListeners(ChangeListener.class);
		for (ChangeListener changeListener : list) {
			changeListener.stateChanged(new ChangeEvent(pcs));
		}
	}
	
	private synchronized void incFrame()
	{
		frame++;
		PlayerControllStatus pcs = new PlayerControllStatus(State.FramePosUpdate);
		pcs.firstFrame = pcs.lastFrame = frame;
		fireChangeEvent(pcs);
		repaint();
	}
	
	public void run() {
		while(frame < maxFrames)
		{
			try {
				while (paused) Thread.sleep(100);
				Thread.sleep((long) (1/Math.max(1, speed ) * (1000/25)) );} catch (InterruptedException e) {			}
			incFrame();
		}
	}

	public void toggleFullScreen() {
		// TODO Auto-generated method stub
		
	}

	public void loadAnimation(Animation new_anim) {
		// TODO Auto-generated method stub
		
	}

	public void loadAnimation(String filename) {
		// TODO Auto-generated method stub
		
	}

	public float getSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setMarker(int min, int max) {
		// TODO Auto-generated method stub
		
	}

	public Animation getAnimation() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMarker(int min) {
		// TODO Auto-generated method stub
		
	}

	public int getMinMarker() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxMarker() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void togglePlaySelection() {
		// TODO Auto-generated method stub
		
	}

	public void setMinMarker(int min) {
		// TODO Auto-generated method stub
		
	}

	public void setMaxMarker(int max) {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}


}
