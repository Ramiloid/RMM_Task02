package com.example.rmm_task03_android;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class FinalActivity extends AppCompatActivity {
    private String TempName, TempSurname, TempAge;
    int id;
    private TextView Name, Surname, Age;
    private ImageView Imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);


        // Добавление данных в компонент "editText"
        TempName = getIntent().getStringExtra("name");
        TempSurname = getIntent().getStringExtra("surname");
        TempAge = getIntent().getStringExtra("age");
        id = getIntent().getIntExtra("image", id);

        AfterCreate();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("Name",TempName);
        savedInstanceState.putString("Surname",TempSurname);
        savedInstanceState.putString("Age",TempAge);
        savedInstanceState.putInt("id",id);

        super.onSaveInstanceState(savedInstanceState);
    }

//onRestoreInstanceState

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        TempName = savedInstanceState.getString("Name");
        TempSurname = savedInstanceState.getString("Surname");
        TempAge = savedInstanceState.getString("Age");
        id = savedInstanceState.getInt("id");

    }

    // МЕТОД ДЛЯ КНОПКИ НАЗАД
    protected void AfterCreate() {
        Name = findViewById(R.id.textviewName);
        Surname = findViewById(R.id.textviewSurname);
        Age = findViewById(R.id.textviewAge);
        Imageview = findViewById(R.id.imageViewFinal);
        Imageview.setImageResource(id);
        Name.setText(TempName);
        Surname.setText(TempSurname);
        Age.setText(TempAge);
    }

    public void onBack(View v) {
        setResult(RESULT_OK);
        finish();
    }

    // МЕТОД ДЛЯ КНОПКИ ВЫХОДА
    public void onExit(View v) {
        finishAffinity();
    }

}