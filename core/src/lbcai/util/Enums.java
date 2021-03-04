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
			RUN,
			SKID
		}
		
		public enum HitState {
			NOHIT,
			IFRAME
		}
		
		public enum LockState {
			FREE,
			LOCK,
			ATTACK1LOCK
		}
		
		
}
