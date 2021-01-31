package lbcai.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * One instance of Assets is created when a class is loaded. Assets will hold sprites for the class. All entities will be
 * able to access their sprites in one place and we can avoid reloading textures for multiple entities that need the same sprites!
 * 
 * @author lbcai
 */
public class Assets implements Disposable, AssetErrorListener {
	/**
	 * The class we are making an instance of Assets for.
	 */
	public static final String className = Assets.class.getName();
	
	/**
	 * This class will find the right atlas regions for animations.
	 */
	public ClassAssets classAssets;
	
	/**
	 * This AssetManager will load our TextureAtlas/spritesheet once and distribute it to all entities that need it.
	 */
	private AssetManager assetManager;
	
	/**
	 * Loads the appropriate spritesheet and gives it to ClassAssets to check atlas regions for animation.
	 */
	public void init() {
		this.assetManager = new AssetManager();
		//Will invoke error below. The input for this is an AssetErrorListener, we have an overriden
		//AssetErrorListener in the error method of Assets. So an Assets can be placed inside this method.
		assetManager.setErrorListener(this);
		//Input for this is filename, class type of that file. Here we load the texture atlas into assetManager.
		assetManager.load(Constants.textureAtlas, TextureAtlas.class);
		//Waits until everything is done loading to continue.
		assetManager.finishLoading();
		//Actually get the texture atlas from the assetManager.
		TextureAtlas atlas = assetManager.get(Constants.textureAtlas);
		//Match the appropriate spritesheet with the appropriate atlas regions to make animations down the line.
		classAssets = new ClassAssets(atlas);
	}
	
	/**
	 * This class will contain all of the atlas regions for each animation so the program knows which frames to use together.
	 * 
	 * @author lbcai
	 */
	public class ClassAssets {
		
		TextureRegion[] idleRight = new TextureRegion[5];

		/**
		 * Finds the correct atlas region for starting the animation.
		 * @param atlas the TextureAtlas that has the sprites for this entity on it
		 */
		public ClassAssets(TextureAtlas atlas) {
			idleRight[0] = (atlas.findRegion("1"));
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
