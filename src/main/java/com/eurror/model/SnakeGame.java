package com.eurror.model;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class SnakeGame {
    public static final int WIDTH = 24;
    public static final int HEIGHT = 24;
    private static final int MAX_PENDING_TURNS = 2;

    private int speed = 5;
    private Direction currentDir = Direction.RIGHT;
    private boolean gameOver = false;
    private final Random random = new Random();
    private final Deque<Direction> pendingDirections = new ArrayDeque<>();

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
        pendingDirections.clear();
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

        if (!pendingDirections.isEmpty()) {
            currentDir = pendingDirections.removeFirst();
        }

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
        Direction lastPlannedDirection = pendingDirections.peekLast();
        if (lastPlannedDirection == null) {
            lastPlannedDirection = currentDir;
        }

        if (dir == lastPlannedDirection || isOpposite(dir, lastPlannedDirection) || pendingDirections.size() >= MAX_PENDING_TURNS) {
            return;
        }

        pendingDirections.addLast(dir);
    }

    private boolean isOpposite(Direction first, Direction second) {
        return (first == Direction.UP && second == Direction.DOWN)
                || (first == Direction.DOWN && second == Direction.UP)
                || (first == Direction.LEFT && second == Direction.RIGHT)
                || (first == Direction.RIGHT && second == Direction.LEFT);
    }

    public boolean isGameOver() { return gameOver; }
    public int getSpeed() { return speed; }
    public int getScore() { return score; }
    public List<Point> getSnake() { return snake; }
    public Point getFood() { return food; }
}
