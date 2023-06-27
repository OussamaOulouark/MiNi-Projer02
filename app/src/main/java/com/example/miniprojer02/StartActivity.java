package com.example.miniprojer02;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.miniprojer02.Models.Colors;
import com.example.miniprojer02.Models.Quote;
import com.example.miniprojer02.databsecolors.colersdb;
import com.example.miniprojer02.db.FavoriteQuotesDbOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {
    private final static int INVALIDE_ID = -1;

    TextView tvStartActQuote, tvStartActAuthor;
    Button btnStartActPass;
    ToggleButton tbStartActPinUnpin;
    SharedPreferences sharedPreferences;
    ImageView ivStartActIsFavorite;
    FavoriteQuotesDbOpenHelper db;
    TextView tvStartActId;
    colersdb db1;
    View backgroundc;
    ArrayList<Colors> colors=new ArrayList<>();

    Spinner Spcolors;
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        tvStartActQuote = findViewById(R.id.tvStartActQuote);
        tvStartActAuthor = findViewById(R.id.tvStartActAuthor);
        btnStartActPass = findViewById(R.id.btnStartActPass);
        tbStartActPinUnpin = findViewById(R.id.tbStartActPinUnpin);
        ivStartActIsFavorite = findViewById(R.id.ivStartActIsFavorite);
        tvStartActId = findViewById(R.id.tvStartActId);
        Spcolors = findViewById(R.id.Spcolors);
        backgroundc = findViewById(R.id.backgroundc);



        db1 =new colersdb(this);
        db1.AddCOLOR(new Colors("Default       ", "#FF000000"));
        db1.AddCOLOR(new Colors("LightSalmon   ", "#FFA07A"));
        db1.AddCOLOR(new Colors("Plum          ", "#DDA0DD"));
        db1.AddCOLOR(new Colors("PaleGreen     ", "#98FB98"));
        db1.AddCOLOR(new Colors("CornflowerBlue", "#6495ED"));

        colors = db1.getAll();
        ArrayAdapter<Colors> adapter=new ArrayAdapter<>(StartActivity.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,colors);
        Spcolors.setAdapter(adapter);

        Spcolors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int Position = 0;
                switch (position){
                    case 0:
                        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#FFFFFF"));
                        Position=0;
                        break;
                    case 1:
                        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#FFA07A"));
                        Position=1;

                        break;
                    case 2:
                        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#DDA0DD"));
                        Position = 2;

                        break;
                    case 3:
                        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#98FB98"));
                        Position = 3;

                        break;
                    case 4:
                        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#6495ED"));
                        Position = 4;

                        break;
                }
                db1.addbgcolor("Bgcolor", String.valueOf(Position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        //region Persistence Objects

        db = new FavoriteQuotesDbOpenHelper(this);
        sharedPreferences = getSharedPreferences("pinned-quote", MODE_PRIVATE);

        //endregion

        //region Pin | Unpin Quote

        int pinnedQuoteId = sharedPreferences.getInt("id", INVALIDE_ID);

        if (pinnedQuoteId == INVALIDE_ID) {
            getRandomQuote();
        } else {
            String quote = sharedPreferences.getString("quote", null);
            String author = sharedPreferences.getString("author", null);

            tvStartActId.setText(String.format("#%d", pinnedQuoteId));
            tvStartActQuote.setText(quote);
            tvStartActAuthor.setText(author);

            ivStartActIsFavorite.setImageResource(db.isFavorite(pinnedQuoteId) ? R.drawable.like : R.drawable.dislike);

            tbStartActPinUnpin.setChecked(true);
        }

        tbStartActPinUnpin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int pinnedQuoteId = INVALIDE_ID;
                String quote = null;
                String author = null;

                if (isChecked) {
                    pinnedQuoteId = Integer.parseInt(tvStartActId.getText().toString().substring(1));
                    quote = tvStartActQuote.getText().toString();
                    author = tvStartActAuthor.getText().toString();

                    if (!db.isFavorite(pinnedQuoteId)) {
                        ivStartActIsFavorite.setImageResource(R.drawable.like);

                        db.add(new Quote(pinnedQuoteId, quote, author));

                        //region ToDo: Delete

                        logFavoriteQuotes();

                        //endregion
                    }
                } else {
//                    getRandomQuote();
                }

                editor.putInt("id", pinnedQuoteId);
                editor.putString("quote", quote);
                editor.putString("author", author);

                editor.commit();
            }
        });

        //endregion

        //region Like | Dislike Quote

        ivStartActIsFavorite.setOnClickListener(v -> {
            int id = Integer.parseInt(tvStartActId.getText().toString().substring(1));
            boolean isFavorite = db.isFavorite(id);

            if (isFavorite) {
                tbStartActPinUnpin.setChecked(false);

                ivStartActIsFavorite.setImageResource(R.drawable.dislike);

                db.delete(id);
            } else {
                ivStartActIsFavorite.setImageResource(R.drawable.like);

                String quote = tvStartActQuote.getText().toString();
                String author = tvStartActAuthor.getText().toString();

                db.add(new Quote(id, quote, author));
            }

            //region ToDo: Delete

            logFavoriteQuotes();

            //endregion
        });

        //endregion

        btnStartActPass.setOnClickListener(v -> {
            finish();
        });
    }

    //region ToDo: Delete

    private void logFavoriteQuotes() {
        ArrayList<Quote> quotes = db.getAll();
        for (Quote quote : quotes) {
            Log.e("SQLite", quote.toString());
        }
    }

    //endregion

    private void getRandomQuote() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://dummyjson.com/quotes/random";

        //region ToDo: Delete

//        int randomNumber = ThreadLocalRandom.current().nextInt(1, 5 + 1);
//        String url = String.format("https://dummyjson.com/quotes/%d", randomNumber);

        //endregion

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                url,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("DefaultLocale")
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
}