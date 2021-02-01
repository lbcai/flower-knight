package lbcai.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.util.Assets;
import lbcai.util.Constants;

public class Player {

	public final static String className = Player.class.getName();
	
	Vector2 position;
	//see enum below
	Facing facing;
	JumpState jumpState;
	RunState runState;
	//a long is like a 64-bit int. a BIG int.
	long jumpStartTime;
	long runStartTime;
	long idleStartTime;
	Vector2 velocity;
	
	/**
	 * Constructor: make (width, height) vector position. Tells us position is center of head. 
	 * Starts player facing right & falling down (drop them into the level).
	 */
	public Player() {
		position = new Vector2(128, Constants.playerEyeHeight);
		facing = Facing.RIGHT;
		jumpState = JumpState.FALLING;
		velocity = new Vector2();
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
			} else if (jumpState != JumpState.GROUNDED) {
				float jumpTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - jumpStartTime);
				region = Assets.instance.playerAssets.jumpLeftAnim.getKeyFrame(jumpTime);
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
	 * @param delta the time in seconds so we avoid framerate problems and changing the passing of time on different devices.
	 * 				Usually in some fraction of a second. This actually represents the time since last frame.
	 */
	public void update(float delta) {
		//Subtract time * gravity from player velocity to make player accelerate downwards the longer they are in air.
		//Update is called every frame (delta). This means every frame the velocity is affected in a downwards motion.
		velocity.y -= delta * Constants.worldGravity;
		//Scale vector velocity by delta (time) and add to position vector. Changes position of player based on time and
		//gravity. Causes player to fall. Scaled by seconds (delta) to avoid the framerate problem.
		position.mulAdd(velocity, delta);
		//Start player falling.
		if (jumpState != JumpState.FALLING) {
			jumpState = JumpState.FALLING;
		}
		//Prevent player from falling through ground. Player will start falling and hit ground and stay there.
		if (position.y - Constants.playerEyeHeight < 0) {
			jumpState = JumpState.GROUNDED;
			position.y = Constants.playerEyeHeight;
			velocity.y = 0;
		}
		
		//run
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
		
		//jump (apparently you can do this case thing with enums!)
		if (Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
			switch (jumpState) {
			case GROUNDED:
				startJump();
				break;
			case JUMPING:
				continueJump();
				break;
			//note: do nothing when falling.
			}
		} else {
			endJump();
		}

	}
	
	/**
	 * Actual controller for player. Also updates player state, i.e. facing direction, jump state.
	 * 
	 * Moving left/right is simple enough: change the position in the appropriate direction and change the facing to the right
	 * direction. The facing will be used to figure out which sprite direction set to use for animations.
	 * 
	 * Jumping involves a jumpState: GROUNDED, JUMPING, and FALLING. See comments below.
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
		continueJump();
	}
	
	//This is added to allow player to continue to press the jump key while in air in order to jump for longer and go higher.
	private void continueJump() {
		if (jumpState == JumpState.JUMPING) {
			//Multiply by nanoToSec to convert from nanoseconds to seconds. The value we are converting is the length of time it
			//has been since we started jumping. nanoTime() is current time in nanoseconds.
			float jumpDuration = MathUtils.nanoToSec * (TimeUtils.nanoTime() - jumpStartTime);
			//If we are still allowed to be jumping up because max jump time hasn't been reached, we continue to go up by giving a
			//boost to the player's velocity. If we aren't allowed, we just go to falling by ending the jump. The velocity will
			//be affected by gravity in the update method and cause player to fall.
			if (jumpDuration < Constants.maxJumpTime) {
				velocity.y = Constants.jumpSpeed;
			} else {
				endJump();
			}
		}
		endJump();
	}
	
	private void endJump() {
		if (jumpState == JumpState.JUMPING) {
			jumpState = JumpState.FALLING;
		}
	}
	
	//enums are like discrete final variables, like steps you can set something to.
	enum Facing {
		LEFT,
		RIGHT
	}
	
	enum JumpState {
		GROUNDED,
		JUMPING,
		FALLING
	}
	
	enum RunState {
		IDLE,
		RUN
	}
	
}
