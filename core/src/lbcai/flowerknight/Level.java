package lbcai.flowerknight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

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
import lbcai.entities.Effect;
import lbcai.entities.Enemy;
import lbcai.entities.EnemyDandelion;
import lbcai.entities.EnemyPBeetle;
import lbcai.entities.EnemyWaspArcher;
import lbcai.entities.EnemyWaspLancer;
import lbcai.entities.EnemyWaspScout;
import lbcai.entities.Entity;
import lbcai.entities.HitEffect;
import lbcai.entities.Item;
import lbcai.entities.ItemHealSmall;
import lbcai.entities.ItemLife;
import lbcai.entities.Platform;
import lbcai.entities.Player;
import lbcai.entities.Renderable;
import lbcai.entities.Updatable;
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
	
	//list of stuff that must be updated every pass
	DelayedRemovalArray<Updatable> updatables;
	/**
	 * DelayedRemovalArray of enemies. This type of array allows for removal of items from the array even while iterating.
	 * Did the same with bullet objects.
	 */
	private DelayedRemovalArray<Enemy> enemies;
	private DelayedRemovalArray<Item> items;
	private DelayedRemovalArray<Bullet> bullets;
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

		updatables.begin();
		for (Updatable update : updatables) {
			update.update(delta);
			if (update.isExpired()) {
				remove(update);
			}
		}
		updatables.end();

		
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
			
			//allows player to dodge and spawn dust cloud at any time, even after consecutive dodges
			//normally the dustCloudCounter is what stops multiple clouds from forming at once, but in this case using a keydown
			//and making sure player actually is dodging seems to work
			//and this case is only necessary when player is out of jumps and is trying to dodge
			if (Gdx.input.isKeyJustPressed(player.getDodgeKey()) && player.hitState == HitState.DODGE && 
					player.jumpCounter == 2) {
				dustCloudCounter = 0;
				spawnDustCloud(new Vector2(player.position.x, player.position.y - player.eyeHeight.y), player.facing, 0);
			}
			
			if ((player.runState != RunState.SKID && player.hitState != HitState.DODGE) && (player.jumpCounter == 0 || player.jumpCounter == 1)) {
				if (player.getLockState() != LockState.DEATH && player.getLockState() != LockState.DOWN) {
					dustCloudCounter = 0;
					//reset dust flag in player for death and down sliding dust
					player.setDustFlag();
				}
			}
		}
		
		System.out.println("updatables: " + updatables.size);
		
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
		//remove the item if it is removed from updatables to avoid memleaks (placeholder)
		//never remove platforms...
		for (Renderable render : renderables) {
			render.render(batch);
		}
		
		System.out.println("renderables: " + renderables.size());
		
	}
	
	private void initDebugLevel() {
		
		//Initialize the array of platforms and enemies, etc.
		renderables = new ArrayList<Renderable>();
		updatables = new DelayedRemovalArray<Updatable>();
		platforms = new Array<Platform>();
		enemies = new DelayedRemovalArray<Enemy>();
		items = new DelayedRemovalArray<Item>();
		bullets = new DelayedRemovalArray<Bullet>();
		
		//Add player to the level. Add a start position for the level as input.
		create(new Player(new Vector2(2000, 800), this));
		
		//left, top, width, height
		create(new Platform(500, 75, 200, 50, this));
		create(new Platform(0, 0, 512, 50, this));
		create(new Platform(100, 300, 900, 50, this));
		create(new Platform(100, 200, 900, 50, this));
		create(new Platform(100, 400, 900, 50, this));
		create(new Platform(100, 500, 900, 50, this));
		create(new Platform(100, 700, 10, 50, this));
		create(new Platform(0, 1000, 200, 800, this));
		create(new Platform(512, 1000, 200, 800, this));
		create(new Platform(800, 0, 800, 50, this));
		//this is the lowest platform in the map. height must be at least 72, so bottom is corrected to -172, 
		//then +5 to top for background grass, this means the actual top will be -95 y position.
		Platform longPlatform = new Platform(0, -100, 10000, 500, this);
		create(longPlatform);
		create(new BreakableObject(longPlatform, this, Facing.LEFT, new Vector2(0.5f, 0f)));
		create(new BreakableObject(longPlatform, this, Facing.RIGHT, new Vector2(0.1f, 0f)));
		
		Platform enemyPlatform = new Platform(700, 160, 500, 50, this);
		create(new EnemyDandelion(enemyPlatform, this));
		//create(new EnemyPBeetle(enemyPlatform, this));
		create(new EnemyWaspScout(enemyPlatform, this));
		create(new EnemyWaspArcher(enemyPlatform, this));
		//create(new EnemyWaspLancer(enemyPlatform, this));
		create(enemyPlatform);
		create(new ItemHealSmall(new Vector2(100, 100), this));
		create(new ItemHealSmall(new Vector2(200, 100), this));
		create(new ItemHealSmall(new Vector2(300, 100), this));
		create(new ItemHealSmall(new Vector2(400, 100), this));
		
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
		
		for (Item item : items) {
			item.debugRender(shape);
		}
		
	}
	
	public Array<Platform> getPlatforms() {
		return platforms;
	}
	
	public ArrayList<Renderable> getRenderables() {
		return renderables;
	}
	
	public DelayedRemovalArray<Updatable> getUpdatables() {
		return updatables;
	}
	
	public DelayedRemovalArray<Enemy> getEnemies() {
		return enemies;
	}
	
	public DelayedRemovalArray<Bullet> getBullets() {
		return bullets;
	}
	
	public Viewport getViewport() {
		return viewport;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public DelayedRemovalArray<Item> getItems() {
		return items;
	}
	
	public void spawnBullet(Vector2 position, Facing facing, int damage, int type) {
		create(new Bullet(this, position, facing, damage, type));
	}
	
	public void spawnDustCloud(Vector2 position, Facing facing, int type) {
		if (dustCloudCounter == 0) {
			create(new DustCloud(position, facing, type));
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
		create(new HitEffect(new Vector2(x, y), facing, type));
	}
	
	public void spawnDmgNum(Vector2 position, int number, Facing facing) {
		create(new DamageNum(position, number, facing));
	}
	
	public void dropItem(Enemy enemy) {
		//check the enemy's loot table and use randomness to see if item will drop and which item it will be
		//drop item at enemy's position before death
		// 0 = base item: full heal
		// 1 = 20% small heal item
		if (Math.random() <= Constants.itemRollChance) {
			int tableIndex = enemy.rollDrop();
			if (tableIndex == 0) {
				create(new Item(enemy.position, this));
			} else if (tableIndex == 1) {
				create(new ItemHealSmall(enemy.position, this));
			} else if (tableIndex == 2) {
				create(new ItemLife(enemy.position, this));
			}
		}
	}
	
	void create(Entity entity) {
		updatables.add(entity);
		renderables.add(entity);
		if (entity instanceof Enemy) {
			enemies.add((Enemy) entity);
		} else if (entity instanceof Bullet) {
			bullets.add((Bullet) entity);
		} else if (entity instanceof Player) {
			this.player = (Player) entity;
		}
	}
	
	void remove(Updatable entity) {
		
		if (entity instanceof Entity) {
			updatables.removeValue(entity, false);
			renderables.remove((Renderable) entity);
			if (entity instanceof Bullet) {
				bullets.removeValue((Bullet) entity, false);
			} else if (entity instanceof Enemy) {
				enemies.removeValue((Enemy) entity, false);
			}
		} else if (entity instanceof Item) {
			updatables.removeValue(entity, false);
			renderables.remove((Renderable) entity);
			items.removeValue((Item) entity, false);
		} else if (entity instanceof Effect) {
			updatables.removeValue(entity, false);
			renderables.remove((Renderable) entity);
			//no unique effect list
		}
		//platforms do not get destroyed
		//players do not get destroyed
	}
	
	void create(Item item) {
		updatables.add(item);
		renderables.add(item);
		items.add(item);
	}
	
	void create(Effect effect) {
		updatables.add(effect);
		renderables.add(effect);
		//effects.add(effect);
	}
	
	void create(Platform platform) {
		//platform adds its own pieces to renderables and updatables.
		platforms.add(platform);
	}
	
}
