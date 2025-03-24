package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.screens.Play;

import java.util.Random;

public class ExplodingEnemy extends Enemy {

    float projectileSpeed;
    Texture projectileTexture;

    // The constructor - initialize all the variables
    public ExplodingEnemy(Texture texture, Animation<TextureRegion> enemyWalkAnimation, Sound hurtSound, Vector2 startPos, Player target, Texture[] damageParticles, Texture projectileTexture, float projectileSpeed) {
        super(texture,enemyWalkAnimation,hurtSound,startPos,target,damageParticles);
        this.projectileSpeed = projectileSpeed;
        this.projectileTexture = projectileTexture;
    }



    //executed when the enemy runs out of health
    @Override
    protected void die(){
        //Add to the player's score
        Play.score += 100;
        Vector2[] directions = new Vector2[]{
            new Vector2(0, 1),
            new Vector2(1, 1),
            new Vector2(1, 0),
            new Vector2(-1, 1),
            new Vector2(1, -1),
            new Vector2(-1, 0),
            new Vector2(0, -1),
            new Vector2(-1, -1),
        };
        for (Vector2 direction : directions) {
            EnemyProjectile p = new EnemyProjectile(projectileTexture, new Vector2(pos.x + (width/2f), pos.y + (height/2f)), damage, knockback);
            //Turn the bullet so it is facing towards the target
            float angleToLook = (float)Math.atan2(direction.x-(pos.x), direction.y-(pos.y));
            float rotation = angleToLook*-180f/(float)Math.PI;
            p.sprite.setRotation(rotation);
            // apply the movement to the projectile
            direction.nor();
            direction.scl(projectileSpeed);
            p.momentum.add(direction);
            // Add the projectile to the entitiesToAdd list so it can be added to the master entities list, and rendered and have its logic handled
            Play.entitiesToAdd.add(p);
        }
        //Remove this entity from the world
        Play.entitiesToRemove.add(this);
    }

}
