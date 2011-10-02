
package simple.common;

public enum Direction {

    STOP(0, 0, 0) {

        @Override
        public Direction nextDirection() {
            return LEFT;
        }
    },
    UP(1, 0, -1) {

        @Override
        public Direction nextDirection() {
            return RIGHT;
        }
    },
    RIGHT(2, 1, 0) {

        @Override
        public Direction nextDirection() {
            return DOWN;
        }
    },
    DOWN(3, 0, 1) {

        @Override
        public Direction nextDirection() {
            return LEFT;
        }
    },
    LEFT(4, -1, 0) {

        @Override
        public Direction nextDirection() {
            return UP;
        }
    };
    private final int val;
    private final int dx;
    private final int dy;

    public static Direction build(int val) {
        switch (val) {
            case 1:
                return UP;

            case 2:
                return RIGHT;

            case 3:
                return DOWN;

            case 4:
                return LEFT;

            default:
                return STOP;

        }
    }

    public int getdx() {
        return dx;
    }

    public int getdy() {
        return dy;
    }

    public static Direction rand() {
        return Direction.values()[Rand.rand(4) + 1];
    }

    Direction(int val, int dx, int dy) {
        this.val = val;
        this.dx = dx;
        this.dy = dy;
    }

    public int get() {
        return val;
    }

    public Direction oppositeDirection() {
        switch (this) {
            case UP:
                return DOWN;
            case RIGHT:
                return LEFT;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            default:
                return STOP;
        }
    }

    public abstract Direction nextDirection();
}
