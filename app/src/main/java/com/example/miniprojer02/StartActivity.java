package com.example.miniprojer02;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class StartActivity extends AppCompatActivity {

    TextView tvStartActQuote, tvStartActAuthor;
    Button btnStartActPass;
    Random random = new Random();
    int max = 80;
    int min = 25;

    int randomquotes = random.nextInt(max + 1 - min) + min;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        tvStartActQuote = findViewById(R.id.tvStartActQuote);
        tvStartActAuthor = findViewById(R.id.tvStartActAuthor);
        btnStartActPass = findViewById(R.id.btnStartActPass);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://dummyjson.com/quotes/"+randomquotes;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {


                            tvStartActQuote.setText(response.getString("quote"));
                            tvStartActAuthor.setText(response.getString("author"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        queue.add(jsonObjectRequest);

        //endregion

        btnStartActPass.setOnClickListener(v -> {
            finish();
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}