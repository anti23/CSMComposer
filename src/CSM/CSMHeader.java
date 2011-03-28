package CSM;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


public class CSMHeader implements Serializable{
	private static final long serialVersionUID = 8799617713932939669L;

	public static String defaultOder = " LFHD RFHD LBHD RBHD C7 T10 CLAV STRN RBAC LSHO LUPA LELB LFRM LWRA LWRB LFIN RSHO RUPA RELB RFRM RWRA RWRB RFIN LFWT RFWT LPel RPel LBWT RBWT LTHI LKNE LSHN LANK LHEL LTOE LMT5 RTHI RKNE RSHN RANK RHEL RTOE RMT5 ";
	
	public Map<String, String> header = new HashMap<String, String>();
	
	public String filename;
	public String date;
	public String Time;
	public String Actor;
	public String Comments;
	public int firstFrame;
	public int lastFrame;
	public float framerate;
	public float timeInSecs;
	public String[] order;
	
	
	public int getPos(String csmOrderNameTag)
	{
		if (order != null && csmOrderNameTag != null)
		{
			for (int i = 0; i < order.length; i++) {
				if (order[i].contains(csmOrderNameTag))
				{
					return i;
				}
			}
		}
		return -1;
	}
	
	@Override
		public String toString() {
		
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < order.length; i++) {
				sb.append(order[i] + "\n");
			}
			return sb.toString();
		}
	
	public static CSMHeader defaultHeader()
	{
		CSMHeader h = new CSMHeader();
		h.firstFrame = 1;
		h.lastFrame = 1;
		h.framerate = 0;
		h.Actor = "Nobody";
		h.Comments = null;
		h.timeInSecs = 0 ;
		h.filename = null;

		StringTokenizer st = new StringTokenizer(defaultOder);
		
		h.header.put("Keyyyyayay", "Valualala");
		
		h.order = new String[st.countTokens()];
		int cnt = 0 ;
		while (st.hasMoreTokens()) {
			h.order[cnt++] = st.nextToken();
		}
			
		
		return h;
	}
}
