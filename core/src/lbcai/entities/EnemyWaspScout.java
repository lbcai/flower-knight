package lbcai.entities;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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

public class EnemyWaspScout extends EnemyWasp {
	
	//normal wanderstate values randomize left and right behavior, but this enemy can fly in all directions. it also needs
	//a randomized wanderstate value for up and down directions.
	List<Integer> wanderStateRandomizerVert;
	//this is the randomized wanderstate value. normal horizontal wanderstate value is already included in superclasses.
	int wanderStateVert;
	
	//used if the player is above the enemy but the enemy is being pushed down into the floor and there is no space for it
	int getAbovePlayer;
	
	//limit the calls a wasp scout can make to 1
	int calledWasps;
	
	//used to calc dive attack times and animations
	long diveStartTime;
	//used so the wasp knows which direction above or below the player to return to after diving to the player
	int diveDirection;
	//used so the wasp knows whether it's diving towards the player or returning to safety
	int parabolaStage = 0;
	//used to mark the target location of the dive attack even if player moves away
	Vector2 diveGoalPosition = new Vector2();
	
	public EnemyWaspScout(Platform platform, Level level) {
		super(platform, level);
		wanderStateRandomizerVert = Arrays.asList(0, 1, 2);
		homeTracker = new Vector2(platform.centerX, platform.top + eyeHeight.y);
		getAbovePlayer = 0;
		calledWasps = 0;
		
		//this enemy does no damage.
		touchDmg = false;
		
		level.getRenderables().add(this);
	}

