package io.calwe.topdownshooter.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import io.calwe.topdownshooter.screens.Play;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;

// superclass intended to be used by all moving objects/creatures
// This class does not have a constructor because it is abstract and will never be implemented
// But all its subclasses shoudl have constructors
public abstract class Entity {
    //The entity's current position in the world
    public Vector2 pos;
    // The entity's current momentum, which is added to their position each turn to move them
    public Vector2 momentum;
    // How much the entity's momentum reduces each turn - how slippery they are.
    // It is a number between 1 and 0, by which their momentum is multiplied by -
    // 1 means they keep all momentum forever, like in space
    // 0 means they have no momentum and do not slide at all
    // numbers above one should not be used and would cause continous acceleration.
    protected float slide;

    // The width and height of the sprite
    public int width;
    public int height;
    public Sprite sprite;
    //The collision bounds for this entity
    public Rectangle bounds = new Rectangle();
    //Whether or not the entity is solid and should be unable to move through other entities
    public boolean hasSolidCollision = true;

    //How much to reduce the size of the collider from the size of the sprite
    public float boundsHeightReduction;
    public float boundsWidthReduction;

    public void input(OrthographicCamera camera) {

    }

    //The priority for rendering this entity - higher numbers are rendered on top.
    public int layer = 10;

    // Basic logic that most subclasses will override, it moves the entity according to its current momentum,
    // and reduces its momentum based on its slipperyness
    public void logic() {
        tryMove();
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


    //This handles collisions while moving
    protected void tryMove () {
        //Calculate the current collider bounds
        bounds.x = pos.x;
        bounds.y = pos.y;
        bounds.width = width;
        bounds.height = height;
        //get the location we are going to move to in the x direction
        bounds.x += momentum.x;
        //Get all the other objects we could collide with
        Dictionary<Rectangle, Entity> collideableRects = Play.getOtherColliderRects(this);
        Object[] rects = Collections.list(collideableRects.keys()).toArray();
        List<Entity> entityCollisions = new ArrayList<>();
        // Iterate through each other object we could collide with
        for (int i = 0; i < rects.length; i++) {
            Rectangle rect = (Rectangle)rects[i];
            //If we collide with an entity
            if (bounds.overlaps(rect)) {
                //Add them to the list of entities we collided with
                if (!entityCollisions.contains(collideableRects.get(rect))) {
                    entityCollisions.add(collideableRects.get(rect));
                }
                //If both objects have solid colliders
                if (collideableRects.get(rect).hasSolidCollision && hasSolidCollision){
                    //prevent us from moving through us
                    if (momentum.x < 0)
                        bounds.x = rect.x + rect.width + 0.1f;
                    else
                        bounds.x = rect.x - bounds.width - 0.1f;
                    momentum.x = 0;
                }
            }
        }
        //get the location we are going to move to in the y direction
        bounds.y += momentum.y;
        // Iterate through each other object we could collide with
        for (int i = 0; i < rects.length; i++) {
            Rectangle rect = (Rectangle)rects[i];
            //If we collide with an entity
            if (bounds.overlaps(rect)) {
                //Add them to the list of entities we collided with
                if (!entityCollisions.contains(collideableRects.get(rect))) {
                    entityCollisions.add(collideableRects.get(rect));
                }
                //If both objects have solid colliders
                if (collideableRects.get(rect).hasSolidCollision && hasSolidCollision){
                    //prevent us from moving through us
                    if (momentum.y < 0) {
                        bounds.y = rect.y + rect.height + 0.1f;
                    } else
                        bounds.y = rect.y - bounds.height - 0.1f;
                    momentum.y = 0;
                }
            }
        }
        //Call OnEntityCollision for each entity we collided with
        for (Entity e : entityCollisions) {
            OnEntityCollision(e);
        }
        //Move us based on the collisions
        if (this.hasSolidCollision) {
            pos.x = bounds.x;
            pos.y = bounds.y;
        }
        else{
            pos = new Vector2(pos.x + momentum.x, pos.y + momentum.y);
        }
    }


    public void OnEntityCollision(Entity e){

    }

}
