package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;

public class Item {
	
	final public Vector2 position;
	private Vector2 lastPosition;
	private Vector2 startPosition;
	private Vector2 velocity;
	private TextureRegion region = Assets.instance.lifeAssets.lifeAnim.getKeyFrame(0);
	private long startTime;
	private int counterUp = 0;
	private Level level;
	
	public Item(Vector2 position, Level level) {
		this.position = position;
		startPosition = position;
		lastPosition = position;
		velocity = new Vector2(0, 0);
		this.level = level;
	}
	
	public void update(float delta) {
		
		lastPosition.set(position);
		
		velocity.y -= delta * Constants.worldGravity;
		position.mulAdd(velocity, delta);
		
		for (Platform platform : level.getPlatforms()) {
			if (position.x > platform.left && position.x < platform.right) {
				if (lastPosition.y > platform.top && position.y < platform.top) {
					position.y = platform.top;
				}
			}
		}

		if (counterUp == 0) {
			position.lerp(new Vector2(position.x, position.y + 25), 0.2f);
			if (position.y == startPosition.y + 15) {
				counterUp = 1;
			}
		} else if (counterUp == 1) {
			position.lerp(new Vector2(position.x, position.y - 25), 0.2f);
			if (position.y == startPosition.y - 15) {
				counterUp = 0;
			}
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
		System.out.println("item effect on level.player");
	}
	
}
