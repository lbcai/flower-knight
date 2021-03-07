package lbcai.util;

import com.badlogic.gdx.math.Vector2;

public class Utils {
	
	//copied the libgdx lerp method but removed the y component. this allows player to drop off platforms properly when attacking
	//and moving off a platform in the course of the attack.
	public static Vector2 lerpX (Vector2 position, Vector2 target, float alpha) {
		final float invAlpha = 1.0f - alpha;
		position.x = (position.x * invAlpha) + (target.x * alpha);
		return position;
	}
	
}
