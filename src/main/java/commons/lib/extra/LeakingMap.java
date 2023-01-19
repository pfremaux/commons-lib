package commons.lib.extra;

public class LeakingMap {

    private final int[][] map = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 8, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    private final int[][] mapFlag;

    public LeakingMap() {
        this.mapFlag = new int[map.length][map[0].length];
        for (int i = 0; i < this.mapFlag.length; i++) {
            for (int i1 = 0; i1 < this.mapFlag[i].length; i1++) {
                mapFlag[i][i1] = 0;
            }
        }
    }

    public static void main(String[] args) {
        LeakingMap leakingMap = new LeakingMap();
        int col = 7;
        int row = 10;
        int tmp = leakingMap.map[col][row];
        leakingMap.leak(col, row, 10);

        leakingMap.map[col][row] = 0;
        for (int i = 0; i < leakingMap.map.length; i++) {
            for (int i1 = 0; i1 < leakingMap.map[i].length; i1++) {
                System.out.print(leakingMap.map[i][i1]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public void leak(int x, int y, int power) {
        if (!isValidPoint(x, y)) {
            return;
        }
        /*if (mapFlag[x][y] != 0) {
            return;
        }*/
        if (power <= 0) {
            return;
        }
        int noChange = 0;
        for (int distance = 0; distance < 1000 && noChange < 9; distance++) {
            noChange = 0;
            noChange += updateMapIfValid(x - distance, y - distance, power - distance);
            noChange += updateMapIfValid(x - distance, y, power - distance);
            noChange += updateMapIfValid(x - distance, y + distance, power - distance);
            for (int yPrime = y - distance; yPrime < y + distance; yPrime++) {
                updateMapIfValid(x - distance, yPrime, power - distance);
            }

            noChange += updateMapIfValid(x, y - distance, power - distance);

            for (int xPrime = x - distance; xPrime < x + distance; xPrime++) {
                updateMapIfValid(xPrime, y - distance, power - distance);
            }

            noChange += updateMapIfValid(x, y, power);
            noChange += updateMapIfValid(x, y + distance, power - distance);

            for (int xPrime = x - distance; xPrime < x + distance; xPrime++) {
                updateMapIfValid(xPrime, y + distance, power - distance);
            }



            noChange += updateMapIfValid(x + distance, y - distance, power - distance);
            noChange += updateMapIfValid(x + distance, y, power - distance);
            noChange += updateMapIfValid(x + distance, y + distance, power - distance);
            for (int yPrime = y - distance; yPrime < y + distance; yPrime++) {
                updateMapIfValid(x+ distance, yPrime, power - distance);
            }
        }
        

        /*leak(x - 1, y - 1, mapFlag[x - 1][y - 1]);
        leak(x - 1, y, mapFlag[x - 1][y]);
        leak(x - 1, y + 1, mapFlag[x - 1][y + 1]);

        leak(x, y - 1, mapFlag[x][y - 1]);
        // NO leak(x, y, mapFlag[x][y]);
        leak(x, y + 1, mapFlag[x][y + 1]);

        leak(x + 1, y - 1, mapFlag[x + 1][y - 1]);
        leak(x + 1, y, mapFlag[x + 1][y]);
        leak(x + 1, y + 1, mapFlag[x + 1][y + 1]);*/
    }

    private int updateMapIfValid(int x, int y, int power) {
        if (!isValidPoint(x, y)) {
            return 1;
        }
        if (mapFlag[x][y] != 0) {
            return 1;
        }
        int result = power - map[x][y];
        if (result <= 0) {
            return 1;
        }
        map[x][y] = result;
        mapFlag[x][y] = 1;
        return 0;
    }

    private boolean isValidPoint(int x, int y) {
        if (x >= map.length) {
            return false;
        }
        if (x < 0) {
            return false;
        }
        if (y >= map[x].length) {
            return false;
        }
        return y >= 0;
    }

}
