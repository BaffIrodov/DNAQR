package dnaqr.evoluringCore;

import dnaqr.evoluringCore.keyController.KeyTitles;
import dnaqr.evoluringCore.renders.PauseRender;
import dnaqr.evoluringCore.settings.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    GameSettings gameSettings = new GameSettings();
    BoardSettings boardSettings = gameSettings.getBoardSettings();
    EnergyCostSettings energyCostSettings = gameSettings.getCostSetting();
    FoodAddingSettings foodAddingSettings = gameSettings.getFoodAddingSettings();
    CellGenerationSettings cellGenerationSettings = gameSettings.getCellGenerationSettings();
    RenderSettings renderSettings = gameSettings.getRenderSettings();
    CellActions cellActions = new CellActions();
    KeyTitles keyTitles = new KeyTitles();
    PauseRender pauseRender = new PauseRender();
    static List<Cell> cells = new ArrayList<>();
    static List<Square> squares = new ArrayList<>();
    static Map<String, Integer> mapSquareCoordinatesToIndex = new HashMap<>();
    static Map<Map<Integer, Integer>, Integer> foodsMap = new HashMap<>();
    static boolean gameOver = false;
    static boolean gameStoped = false;
    static Random rand = new Random();
    List<Cell> cellsToDelete = new ArrayList<>();
    List<Cell> cellsToAdding = new ArrayList<>();
    public boolean isFoodAdding = false;
    public boolean isOnlyCloseAdding = false;
    public int currentTick = 0;
    public long startGameMills = System.currentTimeMillis();
    private int realFrameCount = 1;

    private int getRandPos(int number) {
        return new Random(number).nextInt();
    }

    public void start(Stage primaryStage) {
        try {
            VBox root = new VBox();
            Canvas c = new Canvas(boardSettings.getWidth() * boardSettings.getSquareSize(), boardSettings.getHeight() * boardSettings.getSquareSize());
            GraphicsContext graphicsContext = c.getGraphicsContext2D();
            root.getChildren().add(c);

            new AnimationTimer() {
                long lastTick = 0;
                int index = 0;

                public void handle(long now) {
                    if (lastTick == 0) {
                        lastTick = now;
                        tick(graphicsContext);
                        return;
                    }

                    if (now - lastTick > 0) {
                        lastTick = now;
                        tick(graphicsContext);
                    }
                }

            }.start();

            Scene scene = new Scene(root, boardSettings.getWidth() * boardSettings.getSquareSize(), boardSettings.getHeight() * boardSettings.getSquareSize());

            // control
            scene.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {

                double realX = mouseEvent.getX() / boardSettings.getSquareSize();
                double realY = mouseEvent.getY() / boardSettings.getSquareSize();
                int index = mapSquareCoordinatesToIndex.get((int) realX + "|" + (int) realY);
                Square square = squares.get(index);
                int i = 0;
            });
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                if (key.getCode() == KeyCode.DIGIT1) {
                    this.realFrameCount = 1;
                    System.out.println(this.realFrameCount);
                }
                if (key.getCode() == KeyCode.DIGIT2) {
                    this.realFrameCount = 10;
                    System.out.println(this.realFrameCount);
                }
                if (key.getCode() == KeyCode.DIGIT3) {
                    this.realFrameCount = 20;
                    System.out.println(this.realFrameCount);
                }
                if (keyTitles.mapKeyByKeyCodes.containsKey(key.getCode())){
                    if (key.getCode() == KeyCode.SPACE) {
                        gameStoped = !gameStoped;
                        if (gameStoped) {
                            if (renderSettings.pauseKeySettingsEnable || renderSettings.pauseGameConditionEnable) {
                                if (renderSettings.pauseBackgroundEnable) {
                                    graphicsContext.setFill(Color.color(0.9, 0.9, 0.9, 0.6));
                                    graphicsContext.fillRect(0, 0, 600, 500);
                                }
                                graphicsContext.setFill(Color.BLACK);
                                graphicsContext.setFont(new Font("", 16));
                                String splitter = "\n----------------\n";
                                String keyDescription = renderSettings.pauseKeySettingsEnable ?
                                        pauseRender.getKeyDescriptions(keyTitles) : "";
                                String gameCondition = renderSettings.pauseGameConditionEnable ?
                                        pauseRender.getGameCondition(cells) : "";
                                graphicsContext.fillText(keyDescription + splitter + gameCondition,
                                        30, 30);
                            }
                        }
                    }
                    if (key.getCode() == KeyCode.UP) {
                        cellAdding();
                    }
                    if (key.getCode() == KeyCode.LEFT) {
                        isFoodAdding = !isFoodAdding;
                    }
                    if (key.getCode() == KeyCode.DOWN) {
                        isOnlyCloseAdding = !isOnlyCloseAdding;
                    }
                    if (key.getCode() == KeyCode.RIGHT) {
                        testFreeFoodInDistrict();
                    }
                }
            });
            // initialization playing field
            squareAdding();


