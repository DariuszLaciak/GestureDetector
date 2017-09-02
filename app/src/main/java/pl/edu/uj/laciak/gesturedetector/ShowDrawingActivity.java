package pl.edu.uj.laciak.gesturedetector;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;

public class ShowDrawingActivity extends Activity {
    PrivateDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_drawing);
        db = new PrivateDatabase(this);

        Button back = findViewById(R.id.backShowDrawing);
        Cursor allGestures = db.getDrawingDestures();
        GridLayout grid = findViewById(R.id.gridImages);
        prepareGrid(grid, allGestures);
        allGestures.close();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void prepareGrid(GridLayout gridLayout, Cursor images) {
        gridLayout.removeAllViews();

        int total = images.getCount();
        int column = 3;
        int row = total / column;
        Log.d("ROW", row + "");
        gridLayout.setColumnCount(column);
        gridLayout.setRowCount(row + 1);
        images.moveToFirst();
        for (int i = 0, c = 0, r = 0; i < total; i++, c++) {
            if (c == column) {
                c = 0;
                r++;
            }
            String filename = images.getString(1);
            Log.d("DB", filename);
            ImageView oImageView = new ImageView(this);
            Bitmap bm = resize(getBitmapFromFile(filename), 220, 220);
            Log.d("image", bm.getWidth() + "");
            oImageView.setImageBitmap(bm);

            oImageView.setLayoutParams(new ViewGroup.LayoutParams(220, 220));

            GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            gridLayout.setUseDefaultMargins(true);
            gridLayout.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
            /*if (r == 0 && c == 0) {
                Log.e("", "spec");
                colspan = GridLayout.spec(GridLayout.UNDEFINED, 2);
                rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 2);
            }*/
            GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                    rowSpan, colspan);
            gridLayout.addView(oImageView, gridParam);
            images.moveToNext();
        }
    }

    private void setOnClickListeners(GridView gridView) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // DO something

            }
        });
    }

    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    private Bitmap getBitmapFromFile(String filename) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            return null;
        }
        return bitmap;
    }
}
