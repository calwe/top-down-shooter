package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.screens.Play;

import java.util.Random;

public class Weapon{
    int damage;
    float fireRate;
    int critChance;
    float inaccuracy;
    float recoil;
    float knockback;
    float bulletSpeed;

    Texture texture;
    Texture firingTexture;
    Texture bulletTexture;
    float weaponWidth = 20;
    float weaponHeight = 5;

    long timeLastFired = 0;

    public Weapon(Texture texture, Texture firingTexture, Texture bulletTexture, int damage, float fireRate, int critChance, float inaccuracy, float recoil, float knockback, float bulletSpeed){
        this.texture = texture;
        this.firingTexture = firingTexture;
        this.bulletTexture = bulletTexture;
        this.bulletSpeed = bulletSpeed;
        this.damage = damage;
        this.fireRate = fireRate;
        this.critChance = critChance;
        this.inaccuracy = inaccuracy;
        this.recoil = recoil;
        this.knockback = knockback;
    }

    public boolean fire(Vector2 playerPos, Vector2 direction, float bulletRotation){
        if (timeLastFired + (long)(1000f/fireRate) < System.currentTimeMillis()){
            Random random = new Random();
            Bullet bullet = new Bullet(bulletTexture, playerPos, damage, critChance, knockback);
            bullet.sprite.setRotation(bulletRotation);
            bullet.pos.set(new Vector2(playerPos.x-(bullet.width/2f), playerPos.y-(bullet.height/2f)));
            direction.nor();
            direction.set(direction.x + ((random.nextFloat(inaccuracy*2f)-inaccuracy)/300f), direction.y+ ((random.nextFloat(inaccuracy*2f)-inaccuracy)/300f));
            direction.scl(bulletSpeed);
            bullet.momentum.set(direction);
            timeLastFired = System.currentTimeMillis();
            Play.entitiesToAdd.add(bullet);
            return true;
        }

        return false;
    }
}

