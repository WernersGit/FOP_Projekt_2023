package projekt.gui.controller;

import javafx.application.Platform;

public abstract class MenuSceneController extends SceneController {

    /**
     * Called when the user clicks the "Quit" button.
     */
    public void quit() {
        Platform.exit();
    }
}
