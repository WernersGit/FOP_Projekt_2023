package projekt.gui.pane;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.jetbrains.annotations.Nullable;
import projekt.base.Location;
import projekt.delivery.routing.Region;
import projekt.delivery.routing.Vehicle;
import projekt.delivery.routing.VehicleManager;
import projekt.gui.TUColors;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.stream.Collector;

import static projekt.gui.TUColors.*;

public class MapPane extends Pane {

    public static final float FIVE_TICKS_WIDTH = .125f;
    public static final float TEN_TICKS_WIDTH = .25f;

    private static final Color EDGE_COLOR = TUColors.COLOR_0C;

    private static final Color NODE_COLOR = COLOR_0D;
    private static final double NODE_DIAMETER = 15;

    private static final double IMAGE_SIZE = 0.1;
    private static final Color CAR_COLOR = TUColors.COLOR_6C;
    private static final Image CAR_IMAGE = loadImage("projekt/gui/scene/car.png", CAR_COLOR);

    private static final double SCALE_IN = 1.1;
    private static final double SCALE_OUT = 1 / SCALE_IN;
    private static final double MAX_SCALE = 100;
    private static final double MIN_SCALE = 3;

    private final AtomicReference<Point2D> lastPoint = new AtomicReference<>();
    private AffineTransform transformation = new AffineTransform();

    private final Text positionText = new Text();

    private final Map<Region.Node, LabeledNode> nodes = new HashMap<>();
    private final Map<Region.Edge, LabeledEdge> edges = new HashMap<>();
    private final Map<Vehicle, ImageView> vehicles = new HashMap<>();
    private final List<Node> grid = new ArrayList<>();

    private Region.Node selectedNode;
    private Consumer<? super Region.Node> nodeSelectionHandler;
    private Consumer<? super Region.Node> nodeRemoveSelectionHandler;

    private Region.Edge selectedEdge;
    private Consumer<? super Region.Edge> edgeSelectionHandler;
    private Consumer<? super Region.Edge> edgeRemoveSelectionHandler;

    private Collection<Vehicle> selectedVehicles;
    private Consumer<? super Collection<Vehicle>> vehiclesSelectionHandler;
    private Consumer<? super Collection<Vehicle>> vehiclesRemoveSelectionHandler;

    private boolean alreadyCentered = false;

    /**
     * Creates a new, empty {@link MapPane}.
     */
    public MapPane() {
        this(List.of(), List.of(), List.of());
    }

    /**
     * Creates a new {@link MapPane}, displays the given components and centers itself.
     *
     * @param nodes    The {@link Region.Node}s to display.
     * @param edges    The {@link Region.Edge}s to display.
     * @param vehicles The {@link Vehicle}s to display.
     */
    public MapPane(Collection<? extends Region.Node> nodes,
                   Collection<? extends Region.Edge> edges,
                   Collection<? extends Vehicle> vehicles) {

        //avoid division by zero when scale = 1
        transformation.scale(MIN_SCALE, MIN_SCALE);

        for (Region.Edge edge : edges) {
            addEdge(edge);
        }

        for (Region.Node node : nodes) {
            addNode(node);
        }

        for (Vehicle vehicle : vehicles) {
            addVehicle(vehicle);
        }

        initListeners();
        drawGrid();
        drawPositionText();
        positionText.setFill(Color.WHITE);
    }

    // --- Edge Handling --- //

    /**
     * Adds an {@link Region.Edge} to this {@link MapPane} and displays it.
     *
     * @param edge The {@link Region.Edge} to display.
     */
    public void addEdge(Region.Edge edge) {
        if (selectedNode != null) {
            if (edge.getNodeA().getLocation().equals(selectedNode.getLocation()) || edge.getNodeB().getLocation().equals(selectedNode.getLocation())) {
                handleNodeClick(nodes.get(selectedNode).ellipse(), selectedNode);
            }
        }

        edges.put(edge, drawEdge(edge));
    }

    /**
     * Adds the {@link Region.Edge}s to this {@link MapPane} and displays them.
     *
     * @param edges The {@link Region.Edge}s to display.
     */
    public void addAllEdges(Collection<? extends Region.Edge> edges) {
        for (Region.Edge edge : edges) {
            addEdge(edge);
        }
    }

