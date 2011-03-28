package CustomSwingComponent;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class BasicFilmStripLayoutManager  implements LayoutManager{

	public void addLayoutComponent(String arg0, Component arg1) {
	}
	
	public BasicFilmStripLayoutManager() {
	}

	public void layoutContainer(Container arg0) {
	}

	public Dimension minimumLayoutSize(Container c) {
		return preferredLayoutSize(c);
	}

	public Dimension preferredLayoutSize(Container c) {
		if (c != null )
		{
			Dimension d =new Dimension(c.getWidth(),97);
			//if (JFilmStripSlider.debug) System.out.println(this.getClass().toString() + " : " + d);
			return d;
		}
		return new Dimension(500,97);
		
	}
	
	public void removeLayoutComponent(Component c) {
	}

}
