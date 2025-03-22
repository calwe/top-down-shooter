package io.calwe.topdownshooter;

import com.badlogic.gdx.Game;
import io.calwe.topdownshooter.screens.GameOver;
import io.calwe.topdownshooter.screens.Play;
import io.calwe.topdownshooter.entities.Entity;
import com.badlogic.gdx.utils.Array;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {


    @Override
    public void create() {
        // set the initial "screen" to the play screen (as we don't have a main menu)
        Play play = new Play();
        setScreen(play);
        Play.main = this;
    }

    public void GameOver(int score){
        GameOver gameOver = new GameOver();
        setScreen(gameOver);
        gameOver.score = score;
        gameOver.main = this;
    }

    public void restart(){
        Play play = new Play();
        setScreen(play);
        Play.main = this;
    }
}
