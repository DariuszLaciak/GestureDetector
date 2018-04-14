package pl.edu.uj.laciak.gesturedetector.view;

import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;
import pl.edu.uj.laciak.gesturedetector.utility.Utility;

/**
 * TODO: document your custom view class.
 */
public class CircleDrawingViewMake extends CircleDrawingView {
    private long timeToMakeGesture;
    private long timeToCalculateGesture;

    public CircleDrawingViewMake(Context context) {
        super(context);
    }

    public CircleDrawingViewMake(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleDrawingViewMake(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clear();
                timeToMakeGesture = SystemClock.elapsedRealtime();
                if (!initated)
                    setIDs();
                finalPoints = new ArrayList<>();
                View childS = getChildOnPosition(touchX, touchY);
                if (childS == null) {
                    childS = getNearestCircle(touchX, touchY);
                }
                if (childS != null) {
                    List<Float> points = getCenterPointsOfCircle(childS);
                    drawPath.moveTo(points.get(0), points.get(1));
                    foundStart = true;
                    prevX = points.get(0);
                    prevY = points.get(1);
                    if (prevPoint != childS.getId() && childS.getId() != -1) {
                        finalPoints.add(childS.getId());
                        prevPoint = childS.getId();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.setLastPoint(prevX, prevY);
                drawPath.lineTo(touchX, touchY);
                View child = getChildOnPosition(touchX, touchY);
                if (foundStart && child != null) {
                    List<Float> pts = getCenterPointsOfCircle(child);
                    drawPath.setLastPoint(prevX, prevY);
                    drawPath.lineTo(pts.get(0), pts.get(1));
                    prevX = pts.get(0);
                    prevY = pts.get(1);
                    if (prevPoint != child.getId()) {
                        finalPoints.add(child.getId());
                        prevPoint = child.getId();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                foundStart = false;
                drawPath.setLastPoint(prevX, prevY);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                timeToMakeGesture = SystemClock.elapsedRealtime() - timeToMakeGesture;
                timeToCalculateGesture = makeGesture();
                Log.d("MakeGesureTime", timeToMakeGesture + "");
                Log.d("CalculateGestureTime", timeToCalculateGesture + "");
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    private long makeGesture() {
        long timeToCalculate = SystemClock.elapsedRealtime();
        PrivateDatabase db = new PrivateDatabase(getContext());
        Cursor allGestures = db.getCircleGestures();
        int id = Utility.getDrawedGesture(allGestures, getFinalPoints());
        if (id != -1) {
            List<String> data = db.getCircleGestureData(id);
            Toast.makeText(getContext(), "Wykryto gest " + data.get(0) + " OPERACJA: " + data.get(1), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Nie wykryto gestu", Toast.LENGTH_SHORT).show();
        }
        return SystemClock.elapsedRealtime() - timeToCalculate;
    }
}
