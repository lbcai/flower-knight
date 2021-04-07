package lbcai.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import lbcai.flowerknight.Level;

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
	public Vector2 lastFramePosition;
	public Vector2 velocity;
	TextureRegion region;
	int damage;
	int range;
	int health;
	int maxHealth;
	float alpha = 255f/255f;
	Level level;

	public void debugRender(ShapeRenderer shape) {

		shape.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);

	}

	
}
