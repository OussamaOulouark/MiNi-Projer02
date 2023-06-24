package com.example.miniprojer02;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.miniprojer02.Models.Quote;
import com.example.miniprojer02.db.FavoriteQuotesDbOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {
    boolean isFavorite = false;

    TextView tvStartActQuote, tvStartActAuthor;
    Button btnStartActPass;
    ToggleButton tbStartActPinUnpin;
    ImageView ivStartActIsFavorite;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        tvStartActQuote = findViewById(R.id.tvStartActQuote);
        tvStartActAuthor = findViewById(R.id.tvStartActAuthor);
        btnStartActPass = findViewById(R.id.btnStartActPass);
        tbStartActPinUnpin = findViewById(R.id.tbStartActPinUnpin);
        ivStartActIsFavorite = findViewById(R.id.ivStartActIsFavorite);



        //region like dislike

        ivStartActIsFavorite.setOnClickListener(v -> {

            if (isFavorite) {
                ivStartActIsFavorite.setImageResource(R.drawable.like);
            } else {
                ivStartActIsFavorite.setImageResource(R.drawable.dislike);

            }
            isFavorite = !isFavorite;


        });
        //endregion
        //region ToDelete : Just for test

        FavoriteQuotesDbOpenHelper db = new FavoriteQuotesDbOpenHelper(this);
//        db.add(new Quote(1, "q1", "a1"));
//        db.add(new Quote(20, "q2", "a2"));
//        db.add(new Quote(30, "q3", "a3"));
        db.delete(20);


        ArrayList<Quote> quotes =  db.getAll();


        for (Quote quote : quotes) {
            Log.e("SQLite", quote.toString());
        }
        //endregion

        //region pin quotes|unpin

        sharedPreferences = getSharedPreferences("pinned-pinnedquote", MODE_PRIVATE);

        String pinnedquote = sharedPreferences.getString("pinnedquote", null);

        if (pinnedquote == null) {
            getRandomQuote();
        } else {
            String author = sharedPreferences.getString("author", null);

            tvStartActQuote.setText(pinnedquote);
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

                editor.putString("pinnedquote", quote);
                editor.putString("author", author);

                editor.commit();
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



    //endregion
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
