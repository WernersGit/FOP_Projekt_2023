package projekt.gui.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import projekt.delivery.archetype.ProblemArchetype;
import projekt.gui.controller.ControlledScene;
import projekt.gui.controller.MenuSceneController;
import projekt.io.ProblemArchetypeIO;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

/**
 * An abstract class for representing a controllable menu scene.<p>
 * The root Element of each menu scene is a {@link BorderPane}.
 * At the top of the menu scene is the title of this scene set in the constructor and at the bottom are the return
 * and quit buttons. The return button will get initialized by the abstract method {@link #initReturnButton()};
 *
 * @param <SC> The type of the {@link MenuSceneController} of this {@link MainMenuScene}.
 */
public abstract class MenuScene<SC extends MenuSceneController> extends Scene implements ControlledScene<SC> {

    protected final BorderPane root;
    protected final SC controller;

    protected final Button returnButton;
    protected final Button quitButton;

    protected List<ProblemArchetype> problems;

    /**
     * Creates a new {@link MainMenuScene}.
     *
     * @param controller  The {@link MenuSceneController} of this {@link MainMenuScene}.
     * @param title       The title of this {@link MainMenuScene}.
     * @param styleSheets The styleSheets to apply.
     */
    public MenuScene(SC controller, String title, String... styleSheets) {
        super(new BorderPane());

        // Typesafe reference to the root group of the scene.
        root = (BorderPane) getRoot();
        root.setPrefSize(700, 700);
        this.controller = controller;

        final Label titleLabel = new Label(title);
        titleLabel.setPadding(new Insets(20, 20, 20, 20));
        titleLabel.setId("Title");

        root.setTop(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        //setup return and quit buttons
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(20, 20, 20, 20));

        returnButton = new Button("Return");
        returnButton.setMaxWidth(600);
        buttons.getChildren().add(returnButton);

        quitButton = new Button("Quit");
        quitButton.setMaxWidth(600);
        quitButton.setOnAction(e -> getController().quit());
        buttons.getChildren().add(quitButton);

        HBox.setHgrow(returnButton, Priority.ALWAYS);
        HBox.setHgrow(quitButton, Priority.ALWAYS);

        root.setBottom(buttons);

        // apply styles
        if (styleSheets != null) {
            root.getStylesheets().addAll(styleSheets);
        }
        root.getStylesheets().add("projekt/gui/menuStyle.css");
        root.getStylesheets().add("projekt/gui/darkMode.css");
    }

    /**
     * Initializes this {@link MainMenuScene} with the given {@link ProblemArchetype}s.
     *
     * @param problems The current selection of {@link ProblemArchetype}s
     */
    public final void init(List<ProblemArchetype> problems) {
        this.problems = problems;
        initComponents();
        initReturnButton();
    }

    /**
     * Initializes all components of this {@link MainMenuScene}.
     */
    public abstract void initComponents();

    /**
     * Initializes the return button of this {@link MainMenuScene}.
     */
    public abstract void initReturnButton();

    @Override
    public SC getController() {
        return controller;
    }

    // --- common util --- //

    /**
     * Creates a {@link TextField} that only accepts long values.
     *
     * @param valueChangeConsumer A {@link Consumer} that will be called when the entered value changes.
     *                            It will be invoked with the new value.
     * @param initialValue        The intial value of the {@link TextField}.
     * @return The created {@link TextField}
     */
    public static TextField createLongTextField(Consumer<Long> valueChangeConsumer, Long initialValue) {
        TextField longTextField = new TextField();
        longTextField.setText(initialValue.toString());
        longTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                longTextField.setText(oldValue);
                return;
            }

            if (newValue.equals("")) return;

            try {
                valueChangeConsumer.accept(Long.parseLong(longTextField.getText()));
            } catch (NumberFormatException exc) {
                longTextField.setText(oldValue);
            }
        });

        return longTextField;
    }

    /**
     * Creates a {@link TextField} that only accepts positive integer values.
     *
     * @param valueChangeConsumer A {@link Consumer} that will be called when the entered value changes.
     *                            It will be invoked with the new value.
     * @param initialValue        The intial value of the {@link TextField}.
     * @return The created {@link TextField}
     */
    public static TextField createPositiveIntegerTextField(Consumer<Integer> valueChangeConsumer, Integer initialValue) {
        TextField positiveIntegerTextField = new TextField();
        positiveIntegerTextField.setText(initialValue.toString());
        positiveIntegerTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                positiveIntegerTextField.setText(oldValue);
                return;
            }

            if (newValue.equals("")) return;

            try {
                valueChangeConsumer.accept(Integer.parseInt(positiveIntegerTextField.getText()));
            } catch (NumberFormatException exc) {
                positiveIntegerTextField.setText(oldValue);
            }
        });

        return positiveIntegerTextField;
    }

    /**
     * Creates a {@link TextField} that only accepts integer values.
     *
     * @param valueChangeConsumer A {@link Consumer} that will be called when the entered value changes.
     *                            It will be invoked with the new value.
     * @param initialValue        The initial value of the {@link TextField}.
     * @return The created {@link TextField}
     */
    public static TextField createIntegerTextField(Consumer<Integer> valueChangeConsumer, Integer initialValue) {
        TextField negativeIntegerTextField = new TextField();
        negativeIntegerTextField.setText(initialValue.toString());
        negativeIntegerTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("-?\\d*")) {
                negativeIntegerTextField.setText(oldValue);
                return;
            }

            if (newValue.equals("") || newValue.equals("-")) return;

            try {
                valueChangeConsumer.accept(Integer.parseInt(negativeIntegerTextField.getText()));
            } catch (NumberFormatException exc) {
                negativeIntegerTextField.setText(oldValue);
            }
        });

        return negativeIntegerTextField;
    }

    /**
     * Creates a {@link TextField} that only accepts positive double values.
     *
     * @param valueChangeConsumer A {@link Consumer} that will be called when the entered value changes.
     *                            It will be invoked with the new value.
     * @param initialValue        The initial value of the {@link TextField}.
     * @return The created {@link TextField}
     */
    public static TextField createPositiveDoubleTextField(Consumer<Double> valueChangeConsumer, Double initialValue) {
        TextField doubleTextField = new TextField();
        doubleTextField.setText(initialValue.toString());
        doubleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                doubleTextField.setText(oldValue);
                return;
            }

            if (newValue.equals("")) return;

            try {
                valueChangeConsumer.accept(Double.parseDouble(doubleTextField.getText()));
            } catch (NumberFormatException exc) {
                doubleTextField.setText(oldValue);
            }
        });

        return doubleTextField;
    }

    /**
     * Creates an empty {@link Region} that always grows inside a {@link HBox}.<p>
     * It can be used for evenly space the components inside a {@link HBox} by putting an intermediate region between each Node.
     *
     * @param minWidth The minimum width of the created {@link Region}.
     * @return The created {@link Region}.
     * @see HBox#setHgrow(Node, Priority)
     */
    public static Region createIntermediateRegion(int minWidth) {
        Region intermediateRegion = new Region();
        intermediateRegion.setMinWidth(minWidth);
        HBox.setHgrow(intermediateRegion, Priority.ALWAYS);

        return intermediateRegion;
    }
}
