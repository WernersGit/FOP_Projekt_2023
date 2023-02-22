package projekt.gui.pane;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import projekt.delivery.archetype.ProblemArchetype;
import projekt.delivery.simulation.Simulation;
import projekt.delivery.simulation.SimulationConfig;

public class ControlsPane extends BorderPane {

    private final Simulation simulation;
    private final SimulationConfig simulationConfig;

    private final Button singleStepButton = new Button("Single step");
    private final Slider tickIntervalSlider = new Slider();
    private final Label tickIntervalSliderLabel = new Label();
    private final Label tickLabel = new Label();

    private final long simulationLength;

    public ControlsPane(Simulation simulation, ProblemArchetype problem, int run, int simulationRuns, long simulationLength, MapPane mapPane) {
        this.simulationLength = simulationLength;
        this.simulation = simulation;
        this.simulationConfig = simulation.getSimulationConfig();
        initComponents(problem, run, simulationRuns, mapPane);
        updateText();
        setPadding(new Insets(5));
    }

    private void initComponents(ProblemArchetype problem, int run, int simulationRuns, MapPane mapPane) {
        Button playPauseButton = new Button("Play / Pause");
        playPauseButton.setOnAction(e -> togglePaused());

        singleStepButton.setDisable(true);
        singleStepButton.setOnAction(e -> simulation.runCurrentTick());

        Button centerButton = new Button("Center Map");
        centerButton.setOnAction(e -> mapPane.center());

        tickIntervalSlider.setValue(simulationConfig.getMillisecondsPerTick());
        tickIntervalSlider.setMin(20);
        tickIntervalSlider.setMax(2000);
        tickIntervalSlider.setMajorTickUnit(1);
        tickIntervalSlider.setSnapToTicks(true);
        tickIntervalSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            simulationConfig.setMillisecondsPerTick(newValue.intValue());
            updateText();
        });
        VBox sliderBox = new VBox(tickIntervalSlider, tickIntervalSliderLabel);

        Label problemLabel = new Label("Simulating Problem: %s".formatted(problem.name()));
        Label runLabel = new Label("Run: %d/%d".formatted(run + 1, simulationRuns));
        VBox labels = new VBox(problemLabel, runLabel, tickLabel);

        Region intermediateRegion = new Region();
        intermediateRegion.setMinWidth(0);
        HBox.setHgrow(intermediateRegion, Priority.ALWAYS);

        HBox box = new HBox(playPauseButton, singleStepButton, centerButton, sliderBox, intermediateRegion, labels);
        box.setPadding(new Insets(0, 10, 0, 10));
        box.setSpacing(10);

        setCenter(box);
    }

    public void updateTickLabel(long tick) {
        tickLabel.setText("Tick: %d/%d".formatted(tick, simulationLength));
    }

    private void updateText() {
        tickIntervalSliderLabel.setText(
            "   Tick interval: %d ms %s".formatted(
                (int) tickIntervalSlider.getValue(),
                simulationConfig.isPaused() ? "(paused)" : ""));
    }

    private void togglePaused() {
        simulationConfig.setPaused(!simulationConfig.isPaused());
        singleStepButton.setDisable(!singleStepButton.isDisabled());
        updateText();
    }
}
