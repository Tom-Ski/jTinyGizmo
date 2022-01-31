package com.asidik.tinygizmo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.IntIntMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class LwjglTest extends ApplicationAdapter {

	private PerspectiveCamera camera;
	private FirstPersonCameraController firstPersonCameraController;

	private TinyGizmo tinyGizmo;


	private FloatBuffer vertices;
	private IntBuffer triangles;

	private float[] verticesFloatArray;
	private short[] trianglesShortArray;

	private Mesh mesh;
	private ShaderProgram shaderProgram;

	private RigidTransform rigidTransform;
	private boolean interactGizmo;

	@Override
	public void create () {
		super.create();

		camera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.z = 10;
		camera.near = 1f;
		camera.far = 20;
		camera.update();


		tinyGizmo = new TinyGizmo();


		vertices = ByteBuffer.allocateDirect(200000).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
		triangles = ByteBuffer.allocateDirect(200000).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
		verticesFloatArray = new float[200000];
		trianglesShortArray = new short[200000];

		mesh = new Mesh(false, 20000, 20000, new VertexAttributes(
			VertexAttribute.Position(),
			VertexAttribute.Normal(),
			VertexAttribute.ColorPacked()
		));

		shaderProgram = new ShaderProgram(Gdx.files.internal("vertex.glsl"), Gdx.files.internal("fragment.glsl"));
		if (!shaderProgram.isCompiled()) {
			System.out.println(shaderProgram.getLog());
		}

		rigidTransform = new RigidTransform();


		Gdx.input.setInputProcessor(new InputMultiplexer(new InputAdapter() {

			IntIntMap keyMap = new IntIntMap();

			{
				keyMap.put(Input.Keys.CONTROL_LEFT, TinyGizmo.KEY_LEFT_CONTROL);
				keyMap.put(Input.Keys.L, TinyGizmo.KEY_L);
				keyMap.put(Input.Keys.T, TinyGizmo.KEY_T);
				keyMap.put(Input.Keys.R, TinyGizmo.KEY_R);
				keyMap.put(Input.Keys.S, TinyGizmo.KEY_S);
				keyMap.put(Input.Keys.W, TinyGizmo.KEY_W);
				keyMap.put(Input.Keys.A, TinyGizmo.KEY_A);
				keyMap.put(Input.Keys.D, TinyGizmo.KEY_D);
			}

			@Override
			public boolean keyDown (int keycode) {
				if (keyMap.containsKey(keycode)) {
					final int gizmoKeyCode = keyMap.get(keycode, -1);
					if (gizmoKeyCode != -1) {
						tinyGizmo.onKeyDown(gizmoKeyCode, TinyGizmo.BUTTON_PUSH, 0);
					}
				}
				return super.keyDown(keycode);
			}

			@Override
			public boolean keyUp (int keycode) {
				if (keyMap.containsKey(keycode)) {
					final int gizmoKeyCode = keyMap.get(keycode, -1);
					if (gizmoKeyCode != -1) {
						tinyGizmo.onKeyDown(gizmoKeyCode, TinyGizmo.BUTTON_RELEASE, 0);
					}
				}
				return super.keyUp(keycode);
			}

			@Override
			public boolean touchDown (int screenX, int screenY, int pointer, int button) {
				if (button == 0) {
					tinyGizmo.onMouseButton(TinyGizmo.MOUSE_BUTTON_LEFT, TinyGizmo.BUTTON_PUSH, 0);
				}
				if (button == 1) {
					tinyGizmo.onMouseButton(TinyGizmo.MOUSE_BUTTON_RIGHT, TinyGizmo.BUTTON_PUSH, 0);
				}
				return super.touchDown(screenX, screenY, pointer, button);
			}

			@Override
			public boolean touchUp (int screenX, int screenY, int pointer, int button) {
				if (button == 0) {
					tinyGizmo.onMouseButton(TinyGizmo.MOUSE_BUTTON_LEFT, TinyGizmo.BUTTON_RELEASE, 0);
				}
				if (button == 1) {
					tinyGizmo.onMouseButton(TinyGizmo.MOUSE_BUTTON_RIGHT, TinyGizmo.BUTTON_RELEASE, 0);
				}
				return super.touchUp(screenX, screenY, pointer, button);
			}
		}, firstPersonCameraController = new FirstPersonCameraController(camera) {
			@Override
			public boolean keyDown (int keycode) {
				if (interactGizmo && Gdx.input.isTouched()) return false;
				return super.keyDown(keycode);
			}

			@Override
			public boolean keyUp (int keycode) {
				if (interactGizmo && Gdx.input.isTouched()) return false;

				return super.keyUp(keycode);
			}

			@Override
			public boolean touchDragged (int screenX, int screenY, int pointer) {
				if (interactGizmo && Gdx.input.isTouched()) return false;

				return super.touchDragged(screenX, screenY, pointer);
			}

			@Override
			public boolean touchDown (int screenX, int screenY, int pointer, int button) {
				if (interactGizmo && Gdx.input.isTouched()) return false;

				return super.touchDown(screenX, screenY, pointer, button);
			}

			@Override
			public boolean mouseMoved (int screenX, int screenY) {
				if (interactGizmo && Gdx.input.isTouched()) return false;

				return super.mouseMoved(screenX, screenY);
			}
		}));
	}

	@Override
	public void render () {
		super.render();

		Gdx.gl.glClearColor(0, 0, 0, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);

		Gdx.gl.glFrontFace(GL20.GL_CW);

		firstPersonCameraController.update();

		final Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());

		tinyGizmo.updateWindow(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		tinyGizmo.updateRay(pickRay.direction.x, pickRay.direction.y, pickRay.direction.z);

		Quaternion quaternion = new Quaternion();
		tinyGizmo.updateCamera(
			camera.fieldOfView, camera.near, camera.far,
			camera.position.x, camera.position.y, camera.position.z,
				quaternion.x, quaternion.y, quaternion.z, quaternion.w
			);


		tinyGizmo.update(new GizmoState());

		interactGizmo = tinyGizmo.transformGizmo("TestGizmo", rigidTransform);

		int[] sizes = new int[2];

		tinyGizmo.obtainRender(vertices, triangles, sizes);

		int vertexCount = sizes[0];
		int indiciesCount = sizes[1];

		int vertexSize = 3 + 3 + 4;
		Color tempColor = new Color();

		vertices.position(0);
		triangles.position(0);


		int idx = 0;
		for (int i = 0; i < vertexCount; i++) {
			final float x = vertices.get();
			final float y = vertices.get();
			final float z = vertices.get();

			final float nx = vertices.get();
			final float ny = vertices.get();
			final float nz = vertices.get();

			final float r = vertices.get();
			final float g = vertices.get();
			final float b = vertices.get();
			final float a = vertices.get();

			final float colourPacked = tempColor.set(r, g, b, a).toFloatBits();

			verticesFloatArray[idx++] = x;
			verticesFloatArray[idx++] = y;
			verticesFloatArray[idx++] = z;
			verticesFloatArray[idx++] = nx;
			verticesFloatArray[idx++] = ny;
			verticesFloatArray[idx++] = nz;
			verticesFloatArray[idx++] = colourPacked;
		}

		int triIndex = 0;
		for (int i = 0; i < indiciesCount; i++) {
			final short t1 = (short)triangles.get();
			trianglesShortArray[triIndex++] = t1;
		}

		mesh.setVertices(verticesFloatArray, 0, idx);
		mesh.setIndices(trianglesShortArray, 0, indiciesCount );


		shaderProgram.bind();
		shaderProgram.setUniformMatrix("u_projTrans", camera.combined);
		mesh.render(shaderProgram, GL20.GL_TRIANGLES);
	}

	@Override
	public void resize (int width, int height) {
		super.resize(width, height);
	}


	public static void main (String[] args) {
		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1280, 720);
		new Lwjgl3Application(new LwjglTest(), config);
	}
}
