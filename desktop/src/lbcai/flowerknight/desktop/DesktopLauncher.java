package lbcai.flowerknight.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import lbcai.flowerknight.MainGame;
import lbcai.flowerknight.mainGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Flower Knight";
		config.addIcon("TitleIcon.png", FileType.Internal);
		new LwjglApplication(new MainGame(), config);
	}
}
