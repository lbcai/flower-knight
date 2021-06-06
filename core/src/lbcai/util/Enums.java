package lbcai.util;

public class Enums {
	//enums are like discrete final variables, like steps you can set something to.
	
		//determines direction entity is facing
		public enum Facing {
			LEFT,
			RIGHT
		}
		
		//determines air state of entity (for ground entities only, not flying enemies)
		public enum JumpState {
			//feet on floor
			GROUNDED,
			//jumping (up state)
			JUMPING,
			//jumping (down state) or just falling off a platform
			FALLING,
			//hanging on side of platform
			WALL
		}
		
		//determines movement of entity, helps with animations
		public enum RunState {
			//standing around
			IDLE,
			//pressing down
			SQUAT,
			//standing around after having been in a fight or taking damage
			COMBATIDLE,
			//moving
			RUN,
			//stopping after moving and transitioning into either type of idle
			SKID
		}
		
		//determines if entity takes damage
		public enum HitState {
			//for nothing special, free to take damage
			NOHIT,
			//for after being hit
			IFRAME,
			//for dodging
			DODGE,
			//for being dead
			DEATH
		}
		
		//determines if entity can give inputs or if entity is locked into an animation
		public enum LockState {
			//for nothing special happening and free to move
			FREE,
			//for being hit normally
			LOCK,
			//for dodging
			DODGE,
			//for doing whatever the 1st assigned attack is
			ATTACK1LOCK,
			//for pressing up on a platform to climb on top of it
			BOOSTLOCK,
			//for attacking in the air
			ATTACKJUMP,
			//for being dead
			DEATH,
			//for being knocked down
			DOWN
		}
		
		public enum EnemyType {
			//for wasp family of enemies to recognize each other
			WASP,
			//for wasp family of enemies to recognize stationary flower turret enemies that can be harvested
			FLOWER,
			//default type that has no special interactions that need a type label
			NONE
		}

}
