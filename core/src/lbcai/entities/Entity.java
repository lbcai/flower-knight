package lbcai.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * 
 * Entities that use a hitbox and are capable of interacting with each other.
 * Superclass includes debug renderer for purpose of checking hitboxes, which are normally invisible.
 * 
 * @author lbcai
 *
 */

public abstract class Entity {
	Rectangle hitBox;
	public Vector2 position;
	TextureRegion region;
	int damage;
	int range;
	int health;
	int maxHealth;

	public void debugRender(ShapeRenderer shape) {

		shape.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);

	}

	
}
