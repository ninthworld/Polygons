package org.ninthworld.polygons.helper;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by NinthWorld on 3/3/2017.
 */
public class MatrixHelper {

    public static Matrix4f createProjectionMatrix(int width, int height, float fov, float nearPlane, float farPlane){
        float aspectRatio = (float) width / (float) height;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = farPlane - nearPlane;

        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((farPlane + nearPlane) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * nearPlane * farPlane) / frustum_length);
        projectionMatrix.m33 = 0;

        return projectionMatrix;
    }

    public static Matrix4f createViewMatrix(Vector3f position, Vector3f rotation){
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate(rotation.getX(), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate(rotation.getY(), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f negativeCameraPos = new Vector3f(-position.getX(), -position.getY(), -position.getZ());
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);

        return viewMatrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f position, Vector3f rotation, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(position, matrix, matrix);
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
        Matrix4f.rotate(rotation.getX(), new Vector3f(1,0,0), matrix, matrix);
        Matrix4f.rotate(rotation.getY(), new Vector3f(0,1,0), matrix, matrix);
        Matrix4f.rotate(rotation.getZ(), new Vector3f(0,0,1), matrix, matrix);

        return matrix;
    }
}
