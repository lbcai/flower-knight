package lbcai.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Constants {
	//put f after the number to make sure it is not a double (64 bit, double precision of a float), but a float (32 bit).
	
	//world
	public static final Color BackgroundColor = Color.SKY;
	public static final int WorldSize = 512;
	public static final float worldGravity = 2000;
	public static final float killPlane = -500;
	public static final float cameraMoveSpeed = 512;
	//fix later
	public static final String textureAtlas = "playerSpritesheet.atlas";
	
	
	//player
	public static final String idleRight = "Idleright";
	public static final String idleLeft = "Idleleft";
	public static final String jumpLeft = "Jumpleft";
	public static final String jumpRight = "Jumpright";
	public static final String runLeft = "Runleft";
	public static final String runRight = "Runright";
	public static final float runCycleTime = 0.04f;
	public static final float idleCycleTime = 0.4f;
	public static final float jumpCycleTime = 0.04f;
	//vector (height, width) where the player's head is centered in the sprite
	public static final Vector2 playerHead = new Vector2(128, 128);
	//f is for filtering. You cannot have a pixel at 0.5 but you can have a sprite drawn at float values with filtering.
	public static final float playerEyeHeight = 128.0f;
	public static final float playerHeight = 164.0f;
	//Distance between player's feet, used to detect when player lands on platforms and when player should fall.
	public static final float playerStance = 60.0f;
	//Player's base move speed.
	public static final float moveSpeed = 512;
	//Player's base jump speed and base time allowed in the air for jump.
	public static final float jumpSpeed = 850;
	//Basic on-touch knockback velocity.
	public static final Vector2 knockbackSpeed = new Vector2(600, 200);
	public static final float iFrameLength = 1f;
	
	
	//tileset
	public static final String platformSprite = "Platform";
	//size of stretchable edges in nine patch (placement of cut lines from edge of image)
	public static final int platformStretchEdge = 33;
	
	
	//enemies
	public static final String pBeetle = "pbeetle";
	public static final Vector2 pBeetleEyeHeight = new Vector2(100, 100);
	public static final float enemyMoveSpeed = 250;
	public static final float floatpBeetleAmplitude = 10;
	public static final float floatpBeetlePeriod = 0.9f;
	public static final float pBeetleCollisionRadius = 50;
}
