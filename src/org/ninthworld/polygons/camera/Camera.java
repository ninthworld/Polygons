package org.ninthworld.polygons.camera;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.polygons.helper.MatrixHelper;

/**
 * Created by NinthWorld on 3/3/2017.
 */
public class Camera {

    private static final float moveSpeed = 0.2f;
    private static final float rotSpeed = 0.002f;
    private static final float maxLook = (float) Math.toRadians(85);

    private Vector3f position;
    private Vector3f rotation;

    public boolean onGround;
    private float velocity, gravity;

    public boolean isFreemode, isntFreemode;
    public boolean isWireframe, isntWireframe;

    public Camera(Vector3f position, Vector3f rotation){
        this.position = position;
        this.rotation = rotation;
        velocity = 0.0f;
        gravity = -0.05f;
        onGround = false;

        isWireframe = !(isntWireframe = true);
        isFreemode = !(isntFreemode = true);
    }

    public void debugKeyboard(){
        if(Keyboard.isKeyDown(Keyboard.KEY_1)){
            if(isWireframe){
                isntWireframe = true;
            }else{
                isntWireframe = false;
            }
        }else{
            if(isWireframe == isntWireframe){
                isWireframe = !isntWireframe;
            }
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_2)){
            if(isFreemode){
                isntFreemode = true;
            }else{
                isntFreemode = false;
            }
        }else{
            if(isFreemode == isntFreemode){
                isFreemode = !isntFreemode;
            }
        }

    }

    public Vector3f getPosition(){
        return position;
    }

    public void updateKeyboard() {
        float sinYaw = (float) Math.sin(rotation.y);
        float cosYaw = (float) Math.cos(rotation.y);

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            position.x += sinYaw * moveSpeed;
            position.z += -cosYaw * moveSpeed;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            position.x += -sinYaw * moveSpeed;
            position.z += cosYaw * moveSpeed;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            position.x += cosYaw * moveSpeed;
            position.z += sinYaw * moveSpeed;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            position.x += -cosYaw * moveSpeed;
            position.z += -sinYaw * moveSpeed;
        }

        if (isFreemode){
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                position.y += moveSpeed;
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                position.y += -moveSpeed;
            }
        }else{
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && onGround) {
                onGround = false;
                velocity = 0.8f;
            }

            if (velocity < -1.0f) {
                velocity = -1.0f;
            } else {
                velocity += gravity;
            }

            position.y += velocity;
        }
    }

    public void updateMouse(){
        if(Mouse.isInsideWindow() && Mouse.isButtonDown(0)){
            Mouse.setGrabbed(true);
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            Mouse.setGrabbed(false);
        }

        if(Mouse.isGrabbed()){
            float mouseDx = Mouse.getDX();
            float mouseDy = Mouse.getDY();

            rotation.x += -mouseDy * rotSpeed;
            rotation.y += mouseDx * rotSpeed;

            rotation.x = Math.max(-maxLook, Math.min(maxLook, rotation.x));
        }
    }

    public Matrix4f getViewMatrix(){
        return MatrixHelper.createViewMatrix(position, rotation);
    }

}
