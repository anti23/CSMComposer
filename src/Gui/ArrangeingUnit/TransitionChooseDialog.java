package Gui.ArrangeingUnit;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JSpinner;

public class TransitionChooseDialog extends JDialog {

	private static final long serialVersionUID = 6871034431026077229L;

	public Transition trans = new NoTransiton();

	List<Transition> array;
	int index;
	Point p;
	public TransitionChooseDialog(Point pos, List<Transition> array, int index,Point p ) {
		
		super();
		this.array = array;
		this.index = index;
		this.p = p;
		setTitle("Transition Chooser Dialog");
		setSize(230,100);
		pos.x -=getSize().width/2;
		pos.y -=getSize().height/2;
		setLocation(pos);
		init();
	}


	private void init() {
		setLayout(new BorderLayout());
		//Transitions
		final JComboBox transitions = new JComboBox(Transition.transitions.keySet().toArray());
		add(transitions,BorderLayout.CENTER);
		//spinner
		final JSpinner spinner = new JSpinner();
		spinner.setValue(120);
		add(spinner,BorderLayout.EAST);
		// Button
		JButton button = new JButton("Set Transition");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Class<? extends Transition> c =  Transition.transitions.get((String)(transitions.getSelectedItem()));
				try {
					trans =  c.newInstance() ;
					trans.setFrameCount((Integer) spinner.getValue());
					trans.bounds.x = p.x;
					trans.bounds.y = p.y;
					System.out.println(trans.getTranstionName());
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				array.set(index, trans);
				dispose();
			}
		});
		add(button,BorderLayout.SOUTH);
	}
}
