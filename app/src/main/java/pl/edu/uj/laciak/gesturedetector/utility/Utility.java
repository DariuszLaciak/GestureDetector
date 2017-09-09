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
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.edu.uj.laciak.gesturedetector.R;
import pl.edu.uj.laciak.gesturedetector.db.PrivateDatabase;

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
        Bitmap returnedBitmap = Bitmap.createBitmap(700, 560, Bitmap.Config.ARGB_8888);
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
                    Log.d("takie same", id + "");
                    return id;
                }
            }
        } while (allGestures.moveToNext());
        return -1;
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


}
