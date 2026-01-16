package com.badlogic.bloodcrypt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.ArrayList;

public class Main implements ApplicationListener {

    public static boolean soundEnabled = true;

    private enum GameState { SPLASH, MENU, GAME, GAMEOVER, TUTORIAL, VICTORY }
    private GameState gameState = GameState.SPLASH;
    private MainMenu mainMenu;
    private Texture splashTexture;
    private float splashTimer = 0f;
    private final float SPLASH_DURATION = 3.0f;
    private final float FADE_DURATION = 1.0f;
    private BitmapFont splashTitleFont;
    private BitmapFont splashAuthorsFont;
    private GlyphLayout splashLayout;
    private BitmapFont gameOverFont;
    private BitmapFont gameOverButtonFont;
    private GlyphLayout gameOverLayout;

    // New for Victory Screen
    private BitmapFont victoryFont;
    private BitmapFont victoryButtonFont;
    private GlyphLayout victoryLayout;
    private int currentLevel = 0; // To track the current level for "Next Level" functionality
    private final int MAX_LEVEL = 3; // Define your maximum level here

    // Music variables
    private Music menuMusic;
    private Music gameMusic;
    private Music winningMusic;
    private Music gameOverMusic;
    private GameState lastPlayedMusicState = null;

    OrthographicCamera guiCamera;
    Texture background;
    Texture guiDA;
    Texture guiDI;
    Texture guiRA;
    Texture guiRI;
    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    FitViewport viewport;
    Texture[] tileTextures;
    int[][] tileMap;
    int mapWidth = 0;
    int mapHeight = 0;
    float tileSize = 1f;
    float normalSpeed = 4f;
    float dodgeSpeed = 8f;
    float speed = normalSpeed;
    float posX = 0, posY = 0;
    String currentDirection = "down";
    float aniTimer = 0f;
    TextureRegion currentFrame;
    boolean isDodging = false;
    boolean isAttack = false;
    float dodgeDuration = 0.25f;
    float dodgeCooldown = 2f;
    float dodgeTimer = 0f;
    float cooldownTimer = 1f;
    float attackTimer = 0f;
    float attackDuration = 0.3f;
    float attackCooldown = 1f;
    int maxHealth = 100;
    int currentHealth = 100;
    float damageCooldown = 1f;
    float damageTimer = 0f;
    float stateTime = 0f;
    ArrayList<Enemy> enemies;
    boolean isGameOver = false;

    // --- New Variables for Score and Pause ---
    private int score = 0;
    private BitmapFont scoreFont;
    private GlyphLayout scoreLayout;
    private boolean isPaused = false;
    private Texture pauseTexture;
    private float pauseButtonX;
    private float pauseButtonY;
    private float pauseButtonWidth = 50; // Example size
    private float pauseButtonHeight = 50; // Example size
    private float pauseBtnWidth = 200; // Width for resume/menu buttons
    private float pauseBtnHeight = 70; // Height for resume/menu buttons
    private float resumeBtnX, resumeBtnY;
    private float menuBtnX, menuBtnY;

    // Pause Menu Fonts (fixed)
    private BitmapFont pauseTitleFont;
    private BitmapFont pauseButtonFont;
    private GlyphLayout pauseTextLayout;
    // --- End New Variables ---

