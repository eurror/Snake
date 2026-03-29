package com.eurror;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int TILE_SIZE = 25;
    private static final int WIDTH = 24;
    private static final int HEIGHT = 24;
    private static final int SCREEN_W = WIDTH * TILE_SIZE;
    private static final int SCREEN_H = HEIGHT * TILE_SIZE;

    // Speed control
    private long lastTick = 0;
    private int speed = 5; // changes per second

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Direction currentDir = Direction.RIGHT;
    private Direction nextDir = Direction.RIGHT; // to prevent reversing into body immediately
    private boolean gameOver = false;
    private final Random random = new Random();

    public static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private final List<Point> snake = new ArrayList<>();
    private final Point food = new Point(0, 0);
    private int score = 0;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        Canvas canvas = new Canvas(SCREEN_W, SCREEN_H);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(root, SCREEN_W, SCREEN_H);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.UP && currentDir != Direction.DOWN) {
                nextDir = Direction.UP;
            } else if (code == KeyCode.DOWN && currentDir != Direction.UP) {
                nextDir = Direction.DOWN;
            } else if (code == KeyCode.LEFT && currentDir != Direction.RIGHT) {
                nextDir = Direction.LEFT;
            } else if (code == KeyCode.RIGHT && currentDir != Direction.LEFT) {
                nextDir = Direction.RIGHT;
            } else if (code == KeyCode.R && gameOver) {
                restartGame();
            }
        });

        restartGame();

        AnimationTimer timer = new AnimationTimer() {
            public void handle(long now) {
                if (lastTick == 0) {
                    lastTick = now;
                    tick(gc);
                    return;
                }

                // 1_000_000_000 nanos in a sec
                if (now - lastTick > 1_000_000_000 / speed) {
                    lastTick = now;
                    tick(gc);
                }
            }
        };
        timer.start();

        primaryStage.setTitle("Snake Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void restartGame() {
        snake.clear();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        snake.add(new Point(WIDTH / 2 - 1, HEIGHT / 2));
        snake.add(new Point(WIDTH / 2 - 2, HEIGHT / 2));

        currentDir = Direction.RIGHT;
        nextDir = Direction.RIGHT;
        gameOver = false;
        score = 0;
        speed = 5;

        spawnFood();
    }

    private void spawnFood() {
        while (true) {
            int foodX = random.nextInt(WIDTH);
            int foodY = random.nextInt(HEIGHT);

            boolean onSnake = false;
            for (Point p : snake) {
                if (p.x == foodX && p.y == foodY) {
                    onSnake = true;
                    break;
                }
            }
            if (!onSnake) {
                food.x = foodX;
                food.y = foodY;
                break;
            }
        }
    }

    private void tick(GraphicsContext gc) {
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Arial", 50));
            gc.fillText("GAME OVER", (double) SCREEN_W / 2 - 140, (double) SCREEN_H / 2);
            gc.setFont(new Font("Arial", 20));
            gc.fillText("Score: " + score + "  Press R to Restart", (double) SCREEN_W / 2 - 130, (double) SCREEN_H / 2 + 40);
            return;
        }

        currentDir = nextDir;

        Point head = snake.getFirst();
        Point newHead = new Point(head.x, head.y);

        switch (currentDir) {
            case UP:
                newHead.y--;
                break;
            case DOWN:
                newHead.y++;
                break;
            case LEFT:
                newHead.x--;
                break;
            case RIGHT:
                newHead.x++;
                break;
        }

        // Wall collision
        if (newHead.x < 0 || newHead.x >= WIDTH || newHead.y < 0 || newHead.y >= HEIGHT) {
            gameOver = true;
            return;
        }

        // Body collision
        for (Point p : snake) {
            if (newHead.x == p.x && newHead.y == p.y) {
                gameOver = true;
                return;
            }
        }

        snake.addFirst(newHead);

        // Eat food
        if (newHead.x == food.x && newHead.y == food.y) {
            score += 10;
            if (speed < 20) {
                speed++; // increase speed as you eat
            }
            spawnFood();
        } else {
            // Remove tail if didn't eat
            snake.removeLast();
        }

        // Render
        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_W, SCREEN_H);

        // Food
        gc.setFill(Color.RED);
        gc.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Snake
        for (int i = 0; i < snake.size(); i++) {
            if (i == 0) {
                gc.setFill(Color.GREEN); // Head
            } else {
                gc.setFill(Color.LIGHTGREEN); // Body
            }
            Point p = snake.get(i);
            gc.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);
        }

        // Score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 24));
        gc.fillText("Score: " + score, 10, 30);
    }

    public static void main(String[] args) {
        launch(args);
    }
}