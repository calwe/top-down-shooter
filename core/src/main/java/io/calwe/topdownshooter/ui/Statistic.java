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
        setFontScale(1f);
        this.name = name;
    }

    public void setValue(int value) {
        setText(name + ": " + value);
    }
}
