package pl.edu.uj.laciak.gesturedetector.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;
import pl.edu.uj.laciak.gesturedetector.utility.CompareGestures;
import pl.edu.uj.laciak.gesturedetector.utility.Utility;

/**
 * TODO: document your custom view class.
 */
public class DrawingViewMake extends DrawingView {

    public DrawingViewMake(Context context) {
        super(context);
    }

    public DrawingViewMake(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawingViewMake(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clear();
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                compareImages();
                Log.d("View", "narysowano gest");
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    private void compareImages() {
        Map<Integer, Bitmap> map = new HashMap<>();
        Bitmap madeBitmap = Utility.getBitmapFromView(this);
        PrivateDatabase db = new PrivateDatabase(getContext());
        Cursor allGestures = db.getDrawingDestures();
        allGestures.moveToFirst();
        do {
            map.put(allGestures.getInt(0), Utility.getBitmapFromFile(allGestures.getString(1), getContext()));
        }
        while (allGestures.moveToNext());
        CompareGestures compareGestures = new CompareGestures(map, madeBitmap);
        try {
            int winner = compareGestures.compare();
            if (winner == -1) {
                Toast.makeText(getContext(), "Nie znaleziono gestu", Toast.LENGTH_SHORT).show();
            } else {
                allGestures.moveToFirst();
                do {
                    if (allGestures.getInt(0) == winner) {
                        break;
                    }
                } while (allGestures.moveToNext());
                Log.d("LOG!", "Wygrał gest " + allGestures.getString(2));
                Toast.makeText(getContext(), "Wygryto gest: " + allGestures.getString(2) + " OPERACJA: " + allGestures.getString(3), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d("LOG!", "Nie znaleziono gestu");
        }
    }
}
