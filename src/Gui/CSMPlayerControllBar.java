package Gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import CustomSwingComponent.JFilmStripSlider;
import Gui.ArrangeingUnit.ArrangerPane;
import Java3D.Java3DCSMPlayer;
import Java3D.CSMPlayer.PlayerControlls;
/**
 * Diese Klass verbidet das Custom Swing Applet JFilmStrip und einige Buttons
 * die die Restlichen Funktionen des PlayerControlls Interfaces ansprechen
 * @author Johannes
 *
 */
public class CSMPlayerControllBar extends JPanel
{
	private static final long serialVersionUID = 8246585468543236639L;
	JFilmStripSlider filmStripSlider;
	JPanel controlls = new JPanel();
	PlayerControlls player;
	ArrangerPane arrangerPane;
	
	public CSMPlayerControllBar() {
		initComponents();
	}
	
	public void setArrangerPane(ArrangerPane ap)
	{
		this.arrangerPane = ap;
	}

	public void setPlayerToControll(PlayerControlls player)
	{
		this.player = player;
		filmStripSlider.setPlayerToControll(player);
		initControllPanel();
	}
	
	private void initComponents() 
	{
		setSize(new Dimension(500,97));
		setLayout(new BorderLayout());
		filmStripSlider = new JFilmStripSlider();
		add(filmStripSlider,BorderLayout.CENTER);
	}


	private void initControllPanel() {
		controlls.setLayout(new GridBagLayout());
		JButton play = new JButton(">");
		JButton pause = new JButton("||");
		JButton faster = new JButton(">>");
		JButton slower = new JButton("<<");
		JButton markMin = new JButton("[<");
		JButton markMax = new JButton(">]");
		JButton playMarked = new JButton("[>]");
		
		JButton copySelection = new JButton("copy");

		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.play();
			}
		});
		faster.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float newspeed = player.getSpeed() * 1.2f;
				player.changeSpeed(newspeed);
			}
		});
		slower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float newspeed = player.getSpeed() *0.8f;
				player.changeSpeed(newspeed);
			}
		});
		
		
		copySelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				arrangerPane.add(player.getAnimation().getSubSequentAnimation(player.getMinMarker(), player.getMaxMarker()));
			}
		});
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.pause();
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		controlls.add(play,c);
		c.gridy = 1;
		controlls.add(pause,c);
		c.gridy = 2;
		controlls.add(slower,c);
		c.gridy = 2;
		c.gridx = 1;
		controlls.add(faster,c);
		
		c.gridy = 3;
		c.gridx = 0;
		controlls.add(markMin,c);
		
		c.gridy = 3;
		c.gridx = 1;
		controlls.add(markMax,c);
		
		c.gridy = 1;
		c.gridx = 1;
		controlls.add(playMarked,c);
		

		controlls.add(copySelection);
		add(controlls,BorderLayout.EAST);
	}

}
