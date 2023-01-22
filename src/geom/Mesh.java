package geom;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Mesh {
    public Vector<Triangle> triangles = new Vector<>();

    public void add(Triangle t) {
        triangles.add(t);
    }

    public void loadObjFile(String fileName) {
        Vector<Vec3D> vertices = new Vector<>();
        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\Development\\Java Projects\\3DEngine\\models\\" + fileName));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    if (line.startsWith("v ")) {
                        String[] coords = line.split(" ");
                        float x = Float.parseFloat(coords[1]);
                        float y = Float.parseFloat(coords[2]);
                        float z = Float.parseFloat(coords[3]);
                        Vec3D v = new Vec3D(x, y, z);
                        vertices.add(v);
                    }
                    if (line.charAt(0) == 'f') {
                        String[] three = line.split(" ");
                        //Filter .obj format
                        if(three[1].contains("/")) {
                            var xs = three[1].split("/");
                            var ys = three[2].split("/");
                            var zs = three[3].split("/");
                            int x = Integer.parseInt((xs[0]));
                            int y = Integer.parseInt((ys[0]));
                            int z = Integer.parseInt((zs[0]));
                            triangles.add(new Triangle(new Vec3D[]{vertices.get(x-1), vertices.get(y-1),vertices.get(z-1)}));
                        }else{
                            int x = Integer.parseInt(three[1]);
                            int y = Integer.parseInt(three[2]);
                            int z = Integer.parseInt(three[3]);
                            triangles.add(new Triangle(new Vec3D[]{vertices.get(x-1), vertices.get(y-1),vertices.get(z-1)}));
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private Mesh createCube() {
        Mesh mesh = new Mesh();
        //South
        Triangle s = new Triangle(new Vec3D[]{new Vec3D(0.0f, 0.0f, 0.0f), new Vec3D(0.0f, 1.0f, 0.0f), new Vec3D(1.0f, 1.0f, 0.0f)});
        Triangle s2 = new Triangle(new Vec3D[]{new Vec3D(0.0f, 0.0f, 0.0f), new Vec3D(1.0f, 1.0f, 0.0f), new Vec3D(1.0f, 0.0f, 0.0f)});
        //EAST
        Triangle e = new Triangle(new Vec3D[]{new Vec3D(1.0f, 0.0f, 0.0f), new Vec3D(1.0f, 1.0f, 0.0f), new Vec3D(1.0f, 1.0f, 1.0f)});
        Triangle e2 = new Triangle(new Vec3D[]{new Vec3D(1.0f, 0.0f, 0.0f), new Vec3D(1.0f, 1.0f, 1.0f), new Vec3D(1.0f, 0.0f, 1.0f)});
        //NORTH
        Triangle n = new Triangle(new Vec3D[]{new Vec3D(1.0f, 0.0f, 1.0f), new Vec3D(1.0f, 1.0f, 1.0f), new Vec3D(0.0f, 1.0f, 1.0f)});
        Triangle n2 = new Triangle(new Vec3D[]{new Vec3D(1.0f, 0.0f, 1.0f), new Vec3D(0.0f, 1.0f, 1.0f), new Vec3D(0.0f, 0.0f, 1.0f)});
        //WEST
        Triangle w = new Triangle(new Vec3D[]{new Vec3D(0.0f, 0.0f, 1.0f), new Vec3D(0.0f, 1.0f, 1.0f), new Vec3D(0.0f, 1.0f, 0.0f)});
        Triangle w2 = new Triangle(new Vec3D[]{new Vec3D(0.0f, 0.0f, 1.0f), new Vec3D(0.0f, 1.0f, 0.0f), new Vec3D(0.0f, 0.0f, 0.0f)});
        //TOP
        Triangle t = new Triangle(new Vec3D[]{new Vec3D(0.0f, 1.0f, 0.0f), new Vec3D(0.0f, 1.0f, 1.0f), new Vec3D(1.0f, 1.0f, 1.0f)});
        Triangle t2 = new Triangle(new Vec3D[]{new Vec3D(0.0f, 1.0f, 0.0f), new Vec3D(1.0f, 1.0f, 1.0f), new Vec3D(1.0f, 1.0f, 0.0f)});
        //Bottom
        Triangle b = new Triangle(new Vec3D[]{new Vec3D(1.0f, 0.0f, 1.0f), new Vec3D(0.0f, 0.0f, 1.0f), new Vec3D(0.0f, 0.0f, 0.0f)});
        Triangle b2 = new Triangle(new Vec3D[]{new Vec3D(1.0f, 0.0f, 1.0f), new Vec3D(0.0f, 0.0f, 0.0f), new Vec3D(1.0f, 0.0f, 0.0f)});

        mesh.add(s);
        mesh.add(s2);
        mesh.add(e);
        mesh.add(e2);
        mesh.add(n);
        mesh.add(n2);
        mesh.add(w);
        mesh.add(w2);
        mesh.add(t);
        mesh.add(t2);
        mesh.add(b);
        mesh.add(b2);
        return mesh;
    }
}
