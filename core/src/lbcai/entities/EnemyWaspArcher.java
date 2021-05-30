package lbcai.entities;

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
import lbcai.util.Enums.RunState;

public class EnemyWaspArcher extends EnemyWasp {
	
	Vector2 bulletPosition;
	
	public EnemyWaspArcher(Platform platform, Level level) {
		super(platform, level);
		bulletPosition = new Vector2();
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
					
					if (position.x > target.position.x - (2 * hitBox.width) && 
							position.x < target.position.x + (2 * hitBox.width)) {
						//if too close to player, move away
						if (target.position.x > position.x) {
							//if player is to the right of archer:
							moveLeft(delta);
						} else if (target.position.x < position.x) {
							//if player is to the left of archer:
							moveRight(delta);
						}
					} else {
						//if player is in aggro range but outside of melee range, shoot arrows at player
						shootArrow();
						
						//if player is leaving the aggro range, follow player
						speedFollow(delta);
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
		
		if (responding == 1) {
			if (moveSpeed != Constants.enemyMoveSpeedAggro) {
				moveSpeed = Constants.enemyMoveSpeedAggro;
			}
			respond(delta, targetPosition);
		}

	}
	
	void shootArrow() {
		if (facing == Facing.LEFT) {
			bulletPosition = new Vector2(
					position.x - hitBox.width/2,
					position.y);
		} else {
			bulletPosition = new Vector2(
					position.x + hitBox.width/2,
					position.y);
		}
		level.spawnBullet(bulletPosition, facing, damage);
	}

}
