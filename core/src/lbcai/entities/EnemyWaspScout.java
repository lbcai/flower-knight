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
	int getAbovePlayer;
	
	public EnemyWaspScout(Platform platform, Level level) {
		super(platform, level);
		wanderStateRandomizerVert = Arrays.asList(0, 1, 2);
		goHome = false;
		homeTracker = new Vector2(platform.centerX, platform.top + eyeHeight.y);
		getAbovePlayer = 0;
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
					
					//the first thing the wasp scout should do when it sees a player is attempt to call other wasps for 
					//backup. then, it will keep the player in sight but avoid going into attack range.
					
					if (moveSpeed != Constants.waspMoveSpeedAggro) {
						//increase speed when chasing. wasp scout aggro move speed is faster than other enemies
						moveSpeed = Constants.waspMoveSpeedAggro;
					}
					
					//call other wasps
					
					//keep the top or bottom of the aggro range box inside the player hitbox depending on whether the player
					//is above or below the wasp scout. and since the aggro range box is taller than it is wide, if the wasp
					//comes in aggro box width/2 of the player's position, back out. this will help it stay safe
					//also keep the player within the width of the aggro box
					
					//requires testing when wasp is boxed against level bounds; how can wasp track player even while jump?
					if (target.position.y >= position.y) {
						
						//check if wasp is being pushed into lowest platform on map, where it is unreachable, if so swap to
						//flying above player
						if (position.y < level.lowestTop && getAbovePlayer == 0) {
							if (getAbovePlayer == 0) {
								getAbovePlayer = 1;
							}
						}

						//if the target is above wasp EXCEPT when the wasp is under the lowest platform in the map
						if (getAbovePlayer == 0) {
							if (aggroRange.y + aggroRange.height > target.position.y) {
								flyDown(delta);
							} else if (aggroRange.y + aggroRange.height < target.position.y - 20) {
								flyUp(delta);
							}
						} else if (getAbovePlayer == 1) {
							//get above player if pushed below map
							flyUp(delta);
						}
						
					} else if (target.position.y < position.y) {
						//reset pushed below map counter
						if (getAbovePlayer == 1) {
							getAbovePlayer = 0;
						}
						//if the target is below wasp
						if (aggroRange.y < target.position.y - 20) {
							flyUp(delta);
						} else if (aggroRange.y > target.position.y) {
							flyDown(delta);
						}
					}
					
					if (target.position.x > aggroRange.x + aggroRange.width) {
						//if the target position is outside of the aggro box but the wasp can still see the player, get the
						//player back into the box before they run off
						moveRight(delta);
					} else if (target.position.x < aggroRange.x) {
						moveLeft(delta);
					}
					
				} else {
					
					wanderTime = (long) Utils.secondsSince(startTime);
					//no player in sight, random movement
					if (moveSpeed != Constants.enemyMoveSpeed) {
						//calm down
						moveSpeed = Constants.enemyMoveSpeed;
					}

					if (wanderTime != 0 && wanderTime % 40 == 0) {
						goHome = true;
					}
					
					System.out.println(wanderTime + " " + homeTracker + " " + position);
					//return to home platform sometimes
					if (goHome == true) {

						if (!hitBox.contains(homeTracker)) {
							
							if (position.x > platform.centerX + 10) {
								moveLeft(delta);
							} else if (position.x < platform.centerX - 10) {
								moveRight(delta);
							} else if (position.x < platform.centerX + 10 && position.x > platform.centerX - 10) {
								//do nothing
							}
							
							if (platform.top + eyeHeight.y < position.y) {
								flyDown(delta);
							} else if (platform.top + eyeHeight.y > position.y) {
								flyUp(delta);
							}
						}

						if (hitBox.contains(homeTracker)) {
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
							moveLeft(delta);
						} else if (wanderState == 2) {
							//move right
							moveRight(delta);
						}
						
						//do nothing if wanderStateVert == 0
						if (wanderStateVert == 1) {
							//move up
							flyUp(delta);
						} else if (wanderStateVert == 2) {
							//move down
							flyDown(delta);
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
			
			//wasp scout should remain visible to player...probably should make aggroRange based on window size
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
	}
	
	public void flyDown(float delta) {
		position.y -= delta * moveSpeed;
	}
	
	public void flyUp(float delta) {
		position.y += delta * moveSpeed;
	}
}
