package projekt.gui.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A SceneController is responsible for dynamically managing a {@link Scene} and its {@link Stage}.
 */
public abstract class SceneController {
    // --Variables-- //

    /**
     * The {@link Stage} that is managed by this {@link SceneController}.
     */
    private Stage stage;

    /**
     * The {@link Stage} that is managed by this {@link SceneController}.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Specifies the title of the {@link Stage}.
     * This is used in {@link #initStage(Stage)} to set the title of the {@link Stage}.
     *
     * @return The title of the {@link Stage}.
     */
    public abstract String getTitle();

    // --Setup Methods-- //

    /**
     * Initializes the {@link Stage} of this {@link SceneController}.
     * This default implementation sets the title of the {@link Stage} to {@link #getTitle()}.
     *
     * @param stage The {@link Stage} to initialize.
     */
    public void initStage(final Stage stage) {
        (this.stage = stage).setTitle(getTitle());
    }
}
