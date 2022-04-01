package no.sandramoen.soottheloot.actors;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.soottheloot.actors.loot.Loot;
import no.sandramoen.soottheloot.utils.BaseActor;
import no.sandramoen.soottheloot.utils.BaseGame;

public class Soot extends BaseActor {
    private float movementSpeed = 18f;
    private Animation<TextureRegion> runningAnimation;
    private Animation<TextureRegion> carryingAnimation;
    private Animation<TextureRegion> draggingAnimation;
    private Array<Loot> loot = new Array();
    public float toX = -1;
    public float toY = -1;
    public float pitch = MathUtils.random(.65f, 1.5f);
    public boolean hasPickedUp = false;

    public final int maxCarry = 1;
    public int carrying = 0;
    public final int id = MathUtils.random(0, 9999);
    public boolean isDragging = true;
    public BaseActor crown;

    public Soot(float x, float y, Stage stage) {
        super(x, y, stage);
        setOrigin(Align.center);
        toX = getX();
        toY = getY();

        int randomPrefix = MathUtils.random(0, 3);

        Array<TextureAtlas.AtlasRegion> images = new Array();
        images.add(BaseGame.textureAtlas.findRegion("soot/" + randomPrefix + "running1"));
        images.add(BaseGame.textureAtlas.findRegion("soot/" + randomPrefix + "running2"));
        runningAnimation = new Animation(.1f, images, Animation.PlayMode.LOOP);
        images.clear();

        images.add(BaseGame.textureAtlas.findRegion("soot/" + randomPrefix + "carrying1"));
        images.add(BaseGame.textureAtlas.findRegion("soot/" + randomPrefix + "carrying2"));
        carryingAnimation = new Animation(.1f, images, Animation.PlayMode.LOOP);
        images.clear();

        images.add(BaseGame.textureAtlas.findRegion("soot/" + randomPrefix + "dragging1"));
        images.add(BaseGame.textureAtlas.findRegion("soot/" + randomPrefix + "dragging2"));
        draggingAnimation = new Animation(.1f, images, Animation.PlayMode.LOOP);
        images.clear();

        setAnimation(draggingAnimation);

        setSize(8, 8);
        setBoundaryPolygon(8);

        crown = new BaseActor(0, 0, stage);
        crown.loadImage("soot/crown");
        crown.setSize(getWidth(), getHeight());
        crown.setOrigin(Align.center);
        crown.addAction(Actions.forever(Actions.sequence(
                Actions.rotateBy(10, .125f),
                Actions.rotateBy(-20, .25f),
                Actions.rotateBy(10, .125f)
        )));
        crown.setVisible(false);
        addActor(crown);

        addAction(Actions.sequence(
                Actions.delay(MathUtils.random(0f, .5f)),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        BaseGame.sootHelloSound.play(BaseGame.soundVolume, pitch, 0);
                    }
                })
        ));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (Loot loot : loot) {
            loot.setX(getX());
            loot.setY(getY() + getHeight() / 2);
        }
    }

    public void carry(Loot loot) {
        if (!hasPickedUp) {
            BaseGame.sootPickupSound.play(BaseGame.soundVolume, pitch, 0);
            hasPickedUp = true;
        }
        carrying++;
        setAnimation(carryingAnimation);
        setSize(8, 8);
        this.loot.add(loot);
        isDragging = false;
    }

    public void notCarrying() {
        carrying = 0;
        setAnimation(runningAnimation);
        setSize(8, 8);
    }

    public boolean canCarry() {
        return maxCarry > carrying && !isDragging;
    }

    public boolean isCarrying() {
        return carrying > 0;
    }

    public Loot getRidOfLoot() {
        if (isCarrying()) {
            BaseGame.sootTossSound.play(BaseGame.soundVolume, pitch, 0);
            carrying = 0;
            addAction(Actions.sequence(
                    Actions.delay(.25f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            setAnimation(runningAnimation);
                            setSize(8, 8);
                        }
                    })
            ));
            return loot.pop();
        }
        return null;
    }

    public void isDraggingBag() {
        if (!isDragging) {
            BaseGame.sootDraggingSound.play(BaseGame.soundVolume, pitch, 0);
            isDragging = true;
            setAnimation(draggingAnimation);
            setSize(8, 8);
            carrying = 0;
            getRidOfLoot();
        }
    }

    public void notDraggingBag() {
        if (isDragging) {
            isDragging = false;
            setAnimation(runningAnimation);
            setSize(8, 8);
        }
    }

    public void playDelayedSound(final Sound sound) {
        addAction(Actions.sequence(
                Actions.delay(MathUtils.random(0f, .5f)),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        sound.play(BaseGame.soundVolume, pitch, 0);
                    }
                })
        ));
    }

    public void setSelect(boolean isSelected) {

    }
}