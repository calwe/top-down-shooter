package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.screens.Play;

//Particles are 1x1 images that exist for a short time before disappearing - like
// the blood particles released when players are damaged
public class Particle extends Entity {
    //How long the particle exists for
    float lifeTime;
    //how long the particle has existed for
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
        //Move and check for collisions
        tryMove();
        //Reduce their momentum over time
        momentum.scl(slide);
        sprite.setPosition(pos.x, pos.y);
        // If the lifetime of this particle has elapsed, destroy it
        timer += Gdx.graphics.getDeltaTime();
        if (timer > lifeTime){
            Play.entitiesToRemove.add(this);
        }
    }
}


