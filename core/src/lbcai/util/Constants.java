package lbcai.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Constants {
	public static final Color BackgroundColor = Color.SKY;
	public static final int WorldSize = 512;
	
	public static final String textureAtlas = "playerSpritesheet.atlas";
	public static final String idleRight = "Idleright";
	public static final String idleLeft = "Idleleft";
	public static final String jumpLeft = "Jumpleft";
	public static final String runLeft = "Runleft";
	public static final String runRight = "Runright";
	
	public static final float runCycleTime = 0.04f;
	public static final float idleCycleTime = 0.4f;
	public static final float jumpCycleTime = 0.04f;
	
	//vector (height, width) where the player's head is centered in the sprite
	public static final Vector2 playerHead = new Vector2(128, 128);
	//f is for filtering. You cannot have a pixel at 0.5 but you can have a sprite drawn at float values with filtering.
	public static final float playerEyeHeight = 128.0f;
	//Player's base move speed.
	public static final float moveSpeed = 512;
	//Player's base jump speed and base time allowed in the air for jump.
	public static final float jumpSpeed = 600;
	public static final float maxJumpTime = 1f;
	public static final float worldGravity = 2000;
}
