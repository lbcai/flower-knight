package lbcai.entities;

import com.badlogic.gdx.graphics.Color;
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
	private Vector2 origPosition;
	private long startTime;
	private int rotation;
	private float scale;
	private Facing facing;
	private boolean flip;
	
	public DustCloud(Vector2 position, Facing facing) {
		this.position = position;
		this.origPosition = new Vector2();
		this.origPosition.x = position.x;
		this.origPosition.y = position.y;
		//if player is facing left, cloud goes right (goes opposite direction to facing)
		this.facing = facing;
		startTime = TimeUtils.nanoTime();
		flip = false;
		rotation = 0;
		scale = 1.5f;
		
	}
	
	public void render(SpriteBatch batch) {
		
		//decided to add transparency in sprites for fade out, tried using batch.setColor to control alpha
		//but it isn't as smooth or nice
		float dustTime = MathUtils.nanoToSec * (TimeUtils.nanoTime() - startTime);
		TextureRegion region = Assets.instance.dustAssets.dustAnim.getKeyFrame(dustTime);

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
				flip, 
				false);

	}
	
	public void update(float delta) {

		//the equation for a parabola is y = ax^2 - b
		position.y = (float) (0.1 * (Math.pow(Math.abs(position.x - origPosition.x), 2)) - Math.abs(origPosition.y));
		//position.y = (float) (Math.sin(position.x - origPosition.x) - Math.abs(origPosition.y));
		if (facing == Facing.LEFT) {
			flip = true;
			//go the opposite direction of player facing
			position.x += 1;
			rotation += 20;
			
		} else {
			position.x -= 1;
			rotation -= 20;
		}
		//shrink the dust cloud as it flies off
		scale -= 0.003 * Math.abs(position.x - origPosition.x);
	}
	
	public boolean isFinished() {
		if (MathUtils.nanoToSec * (TimeUtils.nanoTime() - startTime) > Constants.dustDuration) {
			return true;
		} else {
			return false;
		}
	}
	
}
