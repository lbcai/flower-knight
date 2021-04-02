package lbcai.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.HitState;
import lbcai.util.Enums.JumpState;
import lbcai.util.Enums.LockState;
import lbcai.util.Enums.RunState;
import lbcai.util.Utils;

public class Player extends Entity {

	public final static String className = Player.class.getName();
	
	//use to compare against current position and check if player should land or fall off platforms.
	Vector2 lastFramePosition;
	Vector2 spawnLocation;
	Vector2 velocity;
	//see enum below
	public Facing facing;
	public JumpState jumpState;
	public RunState runState;
	public HitState hitState;
	LockState lockState;
	//a long is like a 64-bit int. a BIG int.
	private long jumpStartTime;
	private long runStartTime;
	private long idleStartTime;
	private long wallStartTime;
	private long dodgeStartTime;
	private long squatStartTime;
	private long boostStartTime;
	private long landStartTime;

	public int jumpCounter = 0;

	long timeSinceHit;
	private int idleTransitionCounter = 0;
	private int flashCounter = 0;
	private long idleTransStartTime;
	private long slideTransStartTime;
	private long attackStartTime;
	//for collision detection of player attacks vs enemy
	private Rectangle attackHitBox = new Rectangle();
	private int attackComboCounter = 0;
	private Vector2 targetPosition;
	private int boostCounter = 0;
	
