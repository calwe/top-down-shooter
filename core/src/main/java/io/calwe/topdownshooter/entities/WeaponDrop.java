package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.Weapon;
import io.calwe.topdownshooter.screens.Play;

import java.util.*;

public class WeaponDrop extends Entity{
    final Weapon weapon;
    //Should we randomly generate the ammunition or use that provided
    final boolean randomlyGenerateAmmo;
    //Make the weapon drop graphic smaller as it is to large by default
    final float scale = 0.6f;

    public WeaponDrop(Weapon weapon, Vector2 position){
        this.randomlyGenerateAmmo = false;
        this.layer = 0;
        this.weapon = weapon;
        this.hasSolidCollision = false;
        this.width = 32;
        this.height = 12;
        this.sprite = new Sprite(weapon.sideOn, width, height);
        sprite.setScale(scale);
        this.pos = new Vector2(position.x - (width*scale/2f), position.y-(height*scale/2f));
        this.momentum = new Vector2(0, 0);
        bounds.x = pos.x;
        bounds.y = pos.y;
        bounds.width = width * scale;
        bounds.height = height * scale;
    }
    WeaponDrop(Weapon weapon, Vector2 position, boolean randomlyGenerateAmmo){
        this.randomlyGenerateAmmo = randomlyGenerateAmmo;
        this.layer = 0;
        this.weapon = weapon;
        this.hasSolidCollision = false;
        this.width = 32;
        this.height = 12;
        this.sprite = new Sprite(weapon.sideOn, width, height);
        sprite.setScale(scale);
        this.pos = new Vector2(position.x - (width*scale/2f), position.y-(height*scale/2f));
        this.momentum = new Vector2(0, 0);
        bounds.x = pos.x;
        bounds.y = pos.y;
        bounds.width = width * scale;
        bounds.height = height * scale;
    }

    @Override
    public void logic() {
        //The weapon shouldn't move, but this also checks for collisions.
        momentum = new Vector2(0,0);
        tryMove();
        //Calculate the sprite offset, based on the scale
        sprite.setPosition(pos.x-(width*((1-scale)/2f)), pos.y-(height*((1-scale)/2f)));

    }

    @Override
    public void OnEntityCollision(Entity e){
        //If this object is colliding with a player
        if (e instanceof Player){
            //if the e key has just been pressed down
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                Player p = (Player)e;
                //Add the weapon from this drop to the player's inventory, with random ammo or not depending on the
                // value of randomlyGenerateAmmo
                if (randomlyGenerateAmmo){
                    Random random = new Random();
                    int ammo = random.nextInt(Math.round(weapon.ammo*1.5f))+Math.round(weapon.ammo*0.5f);
                    p.addToInventory(weapon.copy(ammo));
                }
                else{
                    p.addToInventory(weapon.copy());
                }
                //Destroy this drop
                Play.entitiesToRemove.add(this);
            }
        }
    }
}
