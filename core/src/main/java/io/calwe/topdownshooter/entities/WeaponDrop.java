package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.screens.Play;

import java.util.*;

public class WeaponDrop extends Entity{
    Weapon weapon;
    boolean randomlyGenerateAmmo;

    public WeaponDrop(Weapon weapon, Vector2 position){
        this.randomlyGenerateAmmo = false;

        this.weapon = weapon;
        this.hasSolidCollision = false;
        this.width = 32;
        this.height = 12;
        this.sprite = new Sprite(weapon.sideOn, width, height);
        sprite.setScale(0.6f);
        this.pos = new Vector2(position.x - (width*0.6f/2f), position.y-(height*0.6f/2f));
        this.slide = 0;
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
        momentum = new Vector2(0,0);
        sprite.setPosition(pos.x, pos.y);
        tryMove();
    }

    @Override
    public void OnEntityCollision(Entity e){
        if (e instanceof Player){
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                Player p = (Player)e;
                Random random = new Random();
                if (randomlyGenerateAmmo){
                    int ammo = random.nextInt(Math.round(weapon.ammo*1.5f))+Math.round(weapon.ammo*0.5f);
                    p.addToInventory(new Weapon(weapon, ammo));
                }
                else{
                    p.addToInventory(new Weapon(weapon, weapon.ammo));
                }
                Play.entitiesToRemove.add(this);
            }
        }
    }
}
