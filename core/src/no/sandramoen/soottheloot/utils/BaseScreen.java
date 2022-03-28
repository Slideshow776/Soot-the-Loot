package no.sandramoen.soottheloot.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class BaseScreen implements Screen, InputProcessor {
    protected Stage mainstage;
    protected Stage uiStage;
    protected Table uiTable;

    public BaseScreen() {
        mainstage = new Stage();
        mainstage.setViewport(new ExtendViewport(100, 100));

        uiTable = new Table();
        uiTable.setFillParent(true);
        uiStage = new Stage();
        uiStage.addActor(uiTable);
        uiStage.setViewport(new ScreenViewport());

        initialize();
    }

    public abstract void initialize();

    public abstract void update(float delta);

    @Override
    public void render(float delta) {
        uiStage.act(delta);
        mainstage.act(delta);
        update(delta);

        Gdx.gl.glClearColor(1f, .8f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mainstage.getViewport().apply();
        mainstage.draw();

        uiStage.getViewport().apply();
        uiStage.draw();
    }

    @Override
    public void show() {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.addProcessor(this);
        im.addProcessor(uiStage);
        im.addProcessor(mainstage);
    }

    @Override
    public void hide() {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.removeProcessor(this);
        im.removeProcessor(uiStage);
        im.removeProcessor(mainstage);
    }

    @Override
    public void resize(int width, int height) {
        mainstage.getViewport().update(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
