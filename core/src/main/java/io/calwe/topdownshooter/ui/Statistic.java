package io.calwe.topdownshooter.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import io.calwe.topdownshooter.Utility;


public class Statistic extends Label {
    String name;

    public Statistic(String name, int value) {
        super(name + ": " + value, new LabelStyle(Utility.font, Color.WHITE));
        // reset the font scale to 1. this is adjusted by other classes using the font.
        // ideally, each class using the font should take a copy of the font rather than using a reference,
        // but I am unsure how this works in java (and if this would break anything)
        setFontScale(1f);
        this.name = name;
    }

    public void setValue(int value) {
        setText(name + ": " + value);
    }
}
