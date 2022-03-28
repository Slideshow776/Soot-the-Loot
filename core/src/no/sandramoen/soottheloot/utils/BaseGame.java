package no.sandramoen.soottheloot.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.I18NBundle;

public abstract class BaseGame extends Game implements AssetErrorListener {
    private static BaseGame game;

    public static AssetManager assetManager;
    public static final float WORLD_WIDTH = 100.0F;
    public static final float WORLD_HEIGHT = 100.0F;
    public static final float scale = 1.0F;
    private static float RATIO;
    private static final Color lightPink = new Color(1.0F, 0.816F, 0.94F, 1.0F);
    private static boolean enableCustomShaders = true;

    // game assets
    public static LabelStyle label36Style;
    public static LabelStyle label26Style;
    private static TextButtonStyle textButtonStyle;
    public static TextureAtlas textureAtlas;
    private static Skin skin;

    // game state
    public static Preferences prefs;
    public static boolean loadPersonalParameters;
    public static float soundVolume = 1f;
    public static float musicVolume = 0.5f;
    public static String currentLocale;
    public static I18NBundle myBundle;

    public BaseGame() {
        game = this;
    }

    public static void setActiveScreen(BaseScreen screen) {
        game.setScreen(screen);
    }

    public void create() {
        Gdx.input.setInputProcessor(new InputMultiplexer());

        assetManager();

        label36Style = new LabelStyle();
        BitmapFont myFont = new BitmapFont(Gdx.files.internal("fonts/arcade36.fnt"));
        label36Style.font = myFont;

        label26Style = new LabelStyle();
        BitmapFont myFont2 = new BitmapFont(Gdx.files.internal("fonts/arcade26.fnt"));
        label26Style.font = myFont2;

        /*if (Gdx.app.getType() != Application.ApplicationType.Android) {
            Gdx.graphics.setCursor(Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("images/excluded/cursor.png")), 0, 0));
        }*/
    }

    public void dispose() {
        super.dispose();

        try {
            assetManager.dispose();
        } catch (Error error) {
            Gdx.app.error(this.getClass().getSimpleName(), error.toString());
        }

    }

    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(this.getClass().getSimpleName(), "Could not load asset: " + asset.fileName, throwable);
    }

    private void assetManager() {
        long startTime = System.currentTimeMillis();
        assetManager = new AssetManager();
        assetManager.setErrorListener(this);
        assetManager.load("images/included/packed/images.pack.atlas", TextureAtlas.class);

        // music
        // assetManager.load("audio/music/320732__shadydave__time-break-drum-only.mp3", Music.class);

        // sound
        // assetManager.load("audio/sound/202091__spookymodem__falling-bones.wav", Sound.class);

        assetManager.finishLoading();

        // music
        // levelMusic = assetManager.get("audio/music/320732__shadydave__time-break-drum-only.mp3", Music.class);

        // sound
        // bonesSound = assetManager.get("audio/sound/202091__spookymodem__falling-bones.wav", Sound.class);

        textureAtlas = assetManager.get("images/included/packed/images.pack.atlas");

        long endTime = System.currentTimeMillis();
        Gdx.app.error(this.getClass().getSimpleName(), "Asset manager took " + (endTime - startTime) + " ms to load all game assets.");
    }
}
