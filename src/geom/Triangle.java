package geom;

import java.awt.Color;

public class Triangle {
    private Color color;

    public Triangle(Vec3D[] tri) {
        this.tri = tri;
    }

    public Triangle() {
        this(new Vec3D[]{new Vec3D(), new Vec3D(), new Vec3D()});
    }

    public Vec3D[] tri = new Vec3D[3];

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
            outsidePoints[outside++] = inTri.tri[0];
        }
        if (d1 >= 0) {
            insidePoints[inside++] = inTri.tri[1];
        } else {
            outsidePoints[outside++] = inTri.tri[1];
        }
        if (d2 >= 0) {
            insidePoints[inside++] = inTri.tri[2];
        } else {
            outsidePoints[outside++] = inTri.tri[2];
        }
        if (inside == 0) {
            return 0;
        }
        if (inside == 3) {
            outTri = inTri;
            return 1;
        }
        if (inside == 1 && outside == 2) {
            outTri.setColor(inTri.getColor());
            outTri.tri[0] = insidePoints[0];
            outTri.tri[1] = Vec3D.intersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0]);
            outTri.tri[2] = Vec3D.intersectPlane(planeP, planeN, insidePoints[0], outsidePoints[1]);

            return 1;
        }
        if (inside == 2 && outside == 1) {
            outTri.setColor(inTri.getColor());

            outTri.tri[0] = insidePoints[0];
            outTri.tri[1] = insidePoints[1];
            outTri.tri[2] = Vec3D.intersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0]);

            outTri2.tri[0] = insidePoints[1];
            outTri2.tri[1] = insidePoints[2];
            outTri2.tri[2] = Vec3D.intersectPlane(planeP, planeN, insidePoints[1], outsidePoints[0]);

            return 2;
        }

        return -1;

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
