package no.sandramoen.soottheloot.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.soottheloot.utils.BaseActor;

public class Bag extends BaseActor {
    private float originX = -1;

    public float weight = .1f;
    public float draggers = 0f;

    public Bag(float x, float y, Stage stage) {
        super(x, y, stage);
        originX = getX();
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

        setDebug(true);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (draggers == 0) {
            setX(getX() + 1f);
        }
        else if (weight <= draggers && getX() >= originX) {
            setX(getX() - .05f);
        }
        else if (weight > draggers) {
            setX(getX() + .05f);
        }
    }

    public void addLoot(float weight) {
        setSize(getWidth(), getHeight() + .5f);
        this.weight += weight;
    }
}
