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

public class ZombiesInfo implements Screen {
    SpriteBatch batch;
    public int score = 0;
    public Main main;
    Texture greyZombie;
    Texture redZombie;
    Texture blueZombie;
    Texture orangeZombie;
    Texture landmine;

    @Override
    public void show() {
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
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                main.StartMenu();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                main.StartMenu();
            }

            // clear the screen to black
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            BitmapFont font = new BitmapFont();
            font.setColor(Color.WHITE);
            font.getData().setScale(8f);
            font.draw(batch, "THREATS", Gdx.graphics.getWidth()/2f-300, Gdx.graphics.getHeight()*0.9f);

            batch.draw(orangeZombie, 50, Gdx.graphics.getHeight()*0.75f-72, 72,90);
            batch.draw(blueZombie, 50, Gdx.graphics.getHeight()*0.63f-72, 72,90);
            batch.draw(greyZombie, 50, Gdx.graphics.getHeight()*0.51f-72, 72,90);
            batch.draw(redZombie, 50, Gdx.graphics.getHeight()*0.39f-72, 72,90);
            batch.draw(landmine, 50, Gdx.graphics.getHeight()*0.27f-72, 72,72);

            font.getData().setScale(2.5f);
            font.draw(batch, "NORMAL ZOMBIE: CHARGES AT YOU AND DEALS DAMAGE IF IT COLLIDES WITH YOU.", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.75f);

            font.draw(batch, "EXPLOSIVE ZOMBIE: RELEASES PROJECTILES WHEN KILLED.", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.63f);

            font.draw(batch, "RANGED ZOMBIE: LAUNCHES PROJECTILES AT YOU PERIODICALLY.", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.51f);

            font.draw(batch, "CHARGING ZOMBIE: WHEN NEARBY IT LOCKS ON TO YOU AND CHARGES AT YOU AFTER\nA SHORT PERIOD. THE DIRECTION IT IS GOING TO CHARGE HAS A RED OVERLAY.", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.39f);

            font.draw(batch, "LANDMINE: EXPLODES WHEN YOU OR A ZOMBIE STEPS ON IT.", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.27f);

            font.getData().setScale(3f);
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
        greyZombie.dispose();
        blueZombie.dispose();
        orangeZombie.dispose();
        redZombie.dispose();
    }
}
