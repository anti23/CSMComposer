package datastructure;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.Scanner;

import Misc.StaticTools;

public class Config implements Serializable{
	// --------- Static Constants --------- 
	public static int previewCount = 7;
	public static float skeletScale = 0.01f;
	
	
	public static String floorTexturePath = "floor.jpg";
	public static String sphericalBackGroundTexturePath = "sphereBG.jpg";
	
	// Where to look for CSM files
	String workingDirecttory = null;
	// Where to store Screenshots
	String screeShotDirectory = null;
	int screenShotCount = 0;
	// Where to find and Store Skelet Files
	String skeletDirectory = null;
	// Where to Save created Animations
	String SaveDirectory = null;
	
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
	
}
