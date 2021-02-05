package lbcai.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.util.Assets;
import lbcai.util.Constants;

public class Player {

	public final static String className = Player.class.getName();
	
	public Vector2 position;
	//see enum below
	Facing facing;
	JumpState jumpState;
	RunState runState;
	//a long is like a 64-bit int. a BIG int.
	long jumpStartTime;
	long runStartTime;
	long idleStartTime;
	Vector2 velocity;
	//use to compare against current position and check if player should land or fall off platforms.
	Vector2 lastFramePosition;
	
	/**
	 * Constructor: make (width, height) vector position. Tells us position is center of head. 
	 * Starts player facing right & falling down (drop them into the level). Initializes things like idle start time,
	 * last frame position, etc.
	 */
	public Player() {
		position = new Vector2(128, Constants.playerEyeHeight);
		facing = Facing.RIGHT;
		jumpState = JumpState.FALLING;
		velocity = new Vector2();
		runState = RunState.IDLE;
		idleStartTime = TimeUtils.nanoTime();
		lastFramePosition = new Vector2(position);
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
			} else if (jumpState != JumpState.GROUNDED) {
				float jumpTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - jumpStartTime);
				region = Assets.instance.playerAssets.jumpRightAnim.getKeyFrame(jumpTime);
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
	public void update(float delta, Array<Platform> platforms) {
		//Use position vector to set last frame position.
		lastFramePosition.set(position);

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

		//Detect if landed on a platform. Must have this after vertical velocity and jump state setting code above, or else
		//weird behavior with jumping happens. Note that order in Java DOES matter!
		for (Platform platform : platforms) {
			if (landOnPlatform(platform)) {
				jumpState = JumpState.GROUNDED;
				position.y = platform.top + Constants.playerEyeHeight;
				velocity.y = 0;
			}
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
