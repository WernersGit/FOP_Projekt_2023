package projekt.gui.controller;

/**
 * An interface for a controlled scene.
 */
public interface ControlledScene<SC extends SceneController> {
    /**
     * Gets the {@link SceneController} which is responsible for controlling the scene.
     *
     * @return The {@link SceneController}.
     */
    SC getController();
}
