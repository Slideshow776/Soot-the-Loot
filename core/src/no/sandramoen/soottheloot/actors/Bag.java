package no.sandramoen.soottheloot.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.soottheloot.utils.BaseActor;
import no.sandramoen.soottheloot.utils.BaseGame;

public class Bag extends BaseActor {
    private float originX = -1;
    private int loot = 0;
    private BagDragEffect effect;

    public float weight = .1f;
    public float draggers = 0f;

    public Bag(float x, float y, Stage stage) {
        super(x, y, stage);
        originX = getX();
        loadImage("bag");
        setSize(50f, 5f);
        setOrigin(Align.center);

        addAction(Actions.forever(Actions.sequence(
                Actions.scaleTo(1f, .95f, .5f),
                Actions.scaleTo(1f, 1.05f, .5f)
        )));

        effect = new BagDragEffect();
        effect.setPosition(0f, 0f);
        effect.setWidth(getWidth());
        effect.setScale(.025f);
        effect.start();
        addActor(effect);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (draggers == 0) {
            setX(getX() + 1f);
        } else if (weight <= draggers && getX() >= originX) {
            setX(getX() - .05f);
        } else if (weight > draggers) {
            setX(getX() + .05f);
        }
    }

    public void addLoot(float weight, int value) {
        BaseGame.coinLooted.play(BaseGame.soundVolume);
        setSize(getWidth(), getHeight() + .125f);
        this.weight += weight;
        loot++;
        effect.scaleBy(.001f);

        addFloatingValueText(value);
    }

    public int getLoot() {
        return loot;
    }

    private void addFloatingValueText(int value) {
        final BaseActor labelActor = new BaseActor(
                getX() + MathUtils.random(-10, 10),
                getY() + (getHeight() / 2) + MathUtils.random(-5, 5),
                getStage()
        );
        Label label = new Label("+" + value, BaseGame.label26Style);
        label.setFontScale(.125f);
        label.setColor(BaseGame.pink);
        labelActor.addActor(label);
        labelActor.addAction(Actions.sequence(
                Actions.parallel(
                        Actions.moveBy(10, 10, 2),
                        Actions.fadeOut(2)
                ),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        labelActor.remove();
                    }
                })
        ));
    }
}