    /**
     * Removes the given {@link Region.Edge} from this {@link MapPane}.
     *
     * @param edge The {@link Region.Edge} to remove.
     */
    public void removeEdge(Region.Edge edge) {
        LabeledEdge labeledEdge = edges.remove(edge);

        if (labeledEdge != null) {
            getChildren().removeAll(labeledEdge.line(), labeledEdge.text());
        }
    }

    /**
     * Returns the {@link Region.Edge} selected by the user by clicking onto it or its name.
     *
     * @return The {@link Region.Edge} selected by the user or null if no {@link Region.Edge} is selected.
     */
    public Region.Edge getSelectedEdge() {
        return selectedEdge;
    }

    /**
     * Selects the given {@link Region.Edge} and executes the action set by {@link #onEdgeSelection(Consumer)}.
     * <p>This is equivalent to clicking manually on the {@link Region.Edge}.</p>
     *
     * @throws IllegalArgumentException if the given {@link Region.Edge} is not part of this {@link MapPane}
     * @param edge the edge to select
     */
    public void selectEdge(Region.Edge edge) {
        if (!edges.containsKey(edge)) {
            throw new IllegalArgumentException("The given edge is not part of this MapPane");
        }

        handleEdgeClick(edges.get(edge).line(), edge);
    }
    /**
     * Sets the action that is supposed to be executed when the user selects an {@link Region.Edge}.
     *
     * @param edgeSelectionHandler The {@link Consumer} that executes the action.
     *                             The apply method of the {@link Consumer} will be called with
     *                             the selected {@link Region.Edge} as the parameter.
     */
    public void onEdgeSelection(Consumer<? super Region.Edge> edgeSelectionHandler) {
        this.edgeSelectionHandler = edgeSelectionHandler;
    }

    /**
     * Sets the action that is supposed to be executed when the user removes the selection of an {@link Region.Edge}.<p>
     * When a different {@link Region.Edge} is selected than the previous one only the action set by
     * {@link #onEdgeSelection(Consumer) will be executed.
     * <p>
     *
     * @param edgeRemoveSelectionHandler The {@link Consumer} that executes the action.
     *                                   The apply method of the {@link Consumer} will be called with
     *                                   the previously selected {@link Region.Edge} as the parameter.
     */
    public void onEdgeRemoveSelection(Consumer<? super Region.Edge> edgeRemoveSelectionHandler) {
        this.edgeRemoveSelectionHandler = edgeRemoveSelectionHandler;
    }

    /**
     * Updates the position of all {@link Region.Edge}s on this {@link MapPane}.
     */
    public void redrawEdges() {
        for (Region.Edge edge : edges.keySet()) {
            redrawEdge(edge);
        }
    }

    /**
     * Updates the position of the given {@link Region.Edge}.
     *
     * @param edge The {@link Region.Edge} to update.
     * @throws IllegalArgumentException If the given {@link Region.Edge} is not part of this {@link MapPane}.
     */
    public void redrawEdge(Region.Edge edge) {
        if (!edges.containsKey(edge)) {
            throw new IllegalArgumentException("The given edge is not part of this MapPane");
        }

        Point2D transformedMidPoint = transform(midPoint(edge));
        Point2D transformedPointA = transform(edge.getNodeA().getLocation());
        Point2D transformedPointB = transform(edge.getNodeB().getLocation());

        LabeledEdge labeledEdge = edges.get(edge);

        labeledEdge.line().setStartX(transformedPointA.getX());
        labeledEdge.line().setStartY(transformedPointA.getY());

        labeledEdge.line().setEndX(transformedPointB.getX());
        labeledEdge.line().setEndY(transformedPointB.getY());

        labeledEdge.text().setX(transformedMidPoint.getX());
        labeledEdge.text().setY(transformedMidPoint.getY());
    }

    // --- Node Handling --- //

    /**
     * Adds a {@link Region.Node} to this {@link MapPane} and displays it.
     *
     * @param node The {@link Region.Node} to display.
     */
    public void addNode(Region.Node node) {
        nodes.put(node, drawNode(node));
    }

    /**
     * Adds the {@link Region.Node}s to this {@link MapPane} and displays them.
     *
     * @param nodes The {@link Region.Node}s to display.
     */
    public void addAllNodes(Collection<? extends Region.Node> nodes) {
        for (Region.Node node : nodes) {
            addNode(node);
        }
    }

