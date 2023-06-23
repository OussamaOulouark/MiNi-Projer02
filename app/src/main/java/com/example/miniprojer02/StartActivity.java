package com.example.miniprojer02;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class StartActivity extends AppCompatActivity {

    TextView tvStartActQuote, tvStartActAuthor;
    Spinner SPcolores;
    Button btnStartActPass;
    ToggleButton tbStartActPinUnpin;
    SharedPreferences sharedPreferences;
    ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SPcolores = findViewById(R.id.SPcolores);
        tvStartActQuote = findViewById(R.id.tvStartActQuote);
        tvStartActAuthor = findViewById(R.id.tvStartActAuthor);
        btnStartActPass = findViewById(R.id.btnStartActPass);
        tbStartActPinUnpin = findViewById(R.id.tbStartActPinUnpin);
        sharedPreferences = getSharedPreferences("pinned-quote", MODE_PRIVATE);

        String[] spinnerItems = {"Default", "LightSalmon", "Plum", "PaleGreen", "CornflowerBlue"};
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPcolores.setAdapter(spinnerAdapter);



        String savedColor = sharedPreferences.getString("selectedColor", "Default");
        int savedColorIndex = spinnerAdapter.getPosition(savedColor);
        SPcolores.setSelection(savedColorIndex);

        SPcolores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedColor = parent.getItemAtPosition(position).toString();

                switch (selectedColor) {
                    case "Default":
                        // Set the default background color here
                        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
                        break;
                    case "LightSalmon":
                        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.LightSalmon));
                        break;
                    case "Plum":
                        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.Plum));
                        break;
                    case "PaleGreen":
                        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.PaleGreen));
                        break;
                    case "CornflowerBlue":
                        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.CornflowerBlue));
                        break;
                }

                // Save the selected color to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("selectedColor", selectedColor);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected (if needed)
            }
        });

        String quote = sharedPreferences.getString("quote", null);

        if (quote == null) {
            getRandomQuote();
        } else {
            String author = sharedPreferences.getString("author", null);

            tvStartActQuote.setText(quote);
            tvStartActAuthor.setText(author);

            tbStartActPinUnpin.setChecked(true);
        }

        tbStartActPinUnpin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String quote = null;
                String author = null;

                if (isChecked) {
                    quote = tvStartActQuote.getText().toString();
                    author = tvStartActAuthor.getText().toString();
                } else {
                    getRandomQuote();
                }

                editor.putString("quote", quote);
                editor.putString("author", author);

                editor.apply();
            }
        });

        btnStartActPass.setOnClickListener(v -> {
            finish();
        });
    }

    private void getRandomQuote() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://dummyjson.com/quotes/random";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
