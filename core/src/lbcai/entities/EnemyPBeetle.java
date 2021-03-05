package lbcai.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.util.Constants;

public class EnemyPBeetle extends Enemy {
	
	public EnemyPBeetle(Platform platform) {
		super(platform);
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		//makes the floating bob up and down
		final float elapsedTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - startTime);
		//multiplier of amplitude = 1 + sin(2 PI elapsedTime / period)
		final float floatMultiplier = 1 + MathUtils.sin(MathUtils.PI2 * (elapsedTime / Constants.floatpBeetlePeriod));
		position.y = platform.top + Constants.pBeetleEyeHeight.y + (Constants.floatpBeetleAmplitude * floatMultiplier);
		
	}

}
