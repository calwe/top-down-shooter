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
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        try{
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                main.StartMenu();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                main.info();
            }

            // clear the screen to black
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            BitmapFont font = new BitmapFont();
            font.setColor(Color.WHITE);
            font.getData().setScale(8f);
            font.draw(batch, "BACKGROUND", Gdx.graphics.getWidth()/2f-400, Gdx.graphics.getHeight()*0.9f);

            font.getData().setScale(3f);
            font.draw(batch, "THE WORLD HAS BEEN OVERRUN BY ZOMBIES.\nYOU MUST TRY TO STAY ALIVE AS LONG AS\nPOSSIBLE WHILE THEY HUNT YOU DOWN.\nYOU CAN FIND WEAPONS AND EQUIPMENT IN CRATES\nSCATTERED AROUND TO HELP YOU STAY ALIVE.\nKEEP MOVING AND DON'T GET CAUGHT!", Gdx.graphics.getWidth()*0.5f-550, Gdx.graphics.getHeight()*0.65f);

            font.getData().setScale(3f);
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
        batch.dispose();
    }
}
