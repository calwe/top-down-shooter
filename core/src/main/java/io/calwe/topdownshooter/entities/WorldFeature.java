package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.screens.Play;

import java.util.*;


//Worldfeature is used for houses and trees. It represents a feature in the world you should be able to go into/under
public class WorldFeature extends Entity{
    final float scale;
    boolean playerIsInside = false;
    final Texture normalTexture;
    final Texture insideTexture;
    final Obstacle[] components;

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
        Collections.addAll(Play.entitiesToAdd, components);
    }

    @Override
    public void logic() {
        //The weapon shouldn't move, but this also checks for collisions.
        momentum = new Vector2(0,0);
        tryMove();
        //Calculate the graphic position based on the scale
        sprite.setPosition(pos.x-(width*((1-scale)/2f)), pos.y-(height*((1-scale)/2f)));

    }

    @Override
    public void OnEntityCollision(Entity e){
        //If this object is colliding with a player
        if (e instanceof Player){
            //set playerIsInside to true
            playerIsInside = true;
        }
        //If we are overlapping with a smaller or equal worldfeature, destroy it. This is to prevent trees from spawning inside houses
        if (e instanceof WorldFeature){
            WorldFeature feature = (WorldFeature)e;
            if (feature.width*feature.height <= width*height){
                Play.entitiesToAdd.remove(e);
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch){
        //If the player is inside/under, draw the transparent texture so the player can be seen
        if (playerIsInside){
            sprite.setRegion(insideTexture);
        }
        //Else draw the normal texture
        else{
            sprite.setRegion(normalTexture);
        }
        sprite.draw(batch);
        //Reset this each update loop
        playerIsInside = false;
    }
}
