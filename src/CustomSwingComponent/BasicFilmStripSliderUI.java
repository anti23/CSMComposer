package CustomSwingComponent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.dnd.DragGestureListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.ComponentUI;

import datastructure.Config;
import Java3D.CSMPlayer.PlayerControllStatus;
import Java3D.CSMPlayer.PlayerControllStatus.State;

public class BasicFilmStripSliderUI extends FilmStripSliderUI {
	
	protected JFilmStripSlider filmStripSlider;
	
	protected JLabel[] controlPointLabels;
	
	private BasicFilmStripListener basicListener;
	protected MouseListener mouseListener;
	protected MouseMotionListener mouseMotionListener;
	protected MouseWheelListener mouseWheelListener;
	protected KeyListener keyListener;
	protected DragGestureListener dragGestureListener;
	
	//Needs Updating when Window size changed
	Rectangle bounds;
	Map<Integer, ImageIcon> previews = new HashMap<Integer, ImageIcon>();
	
	//zoomGlobals
	int zoomFrameStart = 0;
	int zoomFrameEnd = 0;
	int frame_range = 0; // number of visible frames
	float marker_Pixel_distance = 0;
	float marker_value = 0 ;
	
	
	//Loading Progress Bar
	int lastLoadedFrame = 0;
	
	
	public void installUI(JComponent c) {
		this.filmStripSlider = (JFilmStripSlider) c;
		installDefaults();
		installComponents();
		installListeners();

		c.setLayout(createLayoutManager());
		c.setBorder(new EmptyBorder(1, 1, 1, 1));
	}

	protected LayoutManager createLayoutManager() {
		return new BasicFilmStripLayoutManager();
	}


	public void installComponents() {
		//int framecount = this.filmStripSlider.model.getMaxFrames();
	}

	
	public void installListeners() {

		this.basicListener = new BasicFilmStripListener(this);
		this.mouseListener = this.basicListener;
		filmStripSlider.addMouseListener(this.mouseListener);
		this.mouseMotionListener = this.basicListener;
		filmStripSlider.addMouseMotionListener(mouseMotionListener);
		this.keyListener = this.basicListener;
		filmStripSlider.addKeyListener(keyListener);
		this.mouseWheelListener = this.basicListener;
		filmStripSlider.addMouseWheelListener(mouseWheelListener);
		
		if(JFilmStripSlider.debug)System.out.println("UI: listeners Installed");
	}

	public void installDefaults() {
		bounds = new Rectangle();
		filmStripSlider.setZoom(0, 250);
	}
	
	
	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
		this.bounds = c.getBounds();
		int[] minMax = filmStripSlider.model.getSelectedArea();
		int min = Math.min(minMax[0], minMax[1]);
		int max = Math.max(minMax[0], minMax[1]);
		this.paintFilmRoll(g);
		this.paintAreaMarer(g, min, max);
		this.paintTimeBarFrames(g);
		this.paintPreviewImages(g);
		
		//Allways at last, so above everything else!
		
