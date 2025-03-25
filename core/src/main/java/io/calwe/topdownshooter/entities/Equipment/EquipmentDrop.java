package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Entity;
import io.calwe.topdownshooter.entities.HitText;
import io.calwe.topdownshooter.entities.Player;
import io.calwe.topdownshooter.screens.Play;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;

public class EquipmentDrop extends Entity {
    String upgradeName;
    String upgradeDescription;
    float scale = 0.4f;
    Texture texture;

    public EquipmentDrop(Texture texture, String upgradeName, String upgradeDescription) {
        this.layer = 0;
        this.hasSolidCollision = false;
        this.width = 28;
        this.height = 32;
        this.texture = texture;
        this.sprite = new Sprite(texture, width, height);
        sprite.setScale(scale);
        this.pos = new Vector2(0, 0);
        this.momentum = new Vector2(0, 0);
        bounds.x = pos.x;
        bounds.y = pos.y;
        bounds.width = (width*scale);
        bounds.height = (height*scale);
        this.upgradeName = upgradeName;
        this.upgradeDescription = upgradeDescription;
    }

    @Override
    public void logic() {
        //The drop shouldn't move, but this also checks for collisions so we still need to run just with no movement
        momentum = new Vector2(0,0);
        sprite.setPosition(pos.x-(width*((1-scale)/2f)), pos.y-(height*((1-scale)/2f)));
        tryMove();
    }

    @Override
    public void OnEntityCollision(Entity e){
        //If this object is colliding with a player
        if (e instanceof Player){
            //if the e key has just been pressed down
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                //Apply the drop's current effect to the player, and show floating text saying what bonus the drop gave us
                Player p = (Player)e;
                ApplyAffect(p);
                HitText h = new HitText(upgradeDescription, new Vector2(p.pos.x, p.pos.y + p.height), Color.YELLOW, 1);
                Play.entitiesToAdd.add(h);
                //Destroy this drop
                Play.entitiesToRemove.add(this);
            }
        }
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

    //Unimplemented, but supposed to be overriden by each subclass so that the drop actually provides a benefit
    public void ApplyAffect(Player p){

    }

    //Duplicate the equipment provided
    public EquipmentDrop getCopy(){
        return new EquipmentDrop(texture, upgradeName, upgradeDescription);
    }

}




