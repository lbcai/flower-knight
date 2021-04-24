package lbcai.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * One instance of Assets is created when a class is loaded. Assets will hold sprites for the class. All entities will be
 * able to access their sprites in one place and we can avoid reloading textures for multiple entities that need the same sprites!
 * 
 * @author lbcai
 */
public class Assets implements Disposable, AssetErrorListener {
	/**
	 * In the GameplayScreen class, we will only ever initialize and dispose of instances of the Assets class. We can give
	 * each type of entity its own Assets this way.
	 */
	public static final Assets instance = new Assets();
	
	/**
	 * The class we are making an instance of Assets for. We will make classes in Assets for each item that will ever use Assets.
	 * className will be one of these classes.
	 */
	public static final String className = Assets.class.getName();
	
	/**
	 * This class will find the right atlas regions for animations.
	 */
	public PlayerAssets playerAssets;
	public PlatformAssets platformAssets;
	public PBeetleAssets pBeetleAssets;
	public DandelionAssets dandelionAssets;
	public DustAssets dustAssets;
	public LifeAssets lifeAssets;
	public BulletAssets bulletAssets;
	public HitAssets hitAssets;
	public DmgNumAssets dmgNumAssets;
	public UIAssets UIassets;
	
	/**
	 * This AssetManager will load our TextureAtlas/spritesheet once and distribute it to all entities that need it.
	 */
	private AssetManager assetManager;
	

	
	/**
	 * Loads the appropriate spritesheet and gives it to ClassAssets to check atlas regions for animation.
	 * 
	 * @param manager the AssetManager that is created whenever the gameplay screen is shown, loads and holds all spritesheets
	 * 				  required to render whatever is currently in the level.
	 */
	public void init(AssetManager manager) {
		this.assetManager = manager;
		//Will invoke error below. The input for this is an AssetErrorListener, we have an overridden
		//AssetErrorListener in the error method of Assets. So an Assets can be placed inside this method.
		assetManager.setErrorListener(this);
		//Input for this is filename, class type of that file. Here we load the texture atlas into assetManager.
		assetManager.load(Constants.textureAtlas, TextureAtlas.class);
		//Waits until everything is done loading to continue.
		assetManager.finishLoading();
		//Actually get the texture atlas from the assetManager.
		TextureAtlas atlas = assetManager.get(Constants.textureAtlas);
		//Match the appropriate spritesheet with the appropriate atlas regions to make animations down the line.
		playerAssets = new PlayerAssets(atlas);
		platformAssets = new PlatformAssets(atlas);
		pBeetleAssets = new PBeetleAssets(atlas);
		dandelionAssets = new DandelionAssets(atlas);
		dustAssets = new DustAssets(atlas);
		lifeAssets = new LifeAssets(atlas);
		bulletAssets = new BulletAssets(atlas);
		hitAssets = new HitAssets(atlas);
		dmgNumAssets = new DmgNumAssets(atlas);
		UIassets = new UIAssets(atlas);
	}
	
	/**
	 * These classes will contain all of the atlas regions for each animation so the program knows which frames to use together.
	 * 
	 * @author lbcai
	 */
	public class PlayerAssets {
		
		public final Array<AtlasRegion> idleRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> idleLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> idleBRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> idleBLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> idleBTransLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> idleBTransRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> jumpLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> jumpRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> runLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> runRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> hangLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> hangRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> skidLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> skidRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> attack1Left = new Array<AtlasRegion>();
		public final Array<AtlasRegion> attack1LeftEnd = new Array<AtlasRegion>();
		public final Array<AtlasRegion> jumpAttack1Left = new Array<AtlasRegion>();
		public final Array<AtlasRegion> jumpAttack1Right = new Array<AtlasRegion>();
		public final Array<AtlasRegion> attack1Right = new Array<AtlasRegion>();
		public final Array<AtlasRegion> attack1RightEnd = new Array<AtlasRegion>();
		public final Array<AtlasRegion> boostToPlatLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> boostToPlatRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> dodgeRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> squatRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> squatLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> landLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> landRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> landLeftCombat = new Array<AtlasRegion>();
		public final Array<AtlasRegion> landRightCombat = new Array<AtlasRegion>();
		public final Array<AtlasRegion> knockdownLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> knockdownRight = new Array<AtlasRegion>();
		
