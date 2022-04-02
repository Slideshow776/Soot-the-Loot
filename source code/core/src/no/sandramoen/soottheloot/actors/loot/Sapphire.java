package no.sandramoen.soottheloot.actors.loot;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Sapphire extends Loot{
    public Sapphire(float x, float y, Stage stage, float toX, float toY) {
        super(x, y, stage, .15f, 25);
        loadImage("sapphire");
        setSize(11, 11);
        startBounce(toX, toY);
        setBoundaryPolygon(8);
    }
}