	@Override
	public void update(float delta) {
		if (inactive == false) {
			//if not dead, do your thing
			lastFramePosition.set(position);
			position.mulAdd(velocity, delta);
			
			if ((wanderTime != 0 && wanderTime % 40 == 0) || target.health < 1) {
				goHome = true;
				if (calledWasps == 1) {
					//reset the ability to call wasps if the wasp has wandered around for a while without seeing
					//the player, or if the player is killed
					calledWasps = 0;
				}
			}
			
			if (lockState == LockState.ATTACK1LOCK) {
				dive(delta);
			} else {
				//if not stunned from being hit, do the AI things
				if (hitState != HitState.IFRAME) {
					
					if (aggroRange.overlaps(target.hitBox) && goHome == false) {
						
						//the first thing the wasp scout should do when it sees a player is attempt to call other wasps for 
						//backup. then, it will keep the player in sight but avoid going into attack range.
						
						if (moveSpeed != Constants.waspMoveSpeedAggro) {
							//increase speed when chasing. wasp scout aggro move speed is faster than other enemies
							moveSpeed = Constants.waspMoveSpeedAggro;
						}
						
						//call other wasps
						if (calledWasps == 0) {
							callWasps(delta, target.position);
							calledWasps = 1;
						}
						
						//keep the top or bottom of the aggro range box inside the player hitbox depending on whether the player
						//is above or below the wasp scout. 
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
								} else if (aggroRange.y + aggroRange.height < target.hitBox.y) {
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
							if (aggroRange.y < target.hitBox.y) {
								flyUp(delta);
							} else if (aggroRange.y > target.position.y) {
								flyDown(delta);
							}
						}
						
						//make wasp move around back and forth instead of freezing in idle when there is nothing else to do
						chaseRandomness(delta);
						
						speedFollow(delta);
						
						//check if any other wasp enemies in aggro box sight range. if so, allow scout to do an annoying knockback
						//attack on the player
						for (Enemy enemy : level.getEnemies()) {
							if (enemy.enemyType == EnemyType.WASP && enemy != this) {
								if (aggroRange.overlaps(enemy.hitBox) && enemy.inactive == false) {
									if (Math.abs(position.x - target.position.x) < (hitBox.width * 2)) {
										dive(delta);
									}
									
								}
							}
						}
						
					} else {
						
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
								
								if (platform.top + eyeHeight.y + 5 < position.y) {
									flyDown(delta);
								} else if (platform.top + eyeHeight.y - 5 > position.y) {
									flyUp(delta);
								}
							} else {
								goHome = false;
							}

							if (aggroRange.contains(target.hitBox)) {
								goHome = false;
							}
							
						} else {
							
							wanderTime = (long) Utils.secondsSince(startTime);
							//no player in sight, random movement
							if (moveSpeed != Constants.enemyMoveSpeed) {
								//calm down
								moveSpeed = Constants.enemyMoveSpeed;
							}
							
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
								if (position.y > level.lowestTop) {
									//do not allow the scout to fly below the level
									flyDown(delta);
								}
								
							}
						}

					}
					
				} else {
					if (Utils.secondsSince(timeSinceHit) > Constants.enemyFlinchTime) {
						hitState = HitState.NOHIT;
					}
				}
			}
			
			

			hitBox = new Rectangle(
					position.x - collisionRadius.x,
					position.y - collisionRadius.y,
					2 * collisionRadius.x,
					2 * collisionRadius.y);

			//wasp scout should remain visible to player...probably should make aggroRange based on window size
			//unfortunately this has the downside of giving the wasp more aggro range when the player extends the window size
			//this allows the player to increase the distance between themselves and the wasp artificially.
			//as a result, decided to set the wasp's aggro range as the default world size, larger resolutions will not 
			//receive a benefit
			aggroRange = new Rectangle(
					position.x - Constants.defaultWorldWidth / 2,
					position.y - Constants.defaultWorldHeight / 2,
					Constants.defaultWorldWidth,
					Constants.defaultWorldHeight);
			
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
			
			//determine if hitting player
			detectHitPlayer(target);

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
					getAbovePlayer = 0;
					calledWasps = 0;
					inactive = false;
					alpha = 255f/255f;
				}
			}
		}

	}
	
	public void flyDown(float delta) {
		//added increase to vertical flight speed for wasp scout so player cannot fall out of aggroRange so easily
		position.y -= delta * moveSpeed * 2;
	}
	
	public void flyUp(float delta) {
		position.y += delta * moveSpeed * 2;
	}
	
	void callWasps(float delta, Vector2 targetPosition) {
		//need to give the wasp scout a calling wasps state to play a special animation and lock it in place
		for (Enemy enemy : level.getEnemies()) {
			if (enemy.enemyType == EnemyType.WASP) {
				EnemyWasp wasp = (EnemyWasp) enemy;
				wasp.respondToCall(delta, targetPosition);
			}
		}
	}
	
	@Override
	void respondToCall(float delta, Vector2 targetPosition) {
		//for scouts only. this code allows scout to fly up/down while moving left/right to target location
		//non-scout ground units are more limited in their movement options
		
		if (!((targetPosition.x - (2 * hitBox.width)) < position.x && position.x < (targetPosition.x + (2 * hitBox.width)))) {
			if (position.x > targetPosition.x) {
				moveLeft(delta);
			} else if (position.x < targetPosition.x) {
				moveRight(delta);
			}
		} 
		
		if (position.y != targetPosition.y + eyeHeight.y) {
			if (targetPosition.y + eyeHeight.y < position.y) {
				flyDown(delta);
			} else if (targetPosition.y + eyeHeight.y > position.y) {
				flyUp(delta);
			}
		} 
	}
	
	@Override
	//help keep the player inside the aggro range box when player is on the edge
	void speedFollow(float delta) {
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
	}
	
	void dive(float delta) {
		//dive at player in a V-shape
		
		if (lockState == LockState.FREE) {
			
			lockState = LockState.ATTACK1LOCK;
			diveStartTime = TimeUtils.nanoTime();
			
			//get the target's position at the start of the dive and place it in diveGoalPosition
			target.hitBox.getCenter(diveGoalPosition);
			
			//depending on if target is to left or right of wasp, change facing direction to look natural
			if (diveGoalPosition.x - position.x > 0) {
				facing = Facing.RIGHT;
			} else {
				facing = Facing.LEFT;
			}
			
			if (diveGoalPosition.y - position.y > 0) {
				//the wasp began below the player.
				diveDirection = 0;
			} else {
				//the wasp began above the player.
				diveDirection = 1;
			}
			
		} else if (lockState == LockState.ATTACK1LOCK) {

			if (parabolaStage == 0) {
				//initial dive from safe position to player
				//set boolean knockback to true so if dive successfully hits player, player is knocked back
				if (knockback == false) {
					knockback = true;
				}
				//the actual dive
				position.lerp(diveGoalPosition, 0.1f);
				
				if (hitBox.contains(diveGoalPosition)) {
					parabolaStage = 1;
					knockback = false;
				}
				
			} else if (parabolaStage == 1) {
				//secondary dive from player to a position safe from player; if the wasp dove from below, return below. if it
				//dove from above, return above.
				if (diveDirection == 0) {
					//below
					if (facing == Facing.RIGHT) {
						position.lerp(new Vector2((target.position.x + (hitBox.width * 2)), (target.position.y - (aggroRange.height / 2))), 0.1f);
					} else {
						position.lerp(new Vector2((target.position.x - (hitBox.width * 2)), (target.position.y - (aggroRange.height / 2))), 0.1f);
					}
					if (position.y - 20 <= (target.position.y - (aggroRange.height / 2))) {
						lockState = LockState.FREE;
						parabolaStage = 0;
					}
				} else if (diveDirection == 1) {
					//above
					if (facing == Facing.RIGHT) {
						position.lerp(new Vector2((target.position.x + (hitBox.width * 2)), (target.position.y + (aggroRange.height / 2))), 0.1f);
					} else {
						position.lerp(new Vector2((target.position.x - (hitBox.width * 2)), (target.position.y + (aggroRange.height / 2))), 0.1f);
					}
					if (position.y + 20 >= (target.position.y + (aggroRange.height / 2))) {
						lockState = LockState.FREE;
						parabolaStage = 0;
					}
				}
				
			}
			
			

		}
		
	}
	
}