		public final Animation<TextureRegion> idleRightAnim;
		public final Animation<TextureRegion> idleLeftAnim;
		public final Animation<TextureRegion> idleBRightAnim;
		public final Animation<TextureRegion> idleBLeftAnim;
		public final Animation<TextureRegion> idleBTransLeftAnim;
		public final Animation<TextureRegion> idleBTransRightAnim;
		public final Animation<TextureRegion> runRightAnim;
		public final Animation<TextureRegion> runLeftAnim;
		public final Animation<TextureRegion> jumpLeftAnim;
		public final Animation<TextureRegion> jumpRightAnim;
		public final Animation<TextureRegion> hangLeftAnim;
		public final Animation<TextureRegion> hangRightAnim;
		public final Animation<TextureRegion> skidLeftAnim;
		public final Animation<TextureRegion> skidRightAnim;
		public final Animation<TextureRegion> attack1LeftAnim;
		public final Animation<TextureRegion> attack1LeftEndAnim;
		public final Animation<TextureRegion> jumpAttack1LeftAnim;
		public final Animation<TextureRegion> jumpAttack1RightAnim;
		public final Animation<TextureRegion> attack1RightAnim;
		public final Animation<TextureRegion> attack1RightEndAnim;
		public final Animation<TextureRegion> boostToPlatLeftAnim;
		public final Animation<TextureRegion> boostToPlatRightAnim;
		public final Animation<TextureRegion> dodgeRightAnim;
		public final Animation<TextureRegion> squatRightAnim;
		public final Animation<TextureRegion> squatRightUpAnim;
		public final Animation<TextureRegion> squatLeftAnim;
		public final Animation<TextureRegion> squatLeftUpAnim;
		public final Animation<TextureRegion> landLeftAnim;
		public final Animation<TextureRegion> landRightAnim;
		public final Animation<TextureRegion> landLeftCombatAnim;
		public final Animation<TextureRegion> landRightCombatAnim;
		public final Animation<TextureRegion> knockdownLeftAnim;
		public final Animation<TextureRegion> knockdownRightAnim;
		
