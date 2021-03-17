package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;

public class Item {
	
	final public Vector2 position;
	private Vector2 lastPosition = new Vector2();
	private Vector2 startPosition = new Vector2();
	private Vector2 velocity;
	private TextureRegion region;
	private long startTime;
	private int counterUp = 0;
	private Array<Platform> platforms;
	private int expire;
	private boolean falling = true;
	private Player player;
	private int rotation;
	private long expireTime;
	
	public Item(Vector2 position, Level level) {
		this.position = position;

		//don't set lastPosition to the same value in memory as position or changing one changes the other
		lastPosition.x = position.x;
		lastPosition.y = position.y;
		
		//start the item with upward velocity so it pops out of monsters/boxes/etc.
		velocity = new Vector2(0, 500);
		platforms = level.getPlatforms();
		//0 = not expired, 1 = using, 2 = expired
		expire = 0;
		player = level.getPlayer();
		region = Assets.instance.lifeAssets.lifeAnim.getKeyFrame(0);
		startTime = TimeUtils.nanoTime();
	}
	
	public void update(float delta) {
		
		lastPosition.set(position);
		
		if (falling == true) {
			if (rotation < 360) {
				rotation += 15;
			}
			
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

	}
	
	public void render(SpriteBatch batch) {
		
		if (expire == 0) {
			float animTime = Utils.secondsSince(startTime);
			region = Assets.instance.lifeAssets.lifeAnim.getKeyFrame(animTime);
		} else {
			float expireAnimTime = Utils.secondsSince(expireTime);
			region = Assets.instance.lifeAssets.lifeFadeAnim.getKeyFrame(expireAnimTime);
			if (Assets.instance.lifeAssets.lifeFadeAnim.isAnimationFinished(expireAnimTime)) {
				expire = 3;
			}
		}
		
		
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
		
	}
	
	public void use() {
		//this is a full heal
		player.health = player.maxHealth;
		expire = 1;
		expireTime = TimeUtils.nanoTime();
	}
	
	void setExpireBoolean() {
		expire = 2;
		expireTime = TimeUtils.nanoTime();
	}
	
	public boolean isExpired() {
		if (expire == 3) {
			return true;
		}
		return false;
	}
	
}
