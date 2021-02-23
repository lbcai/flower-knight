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
			IFRAME,
			WALL
		}
		
		public enum RunState {
			IDLE,
			RUN,
			SKID
		}
		
		
}
