package datastructure;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.media.j3d.BranchGroup;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import CSM.CSMHeader;
import CSM.CSMParser;
import CSM.CSMPoints;
import Gui.ArrangeingUnit.Snippit;
import Java3D.PreviewMaker;
import Java3D.CSMPlayer.PlayerControllStatus;
import Java3D.CSMPlayer.PlayerControllStatus.State;
import Java3D.CSMPlayer.PlayerControlls.playingDirection;

/**
 * The animation Class creates, holds and manages 
 * a sequenz of CSMPoints
 *  * @author Johannes
 */
public class Animation implements 
									ChangeListener,
									ActionListener, Serializable{
	
	private static final long serialVersionUID = -4140561763765572689L;
	//timer
	Timer timer ;
	long animStart; 
	
	
	private Skelett skelett;
	String filename;
	CSMParser parser;
	Calendar cal = Calendar.getInstance();
	public CSMHeader header;
	//CSMPoints currentFrame;
	CSMPoints[] frames;
	float lenghtInSecs;
	int framecount ;
//	BranchGroup skeletGroup;
	int framePos = 0 ;
	int lastLoadedFrame = 0;
	boolean loadingComplete = false;
	public boolean isAnimating = false;
//	private JFilmStripSlider slider = null ;
	boolean isStopped = false; // breaking condition for Animation Thread
	private EventListenerList listenerList = new EventListenerList();
	public Map <Integer,ImageIcon> previews = new HashMap<Integer, ImageIcon>();
	int previewCount = 10;
	
	// Player Controll Attributes
	public float playbackSpeed = 1.0f;
	private playingDirection playbackDirection = playingDirection.FWD;
	int markedArea[] = new int[2];
	boolean playSelection = false;
	
	public Animation() {
		header = CSMHeader.defaultHeader();
		frames = new CSMPoints[1];
		frames[0] = CSMPoints.defaultTPose();
		skelett = new Skelett(header);
		skelett.loadFrame(frames[0].points);
	}

	
	public Animation(String filename) {

		this.filename = filename;
		try {
			loadFile();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	public Animation(String filename, ChangeListener cl) {
		
		listenerList.add(ChangeListener.class, cl);
		
		this.filename = filename;
		try {
			loadFile();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	public Animation getSubSequentAnimation(int firstFrame, int lastFrame)
	{
		Animation anim = new Animation();
		anim.header = header;
		anim.header.firstFrame = 0;
		anim.header.lastFrame = lastFrame-firstFrame;
		anim.framecount = lastFrame-firstFrame;
		anim.previews = new HashMap<Integer, ImageIcon>();
		Set<Integer> set =  previews.keySet();
		for (Integer i : set) {
			if (i > firstFrame && i < lastFrame)
				anim.previews.put(i - firstFrame, previews.get(i));
				
		}
		
		anim.filename = "frame " + firstFrame + " to " + lastFrame + "of Animation " + filename;
		anim.frames = new CSMPoints[lastFrame-firstFrame];
		for (int i = firstFrame; i < lastFrame; i++) {
			anim.frames[i- firstFrame] = frames[i];
		}
		
		return anim;
	}

	public Skelett getSkelett()
	{
		return this.skelett;
	}
	// Constructs and loads CSM datastructure
	void loadFile() throws IOException
	{
		if (filename != null)
		{
			File f = new File(filename);
			if (!f.isFile())
				throw new IOException("File not found "+ f );
		}
		parser = new CSMParser();
		parser.scanFile(filename);
		header = parser.getHeader();
		
		PlayerControllStatus pcs = new PlayerControllStatus(State.AnimationHeaderLoaded);
		pcs.header = header;
		fireChangeEvenet(pcs);

		framecount = header.lastFrame-header.firstFrame;
		frames = new CSMPoints[framecount];
		frames[0] = parser.parseFrame();
		
		
		
		
		if(framecount > 0)	// start asyc loading
		{
			 new Thread(new AsyncLoading()).start();
		}
		
		skelett = new Skelett(header);
		// wait for first framm parsed
		while (lastLoadedFrame < 1)
		{
			try {Thread.sleep(100);	} catch (InterruptedException e) {}
		}
		skelett.loadFrame(frames[0].points);

		pcs = new PlayerControllStatus(State.AnimationLoaded);
		pcs.firstFrame = header.firstFrame;
		pcs.lastFrame = header.lastFrame;
		fireChangeEvenet(pcs);
	}
	

	public void loadFile(String filename) throws IOException {
		this.filename = filename;
		parser = null;
		loadFile();
	}
	
	
	public BranchGroup initPlayerGroup()
	{
	//	skelett = new Skelett(header);
		return skelett.getBG();
	}

	public void play() {
		if (timer != null)
		{
				timer.stop();
				timer = null;
				System.gc();
		}
		isStopped = false;
		isAnimating = true;
		
		timer = new Timer(1000/20, this);
		timer.setDelay(1000/20);
		timer.setRepeats(true);
		timer.start();
		animStart = System.currentTimeMillis();
		
		System.out.println("Animation: play : timer :" + timer + 
				" \n \t animStart " + animStart + 
				" \n \t framepos " + framePos  +
				" \n \t framerate " + header.framerate  
				/*" \n \t listener List: " + listenerList*/
				);
	}
	
	public void togglePlaySelection()
	{
		if (playSelection)
		{
			playSelection = false;
			
		}else if (checkSelection())
		{
		playSelection = true;
		}
		play();
	}
	
	private boolean checkSelection() {
		//sort marked Area
		if(markedArea[0] > markedArea[1])
		{
			int bigger = markedArea[0];
			markedArea[0] = markedArea[1];
			markedArea[1] = bigger;
		}
		if (markedArea[0] > header.firstFrame && markedArea[1] < header.lastFrame)
			return true;
		else
			System.out.println("Animation: check Selection marked Area out of bound: " +markedArea );
		return false;
	}


	public void stop() {
		isStopped = true;
		isAnimating = false;
		if (timer != null)
		{
			timer.stop();
		}
	}
	
	//playback timing Attributes
	int deltaFrame ;
	long delta;	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == timer)
		{
			if (!isAnimating)
			{
				timer.setRepeats(false);
				timer.stop();
				return;
			}
			
			delta =  System.currentTimeMillis() - animStart ;
			animStart = System.currentTimeMillis();
		//	System.out.println("Running for: " + delta + " Milliseconds");
			float frameRate = header.framerate;
			float speedControllFactor = playbackSpeed;
			if (!isAnimating)
				speedControllFactor = 0;
			deltaFrame= (int) ((delta* frameRate * speedControllFactor)/1000 ); //delta in millis
																		//
			framePos += deltaFrame;
			
			// Differentaite Normal Looped and Selected Area Looped Playback
			if (playSelection)
			{
				if (framePos < markedArea[0])
					framePos = markedArea[0];
				if (framePos > markedArea[1])
					framePos = markedArea[0];
					
			}
			setFrame((framePos) % frames.length);
			
		}
	}


	public void setFrame(int frame) {
	//	System.out.println("Animation: setFrame: frame " + frame);
		if (frame < lastLoadedFrame)
			framePos = frame;
		else if (frame > lastLoadedFrame)
		{
			if (frame > (header.lastFrame - 1) )
			{
				System.out.println("Animation: setFrame: Trying to Load Frame : " + frame);
				System.out.println("Animation: setFrame: Header.lastFrame  " + header.lastFrame);
				return;
			}
			System.out.print("Animation: setFrame: Waiting for Frame "+ frame +" :");
			while (lastLoadedFrame <= frame)
				try {
					Thread.sleep(100);
					System.out.print("."+ lastLoadedFrame);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			System.out.println("Loading Done");
		}else return;
		loadFrame();
	}
	
	private void loadFrame()
	{
		if (framePos >= 0 && framePos < frames.length)
		{
			skelett.loadFrame(frames[framePos].points);
			fireChangeEvent(framePos);
		}
		else 
			System.err.println("Animation: loadFrame:"+ framePos + " frames.length: " + frames.length);
	}
	
	private void fireChangeEvent(int framePos) {
		PlayerControllStatus pcs = new PlayerControllStatus(State.FramePosUpdate);
		pcs.firstFrame = pcs.lastFrame = framePos;
		for (ChangeListener changeListener : listenerList.getListeners(ChangeListener.class)) {
			changeListener.stateChanged(new ChangeEvent(pcs));
		}
	}

	private void fireChangeEvenet(PlayerControllStatus pcs)
	{
		for (ChangeListener changeListener : listenerList.getListeners(ChangeListener.class)) {
			changeListener.stateChanged(new ChangeEvent(pcs));
		}
	}

	private class AsyncLoading implements Runnable
	{
		public void run() {
			if (framecount < previewCount)
				previewCount = framecount;
			if (framecount > 0){
				for (int i = 0; i < framecount; i++)
				{
					frames[i] = parser.parseFrame();
					lastLoadedFrame = i;
					PlayerControllStatus pcs = new PlayerControllStatus(State.LoadgingProgressUpdate);
					pcs.firstFrame = pcs.lastFrame = i;
					fireChangeEvenet(pcs);
					if ((i ) % (framecount/previewCount) == 0 )
					{
						 new Thread(new AsyncPreviewMaking(i)).start();
					}
				}
				loadingComplete = true;
				parser = null;
				System.gc();
				PlayerControllStatus pcs = new PlayerControllStatus(State.AnimationLoaded);
				fireChangeEvenet(pcs);
			}
			System.out.println("Animation Loaded Succesfully!");
		} //end run
	}// ende class  AsyncLoading
	
	
	private class AsyncPreviewMaking implements Runnable
	{
		PreviewMaker previewMaker = null;
		int frame;
		public AsyncPreviewMaking(int frame) 
		{
			previewMaker = new PreviewMaker(header);
			this.frame = frame;
		}
		public void run() {
			PlayerControllStatus pcs = new PlayerControllStatus(State.PreviewUpdate);
			pcs.firstFrame = pcs.lastFrame = frame;
			ImageIcon image = new ImageIcon( previewMaker.getImage(frames[frame].points));
			previews.put(frame, image);
			pcs.imgIcon = image;
			fireChangeEvenet(pcs);
			System.out.println("Animation: setPreviewImage: Transfering Image: " +  frame);
			System.out.println("Preview Size" + previews.size());
			//previews.put(frame, image);
		}
		
	}

	public void stateChanged(ChangeEvent e) {
		int frame = (((Integer)e.getSource()).intValue());
		if (e.getSource() == Integer.class)
		{
			System.out.println("Animation: sateChanged: Setting frame to: " + frame);
			setFrame(frame);		
		}
	}


	public void addChangeListener(ChangeListener cl) {
		listenerList.add(ChangeListener.class, cl);
	}
	
	public String toString()
	{
		return "Animation: " + filename + " frame count: " + framecount ;
	}
	
	public CSMPoints getCurrentFrame()
	{
		return frames[framePos];
	}

	public CSMPoints getPoints(int index)
	{
		return frames[index];
	}

	public void fireChangeListenerUpdateEvents() {
		
		PlayerControllStatus pcs = new PlayerControllStatus(State.AnimationHeaderLoaded);
		pcs.header = header;
		fireChangeEvenet(pcs);
		
		//for (int i = 0; i < lastLoadedFrame; i++) {
			PlayerControllStatus pcs1 = new PlayerControllStatus(State.LoadgingProgressUpdate);
			pcs.firstFrame = pcs1.lastFrame = header.lastFrame;
			fireChangeEvenet(pcs1);
	//	}
		PlayerControllStatus pcs2 = new PlayerControllStatus(State.AnimationLoaded);
		fireChangeEvenet(pcs2);
		try {Thread.sleep(100);	} catch (InterruptedException e) {}
		
		Set<Integer> keys = previews.keySet();
		for (Integer i : keys) {
			PlayerControllStatus pcs3 = new PlayerControllStatus(State.PreviewUpdate);
			pcs3.firstFrame = pcs3.lastFrame = i;
			pcs3.imgIcon = previews.get(i);
			fireChangeEvenet(pcs3);
		}
	
	}
	private void writeObject(java.io.ObjectOutputStream out)
    	throws IOException
    {
		while(!loadingComplete)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			System.out.println("Animation is still in Loading State, cat save to Disk while Loading!");
		}
		out.writeObject(header);
		out.writeObject(frames);
		out.writeObject(framePos);
		out.writeObject(previews);
		
    }
	private void readObject(java.io.ObjectInputStream in)
    	throws IOException, ClassNotFoundException{
	
		header = (CSMHeader) in.readObject();
		frames = (CSMPoints[]) in.readObject();
		framePos =  (Integer) in.readObject();
		previews = (Map<Integer, ImageIcon>) in.readObject();
		skelett = new Skelett(header);
		skelett.loadFrame(frames[framePos].points);
		lastLoadedFrame = header.lastFrame -1;
		listenerList = new EventListenerList();
		//Timer setup
		playbackSpeed = 1;
		timer = new Timer(1000/20, this);
		timer.setDelay(1000/20);
		timer.setRepeats(true);
		timer.start();
		isAnimating = true;
		animStart = System.currentTimeMillis();
		
	}


	public int getFramePos() {
		return framePos;
	}


	public void setSelection(int markerMin, int markerMax) {
		markedArea[0] = markerMin; 
		markedArea[1] = markerMax; 
		
		
	}


	public void concat(Animation animation) {

		int newlength = animation.frames.length + frames.length;
		CSMPoints[] newframes = new CSMPoints[newlength];
		for(int i = 0; i < frames.length; i++)
		{
			System.out.println(" FIRTS" + frames[i].points.length);
			newframes[i] = frames[i];
		}
		for(int i = frames.length; i < newlength ; i++)
		{
			System.out.println(" Second" + animation.frames[i- frames.length].points.length);
			newframes[i] = animation.frames[i - frames.length];
		}
		frames = newframes;
		header.lastFrame = newlength;
		lastLoadedFrame = frames.length -1;
		fireChangeListenerUpdateEvents();
	}

} // End Class Animation


