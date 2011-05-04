package CSM;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.vecmath.Point3f;

import datastructure.Animation;

public class CSMWriter {

	CSMHeader header;
	Animation animation;
	String fileName;
	StringBuffer sb = new StringBuffer();
	public CSMWriter(Animation a, String fileName) {
		this.animation = a;
		this.header = a.header;
		this.fileName = fileName;
		writeOutHeader();
	//	writeOutPoints();
	}
	
	private void writeOutHeader()
	{
		Date now = Calendar.getInstance().getTime();
		SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf_time = new SimpleDateFormat("hh:mm:ss");

		sb.append("# -------CSMComposerGenerated ---------" + "\n");
		
		sb.append("$Filename \t" + fileName + "\n");
		sb.append("$Date \t" + sdf_date.format(now) + "\n");
		sb.append("$Time \t" + sdf_time.format(now)+ "\n");
		sb.append("$Actor \t" + header.Actor + "\n");
		//comments
		sb.append("$Comments \t" + "\n");
		sb.append(header.Comments + "\n");
		sb.append("$FirstFrame \t" + header.firstFrame + "\n");
		sb.append("$LastFrame \t" + header.lastFrame + "\n");
		sb.append("$Rate \t" + header.framerate + "\n");
		//order
		sb.append("$Order "+ "\n");
		for (String  marker : header.order) {
			sb.append(marker + " ");
		}
		sb.append( "\n");

		System.out.println("CSMWriter: " + sb);
	}
	
	private void writeOutPoints(BufferedWriter bw) throws IOException
	{
		//points
		bw.write("$Points" + "\n");
		//Wait for Loading finished first
		while ( !animation.isLoadingComplete())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			
			for (int i = 0; i < animation.frames.length; i++) {
				bw.write(i+header.firstFrame + " ");
				Point3f[] row = animation.getPoints(i).points;
				for (Point3f point3f : row) {
					bw.write( point3f.x + " " + point3f.y + " " + point3f.z + " ");
				}
				bw.write( "\n");
			}

	}
	
	public void writeOutCSM()
	{
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			bw.write(sb.toString());
			writeOutPoints(bw);
		}
		catch (IOException e) {
			System.out.println("CSMWriter: writeOutFile: error with buffered Writing");
		}finally
		{
			try {
				bw.flush();
				bw.close();
				fw.flush();
				fw.close();
			} catch (IOException e) {
			}
		}
	}
	public static void main(String[] args) {
		
		Animation a = new Animation("t-pose.csm");
		CSMWriter w = new CSMWriter(a, "writtenAnimaiton.csm");
		w.writeOutCSM();
	}
	
}
