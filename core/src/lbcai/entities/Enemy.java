package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.ai.GdxAI;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;

public class Enemy {
	//extend later when adding more enemy types
	private final Platform platform;
	public Vector2 position;
	Facing facing;
	final long startTime;
	
	public Enemy(Platform platform) {
		this.platform = platform;
		position = new Vector2(platform.left, platform.top + Constants.pBeetleEyeHeight.y);
		facing = Facing.RIGHT;
		startTime = TimeUtils.nanoTime();
	}

	public void update(float delta) {
		switch (facing) {
		case LEFT:
			position.x -= Constants.enemyMoveSpeed * delta;
			break;
		case RIGHT:
			position.x += Constants.enemyMoveSpeed * delta;
		}
		
		if (position.x < platform.left) {
			position.x = platform.left;
			facing = Facing.RIGHT;
		} else if (position.x > platform.right) {
			position.x = platform.right;
			facing = Facing.LEFT;
		}
		
		//makes the floating bob up and down
		final float elapsedTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - startTime);
		//multiplier of amplitude = 1 + sin(2 PI elapsedTime / period)
		final float floatMultiplier = 1 + MathUtils.sin(MathUtils.PI2 * (elapsedTime / Constants.floatpBeetlePeriod));
		position.y = platform.top + Constants.pBeetleEyeHeight.y + (Constants.floatpBeetleAmplitude * floatMultiplier);
		
	}
	
	public void render(SpriteBatch batch) {
		
		TextureRegion region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
		Boolean flipx = false;
		
		if (facing == Facing.LEFT) {
			region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
			flipx = false;
		} else if (facing == Facing.RIGHT) {
			region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
			flipx = true;
		}
		
		
		batch.draw(region.getTexture(), 
				(position.x - Constants.pBeetleEyeHeight.x), 
				(position.y - Constants.pBeetleEyeHeight.y), 
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
