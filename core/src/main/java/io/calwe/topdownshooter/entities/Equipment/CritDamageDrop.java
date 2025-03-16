package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Player;

public class CritDamageDrop extends EquipmentDrop{
    float additionalCritMultiplier;
    public CritDamageDrop(Texture texture, String upgradeName, String upgradeDescription, float additionalCritMultiplier) {
        super(texture, upgradeName, upgradeDescription);
        this.additionalCritMultiplier = additionalCritMultiplier;
    }

    @Override
    public void ApplyAffect(Player p){
        p.critMultiplier += additionalCritMultiplier;
    }
}
