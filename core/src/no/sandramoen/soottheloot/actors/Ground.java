package no.sandramoen.soottheloot.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import no.sandramoen.soottheloot.utils.BaseActor;

public class Ground extends BaseActor {

    private BaseActor ground0;
    private BaseActor ground1;

    private float groundSpeed = .5f;

    public Ground(Stage stage) {
        super(-55, -25, stage);
        ground0 = new BaseActor(getX(), getY(), getStage());
        ground0.loadImage("ground");
        ground0.setSize(worldBounds.width * 3f, 20);
        /*ground0.setColor(Color.PINK);*/

        ground1 = new BaseActor(ground0.getX() - ground0.getWidth() + .1f, ground0.getY(), getStage());
        ground1.loadImage("ground");
        ground1.setSize(worldBounds.width * 3f, 20);

        addActor(ground0);
        addActor(ground1);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        ground0.setX(ground0.getX() + groundSpeed);
        ground1.setX(ground1.getX() + groundSpeed);

        if (ground0.getX() > 200)
            ground0.setX(ground1.getX() - ground1.getWidth() + .1f);

        if (ground1.getX() > 200)
            ground1.setX(ground0.getX() - ground0.getWidth() + .1f);
    }
}
