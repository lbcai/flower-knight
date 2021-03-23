package lbcai.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import lbcai.util.Assets;
import lbcai.util.Constants;

public class DamageNum {
	
	ArrayList<Digit> digitsList;
	Vector2 position;
	
	class Digit {
		
		Vector2 position = new Vector2();
		TextureRegion region;
		
		//constructor, takes the position of the DamageNum and edits depending on the digit number in the DamageNum for the x 
		//value, and randomly for the y value. i = position of this digit in the list of digits that make up the DamageNum.
		//digit is the actual number that must be displayed (the digit itself).
		Digit(Vector2 position, int i, int digit) {
			//assign the appropriate damage number sprite...
			if (digit < 5) {
				if (digit == 0) {
					region = Assets.instance.dmgNumAssets.dmg0;
				} else if (digit == 1) {
					region = Assets.instance.dmgNumAssets.dmg1;
				} else if (digit == 2) {
					region = Assets.instance.dmgNumAssets.dmg2;
				} else if (digit == 3) {
					region = Assets.instance.dmgNumAssets.dmg3;
				} else if (digit == 4) {
					region = Assets.instance.dmgNumAssets.dmg4;
				}
			} else {
				if (digit == 5) {
					region = Assets.instance.dmgNumAssets.dmg5;
				} else if (digit == 6) {
					region = Assets.instance.dmgNumAssets.dmg6;
				} else if (digit == 7) {
					region = Assets.instance.dmgNumAssets.dmg7;
				} else if (digit == 8) {
					region = Assets.instance.dmgNumAssets.dmg8;
				} else if (digit == 9) {
					region = Assets.instance.dmgNumAssets.dmg9;
				}
			}
			
			this.position.x = position.x + (i * Constants.dmgNumSize.x);
			
			// Adds a random spread vertically for digits in the string of damage numbers.
			// Math.random() * (max - min + 1) + min
			this.position.y = (float) (Math.random() * (((position.y + 2) - (position.y - 2) + 1) + (position.y - 2)));
		}
		
	}

	//constructor
	public DamageNum(Vector2 position, int number) {
		this.position = position;
		this.digitsList = new ArrayList<Digit>();
		//had to convert damage int to a string to iterate over it
		String numString = Integer.toString(number);
		for (int j = 0; j < numString.length(); j++) {
			//change each char back into an int
			int currentDigit = Character.getNumericValue(numString.charAt(j));
			//construct the digit
			digitsList.add(new Digit(position, j, currentDigit));
		}
	}
	
	public void render(SpriteBatch batch) {
		for (Digit digit : digitsList) {
			batch.draw(digit.region.getTexture(), 
					(digit.position.x - (Constants.dmgNumSize.x / 2)), 
					(digit.position.y - (Constants.dmgNumSize.y / 2)), 
					0, 
					0, 
					digit.region.getRegionWidth(), 
					digit.region.getRegionHeight(), 
					1, 
					1, 
					0, 
					digit.region.getRegionX(), 
					digit.region.getRegionY(), 
					digit.region.getRegionWidth(), 
					digit.region.getRegionHeight(), 
					false, 
					false);
		}
	}
	
	public void update(float delta) {
		
	}
	
	public boolean isExpired() {
		//placeholder
		return false;
	}
	
}
