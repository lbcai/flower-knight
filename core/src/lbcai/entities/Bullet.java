package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.HitState;

public class Bullet extends Entity {
	
	private final Facing facing;
	private long startTime;
	private Vector2 targetPath;
	private float angle;
	private int type;
	
	public Bullet(Level level, Vector2 position, Facing facing, int damage, int type) {
		
		touchDmg = true;
		knockback = false;
		
		this.level = level;
		
		region = Assets.instance.bulletAssets.bulletAnim.getKeyFrame(0);
		this.position = position.cpy();
		this.facing = facing;
		this.startTime = TimeUtils.nanoTime();
		this.damage = damage;
		this.range = damage/2;
		this.type = type;
		inactive = false;
		
		if (type == 0) {
			moveSpeed = Constants.bulletMoveSpeed;
		} else if (type == 1) {
			moveSpeed = Constants.arrowMoveSpeed;
		}
		
		Vector2 playerHitBox = new Vector2();
		level.getPlayer().hitBox.getCenter(playerHitBox);
		//make a vector from bullet origin point to player (target) and normalize to a unit vector, also get angle of vector
		//relative to x-axis so we know what angle to tilt the sprite at
		if (facing == Facing.LEFT) {
			//if bullet is facing left, the player is to the left of the enemy shooting bullet & the bullet itself
			
			//if the player is walking away, aim a little higher
			if (level.getPlayer().velocity.x < -600) {
				this.targetPath = new Vector2(position.x - playerHitBox.x, position.y - (playerHitBox.y + 30)).nor();
			} else {
				this.targetPath = new Vector2(position.x - playerHitBox.x, position.y - playerHitBox.y).nor();
			}
			
		} else {
			//if bullet facing right, the player is to the right 
			if (level.getPlayer().velocity.x > 600) {
				this.targetPath = new Vector2(playerHitBox.x - position.x, (playerHitBox.y + 30) - position.y).nor();
			} else {
				this.targetPath = new Vector2(playerHitBox.x - position.x, playerHitBox.y - position.y).nor();
			}
			
		}
	
		this.angle = targetPath.angleDeg();
		
		//(bottom left x, bottom left y, width, height)
		hitBox = new Rectangle(
				position.x - Constants.bulletCenter.x,
				position.y - Constants.bulletCenter.y,
				1.5f * Constants.bulletCenter.x,
				1.5f * Constants.bulletCenter.y);
		
	}
	
	@Override
	public void update(float delta) {
		//set position of bullet based on the aim vector established above
		if (facing == Facing.LEFT) {
			position.x -= targetPath.x * moveSpeed * delta;
			position.y -= targetPath.y * moveSpeed * delta;
		} else {
			position.x += targetPath.x * moveSpeed * delta;
			position.y += targetPath.y * moveSpeed * delta;
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
			inactive = true;
		}

		hitBox.set(
				position.x - Constants.bulletCenter.x,
				position.y - Constants.bulletCenter.y,
				1.5f * Constants.bulletCenter.x,
				1.5f * Constants.bulletCenter.y);
		
		detectHitPlayer(level.getPlayer());
		
	}
	
	@Override
	public void render(SpriteBatch batch) {

		float animTime = Utils.secondsSince(startTime);
		if (type == 0) {
			region = Assets.instance.bulletAssets.bulletAnim.getKeyFrame(animTime);	
		} else if (type == 1) {
			//wasp arrow placeholder graphic here
			region = Assets.instance.bulletAssets.bulletAnim.getKeyFrame(animTime);	
		}
		
		
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
	
	public void detectHitPlayer(Player player) {
		if (hitBox.overlaps(player.hitBox)) {
			//facing: this is the direction that the player is on the enemy
			if (player.position.x > position.x) {
				//player is to enemy's right
				if (player.hitState == HitState.NOHIT) {
					if (touchDmg == true) {
						doesDamage(player, Facing.RIGHT);
					}
					
					if (knockback == false) {
						player.flinch(Facing.RIGHT);
					} else {
						player.knockedDown();
					}
				}
				
			} else {
				if (player.hitState == HitState.NOHIT) {
					if (touchDmg == true) {
						doesDamage(player, Facing.LEFT);
					}
					
					if (knockback == false) {
						player.flinch(Facing.LEFT);
					} else {
						player.knockedDown();
					}
				}
				
			}
			
		}
	}
	
	public void doesDamage(Player player, Facing facing) {
		//touch damage method

		int damageInstance = (int) (Math.random() * ((damage + range) - 
				(damage - range) + 1) + 
				(damage - range));
		player.health -= damageInstance;
		level.spawnDmgNum(player.position, damageInstance, facing);
		level.spawnHitEffect(player.hitBox, facing, 2);
		player.hitState = HitState.IFRAME;
		
		inactive = true;
		
	}
	
	@Override
	public boolean isExpired() {
		if (inactive == true) {
			return true;
		} 
		return false;
	}
}
