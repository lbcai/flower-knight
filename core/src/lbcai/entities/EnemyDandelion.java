package lbcai.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;

public class EnemyDandelion extends Enemy {
	
	private Player target;
	private long bulletShotLastTime;
	

	public EnemyDandelion(Platform platform, Player target) {
		super(platform);
		this.target = target;
		this.eyeHeight = Constants.dandelionEyeHeight;
		this.moveSpeed = Constants.dandelionMoveSpeed;
		this.leftIdleAnim = Assets.instance.dandelionAssets.idleLeftAnim;
		this.collisionRadius = Constants.dandelionCollisionRadius;
		this.bulletShotLastTime = TimeUtils.nanoTime();
		this.HP = Constants.dandelionHP;
		position = new Vector2(platform.left + (platform.right - platform.left) / 2, platform.top + eyeHeight.y);
	}
	
	/**
	 * Make the dandelion turn to face the player. Every Constants.bulletCooldown time, shoot a projectile at the player.
	 */
	@Override
	public void update(float delta) {
		if (target.position.x < position.x) {
			facing = Facing.LEFT;
		} else {
			facing = Facing.RIGHT;
		}
		
		Vector2 bulletPosition;
		float bulletWaitTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - bulletShotLastTime);
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
			target.level.spawnBullet(bulletPosition, facing);
			bulletShotLastTime = TimeUtils.nanoTime();
		}
		
	}
	
}
