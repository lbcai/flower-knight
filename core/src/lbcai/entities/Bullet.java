package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;

public class Bullet {
	
	private final Facing facing;
	Vector2 position;
	private long startTime;
	Boolean flipx;
	Level level;
	//For deciding when the clean up the bullet object.
	public Boolean active;
	
	
	public Bullet(Level level, Vector2 position, Facing facing) {
		this.position = position;
		this.facing = facing;
		this.startTime = TimeUtils.nanoTime();
		this.level = level;
		active = true;
	}
	
	
	public void update(float delta) {
		switch (facing) {
		case LEFT:
			position.x -= delta * Constants.bulletMoveSpeed;
			break;
		case RIGHT:
			position.x += delta * Constants.bulletMoveSpeed;
			break;
		}
		
		//get width of screen from level viewport
		final float worldWidth = level.getViewport().getWorldWidth();
		//get camera's horizontal position
		final float cameraX = level.getViewport().getCamera().position.x;
		//check if bullet goes off the screen, change to not active if so, in Level we destroy inactive bullets.
		//this also has the benefit of dandelions not shooting at player until you share a screen with them.
		if (position.x < cameraX - worldWidth / 2 || position.x > cameraX + worldWidth / 2) {
			active = false;
		}
		
	}
	
	public void render(SpriteBatch batch) {
		TextureRegion region = Assets.instance.bulletAssets.bulletAnim.getKeyFrame(0);
		
		float animTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - startTime);
		region = Assets.instance.bulletAssets.bulletAnim.getKeyFrame(animTime);	
		
		if (facing == Facing.LEFT) {
			flipx = false;
		} else if (facing == Facing.RIGHT) {
			flipx = true;
		}
		
		batch.draw(region.getTexture(), 
				(position.x - Constants.bulletCenter.x), 
				(position.y - Constants.bulletCenter.y), 
				0, 
				0, 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				1, 
				1, 
				0, 
				region.getRegionX(), 
				region.getRegionY(), 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				flipx, 
				false);
		
	}
	
}
