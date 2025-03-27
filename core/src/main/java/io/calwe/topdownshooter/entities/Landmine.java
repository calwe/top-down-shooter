package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Enemies.Enemy;
import io.calwe.topdownshooter.screens.Play;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;

public class Landmine extends Entity{
    boolean detonating = false;
    float timer = 0;
    final float knockback;
    final Animation<TextureRegion> explodeAnimation;
    //The damage dealt to the player by an explosion.
    // Enemies take damage based on a formula so they always take more damage than they have health so the mines always kill them
    final int playerDamage = 30;

    public Landmine(Texture texture, Vector2 position, Animation<TextureRegion> explodeAnimation, float knockback){
        this.knockback = knockback;
        this.layer = 0;
        this.hasSolidCollision = false;
        this.width = 12;
        this.height = 12;
        this.sprite = new Sprite(texture, width, height);
        this.explodeAnimation = explodeAnimation;
        this.pos = new Vector2(position.x, position.y);
        this.momentum = new Vector2(0, 0);
        bounds.x = pos.x;
        bounds.y = pos.y;
        bounds.width = width;
        bounds.height = height;
    }

    @Override
    public void logic() {
        //The weapon shouldn't move, but this also checks for collisions.
        momentum = new Vector2(0,0);
        tryMove();
        //Adjust the sprite position depending on whether we are exploding - because the explosion graphic
        // is bigger than the landmine graphic
        if (detonating){
            sprite.setPosition(pos.x-12, pos.y-12);
        }
        else{
            sprite.setPosition(pos.x, pos.y);
        }

    }

    public void  detonate(){
        //Set detonating to true
        //Resize the entity because the explosion is bigger than the landmine
        detonating = true;
        width = 36;
        height = 36;
        //Loop through all entities - deal damage to all players and enemies within 18.
        // Enemies take damage equal to the health of the toughest enemy - so it will always kill any enemy
        // Players take damage equal to the damage stat.
        // Also apply knockback to players - enemies will be dead so theres no point applying it to them.
        for (Entity e : Play.entities){
            if (pos.dst(e.pos) <= 18){
                if (e instanceof Enemy){
                    ((Enemy)e).takeDamage(Math.round(25 * (1 + (0.33f*(Play.currentTier-1)))));
                }
                else if (e instanceof Player){
                    ((Player)e).takeDamage(playerDamage);
                    Vector2 direction = new Vector2(e.pos.x- pos.x, e.pos.y - pos.y);
                    direction.nor();
                    direction.scl(knockback);
                    ((Player)e).applyKnockback(direction);
                }
            }
        }
    }
    @Override
    public void OnEntityCollision(Entity e){
        //If this object is colliding with a player and has not already started exploding
        if (!detonating) {
            if (e instanceof Player || e instanceof Enemy){
                // run the detonate function and play an explosion sound
                detonate();
                Gdx.audio.newSound(Gdx.files.internal("explosion.mp3")).play(0.3f);
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch){
        //If the landmine is exploding, draw the explosion animation
        if (detonating) {
            TextureRegion currentFrame = explodeAnimation.getKeyFrame(timer, false);
            // Set the animation's current frame to the right image
            sprite.setRegion(currentFrame);
            //Resize the sprite as the explosion image is bigger than the landmine image
            sprite.setSize(36, 36);
            sprite.draw(batch);
            //Increase the timer keeping track of what frame of the animation we are on
            timer += Gdx.graphics.getDeltaTime();
            //if the explosion animation has finished, destroy the landmine
            if (timer > explodeAnimation.getAnimationDuration()){
                Play.entitiesToRemove.add(this);
            }
        }
        else{
            //draw the landmine
            sprite.draw(batch);
        }


    }
}
