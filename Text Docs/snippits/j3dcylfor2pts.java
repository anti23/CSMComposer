/* ChartJ3DLink.java
 * Link between two nodes in a Java3D chart
 * Date: Wed Feb 27 00:29:23 2002
 * Author: Sandy Dunlop (sandy@sorn.net)
 * Copyright (C) 2002. Explicitly NO WARRANTY.
 * For license information, see: www.sandyd.org.uk/docs/licenses/gpl.php
 */

import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

public class ChartJ3DLink{
  public final float POLE_WIDTH = 0.025f;
  public ChartJ3DLink(){
  }
  public ChartJ3DLink(float x1,float y1,float z1,float x2,float y2,float z2,Group group){
    //ChartJ3DLink starting at (x1,y1,z2) goint to (x2,y2,z2)
    System.out.println("\nChartJ3DLink from ("+x1+","+y1+","+z1+") to ("+x2+","+y2+","+z2+")");

    TransformGroup tg = new TransformGroup();
    Transform3D transform = new Transform3D();

    //Work out center...
    float cx = (x1+x2) / 2;
    float cy = (y1+y2) / 2;
    float cz = (z1+z2) / 2;
    Vector3f vector = new Vector3f(cx, cy, cz);
    System.out.println("Center: cx="+cx+" cy="+cy+" cz="+cz);
    transform.setTranslation(vector);

    //Work out the length
    double dx = (double) (x2-x1);
    double dy = (double) (y2-y1);
    double dz = (double) (z2-z1);
    System.out.println("Distance: dx="+dx+" dy="+dy+" dz="+dz);
    float s = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
    System.out.println("Length: s="+s);

    //Work out rotation on Z-axis...
    double rotZ=0;
    if (dx==0){
      rotZ=0;
    }else if (dy==0){
      //CHANGE THIS...
      rotZ=Math.PI/2;//if this is negative, remove the -rotX below
      if (dx<0){
        System.out.println("Reversing rotZ (y=0)");
        rotZ = -rotZ;
      }
    }else if (dx!=0){
      rotZ=-Math.atan(Math.abs(dx)/Math.abs(dy));
      if (dy<0){
        System.out.println("Altering rotZ (y<0)");
        if (dx>0){
          rotZ = Math.PI+rotZ;
        }else{
          rotZ = Math.PI*3-rotZ;
        }
      }else{
        if (dx>0){
          System.out.println("Altering rotZ (y>0 x>0)");
          rotZ=-rotZ;
        }
      }
    }
    rotZ=-rotZ;
    double degrees = rotZ * (180d/Math.PI);
    System.out.println("Z-Rot: rad="+ rotZ+" deg="+degrees);

    //Work out rotation on X axis...
    double rotX = Math.atan ( Math.abs(z2-cz) / (0.5*Math.sqrt(dx*dx+dy*dy)) );
    rotX=-rotX;//If this goes, alter the CHANGE THIS above
    if (z2-cz>0){
      //CHANGE THIS...
      System.out.println("blah");
      rotX = -rotX;
    }
    //rotX=Math.PI-rotX;
    degrees = rotX * (180d/Math.PI);
    System.out.println("X-Rot: rad="+ rotX+" deg="+degrees);
    //rotX=0;

    Transform3D rotationZ = new Transform3D();
    Transform3D rotationX = new Transform3D();
    rotationZ.rotZ(rotZ);
    rotationX.rotX(rotX);

    transform.mul(rotationZ);
    transform.mul(rotationX);

    Appearance ap = new Appearance();
    Color3f black = new Color3f (0.0f, 0.0f, 0.0f);
    Color3f white = new Color3f (1.0f, 1.0f, 1.0f);
    Color3f gray  = new Color3f (0.4f, 0.4f, 0.4f);
    Color3f objColor = new Color3f (0.0f, 0.4f, 0.2f);
    ap.setMaterial(new Material(black,gray,objColor, white, 50.0f));

    tg.setTransform(transform);
    Cylinder cylinder = new Cylinder(POLE_WIDTH,s,ap);
    tg.addChild(cylinder);
    group.addChild(tg);
  }
}

