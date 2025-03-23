package io.calwe.topdownshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import io.calwe.topdownshooter.Main;

public class GameOver implements Screen {
    SpriteBatch batch;
    public int score = 0;
    public Main main;
    Texture gravestone;

    @Override
    public void show() {
        batch = new SpriteBatch();
        gravestone = new Texture("Gravestone.png");
    }

    @Override
    public void render(float delta) {
        try{
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                main.restart();
            }

            // clear the screen to black
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            batch.draw(gravestone, Gdx.graphics.getWidth()/2f-Gdx.graphics.getHeight()*0.25f, Gdx.graphics.getHeight()*0.2f, Gdx.graphics.getHeight()*0.5f,Gdx.graphics.getHeight()*0.5f);
            BitmapFont font = new BitmapFont();
            font.setColor(Color.RED);
            font.getData().setScale(8f);
            font.draw(batch, "GAME OVER...", Gdx.graphics.getWidth()/2f-350, Gdx.graphics.getHeight()*0.9f);
            font.getData().setScale(3f);
            font.setColor(Color.BLACK);
            font.draw(batch, "SCORE", Gdx.graphics.getWidth()/2f-80, Gdx.graphics.getHeight()*0.4f);
            StringBuilder s = new StringBuilder("" + score);
            while (s.length() < 7){
                s.insert(0, "0");
            }
            font.draw(batch, s.toString(), Gdx.graphics.getWidth()/2f-80, Gdx.graphics.getHeight()*0.4f-50);

            font.setColor(Color.RED);
            font.draw(batch, "PRESS ENTER TO RESTART.", Gdx.graphics.getWidth()/2f-250, Gdx.graphics.getHeight()*0.1f);
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
