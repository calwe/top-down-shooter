package io.calwe.topdownshooter;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main  {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Top Down Shooter");
        cfg.setWindowedMode(1280, 720);
        new Lwjgl3Application(new Game(), cfg);
    }
}
