package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;

public class DustCloud {
	
	private Vector2 position;
	private long startTime;
	private int rotation;
	private int scale;
	private Facing facing;
	
	public DustCloud(Vector2 position, Facing facing) {
		this.position = position;
		//if player is facing left, cloud goes right (goes opposite direction to facing)
		this.facing = facing;
		startTime = TimeUtils.nanoTime();

		rotation = 0;
		scale = 1;
		
	}
	
	public void render(SpriteBatch batch) {
		
		TextureRegion region = Assets.instance.dustAssets.dust;
		
		batch.draw(region.getTexture(), 
				(position.x - Constants.dustCenter.x), 
				(position.y - Constants.dustCenter.y), 
				Constants.dustCenter.x, 
				Constants.dustCenter.y, 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				scale, 
				scale, 
				rotation, 
				region.getRegionX(), 
				region.getRegionY(), 
				region.getRegionWidth(), 
				region.getRegionHeight(), 
				false, 
				false);
	}
	
	public void update(float delta) {

		position.y = position.y + (Constants.dustAmplitude);
		if (facing == Facing.LEFT) {
			//go the opposite direction of player facing
			position.x += 2;
			rotation += 2;
		} else {
			position.x -= 2;
			rotation -= 2;
		}
		
	}
	
	public boolean isFinished() {
		if (MathUtils.nanoToSec * (TimeUtils.nanoTime() - startTime) > Constants.dustDuration) {
			return true;
		} else {
			return false;
		}
	}
	
}
