package lbcai.flowerknight;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class mainGame extends ApplicationAdapter {
	// Object that "contains" drawn sprites; draw stuff to it to see the stuff. Dispose or else memory will leak.
	SpriteBatch batch;
	
	// Underlying OpenGL texture (image). Dispose or else memory will leak.
	Texture playerTexture;
	
	// Sprite object that has the coordinates and is hooked up to the texture.
	Sprite playerSprite;
	
	
	
	@Override
	public void create () {
		int winHeight = Gdx.graphics.getHeight();
		int winWidth = Gdx.graphics.getWidth();
		
		batch = new SpriteBatch();
		playerTexture = new Texture("badlogic.jpg");
		playerSprite = new Sprite(playerTexture);
		
		playerSprite.setPosition(winWidth/2 - playerSprite.getWidth()/2, 0);
	}

	@Override
	public void resize(int width, int height) {
		
	}
	
	@Override
	public void pause() {
		
	}
	
	@Override
	public void render () {
		
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			playerSprite.translateX(10.0f);
		} else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			playerSprite.translateX(-10.0f);
		}
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		playerSprite.draw(batch);
		batch.end();
		
		
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		playerTexture.dispose();
	}
}
