package com.neonlinks;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;
import java.io.*;
import java.net.*;

public class MainActivity extends Activity {
    EditText eUrl, eAlias;
    TextView tRes;
    Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        eUrl = (EditText) findViewById(R.id.url);
        eAlias = (EditText) findViewById(R.id.alias);
        tRes = (TextView) findViewById(R.id.res);
        btn = (Button) findViewById(R.id.go);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String u = eUrl.getText().toString();
                String a = eAlias.getText().toString();
                new Work().execute(u, a);
            }
        });
    }

    private class Work extends AsyncTask<String, Void, String> {
        protected void onPreExecute() { 
            btn.setEnabled(false); 
            tRes.setText("Загрузка..."); 
        }

        protected String doInBackground(String... p) {
            try {
                URL url = new URL("http://neonlinks.ct.ws/api.jpg");
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                c.setRequestProperty("User-Agent", "Mozilla/5.0");

                String data = "url=" + URLEncoder.encode(p[0], "UTF-8") + 
                             "&custom_code=" + URLEncoder.encode(p[1], "UTF-8") + 
                             "&action=shorten";

                OutputStream os = c.getOutputStream();
                os.write(data.getBytes("UTF-8"));
                os.flush();
                os.close();

                BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String l;
                while ((l = r.readLine()) != null) sb.append(l);
                
                JSONObject j = new JSONObject(sb.toString());
                if (j.getBoolean("success")) {
                    return j.getJSONObject("data").getString("short_url");
                } else {
                    return "Ошибка: " + j.getString("error");
                }
            } catch (Exception e) {
                return "Ошибка сети или API";
            }
        }

        protected void onPostExecute(String s) {
            btn.setEnabled(true);
            tRes.setText(s);
        }
    }
                                       }
