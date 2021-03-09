package Server.utils;

import java.util.Random;

public class Utils {
    // Timeout upper/lower bound
    private static final int TIMEOUTMIN = 200;
    private static final int TIMEOUTMAX = 300;

    public static int getRandomTimeoutVal(int min, int max) {
        Random timeoutVal = new Random();
        int timeout = timeoutVal.nextInt(max) % (max - min + 1) + min;
        return timeout;
    }
    public static int getRandomTimeoutVal() {
       return getRandomTimeoutVal(TIMEOUTMIN, TIMEOUTMAX);
    }

}
