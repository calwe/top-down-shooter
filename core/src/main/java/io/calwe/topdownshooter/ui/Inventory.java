package io.calwe.topdownshooter.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Inventory extends Table {
    public static final int SLOTS = 3;

    public InputAdapter weaponSwitcher;

    public InventorySlot[] slots;

    private int currentInventorySlot = 0;

    public Inventory() {
        slots = new InventorySlot[3];
        // create the input handler, which is used by the 'InputMultiplexer' in the HUD class
        weaponSwitcher = new InputAdapter() {
            // select the correct slot based on which number key is hit
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.NUM_1:
                        selectSlot(0);
                        return true;
                    case Input.Keys.NUM_2:
                        selectSlot(1);
                        return true;
                    case Input.Keys.NUM_3:
                        selectSlot(2);
                        return true;
                    default:
                        return false;
                }
            }
        };
        // create each inventory slot
        for (int i = 0; i < SLOTS; i++) {
            slots[i] = new InventorySlot();
            add(slots[i]);
        }
        slots[0].setSelected(true);
    }

    public void selectSlot(int slot) {
        // deselect the old slot
        slots[currentInventorySlot].setSelected(false);
        // select the new slot
        slots[slot].setSelected(true);
        // store the selected slot index in a variable
        currentInventorySlot = slot;
    }
}