    @Override
    public void create() {
        viewport = new FitViewport(20, 12);
        guiCamera = new OrthographicCamera();
        guiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Assets.load();
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        mainMenu = new MainMenu();
        splashTexture = new Texture(Gdx.files.internal("background/splash.jpg"));

        splashTitleFont = new BitmapFont();
        splashTitleFont.getData().setScale(3.5f);

        splashAuthorsFont = new BitmapFont();
        splashAuthorsFont.getData().setScale(1.5f);

        splashLayout = new GlyphLayout();

        gameOverFont = new BitmapFont();
        gameOverFont.getData().setScale(4f);
        gameOverFont.setColor(Color.RED);

        gameOverButtonFont = new BitmapFont();
        gameOverButtonFont.getData().setScale(2f);
        gameOverButtonFont.setColor(Color.WHITE);

        gameOverLayout = new GlyphLayout();

        // New for Victory Screen
        victoryFont = new BitmapFont();
        victoryFont.getData().setScale(4f);
        victoryFont.setColor(Color.GREEN);

        victoryButtonFont = new BitmapFont();
        victoryButtonFont.getData().setScale(2f);
        victoryButtonFont.setColor(Color.WHITE);

        victoryLayout = new GlyphLayout();

        // Music
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/menumusic.mp3"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/Gamebackgroundmusic.mp3"));
        winningMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/Winningmusic.mp3"));
        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/Gameovermusic.mp3"));

        // Set looping for background music
        menuMusic.setLooping(true);
        gameMusic.setLooping(true);

        // --- New for Score and Pause ---
        scoreFont = new BitmapFont();
        // Adjusted scale for larger score display
        scoreFont.getData().setScale(2.5f); // <-- Changed this line for larger score!
        scoreFont.setColor(Color.WHITE);
        scoreLayout = new GlyphLayout();

        pauseTexture = new Texture("GUI/pause_button.png"); // You'll need to create this texture
        // Position the pause button in the top-right
        pauseButtonX = Gdx.graphics.getWidth() - pauseButtonWidth - 10;
        pauseButtonY = Gdx.graphics.getHeight() - pauseButtonHeight - 10;

        // Calculate pause menu button positions (centered)
        pauseBtnWidth = 200;
        pauseBtnHeight = 70;
        resumeBtnX = (Gdx.graphics.getWidth() - pauseBtnWidth) / 2f;
        resumeBtnY = (Gdx.graphics.getHeight() + pauseBtnHeight) / 2f;
        menuBtnX = (Gdx.graphics.getWidth() - pauseBtnWidth) / 2f;
        menuBtnY = resumeBtnY - pauseBtnHeight - 20; // 20 pixels spacing

        // Pause Menu Fonts (initialization)
        pauseTitleFont = new BitmapFont();
        pauseTitleFont.getData().setScale(4f);
        pauseTitleFont.setColor(Color.YELLOW);

        pauseButtonFont = new BitmapFont();
        pauseButtonFont.getData().setScale(2.5f);
        pauseButtonFont.setColor(Color.WHITE);

        pauseTextLayout = new GlyphLayout();
        // --- End New for Score and Pause ---
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        guiCamera.setToOrtho(false, width, height);

        // Update pause button position on resize
        pauseButtonX = Gdx.graphics.getWidth() - pauseButtonWidth - 10;
        pauseButtonY = Gdx.graphics.getHeight() - pauseButtonHeight - 10;
        resumeBtnX = (Gdx.graphics.getWidth() - pauseBtnWidth) / 2f;
        resumeBtnY = (Gdx.graphics.getHeight() + pauseBtnHeight) / 2f;
        menuBtnX = (Gdx.graphics.getWidth() - pauseBtnWidth) / 2f;
        menuBtnY = resumeBtnY - pauseBtnHeight - 20;
    }

    @Override
    public void render() {
        stateTime += Gdx.graphics.getDeltaTime();

        // Music State Management
        if (soundEnabled) {
            if (gameState != lastPlayedMusicState) {
                // Stop all currently playing music
                if (menuMusic.isPlaying()) menuMusic.stop();
                if (gameMusic.isPlaying()) gameMusic.stop();
                if (winningMusic.isPlaying()) winningMusic.stop();
                if (gameOverMusic.isPlaying()) gameOverMusic.stop();

                // Play music based on current state
                switch (gameState) {
                    case MENU:
                        menuMusic.play();
                        break;
                    case GAME:
                        gameMusic.play();
                        break;
                    case VICTORY:
                        winningMusic.play();
                        break;
                    case GAMEOVER:
                        gameOverMusic.play();
                        break;
                }
                lastPlayedMusicState = gameState;
            }
        } else {
            // If sound is disabled, stop all music
            if (menuMusic.isPlaying()) menuMusic.stop();
            if (gameMusic.isPlaying()) gameMusic.stop();
            if (winningMusic.isPlaying()) winningMusic.stop();
            if (gameOverMusic.isPlaying()) gameOverMusic.stop();
            lastPlayedMusicState = null;
        }

        if (gameState == GameState.SPLASH) {
            splashTimer += Gdx.graphics.getDeltaTime();
            ScreenUtils.clear(Color.BLACK);
            spriteBatch.setProjectionMatrix(guiCamera.combined);
            spriteBatch.begin();

            float alpha = 1.0f;
            if (splashTimer > SPLASH_DURATION - FADE_DURATION) {
                alpha = 1.0f - ((splashTimer - (SPLASH_DURATION - FADE_DURATION)) / FADE_DURATION);
            }
            spriteBatch.setColor(1, 1, 1, alpha);
            spriteBatch.draw(splashTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.setColor(1, 1, 1, 1);

            splashLayout.setText(splashTitleFont, "Blood Crypt");
            float titleX = (Gdx.graphics.getWidth() - splashLayout.width) / 2f;
            float titleY = Gdx.graphics.getHeight() * 0.7f;

            splashTitleFont.setColor(0, 0, 0, alpha * 0.7f);
            splashTitleFont.draw(spriteBatch, splashLayout, titleX + 3, titleY - 3);

            splashTitleFont.setColor(1, 1, 1, alpha);
            splashTitleFont.draw(spriteBatch, splashLayout, titleX, titleY);

            String authorsText = "By: Maddox Ganesh, Taketoshi Miyamoto, Daniyal Ahmad, and Carlos Liu";
            splashLayout.setText(splashAuthorsFont, authorsText);
            float authorsX = (Gdx.graphics.getWidth() - splashLayout.width) / 2f;
            float authorsY = titleY - splashLayout.height - 60;

            splashAuthorsFont.setColor(0, 0, 0, alpha * 0.7f);
            splashAuthorsFont.draw(spriteBatch, splashLayout, authorsX + 2, authorsY - 2);

            splashAuthorsFont.setColor(0.9f, 0.9f, 0.9f, alpha);
            splashAuthorsFont.draw(spriteBatch, splashLayout, authorsX, authorsY);

            spriteBatch.end();

            if (splashTimer >= SPLASH_DURATION) {
                gameState = GameState.MENU;
            }

            return;
        }

        if (gameState == GameState.MENU) {
            mainMenu.update();
            mainMenu.draw();
            if (mainMenu.selectedLevel > 0) {
                startGame(mainMenu.selectedLevel);
                gameState = GameState.GAME;
            }
            return;
        }

        if (gameState == GameState.GAMEOVER) {
            drawGameOverScreen();
            handleGameOverInput();
            return;
        }

        if (gameState == GameState.VICTORY) {
            drawVictoryScreen();
            handleVictoryInput();
            return;
        }

        input();
        logic();
        draw();
    }

    private void startGame(int levelNum) {
        currentLevel = levelNum;
        // Load different maps for each level if you want, e.g.:
        String mapFile = "levels/level" + levelNum + ".csv";
        background = new Texture("background/field.jpg");
        guiDA = new Texture("GUI/dactive.png");
        guiDI = new Texture("GUI/dinactive.png");
        guiRA = new Texture("GUI/ractive.png");
        guiRI = new Texture("GUI/rinactive.png");
        tileTextures = new Texture[14];
        for (int i = 0; i <= 13; i++) {
            tileTextures[i] = new Texture("tiles/" + i + ".png");
        }
        tileMap = MapLoader.loadCsv(mapFile);
        mapHeight = tileMap.length;
        mapWidth = tileMap[0].length;

        // --- NEW: Replace coins (tile ID 9) with floor (tile ID 0) ---
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                if (tileMap[y][x] == 9) { // If it's a coin tile
                    tileMap[y][x] = 0;    // Change it to a normal floor tile (assuming 0.png is your floor)
                }
            }
        }
        // --- END NEW ---

        int[] spawn = MapLoader.findTile(tileMap, 11);
        if (spawn != null) {
            posX = spawn[0] * tileSize;
            posY = spawn[1] * tileSize;
            tileMap[spawn[1]][spawn[0]] = 0;
        } else {
            posX = 1; posY = 1;
        }
        enemies = new ArrayList<>();
        ArrayList<int[]> enemyTiles12 = MapLoader.findAllTiles(tileMap, 12);
        for (int[] tile : enemyTiles12) {
            float worldX = tile[0] + 0.5f;
            float worldY = tile[1] + 0.5f;
            enemies.add(new Enemy(worldX, worldY, 1));
            tileMap[tile[1]][tile[0]] = 0;
        }
        ArrayList<int[]> enemyTiles13 = MapLoader.findAllTiles(tileMap, 13);
        for (int[] tile : enemyTiles13) {
            float worldX = tile[0] + 0.5f;
            float worldY = tile[1] + 0.5f;
            enemies.add(new Enemy(worldX, worldY, 2));
            tileMap[tile[1]][tile[0]] = 0;
        }

        maxHealth = 100;
        currentHealth = 100;
        isGameOver = false;
        isPaused = false; // Ensure game isn't paused at start
        score = 0; // Reset score for new level
        mainMenu.selectedLevel = 0; // Reset so you can return to menu and pick again
    }

    private void input() {
        if (isGameOver) return;

        // Toggle pause with ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            return;
        }

        // --- Handle pause button click ---
        if (Gdx.input.justTouched()) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Pause button (top-right)
            if (mouseX >= pauseButtonX && mouseX <= pauseButtonX + pauseButtonWidth &&
                mouseY >= pauseButtonY && mouseY <= pauseButtonY + pauseButtonHeight) {
                isPaused = !isPaused;
                return;
            }

            // Buttons in pause menu
            if (isPaused) {
                // Resume
                if (mouseX >= resumeBtnX && mouseX <= resumeBtnX + pauseBtnWidth &&
                    mouseY >= resumeBtnY && mouseY <= resumeBtnY + pauseBtnHeight) {
                    isPaused = false;
                    return;
                }

                // Main Menu
                if (mouseX >= menuBtnX && mouseX <= menuBtnX + pauseBtnWidth &&
                    mouseY >= menuBtnY && mouseY <= menuBtnY + pauseBtnHeight) {
                    isPaused = false;
                    gameState = GameState.MENU;
                    currentHealth = maxHealth;
                    enemies.clear();
                    mainMenu.selectedLevel = 0;
                    score = 0; // Reset score when returning to main menu
                    return;
                }
            }
        }

        if (isPaused) return; // Stop processing other input if paused

        float delta = Gdx.graphics.getDeltaTime();
        float moveSpeed = speed * delta;
        float newX = posX;
        float newY = posY;
        boolean isMoving = false;

        // Attack input
        if (Gdx.input.isKeyJustPressed(Input.Keys.C) && attackTimer <= 0 && !isAttack) {
            isAttack = true;
            attackTimer = attackCooldown;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            currentDirection = "up";
            newY += moveSpeed;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            currentDirection = "down";
            newY -= moveSpeed;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            currentDirection = "left";
            newX -= moveSpeed;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            currentDirection = "right";
            newX += moveSpeed;
            isMoving = true;
        }
        // Collision check
        if (!isWallTile(newX, newY)) {
            posX = newX;
            posY = newY;
        }

        if (isAttack) {
            switch (currentDirection) {
                case "up": currentFrame = Assets.slashUP.getKeyFrame(stateTime, true); break;
                case "down": currentFrame = Assets.slashDOWN.getKeyFrame(stateTime, true); break;
                case "left": currentFrame = Assets.slashLEFT.getKeyFrame(stateTime, true); break;
                case "right": currentFrame = Assets.slashRIGHT.getKeyFrame(stateTime, true); break;
            }
        }
        // Animation based on movement state
        else if (isMoving) {
            switch (currentDirection) {
                case "up": currentFrame = Assets.walkUP.getKeyFrame(stateTime, true); break;
                case "down": currentFrame = Assets.walkDOWN.getKeyFrame(stateTime, true); break;
                case "left": currentFrame = Assets.walkLEFT.getKeyFrame(stateTime, true); break;
                case "right": currentFrame = Assets.walkRIGHT.getKeyFrame(stateTime, true); break;
            }
        } else {
            switch (currentDirection) {
                case "up":
                    currentFrame = Assets.idleUP.getKeyFrame(stateTime, true);
                    break;
                case "down":
                    currentFrame = Assets.idleDOWN.getKeyFrame(stateTime, true);
                    break;
                case "left":
                    currentFrame = Assets.idleLEFT.getKeyFrame(stateTime, true);
                    break;
                case "right":
                    currentFrame = Assets.idleRIGHT.getKeyFrame(stateTime, true);
                    break;
            }
        }
        // Dodge input
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && cooldownTimer <= 0 && !isDodging) {
            isDodging = true;
            speed = dodgeSpeed;
            dodgeTimer = dodgeDuration;
            cooldownTimer = dodgeCooldown;
        }
    }

    private boolean isWallTile(float x, float y) {
        float playerWidth = 1.6f;
        float playerHeight = 1.6f;

        return isSolidTileAt(x, y) ||
            isSolidTileAt(x + playerWidth, y) ||
            isSolidTileAt(x, y + playerHeight) ||
            isSolidTileAt(x + playerWidth, y + playerHeight);
    }

    private boolean isSolidTileAt(float x, float y) {
        int tileX = (int) Math.floor(x);
        int tileY = (int) Math.floor(y);

        if (tileX < 0 || tileX >= mapWidth || tileY < 0 || tileY >= mapHeight) {
            return true;
        }

        return tileMap[tileY][tileX] == 7;
    }

    private void logic() {
        if (isGameOver) return;
        if (isPaused) return; // Stop logic updates if paused

        posX = MathUtils.clamp(posX, 0, mapWidth - 1);
        posY = MathUtils.clamp(posY, 0, mapHeight - 1);
        float delta = Gdx.graphics.getDeltaTime();
        damageTimer -= delta;

        if (isDodging) {
            dodgeTimer -= delta;
            if (dodgeTimer <= 0) {
                isDodging = false;
                speed = normalSpeed;
            }
        }

        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
        }

        if (isAttack) {
            attackTimer -= Gdx.graphics.getDeltaTime();

            // Only do damage once per attack
            if (attackTimer > attackCooldown - attackDuration) {
                float attackRadius = 1.0f; // 1-tile radius
                float playerCenterX = posX + 0.75f;
                float playerCenterY = posY + 0.75f;

                // Enemy collision for attack (1 line of code for removal based on condition)
                enemies.removeIf(enemy -> {
                    float dx = enemy.x - playerCenterX;
                    float dy = enemy.y - playerCenterY;
                    boolean hit = dx * dx + dy * dy <= attackRadius * attackRadius;
                    if (hit) {
                        score += 100; // Increase score when enemy is hit
                    }
                    return hit;
                });
            }

            if (attackTimer <= 0f) {
                isAttack = false;
            }
        }

        float playerCenterX = posX + 0.75f;
        float playerCenterY = posY + 0.75f;
        for (Enemy enemy : enemies) {
            enemy.update(playerCenterX, playerCenterY, delta, tileMap, mapWidth, mapHeight);
            float dx = playerCenterX - enemy.x;
            float dy = playerCenterY - enemy.y;
            if (dx * dx + dy * dy < 0.5f && damageTimer <= 0) {
                currentHealth = Math.max(0, currentHealth - 10);
                damageTimer = damageCooldown;
            }
        }
        if (currentHealth <= 0) {
            gameState = GameState.GAMEOVER;
            isGameOver = true;
            currentHealth = 0;
        }

        if (enemies.isEmpty() && gameState == GameState.GAME) {
            gameState = GameState.VICTORY;
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        if (isGameOver) {
            // This handles the game over screen directly from draw()
        }

        viewport.getCamera().position.set(posX, posY, 0);
        viewport.getCamera().update();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        renderTileMap();
        spriteBatch.draw(currentFrame, posX, posY, 1.6f, 1.6f);

        // --- CORRECTED ENEMY DRAWING LOGIC ---
        for (Enemy enemy : enemies) {
            int textureIndex = enemy.type == 2 ? 13 : 12; // Use 13 for type 2, 12 for type 1
            Texture enemyTexture = tileTextures[textureIndex];
            spriteBatch.draw(enemyTexture, enemy.x - 0.5f, enemy.y - 0.5f, 1f, 1f);
        }
        // --- END CORRECTED ENEMY DRAWING LOGIC ---

        spriteBatch.end();

        spriteBatch.setProjectionMatrix(guiCamera.combined);
        spriteBatch.begin();
        spriteBatch.draw(attackTimer > 0 ? guiDI : guiDA, 10, 10, 100, 100);
        spriteBatch.draw(cooldownTimer > 0 ? guiRI : guiRA, 120, 10, 100, 100);
        spriteBatch.end();

        shapeRenderer.setProjectionMatrix(guiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(10, Gdx.graphics.getHeight() - 30, 200, 20);
        shapeRenderer.setColor(Color.GREEN);
        float healthWidth = 200 * ((float) currentHealth / maxHealth);
        shapeRenderer.rect(10, Gdx.graphics.getHeight() - 30, healthWidth, 20);
        shapeRenderer.end();

        // Draw score text
        // Move score text to bottom-right, shifted left and no background
        int boxWidth = 150;
        int boxHeight = 50;
        int padding = 10;
        int boxX = Gdx.graphics.getWidth() - boxWidth - padding - 50; // Moved 50 pixels left
        int boxY = padding;

        spriteBatch.begin();
        String scoreText = "Score: " + score;
        scoreLayout.setText(scoreFont, scoreText);
        float scoreX = boxX + (boxWidth - scoreLayout.width) / 2f;
        float scoreY = boxY + (boxHeight + scoreLayout.height) / 2f;
        scoreFont.draw(spriteBatch, scoreLayout, scoreX, scoreY);
        spriteBatch.end();

        // Draw pause button image
        spriteBatch.begin();
        spriteBatch.draw(pauseTexture, pauseButtonX, pauseButtonY, pauseButtonWidth, pauseButtonHeight);
        spriteBatch.end();

        if (isPaused) {
            // Dim the screen
            shapeRenderer.setProjectionMatrix(guiCamera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.5f); // 0.5f alpha for transparency
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();

            // Draw yellow "PAUSED" title
            spriteBatch.begin();
            // Use the class-level font
            pauseTextLayout.setText(pauseTitleFont, "PAUSED");
            float titleX = (Gdx.graphics.getWidth() - pauseTextLayout.width) / 2f;
            float titleY = Gdx.graphics.getHeight() * 0.75f;
            pauseTitleFont.draw(spriteBatch, pauseTextLayout, titleX, titleY);

            // Draw pause menu buttons with correct colors
            spriteBatch.end(); // End batch to use shapeRenderer
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(resumeBtnX, resumeBtnY, pauseBtnWidth, pauseBtnHeight);

            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.rect(menuBtnX, menuBtnY, pauseBtnWidth, pauseBtnHeight);

            shapeRenderer.end();

            // Draw button labels
            spriteBatch.begin();
            // Use the class-level font
            pauseTextLayout.setText(pauseButtonFont, "Resume");
            float resumeTextX = resumeBtnX + (pauseBtnWidth - pauseTextLayout.width) / 2f;
            float resumeTextY = resumeBtnY + (pauseBtnHeight + pauseTextLayout.height) / 2f;
            pauseButtonFont.draw(spriteBatch, pauseTextLayout, resumeTextX, resumeTextY);

            pauseTextLayout.setText(pauseButtonFont, "Main Menu");
            float menuTextX = menuBtnX + (pauseBtnWidth - pauseTextLayout.width) / 2f;
            float menuTextY = menuBtnY + (pauseBtnHeight + pauseTextLayout.height) / 2f;
            pauseButtonFont.draw(spriteBatch, pauseTextLayout, menuTextX, menuTextY);

            spriteBatch.end();
        }
    }

    private void drawGameOverScreen() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(guiCamera.combined);
        spriteBatch.begin();

        gameOverLayout.setText(gameOverFont, "GAME OVER");
        float gameOverX = (Gdx.graphics.getWidth() - gameOverLayout.width) / 2f;
        float gameOverY = Gdx.graphics.getHeight() / 2f + gameOverLayout.height / 2f + 100;
        gameOverFont.draw(spriteBatch, gameOverLayout, gameOverX, gameOverY);

        float buttonWidth = 250;
        float buttonHeight = 60;
        float buttonSpacing = 20;
        float centerX = Gdx.graphics.getWidth() / 2f;
        float startY = Gdx.graphics.getHeight() / 2f - buttonHeight / 2f - 50;

        float mainMenuButtonX = centerX - buttonWidth / 2f;
        float mainMenuButtonY = startY;
        spriteBatch.end();

        shapeRenderer.setProjectionMatrix(guiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(mainMenuButtonX, mainMenuButtonY, buttonWidth, buttonHeight);
        shapeRenderer.end();

        spriteBatch.begin();
        gameOverLayout.setText(gameOverButtonFont, "MAIN MENU");
        gameOverButtonFont.draw(spriteBatch, gameOverLayout,
            mainMenuButtonX + (buttonWidth - gameOverLayout.width) / 2,
            mainMenuButtonY + (buttonHeight + gameOverLayout.height) / 2);

        float exitButtonX = centerX - buttonWidth / 2f;
        float exitButtonY = startY - (buttonHeight + buttonSpacing);
        spriteBatch.end();

        shapeRenderer.setProjectionMatrix(guiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(exitButtonX, exitButtonY, buttonWidth, buttonHeight);
        shapeRenderer.end();

        spriteBatch.begin();
        gameOverLayout.setText(gameOverButtonFont, "EXIT");
        gameOverButtonFont.draw(spriteBatch, gameOverLayout,
            exitButtonX + (buttonWidth - gameOverLayout.width) / 2,
            exitButtonY + (buttonHeight + gameOverLayout.height) / 2);
        spriteBatch.end();
    }

    private void handleGameOverInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            float buttonWidth = 250;
            float buttonHeight = 60;
            float buttonSpacing = 20;
            float centerX = Gdx.graphics.getWidth() / 2f;
            float startY = Gdx.graphics.getHeight() / 2f - buttonHeight / 2f - 50;

            float mainMenuButtonX = centerX - buttonWidth / 2f;
            float mainMenuButtonY = startY;
            float exitButtonX = centerX - buttonWidth / 2f;
            float exitButtonY = startY - (buttonHeight + buttonSpacing);

            if (mouseX >= mainMenuButtonX && mouseX <= mainMenuButtonX + buttonWidth &&
                mouseY >= mainMenuButtonY && mouseY <= mainMenuButtonY + buttonHeight) {
                gameState = GameState.MENU;
                currentHealth = maxHealth;
                enemies.clear();
                posX = 1;
                posY = 1;
                isGameOver = false;
                score = 0; // Reset score on game over and return to main menu
            } else if (mouseX >= exitButtonX && mouseX <= exitButtonX + buttonWidth &&
                mouseY >= exitButtonY && mouseY <= exitButtonY + buttonHeight) {
                Gdx.app.exit();
            }
        }
    }

    private void drawVictoryScreen() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(guiCamera.combined);
        spriteBatch.begin();

        String victoryMessage;
        if (currentLevel == MAX_LEVEL) {
            victoryMessage = "YOU BEAT THE GAME!";
            victoryFont.setColor(Color.GOLD); // Maybe change color for final victory
        } else {
            victoryMessage = "LEVEL COMPLETE!";
            victoryFont.setColor(Color.GREEN);
        }

        victoryLayout.setText(victoryFont, victoryMessage);
        float victoryX = (Gdx.graphics.getWidth() - victoryLayout.width) / 2f;
        float victoryY = Gdx.graphics.getHeight() / 2f + victoryLayout.height / 2f + 100;
        victoryFont.draw(spriteBatch, victoryLayout, victoryX, victoryY);

        float buttonWidth = 250;
        float buttonHeight = 60;
        float buttonSpacing = 20;
        float centerX = Gdx.graphics.getWidth() / 2f;
        float startY = Gdx.graphics.getHeight() / 2f - buttonHeight / 2f - 50;

        float nextLevelButtonX = centerX - buttonWidth / 2f;
        float nextLevelButtonY = startY;

        float mainMenuButtonX = centerX - buttonWidth / 2f;
        float mainMenuButtonY;

        if (currentLevel == MAX_LEVEL) {
            // If it's the last level, Main Menu button takes the top position
            mainMenuButtonY = startY;
            nextLevelButtonY = -9999; // Move off-screen to hide
        } else {
            mainMenuButtonY = startY - (buttonHeight + buttonSpacing);
        }

        spriteBatch.end();

        shapeRenderer.setProjectionMatrix(guiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw Next Level button only if not on max level
        if (currentLevel < MAX_LEVEL) {
            shapeRenderer.setColor(Color.LIME);
            shapeRenderer.rect(nextLevelButtonX, nextLevelButtonY, buttonWidth, buttonHeight);
        }

        // Draw Main Menu button
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(mainMenuButtonX, mainMenuButtonY, buttonWidth, buttonHeight);
        shapeRenderer.end();

        spriteBatch.begin();
        if (currentLevel < MAX_LEVEL) {
            victoryLayout.setText(victoryButtonFont, "NEXT LEVEL");
            victoryButtonFont.draw(spriteBatch, victoryLayout,
                nextLevelButtonX + (buttonWidth - victoryLayout.width) / 2,
                nextLevelButtonY + (buttonHeight + victoryLayout.height) / 2);
        }

        victoryLayout.setText(victoryButtonFont, "MAIN MENU");
        victoryButtonFont.draw(spriteBatch, victoryLayout,
            mainMenuButtonX + (buttonWidth - victoryLayout.width) / 2,
            mainMenuButtonY + (buttonHeight + victoryLayout.height) / 2);
        spriteBatch.end();
    }

    private void handleVictoryInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            float buttonWidth = 250;
            float buttonHeight = 60;
            float buttonSpacing = 20;
            float centerX = Gdx.graphics.getWidth() / 2f;
            float startY = Gdx.graphics.getHeight() / 2f - buttonHeight / 2f - 50;

            float nextLevelButtonX = centerX - buttonWidth / 2f;
            float nextLevelButtonY = startY;

            float mainMenuButtonX = centerX - buttonWidth / 2f;
            float mainMenuButtonY;

            if (currentLevel == MAX_LEVEL) {
                mainMenuButtonY = startY;
            } else {
                mainMenuButtonY = startY - (buttonHeight + buttonSpacing);
            }

            // Handle "NEXT LEVEL" or "MAIN MENU" if it's the last level
            if (currentLevel < MAX_LEVEL) {
                if (mouseX >= nextLevelButtonX && mouseX <= nextLevelButtonX + buttonWidth &&
                    mouseY >= nextLevelButtonY && mouseY <= nextLevelButtonY + buttonHeight) {
                    currentLevel++;
                    startGame(currentLevel);
                    gameState = GameState.GAME;
                }
            }

            // Handle "MAIN MENU" button click
            if (mouseX >= mainMenuButtonX && mouseX <= mainMenuButtonX + buttonWidth &&
                mouseY >= mainMenuButtonY && mouseY <= mainMenuButtonY + buttonHeight) {
                gameState = GameState.MENU;
                currentLevel = 0;
                currentHealth = maxHealth;
                enemies.clear();
                posX = 1;
                posY = 1;
                score = 0; // Reset score when returning to main menu
            }
        }
    }

    private void renderTileMap() {
        int tilesX = (int) viewport.getWorldWidth() + 2;
        int tilesY = (int) viewport.getWorldHeight() + 2;

        int centerX = (int) (posX + 0.5f);
        int centerY = (int) (posY + 0.5f);

        int startX = Math.max(0, centerX - tilesX / 2);
        int endX = Math.min(mapWidth, centerX + tilesX / 2);
        int startY = Math.max(0, centerY - tilesY / 2);
        int endY = Math.min(mapHeight, centerY + tilesY / 2);

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                int tileId = tileMap[y][x];
                if (tileId >= 0 && tileId < tileTextures.length && tileTextures[tileId] != null) {
                    spriteBatch.draw(tileTextures[tileId], x, y, 1, 1);
                }
            }
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {
        for (Texture texture : tileTextures) {
            if (texture != null) texture.dispose();
        }
        if (background != null) background.dispose();
        if (guiDA != null) guiDA.dispose();
        if (guiDI != null) guiDI.dispose();
        if (guiRA != null) guiRA.dispose();
        if (guiRI != null) guiRI.dispose();
        if (splashTexture != null) splashTexture.dispose();
        if (pauseTexture != null) pauseTexture.dispose(); // Dispose pause texture

        if (splashTitleFont != null) splashTitleFont.dispose();
        if (splashAuthorsFont != null) splashAuthorsFont.dispose();
        if (gameOverFont != null) gameOverFont.dispose();
        if (gameOverButtonFont != null) gameOverButtonFont.dispose();
        if (victoryFont != null) victoryFont.dispose();
        if (victoryButtonFont != null) victoryButtonFont.dispose();
        if (scoreFont != null) scoreFont.dispose(); // Dispose score font

        // Dispose pause menu fonts (fixed)
        if (pauseTitleFont != null) pauseTitleFont.dispose();
        if (pauseButtonFont != null) pauseButtonFont.dispose();

        if (spriteBatch != null) spriteBatch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();

        if (menuMusic != null) menuMusic.dispose();
        if (gameMusic != null) gameMusic.dispose();
        if (winningMusic != null) winningMusic.dispose();
        if (gameOverMusic != null) gameOverMusic.dispose();
    }
}
