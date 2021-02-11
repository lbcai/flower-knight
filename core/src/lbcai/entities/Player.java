package lbcai.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import lbcai.util.Enums.JumpState;
import lbcai.util.Enums.RunState;

public class Player {

	public final static String className = Player.class.getName();
	
	//use to compare against current position and check if player should land or fall off platforms.
	Vector2 lastFramePosition;
	Vector2 spawnLocation;
	Vector2 velocity;
	public Vector2 position;
	//see enum below
	Facing facing;
	JumpState jumpState;
	RunState runState;
	//a long is like a 64-bit int. a BIG int.
	long jumpStartTime;
	long runStartTime;
	long idleStartTime;
	long wallStartTime;
	Level level;
	private int jumpCounter = 0;
	private boolean iFrame = false;
	long timeSinceHit;
	
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
		idleStartTime = TimeUtils.nanoTime();
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

		//Initialize the region to display.
		TextureRegion region = Assets.instance.playerAssets.idleRightAnim.getKeyFrame(0);
		
		//Change the display depending on the direction the character faces.
		if (facing == Facing.RIGHT) {
			if (jumpState == JumpState.GROUNDED) {
				if (runState == RunState.IDLE) {
					float idleTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - idleStartTime);
					region = Assets.instance.playerAssets.idleRightAnim.getKeyFrame(idleTime);
				} else if (runState == RunState.RUN) {
					//Calculate how long we have been running in seconds (nanoToSec just converts to seconds, 
					//nanoTime gets current time).
					float runTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - runStartTime);
					region = Assets.instance.playerAssets.runRightAnim.getKeyFrame(runTime);
				} 
			} else if (jumpState == JumpState.JUMPING) {
				float jumpTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - jumpStartTime);
				region = Assets.instance.playerAssets.jumpRightAnim.getKeyFrame(jumpTime);
			} else if (jumpState == JumpState.FALLING || jumpState == JumpState.IFRAME) {
				region = Assets.instance.playerAssets.jumpRightAnim.getKeyFrame(12);
			} else if (jumpState == JumpState.WALL) {
				region = Assets.instance.playerAssets.idleRightAnim.getKeyFrame(0);
			}
			
		} else if (facing == Facing.LEFT) {
			if (jumpState == JumpState.GROUNDED) {
				if (runState == RunState.IDLE) {
					float idleTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - idleStartTime);
					region = Assets.instance.playerAssets.idleLeftAnim.getKeyFrame(idleTime);
				} else if (runState == RunState.RUN) {
					float runTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - runStartTime);
					region = Assets.instance.playerAssets.runLeftAnim.getKeyFrame(runTime);
				}
			} else if (jumpState == JumpState.JUMPING) {
				float jumpTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - jumpStartTime);
				region = Assets.instance.playerAssets.jumpLeftAnim.getKeyFrame(jumpTime);
			} else if (jumpState == JumpState.FALLING || jumpState == JumpState.IFRAME) {
				region = Assets.instance.playerAssets.jumpLeftAnim.getKeyFrame(12);
			} else if (jumpState == JumpState.WALL) {
				region = Assets.instance.playerAssets.idleLeftAnim.getKeyFrame(0);
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
		velocity.y -= delta * Constants.worldGravity;
		//Scale vector velocity by delta (time) and add to position vector. Changes position of player based on time and
		//gravity. Causes player to fall. Scaled by seconds (delta) to avoid the framerate problem.
		position.mulAdd(velocity, delta);
		
		//Start player falling. Why do we not just use endjump()? Because dropping off a platform will not cause you to enter
		//falling state.
		if (jumpState != JumpState.FALLING && velocity.y < 0) {
			jumpState = JumpState.FALLING;
		}
		
		//Return player to spawn if they fall off the map. Currently doesn't deduct lives or anything.
		if (position.y < Constants.killPlane) {
			init();
		}
		
		//Use for collision detection of player.
		Rectangle playerBound = new Rectangle(
				position.x - Constants.playerStance / 2,
				position.y - Constants.playerEyeHeight, 
				Constants.playerStance,
				Constants.playerHeight);
		
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
			}
			
			if (jumpState == JumpState.JUMPING || jumpState == JumpState.FALLING) {
				
				if (stickToPlatformLeft(platform) || stickToPlatformRight(platform)) {
					System.out.println("stick");
					wallStartTime = TimeUtils.nanoTime();
					jumpState = JumpState.WALL;
					jumpCounter = 0;
					if ((MathUtils.nanoToSec * TimeUtils.nanoTime() - wallStartTime) < Constants.wallTime) {
						if (stickToPlatformLeft(platform)) {
							position.x = platform.left;
						} else {
							position.x = platform.right;
						}
					} 
					
				}
				
			}	
		}
		
		//Collision detection with enemies, includes the direction the hit is coming from. Must go after platform checking code.
		if (MathUtils.nanoToSec * (TimeUtils.nanoTime() - timeSinceHit) > Constants.iFrameLength) {
			iFrame = false;
		}
		
		for (Enemy enemy : level.getEnemies()) {
			//have to make new rectangle because enemies move
			Rectangle enemyBound = new Rectangle(
					enemy.position.x - Constants.pBeetleCollisionRadius,
					enemy.position.y - Constants.pBeetleCollisionRadius,
					2 * Constants.pBeetleCollisionRadius,
					2 * Constants.pBeetleCollisionRadius);
			if (playerBound.overlaps(enemyBound)) {
				if (position.x < enemy.position.x && iFrame == false) {
					flinch(Facing.LEFT);
				} else if (position.x > enemy.position.x && iFrame == false) {
					flinch(Facing.RIGHT);
				}
			}
		}
		
		
		//Prevent player from falling through ground. Player will start falling and hit ground and stay there.
		//if (position.y - Constants.playerEyeHeight < 0) {
		//	jumpState = JumpState.GROUNDED;
		//	position.y = Constants.playerEyeHeight;
		//	velocity.y = 0;
		//}
		
		//run (unavailable while flinching or otherwise iframe animation-locked)
		if (jumpState != JumpState.IFRAME) {
			if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				moveLeft(delta);
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				moveRight(delta);
			} else {
				if (runState != RunState.IDLE && jumpState == JumpState.GROUNDED) {
					idleStartTime = TimeUtils.nanoTime();
				}
				runState = RunState.IDLE;
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
		runState = RunState.RUN;
		facing = Facing.LEFT;
		position.x -= delta * Constants.moveSpeed;
	}
	
	private void moveRight(float delta) {
		if (jumpState == JumpState.GROUNDED && runState != RunState.RUN) {
			runStartTime = TimeUtils.nanoTime();
		}
		runState = RunState.RUN;
		facing = Facing.RIGHT;
		position.x += delta * Constants.moveSpeed;
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
		} else {
			velocity.x = Constants.knockbackSpeed.x;
		}
		jumpState = JumpState.IFRAME;
		timeSinceHit = TimeUtils.nanoTime();
		iFrame = true;
	}
	
	boolean stickToPlatformLeft(Platform platform) {
		boolean leftStick = (position.x + Constants.playerStance / 2) < platform.left + 1 &&
				(position.x + Constants.playerStance / 2) > platform.left - 1;
		boolean between = position.y < platform.top && position.y > platform.bottom;
		boolean left = leftStick && between;
		
		return left;
	}
	
	boolean stickToPlatformRight(Platform platform) {
		boolean rightStick = (position.x - Constants.playerStance / 2) < platform.right + 1 &&
				(position.x + Constants.playerStance / 2) > platform.right - 1;		
		boolean between = position.y < platform.top && position.y > platform.bottom;
		boolean right = rightStick && between;
		
		return right;
	}
	
}
