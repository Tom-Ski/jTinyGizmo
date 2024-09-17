package com.asidik.tinygizmo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class UBOTest extends ApplicationAdapter {

	private SpriteBatch batch;
	private ShaderProgram shaderProgram;
	private Texture fakeTex;

	@Override
	public void create () {




		batch = new SpriteBatch();

		shaderProgram = new ShaderProgram(Gdx.files.internal("vertex.glsl"), Gdx.files.internal("testfrag.glsl"));
		if (!shaderProgram.isCompiled()) {
			System.out.println(shaderProgram.getLog());
		}
		batch.setShader(shaderProgram);

		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		fakeTex = new Texture(pixmap);
		pixmap.dispose();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1f);


		int programId = shaderProgram.getHandle();

// Get the block index for the uniform block
		int uniformBlockIndex = Gdx.gl30.glGetUniformBlockIndex(programId, "VertexData");
		if (uniformBlockIndex == GL30.GL_INVALID_INDEX) {
			throw new IllegalStateException(String.format("found no uniformBlockIndex: %s", uniformBlockIndex));
		}

// creates a simple FloatBuffer
		float[] data = new float[4];
		data[0] = 0.9f;
		data[1] = 0.4f;
		data[2] = 0.2f;
		data[3] = 1;
		FloatBuffer buf = BufferUtils.newFloatBuffer(data.length);
		buf.put(data);
		buf.rewind();
		System.out.println("buf[0] = " + buf.get(0)); // yes, the FloatBuffer is being set to 1.0f

// Create a temporary buffer to create a new handle to store buffers (?)
		IntBuffer tmpBuffer = BufferUtils.newIntBuffer(16);
		Gdx.gl30.glGenBuffers(1, tmpBuffer);
		int bufferHandle = tmpBuffer.get(0);

// create the actual UBO using this handle
		Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, bufferHandle);
		int size = buf.capacity() * 4 /* a float is 4 bytes */;
		Gdx.gl30.glBufferData(GL30.GL_UNIFORM_BUFFER, size, buf, GL30.GL_STATIC_DRAW);
		Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, 0);

// Use the index to bind to a binding point, then bind the buffer
		int bindingPoint = 0;
		Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, bufferHandle);
		Gdx.gl30.glUniformBlockBinding(programId, uniformBlockIndex, bindingPoint);
		Gdx.gl30.glBindBufferBase(GL30.GL_UNIFORM_BUFFER, bindingPoint, bufferHandle);
		Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, 0);



// update the buffer data store (should only be in the render call)
//		Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, bufferHandle);
//		Gdx.gl30.glBufferSubData(GL30.GL_UNIFORM_BUFFER, 0, buf.capacity() * 4, buf);
//		Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, 0);


		batch.begin();

		batch.draw(fakeTex, 0, 0, 150, 150);

		batch.end();
	}


	@Override
	public void dispose () {
		batch.dispose();
		shaderProgram.dispose();
		fakeTex.dispose();
	}

	public static void main (String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2);
		new Lwjgl3Application(new UBOTest(), config);
	}
}
