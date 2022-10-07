package com.example.rmm_task03_android;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class SecondActivity extends AppCompatActivity {

    // Локальные переменные для доступа к компонентам окна
    private RadioGroup RG;
    private String Name,Surname,Age;
    private int id, checkedId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Name = getIntent().getStringExtra("name");
        Surname = getIntent().getStringExtra("surname");
        Age = getIntent().getStringExtra("age");
        RG = findViewById(R.id.RG_1);




        // Инициализация переменных доступа к компонентам окна

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        int tempInt = RG.getCheckedRadioButtonId();
        savedInstanceState.putInt("CurrentChecked", tempInt);
        savedInstanceState.putString("Name", Name);
        savedInstanceState.putString("Surname", Surname);
        savedInstanceState.putString("Age", Age);


        super.onSaveInstanceState(savedInstanceState);
    }

//onRestoreInstanceState

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        RG = findViewById(R.id.RG_1);
        Name = savedInstanceState.getString("Name");
        Surname = savedInstanceState.getString("Surname");
        Age = savedInstanceState.getString("Age");
        RG.check(savedInstanceState.getInt("CurrentChecked"));
    }

    public void onForward(View v) {
        // Создание второго окна
        Intent intent = new Intent(SecondActivity.this, FinalActivity.class);
        RadioButton rb = findViewById(RG.getCheckedRadioButtonId());
        System.out.println("####################################"+rb.getTag());
        int id = Integer.parseInt(String.valueOf(getResources().getIdentifier("com.example.rmm_task03_android:drawable/" + rb.getTag(),null,null)));
        System.out.println(id+"ididididiididididi");



        // Найдём переключатель по его id

        // Подготовка параметров для второго окна
        intent.putExtra("image", id);
        intent.putExtra("name", Name);
        intent.putExtra("surname", Surname);
        intent.putExtra("age", Age);

        // Запуск второго окна
        startActivity(intent);
    }
    public void onBack(View v){
        setResult(RESULT_OK);
        finish();
    }
    public void onExit(View v){
        finishAffinity();
    }
}
