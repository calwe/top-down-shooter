package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Player;

public class CritChanceDrop extends EquipmentDrop{
    int additionalCritChance;
    public CritChanceDrop(Texture texture, String upgradeName, String upgradeDescription, int additionalCritChance) {
        super(texture,  upgradeName, upgradeDescription);
        this.additionalCritChance = additionalCritChance;
    }

    @Override
    public void ApplyAffect(Player p){
        p.additionalCritChance += this.additionalCritChance;
    }
}
