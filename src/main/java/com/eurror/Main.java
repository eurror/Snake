package com.eurror;

import com.eurror.model.Direction;
import com.eurror.model.SnakeGame;
import com.eurror.view.GameRenderer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private long lastTick = 0;
    private SnakeGame game;
    private GameRenderer renderer;

    @Override
    public void start(Stage primaryStage) {
        game = new SnakeGame();
        renderer = new GameRenderer();

        VBox root = new VBox();
        Canvas canvas = new Canvas(GameRenderer.SCREEN_W, GameRenderer.SCREEN_H);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(root, GameRenderer.SCREEN_W, GameRenderer.SCREEN_H);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.UP) {
                game.setNextDirection(Direction.UP);
            } else if (code == KeyCode.DOWN) {
                game.setNextDirection(Direction.DOWN);
            } else if (code == KeyCode.LEFT) {
                game.setNextDirection(Direction.LEFT);
            } else if (code == KeyCode.RIGHT) {
                game.setNextDirection(Direction.RIGHT);
            } else if (code == KeyCode.R && game.isGameOver()) {
                game.restartGame();
            }
        });

        // Doing initial render so we don't have to wait for the first tick
        renderer.render(gc, game);

        AnimationTimer timer = new AnimationTimer() {
            public void handle(long now) {
                if (lastTick == 0) {
                    lastTick = now;
                    game.tick();
                    renderer.render(gc, game);
                    return;
                }

                // 1_000_000_000 nanos in a sec
                if (now - lastTick > 1_000_000_000 / game.getSpeed()) {
                    lastTick = now;
                    game.tick();
                    renderer.render(gc, game);
                }
            }
        };
        timer.start();

        primaryStage.setTitle("Snake Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}