    /**
     * Removes the given {@link Region.Node} from this {@link MapPane}.<p>
     * {@link Region.Edge}s and {@link Vehicle}s connected to the removed {@link Region.Node} will not get removed.
     *
     * @param node The {@link Region.Node} to remove.
     */
    public void removeNode(Region.Node node) {
        LabeledNode labeledNode = nodes.remove(node);

        if (labeledNode != null) {
            getChildren().removeAll(labeledNode.ellipse(), labeledNode.text());
        }
    }

    /**
     * Returns the {@link Region.Node} selected by the user by clicking onto it or its name.
     *
     * @return The {@link Region.Node} selected by the user or null if no {@link Region.Node} is selected.
     */
    public Region.Node getSelectedNode() {
        return selectedNode;
    }

    /**
     * Selects the given {@link Region.Node} and executes the action set by {@link #onNodeSelection(Consumer)}.
     * <p>This is equivalent to clicking manually on the {@link Region.Node}.</p>
     *
     * @throws IllegalArgumentException if the given {@link Region.Node} is not part of this {@link MapPane}
     * @param node the node to select
     */
    public void selectNode(Region.Node node) {
        if (!nodes.containsKey(node)) {
            throw new IllegalArgumentException("The given node is not part of this MapPane");
        }

        handleNodeClick(nodes.get(node).ellipse(), node);
    }

    /**
     * Sets the action that is supposed to be executed when the user selects an {@link Region.Node}.
     *
     * @param nodeSelectionHandler The {@link Consumer} that executes the action.
     *                             The apply method of the {@link Consumer} will be called with
     *                             the selected {@link Region.Node} as the parameter.
     */
    public void onNodeSelection(Consumer<? super Region.Node> nodeSelectionHandler) {
        this.nodeSelectionHandler = nodeSelectionHandler;
    }

    /**
     * Sets the action that is supposed to be executed when the user removes the selection of an {@link Region.Node}.<p>
     * When a different {@link Region.Node} is selected than the previous one only the action set by
     * {@link #onNodeSelection(Consumer)} will be executed.
     * <p>
     *
     * @param nodeRemoveSelectionHandler The {@link Consumer} that executes the action.
     *                                   The apply method of the {@link Consumer} will be called with
     *                                   the previously selected {@link Region.Edge} as the parameter.
     */
    public void onNodeRemoveSelection(Consumer<? super Region.Node> nodeRemoveSelectionHandler) {
        this.nodeRemoveSelectionHandler = nodeRemoveSelectionHandler;
    }

    /**
     * Updates the position of all {@link Region.Node}s on this {@link MapPane}.
     */
    public void redrawNodes() {
        for (Region.Node node : nodes.keySet()) {
            redrawNode(node);
        }
    }

    /**
     * Updates the position of the given {@link Region.Node}.
     *
     * @param node The {@link Region.Node} to update.
     * @throws IllegalArgumentException If the given {@link Region.Node} is not part of this {@link MapPane}.
     */
    public void redrawNode(Region.Node node) {
        if (!nodes.containsKey(node)) {
            throw new IllegalArgumentException("The given node is not part of this MapPane");
        }

        Point2D transformedMidPoint = transform(midPoint(node));

        LabeledNode labeledNode = nodes.get(node);

        labeledNode.ellipse().setCenterX(transformedMidPoint.getX());
        labeledNode.ellipse().setCenterY(transformedMidPoint.getY());

        labeledNode.text().setX(transformedMidPoint.getX() + NODE_DIAMETER);
        labeledNode.text().setY(transformedMidPoint.getY());
    }

    // --- Vehicle Handling --- //

    /**
     * Adds a {@link Vehicle} to this {@link MapPane} and displays it.
     *
     * @param vehicle The {@link Vehicle} to display.
     */
    public void addVehicle(Vehicle vehicle) {
        vehicles.put(vehicle, drawVehicle(vehicle));
    }

    /**
     * Adds the {@link Vehicle}s to this {@link MapPane} and displays them.
     *
     * @param vehicles The {@link Vehicle}s to display.
     */
    public void addAllVehicles(Collection<? extends Vehicle> vehicles) {
        for (Vehicle vehicle : vehicles) {
            addVehicle(vehicle);
        }
    }

