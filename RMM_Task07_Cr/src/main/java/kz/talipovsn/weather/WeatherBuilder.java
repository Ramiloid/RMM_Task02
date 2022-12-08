package kz.talipovsn.weather;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

import static kz.talipovsn.weather.HttpClient.getHTMLData;
import static kz.talipovsn.weather.HttpClient.getHTMLImage;

// СОЗДАТЕЛЬ ПОГОДЫ
public class WeatherBuilder {



    // Получение JSON html-данных погоды по городу и языку
    private static String getWeatherData() {
        String url = "https://api.github.com/repos/cryptomator/cryptomator/releases";
        //System.out.println(url);
        return getHTMLData(url);
    }

    // Парсинг даты в формате JSON с созданием объекта погоды
    private static Repo dataParsing(String json,int i) {
        Repo repo = new Repo();

        try {
            System.out.println(json);
            JSONArray _arr = new JSONArray(json);
            JSONObject _obj = _arr.getJSONObject(i);
            repo.setName(_obj.getString("name"));
            repo.setPublished(_obj.getString("published_at"));
            repo.setUrl(_obj.getString("url"));
        } catch (Exception ignore) {
        }
        return repo;
    }

    // Получение готового объекта погоды по городу и языку
    public static Repo buildWeather (int i) {
        return dataParsing(getWeatherData(),i);
    }

    //private static Repo dataParsing(String weatherData) {
    //    return dataParsing(getWeatherData());
    //}
}