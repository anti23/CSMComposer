package CSM;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import javax.vecmath.Point3f;

/**
 * File will be read completly in statemachine.
 * Parsed CSM Points are stored in points ArrayList.
 * Retrieve pints with method getPoints(int frame)
 * or getNextPoints(); 
 * @author Johannes
 */

public class CSMParser {

	CSMHeader csm_Header = null;
	public static boolean debug = false;
	public static int  statemachine_speed = 0;
	
	public enum state {START,ERROR,DOLLAR_TOKEN,KEY_VALUE,COMMENT,POINTS,READ_POINTS,END, ORDER};
	
	boolean scanSuccesfull = false;
	
	Scanner scanner = null;
	StringTokenizer st = null;
	Map<String, String> header = new HashMap<String, String>();
	String[] order = null;
	String line = null;
	String token = null; 
	int lastFrameNumber = -1 ;
	int firstFrame = -1 ;
	int maxFrames = -1 ;
	float frameRate = -1.0f;
	String scannedFilename = null;

	ArrayList<CSMPoints> points = new ArrayList<CSMPoints>(); // filled and ordered in decreasing framnumber
	private boolean noMoreLines = false;
	private CSMPoints lastLine = null;
	FileInputStream fis;
	
	public void scanFile(String fileName) throws FileNotFoundException
	{
		fis = new FileInputStream(fileName);
		 scanner = new Scanner(fis, "UTF-8");
		 scannedFilename = fileName;
		 stateMachine();
	}

	
	
	private void nextLine()
	{
		if(debug) System.out.println("nextLine");
		line = scanner.nextLine();
		while (line.length() == 0)
			line = scanner.nextLine();
		
		st = new StringTokenizer(line);
		if(st.countTokens() > 0)
		token = st.nextToken();
	}
	
	private void stateMachine()
	{	
		nextLine();
		state status = state.START;
		while(status  != state.END)
		{
			if(debug) 	System.out.println("Status :" + status + " \t\t Token : " + token);
			if(debug)  	try {	Thread.sleep(CSMParser.statemachine_speed);	
						} catch (InterruptedException e) {}
			switch (status)
			{
			case START:
				if (token.startsWith("$") )
				{
					status= state.DOLLAR_TOKEN;
				}else if(st.countTokens() == 1)
				{
					status = state.ERROR;
				}else 
					status = state.END;
				break;
				
			case DOLLAR_TOKEN:
				if (token.startsWith("$Comments") )
				{
					status = state.COMMENT;
				}else if (token.startsWith("$Order") )
				{
					status = state.ORDER;
				}else if (token.startsWith("$Points") )
				{
					status = state.POINTS;
				}else 
					status = state.KEY_VALUE;
				
				break;
				
			case KEY_VALUE:
				status = state.START;
				String key = token.substring(1);
				if (st.hasMoreElements())
				{
					String value = line.substring(token.length());
					header.put(key,value);
					if(debug)  System.out.println(header.size() + " put " + key + " " + value );
				}
				nextLine();
				break;
				
			case COMMENT:
				status = state.START;
				readComment();
				break;
				
			case ORDER:
				status = state.START;
				nextLine(); // first elemet in stringtoeknizer is stored in token				
				int cnt_tokens = 0 ;
				if ((cnt_tokens = st.countTokens() ) > 0)
				{
					order = new String[cnt_tokens + 1];
					order[0] = token;
					for (int i = 1; i < order.length; i++)
					{
						order[i] = st.nextToken();
					}
					if(debug) {
						System.out.println("ORDER:" );
						for (int i = 0; i < order.length; i++) {
							System.out.println(i + ": " + order[i]);
						}
					}
				}
				nextLine();
				break;

			case POINTS:
				status = state.POINTS;
				if(scanner.hasNextLine())
				{
//					parseFrame();
					initCSMHeader();
					status = state.END;
//					status = state.READ_POINTS;
				}
				else 
					status = state.ERROR;
				break;
				
			case READ_POINTS:
				if(scanner.hasNextLine())
					points.add(parseFrame());
				else 
					status = state.END;
				break;
				
			case ERROR:
				System.out.println("State Machiene: Error");
				status  = state.END;
				break;
			} //end switch
		}// end while 
		if(debug) System.out.println("End of StateMachine");
		scanSuccesfull = true;
	}
	
	
	/*Method is called after Statemachien scaned for meta file information
	 * 
	 */
	private void initCSMHeader() {
	
		if (firstFrame == -1)
		{
			String firstframe = header.get("FirstFrame") ;
			if (firstframe != null)
			{
				Scanner s = new Scanner(firstframe);
				s.useLocale(Locale.US);
				firstFrame = s.nextInt();
			}
		}
		if (maxFrames == -1)
		{
			String lastframe = header.get("LastFrame") ;
			if (lastframe != null)
			{
				Scanner s = new Scanner(lastframe);
				s.useLocale(Locale.US);
				maxFrames = s.nextInt();
			}
		}
		
		String rate = header.get("Rate") ;
		if (rate != null)
		{
			Scanner s = new Scanner(rate);
			s.useLocale(Locale.US);
			frameRate = s.nextFloat();
		}
	}

/**
 * At the beginning the scanner still has the $Points Line
 * @return 
 */
	private CSMPoints parseFrame() {
		
		if(noMoreLines)
		{
			return lastLine;
		}
		
		CSMPoints points;
		
		if (scannedFilename == null)
		{
			System.out.println("File has not been scande yet!");
			return null;
		}
		if (scanner.hasNextLine())
		{
			line = scanner.nextLine();
			Scanner s = new Scanner(line);
			s.useLocale(Locale.US);
			
			lastFrameNumber = s.nextInt();
			points = new CSMPoints(lastFrameNumber, this.order.length);
			
			for (int i = 0; i < order.length ; i++) 
			{
				Point3f p = new Point3f(s.nextFloat(),s.nextFloat(),s.nextFloat());
				points.add(p);
				if (debug)  System.out.println("Frame No.: " + lastFrameNumber+ " " + order[i] +" "+ p);
			}
			if (!scanner.hasNextLine())
			{
				noMoreLines = true;
				lastLine = points;
				try {
					fis.close();
				} catch (IOException e) {
					System.out.println("CSMParser: parserFrame: Trouble closing the File input Stream: " + e.getMessage());
				}
			}
			return points;
		}
		System.out.println("CSMParser: No more lines to scan!");
		return null;
	}



