package lbcai.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Constants {
	//put f after the number to make sure it is not a double (64 bit, double precision of a float), but a float (32 bit).
	
	//world
	public static final Color BackgroundColor = Color.SKY;
	public static final int WorldSize = 800; //changes amount visible in window, seems to become pixelly above 820-840 (why?)
	//i think that it has something to do with resolution. need to find appropriate worldSize value for each window size allowed
	//used 512 for small window
	public static final float worldGravity = 2000;
	public static final float killPlane = -500;
	public static final float cameraMoveSpeed = 512;
	public static final float defaultWorldWidth = 1066;
	public static final float defaultWorldHeight = 800;
	//fix later
	public static final String textureAtlas = "playerSpritesheet.atlas";
	
	//ui elements
	public static final Vector2 hpOrbDimensions = new Vector2(100, 100);
	public static final Vector2 hpOrbCenter = new Vector2(75, 192);
	public static final Vector2 livesFlowerCenter = new Vector2(164, 243);
	public static final String healthOrb = "testorb";
	public static final String livesFlowerCover = "testorb_cover";
	public static final String livesFlowerPetal = "petal";
	public static final int margin = 40;
	
	
	//player
	public static final String idleRight = "Idleright";
	public static final String idleLeft = "Idleleft";
	public static final String idleBLeft = "Idlebleft";
	public static final String idleBTransLeft = "Idlebtransleft";
	public static final String idleBRight = "Idlebright";
	public static final String idleBTransRight = "Idlebtransright";
	public static final String jumpLeft = "Jumpleft";
	public static final String jumpRight = "Jumpright";
	public static final String runLeft = "Runleft";
	public static final String runRight = "Runright";
	public static final String slideLeft = "Slideleft";
	public static final String slideRight = "Slideright";
	public static final String hangLeft = "Hangleft";
	public static final String hangRight = "Hangright";
	public static final String skidLeft = "Skidleft";
	public static final String skidRight = "Skidright";
	public static final String attack1Left = "Attack1left";
	public static final String attack1Right = "Attack1right";
	public static final String jumpAttack1Left = "Jumpattack1left";
	public static final String jumpAttack1Right = "Jumpattack1right";
	public static final String boostToPlatLeft = "Boosttoplatleft";
	public static final String boostToPlatRight = "Boosttoplatright";
	public static final String squatRight = "Squatright";
	public static final String squatLeft = "Squatleft";
	public static final String dodgeRight = "Dodgeright";
	public static final String landLeft = "Landleft";
	public static final String landRight = "Landright";
	public static final String landLeftCombat = "Landcombatleft";
	public static final String landRightCombat = "Landcombatright";
	public static final String deathLeft = "Knockdownleft";
	public static final String deathRight = "Knockdownright";
	
	public static final float skidCycleTime = 0.025f;
	public static final float runCycleTime = 0.04f;
	public static final float idleCycleTime = 0.4f;
	public static final float idleTransTime = 0.06f;
	public static final float idleBTime = 3.0f;
	public static final float jumpCycleTime = 0.04f;
	public static final float wallTime = 0.05f;
	public static final float skidTime = 0.25f;
	public static final float skidTimeLimitBreak = 0.25f;
	public static final float animLockTime = 0.3f;
	public static final float attack1CycleTime = 0.05f; //0.05
	public static final float boostCycleTime = 0.05f;
	public static final float dodgeCycleTime = 0.07f; //0.07
	public static final float dodgeCDTime = 0.5f;
	public static final float squatCycleTime = 0.01f; //0.01
	public static final float landCycleTime = 0.06f; //0.05
	public static final float knockdownCycleTime = 0.05f; //standard cycle time seems to be 0.05, should reduce variables
	//vector (height, width) where the player's head is centered in the sprite
	public static final Vector2 playerHead = new Vector2(128, 128);
	//f is for filtering. You cannot have a pixel at 0.5 but you can have a sprite drawn at float values with filtering.

	public static final float playerHeight = 164.0f;
	//Distance between player's feet, used to detect when player lands on platforms and when player should fall.
	public static final float playerStance = 60.0f;
	//Player's base move speed.
	public static final float moveSpeed = 38000;
	//Player's base jump speed and base time allowed in the air for jump.
	public static final float jumpSpeed = 850;
	//Basic on-touch knockback velocity.
	public static final Vector2 knockbackSpeed = new Vector2(600, 200);
	public static final float iFrameLength = 1.0f;
	public static final int playerBaseDamage = 20;
	public static final int playerBaseRange = playerBaseDamage/2;
	public static final Vector2 attackRange1 = new Vector2(150, 170);
	public static final int baseHealth = 100; //100
	public static final int baseLives = 3;
	public static final float deathWaitTime = 1.5f;
	
	//effect
	public static final String runDust = "dust";
	public static final Vector2 dustCenter = new Vector2(25, 25);
	public static final String hitPierceOne = "hitpierce1";
	public static final String hitPierceTwo = "hitpierce2";
	public static final String hitPierceThree = "hitpierce3";
	public static final String hitImpactOne = "hitimpact1";
	public static final String hitImpactTwo = "hitimpact2";
	public static final String hitImpactThree = "hitimpact3";
	public static final String hitSlashOne = "hitslash1";
	public static final String hitSlashTwo = "hitslash2";
	public static final String hitSlashThree = "hitslash3";
	//these cycle times are per frame.
	public static final float hitEffectCycleTime = 0.15f;
	public static final Vector2 hitEffectCenter = new Vector2(50, 50);
	public static final Vector2 dmgNumSize = new Vector2(45, 60);
	public static final String dmg = "dmg";
	
	//tileset
	public static final String tileSet1 = "platform";
	public static final Vector2 cornerTileDim = new Vector2(30, 36);
	public static final Vector2 cornerTileDimTrans = new Vector2(25, 24);
	public static final Vector2 midUpTileDim = new Vector2(90, 36);
	public static final Vector2 midSideTileDim = new Vector2(36, 60);
	public static final Vector2 midTileDim = new Vector2(90, 60);
	
	
	//enemies
	public static final String pBeetle = "pbeetle";
	public static final int pBeetleDamage = 10;
	public static final int pBeetleHP = 100;
	public static final Vector2 pBeetleEyeHeight = new Vector2(100, 100);
	public static final float enemyMoveSpeed = 166f;
	public static final float enemyMoveSpeedAggro = 500f;
	public static final float waspMoveSpeedAggro = 634f;
	public static final float floatpBeetleAmplitude = 10;
	public static final float floatpBeetlePeriod = 0.9f;
	public static final Vector2 pBeetleCollisionRadius = new Vector2(50, 50);
	public static final String dandelion = "dandelion";
	public static final int dandelionDamage = 10;
	public static final int dandelionHP = 50;
	public static final Vector2 dandelionEyeHeight = new Vector2(100, 100);
	public static final Vector2 dandelionMouth = new Vector2(56, 122);
	public static final float dandelionMoveSpeed = 0;
	public static final Vector2 dandelionCollisionRadius = new Vector2 (60, 90);
	public static final String bullet = "bulletseed";
	public static final float bulletMoveSpeed = 900; //600
	public static final float arrowMoveSpeed = 800;
	public static final float bulletCycleTime = 0.2f;
	public static final float bulletCooldown = 1.5f;
	public static final Vector2 bulletCenter = new Vector2(25, 25);
	public static final float enemyFlinchTime = 0.3f;
	public static final int respawnTime = 10; //20
	public static final Vector2 aggroRadius = new Vector2(500, 500);
	
	
	//item
	public static final String life = "life";
	public static final String healLarge = "heal";
	public static final String healSmall = "healsmall";
	public static final Vector2 itemCenter = new Vector2(37.5f, 37.5f);
	public static final float itemExpireTime = 300f;
	public static final float itemCycleTime = 0.1f;
	public static final float itemRollChance = 1.0f; //0.2
	public static final int breakableObjHealth = 60;
}
