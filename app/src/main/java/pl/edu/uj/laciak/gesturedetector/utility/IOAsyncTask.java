package pl.edu.uj.laciak.gesturedetector.utility;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by darek on 26.08.17.
 */

public class IOAsyncTask extends AsyncTask<Map, Void, String> {
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(Map... params) {
        return sendData(params[0]);
    }

    @Override
    protected void onPostExecute(String response) {
        Log.d("networking", response);
    }

    private String sendData(Map<String, String> data) {
        String url = "";
        try {
            FormBody.Builder formBody = new FormBody.Builder()
                    .add("action", data.get("action").toString());
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if (!entry.getKey().equals("action") && !entry.getKey().equals("url")) {
                    formBody.add(entry.getKey(), entry.getValue());
                } else if (entry.getKey().equals("url")) {
                    url = entry.getValue();
                }
            }
            FormBody body = formBody.build();

            Log.d("Async", url);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
