package no.sandramoen.soottheloot.utils;

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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
    public static Color lightBlue = new Color(0.643f, 0.867f, 0.859f, 1f);
    public static LabelStyle label36Style;
    public static LabelStyle label26Style;
    private static TextButtonStyle textButtonStyle;
    public static TextureAtlas textureAtlas;
    private static Skin skin;
    public static Music levelMusic;
    public static Sound coinDropSound;
    public static Sound coinLooted;
    public static Sound sootChosenSound;
    public static Sound sootTossSound;
    public static Sound sootPickupSound;
    public static Sound sootGoToSound;
    public static Sound sootCaughtSound;
    public static Sound sootDraggingSound;
    public static Sound sootYippeeSound;
    public static Sound sootCheerSound;
    public static Sound sootScreamSound;
    public static Sound sootPhewSound;

    // game state
    public static Preferences prefs;
    public static boolean loadPersonalParameters;
    public static float soundVolume = 1f;
    public static float musicVolume = 0.75f;
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
        assetManager.load("audio/music/320685__dpren__swage.mp3", Music.class);

        // sound
        assetManager.load("audio/sound/349277__deleted-user-2104797__coin-on-coins-01.wav", Sound.class);
        assetManager.load("audio/sound/Pickup_Coin13.wav", Sound.class);
        assetManager.load("audio/sound/soots/chosen.wav", Sound.class);
        assetManager.load("audio/sound/soots/toss.wav", Sound.class);
        assetManager.load("audio/sound/soots/pickup.wav", Sound.class);
        assetManager.load("audio/sound/soots/goTo.wav", Sound.class);
        assetManager.load("audio/sound/soots/caught.wav", Sound.class);
        assetManager.load("audio/sound/soots/dragging.wav", Sound.class);
        assetManager.load("audio/sound/soots/yippie.wav", Sound.class);
        assetManager.load("audio/sound/soots/cheer.wav", Sound.class);
        assetManager.load("audio/sound/soots/scream.wav", Sound.class);
        assetManager.load("audio/sound/soots/phew.wav", Sound.class);

        assetManager.finishLoading();

        // music
        levelMusic = assetManager.get("audio/music/320685__dpren__swage.mp3", Music.class);

        // sound
        coinDropSound = assetManager.get("audio/sound/349277__deleted-user-2104797__coin-on-coins-01.wav", Sound.class);
        coinLooted = assetManager.get("audio/sound/Pickup_Coin13.wav", Sound.class);
        sootChosenSound = assetManager.get("audio/sound/soots/chosen.wav", Sound.class);
        sootTossSound = assetManager.get("audio/sound/soots/toss.wav", Sound.class);
        sootPickupSound = assetManager.get("audio/sound/soots/pickup.wav", Sound.class);
        sootGoToSound = assetManager.get("audio/sound/soots/goTo.wav", Sound.class);
        sootCaughtSound = assetManager.get("audio/sound/soots/caught.wav", Sound.class);
        sootDraggingSound = assetManager.get("audio/sound/soots/dragging.wav", Sound.class);
        sootYippeeSound = assetManager.get("audio/sound/soots/yippie.wav", Sound.class);
        sootCheerSound = assetManager.get("audio/sound/soots/cheer.wav", Sound.class);
        sootScreamSound = assetManager.get("audio/sound/soots/scream.wav", Sound.class);
        sootPhewSound = assetManager.get("audio/sound/soots/phew.wav", Sound.class);

        textureAtlas = assetManager.get("images/included/packed/images.pack.atlas");

        long endTime = System.currentTimeMillis();
        Gdx.app.error(this.getClass().getSimpleName(), "Asset manager took " + (endTime - startTime) + " ms to load all game assets.");
    }
}
