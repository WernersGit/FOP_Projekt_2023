package projekt.gui.scene;

import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.rating.RatingCriteria;
import projekt.gui.controller.RaterSceneController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RaterScene extends MenuScene<RaterSceneController> {
    private Map<RatingCriteria, Double> result;

    public RaterScene() {
        super(new RaterSceneController(), "Simulation Score", "projekt/gui/raterStyle.css");
    }

    public void init(List<ProblemArchetype> problems, Map<RatingCriteria, Double> result) {
        this.result = result;
        super.init(problems);
    }

    @Override
    public void initComponents() {
        //TODO H11.3
    }

    @Override
    public void initReturnButton() {
        returnButton.setOnAction(e -> {
            MainMenuScene scene = (MainMenuScene) SceneSwitcher.loadScene(SceneSwitcher.SceneType.MAIN_MENU, getController().getStage());
            scene.init(new ArrayList<>(problems));
        });
    }

    @Override
    public RaterSceneController getController() {
        return controller;
    }
}
