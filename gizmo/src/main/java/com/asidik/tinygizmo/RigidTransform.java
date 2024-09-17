package com.asidik.tinygizmo;

public class RigidTransform {


	/*JNI
		#include "tiny-gizmo.hpp"

		using namespace tinygizmo;
		using namespace minalg;

	 */

    protected long addr;


    private float[] position = new float[3];
    private float[] orientation = new float[4];
    private float[] scale = new float[3];

    public RigidTransform () {
        addr = makeNew();
    }

    private native long makeNew ();/*
		rigid_transform* transform = new rigid_transform;
		return (jlong)transform;
	*/


    public void setScale (float scaleX, float scaleY, float scaleZ) {
        setScale(addr, scaleX, scaleY, scaleZ);
    }

    private native void setScale (long addr, float scaleX, float scaleY, float scaleZ);/*
		rigid_transform* transform = (rigid_transform*)addr;
		transform->scale.x = scaleX;
		transform->scale.y = scaleY;
		transform->scale.z = scaleZ;
	*/

    public float[] getScale () {
        jniGetScale(addr, scale);
        return scale;
    }

    private native void jniGetScale (long addr, float[] out);/*
		rigid_transform* transform = (rigid_transform*)addr;
		out[0] = transform->scale.x;
		out[1] = transform->scale.y;
		out[2] = transform->scale.z;
	*/

    public void setPosition (float x, float y, float z) {
        setPosition(addr, x, y, z);
    }

    private native void setPosition (long addr, float x, float y, float z);/*
		rigid_transform* transform = (rigid_transform*)addr;
		transform->position.x = x;
		transform->position.y = y;
		transform->position.z = z;
	*/


    public float[] getPosition () {
        jniGetPosition(addr, position);
        return position;
    }

    private native void jniGetPosition (long addr, float[] out);/*
		rigid_transform* transform = (rigid_transform*)addr;
		out[0] = transform->position.x;
		out[1] = transform->position.y;
		out[2] = transform->position.z;
	*/

    public float[] getOrientation () {
        jniGetOrientation(addr, orientation);
        return orientation;
    }

    private native void jniGetOrientation (long addr, float[] out);/*
		rigid_transform* transform = (rigid_transform*)addr;
		auto orientation = transform->orientation;
		out[0] = transform->orientation.x;
		out[1] = transform->orientation.y;
		out[2] = transform->orientation.z;
		out[3] = transform->orientation.w;
	*/


}
