package Java3D.CSMPlayer;

import java.awt.Event;
import java.awt.Image;

import javax.swing.ImageIcon;

import CSM.CSMHeader;
/**
 * Diese Klasse dient als Kommunikations Token zwischen dem CMSPlayer und den Controller elementen, die sich als 
 * ChangeListeners in die ListenerList des Players eingetragen haben. <br>
 * Ein PCS Objekt muss mit einem State Erzeugt werden. Abhaengig von diesem Status wird vor versendung 
 * an die Listener entsprechende Information eingesetzt. <br>
 * Z.B. Wird ein PCS mit PreviewUpdate erzeugt, wird auf der gegenseite ein gesetztes Image img erwartet. <br>
 * Z.B. wird PCS mit AnimationHeaderLoaded erzeugt, so erwartet die ControllBar neue werte fuer first und last Frame. <br>
 * Bei State FramePosUpdate, wird firstFrame = Lasframe gesetzt auf den aktuell gespielten Frame. <br>
 * @author Johannes
 *
 */
public class PlayerControllStatus  {

	private static final long serialVersionUID = 1540528934051844984L;
	public enum State {FramePosUpdate, AnimationHeaderLoaded,LoadgingProgressUpdate, PlaybackSpeedChanged,AnimationLoaded,PreviewUpdate, SelectedAreaUpdate};
	
	public State status; 
	public ImageIcon imgIcon;
	public float speed = -1;
	public int firstFrame = -1;
	public int lastFrame = -1;
	public CSMHeader header;
	
	public PlayerControllStatus(State s) {
		status = s;
	//	System.out.println(status);
	}
	
}
