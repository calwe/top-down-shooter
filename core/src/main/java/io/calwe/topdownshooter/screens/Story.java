package io.calwe.topdownshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.calwe.topdownshooter.Main;

public class Story implements Screen {
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
                main.info();
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
            font.draw(batch, "BACKGROUND", Gdx.graphics.getWidth()/2f-400, Gdx.graphics.getHeight()*0.9f);

            //set the font size
            font.getData().setScale(3f);
            //draw the text
            font.draw(batch, "THE WORLD HAS BEEN OVERRUN BY ZOMBIES.\nYOU MUST TRY TO STAY ALIVE AS LONG AS\nPOSSIBLE WHILE THEY HUNT YOU DOWN.\nYOU CAN FIND WEAPONS AND EQUIPMENT IN CRATES\nSCATTERED AROUND TO HELP YOU STAY ALIVE.\nKEEP MOVING AND DON'T GET CAUGHT!", Gdx.graphics.getWidth()*0.5f-550, Gdx.graphics.getHeight()*0.65f);

            font.draw(batch, "PRESS ENTER TO CONTINUE TO THE NEXT PAGE (CONTROLS).", Gdx.graphics.getWidth()/2f-550, Gdx.graphics.getHeight()*0.15f);
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
