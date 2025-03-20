package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.Weapon;
import io.calwe.topdownshooter.entities.Equipment.EquipmentDrop;
import io.calwe.topdownshooter.screens.Play;

import java.util.*;

public class WorldFeature extends Entity{
    float scale;
    boolean playerIsInside = false;
    Texture normalTexture;
    Texture insideTexture;
    Obstacle[] components;

    public WorldFeature(Texture normalTexture, Texture insideTexture, Vector2 position, float scale, int width, int height, Obstacle[] components){
        this.layer = 50;
        this.hasSolidCollision = false;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.normalTexture = normalTexture;
        this.insideTexture = insideTexture;
        this.components = components;
        this.sprite = new Sprite(normalTexture, width, height);
        sprite.setScale(scale);
        this.pos = new Vector2(position.x - (width*scale/2f), position.y-(height*scale/2f));
        this.momentum = new Vector2(0, 0);
        bounds.x = pos.x;
        bounds.y = pos.y;
        bounds.width = width;
        bounds.height = height;
        for (Obstacle o : components) {
            Play.entitiesToAdd.add(o);
        }
    }

    @Override
    public void logic() {
        //The weapon shouldn't move, but this also checks for collisions.
        momentum = new Vector2(0,0);
        sprite.setPosition(pos.x-(width*((1-scale)/2f)), pos.y-(height*((1-scale)/2f)));
        tryMove();
    }

    @Override
    //This handles collisions while moving
    protected void tryMove () {
        //Calculate the current collider bounds
        bounds.x = pos.x;
        bounds.y = pos.y;
        bounds.width = width*scale;
        bounds.height = height*scale;
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

    @Override
    public void OnEntityCollision(Entity e){
        //If this object is colliding with a player
        if (e instanceof Player){
            playerIsInside = true;
        }
    }

    @Override
    public void draw(SpriteBatch batch){
        if (playerIsInside){
            sprite.setRegion(insideTexture);
        }
        else{
            sprite.setRegion(normalTexture);
        }
        sprite.draw(batch);
        playerIsInside = false;
    }
}
