package kz.talipovsn.weather;

import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView; // Иконка погоды
    private TextView textView; // Компонент для данных погоды

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        for(int i=0;i<20;i++) {
        JSONWeatherTask task = new JSONWeatherTask(); // Создание потока определения погоды
        task.setIndex(i);
        task.execute(getString(R.string.Город), getString(R.string.Язык)); // Активация потока
        }

    }

    // Кнопка "Обновить"
    public void onClick(View view) {
        new JSONWeatherTask().execute(getString(R.string.Город), getString(R.string.Язык)); // Создание и активация потока определения погоды
    }

    // Класс отдельного асинхронного потока
    private class JSONWeatherTask extends AsyncTask<String, Void, Repo> {
        int index = 0;
        // Тут реализуем фоновую асинхронную загрузку данных, требующих много времени

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        @Override
        protected Repo doInBackground(String... params) {
            return WeatherBuilder.buildWeather(index);
        }
        // ----------------------------------------------------------------------------

        // Тут реализуем что нужно сделать после окончания загрузки данных
        @Override
        protected void onPostExecute(final Repo weather) {
            super.onPostExecute(weather);

            // Выдаем данные о погоде в компонент
            textView.post(new Runnable() { //  с использованием синхронизации UI
                @Override
                public void run() {
                    if (weather.getName() != null) {
                        textView.append(weather.getName() + "\n");
                        textView.append("Published: ");
                        textView.append(weather.getPublished() + "\n");
                        textView.append(weather.getUrl() + "\n");
                    } else {
                        textView.append("Нет данных!" + "\n");
                        textView.append("Проверьте доступность Интернета");
                    }
                }
            });

        }
        // ------------------------------------------------------------------------------------

    }

}
