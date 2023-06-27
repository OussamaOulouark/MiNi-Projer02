package com.example.miniprojer02;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import java.util.concurrent.ThreadLocalRandom;

public class StartActivity extends AppCompatActivity {
    TextView tvStartActQuote, tvStartActAuthor;
    Button btnStartActShowAllFavQuotes;
    ToggleButton tbStartActPinUnpin;
    SharedPreferences sharedPreferences;
    ImageView ivStartActIsFavorite;
    boolean isFavorite = false;
    FavoriteQuotesDbOpenHelper db;
    TextView tvStartActId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        tvStartActQuote = findViewById(R.id.tvStartActQuote);
        tvStartActAuthor = findViewById(R.id.tvStartActAuthor);
        btnStartActShowAllFavQuotes = findViewById(R.id.btnStartActShowAllFavQuotes);
        tbStartActPinUnpin = findViewById(R.id.tbStartActPinUnpin);
        ivStartActIsFavorite = findViewById(R.id.ivStartActIsFavorite);
        tvStartActId = findViewById(R.id.tvStartActId);

        //region Pin | Unpin Quote

        sharedPreferences = getSharedPreferences("pinned-pinnedQuote", MODE_PRIVATE);

        String pinnedQuote = sharedPreferences.getString("pinnedQuote", null);

        if (pinnedQuote == null) {
            getRandomQuote();
        } else {
            String author = sharedPreferences.getString("author", null);

            tvStartActQuote.setText(pinnedQuote);
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

                editor.putString("pinnedQuote", quote);
                editor.putString("author", author);

                editor.commit();
            }
        });

        //endregion

        //region Like | Dislike Quote

        db = new FavoriteQuotesDbOpenHelper(this);

        ivStartActIsFavorite.setOnClickListener(v -> {
            int id = Integer.parseInt(tvStartActId.getText().toString().substring(1));

            if (isFavorite) {
                ivStartActIsFavorite.setImageResource(R.drawable.dislike);

                db.delete(id);
            } else {
                ivStartActIsFavorite.setImageResource(R.drawable.like);

                String quote = tvStartActQuote.getText().toString();
                String author = tvStartActAuthor.getText().toString();

                db.add(new Quote(id, quote, author));
            }

            isFavorite = !isFavorite;

            //region ToDelete

            ArrayList<Quote> quotes = db.getAll();
            for (Quote quote : quotes) {
                Log.e("SQLite", quote.toString());
            }

            //endregion
        });

        //endregion

        btnStartActShowAllFavQuotes.setOnClickListener(v -> {
            Intent intent = new Intent(this , AllFavoriteQuotesActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void getRandomQuote() {
        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = "https://dummyjson.com/quotes/random";

//        int randomNumber = ThreadLocalRandom.current().nextInt(1, 3 + 1);
        String url = String.format("https://dummyjson.com/quotes/random");


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int id = response.getInt("id");
                            String quote = response.getString("quote");
                            String author = response.getString("author");

                            if (db.isFavorite(id))
                                ivStartActIsFavorite.setImageResource(R.drawable.like);
                            else
                                ivStartActIsFavorite.setImageResource(R.drawable.dislike);

                            tvStartActId.setText(String.format("#%d", id));
                            tvStartActQuote.setText(quote);
                            tvStartActAuthor.setText(author);
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