    /**
     * Removes the given {@link Vehicle} from this {@link MapPane}.
     *
     * @param vehicle The {@link Vehicle} to remove.
     */
    public void removeVehicle(Vehicle vehicle) {
        ImageView imageView = vehicles.remove(vehicle);

        if (imageView != null) {
            getChildren().remove(imageView);
        }
    }

    /**
     * Returns the {@link Vehicle}s selected by the user by clicking onto the {@link Region.Node} or its name
     * the {@link Vehicle}s are on.
     *
     * @return The {@link Vehicle}s selected by the user or null if no {@link Region.Edge} is selected.
     */
    public Collection<Vehicle> getSelectedVehicles() {
        return selectedVehicles;
    }

    /**
     * Sets the action that is supposed to be executed when the user selects {@link Vehicle}s.
     *
     * @param vehiclesSelectionHandler The {@link Consumer} that executes the action.
     *                                 The apply method of the {@link Consumer} will be called with
     *                                 the selected {@link Vehicle}s as the parameter.
     */
    public void onVehicleSelection(Consumer<? super Collection<Vehicle>> vehiclesSelectionHandler) {
        this.vehiclesSelectionHandler = vehiclesSelectionHandler;
    }

    /**
     * Sets the action that is supposed to be executed when the user removes the selection of {@link Vehicle}s.<p>
     * When different {@link Vehicle}s are selected than the previous one only the action set by
     * {@link #onVehicleSelection(Consumer)} will be executed.
     * <p>
     *
     * @param vehiclesRemoveSelectionHandler The {@link Consumer} that executes the action.
     *                                       The apply method of the {@link Consumer} will be called with
     *                                       the previously selected {@link Vehicle}s as the parameter.
     */
    public void onVehicleRemoveSelection(Consumer<? super Collection<Vehicle>> vehiclesRemoveSelectionHandler) {
        this.vehiclesRemoveSelectionHandler = vehiclesRemoveSelectionHandler;
    }

    /**
     * Updates the position of all {@link Vehicle}s on this {@link MapPane}.
     */
    public void redrawVehicles() {
        for (Vehicle vehicle : vehicles.keySet()) {
            redrawVehicle(vehicle);
        }
    }

    /**
     * Updates the position of the given {@link Vehicle}.
     *
     * @param vehicle The {@link Vehicle} to update.
     * @throws IllegalArgumentException If the given {@link Vehicle} is not part of this {@link MapPane}.
     */
    public void redrawVehicle(Vehicle vehicle) {
        if (!vehicles.containsKey(vehicle)) {
            throw new IllegalArgumentException("The given vehicle is not part of this MapPane.");
        }

        Point2D transformedMidPoint = transform(midPoint(vehicle));

        ImageView imageView = vehicles.get(vehicle);
        imageView.setX(transformedMidPoint.getX() - imageView.getImage().getWidth() / 2);
        imageView.setY(transformedMidPoint.getY() - imageView.getImage().getHeight() / 2);
    }

    // --- Other Util --- //

    /**
     * Removes all components from this {@link MapPane}.
     */
    public void clear() {
        for (Region.Node node : new HashSet<>(nodes.keySet())) {
            removeNode(node);
        }

        for (Region.Edge edge : new HashSet<>(edges.keySet())) {
            removeEdge(edge);
        }

        for (Vehicle vehicle : new HashSet<>(vehicles.keySet())) {
            removeVehicle(vehicle);
        }

        selectedNode = null;
        selectedEdge = null;
        selectedVehicles = null;
    }

    /**
     * Updates the position of all components on this {@link MapPane}.
     */
    public void redrawMap() {
        redrawNodes();
        redrawEdges();
        redrawVehicles();
    }

