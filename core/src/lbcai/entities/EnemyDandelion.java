package lbcai.entities;

import lbcai.util.Assets;
import lbcai.util.Constants;

public class EnemyDandelion extends Enemy {

	public EnemyDandelion(Platform platform) {
		super(platform);
		this.eyeHeight = Constants.dandelionEyeHeight;
		this.moveSpeed = Constants.dandelionMoveSpeed;
		this.leftIdleAnim = Assets.instance.dandelionAssets.idleLeftAnim;
	}

}
