package com.badlogic.bloodcrypt;




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;




public class MainMenu {




    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private SpriteBatch batch;
    private Texture backgroundTexture;




    public boolean showLevelSelect = false;
    public int selectedLevel = 0; // 0 = none, 1/2/3 = selected




    private boolean showOptions = false;
    public boolean showTutorial = false;




    private final float buttonWidth = 300;
    private final float buttonHeight = 70;
    private final float buttonSpacing = 40;
    private GlyphLayout layout;
    private final int SOUND_BUTTON_INDEX = 4;
    private final int TUTORIAL_BUTTON_INDEX = 9;




    public MainMenu() {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.8f);
        batch = new SpriteBatch();
        layout = new GlyphLayout();
        backgroundTexture = new Texture(Gdx.files.internal("background/mainmenu.jpg"));
    }




    public void update() {
        float[][] btns = getButtonPositions();
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();




            if (showLevelSelect) {
                if (isInButton(mouseX, mouseY, btns[5])) {
                    selectedLevel = 1;
                } else if (isInButton(mouseX, mouseY, btns[6])) {
                    selectedLevel = 2;
                } else if (isInButton(mouseX, mouseY, btns[7])) {
                    selectedLevel = 3;
                } else if (isInButton(mouseX, mouseY, btns[8])) {
                    showLevelSelect = false;
                }
            } else if (showTutorial) {
                if (isInButton(mouseX, mouseY, btns[10])) {
                    showTutorial = false;
                }
            }
            else if (!showOptions) {
                if (isInButton(mouseX, mouseY, btns[0])) {
                    showLevelSelect = true;
                } else if (isInButton(mouseX, mouseY, btns[1])) {
                    showOptions = true;
                } else if (isInButton(mouseX, mouseY, btns[2])) {
                    Gdx.app.exit();
                }
            } else {
                if (isInButton(mouseX, mouseY, btns[3])) {
                    showOptions = false;
                } else if (isInButton(mouseX, mouseY, btns[SOUND_BUTTON_INDEX])) {
                    Main.soundEnabled = !Main.soundEnabled;
                } else if (isInButton(mouseX, mouseY, btns[TUTORIAL_BUTTON_INDEX])) {
                    showOptions = false;
                    showTutorial = true;
                }
            }
        }
    }




    public void draw() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float[][] btns = getButtonPositions();
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();




        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (showLevelSelect) {
            shapeRenderer.setColor(Color.ORANGE);
            shapeRenderer.rect(btns[5][0], btns[5][1], buttonWidth, buttonHeight);
            shapeRenderer.setColor(Color.ORANGE);
            shapeRenderer.rect(btns[6][0], btns[6][1], buttonWidth, buttonHeight);
            shapeRenderer.setColor(Color.ORANGE);
            shapeRenderer.rect(btns[7][0], btns[7][1], buttonWidth, buttonHeight);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(btns[8][0], btns[8][1], buttonWidth, buttonHeight);
        } else if (showTutorial) {
            // No specific buttons to draw in the shape renderer, text will be drawn later
            shapeRenderer.setColor(Color.YELLOW); // Back button for tutorial
            shapeRenderer.rect(btns[10][0], btns[10][1], buttonWidth, buttonHeight);
        } else if (!showOptions) {
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(btns[0][0], btns[0][1], buttonWidth, buttonHeight);
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.rect(btns[1][0], btns[1][1], buttonWidth, buttonHeight);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(btns[2][0], btns[2][1], buttonWidth, buttonHeight);
        } else {
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(btns[3][0], btns[3][1], buttonWidth, buttonHeight);
            shapeRenderer.setColor(Main.soundEnabled ? Color.GREEN : Color.GRAY);
            shapeRenderer.rect(btns[SOUND_BUTTON_INDEX][0], btns[SOUND_BUTTON_INDEX][1], buttonWidth, buttonHeight);
            shapeRenderer.setColor(Color.CYAN);
            shapeRenderer.rect(btns[TUTORIAL_BUTTON_INDEX][0], btns[TUTORIAL_BUTTON_INDEX][1], buttonWidth, buttonHeight);
        }
        shapeRenderer.end();




        batch.begin();
        if (showLevelSelect) {
            font.getData().setScale(3f);
            layout.setText(font, "SELECT LEVEL");
            float titleX = (Gdx.graphics.getWidth() - layout.width) / 2;
            float titleY = btns[5][1] + buttonHeight + 100;
            font.setColor(Color.WHITE);
            font.draw(batch, layout, titleX, titleY);
            font.getData().setScale(2f);
            drawCenteredText("Level 1", btns[5][0], btns[5][1], buttonWidth, buttonHeight);
            drawCenteredText("Level 2", btns[6][0], btns[6][1], buttonWidth, buttonHeight);
            drawCenteredText("Level 3", btns[7][0], btns[7][1], buttonWidth, buttonHeight);
            font.getData().setScale(1.5f);
            drawCenteredText("Back", btns[8][0], btns[8][1], buttonWidth, buttonHeight);
        } else if (showTutorial) {
            // Make title larger and move it up
            font.getData().setScale(3.5f); // Increased scale for title
            font.setColor(Color.WHITE);
            layout.setText(font, "User Guide");
            font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, Gdx.graphics.getHeight() - 50); // Moved title up




            // Increase font size for instructions and adjust vertical spacing
            font.getData().setScale(2f); // Increased scale for instructions
            font.setColor(Color.WHITE);
            float textStartX = Gdx.graphics.getWidth() / 4f;
            float textStartY = Gdx.graphics.getHeight() - 200; // Adjusted starting Y to move all text up
            float lineHeight = 60; // Increased line height for better spacing




            font.draw(batch, "UP arrow: Moves the character UP", textStartX, textStartY);
            font.draw(batch, "DOWN arrow: Moves the character DOWN", textStartX, textStartY - lineHeight);
            font.draw(batch, "LEFT arrow: Moves the character LEFT", textStartX, textStartY - 2 * lineHeight);
            font.draw(batch, "RIGHT arrow: Moves the character RIGHT", textStartX, textStartY - 3 * lineHeight);
            font.draw(batch, "Z: Dash", textStartX, textStartY - 4 * lineHeight);
            font.draw(batch, "C: Attack enemies", textStartX, textStartY - 5 * lineHeight);




            font.getData().setScale(1.5f);
            drawCenteredText("Back", btns[10][0], btns[10][1], buttonWidth, buttonHeight);
        }
        else if (!showOptions) {
            font.getData().setScale(4f);
            layout.setText(font, "BLOOD CRYPT");
            float titleX = (Gdx.graphics.getWidth() - layout.width) / 2;
            float titleY = btns[0][1] + buttonHeight + 100;
            font.setColor(Color.WHITE);
            font.draw(batch, layout, titleX, titleY);
            font.getData().setScale(2f);
            drawCenteredText("Levels", btns[0][0], btns[0][1], buttonWidth, buttonHeight);
            drawCenteredText("Options", btns[1][0], btns[1][1], buttonWidth, buttonHeight);
            drawCenteredText("Exit", btns[2][0], btns[2][1], buttonWidth, buttonHeight);
        } else {
            font.getData().setScale(2f);
            layout.setText(font, "OPTIONS");
            font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, btns[3][1] + buttonHeight + 90);
            font.getData().setScale(1.8f);
            drawCenteredText("Back", btns[3][0], btns[3][1], buttonWidth, buttonHeight);
            String soundText = "Sound: " + (Main.soundEnabled ? "ON" : "OFF");
            drawCenteredText(soundText, btns[SOUND_BUTTON_INDEX][0], btns[SOUND_BUTTON_INDEX][1], buttonWidth, buttonHeight);
            drawCenteredText("Tutorial", btns[TUTORIAL_BUTTON_INDEX][0], btns[TUTORIAL_BUTTON_INDEX][1], buttonWidth, buttonHeight);
            font.getData().setScale(1.2f);
            layout.setText(font, " ");
            font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, btns[3][1] - 80);
        }
        batch.end();
    }




    private void drawCenteredText(String text, float x, float y, float width, float height) {
        layout.setText(font, text);
        float textX = x + (width - layout.width) / 2;
        float textY = y + (height + layout.height) / 2;
        font.setColor(Color.WHITE);
        font.draw(batch, layout, textX, textY);
    }




    private boolean isInButton(float mx, float my, float[] btn) {
        return mx >= btn[0] && mx <= btn[0] + buttonWidth && my >= btn[1] && my <= btn[1] + buttonHeight;
    }




    // btns[0]: Levels, btns[1]: Options, btns[2]: Exit, btns[3]: Back (options), btns[4]: Sound (options)
    // btns[5]: Level 1, btns[6]: Level 2, btns[7]: Level 3, btns[8]: Back (level select)
    // btns[9]: Tutorial (options), btns[10]: Back (tutorial)
    private float[][] getButtonPositions() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float buttonX = (screenWidth - buttonWidth) / 2;
        float centerY = screenHeight / 2 + buttonHeight + buttonSpacing;
        float[][] btns = new float[11][2];
        btns[0] = new float[] { buttonX, centerY }; // Levels
        btns[1] = new float[] { buttonX, centerY - (buttonHeight + buttonSpacing) }; // Options
        btns[2] = new float[] { buttonX, centerY - 2 * (buttonHeight + buttonSpacing) }; // Exit
        btns[3] = new float[] { buttonX, screenHeight / 2 - buttonHeight / 2 }; // Back (options)
        btns[4] = new float[] { buttonX, screenHeight / 2 - buttonHeight / 2 - (buttonHeight + buttonSpacing) }; // Sound (options)
        // Level select positions
        float levelStartY = screenHeight / 2 + buttonHeight + buttonSpacing;
        btns[5] = new float[] { buttonX, levelStartY }; // Level 1
        btns[6] = new float[] { buttonX, levelStartY - (buttonHeight + buttonSpacing) }; // Level 2
        btns[7] = new float[] { buttonX, levelStartY - 2 * (buttonHeight + buttonSpacing) }; // Level 3
        btns[8] = new float[] { buttonX, levelStartY - 3 * (buttonHeight + buttonSpacing) }; // Back (level select)
        // Tutorial button position in options
        btns[9] = new float[] { buttonX, screenHeight / 2 - buttonHeight / 2 - 2 * (buttonHeight + buttonSpacing) }; // Tutorial (options)
        // Back button position for tutorial screen - moved down slightly for better visual balance
        btns[10] = new float[] { buttonX, buttonSpacing + 20 }; // Back (tutorial) - increased spacing from bottom
        return btns;
    }
}


