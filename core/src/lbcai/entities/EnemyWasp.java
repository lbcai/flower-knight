package lbcai.entities;

import java.util.Arrays;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.EnemyType;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.HitState;
import lbcai.util.Enums.JumpState;
import lbcai.util.Enums.LockState;
import lbcai.util.Enums.RunState;

public abstract class EnemyWasp extends Enemy {

	//occasionally push the enemy to return to the home platform.
	boolean goHome;
	Vector2 homeTracker;
	
	//counter to put the wasp in the 'traveling to location of call' state
	int responding;
	Vector2 targetPosition;
	
	//actual position.y is represented by position.y BUT in order to make the beetle bob up and down, display the sprites
	//using positionYsine, an adjusted position.y 
	protected float positionYsine;
	
	public EnemyWasp(Platform platform, Level level) {
		super(platform, level);
		goHome = false;
		homeTracker = new Vector2(platform.centerX, platform.top + eyeHeight.y);
		enemyType = EnemyType.WASP;
		responding = 0;
		targetPosition = new Vector2();
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
					
					speedFollow(delta);
					
				} else {

					if (moveSpeed != Constants.enemyMoveSpeed) {
						//calm down
						moveSpeed = Constants.enemyMoveSpeed;
					}
					
					wanderTime = (long) Utils.secondsSince(startTime);
					
					if (wanderTime != 0 && wanderTime % 40 == 0) {
						goHome = true;
					}
					
					//imagine the player leaves the aggro range. get enemy back to home platform, then if on home platform, run
					//idle/wander ai. avoid having holes in the map with this method
					if (goHome == true) {
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
							goHome = false;
						}
					} else {
						//wander ANYWHERE not just on home platform!
						
						//1% chance every update for enemy to change behavior
						if (MathUtils.random() < 0.02) {
							wanderState = (int) (MathUtils.random() * wanderStateRandomizer.size());
						}
						
						if (wanderState == 0) {
							runState = RunState.IDLE;
						} else if (wanderState == 1) {
							moveLeft(delta);
						} else if (wanderState == 2) {
							moveRight(delta);
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
	
	void respondToCall(float delta, Vector2 targetPosition) {
		this.targetPosition = targetPosition;
		responding = 1;
	}
	
	void respond(float delta, Vector2 targetPosition) {
		//for non-scouts only. (grounded wasp units)
		if (!((targetPosition.x - hitBox.width < position.x) && (position.x < targetPosition.x + hitBox.width))) {
			if (position.x > targetPosition.x) {
				moveLeft(delta);
			} else if (position.x < targetPosition.x) {
				moveRight(delta);
			}
		} else if (!((position.y + eyeHeight.y > targetPosition.y - hitBox.height) && 
				(position.y + eyeHeight.y < targetPosition.y + hitBox.height))) {
			if (targetPosition.y < position.y) {
				if (jumpState == JumpState.GROUNDED) {
					downJump();
				} else if (jumpState == JumpState.JUMPING) {
					startJump();
				}
			} else if (targetPosition.y > position.y) {
				velocity.y += Constants.jumpSpeed;
				jumpState = JumpState.JUMPING;
			}
		} 
		
		if (aggroRange.overlaps(target.hitBox)) {
			responding = 0;
			if (moveSpeed != Constants.enemyMoveSpeed) {
				moveSpeed = Constants.enemyMoveSpeed;
			}
		}
				
	}
	
	//help keep the player inside the aggro range box when player is on the edge
	void speedFollow(float delta) {
		//horizontal only
		if (target.hitBox.x + target.hitBox.width > aggroRange.x + aggroRange.width) {
			//if the target position is outside of the aggro box but the wasp can still see the player, get the
			//player back into the box before they run off
			moveRight(delta);
			//if player travels fast enough exiting the aggroRange, put on a burst of speed to hopefully catch up
			//check player velocity when dodging; turns out there is none because player is lerped for dodge
			if (target.hitState == HitState.DODGE) {
				Utils.lerpX(position, new Vector2(position.x + 400, position.y), 0.1f);
			}
		} else if (target.hitBox.x < aggroRange.x) {
			moveLeft(delta);
			if (target.hitState == HitState.DODGE) {
				Utils.lerpX(position, new Vector2(position.x - 400, position.y), 0.1f);
			}
		}
		//vertical only
		if (target.hitBox.y + target.hitBox.height > aggroRange.y + aggroRange.height) {
			startJump();
		} else if (target.hitBox.y < aggroRange.y) {
			downJump();
		}
	}
	
	void harvestFlower() {
		//give the wasps interesting idle behaviors: in this case if a working class wasp sees a flower monster, it will harvest
		//pollen and heal from looting 
		
	}
	
}
