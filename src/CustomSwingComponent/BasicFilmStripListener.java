package CustomSwingComponent;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class BasicFilmStripListener implements 	MouseListener,
												MouseMotionListener,
												KeyListener,
												MouseWheelListener
												{

	BasicFilmStripSliderUI ui;
	int x_at_mouse_pressed;
	boolean hasBeenDraged = false;
	
	public BasicFilmStripListener(BasicFilmStripSliderUI ui) {
		this.ui = ui;
	}

	//KeyboardListener
	public void keyPressed(KeyEvent e) {
		System.out.println("Key Char Presser: '"+e.getKeyChar()+"'");
		int code = e.getKeyCode();
		switch (code)
		{
		case KeyEvent.VK_F:
			ui.filmStripSlider.playerToControll.play();

		case KeyEvent.VK_SPACE:
			ui.filmStripSlider.playerToControll.play();
			break;
		case KeyEvent.VK_P:
			ui.filmStripSlider.playerToControll.pause();
			break;
			
		}
	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
	}
	// MouseListener 
	public void mouseClicked(MouseEvent e) {
		int frame = ui.translatePixelToFrame(e.getX());
		//ui.filmStripSlider.model.setMarkedFrame(frame);
//		System.out.println("Basic Listener: mosue Clicked: Pixel: " +e.getX()+" translated to frame: " + frame);
		ui.filmStripSlider.model.setSelectorCursor(frame);
		ui.filmStripSlider.model.setPlayerCursor(frame);
		ui.filmStripSlider.playerToControll.jumpto(frame);

	}

	public void mouseEntered(MouseEvent e) {
		//ui.filmStripSlider.requestFocus();
//		ui.filmStripSlider.setFocusable(true);
		
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		x_at_mouse_pressed = e.getX();
		
	}

	public void mouseReleased(MouseEvent e) {
//		System.out.println("Basic Listener: Mouse Released : " + e);
		if (hasBeenDraged)
		{
			ui.filmStripSlider.setMarkedArea(ui.translatePixelToFrame(x_at_mouse_pressed), 
					ui.translatePixelToFrame(e.getX()));
			hasBeenDraged = false;
		}
	}

	public void mouseDragged(MouseEvent e) {
		//System.out.println("Mouse Dragged : " + e);
		hasBeenDraged = true;
		ui.filmStripSlider.model.setSelectedArea(ui.translatePixelToFrame(x_at_mouse_pressed), 
								ui.translatePixelToFrame(e.getX()));
		ui.filmStripSlider.fireEventChanged();
		int frame = ui.translatePixelToFrame(e.getX());
		ui.filmStripSlider.model.setSelectorCursor(frame);
	}

	// MouseMoitionListener
	public void mouseMoved(MouseEvent me) {
		//Seting Selector curser
		int frame = ui.translatePixelToFrame(me.getX());
		ui.filmStripSlider.model.setSelectorCursor(frame);
		ui.filmStripSlider.fireEventChanged();
		
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
	//	System.out.println("Basic Listener: Mousewheel ScrollAmount: " + e.getScrollAmount());;
	//	System.out.println("Basic Listener: Mousewheel WheelRotation: " + e.getWheelRotation());;
		
		int max =ui.filmStripSlider.model.getMaximumShownFrame() ;
		int min =ui.filmStripSlider.model.getMinimumShownFrame() ;
		int delta = e.getWheelRotation() * e.getScrollAmount();
		
		if (max+delta > min - delta && (max+delta) + (min - delta) != 0)
		{
			ui.filmStripSlider.model.setMaximumShownFrame(max+delta);
			ui.filmStripSlider.model.setMinimumShownFrame(min-delta);
		}
			
		
		ui.filmStripSlider.fireEventChanged();
	}

}
