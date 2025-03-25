package io.calwe.topdownshooter;

import io.calwe.topdownshooter.screens.Death;
import io.calwe.topdownshooter.screens.Play;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Game extends com.badlogic.gdx.Game {
    @Override
    public void create() {
        // set the initial "screen" to the play screen (as we don't have a main menu)
        setScreen(new Death());
    }
}
