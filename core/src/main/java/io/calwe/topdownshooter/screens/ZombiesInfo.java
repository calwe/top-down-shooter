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

public class ZombiesInfo implements Screen {
    SpriteBatch batch;
    public int score = 0;
    public Game game;
    Texture greyZombie;
    Texture redZombie;
    Texture blueZombie;
    Texture orangeZombie;
    Texture landmine;

    @Override
    public void show() {
        //Initialise the spritebatch and load the textures
        batch = new SpriteBatch();
        greyZombie = new Texture("Enemies/greyZombie.png");
        redZombie = new Texture("Enemies/redZombie.png");
        orangeZombie = new Texture("Enemies/orangeZombie.png");
        blueZombie = new Texture("Enemies/blueZombie.png");
        landmine = new Texture("World/Landmine.png");
    }

    @Override
    public void render(float delta) {
        try{
            //Return to the start menu if x or enter is pressed
            if (Gdx.input.isKeyJustPressed(Input.Keys.X) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                game.StartMenu();
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
            font.draw(batch, "THREATS", Gdx.graphics.getWidth()/2f-300, Gdx.graphics.getHeight()*0.9f);
            //draw the zombies
            batch.draw(orangeZombie, 50, Gdx.graphics.getHeight()*0.75f-72, 72,90);
            batch.draw(blueZombie, 50, Gdx.graphics.getHeight()*0.63f-72, 72,90);
            batch.draw(greyZombie, 50, Gdx.graphics.getHeight()*0.51f-72, 72,90);
            batch.draw(redZombie, 50, Gdx.graphics.getHeight()*0.39f-72, 72,90);
            batch.draw(landmine, 50, Gdx.graphics.getHeight()*0.27f-72, 72,72);

            //change the text size
            font.getData().setScale(2.5f);
            //draw the text
            font.draw(batch, "NORMAL ZOMBIE: CHARGES AT YOU AND DEALS DAMAGE IF IT COLLIDES WITH YOU.", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.75f);

            font.draw(batch, "EXPLOSIVE ZOMBIE: RELEASES PROJECTILES WHEN KILLED.", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.63f);

            font.draw(batch, "RANGED ZOMBIE: LAUNCHES PROJECTILES AT YOU PERIODICALLY.", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.51f);

            font.draw(batch, "CHARGING ZOMBIE: WHEN NEARBY IT LOCKS ON TO YOU AND CHARGES AT YOU AFTER\nA SHORT PERIOD. THE DIRECTION IT IS GOING TO CHARGE HAS A RED OVERLAY.", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.39f);

            font.draw(batch, "LANDMINE: EXPLODES WHEN YOU OR A ZOMBIE STEPS ON IT.", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.27f);

            //change the text size
            font.getData().setScale(3f);
            //draw the text
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
        //dispose of the spritebatch and the textures now they aren't needed
        batch.dispose();
        greyZombie.dispose();
        blueZombie.dispose();
        orangeZombie.dispose();
        redZombie.dispose();
    }
}
