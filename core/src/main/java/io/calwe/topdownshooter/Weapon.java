package io.calwe.topdownshooter;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Bullet;
import io.calwe.topdownshooter.screens.Play;

import java.util.Random;

public class Weapon{
    int damage;
    // Number of bullets that can be fired per second
    float fireRate;
    int critChance;
    float inaccuracy;
    public float recoil;
    float knockback;
    float bulletSpeed;
    public int ammo;

    public Texture texture;
    public Texture sideOn;
    public Texture firingTexture;
    Texture bulletTexture;

    Sound fireSound;
    Sound emptySound;

    long timeLastFired = 0;

    public Weapon(Texture texture, Texture firingTexture, Texture sideOn, Texture bulletTexture, Sound fireSound, Sound emptySound, int ammo, int damage, float fireRate, int critChance, float inaccuracy, float recoil, float knockback, float bulletSpeed){
        this.texture = texture;
        this.firingTexture = firingTexture;
        this.sideOn = sideOn;
        this.bulletTexture = bulletTexture;
        this.bulletSpeed = bulletSpeed;
        this.ammo = ammo;
        this.damage = damage;
        this.fireRate = fireRate;
        this.critChance = critChance;
        this.inaccuracy = inaccuracy;
        this.recoil = recoil;
        this.knockback = knockback;
        this.fireSound = fireSound;
        this.emptySound = emptySound;
    }

    public Weapon(Weapon weaponToCopy, int ammo){
        this.texture = weaponToCopy.texture;
        this.firingTexture = weaponToCopy.firingTexture;
        this.bulletTexture = weaponToCopy.bulletTexture;
        this.sideOn = weaponToCopy.sideOn;
        this.bulletSpeed = weaponToCopy.bulletSpeed;
        this.ammo = ammo;
        this.damage = weaponToCopy.damage;
        this.fireRate = weaponToCopy.fireRate;
        this.critChance = weaponToCopy.critChance;
        this.inaccuracy = weaponToCopy.inaccuracy;
        this.recoil = weaponToCopy.recoil;
        this.knockback = weaponToCopy.knockback;
        this.fireSound = weaponToCopy.fireSound;
        this.emptySound = weaponToCopy.emptySound;
    }

    //This function returns true or false depending on whether the weapon was still cooling down
    //or it fired successfully
    public boolean fire(Vector2 gunPos, float bulletRotation){
        //Check if the weapon is ready to fire another shot
        if (timeLastFired + (long)(1000f/fireRate) < System.currentTimeMillis()){
            if (ammo > 0){
                //Create an instance of bullet, give it the bullet texture, and its stats.
                Bullet bullet = new Bullet(bulletTexture, gunPos, damage, critChance, knockback);
                //Turn the bullet so it is facing towards the mouse
                bullet.sprite.setRotation(bulletRotation);
                // Move the bullet so it is emerging from the gun
                bullet.pos.set(new Vector2(gunPos.x-(bullet.width/2f), gunPos.y-(bullet.height/2f)));
                // initialize a new instance of Random, which will be used for providing inaccuracy
                Random random = new Random();
                // add a random offset to direction to cause inaccuracy for the gun
                bulletRotation += random.nextFloat(inaccuracy*2) - inaccuracy;
                // apply the movement to the bullet
                bullet.momentum.set(new Vector2(0, bulletSpeed).rotateDeg(bulletRotation));
                // Start the weapon cooldown
                timeLastFired = System.currentTimeMillis();
                // Add the bullet to the entitiesToAdd list so it can be added to the master entities list, and rendered and have its logic handled
                Play.entitiesToAdd.add(bullet);
                ammo--;
                fireSound.play(0.02f);
                return true;
            }
            else{
                timeLastFired = System.currentTimeMillis();
                emptySound.play(0.2f);
                return false;
            }

        }
        return false;
    }
}

