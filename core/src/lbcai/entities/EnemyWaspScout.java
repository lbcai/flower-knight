package lbcai.entities;

import java.util.Arrays;
import java.util.List;

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

public class EnemyWaspScout extends EnemyPBeetle {
	
	List<Integer> wanderStateRandomizerVert;
	int wanderStateVert;
	boolean goHome;
	Vector2 homeTracker;
	
	public EnemyWaspScout(Platform platform, Level level) {
		super(platform, level);
		wanderStateRandomizerVert = Arrays.asList(0, 1, 2);
		goHome = false;
		homeTracker = new Vector2(platform.centerX, platform.top);
	}

	@Override
	public void update(float delta) {
		if (inactive == false) {
			//if not dead, do your thing
			lastFramePosition.set(position);
			position.mulAdd(velocity, delta);

			//if not stunned from being hit, do the AI things
			if (hitState != HitState.IFRAME) {
				
				if (aggroRange.overlaps(target.hitBox)) {
					
					if (moveSpeed != Constants.enemyMoveSpeedAggro) {
						//increase speed when chasing
						moveSpeed = Constants.enemyMoveSpeedAggro;
					}
					
					//if enemy position IS NOT close enough to player:
					if (!(target.position.x + (target.hitBox.width) > position.x && 
							target.position.x - (target.hitBox.width) < position.x)) {
						
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
						//if enemy position IS close enough to player, continue moving in direction already moving in
						if (facing == Facing.RIGHT) {
							moveRight(delta);
						} else {
							moveLeft(delta);
						}
					}
					
				} else {
					
					wanderTime = (long) Utils.secondsSince(startTime);
					//no player in sight, random movement
					if (moveSpeed != Constants.enemyMoveSpeed) {
						//calm down
						moveSpeed = Constants.enemyMoveSpeed;
					}
					System.out.println(wanderTime);
					if (wanderTime != 0 && wanderTime % 20 == 0) {
						goHome = true;
					}
					
					System.out.println(wanderTime);
					//return to home platform sometimes
					if (goHome == true) {
						System.out.println("gohome");
						if (!(platform.left < position.x && position.x < platform.right)) {
							float homePlatformCenter = platform.centerX;
							if (position.x > homePlatformCenter) {
								flyLeft();
							} else if (position.x < homePlatformCenter) {
								flyRight();
							}
						}
						
						if (position.y != platform.top + eyeHeight.y) {
							if (platform.top < position.y) {
								flyDown();
							} else if (platform.top > position.y) {
								flyUp();
							}
						}
						
						if ((platform.left < position.x && position.x < platform.right) && (hitBox.contains(homeTracker))) {
							goHome = false;
						}
						
					} else {
						//wander on screen
						//2% chance every update for enemy to change behavior
						if (MathUtils.random() < 0.02) {
							wanderState = (int) (MathUtils.random() * wanderStateRandomizer.size());
						} else if (MathUtils.random() < 0.05) {
							wanderStateVert = (int) (MathUtils.random() * wanderStateRandomizerVert.size());
						}
						
						if (wanderState == 0 && wanderStateVert == 0) {
							runState = RunState.IDLE;
						} else if (wanderState == 1) {
							//move left
							flyLeft();
						} else if (wanderState == 2) {
							//move right
							flyRight();
						}
						
						//do nothing if wanderStateVert == 0
						if (wanderStateVert == 1) {
							//move up
							flyUp();
						} else if (wanderStateVert == 2) {
							//move down
							flyDown();
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
	
	public void flyLeft() {
		facing = Facing.LEFT;
		position.x -= 5;
	}
	
	public void flyRight() {
		facing = Facing.RIGHT;
		position.x += 5;
	}
	
	public void flyDown() {
		position.y -= 2;
	}
	
	public void flyUp() {
		position.y += 3;
	}
}
