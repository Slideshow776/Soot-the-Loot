package no.sandramoen.soottheloot.actors.loot;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Ruby extends Loot{
    public Ruby(float x, float y, Stage stage, float toX, float toY) {
        super(x, y, stage, .1f, 50);
        loadImage("ruby");
        setSize(8, 8);
        startBounce(toX, toY);
        setBoundaryPolygon(8);
    }
}
