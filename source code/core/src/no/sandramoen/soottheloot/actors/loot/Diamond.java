package no.sandramoen.soottheloot.actors.loot;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Diamond extends Loot{
    public Diamond(float x, float y, Stage stage, float toX, float toY) {
        super(x, y, stage, .25f, 100);
        loadImage("diamond");
        setSize(15, 15);
        startBounce(toX, toY);
        setBoundaryPolygon(8);
    }
}
