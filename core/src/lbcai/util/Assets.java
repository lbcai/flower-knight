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
	}
	
	/**
	 * These classes will contain all of the atlas regions for each animation so the program knows which frames to use together.
	 * 
	 * @author lbcai
	 */
	public class PlayerAssets {
		
		public final Array<AtlasRegion> idleRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> idleLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> jumpLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> jumpRight = new Array<AtlasRegion>();
		public final Array<AtlasRegion> runLeft = new Array<AtlasRegion>();
		public final Array<AtlasRegion> runRight = new Array<AtlasRegion>();
		
		public final Animation<TextureRegion> idleRightAnim;
		public final Animation<TextureRegion> idleLeftAnim;
		public final Animation<TextureRegion> runRightAnim;
		public final Animation<TextureRegion> runLeftAnim;
		public final Animation<TextureRegion> jumpLeftAnim;
		public final Animation<TextureRegion> jumpRightAnim;
		
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
			
			idleLeft.add(atlas.findRegion(Constants.idleLeft, 1));
			idleLeft.add(atlas.findRegion(Constants.idleLeft, 2));
			idleLeft.add(atlas.findRegion(Constants.idleLeft, 3));
			idleLeft.add(atlas.findRegion(Constants.idleLeft, 4));
			idleLeft.add(atlas.findRegion(Constants.idleLeft, 5));
			idleLeftAnim = new Animation(Constants.idleCycleTime, idleLeft, PlayMode.LOOP);
			
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 1));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 2));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 3));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 3));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 4));
			jumpLeft.add(atlas.findRegion(Constants.jumpLeft, 4));
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
		
		public final AtlasRegion life;
		
		public LifeAssets(TextureAtlas atlas) {
			life = atlas.findRegion(Constants.life);
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
