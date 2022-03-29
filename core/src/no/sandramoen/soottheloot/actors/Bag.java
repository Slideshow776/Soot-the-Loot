package no.sandramoen.soottheloot.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.soottheloot.utils.BaseActor;

public class Bag extends BaseActor {

    public Bag(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("bag");
        setSize(50f, 10f);
        setOrigin(Align.center);
        addAction(Actions.forever(Actions.sequence(
                Actions.scaleTo(1f, .95f, .5f),
                Actions.scaleTo(1f, 1.05f, .5f)
        )));

        BagDragEffect effect = new BagDragEffect();
        effect.setPosition(0f, 0f);
        effect.setScale(.15f);
        effect.start();
        addActor(effect);
    }

    public void incrementSize() {
        setSize(50f, getHeight() + .5f);
    }
}
