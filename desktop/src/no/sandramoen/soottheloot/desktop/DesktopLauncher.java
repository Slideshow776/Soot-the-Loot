package no.sandramoen.soottheloot.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.awt.Dimension;

import no.sandramoen.soottheloot.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		config.width = (int) (dimension.width * .9f);
		float screenRation = .461538461f;
		config.height = (int) (config.width * screenRation);

		config.vSyncEnabled = true;
		config.useGL30 = true;

		// miscellaneous
		config.title = "Soot the Loot";
		config.resizable = true;
		config.addIcon("images/excluded/desktopIcon.png", Files.FileType.Internal);
		new LwjglApplication(new MyGdxGame(), config);
	}
}
