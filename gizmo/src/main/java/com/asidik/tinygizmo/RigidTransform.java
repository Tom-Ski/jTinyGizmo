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


	public float[] getPosition () {
		jniGetPosition(addr, position);
		return position;
	}

	private native void jniGetPosition (long addr, float[] out);/*
		rigid_transform* transform = (rigid_transform*)addr;
		auto pos = transform->position;
		out[0] = pos.x;
		out[1] = pos.y;
		out[2] = pos.z;
	*/

	public float[] getOrientation () {
		jniGetOrientation(addr, orientation);
		return orientation;
	}

	private native void jniGetOrientation (long addr, float[] out);/*
		rigid_transform* transform = (rigid_transform*)addr;
		auto orientation = transform->orientation;
		out[0] = orientation.x;
		out[1] = orientation.y;
		out[2] = orientation.z;
		out[3] = orientation.w;
	*/

	public float[] getScale () {
		jniGetScale(addr, scale);
		return scale;
	}

	private native void jniGetScale (long addr, float[] out);/*
		rigid_transform* transform = (rigid_transform*)addr;
		auto scale = transform->scale;
		out[0] = scale.x;
		out[1] = scale.y;
		out[2] = scale.z;
	*/
}
