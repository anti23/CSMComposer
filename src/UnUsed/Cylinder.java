package UnUsed;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Cylinder extends Shape3D {
	
	
	float length = 4;
	float radius;
	int sides = 8;
	Point3f a;
	Point3f b;
	
  public  Cylinder(Point3f A, Point3f B, float radius) {
	  
	  a = A;
	  b = B;
	  this.radius = radius;
      updateGeometry(A, B, radius);
      this.setAppearance(createAppearance());
    }
  
  public void updateGeometry(Point3f A, Point3f B, float radius)
  {
	  Vector3f a = new Vector3f(A);
	  Vector3f b = new Vector3f(B);
	  //Normale for Foot Plane From a to b
	  b.sub(a);
	  b.normalize();
	  //Frist Orthogonal Plane Vector
		// TODO if ( not axial )
	  float x3 = 1;
		float x2 = (-(x3*b.x)- b.x*x3*b.z)/
		(b.y*(b.x+1));
		float x1 = -(x2*b.y+x3*b.z)/(b.x);
		Vector3f c = new Vector3f(x1,x2,x3);
		//second Orthogonal Vector 
		Vector3f d = new Vector3f();
		d.cross(new Vector3f(c), new Vector3f(b));
		
	  
		Geometry g = createGeometry(c , d, radius);
  }

	private Geometry createGeometry(Vector3f x, Vector3f y, float radius) {

    	TriangleArray head = new TriangleArray(3*sides,
    			GeometryArray.COORDINATES|GeometryArray.NORMALS);

    	TriangleArray foot = new TriangleArray(3*sides,
    			GeometryArray.COORDINATES|GeometryArray.NORMALS);
      QuadArray plane = new QuadArray(4 * sides, GeometryArray.COORDINATES
          | GeometryArray.NORMALS );

      
      int ctr = 0 ;
      for (float i = (float) -Math.PI; i < Math.PI && ctr < sides * 4; i += 2* Math.PI / sides)
      {
    	  /*
    	  Vector3f x1 = (Vector3f) x.clone();
    	  Vector3f x2 = (Vector3f) x.clone();
    	  Vector3f y1 = (Vector3f) y.clone();
    	  Vector3f y2 = (Vector3f) y.clone();
    	  
    	  Point3f p = new Point3f( (float)Math.sin(i)* radius, (float)Math.cos(i)* radius,length);
    	  Point3f q = new Point3f( (float)Math.sin(i)* radius, (float)Math.cos(i)* radius,0);
    	  x1.scale(p.x);
    	  y1.scale(p.y);

    	  Point3f p1 = new Point3f(x1.x + y1.x , x1.y+y1.y , x1.z+y1.z);
    	  x2.scale(q.x);
    	  y2.scale(q.y);
    	  Point3f q1 = new Point3f(x2.x + y2.x , x2.y+y2.y , x2.z+y2.z);
    	  

    	  Vector3f x3 = (Vector3f) x.clone();
    	  Vector3f x4 = (Vector3f) x.clone();
    	  Vector3f y3 = (Vector3f) y.clone();
    	  Vector3f y4 = (Vector3f) y.clone();
    	  
    	  float next = i + 2* (float)Math.PI / sides;
    	  Point3f r = new Point3f( (float)Math.sin(next)* radius, (float)Math.cos(next) * radius,length);
    	  Point3f s = new Point3f( (float)Math.sin(next)* radius, (float)Math.cos(next)* radius,0);

    	  x3.scale(r.x);
    	  y3.scale(r.y);
    	  
    	  Point3f r1 = new Point3f(x3.x + y3.x , x3.y+y3.y , x3.z+y3.y);
    	  x4.scale(s.x);
    	  y4.scale(s.y);
    	  Point3f s1 = new Point3f(x4.x + y4.x , x4.y+y4.y , x4.z+y4.y);
    	  
    	  plane.setCoordinate(ctr +0, r1);
    	  plane.setCoordinate(ctr +1, s1);
    	  plane.setCoordinate(ctr +2, q1);
    	  plane.setCoordinate(ctr +3, p1);
    	  
    	  Vector3f n = new Vector3f((float)Math.sin(i)* (radius+1),
    			  (float)Math.cos(i)* (radius+1),0);
    	  n.normalize();
    	  
    	  plane.setNormal(ctr +0, n  );
    	  plane.setNormal(ctr +1, n ) ;
    	  n = new Vector3f((float)Math.sin(next)* (radius+1),
    			  (float)Math.cos(next)* (radius+1),0);
    	  n.normalize();
    	  plane.setNormal(ctr +2, n ) ;
    	  plane.setNormal(ctr +3, n ) ;
          
    	  
    	  //head 
    	  Point3f vHead = new Point3f(a.x,a.y,a.z);
    	  head.setCoordinate(ctr/4 * 3 + 0, vHead);
    	  head.setCoordinate(ctr/4 * 3 + 1, r1);
    	  head.setCoordinate(ctr/4 * 3 + 2, p1);

    	  //head 
    	  Point3f vFoot = new Point3f(b.x,b.y,b.z);
    	  foot.setCoordinate(ctr/4 * 3 + 0, vFoot);
    	  foot.setCoordinate(ctr/4 * 3 + 1, q1);
    	  foot.setCoordinate(ctr/4 * 3 + 2, s1);
    	  
    	  
    	   */
    	  
			Vector3f bb = (Vector3f) x.clone();
			Vector3f cc = (Vector3f) y.clone();
			bb.normalize();
			cc.normalize();
			
			Vector3f dd = (Vector3f) x.clone();
			Vector3f ee = (Vector3f) y.clone();
			dd.normalize();
			ee.normalize();
			
			
			Vector3f bbb = (Vector3f) x.clone();
			Vector3f ccc = (Vector3f) y.clone();
			bbb.normalize();
			ccc.normalize();
			
			Vector3f ddd = (Vector3f) x.clone();
			Vector3f eee = (Vector3f) y.clone();
			ddd.normalize();
			eee.normalize();
			
			
			bb.scale((float) Math.sin(i)  *radius);
			cc.scale((float) Math.cos(i)  *radius);

			Point3f p1 = new Point3f(bb.x+cc.x+ a.x,
									 bb.y+cc.y+ a.y,
									 bb.z+cc.z+ a.z);

			dd.scale((float) Math.sin(i +  Math.PI/16) *radius );
			ee.scale((float) Math.cos(i +  Math.PI/16) *radius);
			Point3f p2 = new Point3f(dd.x+ee.x + a.x,
								     dd.y+ee.y + a.y,
									 dd.z+ee.z + a.z);
			
			this.addGeometry(line(p1,p2));
			
			bbb.scale((float) Math.sin(i) *radius);
			ccc.scale((float) Math.cos(i) *radius);

			Point3f p3 = new Point3f(bbb.x+ccc.x+ b.x,
									 bbb.y+ccc.y+ b.y,
									 bbb.z+ccc.z+ b.z);
			
			ddd.scale((float) Math.sin(i +  Math.PI/16) *radius);
			eee.scale((float) Math.cos(i +  Math.PI/16) *radius);
			
			Point3f p4 = new Point3f(ddd.x+eee.x + b.x,
									 ddd.y+eee.y + b.y,
									 ddd.z+eee.z + b.z);
			
			this.addGeometry(line(p3,p4));
			this.addGeometry(line(p1,p3));
			this.addGeometry(line(p2,p4));
			
    	  ctr+=4;
      }
    //  this.addGeometry(head); 
    //  this.addGeometry(foot); 
      
      /*
       * 
      plane.setTextureCoordinate (0, new Point2f(0.0f,0.0f));
      plane.setTextureCoordinate (1, new Point2f(1.0f,0.0f)); 
      plane.setTextureCoordinate (2, new Point2f(1.0f,1.0f));
      plane.setTextureCoordinate (3, new Point2f(0.0f,1.0f));
       */

   //   this.addGeometry(plane);
      return plane;
    }
    
    public static Geometry line(Point3f a,Point3f b)
	{
		 // Plain line
	    Point3f[] plaPts = new Point3f[2];
	    plaPts[0] = new Point3f((float)a.x,
					    		(float)a.y,
					    		(float)a.z);
	    plaPts[1] = new Point3f((float)b.x,
					    		(float)b.y,
					    		(float)b.z);
	    
	    

	    LineArray pla = new LineArray(2, LineArray.COORDINATES);
	    pla.setCoordinates(0, plaPts);
	    return pla;
	}

    Appearance createAppearance() {
      Appearance appear = new Appearance();
      Material material = new Material();
	     
      appear.setMaterial(material);
      
      return appear;
    }

	public void calculateGeometry() {
		// TODO Auto-generated method stub
		
	}
  }
