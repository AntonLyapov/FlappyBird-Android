package com.lyapov.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private static final String TAG = FlappyBird.class.getSimpleName();

    // Sprite batch
    private SpriteBatch mSpriteBatch;

    // Sprite background
    private Texture mBackgroundTexture;

    // Array with bird images
    private Texture[] mBirdTextures;

    // Bird circle
    private Circle mBirdCircle;

    // Top and bottom textures
    private Texture mTopTubeTexture;
    private Texture mBottomTubeTexture;

    // Game over texture
    private Texture mGameOverTexture;

    // Game state
    private GameState gameState = GameState.GameBegin;

    // Score properties
    private int mScore = 0;
    private int mScoringTube = 0;

    // Random generator for calculate tube position
    private Random mRandomGenerator;

    // Tube offsets
    private float[] mTubeOffset;

    // Tube 'x' position
    private float[] mTubeX;

    // Distance between tubes
    private float mDistanceBetweenTubes;

    // Rectangles for check overlaps with bird
    private Rectangle[] mTopTubeRectangles;
    private Rectangle[] mBottomTubeRectangles;

    // BitmapFont for counting score
    private BitmapFont mBitmapFont;

    // Flap state for animating bird
    private int mFlapState = 0;

    // Track temporary bird 'y' position
    private float mBirdY = 0;

    // Track temporary bird velocity
    private float mBirdVelocity = 0;

    @Override
    public void create() {
        // Init SpriteBatch
        mSpriteBatch = new SpriteBatch();

        // Init Sprite background
        mBackgroundTexture = new Texture(AppConfiguration.IMAGE_NAME_BACKGROUND);

        // Init two texture for animating bird
        mBirdTextures = new Texture[2];
        mBirdTextures[0] = new Texture(AppConfiguration.IMAGE_NAME_BIRD_1);
        mBirdTextures[1] = new Texture(AppConfiguration.IMAGE_NAME_BIRD_2);

        // Init bird circle for checking overlaps with tubes
        mBirdCircle = new Circle();

        // Init top and bottom textures
        mTopTubeTexture = new Texture(AppConfiguration.IMAGE_NAME_TOP_TUBE);
        mBottomTubeTexture = new Texture(AppConfiguration.IMAGE_NAME_BOTTOM_TUBE);

        // Init game over texture
        mGameOverTexture = new Texture(AppConfiguration.IMAGE_NAME_GAME_OVER);

        // Init random generator
        mRandomGenerator = new Random();

        // Tube offsets
        mTubeOffset = new float[AppConfiguration.NUMBER_OF_TUBES];

        // Tube 'x' position
        mTubeX = new float[AppConfiguration.NUMBER_OF_TUBES];

        // Set distance between tubes
        mDistanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;

        // Init rectangles for check overlaps with bird
        mTopTubeRectangles = new Rectangle[AppConfiguration.NUMBER_OF_TUBES];
        mBottomTubeRectangles = new Rectangle[AppConfiguration.NUMBER_OF_TUBES];

        // Init score bitmap font
        mBitmapFont = new BitmapFont();
        mBitmapFont.setColor(Color.WHITE);
        mBitmapFont.getData().setScale(15);

        // Start game
        startGame();
    }

    @Override
    public void render() {
        // Begin draw sprite
        mSpriteBatch.begin();

        // Draw background texture in full graphic screen
        mSpriteBatch.draw(mBackgroundTexture, 0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        switch (gameState) {
            case GameBegin:
                if (Gdx.input.isTouched()) {
                    gameState = GameState.InGame;
                }
                break;

            case InGame:
                if (mTubeX[mScoringTube] < Gdx.graphics.getWidth() / 2) {
                    mScore++;

                    if (mScoringTube < AppConfiguration.NUMBER_OF_TUBES - 1) {
                        mScoringTube++;
                    } else {
                        mScoringTube = 0;
                    }
                }

                if (Gdx.input.isTouched()) {
                    mBirdVelocity = AppConfiguration.BIRD_POSITIVE_GRAVITY;
                }

                for (int i = 0; i < AppConfiguration.NUMBER_OF_TUBES; i++) {

                    if (mTubeX[i] < -mTopTubeTexture.getWidth()) {
                        mTubeX[i] += AppConfiguration.NUMBER_OF_TUBES * mDistanceBetweenTubes;
                    } else {
                        mTubeX[i] -= AppConfiguration.TUBE_VELOCITY;
                    }

                    float tubeX = mTubeX[i];

                    // Calculate top tube 'y'
                    float topTubeY = (Gdx.graphics.getHeight() + AppConfiguration.GAP) / 2
                            + mTubeOffset[i];

                    // Calculate bottom tube 'y'
                    float bottomTubeY = (Gdx.graphics.getHeight() - AppConfiguration.GAP) / 2
                            - mBottomTubeTexture.getHeight() + mTubeOffset[i];

                    // Draw top and bottom tubes
                    mSpriteBatch.draw(mTopTubeTexture, mTubeX[i], topTubeY);
                    mSpriteBatch.draw(mBottomTubeTexture, mTubeX[i], bottomTubeY);

                    // Set coordinate and dimension for top and bottom tubes
                    mTopTubeRectangles[i].set(tubeX, topTubeY,
                            mTopTubeTexture.getWidth(), mTopTubeTexture.getHeight());
                    mBottomTubeRectangles[i].set(tubeX, bottomTubeY,
                            mBottomTubeTexture.getWidth(), mBottomTubeTexture.getHeight());
                }

                // Check bird 'y' is positive
                if (mBirdY > 0) {
                    // Decrease bird 'y' with calculated velocity
                    mBirdVelocity = mBirdVelocity + AppConfiguration.BIRD_GRAVITY;
                    mBirdY -= mBirdVelocity;
                } else {
                    gameState = GameState.GameOver;
                }
                break;

            case GameOver:
                // Draw game over texture
                mSpriteBatch.draw(mGameOverTexture, (Gdx.graphics.getWidth() - mGameOverTexture.getWidth()) / 2, (Gdx.graphics.getHeight() - mGameOverTexture.getHeight()) / 2);

                if (Gdx.input.isTouched()) {
                    gameState = GameState.InGame;
                    startGame();
                }
                break;
        }

        // Draw specific bird texture in center 'x' and temporary 'y'
        mSpriteBatch.draw(getBirdTextureForState(),
                (Gdx.graphics.getWidth() - getBirdTextureForState().getWidth()) / 2, mBirdY);

        // Set current bird circle for check overlaps with tubes
        mBirdCircle.set(Gdx.graphics.getWidth() / 2,
                mBirdY + getBirdTextureForState().getHeight() / 2,
                getBirdTextureForState().getWidth() / 2);

        // Draw score
        mBitmapFont.draw(mSpriteBatch, String.valueOf(mScore), 50, 150);
        mSpriteBatch.end();

        // Check for collision and
        if (hasCollision()) {
            gameState = GameState.GameOver;
        }
    }

    @Override
    public void dispose() {
        mBackgroundTexture.dispose();
        mTopTubeTexture.dispose();
        mBottomTubeTexture.dispose();
        mGameOverTexture.dispose();

        for (Texture texture : mBirdTextures) {
            texture.dispose();
        }

        mSpriteBatch.dispose();
    }

    /**
     * Get bird state
     *
     * @return
     */
    private Texture getBirdTextureForState() {
        mFlapState = mFlapState == 0 ? 1 : 0;
        return mBirdTextures[mFlapState];
    }

    /**
     * Check for collision between bird and any tube
     *
     * @return
     */
    private boolean hasCollision() {
        for (int i = 0; i < AppConfiguration.NUMBER_OF_TUBES; i++) {
            if (Intersector.overlaps(mBirdCircle, mTopTubeRectangles[i])
                    || Intersector.overlaps(mBirdCircle, mBottomTubeRectangles[i])) {
                return true;
            }
        }

        return false;
    }

    private ShapeRenderer mShapeRenderer;

    private void showDebugShapes() {
        if (mShapeRenderer == null) {
            mShapeRenderer = new ShapeRenderer();
        }

        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        mShapeRenderer.setColor(Color.RED);
        mShapeRenderer.circle(mBirdCircle.x, mBirdCircle.y, mBirdCircle.radius);

        for (int i = 0; i < AppConfiguration.NUMBER_OF_TUBES; i++) {
            float tubeX = mTubeX[i];
            float topTubeY = (Gdx.graphics.getHeight() + AppConfiguration.GAP) / 2 + mTubeOffset[i];
            float bottomTubeY = (Gdx.graphics.getHeight() - AppConfiguration.GAP) / 2 - mBottomTubeTexture.getHeight() + mTubeOffset[i];

            mShapeRenderer.rect(tubeX, topTubeY, mTopTubeTexture.getWidth(), mTopTubeTexture.getHeight());
            mShapeRenderer.rect(tubeX, bottomTubeY, mBottomTubeTexture.getWidth(), mBottomTubeTexture.getHeight());
        }

        mShapeRenderer.end();
    }

    /**
     * Start game
     */
    private void startGame() {
        // Set bird to center in screen
        mBirdY = (Gdx.graphics.getHeight() - getBirdTextureForState().getHeight()) / 2;

        for (int i = 0; i < AppConfiguration.NUMBER_OF_TUBES; i++) {
            // Set default tube 'x' position
            mTubeX[i] = (Gdx.graphics.getWidth() - mTopTubeTexture.getWidth()) / 2 + Gdx.graphics.getWidth() + i * mDistanceBetweenTubes;

            // Set default tube offsets
            mTubeOffset[i] = (mRandomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - AppConfiguration.GAP - AppConfiguration.GAP / 2);

            // Set default tube rectangles
            mTopTubeRectangles[i] = new Rectangle();
            mBottomTubeRectangles[i] = new Rectangle();
        }

        // Set default variables
        mScore = 0;
        mScoringTube = 0;
        mBirdVelocity = 0;
    }
}