	// Reads Comment, state is: Token $Comment
	// Whiteline arent read over since User Readability
	private void readComment() {
		StringBuffer commentText = new StringBuffer();
		boolean end_comment_read = false;
		if (token.startsWith("$Comment") ) // check for comment token (to be sure :D )
		{
			
			while(!end_comment_read)
			{
				String c_line = scanner.nextLine();
				if (c_line.startsWith("$") )
				{
					line = c_line;
					st = new StringTokenizer(line);
					if(st.countTokens() > 0)
						token = st.nextToken();

					end_comment_read = true;
				}else // We are still in a comments line 
				{
					commentText.append(c_line);
					commentText.append("\n");
				}
			} // end while 
		}
		if(debug) System.out.println(commentText.toString());
		header.put("Comments", commentText.toString());
	}



	public static void main(String[] args) {
		CSMParser p = new CSMParser();
		try {
			p.scanFile("t-pose.csm");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(p.getNextPoints());
	}
	
	int nextUnreadFrame = 0 ;
	public CSMPoints getNextPoints()
	{
		return parseFrame();
//		if (points.size() > nextUnreadFrame)
//		{
//			return points.get(nextUnreadFrame++);
//		}else
//			return points.get(points.size()-1);
	}
	
	public CSMPoints getPoints(int frame)
	{
		if (frame < points.size() )
			return points.get(frame);
		else 
		{
			System.err.println("CSMParser: getPoints("+frame+") : Array out of bounds: points.size() = " + points.size());
			return null;
		}
	}

	public CSMHeader getHeader() {
		if (csm_Header == null)
		{
			if (scanSuccesfull)
			{
				csm_Header = new CSMHeader();
				csm_Header.Actor = header.get("Actor");
				csm_Header.Comments = header.get("Comments");
				csm_Header.date = header.get("Date");
				csm_Header.firstFrame = firstFrame;
				csm_Header.lastFrame = maxFrames;
				csm_Header.framerate = frameRate;
				csm_Header.timeInSecs = (maxFrames-firstFrame)/frameRate;
				csm_Header.order = order;
				
				if (debug) System.out.println(csm_Header);
				csm_Header.setHeaderMap(this.header);
				return csm_Header;
			}
			return null;
			
		}else 
			return csm_Header;
	}
}
