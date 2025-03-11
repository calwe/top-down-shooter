package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

// superclass intended to be used by all moving objects/creatures
// This class does not have a constructor because it is abstract and will never be implemented
// But all its subclasses shoudl have constructors
public abstract class Entity {
    //The entity's current position in the world
    public Vector2 pos;
    // The entity's current momentum, which is added to their position each turn to move them
    protected Vector2 momentum;
    // How much the entity's momentum reduces each turn - how slippery they are.
    // It is a number between 1 and 0, by which their momentum is multiplied by -
    // 1 means they keep all momentum forever, like in space
    // 0 means they have no momentum and do not slide at all
    // numbers above one should not be used and would cause continous acceleration.
    protected float slide;

    // The width and height of the sprite
    protected int width;
    protected int height;
    protected Sprite sprite;

    public void input(OrthographicCamera camera) {

    }

    // Basic logic that most subclasses will override, it moves the entity according to its current momentum,
    // and reduces its momentum based on its slipperyness
    public void logic() {
        pos = new Vector2(pos.x + momentum.x, pos.y + momentum.y);
        //Reduce their momentum over time
        momentum.scl(slide);
        sprite.setPosition(pos.x, pos.y);
    }

    // Subclasses that have animations will need to override this class
    // Overwise you probably wont need to override this class
    // It draws this entity's sprite
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
