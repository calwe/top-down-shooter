package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.screens.Play;

public class Particle extends Entity {
    float lifeTime;
    float timer = 0;

    public Particle(Texture texture, Vector2 startPos){
        this.layer = 0;
        this.pos = startPos;
        this.momentum = new Vector2(0, 0);
        this.slide = 0.99f;
        this.width = 1;
        this.height = 1;
        this.lifeTime = 0.2f;
        this.sprite = new Sprite(texture, width, height);
        this.hasSolidCollision = false;
        this.boundsHeightReduction = 0;
        this.boundsWidthReduction = 0;
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
        sprite.setPosition(pos.x, pos.y);
        timer += Gdx.graphics.getDeltaTime();
        if (timer > lifeTime){
            Play.entitiesToRemove.add(this);
        }
    }
}


