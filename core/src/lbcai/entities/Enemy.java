package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Arrays;
import java.util.List;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.EnemyType;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.HitState;
import lbcai.util.Enums.JumpState;
import lbcai.util.Enums.LockState;
import lbcai.util.Enums.RunState;

public abstract class Enemy extends Entity {
	//extend later when adding more enemy types
	final Platform platform;
	
	final long startTime;
	Vector2 collisionRadius;
	public int health;
	Player target;
	
	//countdown til respawn
	long inactiveTimer;

	//zone for enemy to see in (for player detection)
	Rectangle aggroRange;

	//placeholder drop list for basic enemy type: 
	List<Integer> dropTable;
	List<Integer> wanderStateRandomizer;
	int wanderState;
	long wanderTime;
	
	//enum to label enemy types e.g. wasp, flower, etc.
	EnemyType enemyType;
	
	long hitPlayerTime;
	int hitPlayerRecently;
	
	//default enemy type will be a potato beetle
	public Enemy(Platform platform, Level level) {
		
		//default to false because most attacks do not knock back the player significantly
		knockback = false;
		hitPlayerRecently = 0;
		//default to none in case, placeholder
		enemyType = EnemyType.NONE;
		
		//start enemy in idle position (0)
		wanderState = 0;
		wanderTime = 0;
		
		target = level.getPlayer();
		touchDmg = true;
		inactive = false;
		region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
		this.level = level;
		this.platform = platform;
		this.eyeHeight = Constants.pBeetleEyeHeight;
		this.moveSpeed = Constants.enemyMoveSpeed;
		this.collisionRadius = Constants.pBeetleCollisionRadius;
		this.health = Constants.pBeetleHP;
		this.maxHealth = Constants.pBeetleHP;
		this.damage = Constants.pBeetleDamage;
		this.range = damage/2;
		hitState = HitState.NOHIT;
		runState = RunState.IDLE;
		lockState = LockState.FREE;
		jumpState = JumpState.GROUNDED;
		
		//spawn on a random spot on the surface of this platform
		position = new Vector2((MathUtils.random() * (platform.right - platform.left + 1) + platform.left), platform.top + eyeHeight.y);
		lastFramePosition = position.cpy();
		velocity = new Vector2();
		velocity.setZero();
		facing = Facing.RIGHT;
		startTime = TimeUtils.nanoTime();
		
		hitBox = new Rectangle(
				position.x - collisionRadius.x,
				position.y - collisionRadius.y,
				2 * collisionRadius.x,
				2 * collisionRadius.y);
		
		aggroRange = new Rectangle(
				position.x - Constants.aggroRadius.x,
				position.y - Constants.aggroRadius.y / 2,
				2 * Constants.aggroRadius.x,
				Constants.aggroRadius.y);


		
		//set drop table here so different enemy classes can have their own
		dropTable = Arrays.asList(0, 1);
		
		//make a list of possible states for enemy to cycle between when not chasing player
		wanderStateRandomizer = Arrays.asList(0, 1, 2);
		
	}

	public void update(float delta) {
		
		if (inactive == false) {
			//if not dead, do your thing

			switch (facing) {
			case LEFT:
				position.x -= moveSpeed * delta;
				break;
			case RIGHT:
				position.x += moveSpeed * delta;
			}
			
			if (position.x < platform.left) {
				position.x = platform.left;
				facing = Facing.RIGHT;
			} else if (position.x > platform.right) {
				position.x = platform.right;
				facing = Facing.LEFT;
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


			
			if (health <= 0) {
				level.dropItem(this);
				inactive = true;
				inactiveTimer = TimeUtils.nanoTime();
			}

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
	
	public void render(SpriteBatch batch) {

		if (inactive == false) {
			
			//placeholder animations
			if (facing == Facing.LEFT) {
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
				}
				
				flipx = false;
				
			} else if (facing == Facing.RIGHT) {
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
				
				if (lockState == LockState.LOCK) {
					//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
				}
				
				flipx = true;
			}
		}
		
		
		batch.setColor(1, 1, 1, alpha);
		batch.draw(region.getTexture(), 
				(position.x - eyeHeight.x), 
				(position.y - eyeHeight.y), 
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
	
	public void isDamaged(int damage) {
		health -= damage;
		// using iframe to denote flinch animation but no actual iframe for mobs
		hitState = HitState.IFRAME;
		timeSinceHit = TimeUtils.nanoTime();
		
		velocity.y = Constants.knockbackSpeed.y;
		if (target.position.x < position.x) {
			//being hit from the left
			velocity.x = Constants.knockbackSpeed.x;
			position.x += 20;
		} else {
			//being hit from the right
			velocity.x = -Constants.knockbackSpeed.x;
			position.x -= 20;
		}
		
	}
	
	public void detectHitPlayer(Player player) {
		if (hitBox.overlaps(player.hitBox)) {
			//facing: this is the direction that the player is on the enemy
			if (player.position.x > position.x) {
				//player is to enemy's right
				if (player.hitState == HitState.NOHIT) {
					if (touchDmg == true) {
						doesDamage(player, Facing.RIGHT);
					}
					
					if (knockback == false) {
						player.flinch(Facing.RIGHT);
					} else {
						player.knockedDown();
					}
				}
				
			} else {
				if (player.hitState == HitState.NOHIT) {
					if (touchDmg == true) {
						doesDamage(player, Facing.LEFT);
					}
					
					if (knockback == false) {
						player.flinch(Facing.LEFT);
					} else {
						player.knockedDown();
					}
				}
				
			}
			
		}
	}
	
	public void doesDamage(Player player, Facing facing) {
		//touch damage method
		if (hitPlayerRecently == 0) {
			
			hitPlayerRecently = 1;
			hitPlayerTime = TimeUtils.nanoTime();
			int damageInstance = (int) (Math.random() * ((damage + range) - 
					(damage - range) + 1) + 
					(damage - range));
			player.health -= damageInstance;
			player.level.spawnDmgNum(player.position, damageInstance, facing);
			player.level.spawnHitEffect(player.hitBox, facing, 0);
			player.hitState = HitState.IFRAME;

			
		} else {
			if (Utils.secondsSince(hitPlayerTime) > Constants.iFrameLength) {
				hitPlayerRecently = 0;
				
			}
		}
		
	}
	
	public int rollDrop() {
		int index = (int) (Math.random() * dropTable.size()); 
		return dropTable.get(index);
	}
	
	public void chaseRandomness(float delta) {
		//makes enemy move around back and forth instead of standing on player when enemy does not need to move anywhere else
		//to keep up with player (aggro mode only)
		
		if (MathUtils.random() < 0.02) {
			wanderState = (int) (MathUtils.random() * 2);
		}
		
		if (wanderState == 0) {
			if (position.x < target.position.x - (2 * hitBox.width) || hitBox.x < level.levelBound.x + 5) {
				wanderState = 1;
			} else {
				moveLeft(delta);
			}
			
			
		} else if (wanderState == 1) {
			if (position.x > target.position.x + (2 * hitBox.width) || (hitBox.x + hitBox.width) > 
					(level.levelBound.x + level.levelBound.width - 5)) {
				wanderState = 0;
			} else {
				moveRight(delta);
			}
			
		}
		
	}
	
	/**
	 * Allow debug rendering of enemy's sight range.
	 */
	@Override
	public void debugRender(ShapeRenderer shape) {
		super.debugRender(shape);
		shape.rect(aggroRange.x, aggroRange.y, aggroRange.width, aggroRange.height);
	}
	
}
