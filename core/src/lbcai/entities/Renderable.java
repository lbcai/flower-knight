package lbcai.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Renderable {

	public void render(SpriteBatch batch);
	
	public int getzValue();
	
	public int getyValue();
}
