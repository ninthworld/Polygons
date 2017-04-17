package org.ninthworld.polygons.chunk;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by NinthWorld on 4/16/2017.
 */
public class ChunkEntity {

    private String rawModelName;
    private Vector3f position;

    public ChunkEntity(String rawModelName, Vector3f position){
        this.rawModelName = rawModelName;
        this.position = position;
    }

    public String getRawModelName() {
        return rawModelName;
    }

    public void setRawModelName(String rawModelName) {
        this.rawModelName = rawModelName;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }
}
