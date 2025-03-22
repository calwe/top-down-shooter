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

public class Start implements Screen {
    SpriteBatch batch;
    public Main main;


    @Override
    public void show() {
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        try{
            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                main.restart();
            }

            // clear the screen to black
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            batch.draw(new Texture("Skull.png"), (Gdx.graphics.getWidth()/2f)-(Gdx.graphics.getHeight()*0.25f), Gdx.graphics.getHeight()*0.2f, Gdx.graphics.getHeight()*0.5f,Gdx.graphics.getHeight()*0.5f);
            BitmapFont font = new BitmapFont();
            font.setColor(Color.GREEN);
            font.getData().setScale(8f);
            font.draw(batch, "ZOMBIES!", Gdx.graphics.getWidth()/2f-250, Gdx.graphics.getHeight()*0.9f);
            font.getData().setScale(3f);
            font.draw(batch, "PRESS ENTER TO START.", Gdx.graphics.getWidth()/2f-250, Gdx.graphics.getHeight()*0.1f);
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
