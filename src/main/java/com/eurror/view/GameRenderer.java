package com.eurror.view;

import com.eurror.model.Point;
import com.eurror.model.SnakeGame;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class GameRenderer {
    public static final int TILE_SIZE = 25;
    public static final int SCREEN_W = SnakeGame.WIDTH * TILE_SIZE;
    public static final int SCREEN_H = SnakeGame.HEIGHT * TILE_SIZE;

    public void render(GraphicsContext gc, SnakeGame game) {
        if (game.isGameOver()) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Arial", 50));
            gc.fillText("GAME OVER", (double) SCREEN_W / 2 - 140, (double) SCREEN_H / 2);
            gc.setFont(new Font("Arial", 20));
            gc.fillText("Score: " + game.getScore() + "  Press R to Restart", (double) SCREEN_W / 2 - 130, (double) SCREEN_H / 2 + 40);
            return;
        }

        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_W, SCREEN_H);

        // Food
        Point food = game.getFood();
        gc.setFill(Color.RED);
        gc.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Snake
        List<Point> snake = game.getSnake();
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
        gc.fillText("Score: " + game.getScore(), 10, 30);
    }
}
