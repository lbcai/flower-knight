package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import lbcai.util.Enums.Facing;

/**
 * Abstract class to group all uninteractable visual effects.
 * @author lbcai
 *
 */
public abstract class Effect {

	TextureRegion region;
	float alpha = 255f/255f;
	Vector2 position;
	Facing facing;
	
	public boolean isExpired() {
		if (alpha <= 0f/255f) {
			return true;
		}
		return false;
	}
	
}
