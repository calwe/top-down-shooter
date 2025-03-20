package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.Weapon;
import io.calwe.topdownshooter.entities.Equipment.EquipmentDrop;
import io.calwe.topdownshooter.screens.Play;

import java.util.*;

public class Crate extends Entity{
    float scale = 0.4f;

    public Crate(Texture texture, Vector2 position){
        this.layer = 0;
        this.hasSolidCollision = false;
        this.width = 32;
        this.height = 32;
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
            //if the e key has just been pressed down
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                Random random = new Random();
                if (random.nextInt(100) < 50){
                    if (Play.currentTier == 1){
                        List<String> weapons = Collections.list(Play.commonWeapons.keys());
                        Weapon weaponChoice = Play.commonWeapons.get(weapons.get(random.nextInt(weapons.size())));
                        WeaponDrop weaponDrop = new WeaponDrop(weaponChoice, pos, true);
                        Play.entitiesToAdd.add(weaponDrop);
                    }
                    else if (Play.currentTier == 2){
                        List<String> weapons = Collections.list(Play.uncommonWeapons.keys());
                        Weapon weaponChoice = Play.uncommonWeapons.get(weapons.get(random.nextInt(weapons.size())));
                        WeaponDrop weaponDrop = new WeaponDrop(weaponChoice, pos, true);
                        Play.entitiesToAdd.add(weaponDrop);
                    }
                    else if (Play.currentTier == 3){
                        List<String> weapons = Collections.list(Play.rareWeapons.keys());
                        Weapon weaponChoice = Play.rareWeapons.get(weapons.get(random.nextInt(weapons.size())));
                        WeaponDrop weaponDrop = new WeaponDrop(weaponChoice, pos, true);
                        Play.entitiesToAdd.add(weaponDrop);
                    }
                    else if (Play.currentTier == 4){
                        List<String> weapons = Collections.list(Play.epicWeapons.keys());
                        Weapon weaponChoice = Play.epicWeapons.get(weapons.get(random.nextInt(weapons.size())));
                        WeaponDrop weaponDrop = new WeaponDrop(weaponChoice, pos, true);
                        Play.entitiesToAdd.add(weaponDrop);
                    }
                    else if (Play.currentTier == 5){
                        List<String> weapons = Collections.list(Play.legendaryWeapons.keys());
                        Weapon weaponChoice = Play.legendaryWeapons.get(weapons.get(random.nextInt(weapons.size())));
                        WeaponDrop weaponDrop = new WeaponDrop(weaponChoice, pos, true);
                        Play.entitiesToAdd.add(weaponDrop);
                    }

                }
                else{
                    EquipmentDrop equipment = Play.equipment[random.nextInt(Play.equipment.length)].getCopy();
                    equipment.pos = this.pos;
                    Play.entitiesToAdd.add(equipment);
                }
                //Destroy this drop
                Play.entitiesToRemove.add(this);
            }
        }
    }
}
