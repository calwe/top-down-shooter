package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.screens.Play;

import java.util.Random;

public class Weapon{
    int damage;
    // Number of bullets that can be fired per second
    float fireRate;
    int critChance;
    float inaccuracy;
    float recoil;
    float knockback;
    float bulletSpeed;

    Texture texture;
    Texture firingTexture;
    Texture bulletTexture;

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

    //This function returns true or false depending on whether the weapon was still cooling down
    //or it fired successfully
    public boolean fire(Vector2 gunPos, Vector2 direction, float bulletRotation){
        //Check if the weapon is ready to fire another shot
        if (timeLastFired + (long)(1000f/fireRate) < System.currentTimeMillis()){
            //Create an instance of bullet, give it the bullet texture, and its stats.
            Bullet bullet = new Bullet(bulletTexture, gunPos, damage, critChance, knockback);
            //Turn the bullet so it is facing towards the mouse
            bullet.sprite.setRotation(bulletRotation);
            // Move the bullet so it is emerging from the gun
            bullet.pos.set(new Vector2(gunPos.x-(bullet.width/2f), gunPos.y-(bullet.height/2f)));
            // initialize a new instance of Random, which will be used for providing inaccuracy
            Random random = new Random();
            direction.nor();
            // add a random offset to direction to cause inaccuracy for the gun
            direction.set(direction.x + ((random.nextFloat(inaccuracy*2f)-inaccuracy)/300f), direction.y+ ((random.nextFloat(inaccuracy*2f)-inaccuracy)/300f));
            // multiply the direction by the bullet speed to get the bullet's movement
            direction.scl(bulletSpeed);
            // apply the movement to the bullet
            bullet.momentum.set(direction);
            // Start the weapon cooldown
            timeLastFired = System.currentTimeMillis();
            // Add the bullet to the entitiesToAdd list so it can be added to the master entities list, and rendered and have its logic handled
            Play.entitiesToAdd.add(bullet);
            return true;
        }

        return false;
    }
}

