package com.asidik.tinygizmo;

import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class TinyGizmo {

	public static final int MOUSE_BUTTON_LEFT = 1;
	public static final int MOUSE_BUTTON_RIGHT = 2;

	public static final int BUTTON_PUSH = 3;
	public static final int BUTTON_RELEASE = 4;

	public static final int KEY_LEFT_CONTROL = 5;
	public static final int KEY_L = 6;
	public static final int KEY_T = 7;
	public static final int KEY_R = 8;
	public static final int KEY_S = 9;
	public static final int KEY_W = 10;
	public static final int KEY_A = 11;
	public static final int KEY_D = 12;


	static {
		new SharedLibraryLoader().load("jtinygizmo");
	}

	/*JNI
		#include "tiny-gizmo.hpp"
		#include <iostream>

		using namespace tinygizmo;
		using namespace minalg;

		static const int MOUSE_BUTTON_LEFT = 1;
		static const int MOUSE_BUTTON_RIGHT = 2;

		static const int BUTTON_PUSH = 3;
		static const int BUTTON_RELEASE = 4;

		static const int KEY_LEFT_CONTROL = 5;
		static const int KEY_L = 6;
		static const int KEY_T = 7;
		static const int KEY_R = 8;
		static const int KEY_S = 9;
		static const int KEY_W = 10;
		static const int KEY_A = 11;
		static const int KEY_D = 12;


		bool ml = 0, mr = 0, bf = 0, bl = 0, bb = 0, br = 0;

		gizmo_application_state gizmo_state;
	  	gizmo_context gizmo_ctx;

		geometry_mesh cachedRender;

		void renderImpl (const geometry_mesh & r) {
			cachedRender = r;
		}

		void initRenderImpl () {
			gizmo_ctx.render = &renderImpl;
		}


	 */

	public TinyGizmo () {
		nativeInit();
	}

	private native void nativeInit ();/*
		initRenderImpl();
	*/

	public native void onKeyDown (int key, int action, int mods);/*
        if (key == KEY_LEFT_CONTROL) gizmo_state.hotkey_ctrl = (action != BUTTON_RELEASE);
        if (key == KEY_L) gizmo_state.hotkey_local = (action != BUTTON_RELEASE);
        if (key == KEY_T) gizmo_state.hotkey_translate = (action != BUTTON_RELEASE);
        if (key == KEY_R) gizmo_state.hotkey_rotate = (action != BUTTON_RELEASE);
        if (key == KEY_S) gizmo_state.hotkey_scale = (action != BUTTON_RELEASE);
        if (key == KEY_W) bf = (action != BUTTON_RELEASE);
        if (key == KEY_A) bl = (action != BUTTON_RELEASE);
        if (key == KEY_S) bb = (action != BUTTON_RELEASE);
        if (key == KEY_D) br = (action != BUTTON_RELEASE);
	*/

	public native void onMouseButton (int button, int action, int mods);/*
	   	if (button == MOUSE_BUTTON_LEFT) gizmo_state.mouse_left = (action != BUTTON_RELEASE);
        if (button == MOUSE_BUTTON_LEFT) ml = (action != BUTTON_RELEASE);
        if (button == MOUSE_BUTTON_RIGHT) mr = (action != BUTTON_RELEASE);
	*/


	public native void updateWindow (float windowWidth, float windowHeight);/*
		gizmo_state.viewport_size = {windowWidth, windowHeight};
	*/

	public native void updateRay (float rayDirX, float rayDirY, float rayDirZ);/*
		gizmo_state.ray_direction = {rayDirX, rayDirY, rayDirZ};
	*/

	public native void updateCamera (float yfov, float nearClip, float farClip, float posX, float posY, float posZ, float orientationi, float orientationj, float orientationk, float orientationl);/*

		camera_parameters cam = gizmo_state.cam;

		cam.yfov = yfov;
		cam.near_clip = nearClip;
		cam.far_clip = farClip;
		cam.position = { posX, posY, posZ };
		cam.orientation = {orientationi, orientationj, orientationk, orientationl };
		gizmo_state.ray_origin = {posX, posY, posZ};
	*/

	public native void update (GizmoState gizmoState);/*

		gizmo_ctx.update(gizmo_state);
	*/

	public native void setScreenSpaceScale (float screenSpaceScale);/*
		gizmo_state.screenspace_scale = screenSpaceScale;
	*/


	public boolean transformGizmo (String name, RigidTransform rigidTransform) {
		return transformGizmo(name, rigidTransform.addr);
	}
	private native boolean transformGizmo (String name, long rigidTransformAddr);/*

		rigid_transform* transform = (rigid_transform*)rigidTransformAddr;

		bool activated = transform_gizmo(name, gizmo_ctx, *transform);

		return activated;
	*/

	public native void obtainRender (FloatBuffer verts, IntBuffer triangles, int[] sizes);/*
		gizmo_ctx.draw();

		std::vector<geometry_vertex> nverts = cachedRender.vertices;
		std::vector<minalg::uint3> ntris = cachedRender.triangles;

		auto index = 0;
		auto vertexCounter = 0;

		auto triindex = 0;
		auto triCounter = 0;

		for (auto &vertex : nverts) {

			verts[index++] = vertex.position.x;
			verts[index++] = vertex.position.y;
			verts[index++] = vertex.position.z;

			verts[index++] = vertex.normal.x;
			verts[index++] = vertex.normal.y;
			verts[index++] = vertex.normal.z;

			verts[index++] = vertex.color.x;
			verts[index++] = vertex.color.y;
			verts[index++] = vertex.color.z;
			verts[index++] = vertex.color.w;

			vertexCounter++;
		}


		for (auto &tri : ntris) {
			triangles[triindex++] = tri.x;
			triangles[triindex++] = tri.y;
			triangles[triindex++] = tri.z;
			triCounter++;
		}

		sizes[0] = vertexCounter;
		sizes[1] = triCounter * 3;

	*/

}