    /**
     * Tries to center this {@link MapPane} as good as possible such that each node is visible while keeping the zoom factor as high as possible.
     */
    public void center() {

        if (getHeight() == 0.0 || getWidth() == 0.0) {
            return;
        }

        if (nodes.isEmpty()) {
            transformation.scale(20, 20);
            redrawGrid();
            return;
        }

        double maxX = nodes.keySet().stream().map(node -> node.getLocation().getX())
            .collect(new ComparingCollector<Integer>(Comparator.naturalOrder()));

        double maxY = nodes.keySet().stream().map(node -> node.getLocation().getY())
            .collect(new ComparingCollector<Integer>(Comparator.naturalOrder()));

        double minX = nodes.keySet().stream().map(node -> node.getLocation().getX())
            .collect(new ComparingCollector<Integer>(Comparator.reverseOrder()));

        double minY = nodes.keySet().stream().map(node -> node.getLocation().getY())
            .collect(new ComparingCollector<Integer>(Comparator.reverseOrder()));

        if (minX == maxX) {
            minX = minX - 1;
            maxX = maxX + 1;
        }

        if (minY == maxY) {
            minY = minY - 1;
            maxY = maxY + 1;
        }

        AffineTransform reverse = new AffineTransform();

        reverse.setToTranslation(minX, minY);
        reverse.scale(1.25 * (maxX - minX) / getWidth(),1.25 * (maxY - minY) / getHeight());
        reverse.translate(-Math.abs(0.125 * reverse.getTranslateX()) / reverse.getScaleX(),-Math.abs(0.125 * reverse.getTranslateY()) / reverse.getScaleY());

        transformation = reverse;
        transformation = getReverseTransform();

        redrawGrid();
        redrawMap();

        alreadyCentered = true;
    }

    // --- Private Methods --- //

    private void initListeners() {

        setOnMouseDragged(actionEvent -> {
                Point2D point = new Point2D.Double(actionEvent.getX(), actionEvent.getY());
                Point2D diff = getDifference(point, lastPoint.get());

                transformation.translate(diff.getX() / transformation.getScaleX(), diff.getY() / transformation.getScaleY());

                redrawMap();
                redrawGrid();
                updatePositionText(point);

                lastPoint.set(point);
            }
        );

        setOnScroll(event -> {
            if (event.getDeltaY() == 0) {
                return;
            }
            double scale = event.getDeltaY() > 0 ? SCALE_IN : SCALE_OUT;

            if (((transformation.getScaleX() < MIN_SCALE || transformation.getScaleY() < MIN_SCALE) && scale < 1)
                || ((transformation.getScaleX() > MAX_SCALE || transformation.getScaleX() > MAX_SCALE) && scale > 1)) {
                return;
            }

            transformation.scale(scale, scale);

            redrawMap();
            redrawGrid();
        });


        setOnMouseMoved(actionEvent -> {
            Point2D point = new Point2D.Double(actionEvent.getX(), actionEvent.getY());
            lastPoint.set(point);
            updatePositionText(point);
        });

        widthProperty().addListener((obs, oldValue, newValue) -> {
            setClip(new Rectangle(0, 0, getWidth(), getHeight()));

            if (alreadyCentered) {
                redrawGrid();
                redrawMap();
            } else {
                center();
            }

            drawPositionText();
        });

        heightProperty().addListener((obs, oldValue, newValue) -> {
            setClip(new Rectangle(0, 0, getWidth(), getHeight()));

            if (alreadyCentered) {
                redrawGrid();
                redrawMap();
            } else {
                center();
            }

            drawPositionText();
        });
    }

    private LabeledEdge drawEdge(Region.Edge edge) {
        Location a = edge.getNodeA().getLocation();
        Location b = edge.getNodeB().getLocation();

        Point2D transformedA = transform(a);
        Point2D transformedB = transform(b);

        Line line = new Line(transformedA.getX(), transformedA.getY(), transformedB.getX(), transformedB.getY());

        line.setStroke(edge.equals(selectedEdge) ? COLOR_9B : COLOR_0A);
        line.setStrokeWidth(1);

        Point2D transformedMidPoint = transform(midPoint(edge));
        Text text = new Text(transformedMidPoint.getX(), transformedMidPoint.getY(), edge.getName());
        text.setStroke(COLOR_0A);

        getChildren().addAll(line, text);

        line.setOnMouseClicked(e -> handleEdgeClick(line, edge));
        text.setOnMouseClicked(e -> handleEdgeClick(line, edge));

        return new LabeledEdge(line, text);
    }

