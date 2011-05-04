package Java3D.SkeletMaker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import CSM.CSMHeader;

public class SkeletConnections implements Serializable{

	private static final long serialVersionUID = 6453290728060012118L;
	public List<String> connectlist = new ArrayList<String>(20);
	CSMHeader header;
	
	public void setHeader(CSMHeader header)
	{
		this.header = header;
	}
	
	public int get(int index)
	{
		if(header != null)
			return header.getPos(connectlist.get(index));
		return -1;
	}
	
	public int size(){
		return connectlist.size();
	}
	
	public void connect(String a,String b)
	   {
		int first =findPair(a, b) ; 
		if (first >= 0 )
		{
			System.out.println("Connection Allready existing first : " + first );
			return;
		}
		if (a !=null && b!= null)
		{
		   connectlist.add(a);
		   connectlist.add(b);
		   
		}else
			System.out.println("Skelet Connection: connect: no Connection Established between: " + a + " and " + b + " !");
		
		if (header.getPos(a) < 0)
			System.out.println("SkeletonConnections : Point " + a + " is not in Loaded Header");
		if (header.getPos(b) < 0)
			System.out.println("SkeletonConnections : Point " + b + " is not in Loaded Header");
	   }
	
	
	/**
	 *  returns the first index nuber of the pair to search. retrun -1 if pair does not exist in list.
	 * @param a
	 * @param b
	 * @return
	 */
	int findPair(String a, String b)
	{
		for (int i = 0; i < connectlist.size()/2; i++) 
		{
			if (connectlist.get(i*2).compareToIgnoreCase(a)== 0)
			{
				if (connectlist.get(i*2+1).compareToIgnoreCase(b) == 0)
				{
					return i*2;
				}
			}
			if (connectlist.get(i*2).compareToIgnoreCase(b)== 0)
			{
				if (connectlist.get(i*2+1).compareToIgnoreCase(a) == 0)
				{
					return i*2;
				}
			}
		}
		return -1;
	}
	/**
	 * retruns 
	 * @param a fisrt marker point
	 * @param b secon marker point 
	 * @return true if Disconnection was succesful, taht means if
	 * there was a connection anyway. if false there was nothing 
	 * to disconect and no redraw has to be maid.
	 */
	public boolean disconnect(String a, String b)
	{
		//connectionCount is half the size of the conectList
		int first = findPair(a, b);
		if (first > -1)
		{
			connectlist.remove(first+1);
			connectlist.remove(first);
			System.out.println("SkeletonConnections: disconnecting: " + a + " and " + b);
			return true;
		}
		return false;
	}
	
	/* Initing Default Bone Connections
	*/
	public void initConnectList()
	{
		//head
		connect("LFHD","LBHD"); 
		connect("LFHD","RFHD");
	
		connect("RBHD","LBHD");
		connect("RFHD","LBHD");
		connect("RFHD","RBHD");
		
		//right arm
		connect("RUPA","RSHO");
		connect("RUPA","RELB");
		connect("RELB","RFRM");
		connect("RFRM","RFIN");
		connect("RWRA","RWRB");
		
		//left arm
		connect("LUPA","LSHO");
		connect("LUPA","LELB");
		connect("LELB","LFRM");
		connect("LFRM","LFIN");
		connect("LWRA","LWRB");
				// right leg
		connect("RPel","RTHI");
		connect("RTHI","RKNE");
		
		connect("RKNE","RSHN");
		connect("RSHN","RANK");
		connect("RANK","RHEL");
		connect("RTOE","RMT5");
		connect("RANK","RHEE");
		connect("RMT5","RHEE");
		connect("RTOE","RHEE");
		
		//fuss right
		connect("RHEL","RTOE");
		connect("RHEL","RMT5");
		
		// left leg
		connect("LPel","LTHI");
		connect("LTHI","LKNE");
		
		connect("LKNE","LSHN");
		connect("LSHN","LANK");
		connect("LANK","LHEL");
		connect("LANK","LHEE");
		connect("LTOE","LMT5");
		connect("LMT5","LHEE");
		connect("LTOE","LHEE");
		
		//fuss left
		connect("LHEL","LTOE");
		connect("LHEL","LMT5");
	
		// sholders
		connect("RSHO","LSHO");
	
		// shoulder hump connection
		connect("LSHO","LPel");
		connect("RSHO","RPel");
		
		//  guertel
		connect("RBWT","LBWT");
		connect("RPel","RBWT");
		connect("RPel","RFWT");
		connect("LPel","LBWT");
		connect("LPel","LFWT");
		connect("RFWT","LFWT");
		
		// ruecken
		connect("LBWT","T10");
		connect("RBWT","T10");
		connect("C7","T10");
		connect("C7","RSHO");
		connect("C7","LSHO");
 
		// bauch
		connect("LFWT","STRN");
		connect("RFWT","STRN");
		connect("CLAV","STRN");
		connect("CLAV","RSHO");
		connect("CLAV","LSHO");

		
	}
	/**
	 * @deprecated
	 * Ths method adds a half of a connection pair threrfor it allwas should be called twice in a row.
	 * Connection_cout will not be incremented.
	 * 
	 * Just for Compatibility with older Skelet structure.
	 */
	public void add(String a) {
		connectlist.add(a);
	}
	
	
}
