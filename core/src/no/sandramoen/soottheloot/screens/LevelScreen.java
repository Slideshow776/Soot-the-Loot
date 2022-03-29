package no.sandramoen.soottheloot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
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
        bag = new Bag(80f, -35f, mainstage);
        coins = new Array();

        BaseActor coinSpawner = new BaseActor(0, 0, mainstage);
        coinSpawner.addAction(Actions.forever(Actions.sequence(
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        coins.add(new Coin(-110, 100, mainstage, -50, -40));
                    }
                }),
                Actions.delay(4f)
        )));

        soots = new Array();
        for (int i = 0; i < 4; i++)
            soots.add(new Soot(80 + 5 * i, -35, mainstage));
    }

    @Override
    public void update(float delta) {
        for (Coin coin : coins) {
            if (coin.remove) {
                coins.removeValue(coin, true);
                coin.remove();
            }

            for (Soot soot : soots) {
                if (soot.overlaps(coin) && soot.canCarry()) {
                    soot.carry(coin);
                    coin.caught();
                }
            }
        }

        for (int i = 0; i < soots.size; i++) {
            // collect coin to bag
            if (soots.get(i).isCarrying() && soots.get(i).isWithinDistance(20, bag)) {
                Coin coin = soots.get(i).getRidOfCoin();
                coin.addAction(Actions.sequence(Actions.parallel(
                        Actions.scaleTo(0, 0, .2f),
                        Actions.moveTo(bag.getX() + bag.getWidth() / 8, bag.getY() + bag.getHeight() / 4, .2f)
                )));
                bag.incrementSize();
            }

            // pass coin to other soots
            for (int j = 0; j < soots.size; j++) {
                if (soots.get(i).id == soots.get(j).id)
                    continue;

                if (soots.get(i).isCarrying() && !soots.get(j).isCarrying() &&
                        soots.get(i).isWithinDistance(40, soots.get(j)) && soots.get(i).getX() < soots.get(j).getX()
                ) {
                    System.out.println("mark 0");

                    final Coin coin = soots.get(i).getRidOfCoin();

                    final int finalJ = j;
                    coin.addAction(Actions.sequence(
                            Actions.moveTo(
                                    soots.get(j).toX + (soots.get(i).getX() - soots.get(j).toX) / 2,
                                    soots.get(i).toY + MathUtils.random(10, 20),
                                    .3f
                            ),
                            Actions.moveTo(
                                    soots.get(j).toX,
                                    soots.get(j).toY,
                                    .3f
                            ),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    soots.get(finalJ).carry(coin);
                                }
                            })
                    ));
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

        if (currentSoot != null) {
            currentSoot.toX = worldCoordinates.x - currentSoot.getWidth() / 2;
            currentSoot.toY = worldCoordinates.y - currentSoot.getHeight() / 2;
            currentSoot.addAction(Actions.moveTo(
                    worldCoordinates.x - currentSoot.getWidth() / 2,
                    worldCoordinates.y - currentSoot.getHeight() / 2,
                    1f,
                    Interpolation.pow2)
            );
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.Q) Gdx.app.exit();
        if (keycode == Keys.R) BaseGame.setActiveScreen(new LevelScreen());
        return super.keyDown(keycode);
    }
}
