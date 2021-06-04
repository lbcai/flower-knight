package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.HitState;
import lbcai.util.Enums.JumpState;
import lbcai.util.Enums.LockState;
import lbcai.util.Enums.RunState;

public class EnemyWaspArcher extends EnemyWasp {
	
	//for spawning arrow
	Vector2 bulletPosition;
	
	//for shooting arrow animation
	long shootStartTime;
	
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
			
			if (wanderTime != 0 && wanderTime % 40 == 0 || target.health < 1) {
				goHome = true;
			}
			
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
				
				if (aggroRange.overlaps(target.hitBox) && goHome == false) {
					
					if (moveSpeed != Constants.waspMoveSpeedAggro) {
						//increase speed when chasing
						moveSpeed = Constants.waspMoveSpeedAggro;
					}
					
					//keep the enemy away from melee range of player unless the player corners the enemy against the wall
					//of the level, then allow it and just shoot arrows
					if (position.x > (target.position.x - (3 * hitBox.width)) && 
							position.x < (target.position.x + (3 * hitBox.width)) && 
							(position.x > (level.levelBound.x + (hitBox.width / 2)) && 
									position.x < (level.levelBound.x + level.levelBound.width - (hitBox.width / 2)))) {
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
					}

					//separate from move check above so enemy can move and jump at once
					//if player is a certain vertical distance away, jump or downjump to follow them
					if (target.hitBox.y > (position.y + (hitBox.height * 3))) {
						//spam jump
						startJump();
						
					} else if (target.position.y < (position.y - (hitBox.height * 3))) {
						//downjump if player is below enemy
						downJump();
					}
					
					//if player is leaving the aggro range, follow player
					speedFollow(delta);
					
				} else {
					
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
						
						if (aggroRange.contains(target.hitBox)) {
							goHome = false;
						}
						
					} else {
						//wander ANYWHERE not just on home platform!
						
						if (moveSpeed != Constants.enemyMoveSpeed) {
							//calm down
							moveSpeed = Constants.enemyMoveSpeed;
						}
						
						wanderTime = (long) Utils.secondsSince(startTime);
						
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
			
			//expanded vertical range for archer
			aggroRange = new Rectangle(
					position.x - Constants.aggroRadius.x,
					position.y - Constants.aggroRadius.y,
					2 * Constants.aggroRadius.x,
					2 * Constants.aggroRadius.y);
			
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

	@Override
	public void render(SpriteBatch batch) {
		Boolean flipx = false;
		
		if (inactive == false) {

			//placeholder animations
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
			} else if (lockState == LockState.ATTACK1LOCK) {
				float shootTime = Utils.secondsSince(shootStartTime);
				region = Assets.instance.waspArcherAssets.shootLeftAnim.getKeyFrame(shootTime);
				if (Assets.instance.waspArcherAssets.shootLeftAnim.isAnimationFinished(shootTime)) {
					lockState = LockState.FREE;
				}
			}
			
			//for mobs, just flip the sprites over to save resources instead of generating flipped sprites
			if (facing == Facing.LEFT) {
				flipx = false;
			} else if (facing == Facing.RIGHT) {
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

	
	void shootArrow() {
		if (lockState == LockState.FREE) {
			
			lockState = LockState.ATTACK1LOCK;
			shootStartTime = TimeUtils.nanoTime();
			
			if (target.position.x < position.x) {
				facing = Facing.LEFT;
			} else {
				facing = Facing.RIGHT;
			}
			
			if (facing == Facing.LEFT) {
				bulletPosition = new Vector2(
						position.x - hitBox.width/2,
						position.y);
			} else {
				bulletPosition = new Vector2(
						position.x + hitBox.width/2,
						position.y);
			}
			
			level.spawnBullet(bulletPosition, facing, damage, 1);
		}
		
	}

}
