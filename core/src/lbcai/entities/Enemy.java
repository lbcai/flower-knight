package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.ai.GdxAI;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.HitState;
import lbcai.util.Enums.LockState;
import lbcai.util.Enums.RunState;

public class Enemy extends Entity {
	//extend later when adding more enemy types
	final Platform platform;
	Facing facing;
	final long startTime;
	Vector2 eyeHeight;
	float moveSpeed;
	Vector2 collisionRadius;
	public int health;
	HitState hitState;
	RunState runState;
	LockState lockState;
	private long hitStartTime;

	
	//placeholder drop list for basic enemy type: 
	List<Integer> dropTable;
	
	
	//default enemy type will be a potato beetle
	public Enemy(Platform platform) {
		region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
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
		position = new Vector2(platform.left, platform.top + eyeHeight.y);
		facing = Facing.RIGHT;
		startTime = TimeUtils.nanoTime();
		
		hitBox = new Rectangle(
				position.x - collisionRadius.x,
				position.y - collisionRadius.y,
				2 * collisionRadius.x,
				2 * collisionRadius.y);
		
		//set drop table here so different enemy classes can have their own
		dropTable = Arrays.asList(0, 1);
		
	}

	public void update(float delta) {
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
		
	}
	
	public void render(SpriteBatch batch) {
		
		Boolean flipx = false;
		
		//placeholder animations
		if (facing == Facing.LEFT) {
			if (runState == RunState.IDLE) {
				//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
			} else if (runState == RunState.RUN) {
				//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
			}
			
			if (hitState == HitState.IFRAME) {
				//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
				if (Utils.secondsSince(hitStartTime) > Constants.enemyFlinchTime) {
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
				if (Utils.secondsSince(hitStartTime) > Constants.enemyFlinchTime) {
					hitState = HitState.NOHIT;
				}
			}
			
			if (lockState == LockState.LOCK) {
				//region = Assets.instance.pBeetleAssets.idleLeftAnim.getKeyFrame(0);
			}
			
			flipx = true;
		}
		
		
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
	}
	
	public void isDamaged(int damage) {
		health -= damage;
		// using iframe to denote flinch animation but no actual iframe for mobs
		hitState = HitState.IFRAME;
		hitStartTime = TimeUtils.nanoTime();
	}
	
	public void doesDamage(Player player, Facing facing) {
		//touch damage method
		int damageInstance = (int) (Math.random() * ((damage + range) - 
				(damage - range) + 1) + 
				(damage - range));
		player.health -= damageInstance;
		player.level.spawnDmgNum(player.position, damageInstance, facing);
	}
	
	public int rollDrop() {
		int index = (int) (Math.random() * dropTable.size()); 
		return dropTable.get(index);
	}
}
