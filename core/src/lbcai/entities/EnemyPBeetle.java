package lbcai.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.JumpState;

public class EnemyPBeetle extends Enemy {
	
	
	
	public EnemyPBeetle(Platform platform, Level level) {
		super(platform, level);
	}
	
	@Override
	public void update(float delta) {
		if (inactive == false) {
			//if not dead, do your thing
			lastFramePosition.set(position);
			velocity.y -= delta * Constants.worldGravity;
			position.mulAdd(velocity, delta);
			
			
			
			for (Platform platform : level.getPlatforms()) {
				if (landOnPlatform(platform)) {
					jumpState = JumpState.GROUNDED;
					jumpCounter = 0;
					position.y = platform.top + eyeHeight.y;
					velocity.setZero();
				}
			}
			
			if (aggroRange.overlaps(target.hitBox)) {
				if (!(target.position.x + (target.hitBox.width / 2) > position.x && 
						target.position.x - (target.hitBox.width / 2) < position.x)) {
					if (target.position.x > position.x) {
						//move to the right if player is to enemy's right, but if enemy position is in player's hitbox x value, 
						//don't. this prevents enemy from vibrating back and forth on top of player's position
						moveRight(delta);
					} else if (target.position.x < position.x) {
						//same as above, but left
						moveLeft(delta);
					}
				}
				
				
				//separate from move check above so enemy can move and jump at once
				if (target.hitBox.y > position.y) {
					//spam jump
					startJump();
					
				}
				//if target is below the monster do not do anything special.
			} else {
				//imagine the player leaves the aggro range. get enemy back to home platform, then if on home platform, run
				//idle/wander ai. avoid having holes in the map with this method
				if (!(platform.left < position.x && position.x < platform.right)) {
					float homePlatformCenter = platform.getCenterX();
					if (position.x > homePlatformCenter) {
						moveLeft(delta);
					} else if (position.x < homePlatformCenter) {
						moveRight(delta);
					}
				} else if (position.y != platform.top + eyeHeight.y) {
					if (platform.top < position.y) {
						//downjump back to home platform. placeholder
						position.lerp(new Vector2(position.x, platform.top + eyeHeight.y + 1), 0.8f);
					} else if (platform.top > position.y) {
						//special launch jump to return to platform and ignore any other platforms.
						//use a parabola to calculate arc
						position.lerp(new Vector2(position.x, platform.top + eyeHeight.y + 1), 0.8f);
					}
				} else {
					//wander
				}
					
			}

			hitBox = new Rectangle(
					position.x - collisionRadius.x,
					position.y - collisionRadius.y,
					2 * collisionRadius.x,
					2 * collisionRadius.y);
			
			aggroRange = new Rectangle(
					position.x - Constants.aggroRadius.x,
					position.y - Constants.aggroRadius.y / 4,
					2 * Constants.aggroRadius.x,
					1.5f * Constants.aggroRadius.y);
			
			//if falling, set jumpstate to falling
			if (velocity.y < 0) {
				jumpState = JumpState.FALLING;
			}
			
			//kill enemy if it somehow drops off the map, should respawn on its assigned platform
			if (position.y < Constants.killPlane) {
				health = 0;
			}
			
			if (health <= 0) {
				level.dropItem(this);
				inactive = true;
				inactiveTimer = TimeUtils.nanoTime();
			}

		} else {
			if (Utils.secondsSince(inactiveTimer) < Constants.respawnTime) {
				if (alpha > 0f/255f) {
					alpha -= 5f/255f;
				} else {
					alpha = 0f/255f;
				}
			}
			//respawn if time is up
			else if (Utils.secondsSince(inactiveTimer) >= Constants.respawnTime) {
				if (alpha == 0f/255f) {
					position = new Vector2((MathUtils.random() * (platform.right - platform.left + 1) + platform.left), platform.top + eyeHeight.y);
				}
				alpha += 15f/255f;
				if (alpha >= 255f/255f) {
					health = maxHealth;
					inactive = false;
					alpha = 255f/255f;
				}
			}
		}
	}
	
	//placeholder
	void simplePatrol(float delta) {
		//current braindead patrol ai
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
		
		//makes the floating bob up and down
		final float elapsedTime = Utils.secondsSince(startTime);
		//multiplier of amplitude = 1 + sin(2 PI elapsedTime / period)
		final float floatMultiplier = 1 + MathUtils.sin(MathUtils.PI2 * (elapsedTime / Constants.floatpBeetlePeriod));
		position.y = platform.top + Constants.pBeetleEyeHeight.y + (Constants.floatpBeetleAmplitude * floatMultiplier);

	}
	
}
