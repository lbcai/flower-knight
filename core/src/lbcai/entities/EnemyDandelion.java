package lbcai.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.Facing;

public class EnemyDandelion extends Enemy {
	
	private Player target;
	private long bulletShotLastTime;
	

	public EnemyDandelion(Platform platform, Level level) {
		super(platform, level);
		this.target = level.getPlayer();
		this.level = level;
		this.eyeHeight = Constants.dandelionEyeHeight;
		this.moveSpeed = Constants.dandelionMoveSpeed;
		region = Assets.instance.dandelionAssets.idleLeftAnim.getKeyFrame(0);
		this.collisionRadius = Constants.dandelionCollisionRadius;
		this.bulletShotLastTime = TimeUtils.nanoTime();
		this.health = Constants.dandelionHP;
		this.maxHealth = Constants.dandelionHP;
		this.damage = Constants.dandelionDamage;
		this.range = damage/2;
	
		hitBox = new Rectangle(
				position.x - collisionRadius.x,
				position.y - eyeHeight.y,
				2 * collisionRadius.x,
				2 * collisionRadius.y);
	}
	
	/**
	 * Make the dandelion turn to face the player. Every Constants.bulletCooldown time, shoot a projectile at the player.
	 */
	@Override
	public void update(float delta) {
		
		if (inactive == false) {
			
			hitBox = new Rectangle(
					position.x - collisionRadius.x,
					position.y - eyeHeight.y,
					2 * collisionRadius.x,
					2 * collisionRadius.y);
			
			if (target.position.x < position.x) {
				facing = Facing.LEFT;
			} else {
				facing = Facing.RIGHT;
			}
			
			Vector2 bulletPosition;
			float bulletWaitTime = Utils.secondsSince(bulletShotLastTime);
			//check if bullet is not on cooldown and if player is not below the dandelion (can't see below its own platform) before
			//shooting at player.
			if (bulletWaitTime > Constants.bulletCooldown && (target.position.y > position.y - Constants.dandelionEyeHeight.y)) {
				if (facing == Facing.LEFT) {
					bulletPosition = new Vector2(
							position.x - Constants.dandelionMouth.x,
							position.y);
				} else {
					bulletPosition = new Vector2(
							position.x + Constants.dandelionMouth.x,
							position.y);
				}
				//target = player. get the level the player is in and spawn a bullet in the level.
				level.spawnBullet(bulletPosition, facing, damage);
				bulletShotLastTime = TimeUtils.nanoTime();
			}
			
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
}