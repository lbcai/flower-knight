package lbcai.entities;

import lbcai.flowerknight.Level;
import lbcai.util.Constants;

public class EnemyWaspLancer extends EnemyWasp {
	
	
	public EnemyWaspLancer(Platform platform, Level level) {
		super(platform, level);
	}

	public void update(float delta) {
		super.update(delta);
		
		if (responding == 1) {
			if (moveSpeed != Constants.enemyMoveSpeedAggro) {
				moveSpeed = Constants.enemyMoveSpeedAggro;
			}
			respond(delta, targetPosition);
		}
	}
	
	void stab() {
		
	}
	
}
