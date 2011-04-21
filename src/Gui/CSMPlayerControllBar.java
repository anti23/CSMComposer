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

import datastructure.Animation;

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
		JButton stop = new JButton("[]");
		JButton faster = new JButton(">>");
		JButton slower = new JButton("<<");
		JButton markAll = new JButton("Mark All");
		JButton markMin = new JButton("[<");
		JButton markMax = new JButton(">]");
		JButton playSelection = new JButton("[>]");
		JButton copySelection = new JButton("copy");

		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.play();
				System.out.println("CSM PlayerControllBar: play : player :" + player);
			}
		});
		faster.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float newspeed = player.getSpeed() * 1.2f;
				System.out.println(player.getSpeed());
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
				int minFrame = player.getMinMarker();
				int maxFrame = player.getMaxMarker();
				Animation selected = player.getAnimation().getSubSequentAnimation(minFrame,maxFrame) ; 
				arrangerPane.add(selected);
				player.loadAnimation(selected);
			}
		});
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.pause();
			}
		});
		
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.stop();
			}
		});
		
		playSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.togglePlaySelection();
			}
		});
		
		markMin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.setMinMarker(player.getAnimation().getFramePos());
			}
		});
		markMax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.setMaxMarker(player.getAnimation().getFramePos());
			}
		});
		
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		controlls.add(play,c);
		c.gridx = 1;
		controlls.add(pause,c);
		c.gridx = 2;
		controlls.add(stop,c);
		
		c.gridy = 1;
		c.gridx = 0;
		controlls.add(slower,c);
		c.gridx = 1;
		controlls.add(faster,c);
		
		c.gridy = 2;
		c.gridx = 0;
		controlls.add(markMin,c);
		c.gridx = 1;
		controlls.add(markMax,c);
		c.gridx = 2;
		controlls.add(playSelection,c);
		
		c.gridy = 3;
		c.gridx = 2;
		controlls.add(copySelection,c);
		
		add(controlls,BorderLayout.EAST);
	}

}
