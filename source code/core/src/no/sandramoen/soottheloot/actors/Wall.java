package no.sandramoen.soottheloot.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import no.sandramoen.soottheloot.utils.BaseActor;

public class Wall extends BaseActor {

    private BaseActor wall0;
    private BaseActor wall1;

    private float groundSpeed = .5f;

    public Wall(Stage stage) {
        super(-55, -15, stage);
        wall0 = new BaseActor(getX(), getY(), getStage());
        wall0.loadImage("wall");
        wall0.setSize(worldBounds.width * 3f, 100);
        /*ground0.setColor(Color.PINK);*/

        wall1 = new BaseActor(wall0.getX() - wall0.getWidth() + .1f, wall0.getY(), getStage());
        wall1.loadImage("wall");
        wall1.setSize(wall0.getWidth(), wall0.getHeight());

        addActor(wall0);
        addActor(wall1);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        wall0.setX(wall0.getX() + groundSpeed);
        wall1.setX(wall1.getX() + groundSpeed);

        if (wall0.getX() > 200)
            wall0.setX(wall1.getX() - wall1.getWidth() + .1f);

        if (wall1.getX() > 200)
            wall1.setX(wall0.getX() - wall0.getWidth() + .1f);
    }
}
