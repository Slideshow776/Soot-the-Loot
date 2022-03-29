package no.sandramoen.soottheloot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.soottheloot.actors.Bag;
import no.sandramoen.soottheloot.actors.Coin;
import no.sandramoen.soottheloot.actors.Ground;
import no.sandramoen.soottheloot.actors.Soot;
import no.sandramoen.soottheloot.utils.BaseActor;
import no.sandramoen.soottheloot.utils.BaseGame;
import no.sandramoen.soottheloot.utils.BaseScreen;

public class LevelScreen extends BaseScreen {
    private Array<Soot> soots;
    private Soot currentSoot;
    private Bag bag;
    private Array<Coin> coins;

    @Override
    public void initialize() {
        BaseActor.setWorldBounds(100, 100);
        new Ground(mainstage);
        bag = new Bag(80f, -35f,mainstage);
        coins = new Array();

        BaseActor coinSpawner = new BaseActor(0, 0, mainstage);
        coinSpawner.addAction(Actions.forever(Actions.sequence(
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        coins.add(new Coin(-110, 100, mainstage));
                        coins.add(new Coin(-105, 100, mainstage));
                        coins.add(new Coin(-100, 100, mainstage));
                        coins.add(new Coin(-95, 100, mainstage));
                    }
                }),
                Actions.delay(4f)
        )));

        soots = new Array();
        soots.add(new Soot(95, -35, mainstage));
        soots.add(new Soot(90, -35, mainstage));
        soots.add(new Soot(85, -35, mainstage));
        soots.add(new Soot(80, -35, mainstage));
    }

    @Override
    public void update(float delta) {
        for (Coin coin : coins) {
            if (coin.remove) {
                coins.removeValue(coin, true);
                coin.remove();
            }

            for (Soot soot : soots) {
                if (soot.overlaps(coin)) {
                    coins.removeValue(coin, true);
                    coin.remove();
                    bag.incrementSize();
                }
            }
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldCoordinates = mainstage.getCamera().unproject(new Vector3(screenX, screenY, 0f));
        if (worldCoordinates.y > -28) worldCoordinates.set(worldCoordinates.x, -28, 0f);

        for (Soot soot : soots) {
            if (soot != currentSoot)
                soot.setDebug(false);
            if (worldCoordinates.x >= soot.getX() && worldCoordinates.x <= soot.getX() + soot.getWidth() &&
                    worldCoordinates.y >= soot.getY() && worldCoordinates.y <= soot.getY() + soot.getHeight()) {
                if (currentSoot != null)
                    currentSoot.setDebug(false);
                currentSoot = soot;
                soot.setDebug(true);
            }
        }

        if (currentSoot != null)
            currentSoot.addAction(Actions.moveTo(
                    worldCoordinates.x - currentSoot.getWidth() / 2,
                    worldCoordinates.y - currentSoot.getHeight() / 2,
                    1f,
                    Interpolation.pow2)
            );
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.Q) Gdx.app.exit();
        if (keycode == Keys.R) BaseGame.setActiveScreen(new LevelScreen());
        return super.keyDown(keycode);
    }
}
