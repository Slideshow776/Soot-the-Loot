package no.sandramoen.soottheloot.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.soottheloot.utils.BaseActor;
import no.sandramoen.soottheloot.utils.BaseGame;

public class Coin extends BaseActor {

    private float fallingSpeed = 1f;
    private float movementSpeed = .5f;
    private BaseActor shadow;

    public boolean remove = false;
    public float weight = .05f;
    public int value = 1;

    public Coin(float x, float y, Stage stage, float toX, float toY) {
        super(x, y, stage);
        loadImage("coin");
        setSize(10, 10);
        setBoundaryPolygon(8);

        Vector2 bounceTo = new Vector2(toX, toY);
        Vector2 bounceAway = new Vector2(MathUtils.random(110, 200), MathUtils.random(10, 200));
        float bounceToDuration = MathUtils.random(1.5f, 3f);
        float bounceAwayDuration = MathUtils.random(1.5f, 3f);

        setOrigin(Align.center);
        addAction(Actions.sequence(
                Actions.parallel( // bounce to
                        Actions.moveTo(bounceTo.x, bounceTo.y, bounceToDuration, Interpolation.slowFast),
                        Actions.rotateBy(MathUtils.random(0, 360), bounceToDuration)
                ),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        BaseGame.coinDropSound.play(BaseGame.soundVolume, MathUtils.random(.5f, 1.5f), 0);
                    }
                }),
                Actions.parallel( // bounce away
                        Actions.moveTo(bounceAway.x, bounceAway.y, bounceAwayDuration, Interpolation.fastSlow),
                        Actions.rotateBy(MathUtils.random(-720, 720), bounceAwayDuration),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                isCollisionEnabled = false;
                            }
                        })
                ),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        remove = true;
                    }
                })
        ));

        shadow = new BaseActor(bounceTo.x + getWidth() / 2, bounceTo.y, stage);
        shadow.loadImage("whitePixel");
        shadow.setSize(1f, .5f);
        shadow.setColor(Color.BLACK);
        shadow.setOrigin(Align.center);
        shadow.setOpacity(0f);
        shadow.addAction(Actions.sequence(
                Actions.parallel( // bounce to
                        Actions.scaleBy(4, 2, bounceToDuration),
                        Actions.fadeIn(bounceToDuration)
                ),
                Actions.parallel( // bounce away
                        Actions.scaleTo(0, 0, bounceAwayDuration),
                        Actions.moveTo(bounceAway.x, shadow.getY(), bounceAwayDuration),
                        Actions.fadeOut(bounceAwayDuration * .5f)
                )
        ));
    }

    @Override
    public boolean remove() {
        shadow.remove();
        return super.remove();
    }

    public void caught() {
        clearActions();
        isCollisionEnabled = false;
    }
}
