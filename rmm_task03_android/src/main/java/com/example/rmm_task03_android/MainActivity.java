package com.example.rmm_task03_android;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    // Локальные переменные для доступа к компонентам окна
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация переменных доступа к компонентам окна
        editText1 = findViewById(R.id.NameEdit);
        editText2 = findViewById(R.id.SurnameEdit);
        editText3 = findViewById(R.id.AgeEdit);

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {


        savedInstanceState.putString("Name", editText1.getText().toString());
        savedInstanceState.putString("Surname", editText2.getText().toString());
        savedInstanceState.putString("Age", editText3.getText().toString());



        super.onSaveInstanceState(savedInstanceState);
    }

//onRestoreInstanceState

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        editText1.setText(savedInstanceState.getString("Name"));
        editText2.setText(savedInstanceState.getString("Surname"));
        editText3.setText(savedInstanceState.getString("Age"));

        super.onRestoreInstanceState(savedInstanceState);

    }

    public void onForward(View v) {
        // Создание второго окна
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);


        // Подготовка параметров для второго окна
        intent.putExtra("name", editText1.getText().toString());
        intent.putExtra("surname", editText2.getText().toString());
        intent.putExtra("age", editText3.getText().toString());


        // Запуск второго окна
        startActivity(intent);
    }
    public void onExit(View v){
        finishAffinity();
    }
}
