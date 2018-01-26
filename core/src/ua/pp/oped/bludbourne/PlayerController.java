package ua.pp.oped.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vlad on 22.01.2018.
 */

public class PlayerController implements InputProcessor {

    private static final String TAG = PlayerController.class.getSimpleName();

    enum Keys {
        LEFT, RIGHT, UP, DOWN, QUIT
    }

    enum Mouse {
        SELECT, DOACTION
    }

    private static Map<Keys, Boolean> keys = new HashMap<Keys, Boolean>();
    private static Map<Mouse, Boolean> mouseButtons = new HashMap<Mouse, Boolean>();
    private Vector3 lastMouseCoordinates;

    static {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.UP, false);
        keys.put(Keys.DOWN, false);
        keys.put(Keys.QUIT, false);
        mouseButtons.put(Mouse.SELECT, false);
        mouseButtons.put(Mouse.DOACTION, false);
    }

    private Entity player;

    public PlayerController(Entity player) {
        lastMouseCoordinates = new Vector3();
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            leftPressed();
        }
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            rightPressed();
        }
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            upPressed();
        }
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            downPressed();
        }
        if (keycode == Input.Keys.Q) {
            quitPressed();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            leftReleased();
        }
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            rightReleased();
        }
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            upReleased();
        }
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            downReleased();
        }
        if (keycode == Input.Keys.Q) {
            quitReleased();
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
            setClickedMouseCoordinates(screenX, screenY);
        }
        if (button == Input.Buttons.LEFT) {
            selectMouseButtonPressed(screenX, screenY);
        }
        if (button == Input.Buttons.RIGHT) {
            doActionMouseButtonPressed(screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            selectMouseButtonReleased(screenX, screenY);
        }
        if (button == Input.Buttons.RIGHT) {
            doActionMouseButtonReleased(screenX, screenY);
        }
        return true;
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
    public boolean scrolled(int amount) {
        return false;
    }

    public void leftPressed() {
        keys.put(Keys.LEFT, true);
    }

    public void rightPressed() {
        keys.put(Keys.RIGHT, true);
    }

    public void upPressed() {
        keys.put(Keys.UP, true);
    }

    public void downPressed() {
        keys.put(Keys.DOWN, true);
    }

    public void quitPressed() {
        keys.put(Keys.QUIT, true);
    }

    public void setClickedMouseCoordinates(int x, int y) {
        lastMouseCoordinates.set(x, y, 0);
    }

    public void selectMouseButtonPressed(int x, int y) {
        mouseButtons.put(Mouse.SELECT, true);
    }

    public void doActionMouseButtonPressed(int x, int y) {
        mouseButtons.put(Mouse.DOACTION, true);
    }

    public void leftReleased() {
        keys.put(Keys.LEFT, true);
    }

    public void rightReleased() {
        keys.put(Keys.RIGHT, true);
    }

    public void upReleased() {
        keys.put(Keys.UP, true);
    }

    public void downReleased() {
        keys.put(Keys.DOWN, true);
    }

    public void quitReleased() {
        keys.put(Keys.QUIT, true);
    }

    public void selectMouseButtonReleased(int x, int y) {
        mouseButtons.put(Mouse.SELECT, true);
    }

    public void doActionMouseButtonReleased(int x, int y) {
        mouseButtons.put(Mouse.DOACTION, true);
    }

    public void update(float delta) {
        processInput(delta);
    }

    public static void hide() {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.UP, false);
        keys.put(Keys.DOWN, false);
        keys.put(Keys.QUIT, false);
    }

    private void processInput(float delta) {
        if (keys.get(Keys.LEFT)) {
            player.calculateNextPosition(Entity.Direction.LEFT, delta);
            player.setState(Entity.State.WALKING);
            player.setDirection(Entity.Direction.LEFT, delta);
        } else if (keys.get(Keys.RIGHT)) {
            player.calculateNextPosition(Entity.Direction.RIGHT, delta);
            player.setState(Entity.State.WALKING);
            player.setDirection(Entity.Direction.RIGHT, delta);
        } else if (keys.get(Keys.UP)) {
            player.calculateNextPosition(Entity.Direction.UP, delta);
            player.setState(Entity.State.WALKING);
            player.setDirection(Entity.Direction.UP, delta);
        } else if (keys.get(Keys.DOWN)) {
            player.calculateNextPosition(Entity.Direction.DOWN, delta);
            player.setState(Entity.State.WALKING);
            player.setDirection(Entity.Direction.DOWN, delta);
        } else if (keys.get(Keys.QUIT)) {
            Gdx.app.exit();
        }else{
            player.setState(Entity.State.IDLE);
        }
        if (mouseButtons.get(Mouse.SELECT)) {
            mouseButtons.put(Mouse.SELECT,false);
        }
    }

    public void dispose(){

    }
}
