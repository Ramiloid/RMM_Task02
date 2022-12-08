package kz.talipovsn.weather;

import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


// Класс погоды
public class Repo {
    private String name;
    private String published;
    private String url;



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPublished() {
        try {
            DateFormat df_GH = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            DateFormat df_KZ = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            df_GH.setTimeZone(TimeZone.getTimeZone("UTC+6"));
            String date2 = df_KZ.format(df_GH.parse(published));
            return date2;
        } catch (ParseException e) {
            return published;
        }
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
