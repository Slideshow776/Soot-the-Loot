package no.sandramoen.soottheloot.actors.loot;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Coin extends Loot{
    public Coin(float x, float y, Stage stage, float toX, float toY) {
        super(x, y, stage, .05f, 1);
        loadImage("coin");
        setSize(10, 10);
        startBounce(toX, toY);
        setBoundaryPolygon(8);
    }
}
