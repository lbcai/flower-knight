package lbcai.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.HitState;
import lbcai.util.Enums.JumpState;
import lbcai.util.Enums.LockState;
import lbcai.util.Enums.RunState;

public class Player {

	public final static String className = Player.class.getName();
	
	//use to compare against current position and check if player should land or fall off platforms.
	Vector2 lastFramePosition;
	Vector2 spawnLocation;
	Vector2 velocity;
	public Vector2 position;
	//see enum below
	public Facing facing;
	JumpState jumpState;
	public RunState runState;
	HitState hitState;
	LockState lockState;
	//a long is like a 64-bit int. a BIG int.
	long jumpStartTime;
	long runStartTime;
	long idleStartTime;
	long wallStartTime;

	Level level;
	private int jumpCounter = 0;

	long timeSinceHit;
	private int idleTransitionCounter = 0;
	private int flashCounter = 0;
	private long idleTransStartTime;
	private long slideTransStartTime;
	private TextureRegion region;
	
	/**
	 * Constructor: make a player.
	 */
	public Player(Vector2 spawnLocation, Level level) {
		
		this.spawnLocation = spawnLocation;
		//the current level we are in
		this.level = level;
		position = new Vector2();
		lastFramePosition = new Vector2();
		velocity = new Vector2();
		init();

	}
	
	/**
	 * Initialize the player, i.e. on respawn.
	 */
	public void init() {
		position.set(spawnLocation);
		lastFramePosition.set(spawnLocation);
		velocity.setZero();
		facing = Facing.RIGHT;
		jumpState = JumpState.FALLING;
		runState = RunState.IDLE;
		hitState = HitState.NOHIT;
		lockState = LockState.FREE;
		idleStartTime = TimeUtils.nanoTime();
		//Initialize the region to display.
		region = Assets.instance.playerAssets.idleRightAnim.getKeyFrame(0);
	}
	
