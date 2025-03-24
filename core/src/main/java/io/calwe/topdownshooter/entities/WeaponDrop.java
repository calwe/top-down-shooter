package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.Weapon;
import io.calwe.topdownshooter.screens.Play;

import java.util.*;

public class WeaponDrop extends Entity{
    Weapon weapon;
    //Should we randomly generate the ammunition or use that provided
    boolean randomlyGenerateAmmo;

    public WeaponDrop(Weapon weapon, Vector2 position){
        this.randomlyGenerateAmmo = false;
        this.layer = 0;
        this.weapon = weapon;
        this.hasSolidCollision = false;
        this.width = 32;
        this.height = 12;
        this.sprite = new Sprite(weapon.sideOn, width, height);
        sprite.setScale(0.6f);
        this.pos = new Vector2(position.x - (width*0.6f/2f), position.y-(height*0.6f/2f));
        this.momentum = new Vector2(0, 0);
        this.boundsHeightReduction = 0;
        this.boundsWidthReduction = 0;
        bounds.x = pos.x + boundsWidthReduction;
        bounds.y = pos.y + boundsHeightReduction;
        bounds.width = width - (boundsWidthReduction*2f);
        bounds.height = height - (boundsHeightReduction*2f);
    }
    WeaponDrop(Weapon weapon, Vector2 position, boolean randomlyGenerateAmmo){
        this.randomlyGenerateAmmo = randomlyGenerateAmmo;
        this.pos = position;
        this.weapon = weapon;
        this.hasSolidCollision = false;
        this.boundsHeightReduction = 0;
        this.boundsWidthReduction = 0;
        this.momentum = new Vector2(0, 0);
        bounds.x = pos.x + boundsWidthReduction;
        bounds.y = pos.y + boundsHeightReduction;
        bounds.width = width - (boundsWidthReduction*2f);
        bounds.height = height - (boundsHeightReduction*2f);
    }

    @Override
    public void logic() {
        //The weapon shouldn't move, but this also checks for collisions.
        momentum = new Vector2(0,0);
        sprite.setPosition(pos.x, pos.y);
        tryMove();
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
                    p.addToInventory(new Weapon(weapon, ammo));
                }
                else{
                    p.addToInventory(new Weapon(weapon, weapon.ammo));
                }
                //Destroy this drop
                Play.entitiesToRemove.add(this);
            }
        }
    }
}
