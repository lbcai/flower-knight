package lbcai.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.HitState;
import lbcai.util.Enums.JumpState;
import lbcai.util.Enums.LockState;
import lbcai.util.Enums.RunState;

/**
 * 
 * Entities that use a hitbox and are capable of interacting with each other.
 * Superclass includes debug renderer for purpose of checking hitboxes, which are normally invisible.
 * 
 * @author lbcai
 *
 */

public abstract class Entity implements Renderable {
	
	//this is for render order stuff
	int zValue;
	
	Rectangle hitBox;
	public Vector2 position;
	public Vector2 lastFramePosition;
	public Vector2 velocity;
	TextureRegion region;
	int damage;
	int range;
	//public so ui elements can display health values
	public int health;
	int maxHealth;
	float alpha = 255f/255f;
	float moveSpeed;
	Level level;
	
	//public so level can determine where to draw effects in some cases
	public Vector2 eyeHeight = new Vector2(500, 500);
	public Facing facing;
	public HitState hitState;
	public RunState runState;
	public int jumpCounter = 0;
	
	LockState lockState;
	JumpState jumpState;
	
	long jumpStartTime;
	long runStartTime;
	long idleStartTime;
	long timeSinceHit;
	long downStartTime;
	
	//is this "enemy" an object and cannot give touch damage to the player? if so, mark false. 
	boolean touchDmg;
	//is the enemy able to knock back the player currently?
	boolean knockback;

	//is enemy in the death state? for bullets, should we remove the bullet from existence?
	public boolean inactive;
	
	//for determining whether to flip sprite based on left or right facing
	boolean flipx;
	
	public void update(float delta) {
		//overridden by subclasses
	}
	
	public void render(SpriteBatch batch) {
		//overridden by subclasses
	}
	
	public void debugRender(ShapeRenderer shape) {

		shape.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);

	}
	
	void stayInLevel() {
		if ((hitBox.x + hitBox.width) > (level.levelBound.x + level.levelBound.width)) {
			position.x = level.levelBound.x + level.levelBound.width - (hitBox.width / 2);
		} else if (hitBox.x < level.levelBound.x) {
			//had to do this differently due to player's hitbox being off center depending on direction facing -- caused by
			//setup during sprite alignment. next time make sure all mirrored animations center perfectly
			position.x = level.levelBound.x + (position.x - hitBox.x);
		}
		
		if ((hitBox.y + hitBox.height) > level.levelBound.y + level.levelBound.height) {
			position.y = level.levelBound.y + level.levelBound.height - (hitBox.height - eyeHeight.y);
		}
		//no condition for dropping out the bottom of level because going to prevent downjumping through lowest platform on map
		
	}
	
	void moveLeft(float delta) {
		//At the beginning of movement, if we are running, save the time as the run start time.
		if (jumpState == JumpState.GROUNDED && runState != RunState.RUN) {
			runStartTime = TimeUtils.nanoTime();
		}

		facing = Facing.LEFT;
		
		if (jumpState == JumpState.GROUNDED) {
			runState = RunState.RUN;
			position.x -= delta * moveSpeed;
		}
		
	}
	
	void moveRight(float delta) {
		if (jumpState == JumpState.GROUNDED && runState != RunState.RUN) {
			runStartTime = TimeUtils.nanoTime();
		}

		facing = Facing.RIGHT;
		
		if (jumpState == JumpState.GROUNDED) {
			runState = RunState.RUN;
			position.x += delta * moveSpeed;
		}
	}
	
	void startJump() {
		//if not already jumping or in the air, allow the jump.
		if (jumpState == JumpState.GROUNDED) {
			jumpState = JumpState.JUMPING;
			jumpStartTime = TimeUtils.nanoTime();
		}
		
		if (jumpState == JumpState.JUMPING && jumpCounter == 0) {
			velocity.y = Constants.jumpSpeed;
			
			if (facing == Facing.RIGHT) {
				velocity.x = moveSpeed / 2;
			} else {
				velocity.x = -moveSpeed / 2;
			}
			
			jumpCounter += 1;
		}
		
	}
	
	
	void downJump() {
		//prevent downjumping if on the lowest platform of map
		if (((position.y - eyeHeight.y) > level.lowestTop + 10)) {
			if (jumpState == JumpState.GROUNDED) {
				jumpState = JumpState.FALLING;
				position.y -= 10;
			}
		}
	}
	
	boolean landOnPlatform(Platform platform) {
		boolean leftSideFootOnPlatform = false;
		boolean rightSideFootOnPlatform = false;
		boolean bothFootOnPlatform = false;
		
		if ((lastFramePosition.y - eyeHeight.y) >= platform.top && 
				(position.y - eyeHeight.y) < platform.top) {
			//since the player position is marked by the center of the head and this is basically in the center of the texture,
			//the "origin" is 0,0 in the center of the texture. we need to subtract half the stance width to get the edge of each
			//foot, then add on one side and subtract on the other.
			float leftSideFoot = position.x - (hitBox.width / 2);
			float rightSideFoot = position.x + (hitBox.width / 2);
			
			leftSideFootOnPlatform = (platform.left < leftSideFoot && platform.right > leftSideFoot);
			rightSideFootOnPlatform = (platform.left < rightSideFoot && platform.right > rightSideFoot);
			//technically the platform is so tiny it is smaller than the stance width.
			bothFootOnPlatform = (platform.left > leftSideFoot && platform.right < rightSideFoot);
		}
		//return true if one is true, else return false
		return leftSideFootOnPlatform || rightSideFootOnPlatform || bothFootOnPlatform;
	}

	public void knockedDown() {
		//whichever direction facing is, fly in that direction.
		if (lockState != LockState.DOWN) {
			//there is no hitState for being downed because you can still take damage when you are locked and on the floor.
			lockState = LockState.DOWN;
			downStartTime = TimeUtils.nanoTime();
		}
	}
	
	public Level getLevel() {
		return this.level;
	}
	
	public LockState getLockState() {
		return lockState;
	}
	
	public JumpState getJumpState() {
		return jumpState;
	}
	
	public int getzValue() {
		return zValue;
	}
	
	public int getyValue() {
		//add 2 to keep the entity in the right render layer compared to grass, which must have a y value lower than front 
		//platform due to overlap, so it has a little artificial padding added
		return (int) (position.y - eyeHeight.y + 2);
	}
}
