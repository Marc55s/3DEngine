import geom.Mat4x4;
import geom.Mesh;
import geom.Triangle;
import geom.Vec3D;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class Screen extends JPanel implements Runnable {

    Mesh mesh;
    private Thread thread;

    //Screenvars
    final int originalTileSize = 16;
    final int scale = 3;

    final int tileSize = originalTileSize * scale;
    final int maxScreenCol = 18;
    final int maxScreenRow = 14;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;

    Vec3D vCamera;
    Vec3D vLookDir = new Vec3D(1, 1, 1);
    Mat4x4 projectionMatrix;
    float fNear;
    float fFar;
    float fAspectratio;
    float fFov;
    float alpha;
    float fYaw;

    {
        fNear = 0.1f;
        fFar = 1000f;
        fFov = 90;
        fAspectratio = (float) screenHeight / (float) screenWidth;
        vCamera = new Vec3D(0, 0, 0);
        projectionMatrix = Mat4x4.createProjectionMatrix(fAspectratio, fFov, fFar, fNear);
    }


    public Screen() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        mesh = new Mesh();
        mesh.loadObjFile("axis.obj");
        //mesh.loadObjFile("VFAN500_v13.obj");
        //mesh.loadObjFile("ship.obj");
        //mesh.loadObjFile("Porsche 911 CFD READY v1.obj");
    }

    Vector<Triangle> vecTrianglesToDraw = new Vector<>();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.WHITE);
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);

        //Z-Buffer
        //Map<Float, Triangle> map = new HashMap<>();
        /*
        TreeMap<Float, Triangle> map = new TreeMap<>(Collections.reverseOrder());
        List<Float> zDepth = vecTrianglesToDraw.parallelStream().map(e -> (e.tri[0].z + e.tri[1].z + e.tri[2].z) / 3).toList();
        for (int i = 0; i < vecTrianglesToDraw.size(); i++) {
            map.put(zDepth.get(i), vecTrianglesToDraw.get(i));
        }
        for (Map.Entry<Float, Triangle> entry : map.entrySet()) {
            Triangle triangle = entry.getValue();
            drawTriangle(g2, (int) triangle.tri[0].x, (int) triangle.tri[0].y, (int) triangle.tri[1].x, (int) triangle.tri[1].y, (int) triangle.tri[2].x, (int) triangle.tri[2].y, triangle);
        }
         */

        for (int j = 0; j < vecTrianglesToDraw.size(); j++) {
            Triangle triangle = vecTrianglesToDraw.get(j);
            drawTriangle(g2, (int) triangle.tri[0].x, (int) triangle.tri[0].y, (int) triangle.tri[1].x, (int) triangle.tri[1].y, (int) triangle.tri[2].x, (int) triangle.tri[2].y, triangle);
        }
    }

    void drawTriangle(Graphics2D g, int x1, int y1, int x2, int y2, int x3, int y3, Triangle triangle) {
        Polygon p = new Polygon();
        p.addPoint(x1, y1);
        p.addPoint(x2, y2);
        p.addPoint(x3, y3);
        //triangle.setColor(Color.darkGray);
        //g.setPaint(new GradientPaint(x1, y1, triangle.getColor().brighter(), x2, y2, triangle.getColor().darker()));
        g.setColor(triangle.getColor());
        g.fillPolygon(p);
    }

    public void update(double delta) {
        Vec3D vForward = Vec3D.mul(vLookDir, (float) (0.5f * delta));

        switch (KeyHandler.direction) {
            case UP -> vCamera.y += 0.2f * delta;
            case DOWN -> vCamera.y -= 0.2f * delta;
            case LEFT -> vCamera.x += 0.2f * delta;
            case RIGHT -> vCamera.x -= 0.2f * delta;
            case W -> vCamera = Vec3D.add(vCamera, vForward);
            case S -> vCamera = Vec3D.sub(vCamera, vForward);
            case A -> fYaw += 0.05f * delta;
            case D -> fYaw -= 0.05f * delta;
        }

        Mat4x4 xRotation;
        Mat4x4 zRotation;
        zRotation = Mat4x4.createZRotationMatrix(alpha * 0.5f);
        xRotation = Mat4x4.createXRotationMatrix(alpha);

        Mat4x4 translationMatrix = Mat4x4.createTranslationMatrix(0, 0, 6); // OFFSET Y ###########################################
        //World matrix
        Mat4x4 world;
        world = Mat4x4.multiplyMatrices(zRotation, xRotation);
        world = Mat4x4.multiplyMatrices(world, translationMatrix);

        Vec3D vUp = new Vec3D(0, 1, 0);
        Vec3D vTarget = new Vec3D(0, 0, 1);
        Mat4x4 matCameraRot = Mat4x4.createYRotation(fYaw);
        vLookDir = new Vec3D(0, 0, 1);
        vLookDir = Mat4x4.multiplyVector(vTarget, matCameraRot);
        vTarget = Vec3D.add(vCamera, vLookDir);

        Mat4x4 matCamera = Mat4x4.pointAt(vCamera, vTarget, vUp);

        Mat4x4 viewMat = Mat4x4.quickInverse(matCamera);

        vecTrianglesToDraw.clear();

        for (Triangle triangle : mesh.triangles) {
            Triangle triProjected, triTransformed, triViewed;

            //Transform
            triTransformed = triangle.applyMatrix(world);

            //N-vector for each triangle
            Vec3D nNormalized, line1, line2;
            line1 = Vec3D.sub(triTransformed.tri[1], triTransformed.tri[0]);
            line2 = Vec3D.sub(triTransformed.tri[2], triTransformed.tri[0]);
            nNormalized = Vec3D.crossProduct(line1, line2);
            nNormalized.normalize();

            Vec3D vCameraRay = Vec3D.sub(triTransformed.tri[0], vCamera);

            //if(nNormalized.z < 0) {
            if (Vec3D.dotProduct(nNormalized, vCameraRay) < 0.0f) {

                //Illumination
                Vec3D lightDirection = new Vec3D(0.0f, 1.0f, -1.0f);
                lightDirection.normalize();

                //Dotproduct to adapt shading of the light
                float dp = Vec3D.dotProduct(lightDirection, nNormalized);
                Color c = null;
                int grey = (int) (Math.abs(dp) * 255);
                if (grey >= 255) grey = 255;
                c = new Color(grey, grey, grey);

                //Convert World Space --> View Space
                triViewed = triTransformed.applyMatrix(viewMat);

                //Clipping
                int clippedTriangles = 0;
                Triangle[] clipped = new Triangle[2];
                clipped[0] = new Triangle();
                clipped[1] = new Triangle();
                clippedTriangles = Triangle.clipAgainstPlane(new Vec3D(0, 0, 0.1f), new Vec3D(0, 0, 1), triViewed, clipped[0], clipped[1]);
                for (int n = 0; n < clippedTriangles; n++) {
                    triProjected = new Triangle();
                    triProjected.tri[0] = Mat4x4.multiplyVector(clipped[n].tri[0], projectionMatrix);
                    triProjected.tri[1] = Mat4x4.multiplyVector(clipped[n].tri[1], projectionMatrix);
                    triProjected.tri[2] = Mat4x4.multiplyVector(clipped[n].tri[2], projectionMatrix);

                    triProjected.tri[0] = Vec3D.div(triProjected.tri[0], triProjected.tri[0].w);
                    triProjected.tri[1] = Vec3D.div(triProjected.tri[1], triProjected.tri[1].w);
                    triProjected.tri[2] = Vec3D.div(triProjected.tri[2], triProjected.tri[2].w);

                    triProjected.setColor(clipped[n].getColor());

                    for (int i = 0; i < 3; i++) {
                        triProjected.tri[i].x *= -1.0f;
                        triProjected.tri[i].y *= -1.0f;
                    }

                    //Scale
                    Vec3D vOffsetView = new Vec3D(1, 1, 0);
                    for (int i = 0; i < 3; i++) {
                        triProjected.tri[i] = Vec3D.add(triProjected.tri[i], vOffsetView);
                    }
                    for (int i = 0; i < 3; i++) {
                        triProjected.tri[i].x *= 0.5f * screenWidth;
                        triProjected.tri[i].y *= 0.5f * screenHeight;
                    }
                    if (triProjected.getColor() == null)
                        triProjected.setColor(c);
                    vecTrianglesToDraw.add(triProjected);
                }
            }
        }
    }


    public void startGameThread() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        double FPS = 60;
        double frames = 0;
        double timer = 0;
        double drawInterval = 1_000_000_000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (thread.isAlive()) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update(delta);
                //paintImmediately(getBounds());
                repaint();
                frames++;
                delta--;
            }
            if (timer >= 1_000_000_000) {
                //System.out.println("FPS : " + frames);
                timer = 0;
                frames = 0;
            }
        }
    }
}