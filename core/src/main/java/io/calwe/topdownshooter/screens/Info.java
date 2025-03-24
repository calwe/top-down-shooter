package io.calwe.topdownshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.calwe.topdownshooter.Main;

public class Info implements Screen {
    SpriteBatch batch;
    public int score = 0;
    public Main main;

    @Override
    public void show() {
        //Initialise the spritebatch
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        try{
            //Return to the start menu if x is pressed
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                main.StartMenu();
            }
            //Go on to the next info screen if enter is pressed
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                main.WeaponsInfo();
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
            font.draw(batch, "CONTROLS", Gdx.graphics.getWidth()/2f-300, Gdx.graphics.getHeight()*0.9f);
            //set the font size
            font.getData().setScale(3f);
            //draw the text
            font.draw(batch, "UP: W", Gdx.graphics.getWidth()/2f-350, Gdx.graphics.getHeight()*0.7f);
            font.draw(batch, "DOWN: S", Gdx.graphics.getWidth()/2f-350, Gdx.graphics.getHeight()*0.65f);
            font.draw(batch, "LEFT: A", Gdx.graphics.getWidth()/2f-350, Gdx.graphics.getHeight()*0.6f);
            font.draw(batch, "RIGHT: D", Gdx.graphics.getWidth()/2f-350, Gdx.graphics.getHeight()*0.55f);
            font.draw(batch, "AIM GUN: CURSOR", Gdx.graphics.getWidth()/2f-350, Gdx.graphics.getHeight()*0.5f);
            font.draw(batch, "FIRE GUN: CLICK", Gdx.graphics.getWidth()/2f-350, Gdx.graphics.getHeight()*0.45f);
            font.draw(batch, "PICK UP ITEM/OPEN CRATE: E", Gdx.graphics.getWidth()/2f-350, Gdx.graphics.getHeight()*0.4f);

            font.draw(batch, "PRESS ENTER TO CONTINUE TO THE NEXT PAGE (WEAPONS).", Gdx.graphics.getWidth()/2f-550, Gdx.graphics.getHeight()*0.15f);
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
        //dispose of the spritebatch now it isn't needed
        batch.dispose();
    }
}