    private LabeledNode drawNode(Region.Node node) {
        Point2D transformedPoint = transform(node.getLocation());

        Ellipse ellipse = new Ellipse(transformedPoint.getX(), transformedPoint.getY(), NODE_DIAMETER, NODE_DIAMETER);
        ellipse.setFill(NODE_COLOR);
        ellipse.setStrokeWidth(1);
        ellipse.setStroke(node.equals(selectedNode) ? COLOR_9B : COLOR_0A);
        setMouseTransparent(false);

        Text text = new Text(transformedPoint.getX(), transformedPoint.getY(), node.getName());
        text.setStroke(COLOR_0A);

        getChildren().addAll(ellipse, text);

        ellipse.setOnMouseClicked(e -> handleNodeClick(ellipse, node));
        text.setOnMouseClicked(e -> handleNodeClick(ellipse, node));

        return new LabeledNode(ellipse, text);
    }

    private ImageView drawVehicle(Vehicle vehicle) {
        Point2D transformedMidPoint = transform(midPoint(vehicle));

        var imageView = new ImageView();
        imageView.setImage(CAR_IMAGE);
        imageView.scaleXProperty().set(IMAGE_SIZE);
        imageView.scaleYProperty().set(IMAGE_SIZE);
        imageView.setX(transformedMidPoint.getX() - imageView.getImage().getWidth() / 2);
        imageView.setY(transformedMidPoint.getY() - imageView.getImage().getHeight() / 2);
        getChildren().add(imageView);

        return imageView;
    }

