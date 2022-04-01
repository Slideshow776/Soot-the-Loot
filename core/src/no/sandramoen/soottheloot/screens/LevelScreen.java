package no.sandramoen.soottheloot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.soottheloot.actors.Bag;
import no.sandramoen.soottheloot.actors.Trash;
import no.sandramoen.soottheloot.actors.loot.Coin;
import no.sandramoen.soottheloot.actors.loot.Diamond;
import no.sandramoen.soottheloot.actors.loot.Loot;
import no.sandramoen.soottheloot.actors.Ground;
import no.sandramoen.soottheloot.actors.Soot;
import no.sandramoen.soottheloot.actors.Wall;
import no.sandramoen.soottheloot.actors.loot.Ruby;
import no.sandramoen.soottheloot.actors.loot.Sapphire;
import no.sandramoen.soottheloot.utils.BaseActor;
import no.sandramoen.soottheloot.utils.BaseGame;
import no.sandramoen.soottheloot.utils.BaseScreen;

public class LevelScreen extends BaseScreen {
    private Array<Soot> soots;
    private Soot currentSoot;
    private Bag bag;
    private Array<Loot> loot;
    private Array<Trash> trash;
    private Label lootCollectedLabel;
    private Label storyLabel;
    private Label label;
    private Label label1;
    private boolean loosingBagFlag = false;
    private boolean gameOver = false;
    private int level = 0;
    private BaseActor coinSpawner;
    private BaseActor gemSpawner;

    @Override
    public void initialize() {
        playLevelMusic();

        BaseActor.setWorldBounds(100, 100);
        new Ground(mainstage);
        new Wall(mainstage);
        bag = new Bag(170, -35, mainstage); // 70
        loot = new Array();

        soots = new Array();
        for (int i = 0; i < 1; i++)
            soots.add(new Soot(80 + 2 * i, -35, mainstage));

        trash = new Array();
        spawnLoot();
        uiSetup();
    }

