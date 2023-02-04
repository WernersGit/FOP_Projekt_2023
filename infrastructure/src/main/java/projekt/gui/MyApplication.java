package projekt.gui;

import javafx.stage.Stage;
import projekt.gui.scene.MainMenuScene;
import projekt.gui.scene.SceneSwitcher;

public class MyApplication extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(600);
        MainMenuScene scene = (MainMenuScene) SceneSwitcher.loadScene(SceneSwitcher.SceneType.MAIN_MENU, primaryStage);
        scene.init();
    }
}
