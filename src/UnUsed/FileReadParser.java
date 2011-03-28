package UnUsed;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import javax.vecmath.Point3f;


public class FileReadParser {
	
	public String filename = null;
	private Scanner tos;
	String fFileName = "Roman_animations.csm";
	public List<String> bezeichnungen;
	
	List<String> getOrder(Scanner scanner){

	 String s = "";
	 List<String> list = new ArrayList<String>();
			s = scanner.nextLine();
			StringTokenizer st = new StringTokenizer(s);
			while(st.hasMoreElements())
			{
				String token = st.nextToken();
		//		System.out.println(token);
			//	System.out.println(token);
				list.add(token);
			}
		System.out.println("Size "+ list.size() * 3);
		return list;
			 
 }
 
 public List<Point3f>  getNextFrameShot(Scanner scanner,int skiplines)
 {
	 
	 if (tos == null )
	 {
		 tos = scanner;
	 }
	 else scanner = tos;

	 if(!scanner.hasNext())
		 return null;
	 
	 if (scanner.hasNextLine())
		 for (int i = 0; i < skiplines; i++) {
			 if (scanner.hasNextLine())
				 	scanner.nextLine();
		}
	 
	 if (scanner.hasNextLine())
	 {
		 List<Point3f> result = new ArrayList<Point3f>();
		 
		 StringTokenizer st = new StringTokenizer(scanner.nextLine());
		 
		 // Erstes Token ist zeilen Nummer
		 if (st.hasMoreTokens())
			 System.out.println(Double.parseDouble(st.nextToken()) +" ");
		 int cnt=0;
		 float[] tmp = new float[3];
		 while(st.hasMoreTokens())
		 {
			 float  val  = Float.parseFloat(st.nextToken());
			 tmp[cnt%3] = val;

			 if (cnt%3 == 2)
			 {
				 Point3f p = new Point3f(tmp[0],tmp[2],tmp[1]);
				 p.scale(0.01f);
				 result.add(p);
				// tmp = new double[3];
			 }
			 
			 cnt++;
		 }
		 return result;
	 }
	 return null;
 }
	public void read(String fileName) throws IOException {
		this.filename = fileName;
			Scanner scanner = new Scanner(new FileInputStream(fileName), "UTF-8");
			String s = scanner.nextLine();
			String token = "";
			while (scanner.hasNextLine())
			{
				StringTokenizer st = new StringTokenizer(s);	
				if (st.hasMoreTokens()  )// s.startsWith("$")|| s.startsWith("G"))
				{
					token = st.nextToken();
				}
				if (token.startsWith("$") || token.startsWith("G"))
				{
				//	System.out.println(s);
				//	System.out.println("$ G");
					if (s.startsWith("$Order"))
					{
						bezeichnungen = getOrder(scanner);
					}	
				}
				else
				{
					tos = scanner;
					//getNextFrameShot(scanner,0);
					break;
				}
				s = scanner.nextLine();
			}
			
		  }
}
