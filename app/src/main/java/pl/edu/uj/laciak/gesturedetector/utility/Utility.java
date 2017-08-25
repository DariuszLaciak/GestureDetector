package pl.edu.uj.laciak.gesturedetector.utility;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by darek on 25.08.17.
 */

public class Utility {
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
