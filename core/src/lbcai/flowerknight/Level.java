package lbcai.flowerknight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
import lbcai.entities.Renderable;
import lbcai.util.Constants;
import lbcai.util.Enums.Facing;
import lbcai.util.Enums.HitState;
import lbcai.util.Enums.JumpState;
import lbcai.util.Enums.LockState;
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
	
	//list of stuff that must be rendered, will sort by z value and render in order, lowest values render first, highest render
	//last
	ArrayList<Renderable> renderables;
	
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
		player.update(delta);
		System.out.println(dustCloudCounter + " " + player.hitState + " " + player.jumpCounter);
		if (player.runState == RunState.SKID || player.hitState == HitState.DODGE) {
			spawnDustCloud(new Vector2(player.position.x, player.position.y - player.eyeHeight.y), player.facing, 0);
		} else if (player.jumpCounter == 2) {
			spawnDustCloud(new Vector2(player.position.x, player.position.y - player.eyeHeight.y), player.facing, 1);
			
		} else if (player.getLockState() == LockState.DOWN) {
			//...added this later. getlockstate because finally consciously understood what a getter is for
			//now it is not necessary to make random things public everywhere
			if (player.getDustFlag() == 1) {
				if (player.facing == Facing.RIGHT) {
					spawnDustCloud(new Vector2(player.position.x + 60, player.position.y - player.eyeHeight.y), Facing.LEFT, 0);
				} else {
					spawnDustCloud(new Vector2(player.position.x - 60, player.position.y - player.eyeHeight.y), Facing.RIGHT, 0);
				}
			}
			
		} else if (player.getLockState() == LockState.DEATH) {
			//...added this later. getlockstate because finally consciously understood what a getter is for
			//now it is not necessary to make random things public everywhere
			if (player.getDustFlag() == 1) {
				if (player.facing == Facing.RIGHT) {
					spawnDustCloud(new Vector2(player.position.x + 60, player.position.y - player.eyeHeight.y), Facing.LEFT, 0);
				} else {
					spawnDustCloud(new Vector2(player.position.x - 60, player.position.y - player.eyeHeight.y), Facing.RIGHT, 0);
				}
			}
			
		}
		
		if (dustCloudCounter == 1) {
			if ((player.runState != RunState.SKID && player.hitState != HitState.DODGE) && (player.jumpCounter == 0 || player.jumpCounter == 1)) {
				if (player.getLockState() != LockState.DEATH && player.getLockState() != LockState.DOWN) {
					dustCloudCounter = 0;
					//reset dust flag in player for death and down sliding dust
					player.setDustFlag();
				}
			}
		}
		
		enemies.begin();
		for (int i = 0; i < enemies.size; i++) {
			Enemy enemy = enemies.get(i);
			enemy.update(delta);
		}
		enemies.end();
		
		for (Platform platform : platforms) {
			platform.update();
		}
		
		//removals from the delayed removal array are queued after begin and actually performed after end.
		bullets.begin();
		for (Bullet bullet : bullets) {
			//update any bullets in the bullet array
			bullet.update(delta);
			if (bullet.inactive == true) {
				//if the bullet is not active (aka it is outside of the viewport), delete from bullet array
				//removeValue only removes the first instance of the thing, false means use .equals() for comparison and not ==
				bullets.removeValue(bullet, false);
				renderables.remove(bullet);
			}
		}
		bullets.end();

		dustClouds.begin();
		for (DustCloud dust : dustClouds) {
			dust.update(delta);
			if (dust.isExpired()) {
				dustClouds.removeValue(dust, false);
				renderables.remove(dust);
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
		
		//sort the items that must be rendered by z value. lowest z value items will be rendered first
		//lambda expression (args) -> {body}
		Collections.sort(renderables, (r1, r2) -> {int zDiff = r1.getzValue() - r2.getzValue(); 
													if (zDiff == 0) return r2.getyValue() - r1.getyValue();
													return zDiff;
													});
		
		//render the sorted list of items that must be rendered
		for (Renderable render : renderables) {
			render.render(batch);
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
		renderables = new ArrayList<Renderable>();
		platforms = new Array<Platform>();
		enemies = new DelayedRemovalArray<Enemy>();
		bullets = new DelayedRemovalArray<Bullet>();
		dustClouds = new DelayedRemovalArray<DustCloud>();
		hitEffects = new DelayedRemovalArray<HitEffect>();
		damageNums = new DelayedRemovalArray<DamageNum>();
		items = new DelayedRemovalArray<Item>();
		
		//left, top, width, height
		platforms.add(new Platform(500, 75, 200, 50, this));
		platforms.add(new Platform(0, 0, 512, 50, this));
		platforms.add(new Platform(100, 300, 900, 50, this));
		platforms.add(new Platform(100, 200, 900, 50, this));
		platforms.add(new Platform(100, 400, 900, 50, this));
		platforms.add(new Platform(100, 500, 900, 50, this));
		platforms.add(new Platform(100, 700, 10, 50, this));
		platforms.add(new Platform(0, 1000, 200, 800, this));
		platforms.add(new Platform(512, 1000, 200, 800, this));
		platforms.add(new Platform(800, 0, 800, 50, this));
		//this is the lowest platform in the map. height must be at least 72, so bottom is corrected to -172, 
		//then +5 to top for background grass, this means the actual top will be -95 y position.
		Platform longPlatform = new Platform(0, -100, 10000, 500, this);
		platforms.add(longPlatform);
		enemies.add(new BreakableObject(longPlatform, this, Facing.LEFT, new Vector2(0.5f, 0f)));
		enemies.add(new BreakableObject(longPlatform, this, Facing.RIGHT, new Vector2(0.1f, 0f)));
		
		
		//Add player to the level. Add a start position for the level as input.
		player = new Player(new Vector2(2000, 800), this);
		
		Platform enemyPlatform = new Platform(700, 160, 500, 50, this);
		enemies.add(new EnemyDandelion(enemyPlatform, this));
		//enemies.add(new EnemyPBeetle(enemyPlatform, this));
		enemies.add(new EnemyWaspScout(enemyPlatform, this));
		enemies.add(new EnemyWaspArcher(enemyPlatform, this));
		enemies.add(new EnemyWaspLancer(enemyPlatform, this));
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
	
	public ArrayList<Renderable> getRenderables() {
		return renderables;
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
		if (dustCloudCounter != 1) {
			dustClouds.add(new DustCloud(position, facing, type, this));
			dustCloudCounter = 1;
		}
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
