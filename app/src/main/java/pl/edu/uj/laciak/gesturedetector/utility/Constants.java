package pl.edu.uj.laciak.gesturedetector.utility;

import android.graphics.Point;
import android.util.Log;

/**
 * Created by darek on 07.09.17.
 */

public abstract class Constants {
    public static final int CIRCLE_X = 6;
    public static final int CIRCLE_Y = 6;
    Integer[][] hash = new Integer[CIRCLE_X][CIRCLE_Y];

    public Constants() {
        for (int x = 0; x < CIRCLE_X; ++x) {
            for (int y = 0; y < CIRCLE_Y; ++y) {
                hash[x][y] = x * CIRCLE_X + y;
            }
        }
    }

    Point getPointOfHash(int point) {
        for (int x = 0; x < CIRCLE_X; ++x) {
            for (int y = 0; y < CIRCLE_Y; ++y) {
                if (hash[x][y] == point) {
                    return new Point(y, x);
                }
            }
        }
        Log.d("Nie znalazło", point + "");
        return null;
    }

    int getPointOfPoint(Point point) {
        return hash[point.y][point.x];
    }
}
