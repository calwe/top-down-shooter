package io.calwe.topdownshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Utility {
    public static BitmapFont font;
    static {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("SUSE-Regular.ttf")
        );
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        parameter.mono = true;
        font = generator.generateFont(parameter);
    }
}
