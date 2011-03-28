package datastructure;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class LitQuad extends Shape3D {
  public  LitQuad(Point3f A, Point3f B, Point3f C, Point3f D) {
      this.setGeometry(createGeometry(A, B, C, D));
      this.setAppearance(createAppearance());
    }

    @SuppressWarnings("deprecation")
	Geometry createGeometry(Point3f A, Point3f B, Point3f C, Point3f D) {

      QuadArray plane = new QuadArray(4, GeometryArray.COORDINATES
          | GeometryArray.NORMALS |GeometryArray.TEXTURE_COORDINATE_2);

      plane.setCoordinate(0, A);
      plane.setCoordinate(1, B);
      plane.setCoordinate(2, C);
      plane.setCoordinate(3, D);

      
      plane.setTextureCoordinate (0, new Point2f(0.0f,0.0f));
      plane.setTextureCoordinate (1, new Point2f(1.0f,0.0f)); 
      plane.setTextureCoordinate (2, new Point2f(1.0f,1.0f));
      plane.setTextureCoordinate (3, new Point2f(0.0f,1.0f));

      Vector3f a = new Vector3f(A.x - B.x, A.y - B.y, A.z - B.z);
      Vector3f b = new Vector3f(C.x - B.x, C.y - B.y, C.z - B.z);
      Vector3f n = new Vector3f();
      n.cross(b, a);

      n.normalize();

      plane.setNormal(0, n);
      plane.setNormal(1, n);
      plane.setNormal(2, n);
      plane.setNormal(3, n);

      return plane;
    }

    Appearance createAppearance() {
      Appearance appear = new Appearance();
      Material material = new Material();
	     
      appear.setMaterial(material);
      
      return appear;
    }

  }