	//for allowing player to rebind controls
	private int attackKey = Keys.X;
	private int jumpKey = Keys.ALT_LEFT;
	private int dodgeKey = Keys.C;
	private int lootKey = Keys.Z;
	
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
		//set max health here so if player dies and respawns they can keep max health buffs, same with damage buffs
		maxHealth = Constants.baseHealth;
		damage = Constants.playerBaseDamage;
		range = Constants.playerBaseRange;
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
		health = Constants.baseHealth;
		
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
					float idleTime = Utils.secondsSince(idleStartTime);
					//use idle transition counter (set when idle begins to 0) to determine what idle state we are in:
					//0 = catch breath idle, 1 = transition animation, 2 = normal idle, 3 = transition from squat, 4 = 
					//transition from falling (landing animation)
					if (Utils.secondsSince(timeSinceHit) < Constants.idleBTime || 
							Utils.secondsSince(attackStartTime) < Constants.idleBTime || Utils.secondsSince(dodgeStartTime) <
							Constants.idleBTime) {
						if (idleTransitionCounter == 4) {
							//combat landing into combat idle
							float landTransTime = Utils.secondsSince(landStartTime);
							region = Assets.instance.playerAssets.landRightCombatAnim.getKeyFrame(landTransTime);
							if (Assets.instance.playerAssets.landRightCombatAnim.isAnimationFinished(landTransTime)) {
								idleTransitionCounter = 2;
							}
						} else {
							region = Assets.instance.playerAssets.idleBRightAnim.getKeyFrame(idleTime);
							idleTransitionCounter = 1;
							idleTransStartTime = TimeUtils.nanoTime();
						}

					} else if (idleTransitionCounter == 0) {
						idleTransitionCounter = 2;
					} else if (idleTransitionCounter == 1) {
						float idleTransTime = Utils.secondsSince(idleTransStartTime);
						region = Assets.instance.playerAssets.idleBTransRightAnim.getKeyFrame(idleTransTime);
						if (Assets.instance.playerAssets.idleBTransRightAnim.isAnimationFinished(idleTransTime)) {
							idleTransitionCounter = 2;
						}
					} else if (idleTransitionCounter == 2) {
						region = Assets.instance.playerAssets.idleRightAnim.getKeyFrame(idleTime);
					} else if (idleTransitionCounter == 3) {
						region = Assets.instance.playerAssets.squatRightUpAnim.getKeyFrame(idleTime);
						if (Assets.instance.playerAssets.squatRightUpAnim.isAnimationFinished(idleTime)) {
							idleTransitionCounter = 2;
						}
					} else if (idleTransitionCounter == 4) {
						float landTransTime = Utils.secondsSince(landStartTime);
						region = Assets.instance.playerAssets.landRightAnim.getKeyFrame(landTransTime);
						if (Assets.instance.playerAssets.landRightAnim.isAnimationFinished(landTransTime)) {
							idleTransitionCounter = 2;
						}
					}
					
				} else if (runState == RunState.RUN) {
					//Calculate how long we have been running in seconds (nanoToSec just converts to seconds, 
					//nanoTime gets current time).
					float runTime = Utils.secondsSince(runStartTime);
					region = Assets.instance.playerAssets.runRightAnim.getKeyFrame(runTime);
				} else if (runState == RunState.SKID) {
					float skidTime = Utils.secondsSince(slideTransStartTime);
					region = Assets.instance.playerAssets.skidRightAnim.getKeyFrame(skidTime);
				} else if (runState == RunState.SQUAT) {
					float squatTime = Utils.secondsSince(squatStartTime);
					region = Assets.instance.playerAssets.squatRightAnim.getKeyFrame(squatTime);
					idleTransitionCounter = 3;			
				}
			} else if (jumpState == JumpState.JUMPING) {
				float jumpTime = Utils.secondsSince(jumpStartTime);
				region = Assets.instance.playerAssets.jumpRightAnim.getKeyFrame(jumpTime);
			} else if (jumpState == JumpState.FALLING) {
				region = Assets.instance.playerAssets.jumpRightAnim.getKeyFrame(12);
			} else if (jumpState == JumpState.WALL) {
				float hangTime = Utils.secondsSince(wallStartTime);
				region = Assets.instance.playerAssets.hangRightAnim.getKeyFrame(hangTime);
			}
			
			if (hitState == HitState.IFRAME) {
				if (flashCounter == 0 || flashCounter % 5 != 0) {
					flashCounter += 1;
				} else if (flashCounter % 5 == 0) {
					batch.setColor(183f/255f, 183f/255f, 183f/255f, alpha);
					flashCounter = 0;
				}
				
				if (lockState == LockState.LOCK) {
					region = Assets.instance.playerAssets.jumpRightAnim.getKeyFrame(12);
				}
			}
			
			if (lockState == LockState.ATTACKJUMP) {
				float attackTime = Utils.secondsSince(attackStartTime);
				region = Assets.instance.playerAssets.jumpAttack1RightAnim.getKeyFrame(attackTime);
				if (Assets.instance.playerAssets.jumpAttack1RightAnim.isAnimationFinished(attackTime)) {
					lockState = LockState.FREE;
				}
			}
			
			if (lockState == LockState.ATTACK1LOCK) {
				float attackTime = Utils.secondsSince(attackStartTime);
				region = Assets.instance.playerAssets.attack1RightAnim.getKeyFrame(attackTime);

				if (Assets.instance.playerAssets.attack1RightAnim.isAnimationFinished(attackTime)) {
					lockState = LockState.FREE;
					attackStartTime = TimeUtils.nanoTime();
				}
			} else if (lockState == LockState.DODGE) {
				float dodgeTime = Utils.secondsSince(dodgeStartTime);
				region = Assets.instance.playerAssets.skidRightAnim.getKeyFrame(dodgeTime);
				if (Assets.instance.playerAssets.skidRightAnim.isAnimationFinished(dodgeTime)) {
					lockState = LockState.FREE;
					hitState = HitState.NOHIT;
				}
			}

			if (lockState == LockState.FREE && runState == RunState.IDLE) {
				if (attackComboCounter == 1) {
					float attackTime = Utils.secondsSince(attackStartTime);
					region = Assets.instance.playerAssets.attack1RightEndAnim.getKeyFrame(attackTime);
					if (Assets.instance.playerAssets.attack1RightEndAnim.isAnimationFinished(attackTime)) {
						attackComboCounter = 0;
					}
				}
			}

			if (boostCounter == 1) {
				float boostTime = Utils.secondsSince(boostStartTime);
				region = Assets.instance.playerAssets.boostToPlatRightAnim.getKeyFrame(boostTime);
				if (Assets.instance.playerAssets.boostToPlatRightAnim.isAnimationFinished(boostTime)) {
					boostCounter = 0;
					lockState = LockState.FREE;
				}
			}
			
		} else if (facing == Facing.LEFT) {
			if (jumpState == JumpState.GROUNDED) {
				if (runState == RunState.IDLE) {
					float idleTime = Utils.secondsSince(idleStartTime);
					//use idle transition counter to figure out when to play transition animation between combat & normal idles
					if (Utils.secondsSince(timeSinceHit) < Constants.idleBTime || 
							Utils.secondsSince(attackStartTime) < Constants.idleBTime || Utils.secondsSince(dodgeStartTime) <
							Constants.idleBTime) {
						if (idleTransitionCounter == 4) {
							//combat landing into combat idle
							float landTransTime = Utils.secondsSince(landStartTime);
							region = Assets.instance.playerAssets.landLeftCombatAnim.getKeyFrame(landTransTime);
							if (Assets.instance.playerAssets.landLeftCombatAnim.isAnimationFinished(landTransTime)) {
								idleTransitionCounter = 2;
							}
						} else {
							region = Assets.instance.playerAssets.idleBLeftAnim.getKeyFrame(idleTime);
							idleTransitionCounter = 1;
							idleTransStartTime = TimeUtils.nanoTime();
						}

					} else if (idleTransitionCounter == 0) {
						idleTransitionCounter = 2;
					} else if (idleTransitionCounter == 1) {
						float idleTransTime = Utils.secondsSince(idleTransStartTime);
						region = Assets.instance.playerAssets.idleBTransLeftAnim.getKeyFrame(idleTransTime);
						if (Assets.instance.playerAssets.idleBTransLeftAnim.isAnimationFinished(idleTransTime)) {
							idleTransitionCounter = 2;
						}
					} else if (idleTransitionCounter == 2) {
						region = Assets.instance.playerAssets.idleLeftAnim.getKeyFrame(idleTime);
					} else if (idleTransitionCounter == 3) {
						region = Assets.instance.playerAssets.squatLeftUpAnim.getKeyFrame(idleTime);
						if (Assets.instance.playerAssets.squatLeftUpAnim.isAnimationFinished(idleTime)) {
							idleTransitionCounter = 2;
						}
					} else if (idleTransitionCounter == 4) {
						float landTransTime = Utils.secondsSince(landStartTime);
						region = Assets.instance.playerAssets.landLeftAnim.getKeyFrame(landTransTime);
						if (Assets.instance.playerAssets.landLeftAnim.isAnimationFinished(landTransTime)) {
							idleTransitionCounter = 2;
						}
					}

				} else if (runState == RunState.RUN) {
					float runTime = Utils.secondsSince(runStartTime);
					region = Assets.instance.playerAssets.runLeftAnim.getKeyFrame(runTime);
				} else if (runState == RunState.SKID) {
					float skidTime = Utils.secondsSince(slideTransStartTime);
					region = Assets.instance.playerAssets.skidLeftAnim.getKeyFrame(skidTime);
				} else if (runState == RunState.SQUAT) {
					float squatTime = Utils.secondsSince(squatStartTime);
					region = Assets.instance.playerAssets.squatLeftAnim.getKeyFrame(squatTime);
					idleTransitionCounter = 3;			
				}

			} else if (jumpState == JumpState.JUMPING) {
				float jumpTime = Utils.secondsSince(jumpStartTime);
				region = Assets.instance.playerAssets.jumpLeftAnim.getKeyFrame(jumpTime);
			} else if (jumpState == JumpState.FALLING) {
				region = Assets.instance.playerAssets.jumpLeftAnim.getKeyFrame(12);
			} else if (jumpState == JumpState.WALL) {
				float hangTime = Utils.secondsSince(wallStartTime);
				region = Assets.instance.playerAssets.hangLeftAnim.getKeyFrame(hangTime);
			}
			
			if (hitState == HitState.IFRAME) {
				if (flashCounter == 0 || flashCounter % 5 != 0) {
					flashCounter += 1;
				} else if (flashCounter % 5 == 0) {
					batch.setColor(183f/255f, 183f/255f, 183f/255f, alpha);
					flashCounter = 0;
				}
				
				if (lockState == LockState.LOCK) {
					region = Assets.instance.playerAssets.jumpLeftAnim.getKeyFrame(12);
				}
				
			}
			
			if (lockState == LockState.ATTACKJUMP) {
				float attackTime = Utils.secondsSince(attackStartTime);
				if (jumpState != JumpState.GROUNDED) {
					region = Assets.instance.playerAssets.jumpAttack1LeftAnim.getKeyFrame(attackTime);
				} else {
					//placeholder...left side only currently. testing animation cancel, may have to make trans anim
					float landTransTime = Utils.secondsSince(landStartTime);
					region = Assets.instance.playerAssets.landLeftAnim.getKeyFrame(landTransTime);
					if (Assets.instance.playerAssets.landLeftAnim.isAnimationFinished(landTransTime)) {
						idleTransitionCounter = 2;
					}
				}
				
				if (Assets.instance.playerAssets.jumpAttack1LeftAnim.isAnimationFinished(attackTime)) {
					lockState = LockState.FREE;
				}
			}
			
			if (lockState == LockState.ATTACK1LOCK) {
				float attackTime = Utils.secondsSince(attackStartTime);
				region = Assets.instance.playerAssets.attack1LeftAnim.getKeyFrame(attackTime);

				if (Assets.instance.playerAssets.attack1LeftAnim.isAnimationFinished(attackTime)) {
					lockState = LockState.FREE;
					attackStartTime = TimeUtils.nanoTime();
				}
			} else if (lockState == LockState.DODGE) {
				float dodgeTime = Utils.secondsSince(dodgeStartTime);
				//placeholder
				region = Assets.instance.playerAssets.skidLeftAnim.getKeyFrame(dodgeTime);
				if (Assets.instance.playerAssets.skidLeftAnim.isAnimationFinished(dodgeTime)) {
					lockState = LockState.FREE;
					hitState = HitState.NOHIT;
				}
			}

			if (lockState == LockState.FREE && runState == RunState.IDLE) {
				if (attackComboCounter == 1) {
					float attackTime = Utils.secondsSince(attackStartTime);
					region = Assets.instance.playerAssets.attack1LeftEndAnim.getKeyFrame(attackTime);
					if (Assets.instance.playerAssets.attack1LeftEndAnim.isAnimationFinished(attackTime)) {
						attackComboCounter = 0;
					}
				}
			}

			if (boostCounter == 1) {
				float boostTime = Utils.secondsSince(boostStartTime);
				region = Assets.instance.playerAssets.boostToPlatLeftAnim.getKeyFrame(boostTime);
				if (Assets.instance.playerAssets.boostToPlatLeftAnim.isAnimationFinished(boostTime)) {
					boostCounter = 0;
					lockState = LockState.FREE;
				}
			}
			
		}

		//Actually draw the sprites.
		batch.draw(region.getTexture(), 
				(position.x - Constants.playerHead.x + ((AtlasRegion) region).offsetX), 
				(position.y - Constants.playerHead.y + ((AtlasRegion) region).offsetY), 
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
		
		//Return player to spawn if they fall off the map. Currently doesn't deduct lives or anything.
		if (position.y < Constants.killPlane) {
			init();
		}
		
		//Use for collision detection of player.
		if ((runState == RunState.SQUAT || boostCounter == 1) && jumpState != JumpState.FALLING) {

			if (facing == Facing.RIGHT) {
				hitBox = new Rectangle(
						position.x - Constants.playerStance / 2,
						position.y - Constants.playerEyeHeight, 
						Constants.playerStance + 50,
						Constants.playerHeight - 70);
			} else {
				hitBox = new Rectangle(
						position.x - Constants.playerStance - 20,
						position.y - Constants.playerEyeHeight, 
						Constants.playerStance + 50,
						Constants.playerHeight - 70);
			}

		} else if (jumpState == JumpState.WALL) {
			hitBox = new Rectangle(
					position.x - Constants.playerStance / 2,
					position.y - Constants.playerEyeHeight + 10, 
					Constants.playerStance,
					Constants.playerHeight);
		} else {
			if (facing == Facing.LEFT) {
				hitBox = new Rectangle(
						position.x - Constants.playerStance / 2,
						position.y - Constants.playerEyeHeight, 
						Constants.playerStance,
						Constants.playerHeight - 20);
			} else {
				hitBox = new Rectangle(
						position.x - Constants.playerStance / 2 + 10,
						position.y - Constants.playerEyeHeight, 
						Constants.playerStance,
						Constants.playerHeight - 20);
			}

			}

		
		//Loot items
		//away from the other input code so player can still loot if they're animation locked
		if (Gdx.input.isKeyJustPressed(lootKey)) {
			DelayedRemovalArray<Item> items = level.getItems();
			items.begin();
			for (int i = 0; i < items.size; i++) {
				Item item = items.get(i);
				
				if (hitBox.overlaps(item.hitBox)) {
					item.use();
				}
			}
			items.end();
		}
		
		//Detect if landed on a platform. Must have this after vertical velocity and jump state setting code above, or else
		//weird behavior with jumping happens. Note that order in Java DOES matter!
		//Also collision detection with sides of platform.
		for (Platform platform : platforms) {

			if (landOnPlatform(platform)) {
				if (jumpState == JumpState.FALLING) {
					landStartTime = TimeUtils.nanoTime();
				}
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
						position.x = platform.right - 10;
					} else if (position.x < platform.left) {
						position.x = platform.left + 10;
					}
				}
				
			}
			
			//allows sticking to walls and walljumping
			if (jumpState == JumpState.JUMPING || jumpState == JumpState.FALLING || jumpState == JumpState.WALL) {
				
				if (Gdx.input.isKeyPressed(Keys.LEFT) && !(Gdx.input.isKeyPressed(Keys.RIGHT)) && facing == Facing.LEFT) {

						if (stickToPlatformRight(platform)) {
							if (jumpState != JumpState.WALL) {
								wallStartTime = TimeUtils.nanoTime();
								jumpState = JumpState.WALL;
								jumpCounter = 0;
							}

							if ((Utils.secondsSince(wallStartTime)) < Constants.wallTime) {
								position.x = platform.right + Constants.playerStance / 2;
								velocity.x = 0;
								velocity.y = 0;
							} 
							
							if (Gdx.input.isKeyPressed(Keys.LEFT) && (Gdx.input.isKeyPressed(jumpKey))) {
								position.x = (platform.right + Constants.playerStance / 2) - 10;
								startJump();
							}
							
						}
					
				} else if (Gdx.input.isKeyPressed(Keys.RIGHT) && !(Gdx.input.isKeyPressed(Keys.LEFT)) && facing == Facing.RIGHT) {
					
						if (stickToPlatformLeft(platform)) {
							if (jumpState != JumpState.WALL) {
								wallStartTime = TimeUtils.nanoTime();
								jumpState = JumpState.WALL;
								jumpCounter = 0;
							}


							if ((Utils.secondsSince(wallStartTime)) < Constants.wallTime) {
								position.x = platform.left - Constants.playerStance / 2;
								velocity.x = 0;
								velocity.y = 0;
							} 
							
							if (Gdx.input.isKeyPressed(Keys.RIGHT) && (Gdx.input.isKeyPressed(jumpKey))) {
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
			if (Utils.secondsSince(timeSinceHit) > Constants.animLockTime) {
				lockState = LockState.FREE;
			}
		} else {
			//check if invincible grace period is over.
			if (Utils.secondsSince(timeSinceHit) > Constants.iFrameLength) {
				if (hitState == HitState.IFRAME) {
					hitState = HitState.NOHIT;
				}
			}
		}

		//Collision detection with enemies, includes the direction the hit is coming from. Must go after platform checking code.
		for (Enemy enemy : level.getEnemies()) {
			if (enemy.inactive == false && enemy.touchDmg == true) {
				//have to make new rectangle because enemies move (bottom left x, bottom left y, width, height)
				if (hitBox.overlaps(enemy.hitBox)) {
					if (position.x < enemy.position.x && hitState == HitState.NOHIT) {
						velocity.x = 0;
						//player is on the left of the enemy
						flinch(Facing.LEFT);
						enemy.doesDamage(this, Facing.LEFT);

					} else if (position.x > enemy.position.x && hitState == HitState.NOHIT) {
						velocity.x = 0;
						//player is on the right of the enemy
						flinch(Facing.RIGHT);
						enemy.doesDamage(this, Facing.RIGHT);

					}
				}
			}

		}
		
		//check if projectiles are hitting player
		for (Bullet bullet : level.getBullets()) {

			//if player comes in contact with bullet, stop player, make flinch, destroy bullet
			if (hitBox.overlaps(bullet.hitBox)) {
				if (position.x < bullet.position.x && hitState == HitState.NOHIT) {
					velocity.x = 0;
					flinch(Facing.LEFT);
					bullet.doesDamage(this, Facing.LEFT);
					
				} else if (position.x > bullet.position.x && hitState == HitState.NOHIT) {
					velocity.x = 0;
					flinch(Facing.RIGHT);
					bullet.doesDamage(this, Facing.RIGHT);
				}
			}
		}

		
		//move player during attack animation, dodge animation
		if (lockState == LockState.ATTACK1LOCK) {
			Utils.lerpX(position, targetPosition, 0.5f);
		} else if (lockState == LockState.DODGE) {
			Utils.lerpX(position, targetPosition, 0.1f);
		}
		
		
		//Prevent player from falling through ground. Player will start falling and hit ground and stay there.
		//if (position.y - Constants.playerEyeHeight < 0) {
		//	jumpState = JumpState.GROUNDED;
		//	position.y = Constants.playerEyeHeight;
		//	velocity.y = 0;
		//}

		//run (unavailable while flinching or otherwise iframe animation-locked, which is a shorter period of time than
		//the actual invincible time)
		if (lockState == LockState.FREE && jumpState != JumpState.WALL) {
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
					if (Utils.secondsSince(runStartTime) > Constants.skidTimeLimitBreak) {
						slideTransStartTime = TimeUtils.nanoTime();
						runState = RunState.SKID;
					} else {
						goToIdle();
					}

				}
				if (runState == RunState.SKID && jumpState == JumpState.GROUNDED) {
					if (Utils.secondsSince(slideTransStartTime) < Constants.skidTime) {
						if (facing == Facing.LEFT) {
							moveLeft(delta);
						} else {
							moveRight(delta);
						}

					} else {
						goToIdle();
					}
				} 
				
				if ((runState == RunState.IDLE || runState == RunState.SQUAT) && jumpState == JumpState.GROUNDED) {
					if (Gdx.input.isKeyPressed(Keys.DOWN)) {
						if (runState != RunState.SQUAT) {
							squatStartTime = TimeUtils.nanoTime();
							runState = RunState.SQUAT;
						}
					} else {
						if (runState == RunState.SQUAT) {
							goToIdle();
						}

					}
				}

			}
			
			//player attacks and collision detection
			if (Gdx.input.isKeyJustPressed(attackKey)) {
				if (lockState == LockState.FREE && jumpState == JumpState.GROUNDED) {
					if (lockState != LockState.ATTACK1LOCK) {
						lockState = LockState.ATTACK1LOCK;
						attackStartTime = TimeUtils.nanoTime();
						attackComboCounter = 1;
					}
		
					if (facing == Facing.LEFT) {
						targetPosition = new Vector2(position.x - 25, position.y);
						attackHitBox = new Rectangle(
							position.x - (Constants.playerStance) - Constants.attackRange1.x,
							position.y - Constants.playerEyeHeight,
							Constants.attackRange1.x,
							Constants.attackRange1.y);
					} else {
						targetPosition = new Vector2(position.x + 25, position.y);
						attackHitBox = new Rectangle(
							position.x + (Constants.playerStance) + 14,
							position.y - Constants.playerEyeHeight,
							Constants.attackRange1.x,
							Constants.attackRange1.y);
					}
					
				} else if (lockState == LockState.FREE && jumpState != JumpState.GROUNDED) {
					if (lockState != LockState.ATTACKJUMP) {
						lockState = LockState.ATTACKJUMP;
						attackStartTime = TimeUtils.nanoTime();
					}
					
					if (facing == Facing.LEFT) {
						attackHitBox = new Rectangle(
							position.x - (Constants.playerStance / 2) - Constants.attackRange1.x,
							position.y - 30,
							Constants.attackRange1.x,
							Constants.attackRange1.y + 20);
					} else {
						attackHitBox = new Rectangle(
							position.x + (Constants.playerStance / 2) + 14,
							position.y - 30,
							Constants.attackRange1.x,
							Constants.attackRange1.y + 20);
					}
					
				}
				
				for (Enemy enemy: level.getEnemies()) {
					if (enemy.inactive == false) {
						if (attackHitBox.overlaps(enemy.hitBox)) {
							//random number in range min, max: Math.random() * (max - min + 1) + min
							int damageInstance = (int) (Math.random() * ((damage + range) - 
									(damage - range) + 1) + 
									(damage - range));
							enemy.isDamaged(damageInstance); 
							level.spawnDmgNum(enemy.position, damageInstance, facing);
							level.spawnHitEffect(enemy.position, enemy.facing, 1);
						}
					}
				}
				
			}
			
			//jump (apparently you can do this case thing with enums!) use isKeyJustPressed to avoid continuous input multi-jump
			if (Gdx.input.isKeyJustPressed(jumpKey) && !Gdx.input.isKeyPressed(Keys.DOWN)) {
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
			
			} else if (Gdx.input.isKeyJustPressed(jumpKey) && Gdx.input.isKeyPressed(Keys.DOWN)) {
				//downjump
				//isKeyJustPressed prevent player from being able to doublejump straight onto the platform they 
				//downjumped off on accident
				jumpState = JumpState.FALLING;
				position.y -= 10; 
			}
			
			//allows boosting up to platform surfaces that player is just shy of
			if (Gdx.input.isKeyJustPressed(Keys.UP)) {
				for (Platform platform : platforms) {
					if (boostUpToPlatform(platform)) {
						position.y = platform.top + Constants.playerEyeHeight;
						velocity.x = 0;
						velocity.y = 0;
					}
				}
			}
			
			//dodge roll
			if (Gdx.input.isKeyJustPressed(dodgeKey)) {
				if (Utils.secondsSince(dodgeStartTime) > Constants.dodgeCDTime) {
					if (lockState != LockState.DODGE) {
						lockState = LockState.DODGE;
						hitState = HitState.DODGE;
						dodgeStartTime = TimeUtils.nanoTime();
						if (facing == Facing.LEFT) {
							targetPosition = new Vector2(position.x - 400, position.y);
						} else {
							targetPosition = new Vector2(position.x + 400, position.y);
						}
					}
				}
			}
			
		
			
		}
		
		//Enter falling state if dropping, put this last because then we don't have issues where the player thinks it's falling
		//but it's standing on the platform and this interacts badly with sticking to walls (allows head getting stuck on platform
		//while running).
		if (velocity.y < 0) {
			jumpState = JumpState.FALLING;
			idleTransitionCounter = 4;
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
		if (runState != RunState.SKID && jumpState == JumpState.GROUNDED) {
			runState = RunState.RUN;
		} else if (runState != RunState.SKID && jumpState != JumpState.GROUNDED) {
			runState = RunState.IDLE;
		}

		facing = Facing.LEFT;
		if (hitState == HitState.IFRAME && jumpState != JumpState.GROUNDED) {
			velocity.x -= delta * 2;
		} else {
			velocity.x = -delta * Constants.moveSpeed;
		}
		
	}
	
	private void moveRight(float delta) {
		if (jumpState == JumpState.GROUNDED && runState != RunState.RUN) {
			runStartTime = TimeUtils.nanoTime();
		}
		if (runState != RunState.SKID && jumpState == JumpState.GROUNDED) {
			runState = RunState.RUN;
		} else if (runState != RunState.SKID && jumpState != JumpState.GROUNDED) {
			runState = RunState.IDLE;
		}
		facing = Facing.RIGHT;
		if (hitState == HitState.IFRAME && jumpState != JumpState.GROUNDED) {
			velocity.x += delta * 2;
		} else {
			velocity.x = delta * Constants.moveSpeed;
		}
		
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
			if (boostCounter != 1) {
				velocity.x = -Constants.knockbackSpeed.x;
				position.x -= 30;
			}
			
		} else {
			if (boostCounter != 1) {
				velocity.x = Constants.knockbackSpeed.x;
				position.x += 30;
			}
		
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
			if (position.y < platform.top - 35 && position.y > platform.bottom) {
				return true;
			}
		}
		return false;
	}
	
	boolean stickToPlatformRight(Platform platform) {
		if ((lastFramePosition.x - Constants.playerStance / 2) >= platform.right && 
				(position.x - Constants.playerStance / 2) <= platform.right) {
			if (position.y < platform.top - 35 && position.y > platform.bottom) {
				return true;
			}	
		}
		return false;
	}
	
	boolean boostUpToPlatform(Platform platform) {
		if (lockState == LockState.FREE) {
			if (position.x < platform.right && position.x > platform.left) {
				if (position.y - Constants.playerEyeHeight < platform.top && position.y > platform.top) {
					boostCounter = 1;
					lockState = LockState.BOOSTLOCK;
					boostStartTime = TimeUtils.nanoTime();
					return true;
				}
			}
		}
		return false;
	}
	
	void goToIdle() {
		idleStartTime = TimeUtils.nanoTime();
		//to keep track of when transition animation should be played for idle, if 3 then coming out of squat and ignore this
		//if 4 then landing, ignore this
		if (idleTransitionCounter != 3 && idleTransitionCounter != 4) {
			idleTransitionCounter = 0;
		}
		runState = RunState.IDLE;
	}
	
	/**
	 * Allow debug rendering of player's last attack damage zone. Other entity hitboxes are rendered with code in the Entity
	 * class. Enemies with special attack zones should also use a similar method to this one.
	 */
	@Override
	public void debugRender(ShapeRenderer shape) {
		super.debugRender(shape);
		shape.rect(attackHitBox.x, attackHitBox.y, attackHitBox.width, attackHitBox.height);
	}
	
}