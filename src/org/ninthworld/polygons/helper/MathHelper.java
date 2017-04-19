package org.ninthworld.polygons.helper;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by NinthWorld on 4/16/2017.
 */
public class MathHelper {

    public static double max(double a, double...b){
        for(int i=0; i<b.length; i++){
            a = Math.max(a, b[i]);
        }

        return a;
    }

    public static double min(double a, double...b){
        for(int i=0; i<b.length; i++){
            a = Math.min(a, b[i]);
        }

        return a;
    }

    public static double clamp(double a, double min, double max){
        return Math.max(Math.min(a, max), min);
    }

    public static Vector3f mix(Vector3f v0, Vector3f v1, float a){
        return Vector3f.add(new Vector3f(v0.x*(1f-a), v0.y*(1f-a), v0.z*(1f-a)), new Vector3f(v1.x*a, v1.y*a, v1.z*a), null);
    }

    public static Vector3f mul(Vector3f v, float a){
        return new Vector3f(v.x * a, v.y * a, v.z * a);
    }
}
