package io.calwe.topdownshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import io.calwe.topdownshooter.Game;
import io.calwe.topdownshooter.Utility;

public class GameOver implements Screen {
    SpriteBatch batch;
    public int score = 0;
    public Game game;
    Texture gravestone;

    @Override
    public void show() {
        //Initialise the spritebatch and load the texture
        batch = new SpriteBatch();
        gravestone = new Texture("Gravestone.png");
    }

    @Override
    public void render(float delta) {
        try{
            //if enter is pressed start a new game
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                game.restart();
            }

            // clear the screen to black
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            //draw a gravestone
            batch.draw(gravestone, Gdx.graphics.getWidth()/2f-Gdx.graphics.getHeight()*0.25f, Gdx.graphics.getHeight()*0.2f, Gdx.graphics.getHeight()*0.5f,Gdx.graphics.getHeight()*0.5f);

            // get a copy of the game font
            BitmapFont font = Utility.font;
            //Set the font color and size
            font.setColor(Color.RED);
            font.getData().setScale(8f);
            //draw the title
            font.draw(batch, "GAME OVER...", Gdx.graphics.getWidth()/2f-350, Gdx.graphics.getHeight()*0.9f);
            //set the font size and color
            font.getData().setScale(3f);
            font.setColor(Color.BLACK);
            //draw the score text
            font.draw(batch, "SCORE", Gdx.graphics.getWidth()/2f-80, Gdx.graphics.getHeight()*0.4f);
            StringBuilder s = new StringBuilder("" + score);
            while (s.length() < 7){
                s.insert(0, "0");
            }
            font.draw(batch, s.toString(), Gdx.graphics.getWidth()/2f-80, Gdx.graphics.getHeight()*0.4f-50);
            //set the text color
            font.setColor(Color.RED);
            //draw the text
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
        //dispose of the spritebatch and the textures now they aren't needed
        batch.dispose();
        gravestone.dispose();
    }
}
