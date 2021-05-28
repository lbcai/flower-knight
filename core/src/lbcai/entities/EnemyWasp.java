package lbcai.entities;

import java.util.Arrays;

import com.badlogic.gdx.math.Vector2;

import lbcai.flowerknight.Level;
import lbcai.util.Constants;
import lbcai.util.Enums.EnemyType;
import lbcai.util.Enums.JumpState;

public abstract class EnemyWasp extends EnemyPBeetle {

	//occasionally push the enemy to return to the home platform.
	boolean goHome;
	Vector2 homeTracker;
	
	public EnemyWasp(Platform platform, Level level) {
		super(platform, level);
		goHome = false;
		homeTracker = new Vector2(platform.centerX, platform.top + eyeHeight.y);
		enemyType = EnemyType.WASP;
	}
	
	void respondToCall(float delta, Vector2 targetPosition) {
		//for non-scouts only. (grounded wasp units)
		if (!((targetPosition.x - (2 * hitBox.width)) < position.x && position.x < (targetPosition.x + (2 * hitBox.width)))) {
			if (position.x > targetPosition.x) {
				moveLeft(delta);
			} else if (position.x < targetPosition.x) {
				moveRight(delta);
			}
		} else if (position.y != targetPosition.y + eyeHeight.y) {
			if (targetPosition.y < position.y) {
				if (jumpState == JumpState.GROUNDED) {
					downJump();
				} else if (jumpState == JumpState.JUMPING) {
					startJump();
				}
			} else if (targetPosition.y > position.y) {
				velocity.y += Constants.jumpSpeed;
				jumpState = JumpState.JUMPING;
			}
		} 
	}
	
	void harvestFlower() {
		//give the wasps interesting idle behaviors: in this case if a working class wasp sees a flower monster, it will harvest
		//pollen and heal from looting 
		
	}
	
}
