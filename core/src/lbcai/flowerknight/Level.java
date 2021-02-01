package lbcai.flowerknight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import lbcai.entities.Player;

public class Level {
	
	/**
	 * Make the player.
	 */
	Player player;
	
	public Level() {
		//Add player to the level.
		player = new Player();
	}
	
	public void update(float delta) {
		player.update(delta);
	}
	
	public void render(SpriteBatch batch) {
		batch.begin();
		player.render(batch);
		batch.end();
	}
}
