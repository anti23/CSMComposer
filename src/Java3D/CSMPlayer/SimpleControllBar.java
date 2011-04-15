package Java3D.CSMPlayer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import CustomSwingComponent.JFilmStripSlider;
import Java3D.CSMPlayer.PlayerControlls.playingDirection;
import UnUsed.CSMSimpleRenderdPlayer;

public class SimpleControllBar extends JPanel implements ChangeListener{
	private static final long serialVersionUID = -7189403877971718824L;

	public PlayerControlls getPlayerToControll() {
		return playerToControll;
	}


	public void setPlayerToControll(PlayerControlls playerToControll) {
		this.playerToControll = playerToControll;
		initListeners();
	}

	
	PlayerControlls playerToControll;
	JPanel buttonPanel = new JPanel();
	JSlider slider = new JSlider(JSlider.HORIZONTAL);
	JButton play = new JButton(">"); 
	JButton pause = new JButton("||"); 
	JButton stop = new JButton("[]");
	JSlider slider_speed = new JSlider(JSlider.VERTICAL);
	
	public SimpleControllBar() {
		initComponents();
	}
	
	
	void initListeners()
	{
		play.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				playingDirection p = playingDirection.FWD;
				playerToControll.changePlayingDirection(p);
				playerToControll.play();
			}
		});
		
		pause.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				playerToControll.pause();
			}
		});
		
		stop.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				playerToControll.jumpto(0);
				playerToControll.pause();
			}
		});
		
		
		
		slider.addChangeListener(this);
		
		
		slider_speed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				if (ce.getSource() == slider_speed)
				{
					JSlider sl = (JSlider) ce.getSource(); 
					int speedValue = sl.getValue();
					if (speedValue < 0 )
						playerToControll.changePlayingDirection(playingDirection.BKWD);
					else 
						playerToControll.changePlayingDirection(playingDirection.FWD);
					
					playerToControll.changeSpeed(Math.abs(speedValue));
					System.out.println("SimpleControllbar: Adjusting Speed to : " + speedValue);
				}
			}
		});
		
		playerToControll.addChangeListener(this);
		
	}
	
	void initComponents()
	{
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(play);
		buttonPanel.add(pause);
		buttonPanel.add(stop);
		
		this.setLayout(new BorderLayout());
		
		this.add(slider,BorderLayout.CENTER);
		this.add(buttonPanel,BorderLayout.EAST);
		
		
		slider_speed.setMinimum(-10);
		slider_speed.setMaximum(10);
		slider_speed.setValue(1);
		slider_speed.setPreferredSize(new Dimension(50, 175));
		
		this.add(slider_speed,BorderLayout.WEST);
		

		
	}
	
	
	public static void main(String[] args) {
		JFrame f = new  JFrame("Testing Player interface");
		SimpleControllBar scb1 = new SimpleControllBar();
		SimpleControllBar scb2 = new SimpleControllBar();
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		
		f.setLayout(new BorderLayout());
		f.setSize(new Dimension(500,500));
		sliderPanel.add(scb1);
		sliderPanel.add(scb2);
		f.add(sliderPanel,BorderLayout.SOUTH);
		
		CSMSimpleRenderdPlayer player = new CSMSimpleRenderdPlayer();
		
		JFilmStripSlider jfs = new JFilmStripSlider();
		jfs.setPlayerToControll(player);
		jfs.setMaxFrames(100);
		sliderPanel.add(jfs);
		
		scb1.setPlayerToControll(player);
		scb2.setPlayerToControll(player);
		f.add(player,BorderLayout.CENTER);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
	}


	int stateChangeCnt = 0;
	
	public void stateChanged(ChangeEvent arg0) {
		//System.out.println("Simple ControllBar: Change Event: " + arg0.getSource().getClass() + " Cnt: " + stateChangeCnt++);

		if (arg0.getSource().getClass() == JSlider.class)
		{
			
			slider.removeChangeListener(this);
			playerToControll.jumpto(((JSlider)arg0.getSource()).getValue());
			slider.addChangeListener(this);
		}
		
		
		if (arg0.getSource().getClass() == PlayerControllStatus.class)
		{
			PlayerControllStatus pcs = (PlayerControllStatus) arg0.getSource();
			
			switch (pcs.status) {
			case FramePosUpdate:
				
				slider.removeChangeListener(this);
				slider.setValue(pcs.firstFrame);
				slider.addChangeListener(this);
				break;
			case PlaybackSpeedChanged:
				slider_speed.removeChangeListener(this);
				slider_speed.setValue((int) pcs.speed);
				slider_speed.addChangeListener(this);
				break;
			default:
				break;
			}
		}
	}
}
