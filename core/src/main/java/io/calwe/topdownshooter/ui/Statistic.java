package io.calwe.topdownshooter.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;


public class Statistic extends Label {
    String name;

    public Statistic(String name, int value) {
        super(name + ": " + value, defaultStyle());
        setFontScale(0.5f);
        this.name = name;
    }

    public void setValue(int value) {
        setText(name + ": " + value);
    }

    // TODO: we need a better font, the default has weird kerning problems
    public static LabelStyle defaultStyle() {
        LabelStyle style = new LabelStyle();
        style.font = new BitmapFont();
        return style;
    }
}
