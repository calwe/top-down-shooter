package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.Weapon;
import io.calwe.topdownshooter.entities.Player;

public class ExtraInventoryDrop extends EquipmentDrop{
    public ExtraInventoryDrop(Texture texture, String upgradeName, String upgradeDescription){
        super(texture, upgradeName, upgradeDescription);
    }

    @Override
    public void ApplyAffect(Player p){
        Weapon[] temp = p.inventory;
        Weapon[] newInventory = new Weapon[temp.length+1];
        System.arraycopy(temp, 0, newInventory, 0, temp.length);
        p.inventory = newInventory;
    }
}
