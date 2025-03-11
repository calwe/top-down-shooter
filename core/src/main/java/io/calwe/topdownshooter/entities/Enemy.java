package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Entity {

    // The maximum amount of health the player can have, and how much health they start with
    int maxHealth;
    // The amount of health the player currently has remaining
    int health;
    // The amount of damage done to the player on contact
    int damage;
    //how fast the player move
    float movementSpeed;

    // The player's movement animation
    Animation<TextureRegion> enemyWalkAnimation;
    // A float that stores the time since the animation started playing so we can figure out what frame
    // of the animation to display
    float elapsedTime = 0.0f;

    Texture enemyTexture;

    Player target;

    // The constructor - initialize all the variables
    public Enemy(Texture texture, Animation<TextureRegion> enemyWalkAnimation, Vector2 startPos, Player target) {
        this.maxHealth = 100;
        this.damage = 8;
        this.pos = startPos;
        this.momentum = new Vector2(0, 0);
        this.movementSpeed = 5f;
        this.enemyWalkAnimation = enemyWalkAnimation;
        this.slide = 0.85f;
        this.width = 12;
        this.height = 16;
        this.sprite = new Sprite(texture, width, height);
        this.health = maxHealth;
        this.target = target;
        this.enemyTexture = texture;
    }

    //This overrides Entity's logic method
    @Override
    public void logic(){
        Vector2 movementToPlayer = new Vector2(target.pos.x-pos.x,target.pos.y-pos.y);
        movementToPlayer.nor();
        movementToPlayer.scl(movementSpeed*Gdx.graphics.getDeltaTime());
        momentum.add(movementToPlayer);

        pos = new Vector2(pos.x + momentum.x, pos.y + momentum.y);
        //Reduce their momentum over time
        momentum.scl(slide);

        float angleToLook = (float)Math.atan2(target.pos.x-(pos.x), target.pos.y-(pos.y));
        // convert the angle given from radians to degrees, and rotate the enemy to look in that direction
        sprite.setRotation(angleToLook*-180f/(float)Math.PI);

        // Add an offset to the sprite to account for the fact that the sprite is not centered in its image.
        Vector2 spriteOffset = new Vector2(0, 2).rotate(angleToLook*-180f/(float)Math.PI);
        spriteOffset.add(pos);
        sprite.setPosition(spriteOffset.x, spriteOffset.y);

    }

    // This overrides entity's draw method so we can have animation
    @Override
    public void draw(SpriteBatch batch) {
        // calculated based on how much time has passed since we started playing the animation what frame of the
        // animation we should be showing
        TextureRegion currentFrame = enemyWalkAnimation.getKeyFrame(elapsedTime, true);
        // Set the animation's current frame to the sprite's image
        sprite.setRegion(currentFrame);

        // Render the sprite
        sprite.draw(batch);

        sprite.setRegion(enemyTexture);
        sprite.draw(batch);
        // Update how much time has passed since we started showing the animation
        elapsedTime += Gdx.graphics.getDeltaTime();
    }
}
