package kz.talipovsn.json_micro;

import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {

    private TextView textView; // Компонент для отображения данных

    String url = "https://api.coindesk.com/v1/bpi/currentprice.json"; // Адрес получения JSON - данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ЭТОТ КУСОК КОДА НЕОБХОДИМ ДЛЯ ТОГО, ЧТОБЫ ОТКРЫВАТЬ САЙТЫ С HTTPS!
        try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        // ----------------------------------------------------------------------

        // Разрешаем запуск в общем потоке выполнеия длительных задач (например, чтение с сети)
        // ЭТО ТОЛЬКО ДЛЯ ПРИМЕРА, ПО-НОРМАЛЬНОМУ НАДО ВСЕ В ОТДЕЛЬНЫХ ПОТОКАХ
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        textView = findViewById(R.id.textView);

        onClick(null); // Нажмем на кнопку "Обновить"
    }

    // Кнопка "Обновить"
    public void onClick(View view) {
        textView.setText(R.string.not_data);
        String json = getHTMLData(url);
        if (json != null) {
            JSONObject _root = null;
            try {
                _root = new JSONObject(json);
                JSONObject bpi = _root.getJSONObject("bpi");
                JSONObject USD = bpi.getJSONObject("USD");
                String rate = USD.getString("rate");
                String code = USD.getString("code");
                JSONObject EUR = bpi.getJSONObject("EUR");
                String rate1 = EUR.getString("rate");
                String code1 = EUR.getString("code");
                textView.setText("");
                textView.append("1 BTC = " + rate + " " + code);
                textView.append("\n");
                textView.append("1 BTC = " + rate1 + " " + code1);
            } catch (Exception e) {
                textView.setText(R.string.error);
            }
        }
    }

    // Метод чтения данных с сети по протоколу HTTP
    public static String getHTMLData(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                StringBuilder data = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        data.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data.toString();
            } else {
                return null;
            }
        } catch (Exception ignored) {
        } finally {
            conn.disconnect();
        }
        return null;
    }
}
