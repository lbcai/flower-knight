package lbcai.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.Facing;

public class EnemyPBeetle extends Enemy {
	
	public EnemyPBeetle(Platform platform, Level level) {
		super(platform, level);
	}
	
	@Override
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
			
			//makes the floating bob up and down
			final float elapsedTime = Utils.secondsSince(startTime);
			//multiplier of amplitude = 1 + sin(2 PI elapsedTime / period)
			final float floatMultiplier = 1 + MathUtils.sin(MathUtils.PI2 * (elapsedTime / Constants.floatpBeetlePeriod));
			position.y = platform.top + Constants.pBeetleEyeHeight.y + (Constants.floatpBeetleAmplitude * floatMultiplier);
			
			hitBox = new Rectangle(
					position.x - collisionRadius.x,
					position.y - collisionRadius.y,
					2 * collisionRadius.x,
					2 * collisionRadius.y);
			
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
