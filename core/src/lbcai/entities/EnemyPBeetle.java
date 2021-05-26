package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.HitState;
import lbcai.util.Enums.JumpState;
import lbcai.util.Enums.LockState;
import lbcai.util.Enums.RunState;

public class EnemyPBeetle extends Enemy {
	
	//actual position.y is represented by position.y BUT in order to make the beetle bob up and down, display the sprites
	//using positionYsine, an adjusted position.y 
	protected float positionYsine;
	
	public EnemyPBeetle(Platform platform, Level level) {
		super(platform, level);
		positionYsine = position.y;
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
			
			//if not stunned from being hit, do the AI things
			if (hitState != HitState.IFRAME) {
				
				if (aggroRange.overlaps(target.hitBox)) {
					
					if (moveSpeed != Constants.enemyMoveSpeedAggro) {
						//increase speed when chasing
						moveSpeed = Constants.enemyMoveSpeedAggro;
					}
					
					if (position.x < target.position.x - (2 * hitBox.width) && 
							position.x > target.position.x + (2 * hitBox.width)) {
						//just approach player
						if (target.position.x > position.x) {
							//move to the right if player is to enemy's right, but if enemy position is in player's hitbox x value, 
							//don't. this prevents enemy from vibrating back and forth on top of player's position
							moveRight(delta);
						} else if (target.position.x < position.x) {
							//same as above, but left
							moveLeft(delta);
						}
					} else {
						//if enemy is standing on player, move around
						chaseRandomness(delta);
					}

					//separate from move check above so enemy can move and jump at once
					//if player's foot is above the enemy, jump
					if (target.hitBox.y > position.y) {
						//spam jump
						startJump();
						
					} else if (target.position.y < position.y) {
						//downjump if player is below enemy
						downJump();
					}
					
				} else {
					
					if (moveSpeed != Constants.enemyMoveSpeed) {
						//calm down
						moveSpeed = Constants.enemyMoveSpeed;
					}
					
					//imagine the player leaves the aggro range. get enemy back to home platform, then if on home platform, run
					//idle/wander ai. avoid having holes in the map with this method
					if (!(platform.left < position.x && position.x < platform.right)) {
						float homePlatformCenter = platform.centerX;
						if (position.x > homePlatformCenter) {
							moveLeft(delta);
						} else if (position.x < homePlatformCenter) {
							moveRight(delta);
						}
					} else if (position.y != platform.top + eyeHeight.y) {
						if (platform.top < position.y) {
							//downjump back to home platform. 
							if (jumpState == JumpState.GROUNDED) {
								//if enemy just ended up above its home platform, it can downjump until it reaches home.
								downJump();
							} else if (jumpState == JumpState.JUMPING) {
								//see launch jump code below. once the enemy arrives on the platform, add extra velocity by
								//having the enemy perform a jump onto the actual surface of the platform. it now appears
								//that the enemy performed a very high superjump to arrive at its destination
								startJump();
							}
						} else if (platform.top > position.y) {
							//special launch jump to return to platform and ignore any other platforms if enemy ended up below
							//its home platform.
							velocity.y += Constants.jumpSpeed;
							jumpState = JumpState.JUMPING;
						}
					} else {
						//wander on home platform
						
						//1% chance every update for enemy to change behavior
						if (MathUtils.random() < 0.02) {
							wanderState = (int) (MathUtils.random() * wanderStateRandomizer.size());
						}
						
						if (wanderState == 0) {
							runState = RunState.IDLE;
							
						} else if (wanderState == 1) {
							//stop the enemy near the edge of the platform to avoid weird vibrating shenanigans
							if (position.x > platform.left + 10) {
								moveLeft(delta);
							} else {
								wanderState = 0;
							}

						} else if (wanderState == 2) {
							if (position.x < platform.right - 10) {
								moveRight(delta);
							} else {
								wanderState = 0;
							}

							
						}
						
					}
						
				}
				
			} else {
				if (Utils.secondsSince(timeSinceHit) > Constants.enemyFlinchTime) {
					hitState = HitState.NOHIT;
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
			
			//Keep enemy in level boundary
			stayInLevel();
			
			if (health <= 0) {
				level.dropItem(this);
				inactive = true;
				inactiveTimer = TimeUtils.nanoTime();
			}
			
			//calculate the bobbing motion
			sineMovement();

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
					positionYsine = position.y;
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
	
	@Override
	public void render(SpriteBatch batch) {
		Boolean flipx = false;
		
		if (inactive == false) {
			
			//placeholder animations
			if (facing == Facing.LEFT) {
				if (runState == RunState.IDLE) {
					//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
				} else if (runState == RunState.RUN) {
					//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
				}
				
				if (hitState == HitState.IFRAME) {
					//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
					if (Utils.secondsSince(timeSinceHit) > Constants.enemyFlinchTime) {
						hitState = HitState.NOHIT;
					}
				}
				//use for attacking animation
				if (lockState == LockState.LOCK) {
					//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
				}
				
				flipx = false;
				
			} else if (facing == Facing.RIGHT) {
				if (runState == RunState.IDLE) {
					//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
				} else if (runState == RunState.RUN) {
					//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
				}
				
				if (hitState == HitState.IFRAME) {
					//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
					if (Utils.secondsSince(timeSinceHit) > Constants.enemyFlinchTime) {
						hitState = HitState.NOHIT;
					}
				}
				
				if (lockState == LockState.LOCK) {
					//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
				}
				
				flipx = true;
			}
		}
		
		
		batch.setColor(1, 1, 1, alpha);
		batch.draw(region.getTexture(), 
				(position.x - eyeHeight.x), 
				(positionYsine - eyeHeight.y), 
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
		batch.setColor(1, 1, 1, 1);
	}

	void sineMovement() {
		if (jumpState != JumpState.JUMPING || jumpState != JumpState.FALLING) {
			//makes the floating bob up and down
			final float elapsedTime = Utils.secondsSince(startTime);
			//multiplier of amplitude = 1 + sin(2 PI elapsedTime / period)
			final float floatMultiplier = 1 + MathUtils.sin(MathUtils.PI2 * (elapsedTime / Constants.floatpBeetlePeriod));
			positionYsine = position.y + (Constants.floatpBeetleAmplitude * floatMultiplier);
		}

	}
	
	
}
