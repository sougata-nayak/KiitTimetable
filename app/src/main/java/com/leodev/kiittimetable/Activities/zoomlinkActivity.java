package com.leodev.kiittimetable.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.leodev.kiittimetable.Adapters.ZoomAdapter;
import com.leodev.kiittimetable.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.SharedPreferences.*;

public class zoomlinkActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<String> list;
    private ZoomAdapter adapter;
    private Object Editor;

    private EditText linkEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actrivity_zoom_link);
        recyclerView=findViewById(R.id.recyclerview_zoom);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        list= Arrays.asList(getResources().getStringArray(R.array.subjects));
        adapter=new ZoomAdapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter((RecyclerView.Adapter) adapter);


        SharedPreferences sharedPreferences=getSharedPreferences("Zoom Links",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        View rvItem;
        for(int i=0;i<recyclerView.getChildCount();i++){

        }
    }
}
