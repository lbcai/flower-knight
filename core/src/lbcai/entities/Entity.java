package lbcai.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public abstract class Entity {
	Rectangle hitBox;
	boolean DEBUG = false;
	
	public void render(SpriteBatch batch) {
		//Feels kind of illegal to put control responses in the render method, but let's see if it works out.
		if (Gdx.input.isKeyJustPressed(Keys.F1)) {
			if (DEBUG == false) {
				DEBUG = true;
			} else {
				DEBUG = false;
			}
		}
		
		if (DEBUG == true) {
			//insert shaperenderer stuff here
		}
		
	}

	
}
