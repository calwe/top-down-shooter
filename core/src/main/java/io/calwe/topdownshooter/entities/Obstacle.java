package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Obstacle extends Entity{
    final float scale;

    public Obstacle(Texture texture, Vector2 position, float scale, int width, int height) {
        this.layer = 20;
        this.hasSolidCollision = true;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.sprite = new Sprite(texture, width, height);
        sprite.setScale(scale);
        this.pos = new Vector2(position.x - (width*scale/2f), position.y-(height*scale/2f));
        this.momentum = new Vector2(0, 0);
        bounds.x = pos.x;
        bounds.y = pos.y;
        bounds.width = width;
        bounds.height = height;
    }

    //Some obstacles need to be on a specific layer
    public Obstacle(Texture texture, Vector2 position, float scale, int width, int height, boolean solidCollision, int layer) {
        this.layer = layer;
        this.hasSolidCollision = solidCollision;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.sprite = new Sprite(texture, width, height);
        sprite.setScale(scale);
        this.pos = new Vector2(position.x - (width*scale/2f), position.y-(height*scale/2f));
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
        //Offset the sprite to account for the scale
        sprite.setPosition(pos.x-(width*((1-scale)/2f)), pos.y-(height*((1-scale)/2f)));
    }
}
