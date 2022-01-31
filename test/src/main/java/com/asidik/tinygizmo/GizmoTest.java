package com.asidik.tinygizmo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GizmoTest {

	public static void main (String[] args) {

		final TinyGizmo tinyGizmo = new TinyGizmo();


		RigidTransform rigidTransform = new RigidTransform();
		rigidTransform.getPosition();
		rigidTransform.getOrientation();
		rigidTransform.getScale();


		final FloatBuffer floatBuffer = ByteBuffer.allocateDirect(20000).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
		final ShortBuffer shortBuffer = ByteBuffer.allocateDirect(20000).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();


		tinyGizmo.update(new GizmoState());


		final boolean activated = tinyGizmo.transformGizmo("Test gizmo", rigidTransform);

	}
}
