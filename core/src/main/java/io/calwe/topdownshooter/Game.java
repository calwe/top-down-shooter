package io.calwe.topdownshooter;

import io.calwe.topdownshooter.screens.*;

public class Game extends com.badlogic.gdx.Game{
    @Override
    public void create() {
        // set the initial "screen" to the play screen (as we don't have a main menu)
        Start start = new Start();
        setScreen(start);
        start.game = this;
    }

    //Switch to the game over screen and pass in the score
    public void GameOver(int score){
        GameOver gameOver = new GameOver();
        setScreen(gameOver);
        gameOver.score = score;
        gameOver.game = this;
    }

    //switch to the start menu screen
    public void StartMenu(){
        Start start = new Start();
        setScreen(start);
        start.game = this;
    }

    //start the game
    public void restart(){
        Play play = new Play();
        setScreen(play);
        Play.game = this;
    }

    //switch to the info screen
    public void info(){
        Info info = new Info();
        setScreen(info);
        info.game = this;
    }

    //switch to the equipment info screen
    public void EquipmentInfo(){
        EquipmentInfo equipmentInfo = new EquipmentInfo();
        setScreen(equipmentInfo);
        equipmentInfo.game = this;
    }

    //switch to the weapons info screen
    public void WeaponsInfo(){
        WeaponsInfo weaponsInfo = new WeaponsInfo();
        setScreen(weaponsInfo);
        weaponsInfo.game = this;
    }

    //switch to the zombies info screen
    public void ZombiesInfo(){
        ZombiesInfo zombieInfo = new ZombiesInfo();
        setScreen(zombieInfo);
        zombieInfo.game = this;
    }
    //switch to the backstory screen
    public void Story(){
        Story story = new Story();
        setScreen(story);
        story.game = this;
    }
}
