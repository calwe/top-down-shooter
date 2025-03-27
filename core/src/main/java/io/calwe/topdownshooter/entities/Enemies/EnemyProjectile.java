package io.calwe.topdownshooter.entities.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Entity;
import io.calwe.topdownshooter.entities.Obstacle;
import io.calwe.topdownshooter.entities.Player;
import io.calwe.topdownshooter.screens.Play;

import java.util.Random;

public class EnemyProjectile extends Entity {
    final int damage;
    final float knockback;
    final float lifeTime = 5;
    float timer = 0;

    public EnemyProjectile(Texture texture, Vector2 startPos, int damage, float knockback){
        this.pos = startPos;
        this.momentum = new Vector2(0, 0);
        this.width = 4;
        this.height = 4;
        this.sprite = new Sprite(texture, width, height);
        this.damage = damage;
        this.knockback = knockback;
        this.hasSolidCollision = false;
        this.boundsHeightReduction = 0;
        this.boundsWidthReduction = 0;
        //Generate the bullet's collider bounds
        bounds.x = pos.x + boundsWidthReduction;
        bounds.y = pos.y + boundsHeightReduction;
        bounds.width = width - (boundsWidthReduction*2f);
        bounds.height = height - (boundsHeightReduction*2f);
    }

    @Override
    public void logic() {
        //move the bullet based on its momentum
        tryMove();
        sprite.setPosition(pos.x, pos.y);
        //destroy the bullet after a certain amount of time has passed, to prevent it from existing forever
        // and slowing down the game
        timer += Gdx.graphics.getDeltaTime();
        if (timer > lifeTime){
            Play.entitiesToRemove.add(this);
        }
    }

    //Run when the bullet hits something
    @Override
    public void OnEntityCollision(Entity e){
        //If the thing we hit is a player
        if (e instanceof Player){
            Player player = (Player)e;
            Random random = new Random();
            //Deal the damage to the player
            player.takeDamage(damage);
            // calculate and apply knockback to the player
            Vector2 knockbackDirection = new Vector2(player.pos.x-pos.x,player.pos.y-pos.y);
            knockbackDirection.nor();
            knockbackDirection.scl(knockback);
            player.applyKnockback(knockbackDirection);
            //Destroy this bullet
            Play.entitiesToRemove.add(this);
        }
        if (e instanceof Obstacle){
            //Destroy this bullet
            Play.entitiesToRemove.add(this);
        }
    }
}


