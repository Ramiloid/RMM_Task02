package kz.talipovsn.rates;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// СОЗДАТЕЛЬ КОТИРОВОК ВАЛЮТ
public class RatesReader {

    private static final String BASE_URL = "https://myfin.by/crypto-rates"; // Адрес с котировками

    // Парсинг котировок из формата html web-страницы банка, при ошибке доступа возвращаем null
    public static String getRatesData() {
        StringBuilder data = new StringBuilder();
        try {
            Document doc = Jsoup.connect(BASE_URL).timeout(5000).get(); // Создание документа JSOUP из html
            data.append("\n");
            Elements e = doc.select("div.rates-table");
            Elements tables = e.select("table");
            Element table = tables.get(0);
            int i = 0;
            // Цикл по строкам таблицы
            for (Element row : table.select("tbody tr:not(.table-creative-tr)")) {
                System.out.println(row.text());
                // Цикл по столбцам таблицы
                data.append(row.select(".names a").get(0).text()).append(" (").append(row.select(".names .crypto_iname").get(0).text()).append(")");
                data.append(String.format("%18s", row.select("td").get(1).text()));
                data.append("\n"); // Добавляем переход на следующую строку;
                data.append("Капитализация: ").append(row.select("td").get(2).text());
                data.append("\n"); // Добавляем переход на следующую строку;
                data.append("\n"); // Добавляем переход на следующую строку;
            }
        } catch (Exception ignored) {
            return null; // При ошибке доступа возвращаем null
        }
        return data.toString().trim(); // Возвращаем результат
    }

}