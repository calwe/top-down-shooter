package io.calwe.topdownshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.calwe.topdownshooter.Game;

public class EquipmentInfo implements Screen {
    SpriteBatch batch;
    public int score = 0;
    public Game game;
    Texture hollowPoint;
    Texture APBullets;
    Texture medkit;
    Texture kevlar;
    Texture redDotSight;
    Texture bomb;
    Texture magazine;

    @Override
    public void show() {
        //Initialise the spritebatch and load all the textures
        batch = new SpriteBatch();
        hollowPoint = new Texture("Equipment/ammo.png");
        APBullets = new Texture("Equipment/APRounds.png");
        medkit = new Texture("Equipment/medkit.png");
        kevlar = new Texture("Equipment/flakVest.png");
        redDotSight = new Texture("Equipment/redDotSight.png");
        magazine = new Texture("Equipment/magazine.png");
        bomb = new Texture("Equipment/outlinedBomb.png");
    }

    @Override
    public void render(float delta) {
        try{
            //Return to the start menu if x is pressed
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                game.StartMenu();
            }
            //Go on to the next info screen if enter is pressed
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                game.ZombiesInfo();
            }

            // clear the screen to black
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            //Initialise a new font
            BitmapFont font = new BitmapFont();
            //Set the font color and size
            font.setColor(Color.WHITE);
            font.getData().setScale(8f);
            //draw the title
            font.draw(batch, "EQUIPMENT", Gdx.graphics.getWidth()/2f-300, Gdx.graphics.getHeight()*0.9f);
            //Draw all the equipment icons
            batch.draw(hollowPoint, Gdx.graphics.getWidth()*0.1f, Gdx.graphics.getHeight()*0.75f-32, 48,48);
            batch.draw(medkit, Gdx.graphics.getWidth()*0.1f, Gdx.graphics.getHeight()*0.68f-32, 48,48);
            batch.draw(kevlar, Gdx.graphics.getWidth()*0.1f, Gdx.graphics.getHeight()*0.61f-32, 48,48);
            batch.draw(APBullets, Gdx.graphics.getWidth()*0.1f, Gdx.graphics.getHeight()*0.54f-32, 48,48);
            batch.draw(redDotSight, Gdx.graphics.getWidth()*0.1f, Gdx.graphics.getHeight()*0.47f-32, 48,48);
            batch.draw(magazine, Gdx.graphics.getWidth()*0.1f, Gdx.graphics.getHeight()*0.4f-32, 48,48);
            batch.draw(bomb, Gdx.graphics.getWidth()*0.1f, Gdx.graphics.getHeight()*0.33f-32, 48,48);

            //set the text size
            font.getData().setScale(3f);
            //draw all the text
            font.draw(batch, "HOLLOW POINT BULLETS: DAMAGE +20%", Gdx.graphics.getWidth()/2f-700, Gdx.graphics.getHeight()*0.75f);
            font.draw(batch, "MEDKIT: RESTORE 25% HEALTH", Gdx.graphics.getWidth()/2f-700, Gdx.graphics.getHeight()*0.68f);
            font.draw(batch, "KEVLAR VEST: INCREASE HEALTH AND MAX HEALTH BY 10%", Gdx.graphics.getWidth()/2f-700, Gdx.graphics.getHeight()*0.61f);
            font.draw(batch, "ARMOR PIERCING BULLETS: INCREASE CRIT DAMAGE BY 25%", Gdx.graphics.getWidth()/2f-700, Gdx.graphics.getHeight()*0.54f);
            font.draw(batch, "RED DOT SIGHT: INCREASE CRIT CHANCE BY 7%", Gdx.graphics.getWidth()/2f-700, Gdx.graphics.getHeight()*0.47f);
            font.draw(batch, "EXTENDED MAGAZINE: 8% CHANCE NOT TO CONSUME AMMUNITION", Gdx.graphics.getWidth()/2f-700, Gdx.graphics.getHeight()*0.4f);
            font.draw(batch, "BOMB: KILL ALL ZOMBIES ON THE SCREEN", Gdx.graphics.getWidth()/2f-700, Gdx.graphics.getHeight()*0.33f);

            font.draw(batch, "PRESS ENTER TO CONTINUE TO THE NEXT PAGE (ZOMBIES).", Gdx.graphics.getWidth()/2f-550, Gdx.graphics.getHeight()*0.15f);
            font.draw(batch, "PRESS X TO RETURN TO THE START MENU.", Gdx.graphics.getWidth()/2f-550, Gdx.graphics.getHeight()*0.1f);
            batch.end();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        //dispose of the spritebatch now its not needed
        batch.dispose();
        hollowPoint.dispose();
        APBullets.dispose();
        redDotSight.dispose();
        magazine.dispose();
        medkit.dispose();
        kevlar.dispose();
        bomb.dispose();
    }
}
