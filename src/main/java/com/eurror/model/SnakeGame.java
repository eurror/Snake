package com.eurror.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame {
    public static final int WIDTH = 24;
    public static final int HEIGHT = 24;

    private int speed = 5;
    private Direction currentDir = Direction.RIGHT;
    private Direction nextDir = Direction.RIGHT;
    private boolean gameOver = false;
    private final Random random = new Random();
    
    private final List<Point> snake = new ArrayList<>();
    private final Point food = new Point(0, 0);
    private int score = 0;

    public SnakeGame() {
        restartGame();
    }

    public void restartGame() {
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

    public void tick() {
        if (gameOver) {
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

        newHead.x = (newHead.x + WIDTH) % WIDTH;
        newHead.y = (newHead.y + HEIGHT) % HEIGHT;

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
    }
    
    public void setNextDirection(Direction dir) {
        if (dir == Direction.UP && currentDir != Direction.DOWN) {
            nextDir = Direction.UP;
        } else if (dir == Direction.DOWN && currentDir != Direction.UP) {
            nextDir = Direction.DOWN;
        } else if (dir == Direction.LEFT && currentDir != Direction.RIGHT) {
            nextDir = Direction.LEFT;
        } else if (dir == Direction.RIGHT && currentDir != Direction.LEFT) {
            nextDir = Direction.RIGHT;
        }
    }

    public boolean isGameOver() { return gameOver; }
    public int getSpeed() { return speed; }
    public int getScore() { return score; }
    public List<Point> getSnake() { return snake; }
    public Point getFood() { return food; }
}
