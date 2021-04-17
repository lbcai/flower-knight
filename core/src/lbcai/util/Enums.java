package lbcai.util;

public class Enums {
	//enums are like discrete final variables, like steps you can set something to.
		public enum Facing {
			LEFT,
			RIGHT
		}
		
		public enum JumpState {
			GROUNDED,
			JUMPING,
			FALLING,
			WALL
		}
		
		public enum RunState {
			IDLE,
			SQUAT,
			COMBATIDLE,
			RUN,
			SKID
		}
		
		public enum HitState {
			NOHIT,
			IFRAME,
			DODGE,
			DEATH
		}
		
		public enum LockState {
			FREE,
			//for being hit normally
			LOCK,
			DODGE,
			ATTACK1LOCK,
			BOOSTLOCK,
			ATTACKJUMP,
			DEATH
		}
		

}
