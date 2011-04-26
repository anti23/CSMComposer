package CustomSwingComponent;

import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

public abstract class FilmStripSliderUI extends ComponentUI implements ChangeListener{
	

	public abstract int translateFrameToPixel(int frame);
	public abstract int translatePixelToFrame(int pixel);
	protected abstract Map<Integer, ImageIcon> getPreviews();
	protected abstract void setPreviews(Map<Integer, ImageIcon> previews);
}
