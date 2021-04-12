package tech.fedorov.predictor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    TextInputEditText inp;
    Button btn;
    TextView tv;
    Switch sw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        inp = (TextInputEditText) findViewById(R.id.textInputEditText);
        tv = (TextView) findViewById(R.id.textView);
        inp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String ss = s + "";
                if (String.valueOf(inp.getText()).equals("")) {
                } else {
                    predict(ss);
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = String.valueOf(inp.getText());
                String response =  String.valueOf(tv.getText());
                if (pos == 1) {
                    userInput = userInput + " " + response;
                } else {
                    userInput = userInput.substring(0, userInput.length() + pos) + response;
                }
                inp.setText(userInput);
                inp.setSelection(inp.getText().length());
            }
        });
    }
    int pos;
    void predict(String text) {
        final String request = "https://predictor.yandex.net/api/v1/predict.json/complete" +
                "?key=pdct.1.1.20210412T141130Z.7fe24c3464fb114c.a91bd86212d96a2ed24c9e0a44fc721cd4e118ea" +
                "&q=" + text +
                "&lang=ru";
        new Thread(new Runnable() {
            @Override
            public void run() {
                URLConnection connection = null;
                try {
                    connection = new URL(request).openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scanner in = null;
                try {
                    in = new Scanner(connection.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final String response = in.nextLine();


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String jsonString = response; //assign your JSON String here
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(jsonString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONArray arr = null; // notice that `"posts": [...]`
                        try {
                            arr = obj.getJSONArray("text");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < arr.length(); i++)
                        {
                            tv.setText("");
                            try {
                                tv.append(arr.get(i).toString());
                                pos = obj.getInt("pos");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }
}