    @Override
    public void update(float delta) {
        for (Loot loot : loot) {
            checkIfCoinRemove(loot);

            for (Soot soot : soots) {
                if (soot.overlaps(loot) && soot.canCarry()) {
                    soot.carry(loot);
                    BaseGame.sootCaughtSound.play(BaseGame.soundVolume, soot.pitch, 0f);
                    loot.caught();
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
        if (level > 0 && bag.inPlay) {
            loosingBagSounds();
            checkGameOver();
        }

        for (Trash trash : trash) {
            for (Soot soot : soots) {
                if (trash.overlaps(soot)) {
                    soots.removeValue(soot, true);
                    BaseGame.sootDeathSound.play(BaseGame.soundVolume, soot.pitch, 0);
                    soot.remove();
                }
            }
        }

        levelProgression();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!BaseGame.levelMusic.isPlaying())
            playLevelMusic();

        Vector3 worldCoordinates = mainstage.getCamera().unproject(new Vector3(screenX, screenY, 0f));
        if (worldCoordinates.y > -28) worldCoordinates.set(worldCoordinates.x, -28, 0f);

        Soot pastSoot = currentSoot;
        for (Soot soot : soots) {
            if (soot != currentSoot)
                soot.crown.setVisible(false);
            if (worldCoordinates.x >= soot.getX() && worldCoordinates.x <= soot.getX() + soot.getWidth() &&
                    worldCoordinates.y >= soot.getY() && worldCoordinates.y <= soot.getY() + soot.getHeight()) {
                if (currentSoot != null)
                    currentSoot.crown.setVisible(false);
                currentSoot = soot;
                soot.crown.setVisible(true);
                BaseGame.sootChosenSound.play(BaseGame.soundVolume, soot.pitch, 0);
            }
        }

        if (currentSoot != null && pastSoot == currentSoot) {
            BaseGame.sootGoToSound.play(BaseGame.soundVolume, currentSoot.pitch, 0);
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

    private void checkIfCoinRemove(Loot loot) {
        if (loot.remove) {
            this.loot.removeValue(loot, true);
            loot.remove();
        }
    }

    private void setDraggingBag(int i) {
        if (soots.get(i).overlaps(bag)) {
            soots.get(i).isDraggingBag();
        } else {
            soots.get(i).notDraggingBag();
        }
    }

    private void loosingBagSounds() {
        if (bag.isLoosing && !loosingBagFlag) {
            loosingBagFlag = true;
            for (int j = 0; j < soots.size; j++) {
                soots.get(j).playDelayedSound(BaseGame.sootScreamSound);
            }
        } else if (!bag.isLoosing && loosingBagFlag) {
            loosingBagFlag = false;
            for (int j = 0; j < soots.size; j++) {
                soots.get(j).playDelayedSound(BaseGame.sootPhewSound);
            }
        }
    }

    private void collectCoinToBag(int i) {
        if (soots.get(i).isCarrying() && soots.get(i).isWithinDistance(20, bag)) {
            BaseGame.sootYippeeSound.play(BaseGame.soundVolume, soots.get(i).pitch, 0);
            for (int j = 0; j < soots.size; j++) {
                if (soots.get(j).isDragging) {
                    soots.get(j).playDelayedSound(BaseGame.sootCheerSound);
                }
            }
            Loot loot = soots.get(i).getRidOfLoot();
            loot.addAction(Actions.sequence(Actions.parallel(
                    Actions.scaleTo(0, 0, .2f),
                    Actions.moveTo(bag.getX() + bag.getWidth() / 8, bag.getY() + bag.getHeight() / 4, .2f)
            )));
            if (loot.value == 1)
                bag.addLoot(loot.weight, loot.value, BaseGame.coinLooted);
            else
                bag.addLoot(loot.weight, loot.value, BaseGame.gemLooted);
            lootCollectedLabel.setText("Loot: " + bag.getLootValue());
        }
    }

    private void passCoinToOtherSoots(int i) {
        for (int j = 0; j < soots.size; j++) {
            if (soots.get(i).id == soots.get(j).id)
                continue;

            if (soots.get(i).isCarrying() && !soots.get(j).isCarrying() && !soots.get(j).isDragging && soots.get(j).canCarry() &&
                    soots.get(i).isWithinDistance(40, soots.get(j)) && soots.get(i).getX() < soots.get(j).getX()
            ) {
                final Loot loot = soots.get(i).getRidOfLoot();
                final int finalJ = j;
                loot.addAction(Actions.sequence(
                        Actions.parallel(
                                Actions.sequence(
                                        Actions.moveTo(
                                                soots.get(j).toX + (soots.get(i).getX() - soots.get(j).toX) / 2,
                                                soots.get(i).toY + MathUtils.random(10, 20),
                                                .3f
                                        ),
                                        Actions.moveTo(
                                                soots.get(j).toX,
                                                soots.get(j).toY,
                                                .3f
                                        )
                                ),
                                Actions.rotateBy(MathUtils.random(-360, 360), .6f)
                        ),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                if (!soots.get(finalJ).isDragging && soots.get(finalJ).canCarry())
                                    soots.get(finalJ).carry(loot);
                                else {
                                    loot.clearActions();
                                    loot.addAction(Actions.forever(Actions.rotateBy(-720f, 1f)));
                                    loot.addAction(Actions.sequence(
                                            Actions.moveTo(200, loot.getY(), 1f),
                                            Actions.run(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loot.remove = true;
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

    private void checkGameOver() {
        if (bag.getX() > 110 && !gameOver) {
            gameOver = true;
            bag.inPlay = false;
            bag.setVisible(false);
            lootCollectedLabel.clearActions();
            storyLabel.clearActions();

            storyLabel.setText("GAME OVER!");
            storyLabel.setFontScale(2);
            storyLabel.addAction(Actions.fadeIn(1f));
            label.addAction(Actions.fadeIn(1f));
            label1.addAction(Actions.fadeIn(1f));
            for (Soot soot : soots) {
                soot.addAction(Actions.moveTo(200, soot.getY(), 1f));
            }
        }
    }

    private void spawnLoot() {
        coinSpawner = new BaseActor(0, 0, mainstage);
        coinSpawner.addAction(Actions.forever(Actions.sequence(
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        loot.add(new Coin(-80, 100, mainstage, 0, -40));
                    }
                }),
                Actions.delay(4f)
        )));
    }

    private void playLevelMusic() {
        BaseGame.levelMusic.setVolume(BaseGame.musicVolume);
        BaseGame.levelMusic.setLooping(true);
        BaseGame.levelMusic.play();
    }

    private void levelProgression() {
        if (level == 0) {
            for (Soot soot : soots) {
                if (soot.isCarrying() && !storyLabel.hasActions()) {
                    storyLabel.setText("Good job!");
                    storyLabel.addAction(Actions.sequence(
                            Actions.fadeIn(1),
                            Actions.delay(2),
                            Actions.fadeOut(1),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    level++;
                                }
                            })
                    ));
                }
            }
        } else if (level == 1) {
            if (soots.size == 1) {
                soots.add(new Soot(bag.getX() + bag.getWidth() / 2, bag.getY(), mainstage));
                soots.get(1).addAction(Actions.moveTo(90, bag.getY(), 3f));
                bag.addAction(Actions.moveTo(70, bag.getY(), 3f));
                BaseGame.bagDragging.play(BaseGame.soundVolume);

                storyLabel.setText("Your new friend got a bag to collect loot in!");
                storyLabel.addAction(Actions.sequence(
                        Actions.fadeIn(1),
                        Actions.delay(2),
                        Actions.fadeOut(1),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                lootCollectedLabel.addAction(Actions.fadeIn(1));
                                bag.inPlay = true;
                            }
                        })
                ));
            }
            if (bag.getLootValue() > 3) level++;
        } else if (level == 2) {
            if (soots.size == 2) {
                storyLabel.setText("Even more friends!");
                storyLabel.addAction(Actions.sequence(
                        Actions.fadeIn(1),
                        Actions.delay(2),
                        Actions.fadeOut(1)
                ));

                soots.add(new Soot(170, bag.getY(), mainstage));
                soots.add(new Soot(170, bag.getY(), mainstage));
                soots.get(2).addAction(Actions.moveTo(70 + bag.getWidth() / 2 + MathUtils.random(-20, 5f), bag.getY() + MathUtils.random(-5f, 5f), 3f));
                soots.get(3).addAction(Actions.moveTo(70 + bag.getWidth() / 2 + MathUtils.random(-20f, 5f), bag.getY() + MathUtils.random(-5f, 5f), 3f));
            }
            if (bag.getLootValue() > 10) level++;
        } else if (level == 3) {
            if (soots.size == 4) {
                soots.add(new Soot(170, bag.getY(), mainstage));
                soots.get(4).addAction(Actions.moveTo(70 + bag.getWidth() / 2 + MathUtils.random(-20f, 5f), bag.getY() + MathUtils.random(-5f, 5f), 3f));
                coinSpawner.clearActions();
                coinSpawner.addAction(Actions.forever(Actions.sequence(
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                loot.add(new Coin(-100, 100, mainstage, 0, -40));
                                loot.add(new Coin(-120, 100, mainstage, -50, -35));
                            }
                        }),
                        Actions.delay(4f)
                )));
            }
            if (bag.getLootValue() > 20) level++;
        } else if (level == 4) {
            if (soots.size == 5) {
                soots.add(new Soot(170, bag.getY(), mainstage));
                soots.get(5).addAction(Actions.moveTo(70 + bag.getWidth() / 2 + MathUtils.random(-20f, 5f), bag.getY() + MathUtils.random(-5f, 5f), 3f));
                soots.add(new Soot(170, bag.getY(), mainstage));
                soots.get(6).addAction(Actions.moveTo(70 + bag.getWidth() / 2 + MathUtils.random(-20f, 5f), bag.getY() + MathUtils.random(-5f, 5f), 3f));
            }
            if (bag.getLootValue() > 32) level++;
        } else if (level == 5) {
            if (soots.size == 7) {
                soots.add(new Soot(170, bag.getY(), mainstage));
                soots.get(7).addAction(Actions.moveTo(70 + bag.getWidth() / 2 + MathUtils.random(-20f, 5f), bag.getY() + MathUtils.random(-5f, 5f), 3f));
                soots.add(new Soot(170, bag.getY(), mainstage));
                soots.get(8).addAction(Actions.moveTo(70 + bag.getWidth() / 2 + MathUtils.random(-20f, 5f), bag.getY() + MathUtils.random(-5f, 5f), 3f));
                soots.add(new Soot(170, bag.getY(), mainstage));
                soots.get(9).addAction(Actions.moveTo(70 + bag.getWidth() / 2 + MathUtils.random(-20f, 5f), bag.getY() + MathUtils.random(-5f, 5f), 3f));
                soots.add(new Soot(170, bag.getY(), mainstage));
                soots.get(10).addAction(Actions.moveTo(70 + bag.getWidth() / 2 + MathUtils.random(-20f, 5f), bag.getY() + MathUtils.random(-5f, 5f), 3f));
                coinSpawner.clearActions();
                coinSpawner.addAction(Actions.forever(Actions.sequence(
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                loot.add(new Coin(-100, 100, mainstage, 0, -40));
                                loot.add(new Coin(-120, 100, mainstage, -50, -35));
                                loot.add(new Coin(-120, 100, mainstage, -90, -30));
                                loot.add(new Coin(-120, 100, mainstage, -70, -35));
                            }
                        }),
                        Actions.delay(4f)
                )));
                gemSpawner = new BaseActor(0, 0, mainstage);
                gemSpawner.addAction(Actions.forever(Actions.sequence(
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                Vector2 randomPosition = new Vector2(MathUtils.random(-50, 30), MathUtils.random(-40, -32));
                                int randomGem = MathUtils.random(1, 3);
                                if (randomGem == 1)
                                    loot.add(new Diamond(-80, 100, mainstage, randomPosition.x, randomPosition.y));
                                else if (randomGem == 2)
                                    loot.add(new Ruby(-80, 100, mainstage, randomPosition.x, randomPosition.y));
                                else if (randomGem == 3)
                                    loot.add(new Sapphire(-80, 100, mainstage, randomPosition.x, randomPosition.y));
                            }
                        }),
                        Actions.delay(20f)
                )));
                BaseActor levelSpawner = new BaseActor(0f, 0f, mainstage);
                levelSpawner.addAction(Actions.forever(Actions.sequence(
                        Actions.delay(20),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                coinSpawner.addAction(Actions.forever(Actions.sequence(
                                        Actions.run(new Runnable() {
                                            @Override
                                            public void run() {
                                                loot.add(new Coin(-100, 100, mainstage, MathUtils.random(-80, 10), MathUtils.random(-40, -32)));
                                            }
                                        }),
                                        Actions.delay(4)
                                )));
                                if (soots.size < 50) {
                                    soots.add(new Soot(170, bag.getY(), mainstage));
                                    soots.get(soots.size - 1).addAction(Actions.moveTo(70 + bag.getWidth() / 2 + MathUtils.random(-20f, 5f), bag.getY() + MathUtils.random(-5f, 5f), 3f));
                                }
                            }
                        })
                )));
                new BaseActor(0, 0, mainstage).addAction(Actions.forever(Actions.sequence(
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                trash.add(new Trash(-100, 100, mainstage, MathUtils.random(-80, 10), MathUtils.random(-40, -32)));
                                soots.add(new Soot(170, bag.getY(), mainstage));
                                soots.get(soots.size - 1).addAction(Actions.moveTo(70 + bag.getWidth() / 2 + MathUtils.random(-20f, 5f), bag.getY() + MathUtils.random(-5f, 5f), 3f));
                            }
                        }),
                        Actions.delay(60)
                )));
            }
        }
    }

    private void uiSetup() {
        lootCollectedLabel = new Label("Loot: 0", BaseGame.label36Style);
        lootCollectedLabel.setColor(BaseGame.lightBlue);
        lootCollectedLabel.addAction(Actions.fadeOut(0f));
        uiTable.add(lootCollectedLabel).expandY().top().padTop(Gdx.graphics.getHeight() * .01f).row();

        storyLabel = new Label("Follow the overburdened Adventurer,\nand grab the loot they're dropping!", BaseGame.label36Style);
        storyLabel.setFontScale(1.2f);
        storyLabel.setColor(BaseGame.lightBlue);
        storyLabel.addAction(Actions.sequence(
                Actions.delay(5f),
                Actions.fadeOut(1f)
        ));
        uiTable.add(storyLabel).expandY().top().padTop(Gdx.graphics.getHeight() * .01f).row();

        Color color = new Color(0.035f, 0.039f, 0.078f, 1f);
        label = new Label("You lost the bag, silly!", BaseGame.label26Style);
        label.setColor(color);
        label.addAction(Actions.fadeOut(0));
        uiTable.add(label).padBottom(Gdx.graphics.getHeight() * .01f).row();
        label1 = new Label("Press 'R' to restart", BaseGame.label26Style);
        label1.setColor(color);
        label1.addAction(Actions.fadeOut(0));
        uiTable.add(label1).padBottom(Gdx.graphics.getHeight() * .08f);
    }
}