		this.paintCurserPosition(g,filmStripSlider.model.getSelectorCursor());
		this.paintCurserPosition(g,filmStripSlider.model.getPlayerCursor());
		
	}

	private void paintPreviewImages(Graphics g) {
		if(previews != null)
			if(previews.size() > 0)
		{
			int default_image_length = 100;
			int default_image_height = 70;
			// itterates at tenth stepping between min and max
			int visible_pixels = bounds.width;
			int stepps = (visible_pixels)/ (int)(default_image_length * 0.5f);
			if (stepps == 0 )
				stepps =1;
			
			float step_delta = visible_pixels/(float)stepps;
			
			for (float i = 0; i <= visible_pixels ; i+=step_delta) 
			{
				int pixel = (int)i;
				int closest = getClosestImage(translatePixelToFrame(pixel));
				if ( previews.get(closest) != null) // should work everytime!
				{
					//		System.out.println("Frame i: " + i + " Closest:" + closest);
					g.drawImage(previews.get(closest).getImage(), translateFrameToPixel(closest), 20, default_image_length, default_image_height, null);
				}
				
			}
			/* ursprung
			int stepping = (frame_range/ Config.previewCount);
			for (int i = zoomFrameStart; i <= zoomFrameEnd; i+=stepping) 
			{
				if ( previews.get(i) != null)
				{
			//		System.out.println("Frame i: " + i + " Closest:" + closest);
					g.drawImage(previews.get(i).getImage(), translateFrameToPixel(i), 20, default_image_length, default_image_height, null);
				}
				
			}
			
			 * 
			for (int i = 0; i < maxImgs; i++) 
			{
				System.out.println("image: " + i + " should show frame:" + translatePixelToFrame(i*imgSteps) + " at Pixel " + i*imgSteps);
				int closest = getClosestImage(translatePixelToFrame(i*imgSteps));
				System.out.println("Choosing: " + closest);
				g.drawImage(previews.get(closest), translateFrameToPixel(closest), 20, 100, 70, null);
				System.out.println("Showing: " +closest + " at Pixel " + translateFrameToPixel(closest));
				//i=closest+1; // ganz schlechter style!!!!!!!!
				
			}
			 */
		}
	}
	
	/*
	 * Returns the best possible PrevieImage available
	 */
	int getClosestImage(int frame)
	{
		if (previews.size() <= 0)
			return 0;
		
		int[] pFrames = new int[previews.size()];
		int pCnt = 0;
		for (Integer i : previews.keySet()) {
			pFrames[pCnt] = i;
			pCnt++;
		}
		Arrays.sort(pFrames);
		
		int cnt = 0;
		int lower = pFrames[cnt];  //beide low
		int higher = pFrames[cnt];
		while(cnt < pFrames.length -1 && higher < frame)
		{
			lower = higher;
			cnt++;
			higher = pFrames[cnt];
			
		}
		int result = 0;
		if (frame - lower >= higher - frame)
			result =  higher;
		else 
			result = lower;
		
		return result;
	}

	private void paintCurserPosition(Graphics g, int frame) {
		Color old = g.getColor();
		g.setColor(Color.GRAY);
		
		int x = translateFrameToPixel(frame);
		// grau 
		g.drawLine( x+1,30,x+1,80 );
		g.drawLine( x-1,30,x-1,80 );
		
		
		// dreieck oben
		int[] xs = {x+10, x,x-10};
		int[] ys = {  20,30,20};
		g.fillPolygon(new Polygon(xs, ys, 3));
		// dreieck unten
		int[] xs2 = {x+10, x,x-10};
		int[] ys2 = {  90,80,90};
		g.fillPolygon(new Polygon(xs2, ys2, 3));

		//schwarz
		g.setColor(Color.black);
		g.drawLine( x,30,x,80 );
		//pfeil oben
		g.drawLine( x+10,20,x,30 );
		g.drawLine( x-10,20,x,30 );
		//pfeil unten
		g.drawLine( x+10,90,x,80 );
		g.drawLine( x-10,90,x,80 );
		
		String frameName = "Frame: "+ (zoomFrameStart + frame) ;
		if (x < bounds.width - 7 * frameName.length())
			g.drawString(frameName, x +10, 90);
		else 
			g.drawString(frameName, x - 10 - 7 * frameName.length(), 90);
			
		g.setColor(old);
	}
	
	private void paintAreaMarer(Graphics g, int minFrame, int maxFrame)
	{
		
		int minX = translateFrameToPixel(minFrame);
		int maxX = translateFrameToPixel(maxFrame);
		Color old = g.getColor();
		int[] xs3 = {minX+2,minX+2, maxX-2,maxX-2};
		int[] ys3 = {  2,		14,		14,		2 };
		Polygon p = null; p = new Polygon(xs3, ys3, 4);
		g.setColor(Color.GRAY);
		g.fillPolygon(p);
		
		// dreieck gruen min
		int x = minX;
		int[] xs = {x+10, x,x-10};
		int[] ys = {  2,14,2 };
		 p =new Polygon(xs, ys, 3);
		g.setColor(Color.GREEN);
		g.fillPolygon(p);
		g.setColor(Color.BLACK);
		g.drawPolygon(p);

		// dreieck rot max
		x = maxX;
		int[] xs2 = {x+10, x,x-10};
		int[] ys2 = {  2,14,2 };
		p = null; p = new Polygon(xs2, ys2, 3);
		g.setColor(Color.RED);
		g.fillPolygon(p);
		g.setColor(Color.BLACK);
		g.drawPolygon(p);

		g.setColor(old);
	}

	private void paintTimeBarFrames(Graphics g) {
		int border_offset = 2; // px
		int singel_frame_marker_heigth = 3; // px
		int five_frames_marker_height = 6; // px
		int ten_frames_marker_height = 10; // px
		int max_heigth = border_offset + ten_frames_marker_height;
		
		Rectangle r= (Rectangle) bounds.clone();
		r.x = border_offset; r.y = border_offset;
		g.setColor(Color.blue.brighter());
		g.drawRect( r.x -border_offset/2,  r.y - border_offset/2, r.width-border_offset, r.height-border_offset);
		g.drawRect( r.x,  r.y, r.width-2*border_offset, r.height-2*border_offset);
		
		g.setColor(Color.black);
		
		zoomFrameStart = filmStripSlider.model.getMinimumShownFrame();
		zoomFrameEnd = filmStripSlider.model.getMaximumShownFrame();
		frame_range = (zoomFrameEnd-zoomFrameStart);
		int width = bounds.width;
		int maxMarkers = width/3; // we need 2 pixels for one marker (so markers can be seperated with white line)
		if (maxMarkers < 0) maxMarkers = 1;
		
	
		
		if (frame_range < maxMarkers)
		{
			// Marker Abstand muss wachsen
			marker_Pixel_distance = width/(1.0f*frame_range);
			marker_value = 1; // 1 frame pro marker
		} else
		{
			// marker abst wir minimal = 2
			// marker bedeutung muss wachsen
			marker_Pixel_distance = 3;
			marker_value = frame_range / (1.0f *maxMarkers);
			
		}

		//itterate over pixel
		int markerCnt = 0;
		for (float x = 0 ; x < width ; x+=marker_Pixel_distance ) {
			
			int i = Math.round(x);
				//g.drawLine(i-1, border_offset, i-1, border_offset+ten_frames_marker_height);
			if (markerCnt % 10 == 0 )
			{
				g.drawLine(i, border_offset, i, border_offset+ten_frames_marker_height);
				g.drawString(""+ (int)(zoomFrameStart+markerCnt*marker_value), i, ten_frames_marker_height*3);
			}
			else if (markerCnt % 5 == 0 )
				g.drawLine(i, border_offset, i, border_offset+five_frames_marker_height);
			else 
			{
				g.drawLine(i, border_offset, i, border_offset+singel_frame_marker_heigth);
			}
			markerCnt++;
				
		}
	}
	
	private void paintFilmRoll(Graphics g)
	{
		int border_y_offset = 14;
		int border_x_offset = 2;
		int white_Square_baseline_lengh = 4;
		int film_roll_border_width = 1;
		int preview_spaec_height = 70;
		Rectangle r= (Rectangle) bounds.clone();		
		r.x = border_x_offset; r.y = border_y_offset;
		Rectangle oben = (Rectangle) r.clone();
		oben.height = 2*film_roll_border_width + white_Square_baseline_lengh;
		
		
		Color old = g.getColor();
		g.setColor(Color.black);
		
		g.fillRect(oben.x, oben.y,  translateFrameToPixel(lastLoadedFrame), oben.height);
		// unten = oben um  2*film_roll_border_width + white_Square_baseline_lengh +preview_spaec_height versetzt
		oben.y = border_y_offset + 2*film_roll_border_width + white_Square_baseline_lengh +preview_spaec_height;
		g.fillRect(oben.x, oben.y,  translateFrameToPixel(lastLoadedFrame), oben.height);
		
		Rectangle whiteHole = new Rectangle(white_Square_baseline_lengh,white_Square_baseline_lengh);
		whiteHole.y = border_y_offset + film_roll_border_width;
		g.setColor(Color.white);
		for (int i = 2* border_x_offset; i < r.width; i+=2*white_Square_baseline_lengh) {
			whiteHole.x = i;
			g.fillRect(whiteHole.x, whiteHole.y, whiteHole.width, whiteHole.height);
			g.fillRect(whiteHole.x, whiteHole.y+preview_spaec_height+ 2*film_roll_border_width + white_Square_baseline_lengh, whiteHole.width, whiteHole.height);
		}

		g.setColor(old);
	}
	
	public int translatePixelToFrame(int pixel)
	{
		int frameCnt = zoomFrameEnd - zoomFrameStart;
		if (frameCnt == 0) frameCnt = 1;
		int width = bounds.width;
		
		int frame = (pixel * frameCnt) / width;
		return frame;
		//return (pixel/pixel_frame_ratio);
	}
	public int translateFrameToPixel(int frame)
	{
		
		int frameCnt = zoomFrameEnd - zoomFrameStart;
		if (frameCnt == 0) frameCnt = 1;
		int width = bounds.width;
		int pixel = (frame * width) / frameCnt;
		return pixel;
		//return  (frame * pixel_frame_ratio) ;
	}

	
	protected Map<Integer, ImageIcon> getPreviews() {
		return previews;
	}
	
	protected void setPreviews(Map<Integer, ImageIcon> previews) {
		this.previews = previews;
	}
	
	
	public static ComponentUI createUI(JComponent c) {
		return new BasicFilmStripSliderUI();
	}

	public void stateChanged(ChangeEvent e) {
	//	System.out.println("Basic UI: stateChanged: " + e.getSource());
		if (e.getSource().getClass() == Integer.class)
		{
			//int frame = ((Integer)e.getSource()).intValue();
			//filmStripSlider.setPlayerCurser( frame );
		}
		
		if(e.getSource().getClass() == PlayerControllStatus.class)
		{
			PlayerControllStatus pcs = (PlayerControllStatus) e.getSource();
			State s = pcs.status;
			switch (s)
			{
			case FramePosUpdate:
					filmStripSlider.setPlayerCurser(pcs.firstFrame);
				break;
			case AnimationHeaderLoaded:
				int framecount = pcs.header.lastFrame - pcs.header.firstFrame;
					filmStripSlider.model.setMaxFrames(framecount);
					previews.clear();
					lastLoadedFrame  = 0;
				break;
			case LoadgingProgressUpdate:
				lastLoadedFrame = pcs.lastFrame;
				break;
			case PreviewUpdate:
				if(pcs.imgIcon != null && pcs.firstFrame == pcs.lastFrame)
				{
				//	System.out.println("Basic UI: recieved Preview Update frame :" + pcs.firstFrame +" " + e);
					previews.put(pcs.firstFrame, pcs.imgIcon);
				}
				break;
				
			case SelectedAreaUpdate :
				filmStripSlider.model.setSelectedArea(pcs.firstFrame,pcs.lastFrame);
				break ;
			}
		}
		filmStripSlider.fireEventChanged();
	}
	
	
}
