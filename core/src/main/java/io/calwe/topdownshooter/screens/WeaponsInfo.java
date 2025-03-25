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

public class WeaponsInfo implements Screen {
    SpriteBatch batch;
    public int score = 0;
    public Game game;
    Texture pistolTexture;
    Texture assaultTexture;
    Texture SMGTexture;
    Texture sniperTexture;
    Texture shotgunTexture;

    @Override
    public void show() {
        //Initialise the spritebatch and load the textures
        batch = new SpriteBatch();
        pistolTexture = new Texture("PistolSideOn.png");
        SMGTexture = new Texture("SMGSideOn.png");
        assaultTexture = new Texture("AssaultRifleSideOn.png");
        sniperTexture = new Texture("sniperSideOn.png");
        shotgunTexture = new Texture("shotgunSideOn.png");
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
                game.EquipmentInfo();
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
            font.draw(batch, "WEAPONS", Gdx.graphics.getWidth()/2f-300, Gdx.graphics.getHeight()*0.9f);

            //draw the weapon icons
            batch.draw(pistolTexture, 50, Gdx.graphics.getHeight()*0.75f-36, 96,36);
            batch.draw(SMGTexture, 50, Gdx.graphics.getHeight()*0.55f-36, 96,36);
            batch.draw(assaultTexture, 50, Gdx.graphics.getHeight()*0.35f-36, 96,36);
            batch.draw(sniperTexture, Gdx.graphics.getWidth()*0.5f, Gdx.graphics.getHeight()*0.75f-36, 96,36);
            batch.draw(shotgunTexture, Gdx.graphics.getWidth()*0.5f, Gdx.graphics.getHeight()*0.55f-36, 96,36);

            //change the text size
            font.getData().setScale(2f);
            //draw the text
            font.draw(batch, "PISTOL:", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.75f);
            font.draw(batch, "DAMAGE: MEDIUM\nRATE OF FIRE: MEDIUM\nAMMO: MEDIUM\nACCURACY: LOW", Gdx.graphics.getWidth()*0.1f+300, Gdx.graphics.getHeight()*0.75f);

            font.draw(batch, "SMG:", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.55f);
            font.draw(batch, "DAMAGE: VERY LOW\nRATE OF FIRE: VERY HIGH\nAMMO: HIGH\nACCURACY: VERY LOW", Gdx.graphics.getWidth()*0.1f+300, Gdx.graphics.getHeight()*0.55f);

            font.draw(batch, "ASSAULT RIFLE:", Gdx.graphics.getWidth()*0.1f+50, Gdx.graphics.getHeight()*0.35f);
            font.draw(batch, "DAMAGE: LOW\nRATE OF FIRE: HIGH\nAMMO: HIGH\nACCURACY: HIGH", Gdx.graphics.getWidth()*0.1f+300, Gdx.graphics.getHeight()*0.35f);

            font.draw(batch, "SNIPER RIFLE:", Gdx.graphics.getWidth()*0.6f+50, Gdx.graphics.getHeight()*0.75f);
            font.draw(batch, "DAMAGE: HIGH\nRATE OF FIRE: VERY LOW\nAMMO: LOW\nACCURACY: HIGH\nSPECIAL: BULLETS PIERCE THROUGH ENEMIES", Gdx.graphics.getWidth()*0.6f+300, Gdx.graphics.getHeight()*0.75f);

            font.draw(batch, "SHOTGUN:", Gdx.graphics.getWidth()*0.6f+50, Gdx.graphics.getHeight()*0.55f);
            font.draw(batch, "DAMAGE: HIGH\nRATE OF FIRE: LOW\nAMMO: LOW\nACCURACY: LOW\nSPECIAL: FIRES A SPRAY OF BULLETS", Gdx.graphics.getWidth()*0.6f+300, Gdx.graphics.getHeight()*0.55f);
            //change the text size
            font.getData().setScale(3f);
            //draw the text
            font.draw(batch, "PRESS ENTER TO CONTINUE TO THE NEXT PAGE (EQUIPMENT).", Gdx.graphics.getWidth()/2f-550, Gdx.graphics.getHeight()*0.15f);
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
        pistolTexture.dispose();
        SMGTexture.dispose();
        assaultTexture.dispose();
        sniperTexture.dispose();
        shotgunTexture.dispose();
    }
}
