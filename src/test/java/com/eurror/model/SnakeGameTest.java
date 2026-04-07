package com.eurror.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnakeGameTest {

    @Test
    void wrapsFromRightEdgeToLeftEdge() throws Exception {
        SnakeGame game = createGameWithSnake(Direction.RIGHT, List.of(
                new Point(SnakeGame.WIDTH - 1, 5)
        ));

        game.tick();

        Point head = game.getSnake().getFirst();
        assertEquals(0, head.x);
        assertEquals(5, head.y);
        assertFalse(game.isGameOver());
    }

    @Test
    void wrapsFromLeftEdgeToRightEdge() throws Exception {
        SnakeGame game = createGameWithSnake(Direction.LEFT, List.of(
                new Point(0, 7)
        ));

        game.tick();

        Point head = game.getSnake().getFirst();
        assertEquals(SnakeGame.WIDTH - 1, head.x);
        assertEquals(7, head.y);
        assertFalse(game.isGameOver());
    }

    @Test
    void wrapsFromTopEdgeToBottomEdge() throws Exception {
        SnakeGame game = createGameWithSnake(Direction.UP, List.of(
                new Point(4, 0)
        ));

        game.tick();

        Point head = game.getSnake().getFirst();
        assertEquals(4, head.x);
        assertEquals(SnakeGame.HEIGHT - 1, head.y);
        assertFalse(game.isGameOver());
    }

    @Test
    void wrapsFromBottomEdgeToTopEdge() throws Exception {
        SnakeGame game = createGameWithSnake(Direction.DOWN, List.of(
                new Point(9, SnakeGame.HEIGHT - 1)
        ));

        game.tick();

        Point head = game.getSnake().getFirst();
        assertEquals(9, head.x);
        assertEquals(0, head.y);
        assertFalse(game.isGameOver());
    }

    @Test
    void canEatFoodImmediatelyAfterWrapping() throws Exception {
        SnakeGame game = createGameWithSnake(Direction.RIGHT, List.of(
                new Point(SnakeGame.WIDTH - 1, 10)
        ));
        game.getFood().x = 0;
        game.getFood().y = 10;

        game.tick();

        Point head = game.getSnake().getFirst();
        assertEquals(0, head.x);
        assertEquals(10, head.y);
        assertEquals(10, game.getScore());
        assertEquals(2, game.getSnake().size());
        assertFalse(game.isGameOver());
    }

    @Test
    void stillDiesWhenWrappingIntoBody() throws Exception {
        SnakeGame game = createGameWithSnake(Direction.RIGHT, List.of(
                new Point(SnakeGame.WIDTH - 1, 3),
                new Point(0, 3),
                new Point(1, 3)
        ));

        game.tick();

        assertTrue(game.isGameOver());
    }

    @Test
    void buffersRapidTurnsAcrossConsecutiveTicks() throws Exception {
        SnakeGame game = createGameWithSnake(Direction.RIGHT, List.of(
                new Point(5, 5),
                new Point(4, 5),
                new Point(3, 5)
        ));

        game.setNextDirection(Direction.UP);
        game.setNextDirection(Direction.LEFT);

        game.tick();

        Point firstHead = game.getSnake().getFirst();
        assertEquals(5, firstHead.x);
        assertEquals(4, firstHead.y);
        assertFalse(game.isGameOver());

        game.tick();

        Point secondHead = game.getSnake().getFirst();
        assertEquals(4, secondHead.x);
        assertEquals(4, secondHead.y);
        assertFalse(game.isGameOver());
    }

    @Test
    void ignoresReverseTurnAgainstQueuedDirection() throws Exception {
        SnakeGame game = createGameWithSnake(Direction.RIGHT, List.of(
                new Point(5, 5),
                new Point(4, 5),
                new Point(3, 5)
        ));

        game.setNextDirection(Direction.UP);
        game.setNextDirection(Direction.DOWN);

        game.tick();
        Point firstHead = game.getSnake().getFirst();
        assertEquals(5, firstHead.x);
        assertEquals(4, firstHead.y);

        game.tick();
        Point secondHead = game.getSnake().getFirst();
        assertEquals(5, secondHead.x);
        assertEquals(3, secondHead.y);
        assertFalse(game.isGameOver());
    }

    @Test
    void ignoresImmediateReverseTurn() throws Exception {
        SnakeGame game = createGameWithSnake(Direction.RIGHT, List.of(
                new Point(5, 5),
                new Point(4, 5),
                new Point(3, 5)
        ));

        game.setNextDirection(Direction.LEFT);

        game.tick();

        Point head = game.getSnake().getFirst();
        assertEquals(6, head.x);
        assertEquals(5, head.y);
        assertFalse(game.isGameOver());
    }

    private SnakeGame createGameWithSnake(Direction direction, List<Point> points) throws Exception {
        SnakeGame game = new SnakeGame();
        game.getSnake().clear();
        for (Point point : points) {
            game.getSnake().add(new Point(point.x, point.y));
        }
        setDirection(game, direction);
        return game;
    }

    private void setDirection(SnakeGame game, Direction direction) throws Exception {
        Field currentDir = SnakeGame.class.getDeclaredField("currentDir");
        currentDir.setAccessible(true);
        currentDir.set(game, direction);

        Field pendingDirections = SnakeGame.class.getDeclaredField("pendingDirections");
        pendingDirections.setAccessible(true);
        ((java.util.Deque<?>) pendingDirections.get(game)).clear();
    }
}