		/**
		 * Finds the correct atlas region for playing the animation. Adds the region of each frame of each animation to an array,
		 * then packs that into an animation we can play.
		 * @param atlas the TextureAtlas that has the sprites for this entity on it
		 */
		public PlayerAssets(TextureAtlas atlas) {
			idleRight.add(atlas.findRegion(Constants.idleRight, 1));
			idleRight.add(atlas.findRegion(Constants.idleRight, 2));
			idleRight.add(atlas.findRegion(Constants.idleRight, 3));
			idleRight.add(atlas.findRegion(Constants.idleRight, 4));
			idleRight.add(atlas.findRegion(Constants.idleRight, 5));
			idleRightAnim = new Animation(Constants.idleCycleTime, idleRight, PlayMode.LOOP);
			
			//catch breath before complete idle
			idleBRight.add(atlas.findRegion(Constants.idleBRight, 1));
			idleBRight.add(atlas.findRegion(Constants.idleBRight, 2));
			idleBRight.add(atlas.findRegion(Constants.idleBRight, 3));
			idleBRight.add(atlas.findRegion(Constants.idleBRight, 4));
			idleBRight.add(atlas.findRegion(Constants.idleBRight, 5));
			idleBRightAnim = new Animation(Constants.idleCycleTime, idleBRight, PlayMode.LOOP);
			
			//transition animation from catch breath idle to normal idle
			idleBTransRight.add(atlas.findRegion(Constants.idleBTransRight, 1));
			idleBTransRight.add(atlas.findRegion(Constants.idleBTransRight, 2));
			idleBTransRight.add(atlas.findRegion(Constants.idleBTransRight, 3));
			idleBTransRight.add(atlas.findRegion(Constants.idleBTransRight, 4));
			idleBTransRightAnim = new Animation(Constants.idleTransTime, idleBTransRight, PlayMode.NORMAL);
			
			idleLeft.add(atlas.findRegion(Constants.idleLeft, 1));
			idleLeft.add(atlas.findRegion(Constants.idleLeft, 2));
			idleLeft.add(atlas.findRegion(Constants.idleLeft, 3));
			idleLeft.add(atlas.findRegion(Constants.idleLeft, 4));
			idleLeft.add(atlas.findRegion(Constants.idleLeft, 5));
			idleLeftAnim = new Animation(Constants.idleCycleTime, idleLeft, PlayMode.LOOP);
			
			//transition animation from catch breath idle to normal idle
			idleBTransLeft.add(atlas.findRegion(Constants.idleBTransLeft, 1));
			idleBTransLeft.add(atlas.findRegion(Constants.idleBTransLeft, 2));
			idleBTransLeft.add(atlas.findRegion(Constants.idleBTransLeft, 3));
			idleBTransLeft.add(atlas.findRegion(Constants.idleBTransLeft, 4));
			idleBTransLeftAnim = new Animation(Constants.idleTransTime, idleBTransLeft, PlayMode.NORMAL);
			
			//catch breath before complete idle
			idleBLeft.add(atlas.findRegion(Constants.idleBLeft, 1));
			idleBLeft.add(atlas.findRegion(Constants.idleBLeft, 2));
			idleBLeft.add(atlas.findRegion(Constants.idleBLeft, 3));
			idleBLeft.add(atlas.findRegion(Constants.idleBLeft, 4));
			idleBLeft.add(atlas.findRegion(Constants.idleBLeft, 5));
			idleBLeft.add(atlas.findRegion(Constants.idleBLeft, 6));
			idleBLeftAnim = new Animation(Constants.idleCycleTime, idleBLeft, PlayMode.LOOP);
			
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 1));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 2));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 3));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 3));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 4));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 4));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 5));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 5));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 5));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 6));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 6));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 7));
			jumpLeftAnim = new Animation(Constants.jumpCycleTime, jumpLeft, PlayMode.NORMAL);
			
			jumpRight.add(atlas.findRegion(Constants.jumpRight, 1));
			jumpRight.add(atlas.findRegion(Constants.jumpRight, 2));
			jumpRight.add(atlas.findRegion(Constants.jumpRight, 3));
			jumpRight.add(atlas.findRegion(Constants.jumpRight, 3));
			jumpRight.add(atlas.findRegion(Constants.jumpRight, 4));
			jumpRight.add(atlas.findRegion(Constants.jumpRight, 4));
			jumpRight.add(atlas.findRegion(Constants.jumpRight, 5));
			jumpRight.add(atlas.findRegion(Constants.jumpRight, 5));
			jumpRight.add(atlas.findRegion(Constants.jumpRight, 5));
			jumpRight.add(atlas.findRegion(Constants.jumpRight, 6));
			jumpRight.add(atlas.findRegion(Constants.jumpRight, 6));
			jumpRight.add(atlas.findRegion(Constants.jumpRight, 7));
			jumpRightAnim = new Animation(Constants.jumpCycleTime, jumpRight, PlayMode.NORMAL);
			
			runLeft.add(atlas.findRegion(Constants.runLeft, 1));
			runLeft.add(atlas.findRegion(Constants.runLeft, 2));
			runLeft.add(atlas.findRegion(Constants.runLeft, 3));
			runLeft.add(atlas.findRegion(Constants.runLeft, 4));
			runLeft.add(atlas.findRegion(Constants.runLeft, 5));
			runLeft.add(atlas.findRegion(Constants.runLeft, 6));
			runLeft.add(atlas.findRegion(Constants.runLeft, 7));
			runLeft.add(atlas.findRegion(Constants.runLeft, 8));
			runLeft.add(atlas.findRegion(Constants.runLeft, 9));
			runLeft.add(atlas.findRegion(Constants.runLeft, 10));
			runLeft.add(atlas.findRegion(Constants.runLeft, 11));
			runLeftAnim = new Animation(Constants.runCycleTime, runLeft, PlayMode.LOOP);
			
			runRight.add(atlas.findRegion(Constants.runRight, 1));
			runRight.add(atlas.findRegion(Constants.runRight, 2));
			runRight.add(atlas.findRegion(Constants.runRight, 3));
			runRight.add(atlas.findRegion(Constants.runRight, 4));
			runRight.add(atlas.findRegion(Constants.runRight, 5));
			runRight.add(atlas.findRegion(Constants.runRight, 6));
			runRight.add(atlas.findRegion(Constants.runRight, 7));
			runRight.add(atlas.findRegion(Constants.runRight, 8));
			runRight.add(atlas.findRegion(Constants.runRight, 9));
			runRight.add(atlas.findRegion(Constants.runRight, 10));
			runRight.add(atlas.findRegion(Constants.runRight, 11));
			runRightAnim = new Animation(Constants.runCycleTime, runRight, PlayMode.LOOP);
			
			hangLeft.add(atlas.findRegion(Constants.hangLeft, 1));
			hangLeft.add(atlas.findRegion(Constants.hangLeft, 2));
			hangLeft.add(atlas.findRegion(Constants.hangLeft, 3));
			hangLeft.add(atlas.findRegion(Constants.hangLeft, 4));
			hangLeftAnim = new Animation(Constants.idleCycleTime, hangLeft, PlayMode.LOOP);
			
			hangRight.add(atlas.findRegion(Constants.hangRight, 1));
			hangRight.add(atlas.findRegion(Constants.hangRight, 2));
			hangRight.add(atlas.findRegion(Constants.hangRight, 3));
			hangRight.add(atlas.findRegion(Constants.hangRight, 4));
			hangRightAnim = new Animation(Constants.idleCycleTime, hangRight, PlayMode.LOOP);
			
			skidLeft.add(atlas.findRegion(Constants.skidLeft, 1));
			skidLeft.add(atlas.findRegion(Constants.skidLeft, 2));
			skidLeft.add(atlas.findRegion(Constants.skidLeft, 2));
			skidLeft.add(atlas.findRegion(Constants.skidLeft, 2));
			skidLeft.add(atlas.findRegion(Constants.skidLeft, 1));
			skidLeftAnim = new Animation(Constants.skidCycleTime, skidLeft, PlayMode.NORMAL);
			
			skidRight.add(atlas.findRegion(Constants.skidRight, 1));
			skidRight.add(atlas.findRegion(Constants.skidRight, 2));
			skidRight.add(atlas.findRegion(Constants.skidRight, 2));
			skidRight.add(atlas.findRegion(Constants.skidRight, 2));
			skidRight.add(atlas.findRegion(Constants.skidRight, 1));
			skidRightAnim = new Animation(Constants.skidCycleTime, skidRight, PlayMode.NORMAL);
			
			attack1Left.add(atlas.findRegion(Constants.attack1Left, 1));
			attack1Left.add(atlas.findRegion(Constants.attack1Left, 2));
			attack1Left.add(atlas.findRegion(Constants.attack1Left, 3));
			attack1Left.add(atlas.findRegion(Constants.attack1Left, 3));
			attack1Left.add(atlas.findRegion(Constants.attack1Left, 4));
			attack1Left.add(atlas.findRegion(Constants.attack1Left, 5));
			attack1Left.add(atlas.findRegion(Constants.attack1Left, 5));
			attack1Left.add(atlas.findRegion(Constants.attack1Left, 5));
			attack1LeftAnim = new Animation(Constants.attack1CycleTime, attack1Left, PlayMode.NORMAL);
			
			jumpAttack1Left.add(atlas.findRegion(Constants.jumpAttack1Left, 1));
			jumpAttack1Left.add(atlas.findRegion(Constants.jumpAttack1Left, 2));
			jumpAttack1Left.add(atlas.findRegion(Constants.jumpAttack1Left, 2));
			jumpAttack1Left.add(atlas.findRegion(Constants.jumpAttack1Left, 3));
			jumpAttack1Left.add(atlas.findRegion(Constants.jumpAttack1Left, 3));
			jumpAttack1Left.add(atlas.findRegion(Constants.jumpAttack1Left, 4));
			jumpAttack1Left.add(atlas.findRegion(Constants.jumpAttack1Left, 5));
			jumpAttack1Left.add(atlas.findRegion(Constants.jumpAttack1Left, 5));
			jumpAttack1Left.add(atlas.findRegion(Constants.jumpAttack1Left, 5));
			jumpAttack1Left.add(atlas.findRegion(Constants.jumpAttack1Left, 5));
			jumpAttack1Left.add(atlas.findRegion(Constants.jumpAttack1Left, 6));
			jumpAttack1Left.add(atlas.findRegion(Constants.jumpAttack1Left, 7));
			jumpAttack1LeftAnim = new Animation(Constants.attack1CycleTime, jumpAttack1Left, PlayMode.NORMAL);
			
			attack1LeftEnd.add(atlas.findRegion(Constants.attack1Left, 5));
			attack1LeftEnd.add(atlas.findRegion(Constants.attack1Left, 6));
			attack1LeftEnd.add(atlas.findRegion(Constants.attack1Left, 7));
			attack1LeftEnd.add(atlas.findRegion(Constants.attack1Left, 8));
			attack1LeftEnd.add(atlas.findRegion(Constants.attack1Left, 9));
			attack1LeftEndAnim = new Animation(Constants.attack1CycleTime, attack1LeftEnd, PlayMode.NORMAL);
			
			attack1Right.add(atlas.findRegion(Constants.attack1Right, 1));
			attack1Right.add(atlas.findRegion(Constants.attack1Right, 2));
			attack1Right.add(atlas.findRegion(Constants.attack1Right, 3));
			attack1Right.add(atlas.findRegion(Constants.attack1Right, 3));
			attack1Right.add(atlas.findRegion(Constants.attack1Right, 4));
			attack1Right.add(atlas.findRegion(Constants.attack1Right, 5));
			attack1Right.add(atlas.findRegion(Constants.attack1Right, 5));
			attack1Right.add(atlas.findRegion(Constants.attack1Right, 5));
			attack1RightAnim = new Animation(Constants.attack1CycleTime, attack1Right, PlayMode.NORMAL);
			
			jumpAttack1Right.add(atlas.findRegion(Constants.jumpAttack1Right, 1));
			jumpAttack1Right.add(atlas.findRegion(Constants.jumpAttack1Right, 2));
			jumpAttack1Right.add(atlas.findRegion(Constants.jumpAttack1Right, 2));
			jumpAttack1Right.add(atlas.findRegion(Constants.jumpAttack1Right, 3));
			jumpAttack1Right.add(atlas.findRegion(Constants.jumpAttack1Right, 3));
			jumpAttack1Right.add(atlas.findRegion(Constants.jumpAttack1Right, 4));
			jumpAttack1Right.add(atlas.findRegion(Constants.jumpAttack1Right, 5));
			jumpAttack1Right.add(atlas.findRegion(Constants.jumpAttack1Right, 5));
			jumpAttack1Right.add(atlas.findRegion(Constants.jumpAttack1Right, 5));
			jumpAttack1Right.add(atlas.findRegion(Constants.jumpAttack1Right, 5));
			jumpAttack1Right.add(atlas.findRegion(Constants.jumpAttack1Right, 6));
			jumpAttack1Right.add(atlas.findRegion(Constants.jumpAttack1Right, 7));
			jumpAttack1RightAnim = new Animation(Constants.attack1CycleTime, jumpAttack1Right, PlayMode.NORMAL);
			
			attack1RightEnd.add(atlas.findRegion(Constants.attack1Right, 5));
			attack1RightEnd.add(atlas.findRegion(Constants.attack1Right, 6));
			attack1RightEnd.add(atlas.findRegion(Constants.attack1Right, 7));
			attack1RightEnd.add(atlas.findRegion(Constants.attack1Right, 8));
			attack1RightEnd.add(atlas.findRegion(Constants.attack1Right, 9));
			attack1RightEndAnim = new Animation(Constants.attack1CycleTime, attack1RightEnd, PlayMode.NORMAL);
			
			boostToPlatLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 1));
			boostToPlatLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 2));
			boostToPlatLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 3));
			boostToPlatLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 4));
			boostToPlatLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 5));
			boostToPlatLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 5));
			boostToPlatLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 6));
			boostToPlatLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 7));
			boostToPlatLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 8));
			boostToPlatLeftAnim = new Animation(Constants.boostCycleTime, boostToPlatLeft, PlayMode.NORMAL);
			
			boostToPlatRight.add(atlas.findRegion(Constants.boostToPlatRight, 1));
			boostToPlatRight.add(atlas.findRegion(Constants.boostToPlatRight, 2));
			boostToPlatRight.add(atlas.findRegion(Constants.boostToPlatRight, 3));
			boostToPlatRight.add(atlas.findRegion(Constants.boostToPlatRight, 4));
			boostToPlatRight.add(atlas.findRegion(Constants.boostToPlatRight, 5));
			boostToPlatRight.add(atlas.findRegion(Constants.boostToPlatRight, 5));
			boostToPlatRight.add(atlas.findRegion(Constants.boostToPlatRight, 6));
			boostToPlatRight.add(atlas.findRegion(Constants.boostToPlatRight, 7));
			boostToPlatRight.add(atlas.findRegion(Constants.boostToPlatRight, 8));
			boostToPlatRightAnim = new Animation(Constants.boostCycleTime, boostToPlatRight, PlayMode.NORMAL);
			
			squatRight.add(atlas.findRegion(Constants.squatRight, 1));
			squatRight.add(atlas.findRegion(Constants.squatRight, 2));
			squatRight.add(atlas.findRegion(Constants.boostToPlatRight, 4));
			squatRight.add(atlas.findRegion(Constants.boostToPlatRight, 5));
			squatRightAnim = new Animation(Constants.squatCycleTime, squatRight, PlayMode.NORMAL);
			squatRightUpAnim = new Animation(Constants.squatCycleTime, squatRight, PlayMode.REVERSED);
			
			squatLeft.add(atlas.findRegion(Constants.squatLeft, 1));
			squatLeft.add(atlas.findRegion(Constants.squatLeft, 2));
			squatLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 4));
			squatLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 5));
			squatLeftAnim = new Animation(Constants.squatCycleTime, squatLeft, PlayMode.NORMAL);
			squatLeftUpAnim = new Animation(Constants.squatCycleTime, squatLeft, PlayMode.REVERSED);
			
			dodgeRight.add(atlas.findRegion(Constants.dodgeRight, 1));
			dodgeRight.add(atlas.findRegion(Constants.dodgeRight, 3));
			dodgeRight.add(atlas.findRegion(Constants.dodgeRight, 4));
			dodgeRight.add(atlas.findRegion(Constants.dodgeRight, 5));
			dodgeRight.add(atlas.findRegion(Constants.dodgeRight, 6));
			dodgeRight.add(atlas.findRegion(Constants.dodgeRight, 7));
			dodgeRight.add(atlas.findRegion(Constants.dodgeRight, 8));
			dodgeRight.add(atlas.findRegion(Constants.dodgeRight, 9));
			dodgeRightAnim = new Animation(Constants.dodgeCycleTime, dodgeRight, PlayMode.NORMAL);
			
			landLeft.add(atlas.findRegion(Constants.landLeft, 1));
			landLeft.add(atlas.findRegion(Constants.landLeft, 2));
			landLeft.add(atlas.findRegion(Constants.landLeft, 3));
			landLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 7));
			landLeft.add(atlas.findRegion(Constants.boostToPlatLeft, 8));
			landLeftAnim = new Animation(Constants.landCycleTime, landLeft, PlayMode.NORMAL);
			
			landLeftCombat.add(atlas.findRegion(Constants.landLeft, 1));
			landLeftCombat.add(atlas.findRegion(Constants.landLeft, 2));
			landLeftCombat.add(atlas.findRegion(Constants.landLeftCombat, 3));
			landLeftCombat.add(atlas.findRegion(Constants.landLeftCombat, 4));
			landLeftCombat.add(atlas.findRegion(Constants.landLeftCombat, 5));
			landLeftCombat.add(atlas.findRegion(Constants.landLeftCombat, 6));
			landLeftCombatAnim = new Animation(Constants.landCycleTime, landLeftCombat, PlayMode.NORMAL);
			
			landRight.add(atlas.findRegion(Constants.landRight, 1));
			landRight.add(atlas.findRegion(Constants.landRight, 2));
			landRight.add(atlas.findRegion(Constants.landRight, 3));
			landRight.add(atlas.findRegion(Constants.boostToPlatRight, 7));
			landRight.add(atlas.findRegion(Constants.boostToPlatRight, 8));
			landRightAnim = new Animation(Constants.landCycleTime, landRight, PlayMode.NORMAL);
			
			landRightCombat.add(atlas.findRegion(Constants.landRight, 1));
			landRightCombat.add(atlas.findRegion(Constants.landRight, 2));
			landRightCombat.add(atlas.findRegion(Constants.landRightCombat, 3));
			landRightCombat.add(atlas.findRegion(Constants.landRightCombat, 4));
			landRightCombat.add(atlas.findRegion(Constants.landRightCombat, 5));
			landRightCombat.add(atlas.findRegion(Constants.landRightCombat, 6));
			landRightCombatAnim = new Animation(Constants.landCycleTime, landRightCombat, PlayMode.NORMAL);
			
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 1));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 2));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 2));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 2));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 3));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 3));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 3));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 4));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 5));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 6));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 7));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 8));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 9));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 10));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 11));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 12));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 13));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 14));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 15));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 16));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 17));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 18));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 19));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 20));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 20));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 20));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 20));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 20));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 20));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 20));
			knockdownLeft.add(atlas.findRegion(Constants.knockdownLeft, 20));
			knockdownLeftAnim = new Animation(Constants.knockdownCycleTime, knockdownLeft, PlayMode.NORMAL);
			
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 1));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 2));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 2));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 2));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 3));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 3));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 3));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 4));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 5));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 6));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 7));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 8));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 9));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 10));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 11));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 12));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 13));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 14));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 15));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 16));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 17));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 18));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 19));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 20));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 20));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 20));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 20));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 20));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 20));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 20));
			knockdownRight.add(atlas.findRegion(Constants.knockdownRight, 20));
			knockdownRightAnim = new Animation(Constants.knockdownCycleTime, knockdownRight, PlayMode.NORMAL);
			
		}
	}
	
	public class PlatformAssets {
		
		//A nine patch is an object that will make our platforms from 9 different sprites so we can modularly display them.
		public final NinePatch platformNinepatch;
		
		public PlatformAssets(TextureAtlas atlas) {
			AtlasRegion region = atlas.findRegion(Constants.platformSprite);
			int edge = Constants.platformStretchEdge;
			//Order of edge arguments: left, right, top, bottom. Each of these means "pixels from left/right/top/bottom" and places
			//a line on the image that will "cut" the image into the tileable pieces.
			platformNinepatch = new NinePatch(region, edge, edge, edge, edge);
		}
		
	}
	
	public class PBeetleAssets {
		
		public final Array<AtlasRegion> idleLeft = new Array<AtlasRegion>();
		public final Animation<TextureRegion> idleLeftAnim;

		
		public PBeetleAssets(TextureAtlas atlas) {
			idleLeft.add(atlas.findRegion(Constants.pBeetle));
			idleLeftAnim = new Animation(Constants.runCycleTime, idleLeft, PlayMode.LOOP);

		}
		
	}
	
	public class DandelionAssets {
			
			public final Array<AtlasRegion> idleLeft = new Array<AtlasRegion>();
			public final Animation<TextureRegion> idleLeftAnim;
	
			
			public DandelionAssets(TextureAtlas atlas) {
				idleLeft.add(atlas.findRegion(Constants.dandelion));
				idleLeftAnim = new Animation(Constants.runCycleTime, idleLeft, PlayMode.LOOP);
	
			}
			
		}
	
	public class DustAssets {
		
		public final AtlasRegion dust;
		
		public DustAssets(TextureAtlas atlas) {
			dust = atlas.findRegion(Constants.runDust);
		}
		
	}
	
	public class LifeAssets {
		
		public final Array<AtlasRegion> life = new Array<AtlasRegion>();
		public final Animation<TextureRegion> lifeAnim;
		
		public final Array<AtlasRegion> healSmall = new Array<AtlasRegion>();
		public final Animation<TextureRegion> healSmallAnim;
		
		public final Array<AtlasRegion> healLarge = new Array<AtlasRegion>();
		public final Animation<TextureRegion> healLargeAnim;
		
		public LifeAssets(TextureAtlas atlas) {
			life.add(atlas.findRegion(Constants.life, 1));
			life.add(atlas.findRegion(Constants.life, 1));
			lifeAnim = new Animation(Constants.itemCycleTime, life, PlayMode.LOOP);
			
			healSmall.add(atlas.findRegion(Constants.healSmall, 1));
			healSmallAnim = new Animation(Constants.itemCycleTime, healSmall, PlayMode.LOOP);
			
			healLarge.add(atlas.findRegion(Constants.healLarge, 1));
			healLargeAnim = new Animation(Constants.itemCycleTime, healLarge, PlayMode.LOOP);
			

		}
		
	}
	
	public class BulletAssets {
		
		public final Array<AtlasRegion> bullet = new Array<AtlasRegion>();
		public final Animation<TextureRegion> bulletAnim;
		
		public BulletAssets(TextureAtlas atlas) {
			bullet.add(atlas.findRegion(Constants.bullet, 1));
			bullet.add(atlas.findRegion(Constants.bullet, 2));
			bulletAnim = new Animation(Constants.bulletCycleTime, bullet, PlayMode.LOOP);
		}
		
	}
	
	public class HitAssets {
			
			public final Array<AtlasRegion> hitPierceOne = new Array<AtlasRegion>();
			public final Animation<TextureRegion> hitPierceOneAnim;
			
			public final Array<AtlasRegion> hitPierceTwo = new Array<AtlasRegion>();
			public final Animation<TextureRegion> hitPierceTwoAnim;
			
			public final Array<AtlasRegion> hitPierceThree = new Array<AtlasRegion>();
			public final Animation<TextureRegion> hitPierceThreeAnim;
			
			public final Array<AtlasRegion> hitImpactOne = new Array<AtlasRegion>();
			public final Animation<TextureRegion> hitImpactOneAnim;
			
			public final Array<AtlasRegion> hitImpactTwo = new Array<AtlasRegion>();
			public final Animation<TextureRegion> hitImpactTwoAnim;
			
			public final Array<AtlasRegion> hitImpactThree = new Array<AtlasRegion>();
			public final Animation<TextureRegion> hitImpactThreeAnim;
			
			public final Array<AtlasRegion> hitSlashOne = new Array<AtlasRegion>();
			public final Animation<TextureRegion> hitSlashOneAnim;
			
			public final Array<AtlasRegion> hitSlashTwo = new Array<AtlasRegion>();
			public final Animation<TextureRegion> hitSlashTwoAnim;
			
			public final Array<AtlasRegion> hitSlashThree = new Array<AtlasRegion>();
			public final Animation<TextureRegion> hitSlashThreeAnim;
			
			public HitAssets(TextureAtlas atlas) {
				hitPierceOne.add(atlas.findRegion(Constants.hitPierceOne, 1));
				hitPierceOne.add(atlas.findRegion(Constants.hitPierceOne, 2));
				hitPierceOne.add(atlas.findRegion(Constants.hitPierceOne, 3));
				hitPierceOneAnim = new Animation(Constants.hitEffectCycleTime, hitPierceOne, PlayMode.NORMAL);
				
				hitPierceTwo.add(atlas.findRegion(Constants.hitPierceTwo, 1));
				hitPierceTwo.add(atlas.findRegion(Constants.hitPierceTwo, 2));
				hitPierceTwo.add(atlas.findRegion(Constants.hitPierceTwo, 3));
				hitPierceTwoAnim = new Animation(Constants.hitEffectCycleTime, hitPierceTwo, PlayMode.NORMAL);
				
				hitPierceThree.add(atlas.findRegion(Constants.hitPierceThree, 1));
				hitPierceThree.add(atlas.findRegion(Constants.hitPierceThree, 2));
				hitPierceThree.add(atlas.findRegion(Constants.hitPierceThree, 3));
				hitPierceThreeAnim = new Animation(Constants.hitEffectCycleTime, hitPierceThree, PlayMode.NORMAL);
				
				hitImpactOne.add(atlas.findRegion(Constants.hitImpactOne, 1));
				hitImpactOne.add(atlas.findRegion(Constants.hitImpactOne, 2));
				hitImpactOne.add(atlas.findRegion(Constants.hitImpactOne, 3));
				hitImpactOneAnim = new Animation(Constants.hitEffectCycleTime, hitImpactOne, PlayMode.NORMAL);
				
				hitImpactTwo.add(atlas.findRegion(Constants.hitImpactTwo, 1));
				hitImpactTwo.add(atlas.findRegion(Constants.hitImpactTwo, 2));
				hitImpactTwo.add(atlas.findRegion(Constants.hitImpactTwo, 3));
				hitImpactTwoAnim = new Animation(Constants.hitEffectCycleTime, hitImpactTwo, PlayMode.NORMAL);
				
				hitImpactThree.add(atlas.findRegion(Constants.hitImpactThree, 1));
				hitImpactThree.add(atlas.findRegion(Constants.hitImpactThree, 2));
				hitImpactThree.add(atlas.findRegion(Constants.hitImpactThree, 3));
				hitImpactThreeAnim = new Animation(Constants.hitEffectCycleTime, hitImpactThree, PlayMode.NORMAL);
				
				hitSlashOne.add(atlas.findRegion(Constants.hitSlashOne, 1));
				hitSlashOne.add(atlas.findRegion(Constants.hitSlashOne, 2));
				hitSlashOne.add(atlas.findRegion(Constants.hitSlashOne, 3));
				hitSlashOneAnim = new Animation(Constants.hitEffectCycleTime, hitSlashOne, PlayMode.NORMAL);
				
				hitSlashTwo.add(atlas.findRegion(Constants.hitSlashTwo, 1));
				hitSlashTwo.add(atlas.findRegion(Constants.hitSlashTwo, 2));
				hitSlashTwo.add(atlas.findRegion(Constants.hitSlashTwo, 3));
				hitSlashTwoAnim = new Animation(Constants.hitEffectCycleTime, hitSlashTwo, PlayMode.NORMAL);
				
				hitSlashThree.add(atlas.findRegion(Constants.hitSlashThree, 1));
				hitSlashThree.add(atlas.findRegion(Constants.hitSlashThree, 2));
				hitSlashThree.add(atlas.findRegion(Constants.hitSlashThree, 3));
				hitSlashThreeAnim = new Animation(Constants.hitEffectCycleTime, hitSlashThree, PlayMode.NORMAL);
			}
			
		}
	
	public class DmgNumAssets {
		
		public final AtlasRegion dmg0;
		public final AtlasRegion dmg1;
		public final AtlasRegion dmg2;
		public final AtlasRegion dmg3;
		public final AtlasRegion dmg4;
		public final AtlasRegion dmg5;
		public final AtlasRegion dmg6;
		public final AtlasRegion dmg7;
		public final AtlasRegion dmg8;
		public final AtlasRegion dmg9;
		
		public DmgNumAssets(TextureAtlas atlas) {
			dmg0 = atlas.findRegion(Constants.dmg, 0);
			dmg1 = atlas.findRegion(Constants.dmg, 1);
			dmg2 = atlas.findRegion(Constants.dmg, 2);
			dmg3 = atlas.findRegion(Constants.dmg, 3);
			dmg4 = atlas.findRegion(Constants.dmg, 4);
			dmg5 = atlas.findRegion(Constants.dmg, 5);
			dmg6 = atlas.findRegion(Constants.dmg, 6);
			dmg7 = atlas.findRegion(Constants.dmg, 7);
			dmg8 = atlas.findRegion(Constants.dmg, 8);
			dmg9 = atlas.findRegion(Constants.dmg, 9);
		}
		
	}
	
	public class UIAssets {
		
		public final AtlasRegion orb;
		public final AtlasRegion livesFlowerCover;
		public final AtlasRegion livesFlower1;
		public final AtlasRegion livesFlower2;
		public final AtlasRegion livesFlower3;
		public final AtlasRegion livesFlower4;
		public final AtlasRegion livesFlower5;
		
		public UIAssets(TextureAtlas atlas) {
			orb = atlas.findRegion(Constants.healthOrb);
			livesFlowerCover = atlas.findRegion(Constants.livesFlowerCover);
			livesFlower1 = atlas.findRegion(Constants.livesFlowerPetal, 1);
			livesFlower2 = atlas.findRegion(Constants.livesFlowerPetal, 2);
			livesFlower3 = atlas.findRegion(Constants.livesFlowerPetal, 3);
			livesFlower4 = atlas.findRegion(Constants.livesFlowerPetal, 4);
			livesFlower5 = atlas.findRegion(Constants.livesFlowerPetal, 5);
		}
		
	}
	
	/**
	 * Disposable interface allows us to dispose of things like SpriteBatches and TextureAtlases when we don't need them anymore
	 * so we can avoid memory leaks.
	 */
	@Override 
	public void dispose() {
		assetManager.dispose();
	}
	
	/**
	 * AssetErrorListener interface lets us handle errors if loading an asset fails. 
	 * 
	 * @param asset      the asset that failed to load
	 * @param throwable  thrown exception
	 * 
	 */
	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(className, "Error loading asset: " + asset.fileName, throwable);
		
	}
}
