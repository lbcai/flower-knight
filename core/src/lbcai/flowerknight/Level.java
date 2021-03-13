package lbcai.flowerknight;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.Viewport;

import lbcai.entities.Bullet;
import lbcai.entities.DustCloud;
import lbcai.entities.Enemy;
import lbcai.entities.EnemyDandelion;
import lbcai.entities.EnemyPBeetle;
import lbcai.entities.Item;
import lbcai.entities.Platform;
import lbcai.entities.Player;
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.JumpState;
import lbcai.util.Enums.RunState;

public class Level {
	//get each level
	public static final String className = Level.class.getName();
	private Viewport viewport;
	
	
	/**
	 * Make the player.
	 */
	Player player;
	
	/**
	 * Make an array of platforms to hold all platforms in the level.
	 */
	Array<Platform> platforms;
	
	/**
	 * DelayedRemovalArray of enemies. This type of array allows for removal of items from the array even while iterating.
	 * Did the same with bullet objects.
	 */
	private DelayedRemovalArray<Enemy> enemies;
	private DelayedRemovalArray<Bullet> bullets;
	private DelayedRemovalArray<DustCloud> dustClouds;
	private DelayedRemovalArray<Item> items;
	private int dustCloudCounter = 0;
	
	public Level(Viewport viewport) {
		this.viewport = viewport;

		//Start up the Debug level.
		initDebugLevel();
	}
	
	public void update(float delta) {
		player.update(delta, platforms);
		
		if (dustCloudCounter == 0) {
			if (player.runState == RunState.SKID) {
				spawnDustCloud(new Vector2(player.position.x, player.position.y - Constants.playerEyeHeight), player.facing, 0);
				dustCloudCounter = 1;
			} else if (player.jumpCounter == 2) {
				spawnDustCloud(new Vector2(player.position.x, player.position.y - Constants.playerEyeHeight), player.facing, 1);
				dustCloudCounter = 1;
			}
			
		} else if (dustCloudCounter == 1) {
			if (player.runState != RunState.SKID && (player.jumpCounter == 0 || player.jumpCounter == 1)) {
				dustCloudCounter = 0;
			}
		}

		enemies.begin();
		for (int i = 0; i < enemies.size; i++) {
			Enemy enemy = enemies.get(i);
			enemy.update(delta);
			if (enemy.HP < 1) {
				enemies.removeIndex(i);
			}
		}
		enemies.end();
		
		//removals from the delayed removal array are queued after begin and actually performed after end.
		bullets.begin();
		for (Bullet bullet : bullets) {
			//update any bullets in the bullet array
			bullet.update(delta);
			if (!bullet.active) {
				//if the bullet is not active (aka it is outside of the viewport), delete from bullet array
				//removeValue only removes the first instance of the thing, false means use .equals() for comparison and not ==
				bullets.removeValue(bullet, false);
			}
		}
		bullets.end();

		dustClouds.begin();
		for (int i = 0; i < dustClouds.size; i++) {
			dustClouds.get(i).update(delta);
			if (dustClouds.get(i).isFinished()) {
				dustClouds.removeIndex(i);
			}
		}
		dustClouds.end();
		
		items.begin();
		for (Item item : items) {
			item.update(delta);
			if (item.expire == true) {
				//if item expires or falls off map, remove
				items.removeValue(item, false);
			}
		}
		items.end();
		
	}
	
	/**
	 * Render the level.
	 * 
	 * @param batch      Object that draws textures on a bunch of rectangles/squares in OpenGL. Disposable.
	 * @param renderer   Renders points, outlines, filled shapes, lines. Disposable.
	 */
	public void render(SpriteBatch batch) {

		
		for (Platform platform : platforms) {
			platform.render(batch);
		}
		for (Enemy enemy : enemies) {
			enemy.render(batch);
		}
		player.render(batch);
		
		for (Bullet bullet : bullets) {
			bullet.render(batch);
		}
		
		for (DustCloud dustcloud : dustClouds) {
			dustcloud.render(batch);
		}
		
		for (Item item : items) {
			item.render(batch);
		}
		
		
	}
	
	private void initDebugLevel() {
		
		//Initialize the array of platforms and enemies, etc.
		platforms = new Array<Platform>();
		enemies = new DelayedRemovalArray<Enemy>();
		bullets = new DelayedRemovalArray<Bullet>();
		dustClouds = new DelayedRemovalArray<DustCloud>();
		items = new DelayedRemovalArray<Item>();
		
		//left, top, width, height
		platforms.add(new Platform(500, 75, 200, 50));
		platforms.add(new Platform(0, 0, 512, 50));
		//platform height bug (jump distance)
		platforms.add(new Platform(100, 85, 300, 50));
		platforms.add(new Platform(100, 160, 500, 50));
		platforms.add(new Platform(0, 1000, 200, 800));
		platforms.add(new Platform(512, 1000, 200, 800));
		platforms.add(new Platform(800, 0, 800, 50));
		platforms.add(new Platform(0, -100, 10000, 50));
		
		//Add player to the level. Add a start position for the level as input.
		player = new Player(new Vector2(100, 200), this);
		
		Platform enemyPlatform = new Platform(700, 160, 500, 50);
		enemies.add(new EnemyDandelion(enemyPlatform, player));
		//enemies.add(new EnemyPBeetle(enemyPlatform));
		platforms.add(enemyPlatform);
		
		//add base item for testing
		items.add(new Item(new Vector2(100, 100), this));
		

	}
	
	public Array<Platform> getPlatforms() {
		return platforms;
	}
	
	public DelayedRemovalArray<Enemy> getEnemies() {
		return enemies;
	}
	
	public DelayedRemovalArray<Bullet> getBullets() {
		return bullets;
	}
	
	public DelayedRemovalArray<Item> getItems() {
		return items;
	}
	
	public Viewport getViewport() {
		return viewport;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void spawnBullet(Vector2 position, Facing facing) {
		bullets.add(new Bullet(this, position, facing));
	}
	
	public void spawnDustCloud(Vector2 position, Facing facing, int type) {
		dustClouds.add(new DustCloud(position, player.facing, type));
	}
	
}