//            cellAdding();
            primaryStage.setScene(scene);
            primaryStage.setTitle("EVOLURING");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // tick
    public void tick(GraphicsContext graphicsContext) {
        for (int i = 0; i < realFrameCount; i++) {
            currentTick++;
//        if (currentTick % 100 == 0) {
////            testFreeFoodInDistrict();
////            testCloseFoodInDistrict();
//        }
            if (currentTick % 100 == 0) {
//            isFoodAdding = !isFoodAdding;
                System.out.println(System.currentTimeMillis() - startGameMills);
                startGameMills = System.currentTimeMillis();
            }

            if (!gameStoped) {
                Long now = System.currentTimeMillis();

                if (isFoodAdding) {
                    freeFoodAdding();
                    closeFoodAdding();
                }

                if (isOnlyCloseAdding) {
                    closeFoodAdding();
                }

                for (Square square : squares) { //отрисовка квадратиков
                    graphicsContext.setFill(square.color);
                    graphicsContext.fillRect(square.coordinates.x, square.coordinates.y, boardSettings.getSquareSize(), boardSettings.getSquareSize());
                }
                Integer red = 0;
                Integer black = 0;
                Integer green = 0;
                Integer blue = 0;
                for (Cell cell : cells) {
                    switch (cell.name) {
                        case "red" -> red++;
                        case "black" -> black++;
                        case "green" -> green++;
                        case "blue" -> blue++;
                    }
                    boolean isDeath = cell.checkIfDeath();
                    Square currentSquare = getCurrentSquare(cell);
                    if (cellGenerationSettings.generateByFood) {
                        if (cell.energy > cellGenerationSettings.costOfGenerationByFood) {
                            cellsToAdding.add(cell.generateChild(boardSettings.getSquareSize()));
                        }
                    }
                    if (!isDeath) {
                        CellActions.CellActionsNames nextAction = cell.getNextAction();
                        switch (nextAction) {
                            case GENERATE_CHILD -> {
                                if (cellGenerationSettings.generateByGene) {
                                    cellsToAdding.add(cellActions.onGenerateChild(cell));
                                }
                            }
                            case DO_NOTHING -> {
                                cellActions.onDoNothing(cell);
                            }
                            case MOVE_LEFT -> {
//                            currentSquare.removeObjectFromSquareItems(cell);
                                cellActions.onMoveLeft(cell);
                                Square tempSquare = getCurrentSquare(cell);
                                if (tempSquare.items.stream().anyMatch(e -> {
                                    return ((Cell) e).name.equals(cell.name);
                                })) {
                                    cellActions.onMoveRight(cell);
                                } else {
                                    currentSquare.removeObjectFromSquareItems(cell);
                                    tempSquare.addObjectToSquareItems(cell);
                                }
//                            currentSquare = getCurrentSquare(cell);
//                            currentSquare.addObjectToSquareItems(cell);
                            }
                            case MOVE_RIGHT -> {
//                            currentSquare.removeObjectFromSquareItems(cell);
                                cellActions.onMoveRight(cell);
                                Square tempSquare = getCurrentSquare(cell);
                                if (tempSquare.items.stream().anyMatch(e -> {
                                    return ((Cell) e).name.equals(cell.name);
                                })) {
                                    cellActions.onMoveLeft(cell);
                                } else {
                                    currentSquare.removeObjectFromSquareItems(cell);
                                    tempSquare.addObjectToSquareItems(cell);
                                }
//                            currentSquare = getCurrentSquare(cell);
//                            currentSquare.addObjectToSquareItems(cell);
                            }
                            case MOVE_DOWN -> {
//                            currentSquare.removeObjectFromSquareItems(cell);
                                cellActions.onMoveDown(cell);
                                Square tempSquare = getCurrentSquare(cell);
                                if (tempSquare.items.stream().anyMatch(e -> {
                                    return ((Cell) e).name.equals(cell.name);
                                })) {
                                    cellActions.onMoveUp(cell);
                                } else {
                                    currentSquare.removeObjectFromSquareItems(cell);
                                    tempSquare.addObjectToSquareItems(cell);
                                }
//                            currentSquare = getCurrentSquare(cell);
//                            currentSquare.addObjectToSquareItems(cell);
                            }
                            case MOVE_UP -> {
//                            currentSquare.removeObjectFromSquareItems(cell);
                                cellActions.onMoveUp(cell);
                                Square tempSquare = getCurrentSquare(cell);
                                if (tempSquare.items.stream().anyMatch(e -> {
                                    return ((Cell) e).name.equals(cell.name);
                                })) {
                                    cellActions.onMoveDown(cell);
                                } else {
                                    currentSquare.removeObjectFromSquareItems(cell);
                                    tempSquare.addObjectToSquareItems(cell);
                                }
//                            currentSquare = getCurrentSquare(cell);
//                            currentSquare.addObjectToSquareItems(cell);
                            }
                            case EAT_CLOSE_FOOD -> {
                                currentSquare = getCurrentSquare(cell);
                                cellActions.onEatCloseFood(cell, currentSquare);
                            }
                        }
                        graphicsContext.setFill(cell.color);
                        graphicsContext.fillRect(cell.coordinates.x, cell.coordinates.y, boardSettings.getSquareSize() /*- 1*/, boardSettings.getSquareSize() /*- 1*/);
                        if (energyCostSettings.isConsiderPassiveCost) {
                            cell.energy -= cell.energyCost;
                        }
                        cell.energy--;
                    } else {
                        cellsToDelete.add(cell);
//                    currentSquare.freeFood += foodAddingSettings.freeEatAddingByDeath;
//                    currentSquare.calculateColor(true);
                    }
                }
                if (!cellsToDelete.isEmpty()) {
                    cellsToDelete.forEach(e -> cells.remove(e));
                    cellsToDelete = new ArrayList<>();
                }
                if (!cellsToAdding.isEmpty()) {
                    cells.addAll(cellsToAdding);
                    cellsToAdding = new ArrayList<>();
                }
                for (Square square : squares) {
                    if (!square.items.isEmpty()) {
                        square.calculateEating(cells);
                    }
                }
//            System.out.print(System.currentTimeMillis() - now);
//            System.out.print("\n" + "red: " + red.toString() + "|" +
//                    "black: " + black.toString() + "|" +
//                    "green: " + green.toString() + "|" +
//                    "blue: " + blue.toString() + "|" + "\n");
            }
        }
    }

    public void squareAdding() { //начальная инициация всех клеток игрового поля
        int index = 0;
        for (int i = 0; i < boardSettings.getWidth(); i++) {
            for (int j = 0; j < boardSettings.getHeight(); j++) {
                squares.add(new Square(new Coordinates(i * boardSettings.getSquareSize(), j * boardSettings.getSquareSize()), 0, 0, new ArrayList<>()));
                mapSquareCoordinatesToIndex.put((String.valueOf(i) + "|" + String.valueOf(j)), index);
                index++;
            }
        }
    }

    public void cellAdding() {
        cells.add(new Cell("red", 1, 500, new Coordinates(150, 150), new DNA("abcdh", 0), Color.RED));
        cells.add(new Cell("black", 1, 500, new Coordinates(450, 150), new DNA("abcdh", 0), Color.BLACK));
        cells.add(new Cell("green", 1, 500, new Coordinates(150, 450), new DNA("h", 0), Color.GREEN));
        cells.add(new Cell("blue", 1, 500, new Coordinates(450, 450), new DNA("h", 0), Color.BLUE));
    }

    public void testFreeFoodInDistrict() {
        for (Square square : squares) {
            if (square.coordinates.x >= 100 && square.coordinates.x <= 200 && square.coordinates.y >= 100 && square.coordinates.y <= 200) {
                square.freeFood += 100;
                square.calculateColor(true);
            }
            if (square.coordinates.x >= 200 && square.coordinates.x <= 400 && square.coordinates.y >= 140 && square.coordinates.y <= 160) {
                square.freeFood += 100;
                square.calculateColor(true);
            }
            if (square.coordinates.x >= 400 && square.coordinates.x <= 500 && square.coordinates.y >= 100 && square.coordinates.y <= 200) {
                square.freeFood += 100;
                square.calculateColor(true);
            }
            if (square.coordinates.x >= 200 && square.coordinates.x <= 400 && square.coordinates.y >= 440 && square.coordinates.y <= 460) {
                square.freeFood += 100;
                square.calculateColor(true);
            }
            if (square.coordinates.x >= 100 && square.coordinates.x <= 200 && square.coordinates.y >= 400 && square.coordinates.y <= 500) {
                square.freeFood += 100;
                square.calculateColor(true);
            }
            if (square.coordinates.x >= 440 && square.coordinates.x <= 460 && square.coordinates.y >= 200 && square.coordinates.y <= 400) {
                square.freeFood += 100;
                square.calculateColor(true);
            }
            if (square.coordinates.x >= 400 && square.coordinates.x <= 500 && square.coordinates.y >= 400 && square.coordinates.y <= 500) {
                square.freeFood += 100;
                square.calculateColor(true);
            }
            if (square.coordinates.x >= 140 && square.coordinates.x <= 160 && square.coordinates.y >= 200 && square.coordinates.y <= 400) {
                square.freeFood += 100;
                square.calculateColor(true);
            }
        }

    }

    public void testCloseFoodInDistrict() {
        for (Square square : squares) {
            if (square.coordinates.x >= 700 && square.coordinates.x <= 800) {
                square.closeFood += 100;
                square.calculateColor(true);
            }
        }

    }

    public void freeFoodAdding() {
        for (int i = 0; i < foodAddingSettings.freeFoodAddingRate; i++) {
            int randomSquareIndex = rand.nextInt(squares.size() - 1);
            Square randomSquare = squares.get(randomSquareIndex);
            randomSquare.freeFoodOnLastFrame = randomSquare.freeFood;
            randomSquare.freeFood += foodAddingSettings.freeEatAddingByEveryTick;
            randomSquare.calculateColor(false);
        }
    }

    public void closeFoodAdding() {
        for (int i = 0; i < foodAddingSettings.closeFoodAddingRate; i++) {
            int randomSquareIndex = rand.nextInt(squares.size() - 1);
            Square randomSquare = squares.get(randomSquareIndex);
            randomSquare.closeFoodOnLastFrame = randomSquare.closeFood;
            randomSquare.closeFood += foodAddingSettings.closeEatAddingByEveryTick;
            randomSquare.calculateColor(false);
        }
    }

    public Square getCurrentSquare(Cell cell) {
        if (this.mapSquareCoordinatesToIndex.get((String.valueOf(cell.coordinates.x / this.boardSettings.getSquareSize())
                + "|" + String.valueOf(cell.coordinates.y / this.boardSettings.getSquareSize()))) == null) {
            int wow = 0;
            cellActions.teleportCell(cell);
        }
        if ((String.valueOf(cell.coordinates.x / this.boardSettings.getSquareSize())
                + "|" + String.valueOf(cell.coordinates.y / this.boardSettings.getSquareSize())) != null) {
            return squares.get(this.mapSquareCoordinatesToIndex.get((String.valueOf(cell.coordinates.x / this.boardSettings.getSquareSize())
                    + "|" + String.valueOf(cell.coordinates.y / this.boardSettings.getSquareSize()))));
        } else {
            return squares.get(this.mapSquareCoordinatesToIndex.get("100" + "|" + "100"));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}