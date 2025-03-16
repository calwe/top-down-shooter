package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Player;

public class SaveAmmoDrop extends EquipmentDrop{
    int ammoSaveChance;
    public SaveAmmoDrop(Texture texture, String upgradeName, String upgradeDescription, int ammoSaveChance) {
        super(texture, upgradeName, upgradeDescription);
        this.ammoSaveChance = ammoSaveChance;
    }

    @Override
    public void ApplyAffect(Player p){
        p.saveAmmoChance = ammoSaveChance;
    }
}
