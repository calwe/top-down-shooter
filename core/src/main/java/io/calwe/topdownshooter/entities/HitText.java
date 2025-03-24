package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.screens.Play;

//Floating text that rises up from its position - used for showing the damage when you hit an enemy
public class HitText extends Entity {
    //The text to be displayed
    String text;
    //How long before the text should disappear
    float lifeTime;
    //The timer to keep track of how long the text has existed
    float timer;
    //The font
    BitmapFont font;

    public HitText(String text, Vector2 startPos, Color color){
        this.font = new BitmapFont();
        this.font.setColor(color);
        this.font.getData().setScale(0.4f);
        this.pos = startPos;
        this.momentum = new Vector2(0, 0.6f);
        this.slide = 0.99f;
        this.width = 1;
        this.height = 1;
        this.text = text;
        this.hasSolidCollision = false;
        this.boundsHeightReduction = 0;
        this.boundsWidthReduction = 0;
        this.lifeTime = 0.2f;
        this.timer = 0;
        bounds.x = pos.x + boundsWidthReduction;
        bounds.y = pos.y + boundsHeightReduction;
        bounds.width = width - (boundsWidthReduction*2f);
        bounds.height = height - (boundsHeightReduction*2f);
    }

    public HitText(String text, Vector2 startPos, Color color, float lifeTime){
        this.font = new BitmapFont();
        this.font.setColor(color);
        this.font.getData().setScale(0.4f);
        this.pos = startPos;
        this.momentum = new Vector2(0, 0.6f);
        this.slide = 0.99f;
        this.width = 1;
        this.height = 1;
        this.text = text;
        this.hasSolidCollision = false;
        this.boundsHeightReduction = 0;
        this.boundsWidthReduction = 0;
        this.lifeTime = lifeTime;
        this.timer = 0;
        bounds.x = pos.x + boundsWidthReduction;
        bounds.y = pos.y + boundsHeightReduction;
        bounds.width = width - (boundsWidthReduction*2f);
        bounds.height = height - (boundsHeightReduction*2f);
    }

    @Override
    public void logic() {
        tryMove();
        //Reduce their momentum over time
        momentum.scl(slide);
        //Check if the time this text is supposed to last has elapsed. If it has, destroy the text
        timer += Gdx.graphics.getDeltaTime();
        if (timer > lifeTime){
            Play.entitiesToRemove.add(this);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        //draw the text
        font.draw(batch, text, pos.x, pos.y);
    }
}



