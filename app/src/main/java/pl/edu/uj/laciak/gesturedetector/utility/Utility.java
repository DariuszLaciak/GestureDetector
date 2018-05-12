package pl.edu.uj.laciak.gesturedetector.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.edu.uj.laciak.gesturedetector.R;
import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;
import pl.edu.uj.laciak.gesturedetector.view.DrawingView;

/**
 * Created by darek on 25.08.17.
 */

public class Utility {
    public static NetworkState checkWifiOnAndConnected(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if (wifiInfo.getNetworkId() == -1) {
                return NetworkState.WIFI_DISCONNECTED;
            }
            return NetworkState.WIFI_CONNECTED;
        } else {
            return NetworkState.WIFI_DISABLED;
        }
    }

    public static void buildConnectToWifiPopup(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        // set title
        alertDialogBuilder.setTitle("Wifi Settings");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to enable WIFI ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //enable wifi
                        wifiManager.setWifiEnabled(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //disable wifi
                        wifiManager.setWifiEnabled(false);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static void createConnectToNetworkDialog(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Wifi Settings");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to connect to a WIFI network?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static Bitmap getBitmapFromView(View view) {

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    public static Bitmap getBitmapFromFile(String filename, Context context) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = context.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            return null;
        }
        return bitmap;
    }

    public static void fillSpinner(PrivateDatabase db, Context context, Spinner spinner) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "getActions");
        map.put("url", db.getServerUrl());
        List<String> items = new ArrayList<>();
//        try {
//            String response = new IOAsyncTask().execute(map).get();
//            Log.d("Server", response);
//            JSONParser parser = new JSONParser();
//            JSONArray ja = (JSONArray) parser.parse(response);
//
//            if (ja != null) {
//                for (Object o : ja) {
//                    items.add(o.toString());
//                }
//            }
        items.add("nowa operacja");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
//        } catch (InterruptedException | ParseException | ExecutionException e) {
//            e.printStackTrace();
//        }
    }

    public static int getDrawedGesture(Cursor allGestures, List<Integer> justDrawnGesture) {
        List<Integer> minimalJustDrawn = Node.cutToMinimum(justDrawnGesture);

        allGestures.moveToFirst();
        do {
            Node justDrawnNode = Node.buildTree(minimalJustDrawn);
            String points = allGestures.getString(1);
            List<String> pointsLise = parsePoints(points);
            int id = allGestures.getInt(0);
            List<Integer> minimalGesture = Node.cutToMinimum(parseIntegerList(pointsLise));
            if (!minimalGesture.contains(-1)) {
                Node gesture = Node.buildTree(minimalGesture);
                if (Node.doesNodeContainsOther(justDrawnNode, gesture)) {
                    return id;
                }
            }
        } while (allGestures.moveToNext());
        return -1;
    }

    public static String saveBitmap(DrawingView view) {
        OutputStream os = null;
        String filename = "Drawing_" + SystemClock.elapsedRealtime() + ".jpg";
        try {
            os = view.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (Utility.getBitmapFromView(view).compress(Bitmap.CompressFormat.JPEG, 100, os)) {
            Toast.makeText(view.getContext(), "Pomyślnie dodano gest", Toast.LENGTH_SHORT).show();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public static List<Integer> parseIntegerList(List<String> points) {
        List<Integer> list = new ArrayList<>();
        for (String s : points) list.add(Integer.valueOf(s.trim()));

        return list;
    }

    public static List<String> parsePoints(String points) {
        points = points.replaceAll("^\\[|]$", "");
        List<String> myList = new ArrayList<String>(Arrays.asList(points.split(",")));
        return myList;
    }

    public static void saveNextGestureSample(Context context, DrawingView view, String nameInput, String types, int gestureId) {
        PrivateDatabase db = new PrivateDatabase(context);
        String savePath = Utility.saveBitmap(view);
        String name = nameInput;
        String action = types;
        Log.d("DBG", savePath);
        Log.d("DBG", name);
        Log.d("DBG", action);
        db.saveNewDrawingGesure(name, action, savePath, gestureId);
    }

    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
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

    public static Bitmap invert(Bitmap src)
    {
        int height = src.getHeight();
        int width = src.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        ColorMatrix matrixGrayscale = new ColorMatrix();
        matrixGrayscale.setSaturation(0);

        ColorMatrix matrixInvert = new ColorMatrix();
        matrixInvert.set(new float[]
                {
                        -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                });
        matrixInvert.preConcat(matrixGrayscale);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixInvert);
        paint.setColorFilter(filter);

        canvas.drawBitmap(src, 0, 0, paint);
        return bitmap;
    }

    public static byte[] getBytesFromBm(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        return bos.toByteArray();
    }



}
