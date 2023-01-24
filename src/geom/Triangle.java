package geom;

import java.awt.Color;

public class Triangle {
    private Color color;
    public Vec3D[] tri;

    public Triangle(Vec3D[] tri) {
        this.tri = tri;
    }

    public Triangle() {
        this(new Vec3D[]{new Vec3D(), new Vec3D(), new Vec3D()});
    }

    public Triangle applyMatrix(Mat4x4 mat) {
        Vec3D[] tri = this.tri;
        return new Triangle(new Vec3D[]{
                Mat4x4.multiplyVector(tri[0], mat),
                Mat4x4.multiplyVector(tri[1], mat),
                Mat4x4.multiplyVector(tri[2], mat),
        });
    }

    public static int clipAgainstPlane(Vec3D planeP, Vec3D planeN, Triangle inTri, Triangle outTri, Triangle outTri2) {
        planeN.normalize();
        //shortest distance from point to plane d(E,P); HNF
        int inside = 0;
        int outside = 0;
        Vec3D[] insidePoints = new Vec3D[3];
        Vec3D[] outsidePoints = new Vec3D[3];

        float d0 = shortestDistanceToPlane(planeP, planeN, inTri.tri[0]);
        float d1 = shortestDistanceToPlane(planeP, planeN, inTri.tri[1]);
        float d2 = shortestDistanceToPlane(planeP, planeN, inTri.tri[2]);

        if (d0 >= 0) {
            insidePoints[inside++] = inTri.tri[0];
        } else {
            outsidePoints[outside] = inTri.tri[0];
            outside++;
        }
        if (d1 >= 0) {
            insidePoints[inside++] = inTri.tri[1];
        } else {
            outsidePoints[outside] = inTri.tri[1];
            outside++;
        }
        if (d2 >= 0) {
            insidePoints[inside++] = inTri.tri[2];
        } else {
            outsidePoints[outside] = inTri.tri[2];
            outside++;
        }
        if (inside == 0) {
            return 0;
        }
        if (inside == 3) {
            System.arraycopy(inTri.tri, 0, outTri.tri, 0, outTri.tri.length);
            //outTri = inTri;
            return 1;
        }
        if (inside == 1) {
            outTri.setColor(Color.GREEN.darker().darker().darker());
            outTri.tri[0] = insidePoints[0];
            outTri.tri[1] = intersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0]);
            outTri.tri[2] = intersectPlane(planeP, planeN, insidePoints[0], outsidePoints[1]);
            return 1;
        }
        if (inside == 2) {
            outTri.setColor(Color.RED);
            outTri.tri[0] = insidePoints[0];
            outTri.tri[1] = insidePoints[1];
            outTri.tri[2] = intersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0]);

            outTri2.setColor(Color.BLUE);
            outTri2.tri[0] = insidePoints[1];
            outTri2.tri[1] = outTri.tri[2];
            outTri2.tri[2] = intersectPlane(planeP, planeN, insidePoints[1], outsidePoints[0]);
            return 2;
        }
        return 0;
    }


    public static Vec3D intersectPlane(Vec3D planeP, Vec3D planeN, Vec3D lineStart, Vec3D lineEnd) {
        planeN.normalize();
        float planeD = -Vec3D.dotProduct(planeN, planeP);
        float ad = Vec3D.dotProduct(lineStart, planeN);
        float bd = Vec3D.dotProduct(lineEnd, planeN);
        float t = (-planeD - ad) / (bd - ad);
        Vec3D lineStartToEnd = Vec3D.sub(lineEnd, lineStart);
        Vec3D lineToIntersect = Vec3D.mul(lineStartToEnd, t);
        return Vec3D.add(lineStart, lineToIntersect);
    }

    private static float shortestDistanceToPlane(Vec3D planeP, Vec3D planeN, Vec3D p) {
        return Vec3D.dotProduct(planeN, p) - Vec3D.dotProduct(planeN, planeP);
    }

    public static Triangle translate(Triangle t, Vec3D a) {
        Vec3D[] vec3DS = t.tri;
        for (int i = 0; i < vec3DS.length; i++) {
            Vec3D vec3D = vec3DS[i];
            vec3D.x += a.x;
            vec3D.y += a.y;
            vec3D.z += a.z;
            vec3DS[i] = vec3D;
        }
        return new Triangle(vec3DS);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
