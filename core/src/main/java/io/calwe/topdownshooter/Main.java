package io.calwe.topdownshooter;

import com.badlogic.gdx.Game;
import io.calwe.topdownshooter.screens.*;
import com.badlogic.gdx.Gdx;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {


    @Override
    public void create() {
        // set the initial "screen" to the play screen (as we don't have a main menu)
        Start start = new Start();
        setScreen(start);
        start.main = this;
    }

    public void GameOver(int score){
        GameOver gameOver = new GameOver();
        setScreen(gameOver);
        gameOver.score = score;
        gameOver.main = this;
    }

    public void StartMenu(){
        Start start = new Start();
        setScreen(start);
        start.main = this;
    }

    public void restart(){
        Play play = new Play();
        setScreen(play);
        Play.main = this;
    }

    public void info(){
        Info info = new Info();
        setScreen(info);
        info.main = this;
    }

    public void EquipmentInfo(){
        EquipmentInfo equipmentInfo = new EquipmentInfo();
        setScreen(equipmentInfo);
        equipmentInfo.main = this;
    }

    public void WeaponsInfo(){
        WeaponsInfo weaponsInfo = new WeaponsInfo();
        setScreen(weaponsInfo);
        weaponsInfo.main = this;
    }

    public void ZombiesInfo(){
        ZombiesInfo zombieInfo = new ZombiesInfo();
        setScreen(zombieInfo);
        zombieInfo.main = this;
    }
    public void Story(){
        Story story = new Story();
        setScreen(story);
        story.main = this;
    }
}
