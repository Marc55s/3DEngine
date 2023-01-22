package geom;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Mat4x4 {
    public float[][] m = new float[4][4];

    public static Mat4x4 pointAt(Vec3D pos, Vec3D target, Vec3D up){
        Mat4x4 matrix = new Mat4x4();
        Vec3D newForward = Vec3D.sub(target,pos);
        newForward.normalize();
        Vec3D vec = Vec3D.mul(newForward, Vec3D.dotProduct(up,newForward));
        Vec3D newUp = Vec3D.sub(up,vec);
        newUp.normalize();

        Vec3D newRight = Vec3D.crossProduct(newUp,newForward);

        //Construc matrix
        matrix.m[0][0] = newRight.x;	matrix.m[0][1] = newRight.y;	matrix.m[0][2] = newRight.z;	matrix.m[0][3] = 0.0f;
        matrix.m[1][0] = newUp.x;		matrix.m[1][1] = newUp.y;		matrix.m[1][2] = newUp.z;		matrix.m[1][3] = 0.0f;
        matrix.m[2][0] = newForward.x;	matrix.m[2][1] = newForward.y;	matrix.m[2][2] = newForward.z;	matrix.m[2][3] = 0.0f;
        matrix.m[3][0] = pos.x;			matrix.m[3][1] = pos.y;			matrix.m[3][2] = pos.z;			matrix.m[3][3] = 1.0f;
        return matrix;
    }

    public static Mat4x4 quickInverse(Mat4x4 m) // Only for Rotation/Translation Matrices
    {
        Mat4x4 matrix = new Mat4x4();
        matrix.m[0][0] = m.m[0][0]; matrix.m[0][1] = m.m[1][0]; matrix.m[0][2] = m.m[2][0]; matrix.m[0][3] = 0.0f;
        matrix.m[1][0] = m.m[0][1]; matrix.m[1][1] = m.m[1][1]; matrix.m[1][2] = m.m[2][1]; matrix.m[1][3] = 0.0f;
        matrix.m[2][0] = m.m[0][2]; matrix.m[2][1] = m.m[1][2]; matrix.m[2][2] = m.m[2][2]; matrix.m[2][3] = 0.0f;
        matrix.m[3][0] = -(m.m[3][0] * matrix.m[0][0] + m.m[3][1] * matrix.m[1][0] + m.m[3][2] * matrix.m[2][0]);
        matrix.m[3][1] = -(m.m[3][0] * matrix.m[0][1] + m.m[3][1] * matrix.m[1][1] + m.m[3][2] * matrix.m[2][1]);
        matrix.m[3][2] = -(m.m[3][0] * matrix.m[0][2] + m.m[3][1] * matrix.m[1][2] + m.m[3][2] * matrix.m[2][2]);
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    public static Vec3D multiplyVector(Vec3D i, Mat4x4 m) {
        Vec3D o = new Vec3D();
        o.x = i.x * m.m[0][0] + i.y * m.m[1][0] + i.z * m.m[2][0] + m.m[3][0];
        o.y = i.x * m.m[0][1] + i.y * m.m[1][1] + i.z * m.m[2][1] + m.m[3][1];
        o.z = i.x * m.m[0][2] + i.y * m.m[1][2] + i.z * m.m[2][2] + m.m[3][2];
        o.w = i.x * m.m[0][3] + i.y * m.m[1][3] + i.z * m.m[2][3] + m.m[3][3];
        return o;
    }


    public static Mat4x4 createMatrixIdentity() {
        Mat4x4 matrix = new Mat4x4();
        for (int i = 0; i < 4; i++) {
            matrix.m[i][i] = 1;
        }
        return matrix;
    }

    public static Mat4x4 multiplyMatrices(Mat4x4 mat, Mat4x4 mat2) {
        Mat4x4 matrix = new Mat4x4();
        for (int i = 0; i < mat.m.length; i++) {
            for (int j = 0; j < mat2.m.length; j++) {
                matrix.m[j][i] = mat.m[j][0] * mat2.m[0][i] + mat.m[j][1] * mat2.m[1][i] + mat.m[j][2] * mat2.m[2][i] + mat.m[j][3] * mat2.m[3][i];
            }
        }
        return matrix;
    }


    public static Mat4x4 createProjectionMatrix(float fAspectratio, float fFovDegrees, float fFar, float fNear) {
        /*
            ( af | 0 | 0 | 0 )
            ( 0 | f | 0 | 0 )
            ( 0 | 0 | q | 1 )
            ( 0 | 0 | -Znear*q | 0 )
         */
        float fFovRad = (float) (1.0f / Math.tan(fFovDegrees * 0.5f / 180f * Math.PI)); // ? Math.toRadians
        Mat4x4 matrix = new Mat4x4();
        matrix.m[0][0] = fAspectratio * fFovRad;
        matrix.m[1][1] = fFovRad;
        matrix.m[2][2] = fFar / (fFar - fNear);
        matrix.m[3][2] = (-fFar * fNear) / (fFar - fNear);
        matrix.m[2][3] = 1.0f;
        matrix.m[3][3] = 0;
        return matrix;
    }

    public static Mat4x4 createTranslationMatrix(float x, float y, float z) {
        Mat4x4 matrix = new Mat4x4();
        matrix.m[0][0] = 1;
        matrix.m[1][1] = 1;
        matrix.m[2][2] = 1;
        matrix.m[3][3] = 1;
        matrix.m[3][0] = x;
        matrix.m[3][1] = y;
        matrix.m[3][2] = z;
        return matrix;
    }

    public static Mat4x4 createZRotationMatrix(float angle) {
        Mat4x4 zRotation = new Mat4x4();
        zRotation.m[0][0] = (float) cos(angle);
        zRotation.m[0][1] = (float) sin(angle);
        zRotation.m[1][0] = (float) -sin(angle);
        zRotation.m[1][1] = (float) cos(angle);
        zRotation.m[2][2] = 1;
        zRotation.m[3][3] = 1;
        return zRotation;
    }

    public static Mat4x4 createXRotationMatrix(float angle) {
        Mat4x4 xRotation = new Mat4x4();
        xRotation.m[0][0] = 1;
        xRotation.m[1][1] = (float) cos(angle);
        xRotation.m[1][2] = (float) sin(angle);
        xRotation.m[2][1] = (float) -sin(angle);
        xRotation.m[2][2] = (float) cos(angle);
        xRotation.m[3][3] = 1;
        return xRotation;
    }

    public static Mat4x4 createYRotation(float fAngleRad) {
        Mat4x4 matrix = new Mat4x4();
        matrix.m[0][0] = (float) cos(fAngleRad);
        matrix.m[0][2] = (float) sin(fAngleRad);
        matrix.m[2][0] = (float) -sin(fAngleRad);
        matrix.m[1][1] = 1.0f;
        matrix.m[2][2] = (float) cos(fAngleRad);
        matrix.m[3][3] = 1.0f;
        return matrix;
    }


}
