package ua.pp.oped.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ua.pp.oped.bludbourne.BludBourne;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title="BloodBourne";
		config.useGL30 = false;
		config.width=800;
		config.height=600;
		Application app = new LwjglApplication(new BludBourne(), config);
		app.setLogLevel(Application.LOG_DEBUG );
	}
}
