package io.calwe.topdownshooter.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;
import io.calwe.topdownshooter.Weapon;

public class InventorySlot extends Actor {
    public static final float TEXTURE_SIZE = 12.0f;
    public static final float SCALE = 5.0f;

    Texture unselectedTexture;
    Texture selectedTexture;

    public Weapon weaponInSlot;
    boolean selected;

    public InventorySlot() {
        unselectedTexture = new Texture("unselectedSlot.png");
        selectedTexture = new Texture("selectedSlot.png");

        setSize(unselectedTexture.getWidth(), unselectedTexture.getHeight());
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        if (selected) {
            batch.draw(selectedTexture, getX(), getY(), getOriginX(), getOriginY(),
                selectedTexture.getWidth(), selectedTexture.getHeight(), getScaleX(), getScaleY(), getRotation(), 0, 0,
                selectedTexture.getWidth(), selectedTexture.getWidth(), false, false);
        } else {
            batch.draw(unselectedTexture, getX() + 1, getY() + 1, getOriginX(), getOriginY(),
                unselectedTexture.getWidth(), unselectedTexture.getHeight(), getScaleX(), getScaleY(), getRotation(), 0, 0,
                unselectedTexture.getWidth(), unselectedTexture.getHeight(), false, false);
        }

        if (weaponInSlot != null) {
            float scalar = 0.4f;
            // aspect ratio for the weapon texture
            float aspectRatio = (float) weaponInSlot.sideOn.getHeight() / weaponInSlot.sideOn.getWidth();
            // use the aspect ratio to find the correct height for the rendered texture
            float height = getWidth() * aspectRatio;
            // y position to render the texture:
            //     getY(): bottom of the slot
            //     + (getHeight() / 2): add half the height of the inventory slot
            //     - (height / 2): subtract half the height of the texture, as the draw origin is from the bottom of the texture, not the middle
            float yCenter = getY() + ((getHeight()) / 2) - ((height) / 2);
            batch.draw(weaponInSlot.sideOn, getX(), yCenter, getOriginX(), getOriginY(),
                getWidth(), height, getScaleX(), getScaleY(), getRotation(), 0, 0,
                weaponInSlot.sideOn.getWidth(), weaponInSlot.sideOn.getHeight(), false, false);
        }
        batch.setColor(Color.WHITE);
    }
}
