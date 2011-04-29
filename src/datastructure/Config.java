package datastructure;

import java.io.File;
import java.io.FileFilter;
import java.util.Scanner;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import Misc.StaticTools;

public class Config  {
	// --------- Static Constants --------- 
	public static int previewCount = 7;
	public static float skeletScale = 0.01f;
	
	public static String floorTexturePath = "floor.jpg";
	public static String sphericalBackGroundTexturePath = "sphereBG.jpg";
	
	// Where to look for CSM files
	String workingDirecttory = null;
	// Where to store Screenshots
	public static String screeShotDirectory = "./";
	public static String screeShotFileName = "shot.jpg";
	private int screenShotCount = 0;
	// Where to find and Store Skelet Files
	String skeletDirectory = null;
	// Where to Save created Animations
	String SaveDirectory = null;

	public String version = "0.98";
	
	
	
	public void calcScreenShotCount()
	{
		if(screeShotDirectory != null)
		{
			File ssd = new File(screeShotDirectory);
			if(ssd.isDirectory())
			{
				int maxSSC = 0;
				for (File f : ssd.listFiles(StaticTools.fileFilterIsFile("jpg") ) )
				{
					String s = f.getName().toString();
					Scanner scanner = new Scanner(s);
					int n = scanner.nextInt();
					if (n > maxSSC)
						maxSSC = n;
				}
				screenShotCount = maxSSC;
			}
		}
	}
	
	
	File configFile;
	public void loadConfig() {
		File programDir = new File(".");
		
		for (File  s : programDir.listFiles(new FileFilter() {
			public boolean accept(File arg0) {
				return arg0.toString().endsWith("CSMCConf.xml");
			}
		})) {			
			if(s.isFile())
				configFile = s;
		}

	}
	
	public void saveConfig()
	{
		
	}
		

}
