package lbcai.flowerknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.Viewport;

import lbcai.entities.BreakableObject;
import lbcai.entities.Bullet;
import lbcai.entities.DamageNum;
import lbcai.entities.DustCloud;
import lbcai.entities.Enemy;
import lbcai.entities.EnemyDandelion;
import lbcai.entities.EnemyPBeetle;
import lbcai.entities.EnemyWaspArcher;
import lbcai.entities.EnemyWaspLancer;
import lbcai.entities.EnemyWaspScout;
import lbcai.entities.HitEffect;
import lbcai.entities.Item;
import lbcai.entities.ItemHealSmall;
import lbcai.entities.ItemLife;
import lbcai.entities.Platform;
import lbcai.entities.Player;
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.HitState;
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
	private DelayedRemovalArray<HitEffect> hitEffects;
	private DelayedRemovalArray<DamageNum> damageNums;
	private DelayedRemovalArray<Item> items;
	private int dustCloudCounter = 0;
	
	//define the level bounds
	public Rectangle levelBound = new Rectangle();
	public float lowestTop;
	
	public Level(Viewport viewport) {
		this.viewport = viewport;

		//Start up the Debug level.
		initDebugLevel();
	}
	
	public void update(float delta) {
		player.update(delta, platforms);
		
		if (dustCloudCounter == 0) {
			if (player.runState == RunState.SKID || player.hitState == HitState.DODGE) {
				spawnDustCloud(new Vector2(player.position.x, player.position.y - player.eyeHeight.y), player.facing, 0);
			} else if (player.jumpCounter == 2) {
				spawnDustCloud(new Vector2(player.position.x, player.position.y - player.eyeHeight.y), player.facing, 1);
			}
			
		} else if (dustCloudCounter == 1) {
			if ((player.runState != RunState.SKID && player.hitState != HitState.DODGE) && (player.jumpCounter == 0 || player.jumpCounter == 1)) {
				dustCloudCounter = 0;
			}
		}

		enemies.begin();
		for (int i = 0; i < enemies.size; i++) {
			Enemy enemy = enemies.get(i);
			enemy.update(delta);
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
			if (dustClouds.get(i).isExpired()) {
				dustClouds.removeIndex(i);
			}
		}
		dustClouds.end();
		
		items.begin();
		for (Item item : items) {
			item.update(delta);
			if (item.isExpired() == true) {
				//if item expires or falls off map, remove
				items.removeValue(item, false);
			}
		}
		items.end();
		
		hitEffects.begin();
		for (HitEffect effect : hitEffects) {
			if (effect.isExpired() == true) {
				hitEffects.removeValue(effect, false);
			}
		}
		hitEffects.end();
		
		damageNums.begin();
		for (DamageNum damageNum : damageNums) {
			damageNum.update(delta);
			if (damageNum.isExpired() == true) {
				damageNums.removeValue(damageNum, false);
			}
		}
		damageNums.end();
		
		
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
		
		for (HitEffect effect : hitEffects) {
			effect.render(batch);
		}
		
		for (DamageNum damageNum : damageNums) {
			damageNum.render(batch);
		}
		
	}
	
	private void initDebugLevel() {
		
		//Initialize the array of platforms and enemies, etc.
		platforms = new Array<Platform>();
		enemies = new DelayedRemovalArray<Enemy>();
		bullets = new DelayedRemovalArray<Bullet>();
		dustClouds = new DelayedRemovalArray<DustCloud>();
		hitEffects = new DelayedRemovalArray<HitEffect>();
		damageNums = new DelayedRemovalArray<DamageNum>();
		items = new DelayedRemovalArray<Item>();
		
		//left, top, width, height
		platforms.add(new Platform(500, 75, 200, 50));
		platforms.add(new Platform(0, 0, 512, 50));
		platforms.add(new Platform(100, 300, 900, 50));
		platforms.add(new Platform(100, 200, 900, 50));
		platforms.add(new Platform(100, 400, 900, 50));
		platforms.add(new Platform(100, 500, 900, 50));
		platforms.add(new Platform(0, 1000, 200, 800));
		platforms.add(new Platform(512, 1000, 200, 800));
		platforms.add(new Platform(800, 0, 800, 50));
		//this is the lowest platform in the map. height must be at least 72, so bottom is corrected to -172, 
		//then +5 to top for background grass, this means the actual top will be -95 y position.
		Platform longPlatform = new Platform(0, -100, 10000, 500);
		platforms.add(longPlatform);
		enemies.add(new BreakableObject(longPlatform, this, Facing.LEFT, new Vector2(0.5f, 0f)));
		enemies.add(new BreakableObject(longPlatform, this, Facing.RIGHT, new Vector2(0.1f, 0f)));
		
		
		//Add player to the level. Add a start position for the level as input.
		player = new Player(new Vector2(100, 200), this);
		
		Platform enemyPlatform = new Platform(700, 160, 500, 50);
		//enemies.add(new EnemyDandelion(enemyPlatform, this));
		//enemies.add(new EnemyPBeetle(enemyPlatform, this));
		enemies.add(new EnemyWaspScout(enemyPlatform, this));
		enemies.add(new EnemyWaspArcher(enemyPlatform, this));
		platforms.add(enemyPlatform);
		
		
		lowestTop = platforms.get(0).getTop();		
		float lowestTopLength = platforms.get(0).getWidth();
		float lowestTopLeft = platforms.get(0).getLeft();
		for (Platform platform : platforms) {
			if (platform.getTop() < lowestTop) {
				lowestTop = platform.getTop();
				lowestTopLength = platform.getWidth();
				lowestTopLeft = platform.getLeft();
			}
		}
		//x, y, width, height; set the size of the level (entities cannot exit this boundary)
		levelBound.set(lowestTopLeft, lowestTop, lowestTopLength, 2500);


	}
	
	public void debugRender(ShapeRenderer shape) {

		for (Enemy enemy : enemies) {
			enemy.debugRender(shape);
		}
		
		player.debugRender(shape);
		
		for (Bullet bullet : bullets) {
			bullet.debugRender(shape);
		}
		
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
	
	public void spawnBullet(Vector2 position, Facing facing, int damage, int type) {
		bullets.add(new Bullet(this, position, facing, damage, type));
	}
	
	public void spawnDustCloud(Vector2 position, Facing facing, int type) {
		dustClouds.add(new DustCloud(position, facing, type));
		dustCloudCounter = 1;
	}
	
	public void spawnHitEffect(Rectangle rectangle, Facing facing, int type) {
		// an equation to get random numbers in a range:
		// Math.random() * (max - min + 1) + min
		Vector2 position = new Vector2();
		rectangle.getCenter(position);
		float x = (float) (Math.random() * ((position.x + 10) - (position.x - 10) + 1) + (position.x - 10));
		float y = (float) (Math.random() * ((position.y + 40) - (position.y - 40) + 1) + (position.y - 40));
		hitEffects.add(new HitEffect(new Vector2(x, y), facing, type));
	}
	
	public void spawnDmgNum(Vector2 position, int number, Facing facing) {
		damageNums.add(new DamageNum(position, number, facing));
	}
	
	public void dropItem(Enemy enemy) {
		//check the enemy's loot table and use randomness to see if item will drop and which item it will be
		//drop item at enemy's position before death
		// 0 = base item: full heal
		// 1 = 20% small heal item
		if (Math.random() <= Constants.itemRollChance) {
			int tableIndex = enemy.rollDrop();
			if (tableIndex == 0) {
				items.add(new Item(enemy.position, this));
			} else if (tableIndex == 1) {
				items.add(new ItemHealSmall(enemy.position, this));
			} else if (tableIndex == 2) {
				items.add(new ItemLife(enemy.position, this));
			}
		}
	}
	
	
}
