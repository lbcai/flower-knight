package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
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
	final Platform platform;
	public Vector2 position;
	Facing facing;
	final long startTime;
	Vector2 eyeHeight;
	float moveSpeed;
	Animation<TextureRegion> leftIdleAnim;
	
	
	//default enemy type will be a potato beetle
	public Enemy(Platform platform) {
		this.platform = platform;
		this.eyeHeight = Constants.pBeetleEyeHeight;
		this.moveSpeed = Constants.enemyMoveSpeed;
		this.leftIdleAnim = Assets.instance.pBeetleAssets.idleLeftAnim;
		
		position = new Vector2(platform.left, platform.top + eyeHeight.y);
		facing = Facing.RIGHT;
		startTime = TimeUtils.nanoTime();
	}

	public void update(float delta) {
		switch (facing) {
		case LEFT:
			position.x -= moveSpeed * delta;
			break;
		case RIGHT:
			position.x += moveSpeed * delta;
		}
		
		if (position.x < platform.left) {
			position.x = platform.left;
			facing = Facing.RIGHT;
		} else if (position.x > platform.right) {
			position.x = platform.right;
			facing = Facing.LEFT;
		}
		
		
	}
	
	public void render(SpriteBatch batch) {
		
		TextureRegion region = leftIdleAnim.getKeyFrame(0);
		Boolean flipx = false;
		
		if (facing == Facing.LEFT) {
			region = leftIdleAnim.getKeyFrame(0);
			flipx = false;
		} else if (facing == Facing.RIGHT) {
			region = leftIdleAnim.getKeyFrame(0);
			flipx = true;
		}
		
		
		batch.draw(region.getTexture(), 
				(position.x - eyeHeight.x), 
				(position.y - eyeHeight.y), 
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
