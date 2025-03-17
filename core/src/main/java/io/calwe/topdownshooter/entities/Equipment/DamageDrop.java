package io.calwe.topdownshooter.entities.Equipment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.calwe.topdownshooter.entities.Player;

public class DamageDrop extends EquipmentDrop{
    float damageMultiplier;
    public DamageDrop(Texture texture, String upgradeName, String upgradeDescription, float damageMultiplier){
        super(texture, upgradeName, upgradeDescription);
        this.damageMultiplier = damageMultiplier;
    }

    @Override
    public void ApplyAffect(Player p){
        p.damageMultiplier += damageMultiplier;
    }
    @Override
    public EquipmentDrop getCopy(){
        return new DamageDrop(texture, upgradeName, upgradeDescription, damageMultiplier);
    }
}
