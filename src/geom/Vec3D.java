package geom;


public class Vec3D {

    public float x, y, z, w;

    public Vec3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3D(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec3D() {
        this(0, 0, 0, 1);
    }


    public static Vec3D add(Vec3D a, Vec3D b) {
        return new Vec3D(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vec3D sub(Vec3D a, Vec3D b) {
        return new Vec3D(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static Vec3D mul(Vec3D a, float b) {
        return new Vec3D(a.x * b, a.y * b, a.z * b);
    }

    public static Vec3D div(Vec3D a, float b) {
        return new Vec3D(a.x / b, a.y / b, a.z / b);
    }

    public static Vec3D crossProduct(Vec3D a, Vec3D b) {
        Vec3D n = new Vec3D();
        n.x = a.y * b.z - a.z * b.y;
        n.y = a.z * b.x - a.x * b.z;
        n.z = a.x * b.y - a.y * b.x;
        return n;
    }

    public static float getLength(Vec3D v) {
        return (float) Math.sqrt(dotProduct(v,v));
    }

    public void normalize() {
        Vec3D v = this;
        float length = (float) Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        if (length == 0) {
            return;
        }
        this.x /= length;
        this.y /= length;
        this.z /= length;
        this.w /= length;
    }

    public static Vec3D normalize(Vec3D v) {
        float length = (float) Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        return new Vec3D(v.x / length, v.y / length, v.z / length);
    }

    public float dotProduct(Vec3D a) {
        Vec3D b = this;
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static float dotProduct(Vec3D a, Vec3D b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

}
