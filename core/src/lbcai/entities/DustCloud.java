package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import lbcai.flowerknight.Level;
import lbcai.util.Assets;
import lbcai.util.Constants;
import lbcai.util.Utils;
import lbcai.util.Enums.Facing;

public class DustCloud extends Effect {
	
	private Vector2 origPosition;
	private int rotation;
	private float scale;
	private boolean flip;
	private int type;
	
	public DustCloud(Vector2 position, Facing facing, int type, Level level) {
		
		level.getRenderables().add(this);
		
		region = Assets.instance.dustAssets.dust;
		this.position = position;
		this.origPosition = new Vector2();
		this.origPosition.x = position.x;
		this.origPosition.y = position.y;
		//if player is facing left, cloud goes right (goes opposite direction to facing)
		this.facing = facing;
		flip = false;
		rotation = 0;
		scale = 1.5f;
		this.type = type;
	}
	
	public void render(SpriteBatch batch) {
		
		alpha -= 15f/255f;
		batch.setColor(1, 1, 1, alpha);
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
		batch.setColor(1, 1, 1, 1);

	}
	
	public void update(float delta) {
		
		if (type == 0) {
			//ground dust
			//the equation for a parabola is y = ax^2 - b
			position.y = (float) (origPosition.y + (0.1 * (Math.pow(position.x - origPosition.x, 2))));
			//position.y = (float) (Math.sin(position.x - origPosition.x) - Math.abs(origPosition.y));
			//shrink the dust cloud as it flies off
			scale -= 0.003 * Math.abs(position.x - origPosition.x);

		} else if (type == 1) {
			//double jump dust
			position.y -= 1;
			//shrink the dust cloud as it flies off
			scale -= 0.008 * Math.abs(position.x - origPosition.x);

		}
		
		if (facing == Facing.LEFT) {
			flip = true;
			//go the opposite direction of player facing
			position.x += 1;
			rotation += 20;
			
		} else {
			position.x -= 1;
			rotation -= 20;
		}
		
	}
	
	
}
