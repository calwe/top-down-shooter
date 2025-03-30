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
import io.calwe.topdownshooter.Main;
import io.calwe.topdownshooter.Utility;

public class Start implements Screen {
    SpriteBatch batch;
    public Game game;
    Texture skull;


    @Override
    public void show() {
        //Initialise the spritebatch and load the texture
        batch = new SpriteBatch();
        skull = new Texture("Skull.png");
    }

    @Override
    public void render(float delta) {
        try{
            //Start the game if enter is pressed
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                game.restart();
            }
            //Load the backstory screen if I is pressed
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                game.Story();
            }
            // clear the screen to black
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            batch.draw(skull, (Gdx.graphics.getWidth()/2f)-(Gdx.graphics.getHeight()*0.25f), Gdx.graphics.getHeight()*0.2f, Gdx.graphics.getHeight()*0.5f,Gdx.graphics.getHeight()*0.5f);
            //Initialise a new font
            BitmapFont font = Utility.font;
            //Set the font color and size
            font.setColor(Color.GREEN);
            font.getData().setScale(8f);
            //draw the title
            font.draw(batch, "ZOMBIES!", Gdx.graphics.getWidth()/2f-250, Gdx.graphics.getHeight()*0.9f);

            //Set the font size
            font.getData().setScale(3f);
            //draw the text
            font.draw(batch, "PRESS ENTER TO START.", Gdx.graphics.getWidth()/2f-250, Gdx.graphics.getHeight()*0.14f);
            font.draw(batch, "PRESS I FOR INFO.", Gdx.graphics.getWidth()/2f-250, Gdx.graphics.getHeight()*0.08f);
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
        skull.dispose();
    }
}