	/**
	 * Input our texture using the texture region and draw it on the screen. See below for full input arguments:
	 * void draw(Texture texture,
          float x,
          float y,
          float originX,
          float originY,
          float width,
          float height,
          float scaleX,
          float scaleY,
          float rotation,
          int srcX,
          int srcY,
          int srcWidth,
          int srcHeight,
          boolean flipX,
          boolean flipY)
     * Rectangle offset by originX/Y relative to origin. Scaled around originX/Y. Rotation is counterclockwise around originX/Y.
     * Will display the texture dictated by src values (texel values). Flip determines if the texture is mirrored along the axis.
	 * @param batch SpriteBatch from GameplayScreen
	 */
	public void render(SpriteBatch batch) {

		//Change the display depending on the direction the character faces.
		if (facing == Facing.RIGHT) {
			if (jumpState == JumpState.GROUNDED) {
				if (runState == RunState.IDLE) {
					float idleTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - idleStartTime);
					//use idle transition counter (set when idle begins to 0) to determine what idle state we are in:
					//0 = catch breath idle, 1 = transition animation, 2 = normal idle
					if (MathUtils.nanoToSec * (TimeUtils.nanoTime() - timeSinceHit) < Constants.idleBTime) {
						region = Assets.instance.playerAssets.idleBRightAnim.getKeyFrame(idleTime);
						idleTransitionCounter = 1;
						idleTransStartTime = TimeUtils.nanoTime();
					} else if (idleTransitionCounter == 0) {
						idleTransitionCounter = 2;
					} else if (idleTransitionCounter == 1) {
						float idleTransTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - idleTransStartTime);
						region = Assets.instance.playerAssets.idleBTransRightAnim.getKeyFrame(idleTransTime);
						if (Assets.instance.playerAssets.idleBTransRightAnim.isAnimationFinished(idleTransTime)) {
							idleTransitionCounter = 2;
						}
					} else if (idleTransitionCounter == 2) {
						region = Assets.instance.playerAssets.idleRightAnim.getKeyFrame(idleTime);
					}
					
				} else if (runState == RunState.RUN) {
					//Calculate how long we have been running in seconds (nanoToSec just converts to seconds, 
					//nanoTime gets current time).
					float runTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - runStartTime);
					region = Assets.instance.playerAssets.runRightAnim.getKeyFrame(runTime);
				} else if (runState == RunState.SKID) {
					float skidTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - slideTransStartTime);
					region = Assets.instance.playerAssets.skidRightAnim.getKeyFrame(skidTime);
				} 
			} else if (jumpState == JumpState.JUMPING) {
				float jumpTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - jumpStartTime);
				region = Assets.instance.playerAssets.jumpRightAnim.getKeyFrame(jumpTime);
			} else if (jumpState == JumpState.FALLING) {
				region = Assets.instance.playerAssets.jumpRightAnim.getKeyFrame(12);
			} else if (jumpState == JumpState.WALL) {
				float hangTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - wallStartTime);
				region = Assets.instance.playerAssets.hangRightAnim.getKeyFrame(hangTime);
			}
			
			if (hitState == HitState.IFRAME) {
				if (flashCounter == 0 || flashCounter % 5 != 0) {
					flashCounter += 1;
				} else if (flashCounter % 5 == 0) {
					batch.setColor(183f/255f, 183f/255f, 183f/255f, 255f/255f);
					flashCounter = 0;
				}
				
				if (lockState == LockState.LOCK) {
					region = Assets.instance.playerAssets.jumpRightAnim.getKeyFrame(12);
				}
			}
			
		} else if (facing == Facing.LEFT) {
			if (jumpState == JumpState.GROUNDED) {
				if (runState == RunState.IDLE) {
					float idleTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - idleStartTime);
					//use idle transition counter to figure out when to play transition animation between combat & normal idles
					if (MathUtils.nanoToSec * (TimeUtils.nanoTime() - timeSinceHit) < Constants.idleBTime) {
						region = Assets.instance.playerAssets.idleBLeftAnim.getKeyFrame(idleTime);
						idleTransitionCounter = 1;
						idleTransStartTime = TimeUtils.nanoTime();
					} else if (idleTransitionCounter == 0) {
						idleTransitionCounter = 2;
					} else if (idleTransitionCounter == 1) {
						float idleTransTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - idleTransStartTime);
						region = Assets.instance.playerAssets.idleBTransLeftAnim.getKeyFrame(idleTransTime);
						if (Assets.instance.playerAssets.idleBTransLeftAnim.isAnimationFinished(idleTransTime)) {
							idleTransitionCounter = 2;
						}
					} else if (idleTransitionCounter == 2) {
						region = Assets.instance.playerAssets.idleLeftAnim.getKeyFrame(idleTime);
					}

				} else if (runState == RunState.RUN) {
					float runTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - runStartTime);
					region = Assets.instance.playerAssets.runLeftAnim.getKeyFrame(runTime);
				} else if (runState == RunState.SKID) {
					float skidTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - slideTransStartTime);
					region = Assets.instance.playerAssets.skidLeftAnim.getKeyFrame(skidTime);
				}
				
				
			} else if (jumpState == JumpState.JUMPING) {
				float jumpTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - jumpStartTime);
				region = Assets.instance.playerAssets.jumpLeftAnim.getKeyFrame(jumpTime);
			} else if (jumpState == JumpState.FALLING) {
				region = Assets.instance.playerAssets.jumpLeftAnim.getKeyFrame(12);
			} else if (jumpState == JumpState.WALL) {
				float hangTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - wallStartTime);
				region = Assets.instance.playerAssets.hangLeftAnim.getKeyFrame(hangTime);
			}
			
			if (hitState == HitState.IFRAME) {
				if (flashCounter == 0 || flashCounter % 5 != 0) {
					flashCounter += 1;
				} else if (flashCounter % 5 == 0) {
					batch.setColor(183f/255f, 183f/255f, 183f/255f, 255f/255f);
					flashCounter = 0;
				}
				
				if (lockState == LockState.LOCK) {
					region = Assets.instance.playerAssets.jumpLeftAnim.getKeyFrame(12);
				}
				
			}
			
		}
		
		
		//Actually draw the sprites.
		batch.draw(region.getTexture(), 
				(position.x - Constants.playerHead.x), 
				(position.y - Constants.playerHead.y), 
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
				false, 
				false);
		batch.setColor(1, 1, 1, 1);
	}
	
	/**
	 * Update player based on input from controller.
	 * 
	 * @param delta the time in seconds so we avoid framerate problems and changing the passing of time on different devices.
	 * 				Usually in some fraction of a second. This actually represents the time since last frame.
	 */
	public void update(float delta, Array<Platform> platforms) {
		//Use position vector to set last frame position.
		lastFramePosition.set(position);

		//Subtract delta * gravity from player velocity to make player accelerate downwards the longer they are in air.
		//Update is called every frame (delta). This means every frame the velocity is affected in a downwards motion.
		if (jumpState != JumpState.WALL) {
			velocity.y -= delta * Constants.worldGravity;
			//Multiply vector velocity by scalar delta (time) and add to position vector. Changes position of player based on time and
			//gravity. Causes player to fall. Scaled by seconds (delta) to avoid the framerate problem.
			position.mulAdd(velocity, delta);
		}

		
		//Enter falling state if dropping
		if (velocity.y < 0) {
			jumpState = JumpState.FALLING;
		}
		
		//Return player to spawn if they fall off the map. Currently doesn't deduct lives or anything.
		if (position.y < Constants.killPlane) {
			init();
		}
		
		//Use for collision detection of player.
		Rectangle playerBound = new Rectangle(
				position.x - Constants.playerStance / 2,
				position.y - Constants.playerEyeHeight + 40, 
				Constants.playerStance,
				Constants.playerHeight - 20);
		
		//Detect if landed on a platform. Must have this after vertical velocity and jump state setting code above, or else
		//weird behavior with jumping happens. Note that order in Java DOES matter!
		//Also collision detection with sides of platform.
		for (Platform platform : platforms) {

			if (landOnPlatform(platform)) {
				jumpState = JumpState.GROUNDED;
				position.y = platform.top + Constants.playerEyeHeight;
				velocity.x = 0;
				velocity.y = 0;
				if (jumpCounter >= 2) {
					jumpCounter = 0;
				}
				//prevents player from skidding off platforms (for convenience)
				if (runState == RunState.SKID) {
					if (position.x > platform.right) {
						position.x = platform.right;
					} else if (position.x < platform.left) {
						position.x = platform.left;
					}
				}
				
			}
			
			//allows sticking to walls and walljumping
			if (jumpState == JumpState.JUMPING || jumpState == JumpState.FALLING || jumpState == JumpState.WALL) {
				
				if (Gdx.input.isKeyPressed(Keys.LEFT)) {
					
					if (stickToPlatformRight(platform)) {
						if (jumpState != JumpState.WALL) {
							wallStartTime = TimeUtils.nanoTime();
							jumpState = JumpState.WALL;
							jumpCounter = 0;
						}

						
						if ((MathUtils.nanoToSec * (TimeUtils.nanoTime() - wallStartTime)) < Constants.wallTime) {
							position.x = platform.right + Constants.playerStance / 2;
							velocity.x = 0;
							velocity.y = 0;
						} 
						
						if (Gdx.input.isKeyPressed(Keys.LEFT) && (Gdx.input.isKeyPressed(Keys.ALT_LEFT))) {
							position.x = (platform.right + Constants.playerStance / 2) - 10;
							startJump();
						}
						
					}
				} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
					
					if (stickToPlatformLeft(platform)) {
						if (jumpState != JumpState.WALL) {
							wallStartTime = TimeUtils.nanoTime();
							jumpState = JumpState.WALL;
							jumpCounter = 0;
						}


						if ((MathUtils.nanoToSec * (TimeUtils.nanoTime() - wallStartTime)) < Constants.wallTime) {
							position.x = platform.left - Constants.playerStance / 2;
							velocity.x = 0;
							velocity.y = 0;
						} 
						
						if (Gdx.input.isKeyPressed(Keys.RIGHT) && (Gdx.input.isKeyPressed(Keys.ALT_LEFT))) {
							position.x = (platform.left - Constants.playerStance / 2) + 10;
							startJump();
						}

					}
				} else {
					//makes the player fall off the wall if not actively trying to stick
					if (jumpState == JumpState.WALL) {
						jumpState = JumpState.FALLING;
					}
				}
			}
		}
		
		
		if (lockState == LockState.LOCK) {
			if (MathUtils.nanoToSec * (TimeUtils.nanoTime() - timeSinceHit) > Constants.animLockTime) {
				lockState = LockState.FREE;
			}
		} else {
			//check if invincible grace period is over.
			if (MathUtils.nanoToSec * (TimeUtils.nanoTime() - timeSinceHit) > Constants.iFrameLength) {
				hitState = HitState.NOHIT;
			}
		}
		
		//Collision detection with enemies, includes the direction the hit is coming from. Must go after platform checking code.
		for (Enemy enemy : level.getEnemies()) {
			//have to make new rectangle because enemies move (bottom left x, bottom left y, width, height)
			Rectangle enemyBound = new Rectangle(
					enemy.position.x - enemy.collisionRadius,
					enemy.position.y - enemy.collisionRadius,
					2 * enemy.collisionRadius,
					2 * enemy.collisionRadius);
			if (playerBound.overlaps(enemyBound)) {
				if (position.x < enemy.position.x && hitState == HitState.NOHIT) {
					velocity.x = 0;
					flinch(Facing.LEFT);
				} else if (position.x > enemy.position.x && hitState == HitState.NOHIT) {
					velocity.x = 0;
					flinch(Facing.RIGHT);
				}
			}
			
			if (Gdx.input.isKeyJustPressed(Keys.X)) {
				if (facing == Facing.LEFT) {
					Rectangle attack = new Rectangle(
						position.x - (Constants.playerStance / 2) - Constants.attackRange.x,
						position.y - Constants.playerEyeHeight,
						Constants.attackRange.x,
						Constants.attackRange.y);
					if (attack.overlaps(enemyBound)) {
						enemy.HP -= Constants.playerBaseDamage; 
					}
				} else {
					Rectangle attack = new Rectangle(
						position.x + (Constants.playerStance / 2) + Constants.attackRange.x,
						position.y - Constants.playerEyeHeight,
						Constants.attackRange.x,
						Constants.attackRange.y);
					if (attack.overlaps(enemyBound)) {
						enemy.HP -= Constants.playerBaseDamage; 
					}
				}

			}
		}
		
		//check if projectiles are hitting player
		for (Bullet bullet : level.getBullets()) {
			//(bottom left x, bottom left y, width, height)
			Rectangle bulletBound = new Rectangle(
					bullet.position.x - Constants.bulletCenter.x,
					bullet.position.y - Constants.bulletCenter.y,
					2 * Constants.bulletCenter.x,
					2 * Constants.bulletCenter.y);
			//if player comes in contact with bullet, stop player, make flinch, destroy bullet
			if (playerBound.overlaps(bulletBound)) {
				if (position.x < bullet.position.x && hitState == HitState.NOHIT) {
					velocity.x = 0;
					flinch(Facing.LEFT);
					bullet.active = false;
				} else if (position.x > bullet.position.x && hitState == HitState.NOHIT) {
					velocity.x = 0;
					flinch(Facing.RIGHT);
					bullet.active = false;
				}
			}
		}
		
		
		//Prevent player from falling through ground. Player will start falling and hit ground and stay there.
		//if (position.y - Constants.playerEyeHeight < 0) {
		//	jumpState = JumpState.GROUNDED;
		//	position.y = Constants.playerEyeHeight;
		//	velocity.y = 0;
		//}

		//run (unavailable while flinching or otherwise iframe animation-locked, which is a shorter period of time than
		//the actual invincible time)
		if (lockState != LockState.LOCK && jumpState != JumpState.WALL) {
			if (Gdx.input.isKeyPressed(Keys.LEFT) && !Gdx.input.isKeyPressed(Keys.RIGHT)) {
				if (runState == RunState.SKID) {
					runState = RunState.RUN;
				}
				moveLeft(delta);
				
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT) && !Gdx.input.isKeyPressed(Keys.LEFT)) {
				if (runState == RunState.SKID) {
					runState = RunState.RUN;
				}
				moveRight(delta);
			} else {
				if (runState == RunState.RUN && jumpState == JumpState.GROUNDED) {
					//only slide to a stop if player has been running for long enough time
					if (MathUtils.nanoToSec * (TimeUtils.nanoTime() - runStartTime) > Constants.skidTimeLimitBreak &&
							velocity.y == 0) {
						slideTransStartTime = TimeUtils.nanoTime();
						runState = RunState.SKID;
					} else {
						idleStartTime = TimeUtils.nanoTime();
						//to keep track of when transition animation should be played for idle
						idleTransitionCounter = 0;
						runState = RunState.IDLE;
					}

				}
				if (runState == RunState.SKID && jumpState == JumpState.GROUNDED) {
					if (MathUtils.nanoToSec * (TimeUtils.nanoTime() - slideTransStartTime) < Constants.skidTime) {
						if (facing == Facing.LEFT) {
							moveLeft(delta);
						} else {
							moveRight(delta);
						}

					} else {
						idleStartTime = TimeUtils.nanoTime();
						//to keep track of when transition animation should be played for idle
						idleTransitionCounter = 0;
						runState = RunState.IDLE;
					}
				}

			}
		}
		
		
		
		//jump (apparently you can do this case thing with enums!) use isKeyJustPressed to avoid continuous input multi-jump
		if (Gdx.input.isKeyJustPressed(Keys.ALT_LEFT) && !Gdx.input.isKeyPressed(Keys.DOWN)) {
			switch (jumpState) {
			case GROUNDED:
				startJump();
				break;

			case JUMPING:
				if (jumpCounter < 2) {
					startJump();
					break;
				} 

			case FALLING:
				if (jumpCounter < 2) {
					startJump();
					break;
				} 
			case WALL:
				if (jumpCounter < 2)
					startJump();
					break;
				}
		
		} else if (Gdx.input.isKeyJustPressed(Keys.ALT_LEFT) && Gdx.input.isKeyPressed(Keys.DOWN)) {
			//downjump
			//isKeyJustPressed prevent player from being able to doublejump straight onto the platform they 
			//downjumped off on accident
			jumpState = JumpState.FALLING;
			position.y -= 10; 
		}

	}
	
	/**
	 * Actual controller for player. Also updates player state, i.e. facing direction, jump state.
	 * 
	 * Moving left/right is simple enough: change the position in the appropriate direction and change the facing to the right
	 * direction. The facing will be used to figure out which sprite direction set to use for animations.
	 * 
	 * Jumping involves a jumpState: GROUNDED, JUMPING, and FALLING. See comments below. Platform detection & interaction 
	 * is also here.
	 * 
	 * @param delta time in seconds, avoids framerate issues
	 */
	private void moveLeft(float delta) {
		//At the beginning of movement, if we are running, save the time as the run start time.
		if (jumpState == JumpState.GROUNDED && runState != RunState.RUN) {
			runStartTime = TimeUtils.nanoTime();
		}
		if (runState != RunState.SKID) {
			runState = RunState.RUN;
		}
		facing = Facing.LEFT;
		velocity.x = -delta * Constants.moveSpeed;
	}
	
	private void moveRight(float delta) {
		if (jumpState == JumpState.GROUNDED && runState != RunState.RUN) {
			runStartTime = TimeUtils.nanoTime();
		}
		if (runState != RunState.SKID) {
			runState = RunState.RUN;
		}
		facing = Facing.RIGHT;
		velocity.x = delta * Constants.moveSpeed;
	}
	
	private void startJump() {
		jumpState = JumpState.JUMPING;
		//Get current time (of starting jump).
		jumpStartTime = TimeUtils.nanoTime();
		jumpCounter += 1;
		continueJump();
		
	}
	
	//This is added to allow player to doublejump under certain conditions (falling, and not exceeding 2 jumps before touching
	//ground again).
	private void continueJump() {
		if (jumpState == JumpState.JUMPING) {
			//If we are still allowed to be jumping up because max jump hasn't been reached, we continue to go up by giving a
			//boost to the player's velocity. If we aren't allowed, we just go to falling by ending the jump. The velocity will
			//be affected by gravity in the update method and cause player to fall.
			velocity.y = Constants.jumpSpeed;	
		}
	}
	
	private void endJump() {
		if (jumpState == JumpState.JUMPING) {
			jumpState = JumpState.FALLING;
		}
	}
	
	boolean landOnPlatform(Platform platform) {
		boolean leftSideFootOnPlatform = false;
		boolean rightSideFootOnPlatform = false;
		boolean bothFootOnPlatform = false;
		
		if ((lastFramePosition.y - Constants.playerEyeHeight) >= platform.top && 
				(position.y - Constants.playerEyeHeight) < platform.top) {
			//since the player position is marked by the center of the head and this is basically in the center of the texture,
			//the "origin" is 0,0 in the center of the texture. we need to subtract half the stance width to get the edge of each
			//foot, then add on one side and subtract on the other.
			float leftSideFoot = position.x - (Constants.playerStance / 2);
			float rightSideFoot = position.x + (Constants.playerStance / 2);
			
			leftSideFootOnPlatform = (platform.left < leftSideFoot && platform.right > leftSideFoot);
			rightSideFootOnPlatform = (platform.left < rightSideFoot && platform.right > rightSideFoot);
			//technically the platform is so tiny it is smaller than the stance width.
			bothFootOnPlatform = (platform.left > leftSideFoot && platform.right < rightSideFoot);
		}
		//return true if one is true, else return false
		return leftSideFootOnPlatform || rightSideFootOnPlatform || bothFootOnPlatform;
	}
	
	private void flinch(Facing facing) {
		velocity.y = Constants.knockbackSpeed.y;
		if (facing == Facing.LEFT) {
			velocity.x = -Constants.knockbackSpeed.x;
			position.x -= 10;
		} else {
			velocity.x = Constants.knockbackSpeed.x;
			position.x += 10;
		}
		hitState = HitState.IFRAME;
		lockState = LockState.LOCK;
		timeSinceHit = TimeUtils.nanoTime();
		
		if (jumpState == JumpState.WALL) {
			jumpState = JumpState.FALLING;
		}
		
	}
	
	boolean stickToPlatformLeft(Platform platform) {
		if ((lastFramePosition.x + Constants.playerStance / 2) <= platform.left && 
				(position.x + Constants.playerStance / 2) >= platform.left) {
			if (position.y < platform.top - 30 && position.y > platform.bottom) {
				return true;
			}
		}
		return false;
	}
	
	boolean stickToPlatformRight(Platform platform) {
		if ((lastFramePosition.x - Constants.playerStance / 2) >= platform.right && 
				(position.x - Constants.playerStance / 2) <= platform.right) {
			if (position.y < platform.top - 30 && position.y > platform.bottom) {
				return true;
			}	
		}
		return false;
	}
	
}