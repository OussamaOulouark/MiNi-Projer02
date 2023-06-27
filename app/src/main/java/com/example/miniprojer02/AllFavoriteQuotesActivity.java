package com.example.miniprojer02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.miniprojer02.Models.Quote;
import com.example.miniprojer02.adapter.FavoriteQuotesAdapter;
import com.example.miniprojer02.db.FavoriteQuotesDbOpenHelper;

import java.util.ArrayList;

public class AllFavoriteQuotesActivity extends AppCompatActivity {
    RecyclerView rvAllFavQuotesActList;
    FavoriteQuotesDbOpenHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allfavorite_quotes);

        rvAllFavQuotesActList = findViewById(R.id.rvAllFavQuotesActList);
        db = new FavoriteQuotesDbOpenHelper(this);

        db.getAll();




        FavoriteQuotesAdapter adapter =new FavoriteQuotesAdapter(db.getAll());

        rvAllFavQuotesActList.setAdapter(adapter);
        rvAllFavQuotesActList.setLayoutManager(new LinearLayoutManager(this));


    }
}