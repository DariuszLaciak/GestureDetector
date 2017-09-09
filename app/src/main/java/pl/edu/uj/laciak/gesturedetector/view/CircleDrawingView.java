package pl.edu.uj.laciak.gesturedetector.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import pl.edu.uj.laciak.gesturedetector.R;
import pl.edu.uj.laciak.gesturedetector.utility.CircleGridViewAdapter;
import pl.edu.uj.laciak.gesturedetector.utility.CircleItem;
import pl.edu.uj.laciak.gesturedetector.utility.Utility;

/**
 * Created by darek on 05.09.17.
 */

public class CircleDrawingView extends GridView {
    protected Path drawPath;
    protected Paint drawPaint, canvasPaint;
    protected Canvas drawCanvas;
    protected Bitmap canvasBitmap;

    float prevX, prevY;
    int prevPoint = 0;
    ArrayList<CircleItem> data;

    boolean foundStart = false;

    List<Integer> finalPoints;
    boolean initated = false;

    public CircleDrawingView(Context context) {
        super(context);
        setupDrawing(context);
    }

    public CircleDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing(context);
    }

    public CircleDrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupDrawing(context);
    }

    public void setupDrawing(Context context) {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLACK);

        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
        finalPoints = new ArrayList<>();
        data = generateCircles();
        this.setAdapter(new CircleGridViewAdapter(context, R.layout.circle, data));
        this.setNumColumns(6);
    }

    private ArrayList<CircleItem> generateCircles() {
        ArrayList<CircleItem> list = new ArrayList<>();
        for (int i = 0; i < 36; ++i) {
            list.add(new CircleItem(i));
        }
        return list;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clear();
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
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void clear() {
        drawCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
    }

    @Nullable
    protected View getChildOnPosition(float x, float y) {
        int childNum = this.getChildCount();
        for (int i = 0; i < childNum; ++i) {
            View child = this.getChildAt(i);
            if (child.getX() <= x &&
                    child.getX() + child.getWidth() >= x &&
                    child.getY() <= y &&
                    child.getY() + child.getHeight() >= y) {
                //child.setId(i);
                return child;
            }
        }
        return null;
    }

    protected void setIDs() {
        initated = true;
        int childNum = this.getChildCount();
        for (int i = 0; i < childNum; ++i) {
            View child = this.getChildAt(i);
            child.setId(i);
        }
    }

    protected List<Float> getCenterPointsOfCircle(View view) {
        List<Float> list = new ArrayList<>();
        list.add(view.getX() + view.getWidth() / 2);
        list.add(view.getY() + view.getHeight() / 2);
        return list;
    }

    protected View getNearestCircle(float x, float y) {
        int childNum = this.getChildCount();
        float minX = 1000;
        float minY = 1000;
        View nearest = null;
        for (int i = 0; i < childNum; ++i) {
            View child = this.getChildAt(i);
            if (Math.abs(child.getX() + child.getWidth() / 2 - x) < minX ||
                    Math.abs(child.getY() + child.getHeight() / 2 - y) < minY) {
                nearest = child;
                minX = Math.abs(child.getX() + child.getWidth() / 2 - x);
                minY = Math.abs(child.getY() + child.getHeight() / 2 - y);
            }
        }
        return nearest;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    public List<Integer> getFinalPoints() {
        return finalPoints;
    }

    public Path getDrawPath() {
        return drawPath;
    }

    public Canvas getDrawCanvas() {
        return drawCanvas;
    }

    public Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }

    public Paint getDrawPaint() {
        return drawPaint;
    }

    public Paint getCanvasPaint() {
        return canvasPaint;
    }

    public Bitmap createDottedBitmat(List<String> points) {
        Bitmap bm;
        //Bitmap temp = view.getCanvasBitmap();
        List<Float> startPoints = getPointCenter(points.get(0));
        float prevX = startPoints.get(0), prevY = startPoints.get(1);
        for (String point : points) {
            List<Float> pts = getPointCenter(point);
            drawPath.setLastPoint(prevX, prevY);
            prevX = pts.get(0);
            prevY = pts.get(1);
            drawPath.lineTo(prevX, prevY);
        }

        Paint drawPaint = new Paint();
        drawPaint.setColor(Color.BLACK);
        drawCanvas.drawPath(drawPath, drawPaint);
        bm = Utility.getBitmapFromView(this);
        return bm;
    }

    private List<Float> getPointCenter(String point) {
        int dots = this.getChildCount();
        List<Float> list = new ArrayList<>();
        Log.d("DOTS!", dots + "");
        for (int i = 0; i < dots; ++i) {
            View child = this.getChildAt(i);
            if (i == Integer.parseInt(point)) {
                list.add(child.getX() + child.getWidth());
                list.add(child.getY() + child.getHeight());
                break;
            }
        }

        return list;
    }
}
