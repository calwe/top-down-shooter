package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import io.calwe.topdownshooter.entities.Player;

public class SaveAmmoDrop extends EquipmentDrop{
    final int ammoSaveChance;
    public SaveAmmoDrop(Texture texture, String upgradeName, String upgradeDescription, int ammoSaveChance) {
        super(texture, upgradeName, upgradeDescription);
        this.ammoSaveChance = ammoSaveChance;
    }

    @Override
    public void ApplyAffect(Player p){
        //Give the player a chance not to consume ammunition while firing
        p.saveAmmoChance = ammoSaveChance;
    }
    @Override
    public EquipmentDrop getCopy(){
        return new SaveAmmoDrop(texture, upgradeName, upgradeDescription, ammoSaveChance);
    }
}
