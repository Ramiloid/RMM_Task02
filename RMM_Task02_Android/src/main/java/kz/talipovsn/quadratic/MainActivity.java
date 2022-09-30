package kz.talipovsn.quadratic;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Локальные переменные для доступа к компонентам окна
    private EditText editText_a, editText_b, editText_c, editText_x;
    private TextView textView_y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация переменных доступа к компонентам окна
        editText_a = findViewById(R.id.editText_a);
        editText_b = findViewById(R.id.editText_b);
        editText_c = findViewById(R.id.editText_c);
        editText_x = findViewById(R.id.editText_x);
        textView_y = findViewById(R.id.textView_y);


        // Проверка на переворот экрана и восстановление нужных значений в компонентах
        if (savedInstanceState != null) {
            textView_y.setText(savedInstanceState.getString("y"));
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Сохранение нужных нам значений компонент при перевороте экрана
        outState.putString("y", textView_y.getText().toString());
    }

    // МЕТОД ДЛЯ КНОПКИ РАСЧЕТА
    @SuppressLint("DefaultLocale")
    public void onCalc(View v) {

        // Локальные переменные
        double a, b, c, x , y;

        try {
            // Считывание введенных входных значений в переменные
            a = Double.parseDouble(editText_a.getText().toString());
            b = Double.parseDouble(editText_b.getText().toString());
            c = Double.parseDouble(editText_c.getText().toString());
            x = Double.parseDouble(editText_x.getText().toString());
        } catch (Exception e) {
            // Выдача всплывающего сообщения на экран об ошибке
            Toast.makeText(MainActivity.this, "Неверные входные данные!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Расчет значений x1 и x2
        try {
            if(x<4){
                y = ((Math.pow(x,2)+Math.pow(a,2))*c)/(2*b);
            }
            else{
                y=  Math.pow(x,3)*(a-b);
            }
            if (Double.isNaN(y) || Double.isInfinite(y)) {
                throw new Exception();
            }
            textView_y.setText(String.format("y = %.3f", y));
        } catch (Exception e) {
            String err = "Нет решения!";
            textView_y.setText(err);
        }

    }

}
