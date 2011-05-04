package datastructure;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.media.j3d.BranchGroup;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.vecmath.Point3f;

import CSM.CSMHeader;
import CSM.CSMParser;
import CSM.CSMPoints;
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
	public String filename;
	CSMParser parser;
	Calendar cal = Calendar.getInstance();
	public CSMHeader header;
	//CSMPoints currentFrame;
	public CSMPoints[] frames;
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
	
	// Player Controll Attributes
	public float playbackSpeed = 1.0f;
	int selectedArea[] = new int[2];
	boolean playSelection = false;
	
	public Animation() {
		header = CSMHeader.defaultHeader();
		frames = new CSMPoints[1];
		frames[0] = CSMPoints.defaultTPose();
		skelett = new Skelett(header);
		skelett.loadFrame(frames[0].points);
	}
	public Animation(CSMHeader header) {
		this.header = header.clone();
		filename = header.filename;
		framecount = header.lastFrame+1  - header.firstFrame;
		frames = new CSMPoints[framecount];
		frames[0] = CSMPoints.defaultTPose();
		skelett = new Skelett(header);
		loadingComplete = true;
		skelett.loadFrame(frames[0].points);
	}
	public Animation(CSMHeader header, CSMPoints[] frames) {
		this.header = header.clone();
		framecount = header.lastFrame+1 -header.firstFrame;
		this.frames = frames;
		if (framecount != frames.length)
			System.out.println("Animaiton: Construcor (CSMheader,Point3f): header array framecount mismatch, adapting header");
		header.firstFrame = 1;
		header.lastFrame = frames.length;
		framecount = header.lastFrame+1 - header.firstFrame;
		lastLoadedFrame = framecount;

		//frames[0] = CSMPoints.defaultTPose();
		skelett = new Skelett(header);
		//skelett.loadFrame(frames[0].points);
	}

	
	public Animation(String filename) {

		this.filename = filename;
		try {
			loadFromFile();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	
	public Skelett getSkelett()
	{
		return this.skelett;
	}
	
	public CSMPoints getLastFrame()
	{
		return frames[frames.length-1];
	}
	
	public CSMPoints getFirstFrame()
	{
		if(frames != null && frames.length > 0)
			return frames[0];
		else 
		{
			System.out.println("Animaiton : No frist frame to get here!");
			return CSMPoints.defaultTPose();
		}
	}
	
	// Constructs and loads CSM datastructure
	private void loadFromFile() throws IOException
	{
		if (filename != null)
		{
			File f = new File(filename);
			if (!f.isFile())
				throw new IOException("File not found "+ f );
		}else 
		{
			System.out.println("Animation: Loadfrom File: but no Filename!");
			return;
		}
		parser = new CSMParser();
		parser.scanFile(filename);
		header = parser.getHeader();
		
		PlayerControllStatus pcs = new PlayerControllStatus(State.AnimationHeaderLoaded);
		pcs.header = header;
		fireChangeEvenet(pcs);

		framecount = header.lastFrame+1 -header.firstFrame;
		frames = new CSMPoints[framecount];
		//frames[0] = parser.getNextPoints();
		
		
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
		loadFromFile();
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
		
//		System.out.println("Animation: play : timer :" + timer + 
//				" \n \t animStart " + animStart + 
//				" \n \t framepos " + framePos  +
//				" \n \t framerate " + header.framerate  
//				/*" \n \t listener List: " + listenerList*/
//				);
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
		if(selectedArea[0] > selectedArea[1])
		{
			int bigger = selectedArea[0];
			selectedArea[0] = selectedArea[1];
			selectedArea[1] = bigger;
		}
		if (selectedArea[0] > header.firstFrame && selectedArea[1] < header.lastFrame)
			return true;
		else
			System.out.println("Animation: check Selection marked Area out of bound: " +selectedArea );
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
			delta =  System.currentTimeMillis() - animStart ;
			animStart = System.currentTimeMillis();
			if (!isAnimating) // pausing, no need for killing timer
			{
				//timer.setRepeats(false);
				//timer.stop();
				return;
			}
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
				if (framePos < selectedArea[0])
					framePos = selectedArea[0];
				if (framePos > selectedArea[1])
					framePos = selectedArea[0];
					
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
			if (frame >= frames.length )
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
	//	System.out.println("Animaiton: load Frame: id " + framePos + " value " + frames[framePos]);
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
		int previewCount = Config.previewCount;
		public void run() {
			if (framecount < Config.previewCount)
				previewCount = framecount;
			if (framecount > 0){
				for (int i = 0; i < framecount; i++)
				{
					frames[i] = parser.getNextPoints();
					lastLoadedFrame = i+1;
					PlayerControllStatus pcs = new PlayerControllStatus(State.LoadgingProgressUpdate);
					pcs.firstFrame = pcs.lastFrame = i;
					fireChangeEvenet(pcs);
					if ( (framecount/previewCount) > 0 && (i) % (framecount/previewCount) == 0 )
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
			System.out.println("FrameCount" + framecount);
			System.out.println("Frames length " + frames.length);
		} //end run
	}// ende class  AsyncLoading
	
	
	private class AsyncPreviewMaking implements Runnable
	{
		PreviewMaker previewMaker = null;
		int frame;
		public AsyncPreviewMaking(int frame) 
		{ // Alles Sehr schoen programmiert
			previewMaker = new PreviewMaker(header); 
//			previewMaker = PreviewMaker.getInstance(header);
			this.frame = frame;
		}
		public void run() {
			PlayerControllStatus pcs = new PlayerControllStatus(State.PreviewUpdate);
			pcs.firstFrame = pcs.lastFrame = frame;
			ImageIcon image = new ImageIcon( previewMaker.getImage(frames[frame].points));
			previews.put(frame, image);
			pcs.imgIcon = image;
			fireChangeEvenet(pcs);
//			System.out.println("AsyncPreviewMaking: setPreviewImage: Transfering Image: " +  frame);
//			System.out.println("PAsyncPreviewMaking: review Size" + previews.size());
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
		return "Animation: Filename:" + filename + " frame count: " + framecount ;
	}
	
	public CSMPoints getCurrentFrame()
	{
		return frames[framePos];
	}

	public CSMPoints getPoints(int index)
	{
		if (index > frames.length-1)
		{
			System.out.println("Animation: getPoints(index= "+index+"): bigger than frames.length: " + frames.length);
			System.out.println("Animation: getPoints(index= "+index+"): giving a modulo frame.length instead");
		}
		return frames[index % frames.length];
		
	}

	public void fireChangeListenerUpdateEvents() {
		
		PlayerControllStatus pcs = new PlayerControllStatus(State.AnimationHeaderLoaded);
		pcs.header = header;
		fireChangeEvenet(pcs);
		
		// Hopefully Fully Loaded! :( Atleast the Filmstrip status will show that 
		//for (int i = 0; i < lastLoadedFrame; i++) {
			 pcs = new PlayerControllStatus(State.LoadgingProgressUpdate);
			pcs.firstFrame = pcs.lastFrame = header.lastFrame;
			fireChangeEvenet(pcs);
	//	}
			// Marker Positions!
			pcs = new PlayerControllStatus(State.SelectedAreaUpdate);
			pcs.firstFrame = selectedArea[0];
			pcs.lastFrame = selectedArea[1];
			fireChangeEvenet(pcs);
			
			
		 pcs = new PlayerControllStatus(State.AnimationLoaded);
		fireChangeEvenet(pcs);
		try {Thread.sleep(100);	} catch (InterruptedException e) {}
		
		Set<Integer> keys = previews.keySet();
		for (Integer i : keys) {
			pcs = new PlayerControllStatus(State.PreviewUpdate);
			pcs.firstFrame = pcs.lastFrame = i;
			pcs.imgIcon = previews.get(i);
			fireChangeEvenet(pcs);
		}
	
	}
	private void writeObject(java.io.ObjectOutputStream out)
    	throws IOException
    {
		System.out.println("Animation IS beeing serialized!!!!");
		while(!loadingComplete)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			System.out.println("Animation: write Object: still in Loading State, can't save to Disk while Loading!");
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
//		timer = new Timer(1000/20, this);
//		timer.setDelay(1000/20);
//		timer.setRepeats(true);
//		timer.start();
		isAnimating = false;
		animStart = System.currentTimeMillis();
		selectedArea = new int[2];
		loadingComplete = true;
	}


	public int getFramePos() {
		return framePos;
	}
	public boolean isLoadingComplete()
	{
		return loadingComplete;
	}

	public void setSelection(int markerMin, int markerMax) {
		selectedArea[0] = markerMin; 
		selectedArea[1] = markerMax; 
		
		
	}

	public void deleteSubsequence(int firstFrame, int lastFrame)
	{
		System.out.println("Animaiton: deleteSubsequence() ");
		System.out.println("Animaiton: deleteSubsequence: firstFrame : " + firstFrame);
		System.out.println("Animaiton: deleteSubsequence: lastFrame  : " + lastFrame);
		if (firstFrame < 0 && firstFrame > lastLoadedFrame && lastFrame < firstFrame && lastFrame >lastLoadedFrame)
		{
			System.out.println("Animaition: deleteSubsequence: First or last frame to big or to small.");
			return;
		}
		int deleteCount = lastFrame - firstFrame;
		if (deleteCount < 0)
			return;
		System.out.println("Animaiton: deleteSubsequence: DeleteCount: " + deleteCount);
		CSMPoints[] newframes = new CSMPoints[frames.length - deleteCount];
		System.out.println("Animaiton: deleteSubsequence: old Frames length : " + frames.length);
		System.out.println("Animaiton: deleteSubsequence: new Frames length : " + newframes.length);
		for(int i = 0; i < firstFrame; i++)
		{
			newframes[i] = frames[i];
		}
		for(int i = lastFrame; i < frames.length; i++)
		{
			newframes[(i -lastFrame) + firstFrame] = frames[i];
		}
		frames = newframes;
		lastLoadedFrame = frames.length;
		header.lastFrame = frames.length;
		this.framePos = this.framePos % lastLoadedFrame;
		header.getHeaderMap().put("lastframe", Integer.toString(frames.length) );
		

		fireChangeListenerUpdateEvents();
	}

	public Animation getSubSequentAnimation(int firstFrame, int lastFrame)
	{
		Animation anim = new Animation(header.clone());
	
		if (firstFrame == lastFrame)
		{
			anim.frames = new CSMPoints[1];
			anim.frames[0] = frames[firstFrame];
			anim.header.firstFrame = 0;
			anim.header.lastFrame = 0;
			anim.filename += " frame " + firstFrame;
			anim.lastLoadedFrame = 1;
			anim.loadingComplete = true;
			return anim;
		}
		
		anim.filename += " frame " + firstFrame + " to " + lastFrame;
		anim.framecount = lastFrame-firstFrame;
		anim.lastLoadedFrame = anim.framecount;

		anim.header.firstFrame = 0;
		anim.header.lastFrame = anim.framecount;
		//anim.header.filename = anim.filename;
		// Fill Previews
		anim.previews = new HashMap<Integer, ImageIcon>();
		Set<Integer> set =  previews.keySet();
		for (Integer i : set) {
			if (i > firstFrame && i < lastFrame)
				anim.previews.put(i - firstFrame, previews.get(i));
		}
		
		
		anim.frames = new CSMPoints[lastFrame-firstFrame];
		for (int i = firstFrame; i < lastFrame; i++) {
			anim.frames[i- firstFrame] = frames[i];
		}
	
		// If no Previewpic existing, generate one!
		if (anim.previews.size() == 0 && anim.frames.length > 0)
		{
			PreviewMaker pm = new PreviewMaker(header);
			anim.previews.put(0,new ImageIcon(pm.getImage(anim.frames[0].points)));
		}
		
		//seting reading Controll flags, this animaiton is made 100% out of ram
		anim.loadingComplete = true;
		return anim;
	}

	public void concat(Animation animation) {
		
		int newlength = animation.frames.length + frames.length;
		CSMPoints[] newframes = new CSMPoints[newlength];
		for(int i = 0; i < frames.length; i++)
		{
			newframes[i] = frames[i];
		}
		for(int i = frames.length; i < newlength ; i++)
		{
			newframes[i] = animation.frames[i - frames.length];
		}
		// Update Previews
		Map<Integer,ImageIcon> previews = new HashMap<Integer, ImageIcon>();
		Set<Integer> old_keys = this.previews.keySet();
		for (Integer i : old_keys) {
			previews.put(i, this.previews.get(i));
		}
		int offset = this.frames.length;
		// if to cnocat animation has previews add the to new preview array with frame offset
		if(animation.previews != null)
		{
			Set<Integer> new_keys = animation.previews.keySet();
			for (Integer i : new_keys) {
				previews.put(i + offset, animation.previews.get(i));
			}
			
		}
		
		frames = newframes;
		this.previews = previews;
		header.lastFrame = newlength;
		lastLoadedFrame = frames.length -1;
		fireChangeListenerUpdateEvents();
	}
	
	
	public void reverse()
	{
		CSMPoints[] newFrames = new CSMPoints[frames.length];
		for (int i = 0; i < frames.length; i++) {
			newFrames[i] = frames[frames.length-i-1];
		}
		frames = newFrames;
	}
	
	// es wird jeder zweite frame aus dem Arra geloescht
	public void doubleSpeed()
	{
		CSMPoints[] newFrames = new CSMPoints[frames.length/2 ];
		for (int i = 0; i < frames.length/2; i++) {
			newFrames[i] = frames[i*2];
		}
		frames = newFrames;
		header.firstFrame = 1;
		header.lastFrame = frames.length;
		lastLoadedFrame = header.lastFrame -1;

		Map<Integer,ImageIcon> newpreviews = new HashMap<Integer, ImageIcon>();
		for (Integer i : previews.keySet()) {
			newpreviews.put(i/2, previews.get(i));
		}
		previews = newpreviews;
	}
	//animation wird doppelt so gross, zwischen dene fran wird ein weiterer interpoliert 
	public void halfSpeed()
	{
		CSMPoints[] newFrames = new CSMPoints[frames.length*2 -1];
		for (int i = 0; i < frames.length-1; i++) 
		{
			newFrames[i*2] = frames[i];
			
			Point3f[] interP = new Point3f[frames[i].points.length];
			for (int j = 0; j < interP.length; j++) {
				float x = (frames[i].points[j].x +frames[i+1].points[j].x ) *0.5f;
				float y = (frames[i].points[j].y +frames[i+1].points[j].y ) *0.5f;
				float z = (frames[i].points[j].z +frames[i+1].points[j].z ) *0.5f;
				interP[j]= new Point3f(x,y,z);
				
			}
			CSMPoints interpoliert = new CSMPoints(interP);
			newFrames[i*2+1] = interpoliert;
		}
		//letzer Frame ist original
		newFrames[newFrames.length-1] = frames[frames.length-1];
		
		frames = newFrames;
		header.firstFrame = 1;
		header.lastFrame = frames.length;
		lastLoadedFrame = header.lastFrame -1;
		
		
		// update previews 
		Map<Integer,ImageIcon> newpreviews = new HashMap<Integer, ImageIcon>();
		for (Integer i : previews.keySet()) {
			newpreviews.put(i*2, previews.get(i));
		}
		previews = newpreviews;
	}
	
	public void removeChangeListener(ChangeListener player) {
		listenerList.remove(ChangeListener.class, player);
	}

} // End Class Animation


