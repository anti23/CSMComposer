package CustomSwingComponent;

import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import Java3D.CSMPlayer.PlayerControlls;

public  class JFilmStripSlider extends JComponent {
	
	private static final long serialVersionUID = -3796900877924928725L;
	// All positions and areas are frame number units
	
	private static final String uiClassID = "FilmStripSliderUI";
	public static boolean debug = false;
 
	FilmStripModel model = new DefaultFilmStripModel();
	public int[] visibleArea = new int[2]; 
	protected PlayerControlls playerToControll;
	
	// Controll Panel
	JPanel controlls = new JPanel();
	
	
	public JFilmStripSlider() {
		initDefaultValues();
		this.updateUI();
		if(debug) System.out.println("Instancing a FilmstripSlider: finish!");
	}
	
	
	public PlayerControlls getPlayerToControll() {
		return playerToControll;
	}

	public void setPlayerToControll(PlayerControlls playerToControll) {
		if(playerToControll != null)
		{
			this.playerToControll = playerToControll;
			playerToControll.addChangeListener(getUI());
			model.setMaxFrames(playerToControll.getFrameCount());
			model.setMaximumShownFrame(model.getMaxFrames());
			model.setMinimumShownFrame(0);
//			System.out.println("JFilmstripSlider: setPlayerToControll: model status: max Frames: " + model.getMaxFrames());
//			System.out.println("JFilmstripSlider: setPlayerToControll: model status: zoomArea: [" + model.getMinimumShownFrame()
//					+ " , " + model.getMaximumShownFrame() + "]");
		}else
			System.err.println("JFilmstripSlider: setPlayerToControll: null");
		
	}

	private void initDefaultValues() {
		if(debug) System.out.println("Instancing a FilmstripSlider: initing default values.");
		model.setMaximumShownFrame(model.getMaxFrames());
		model.setMinimumShownFrame(0);
	}
	
	public void setPreviews(Map<Integer, ImageIcon> previews)
	{
		getUI().setPreviews(previews);
	}
	
	  public void setUI(FilmStripSliderUI ui) {
	        super.setUI(ui);
	    }

	    public void updateUI() {
	        if (UIManager.get(getUIClassID()) != null) {
	            setUI((FilmStripSliderUI) UIManager.getUI(this));
	        } else {
	            setUI(new BasicFilmStripSliderUI());
	        }
	    }


		public FilmStripSliderUI getUI() {
			return (FilmStripSliderUI) ui;
		}
	    
	    public String getUIClassID() {
	        return uiClassID;
	    }
	    public void addChangeListener(ChangeListener cl)
	    {
	    	if (cl != null)
	    		model.addChangeListener(cl);
	    }
	    
	    public void removeChangeListener(ChangeListener cl)
	    {
	    	if (cl != null)
	    		model.removeChangeListener(cl);
	    }
	    
		
		public void fireEventChanged()
		{
			repaint();
		}
		
		public void setPlayerCurser(int frame)
		{
			model.setPlayerCursor(frame);
		}

		public void setMaxFrames(int maxFrames)
		{
			model.setMaxFrames(maxFrames);
			//todo delete set zoom ; 
			setZoom(0, maxFrames);
		}
		public int getMaxFrames()
		{
			return model.getMaxFrames();
		}
		
		public String toString()
		{
			StringBuffer sb = new StringBuffer();
			sb.append(model);
			
			getUI().toString();
			
			return sb.toString();
		}
		
		public void setZoom(int min, int max)
		{
			model.setMaximumShownFrame(Math.max(min, max));
			model.setMinimumShownFrame(Math.min(min, max));
		}

		/**
		 * Returns the value the Playing Curser has in Frames at the moment
		 * @return
		 */
		public int getCurrentFrameValue()
		{
			return model.getPlayerCursor();	
		}

		public void setMarkedArea(int minFrame,	int maxFrame) {
			model.setSelectedArea(minFrame, maxFrame);
			playerToControll.setMinMarker(minFrame);
			playerToControll.setMaxMarker(maxFrame);
		}
}
