package io.calwe.topdownshooter;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Bullet;
import io.calwe.topdownshooter.screens.Play;

import java.util.Random;

public class Shotgun extends Weapon{
    int numberOfBullets = 5;

    public Shotgun(Texture texture, Texture sideOn, Texture bulletTexture, Sound fireSound, Sound emptySound, int ammo, int damage, float fireRate, int critChance, float inaccuracy, float recoil, float knockback, float bulletSpeed) {
        super(texture, sideOn, bulletTexture, fireSound, emptySound, ammo, damage, fireRate, critChance, inaccuracy, recoil, knockback, bulletSpeed, false);
    }


    public Shotgun(Shotgun weaponToCopy, int ammo){
        super(weaponToCopy.texture, weaponToCopy.sideOn, weaponToCopy.bulletTexture, weaponToCopy.fireSound, weaponToCopy.emptySound, ammo, weaponToCopy.damage, weaponToCopy.fireRate, weaponToCopy.critChance, weaponToCopy.inaccuracy, weaponToCopy.recoil, weaponToCopy.knockback, weaponToCopy.bulletSpeed, false);
    }

    @Override
    public Shotgun copy(){
        return new Shotgun(this, ammo);
    }

    @Override
    public Shotgun copy(int ammo){
        return new Shotgun(this, ammo);
    }

    @Override
    //This function returns true or false depending on whether the weapon was still cooling down
    //or it fired successfully
    public boolean fire(Vector2 gunPos, float bulletRotation, float damageMultiplier, float critMultiplier, int additionalCritChance, int ammoSaveChance){
        //Check if the weapon is ready to fire another shot
        if (timeLastFired + (long)(1000f/fireRate) < System.currentTimeMillis()){
            //Check if we still have ammunition left
            if (ammo > 0){
                // initialize a new instance of Random, which will be used for providing inaccuracy
                Random random = new Random();
                for (int i = 0; i < numberOfBullets; i++){
                    //Create an instance of bullet, give it the bullet texture, and its stats.
                    Bullet bullet = new Bullet(bulletTexture, gunPos, Math.round(damage*damageMultiplier), critChance + additionalCritChance, critMultiplier, knockback, pierces);
                    //Turn the bullet so it is facing towards the mouse
                    bullet.sprite.setRotation(bulletRotation);
                    // Move the bullet so it is emerging from the gun
                    bullet.pos.set(new Vector2(gunPos.x-(bullet.width/2f), gunPos.y-(bullet.height/2f)));
                    // add a random offset to direction to cause inaccuracy for the gun
                    bulletRotation += random.nextFloat(inaccuracy*2) - inaccuracy;
                    // apply the movement to the bullet
                    bullet.momentum.set(new Vector2(0, bulletSpeed).rotateDeg(bulletRotation));
                    // Add the bullet to the entitiesToAdd list so it can be added to the master entities list, and rendered and have its logic handled
                    Play.entitiesToAdd.add(bullet);
                }
                // Start the weapon cooldown
                timeLastFired = System.currentTimeMillis();
                if (random.nextInt(100) >= ammoSaveChance){
                    ammo--;
                }
                fireSound.play(0.02f);
                return true;
            }
            else{
                //If we have no ammo in the gun, play the gun empty sound
                timeLastFired = System.currentTimeMillis();
                emptySound.play(0.2f);
                return false;
            }

        }
        return false;
    }
}
