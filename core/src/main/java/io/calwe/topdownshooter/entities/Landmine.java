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
    boolean detonating;
    float timer = 0;
    float knockback;
    Animation<TextureRegion> explodeAnimation;
    int damage;

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
        damage = Math.round(25 * (1 + (0.33f*(Play.currentTier-1))));
        //The weapon shouldn't move, but this also checks for collisions.
        momentum = new Vector2(0,0);
        if (detonating){
            sprite.setPosition(pos.x-12, pos.y-12);
        }
        else{
            sprite.setPosition(pos.x, pos.y);
        }
        tryMove();
    }

    @Override
    //This handles collisions while moving
    protected void tryMove () {
        //Calculate the current collider bounds
        bounds.x = pos.x;
        bounds.y = pos.y;
        bounds.width = width;
        bounds.height = height;
        //Get all the other objects we could collide with
        Dictionary<Rectangle, Entity> collideableRects = Play.getOtherColliderRects(this);
        Object[] rects = Collections.list(collideableRects.keys()).toArray();
        List<Entity> entityCollisions = new ArrayList<>();
        // Iterate through each other object we could collide with
        for (int i = 0; i < rects.length; i++) {
            Rectangle rect = (Rectangle)rects[i];
            //If we collide with an entity
            if (bounds.overlaps(rect)) {
                //Add them to the list of entities we collided with
                if (!entityCollisions.contains(collideableRects.get(rect))) {
                    entityCollisions.add(collideableRects.get(rect));
                }
            }
        }
        // Iterate through each other object we could collide with
        for (int i = 0; i < rects.length; i++) {
            Rectangle rect = (Rectangle)rects[i];
            //If we collide with an entity
            if (bounds.overlaps(rect)) {
                //Add them to the list of entities we collided with
                if (!entityCollisions.contains(collideableRects.get(rect))) {
                    entityCollisions.add(collideableRects.get(rect));
                }
            }
        }
        //Call OnEntityCollision for each entity we collided with
        for (Entity e : entityCollisions) {
            OnEntityCollision(e);
        }
    }

    void  detonate(){
        detonating = true;
        width = 36;
        height = 36;
        for (Entity e : Play.entities){
            if (pos.dst(e.pos) <= 18){
                if (e instanceof Enemy){
                    ((Enemy)e).takeDamage(damage);
                    Vector2 direction = new Vector2(e.pos.x- pos.x, e.pos.y - pos.y);
                    direction.nor();
                    direction.scl(knockback);
                    ((Enemy)e).applyKnockback(direction);
                }
                else if (e instanceof Player){
                    ((Player)e).takeDamage(damage);
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
        //If this object is colliding with a player
        if (!detonating) {
            if (e instanceof Player || e instanceof Enemy){
                detonate();
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch){
        if (detonating) {
            TextureRegion currentFrame = explodeAnimation.getKeyFrame(timer, false);
            // Set the animation's current frame to the player sprite's image
            sprite.setRegion(currentFrame);
            sprite.setSize(36, 36);
            // Render the player's animated legs
            sprite.draw(batch);
            timer += Gdx.graphics.getDeltaTime();
            if (timer > explodeAnimation.getAnimationDuration()){
                Play.entitiesToRemove.add(this);
            }
        }
        else{
            sprite.draw(batch);
        }


    }
}
