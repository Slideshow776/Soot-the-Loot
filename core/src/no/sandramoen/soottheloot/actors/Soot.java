package no.sandramoen.soottheloot.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.soottheloot.utils.BaseActor;
import no.sandramoen.soottheloot.utils.BaseGame;

public class Soot extends BaseActor {
    private float movementSpeed = 18f;

    public Soot(float x, float y, Stage stage) {
        super(x, y, stage);

        Array<TextureAtlas.AtlasRegion> images = new Array();
        images.add(BaseGame.textureAtlas.findRegion("soot/running1"));
        images.add(BaseGame.textureAtlas.findRegion("soot/running2"));

        Animation running = new Animation(.1f, images, Animation.PlayMode.LOOP);
        images.clear();
        setAnimation(running);

        setSize(8, 8);
        setBoundaryPolygon(8);
    }
}