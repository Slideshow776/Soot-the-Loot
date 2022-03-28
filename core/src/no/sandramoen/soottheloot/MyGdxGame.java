package no.sandramoen.soottheloot;

import com.badlogic.gdx.math.MathUtils;

import no.sandramoen.soottheloot.screens.LevelScreen;
import no.sandramoen.soottheloot.utils.BaseGame;

public class MyGdxGame extends BaseGame {

    @Override
    public void create() {
        super.create();
        setActiveScreen(new LevelScreen());
    }
}
