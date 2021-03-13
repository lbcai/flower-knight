package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;

public class Item {
	
	final public Vector2 position;
	private Vector2 lastPosition = new Vector2();
	private Vector2 startPosition = new Vector2();
	private Vector2 velocity;
	private TextureRegion region = Assets.instance.lifeAssets.lifeAnim.getKeyFrame(0);
	private long startTime;
	private int counterUp = 0;
	private Array<Platform> platforms;
	public boolean expire;
	private boolean falling = true;
	private Player player;
	
	public Item(Vector2 position, Level level) {
		this.position = position;

		//don't set lastPosition to the same value in memory as position or changing one changes the other
		lastPosition.x = position.x;
		lastPosition.y = position.y;
		
		velocity = new Vector2(0, 0);
		platforms = level.getPlatforms();
		expire = false;
		player = level.getPlayer();
	}
	
	public void update(float delta) {
		
		lastPosition.set(position);
		
		if (falling == true) {
			
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
		
		if (position.y < Constants.killPlane || MathUtils.nanoToSec * startTime > Constants.itemExpireTime) {
			expire = true;
		}
		
	}
	
	public void render(SpriteBatch batch) {
		
		float animTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - startTime);
		region = Assets.instance.lifeAssets.lifeAnim.getKeyFrame(animTime);
		
		batch.draw(region.getTexture(), 
				(position.x - Constants.itemCenter.x), 
				(position.y - Constants.itemCenter.y), 
				0, 
				0, 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				1, 
				1, 
				0, 
				region.getRegionX(), 
				region.getRegionY(), 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				false, 
				false);
		
	}
	
	public void use() {
		//this is a full heal (will have to find a way to fill to full health even if max health is adjusted)
		player.health = Constants.baseHealth;
	}
	
}
