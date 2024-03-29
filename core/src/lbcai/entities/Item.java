package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;

public class Item implements Renderable, Updatable {
	
	protected Vector2 lastPosition = new Vector2();
	protected Vector2 startPosition = new Vector2();
	protected Vector2 velocity;

	protected long startTime;
	protected int counterUp = 0;
	protected Array<Platform> platforms;
	protected int expire;
	protected boolean falling = true;
	protected Player player;
	protected int rotation;
	protected float alpha = 255f/255f;
	//render order, must go above player and under grass
	int zValue = 6;
	
	Rectangle hitBox;
	Vector2 position = new Vector2();
	TextureRegion region;
	
	public Item(Vector2 position, Level level) {
		region = Assets.instance.lifeAssets.healLargeAnim.getKeyFrame(0);
		this.position = position.cpy();

		//don't set lastPosition to the same value in memory as position or changing one changes the other
		lastPosition = position.cpy();
		
		//start the item with upward velocity so it pops out of monsters/boxes/etc.
		velocity = new Vector2(0, 500);
		platforms = level.getPlatforms();
		//0 = not expired, 1 = using, 2 = expired
		expire = 0;
		player = level.getPlayer();
		startTime = TimeUtils.nanoTime();
		
		hitBox = new Rectangle(
				position.x - Constants.itemCenter.x,
				position.y - Constants.itemCenter.y,
				Constants.itemCenter.x * 2,
				Constants.itemCenter.y * 2);
		
	}
	
	public void update(float delta) {

		lastPosition.set(position);
		
		if (falling == true) {
			rotation += 15;
			
			velocity.y -= delta * Constants.worldGravity;
			position.mulAdd(velocity, delta);

			for (Platform platform : platforms) {
				if (position.x + Constants.itemCenter.x > platform.left && position.x - Constants.itemCenter.x < platform.right) {
					if (lastPosition.y - Constants.itemCenter.y >= platform.top && position.y - Constants.itemCenter.y < platform.top) {
						position.y = platform.top + Constants.itemCenter.y;
						velocity.y = 0;
						startPosition.x = position.x;
						startPosition.y = position.y;
						falling = false;
					}
				}
			}
		} else {
			//code to make item float up and down over time while facing right side up
			rotation = 0;
			if (counterUp == 0) {
				position.lerp(new Vector2(position.x, position.y + 25), 0.01f);
				if (position.y >= startPosition.y + 10) {
					counterUp = 1;
				}
			} else if (counterUp == 1) {
				position.lerp(new Vector2(position.x, position.y - 25), 0.01f);
				if (position.y <= startPosition.y - 10) {
					counterUp = 0;
				}
			}
		}
		
		//if dropping off the map or sitting for 5 minutes, play expire animation and remove item from array
		if (expire == 0) {
			if (position.y < Constants.killPlane || Utils.secondsSince(startTime) > Constants.itemExpireTime) {
				setExpireBoolean();
			}
		} else if (expire == 1) {
			//if being used, send the item to the player while playing fade out animation
			position.lerp(player.position, 0.1f);
		}
		
		hitBox = new Rectangle(
				position.x - Constants.itemCenter.x,
				position.y - Constants.itemCenter.y,
				Constants.itemCenter.x * 2,
				Constants.itemCenter.y * 2);

	}
	
	public void render(SpriteBatch batch) {
		float animTime = Utils.secondsSince(startTime);
		region = Assets.instance.lifeAssets.healLargeAnim.getKeyFrame(animTime);
		if (expire != 0) {
			region = Assets.instance.lifeAssets.healLargeAnim.getKeyFrame(animTime);
			alpha -= 15f/255f;
			if (alpha <= 0f/255f) {
				expire = 3;
			}
		}
		
		batch.setColor(1, 1, 1, alpha);
		batch.draw(region.getTexture(), 
				(position.x - Constants.itemCenter.x), 
				(position.y - Constants.itemCenter.y), 
				Constants.itemCenter.x, 
				Constants.itemCenter.y, 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				1, 
				1, 
				rotation, 
				region.getRegionX(), 
				region.getRegionY(), 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				false, 
				false);
		batch.setColor(1, 1, 1, 1);
	}
	
	public void use() {
		//this is a full heal
		//check expire int to make sure player cannot use the item more than one time
		if (expire != 1) {
			expire = 1;
			player.health = player.maxHealth;
		}
	}
	
	void setExpireBoolean() {
		expire = 2;
	}
	
	public boolean isExpired() {
		if (expire == 3) {
			return true;
		}
		return false;
	}
	
	public int getzValue() {
		return zValue;
	}
	
	public int getyValue() {
		//add 2 to keep the entity in the right render layer compared to grass, which must have a y value lower than front 
		//platform due to overlap, so it has a little artificial padding added
		//entities are +2 so +3 keeps the items above entities
		return (int) (position.y - Constants.itemCenter.y + 3);
	}
	
	public void debugRender(ShapeRenderer shape) {
		shape.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
	}
	
}
