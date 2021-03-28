package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.Facing;

public class Bullet extends Entity {
	
	private final Facing facing;
	private long startTime;
	private Boolean flipx;
	private Level level;
	//For deciding when the clean up the bullet object.
	public Boolean active;
	private Vector2 targetPath;
	private float angle;
	
	public Bullet(Level level, Vector2 position, Facing facing, int damage) {
		region = Assets.instance.bulletAssets.bulletAnim.getKeyFrame(0);
		this.position = position;
		this.facing = facing;
		this.startTime = TimeUtils.nanoTime();
		this.level = level;
		this.damage = damage;
		this.range = damage/2;
		active = true;
		
		Vector2 playerHitBox = new Vector2();
		level.getPlayer().hitBox.getCenter(playerHitBox);
		//make a vector from bullet origin point to player (target) and normalize to a unit vector, also get angle of vector
		//relative to x-axis so we know what angle to tilt the sprite at
		if (facing == Facing.LEFT) {
			this.targetPath = new Vector2(position.x - playerHitBox.x, position.y - playerHitBox.y).nor();
		} else {
			this.targetPath = new Vector2(playerHitBox.x - position.x, playerHitBox.y - position.y).nor();
		}
	
		this.angle = targetPath.angleDeg();
		
		//(bottom left x, bottom left y, width, height)
		hitBox = new Rectangle(
				position.x - Constants.bulletCenter.x,
				position.y - Constants.bulletCenter.y,
				1.5f * Constants.bulletCenter.x,
				1.5f * Constants.bulletCenter.y);
		
	}
	
	public void update(float delta) {
		//set position of bullet based on the aim vector established above
		if (facing == Facing.LEFT) {
			position.x -= targetPath.x * Constants.bulletMoveSpeed * delta;
			position.y -= targetPath.y * Constants.bulletMoveSpeed * delta;
		} else {
			position.x += targetPath.x * Constants.bulletMoveSpeed * delta;
			position.y += targetPath.y * Constants.bulletMoveSpeed * delta;
		}
		

		
		//get width of screen from level viewport
		final float worldWidth = level.getViewport().getWorldWidth();
		//get camera's horizontal position
		final float cameraX = level.getViewport().getCamera().position.x;
		//check if bullet goes off the screen, change to not active if so, in Level we destroy inactive bullets.
		//this also has the benefit of dandelions not shooting at player until you share a screen with them.
		//decided to add an extra half screenwidth offscreen before deletion in case the player is moving fast and the bullet
		//comes back on screen. it should still be there or else it's immersion-breaking
		if (position.x < cameraX - worldWidth || position.x > cameraX + worldWidth) {
			active = false;
		}
		
		hitBox = new Rectangle(
				position.x - Constants.bulletCenter.x,
				position.y - Constants.bulletCenter.y,
				1.5f * Constants.bulletCenter.x,
				1.5f * Constants.bulletCenter.y);
		
	}
	
	public void render(SpriteBatch batch) {

		float animTime = Utils.secondsSince(startTime);
		region = Assets.instance.bulletAssets.bulletAnim.getKeyFrame(animTime);	
		
		if (facing == Facing.LEFT) {
			flipx = false;
		} else if (facing == Facing.RIGHT) {
			flipx = true;
		}
		
		batch.draw(region.getTexture(), 
				(position.x - Constants.bulletCenter.x), 
				(position.y - Constants.bulletCenter.y), 
				Constants.bulletCenter.x, 
				Constants.bulletCenter.y, 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				1, 
				1, 
				angle, 
				region.getRegionX(), 
				region.getRegionY(), 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				flipx, 
				false);
		
	}
	
	public void doesDamage(Player player, Facing facing) {
		//touch damage method
		int damageInstance = (int) (Math.random() * ((damage + range) - 
				(damage - range) + 1) + 
				(damage - range));
		player.health -= damageInstance;
		player.level.spawnDmgNum(player.position, damageInstance, facing);
		active = false;
	}
	
}
