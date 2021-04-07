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
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;

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
	float moveSpeed;
	Level level;
	//public so level can determine where to draw effects in some cases
	public Vector2 eyeHeight;

	public void debugRender(ShapeRenderer shape) {

		shape.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);

	}
	
	void move(float delta, Facing facing) {
		if (facing == Facing.LEFT) {
			//move left
			position.x -= moveSpeed * delta;
		} else {
			//move right
			position.x += moveSpeed * delta;
		}
	}
	
	void jump(Facing facing) {
		
	}
	
	boolean landOnPlatform(Platform platform) {
		boolean leftSideFootOnPlatform = false;
		boolean rightSideFootOnPlatform = false;
		boolean bothFootOnPlatform = false;
		
		if ((lastFramePosition.y - eyeHeight.y) >= platform.top && 
				(position.y - eyeHeight.y) < platform.top) {
			//since the player position is marked by the center of the head and this is basically in the center of the texture,
			//the "origin" is 0,0 in the center of the texture. we need to subtract half the stance width to get the edge of each
			//foot, then add on one side and subtract on the other.
			float leftSideFoot = position.x - (hitBox.width / 2);
			float rightSideFoot = position.x + (hitBox.width / 2);
			
			leftSideFootOnPlatform = (platform.left < leftSideFoot && platform.right > leftSideFoot);
			rightSideFootOnPlatform = (platform.left < rightSideFoot && platform.right > rightSideFoot);
			//technically the platform is so tiny it is smaller than the stance width.
			bothFootOnPlatform = (platform.left > leftSideFoot && platform.right < rightSideFoot);
		}
		//return true if one is true, else return false
		return leftSideFootOnPlatform || rightSideFootOnPlatform || bothFootOnPlatform;
	}

	
}
