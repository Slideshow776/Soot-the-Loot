package no.sandramoen.soottheloot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.soottheloot.actors.Bag;
import no.sandramoen.soottheloot.actors.Coin;
import no.sandramoen.soottheloot.actors.Ground;
import no.sandramoen.soottheloot.actors.Soot;
import no.sandramoen.soottheloot.actors.Wall;
import no.sandramoen.soottheloot.utils.BaseActor;
import no.sandramoen.soottheloot.utils.BaseGame;
import no.sandramoen.soottheloot.utils.BaseScreen;

public class LevelScreen extends BaseScreen {
    private Array<Soot> soots;
    private Soot currentSoot;
    private Bag bag;
    private Array<Coin> coins;

    private Label lootCollectedLabel;

    @Override
    public void initialize() {
        BaseActor.setWorldBounds(100, 100);
        new Ground(mainstage);
        new Wall(mainstage);
        bag = new Bag(70f, -35f, mainstage);
        coins = new Array();

        soots = new Array();
        for (int i = 0; i < 8; i++)
            soots.add(new Soot(80 + 2 * i, -35, mainstage));

        BaseActor coinSpawner = new BaseActor(0, 0, mainstage);
        coinSpawner.addAction(Actions.forever(Actions.sequence(
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        coins.add(new Coin(-80, 100, mainstage, 0, -40));
                        coins.add(new Coin(-80, 100, mainstage, -10, -35));
                        coins.add(new Coin(-80, 100, mainstage, -20, -30));
                        coins.add(new Coin(-80, 100, mainstage, -30, -32));
                        coins.add(new Coin(-80, 100, mainstage, -40, -33));
                        coins.add(new Coin(-80, 100, mainstage, -50, -35));
                    }
                }),
                Actions.delay(4f)
        )));

        uiSetup();
    }

    @Override
    public void update(float delta) {
        for (Coin coin : coins) {
            checkIfCoinRemove(coin);

            for (Soot soot : soots) {
                if (soot.overlaps(coin) && soot.canCarry()) {
                    soot.carry(coin);
                    coin.caught();
                }
            }
        }

        int countDraggers = 0;
        for (int i = 0; i < soots.size; i++) {
            setDraggingBag(i);
            collectCoinToBag(i);
            passCoinToOtherSoots(i);

            if (soots.get(i).isDragging)
                countDraggers++;
        }

        bag.draggers = countDraggers;
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

    private void checkIfCoinRemove(Coin coin) {
        if (coin.remove) {
            coins.removeValue(coin, true);
            coin.remove();
        }
    }

    private void setDraggingBag(int i) {
        if (soots.get(i).overlaps(bag)) {
            soots.get(i).isDraggingBag();
        } else {
            soots.get(i).notDraggingBag();
        }
    }

    private void collectCoinToBag(int i) {
        if (soots.get(i).isCarrying() && soots.get(i).isWithinDistance(20, bag)) {
            Coin coin = soots.get(i).getRidOfCoin();
            coin.addAction(Actions.sequence(Actions.parallel(
                    Actions.scaleTo(0, 0, .2f),
                    Actions.moveTo(bag.getX() + bag.getWidth() / 8, bag.getY() + bag.getHeight() / 4, .2f)
            )));
            bag.addLoot(coin.weight);
            lootCollectedLabel.setText("Loot: " + bag.getLoot());
        }
    }

    private void passCoinToOtherSoots(int i) {
        for (int j = 0; j < soots.size; j++) {
            if (soots.get(i).id == soots.get(j).id)
                continue;

            if (soots.get(i).isCarrying() && !soots.get(j).isCarrying() && !soots.get(j).isDragging && soots.get(j).canCarry() &&
                    soots.get(i).isWithinDistance(40, soots.get(j)) && soots.get(i).getX() < soots.get(j).getX()
            ) {
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
                                if (!soots.get(finalJ).isDragging && soots.get(finalJ).canCarry())
                                    soots.get(finalJ).carry(coin);
                                else {
                                    coin.clearActions();
                                    coin.addAction(Actions.forever(Actions.rotateBy(-720f, 1f)));
                                    coin.addAction(Actions.sequence(
                                            Actions.moveTo(200, coin.getY(), 1f),
                                            Actions.run(new Runnable() {
                                                @Override
                                                public void run() {
                                                    coin.remove = true;
                                                }
                                            })
                                    ));
                                }
                            }
                        })
                ));
            }
        }
    }

    private void uiSetup() {
        lootCollectedLabel = new Label("Loot: 0", BaseGame.label36Style);
        Color pink = new Color(0.776f, 0.318f, 0.592f, 1f);
        lootCollectedLabel.setColor(pink);
        uiTable.add(lootCollectedLabel).expandY().top().padTop(Gdx.graphics.getHeight() * .01f);
    }
}