    @SuppressWarnings("SameParameterValue")
    private static Image loadImage(String name, Color color) {
        try {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(MapPane.class.getClassLoader().getResource(name)));
            for (int x = 0; x < image.getWidth(); x++)
                for (int y = 0; y < image.getHeight(); y++)
                    if (image.getRGB(x, y) == java.awt.Color.BLACK.getRGB())
                        image.setRGB(x, y, new java.awt.Color(
                            (float) color.getRed(),
                            (float) color.getGreen(),
                            (float) color.getBlue(),
                            (float) color.getOpacity())
                            .getRGB());
            return SwingFXUtils.toFXImage(image, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleNodeClick(Ellipse ellipse, Region.Node node) {
        if (selectedNode != null) {
            nodes.get(selectedNode).ellipse().setStroke(EDGE_COLOR);
        }

        if (node.equals(selectedNode)) {
            if (nodeRemoveSelectionHandler != null) {
                nodeRemoveSelectionHandler.accept(selectedNode);
            }

            if (vehiclesRemoveSelectionHandler != null && selectedVehicles != null) {
                vehiclesRemoveSelectionHandler.accept(selectedVehicles);
            }

            selectedNode = null;
            selectedVehicles = null;
        } else {
            ellipse.setStroke(COLOR_9B);
            selectedNode = node;
            selectedVehicles = vehicles.keySet().stream()
                .filter(vehicle -> vehicle.getOccupied().getComponent().equals(selectedNode))
                .toList();

            if (nodeSelectionHandler != null) {
                nodeSelectionHandler.accept(selectedNode);
            }

            if (vehiclesSelectionHandler != null && selectedVehicles.size() != 0) {
                vehiclesSelectionHandler.accept(selectedVehicles);
            }
        }
    }

    private void handleEdgeClick(Line line, Region.Edge edge) {
        if (selectedEdge != null) {
            edges.get(selectedEdge).line().setStroke(EDGE_COLOR);
        }

        if (edge.equals(selectedEdge)) {
            if (edgeRemoveSelectionHandler != null) {
                edgeRemoveSelectionHandler.accept(selectedEdge);
            }
            selectedEdge = null;
        } else {
            line.setStroke(COLOR_9B);
            selectedEdge = edge;
            if (edgeSelectionHandler != null) {
                edgeSelectionHandler.accept(selectedEdge);
            }
        }
    }

    private void drawGrid() {
        Color color = COLOR_0D;

        int stepX = (int) (transformation.getScaleX() / 2);
        int stepY = (int) (transformation.getScaleY() / 2);

        int offsetX = (int) transformation.getTranslateX();
        int offsetY = (int) transformation.getTranslateY();

        // Vertical Lines
        for (int i = 0, x = offsetX % (stepX * 5); x <= getWidth(); i++, x += stepX) {
            Float strokeWidth = getStrokeWidth(i, offsetX % (stepX * 10) > stepX * 5);
            if (strokeWidth == null) continue;
            Line line = new Line(x, 0, x, getHeight());
            line.setStrokeWidth(strokeWidth);
            line.setStroke(color);
            getChildren().add(line);
            grid.add(line);
        }

        // Horizontal Lines
        for (int i = 0, y = offsetY % (stepY * 5); y <= getHeight(); i++, y += stepY) {
            Float strokeWidth = getStrokeWidth(i, offsetY % (stepY * 10) > stepY * 5);
            if (strokeWidth == null) continue;

            var line = new Line(0, y, getWidth(), y);
            line.setStrokeWidth(strokeWidth);
            line.setStroke(color);
            getChildren().add(line);
            grid.add(line);
        }
    }

    @Nullable
    private static Float getStrokeWidth(int i, boolean inverted) {
        float strokeWidth;
        if (i % 10 == 0) {
            strokeWidth = inverted ? TEN_TICKS_WIDTH : FIVE_TICKS_WIDTH;
        } else if (i % 5 == 0) {
            strokeWidth = inverted ? FIVE_TICKS_WIDTH : TEN_TICKS_WIDTH;
        } else {
            return null;
        }
        return strokeWidth;
    }

    private static Point2D locationToPoint2D(Location location) {
        return new Point2D.Double(location.getX(), location.getY());
    }

    private static Point2D getDifference(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() - p2.getX(), p1.getY() - p2.getY());
    }

    private static Point2D midPoint(VehicleManager.Occupied<?> occupied) {
        if (occupied.getComponent() instanceof Region.Node) {
            return midPoint(((Region.Node) occupied.getComponent()).getLocation());
        } else if (occupied.getComponent() instanceof Region.Edge) {
            return midPoint((Region.Edge) occupied.getComponent());
        }
        throw new UnsupportedOperationException("unsupported type of component");
    }

    private static Point2D midPoint(Location location) {
        return new Point2D.Double(location.getX(), location.getY());
    }

    private static Point2D midPoint(Vehicle vehicle) {
        return midPoint(vehicle.getOccupied());
    }

    private static Point2D midPoint(Region.Node node) {
        return midPoint(node.getLocation());
    }

    private static Point2D midPoint(Region.Edge edge) {
        var l1 = edge.getNodeA().getLocation();
        var l2 = edge.getNodeB().getLocation();
        return new Point2D.Double((l1.getX() + l2.getX()) / 2d, (l1.getY() + l2.getY()) / 2d);
    }

    private void redrawGrid() {
        getChildren().removeAll(grid);
        grid.clear();
        drawGrid();
    }

    private void drawPositionText() {
        positionText.setX(getWidth() - positionText.getLayoutBounds().getWidth());
        positionText.setY(getHeight());
        positionText.setText("(-, -)");
        if (!getChildren().contains(positionText)) {
            getChildren().add(positionText);
        }
    }

    private void updatePositionText(Point2D point) {
        point = getReverseTransform().transform(point, null);
        positionText.setText("(%d, %d)".formatted((int) point.getX(), (int) point.getY()));
        positionText.setX(getWidth() - positionText.getLayoutBounds().getWidth());
        positionText.setY(getHeight());
    }

    private AffineTransform getReverseTransform() {
        try {
            return transformation.createInverse();
        } catch (NoninvertibleTransformException e) {
            throw new IllegalStateException("transformation is not invertible");
        }
    }

    private Point2D transform(Point2D point) {
        return transformation.transform(point, null);
    }

    private Point2D transform(Location location) {
        return transformation.transform(locationToPoint2D(location), null);
    }

    private record LabeledEdge(Line line, Text text) {
    }

    private record LabeledNode(Ellipse ellipse, Text text) {
    }

    private record ComparingCollector<T extends Comparable<T>>(Comparator<T> comparator) implements Collector<T, List<T>, T> {

        @Override
        public Supplier<List<T>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<T>, T> accumulator() {
            return List::add;
        }

        @Override
        public BinaryOperator<List<T>> combiner() {
            return (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            };
        }

        @Override
        public Function<List<T>, T> finisher() {
            return list -> {

                T bestFit = null;

                for (T elem : list) {
                    if (bestFit == null) {
                        bestFit = elem;
                    } else if (comparator.compare(elem, bestFit) > 0) {
                        bestFit = elem;
                    }
                }

                return bestFit;
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }
}
