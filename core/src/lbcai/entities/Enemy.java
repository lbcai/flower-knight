package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.ai.GdxAI;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.HitState;
import lbcai.util.Enums.LockState;
import lbcai.util.Enums.RunState;

public class Enemy {
	//extend later when adding more enemy types
	final Platform platform;
	public Vector2 position;
	Facing facing;
	final long startTime;
	Vector2 eyeHeight;
	float moveSpeed;
	Animation<TextureRegion> leftIdleAnim;
	float collisionRadius;
	public int HP;
	public Rectangle hitBox;
	HitState hitState;
	RunState runState;
	LockState lockState;
	private long hitStartTime;
	
	
	//default enemy type will be a potato beetle
	public Enemy(Platform platform) {
		this.platform = platform;
		this.eyeHeight = Constants.pBeetleEyeHeight;
		this.moveSpeed = Constants.enemyMoveSpeed;
		this.leftIdleAnim = Assets.instance.pBeetleAssets.idleLeftAnim;
		this.collisionRadius = Constants.pBeetleCollisionRadius;
		this.HP = Constants.pBeetleHP;
		hitState = HitState.NOHIT;
		runState = RunState.IDLE;
		lockState = LockState.FREE;
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
		
		hitBox = new Rectangle(
				position.x - collisionRadius,
				position.y - collisionRadius,
				2 * collisionRadius,
				2 * collisionRadius);
		
	}
	
	public void render(SpriteBatch batch) {
		
		TextureRegion region = leftIdleAnim.getKeyFrame(0);
		Boolean flipx = false;
		
		//placeholder animations
		if (facing == Facing.LEFT) {
			if (runState == RunState.IDLE) {
				region = leftIdleAnim.getKeyFrame(0);
			} else if (runState == RunState.RUN) {
				region = leftIdleAnim.getKeyFrame(0);
			}
			
			if (hitState == HitState.IFRAME) {
				region = leftIdleAnim.getKeyFrame(0);
				if (Utils.secondsSince(hitStartTime) > Constants.enemyFlinchTime) {
					hitState = HitState.NOHIT;
				}
			}
			//use for attacking animation
			if (lockState == LockState.LOCK) {
				region = leftIdleAnim.getKeyFrame(0);
			}
			
			flipx = false;
			
		} else if (facing == Facing.RIGHT) {
			if (runState == RunState.IDLE) {
				region = leftIdleAnim.getKeyFrame(0);
			} else if (runState == RunState.RUN) {
				region = leftIdleAnim.getKeyFrame(0);
			}
			
			if (hitState == HitState.IFRAME) {
				region = leftIdleAnim.getKeyFrame(0);
				if (Utils.secondsSince(hitStartTime) > Constants.enemyFlinchTime) {
					hitState = HitState.NOHIT;
				}
			}
			
			if (lockState == LockState.LOCK) {
				region = leftIdleAnim.getKeyFrame(0);
			}
			
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
	
	public void isDamaged(int damage) {
		HP -= damage;
		// using iframe to denote flinch animation but no actual iframe for mobs
		hitState = HitState.IFRAME;
		hitStartTime = TimeUtils.nanoTime();
	}
}
