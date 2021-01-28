package lbcai.flowerknight;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class mainGame extends ApplicationAdapter {
	SpriteBatch player;
	Texture playerTexture;
	
	
	@Override
	public void create () {
		player = new SpriteBatch();
		playerTexture = new Texture("badlogic.jpg");
	}

	@Override
	public void resize(int width, int height) {
		
	}
	
	@Override
	public void pause() {
		
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		player.begin();
		player.draw(playerTexture, 0, 0);
		player.end();
	}
	
	@Override
	public void dispose () {
		player.dispose();
		playerTexture.dispose();
	}
}
