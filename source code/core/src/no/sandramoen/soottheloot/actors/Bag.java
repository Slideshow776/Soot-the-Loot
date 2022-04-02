package no.sandramoen.soottheloot.actors;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.soottheloot.utils.BaseActor;
import no.sandramoen.soottheloot.utils.BaseGame;

public class Bag extends BaseActor {
    private int lootValue = 0;
    private BagDragEffect effect;
    private Label warningLabel;
    private float time = 0;

    public float weight = .1f;
    public float draggers = 0f;
    public boolean isLoosing = false;
    public boolean inPlay = false;

    public Bag(float x, float y, Stage stage) {
        super(x, y, stage);
        loadImage("bag");
        setSize(50f, 5f);
        setOrigin(Align.bottom);

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

        warningLabel = new Label("Don't loose\n the bag!", BaseGame.label26Style);
        warningLabel.setColor(new Color(0.647f, 0.188f, 0.188f, 1f));
        warningLabel.setFontScale(.2f);
        warningLabel.setWrap(true);
        warningLabel.setPosition(-20f, -5);
        warningLabel.setVisible(isLoosing);
        warningLabel.addAction(Actions.fadeOut(0f));
        warningLabel.addAction(Actions.fadeIn(5f));
        addActor(warningLabel);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!inPlay) return;

        if (time < 2f)
            time += delta;
        if (time >= 2) {
            if (draggers <= 0) {
                setX(getX() + 1.5f);
                isLoosing = true;
            } else if (weight <= draggers && getX() >= 70) {
                setX(getX() - .05f);
                isLoosing = false;
            } else if (weight > draggers) {
                setX(getX() + .05f);
                isLoosing = true;
            }

            if (isLoosing) {
                warningLabel.setVisible(true);
            } else {
                warningLabel.setVisible(false);
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        warningLabel.setVisible(false);
    }

    public void addLoot(float weight, int value, Sound sound) {
        sound.play(BaseGame.soundVolume);
        setSize(getWidth(), getHeight() + .125f);
        this.weight += weight;
        lootValue += value;
        effect.scaleBy(.001f);
        addFloatingValueText(value);
    }

    public int getLootValue() {
        return lootValue;
    }

    private void addFloatingValueText(int value) {
        final BaseActor labelActor = new BaseActor(
                getX() + MathUtils.random(-10, 10),
                getY() + (getHeight() / 2) + MathUtils.random(-5, 5),
                getStage()
        );
        Label label = new Label("+" + value, BaseGame.label26Style);
        label.setFontScale(.125f);
        label.setColor(BaseGame.lightBlue);
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
