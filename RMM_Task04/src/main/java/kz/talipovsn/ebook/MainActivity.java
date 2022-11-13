package kz.talipovsn.ebook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {




    private WebView webView; // Компонент для просмотра html-страниц

    private String resourceDir; // Путь к html-страницам в ресурсах приложения

    private static final String ID_TOPIC = "idTopic"; // Название ключа для хранения ID-кода выбранной темы
    private int idTopic; // ID-код выбранной темы

    static final String CONFIG_FILE_NAME = "Config"; // Имя файла настроек приложения
    private SharedPreferences sPref; // Переменная для работы с настройками программы

    private static final String FULL_SIZE_FONT = "isFullSizeFont"; // Название ключа для хранения выбора крупного шрифта
    private static final String MY_SEARCH = "mysearch"; // Название ключа для хранения разрешения поиска
    private boolean isFullSizeFont = true; // Переменная признака выбора крупного шрифта с инициализацией
    private boolean mysearch = true;// Переменная признака разрешения поиска с инициализацией

    private int currentApiOS; // Переменная для определения версии Android пользователя

    EditText searchText; // Поле для ввода искомого текста
    TextView searchCountText; // Поле для отображения сколько найдено фрагментов поиска
    ImageButton searchForwardButton, searchCloseButton, searchBackButton; // Кнопки навигации поиска
    RelativeLayout searchToolLayout;// Панель поиска
    FloatingActionButton searchButton;// Круглая кнопка поиска

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        checkPermissions();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createHTML();





        // Инициализация переменных для поиска
        searchForwardButton = findViewById(R.id.searchForwardButton);
        searchBackButton = findViewById(R.id.searchBackButton);
        searchCloseButton = findViewById(R.id.searchCloseButton);
        searchText = findViewById(R.id.searchText);
        searchCountText = findViewById(R.id.searchCountText);
        searchButton = findViewById(R.id.searchButton);
        searchToolLayout = findViewById(R.id.searchToolLayout);

        // Инициализация переменной настроек программы
        sPref = getSharedPreferences(CONFIG_FILE_NAME, MODE_PRIVATE);

        // Обработка переворачивания экрана и начальная инициализация выбранной темы (ID_TOPIC) в приложении
        if (savedInstanceState != null) {
            // Вторичное создание окна после переворачивания экрана
            isFullSizeFont = savedInstanceState.getBoolean(FULL_SIZE_FONT, isFullSizeFont);
            mysearch = savedInstanceState.getBoolean(MY_SEARCH, mysearch);
            idTopic = savedInstanceState.getInt(ID_TOPIC, R.id.contacts);
        } else {
            // Первый запуск программы до переворачивания экрана
            // Чтение данных с настроек программы
            isFullSizeFont = sPref.getBoolean(FULL_SIZE_FONT, isFullSizeFont);
            mysearch = sPref.getBoolean(MY_SEARCH, mysearch);
            idTopic = sPref.getInt(ID_TOPIC, R.id.contacts);
        }

        // Включение/отключение кнопки поиска в зависимости от настроек пользователя
        if (mysearch) {
            searchButton.setVisibility(View.VISIBLE);
        } else {
            searchButton.setVisibility(View.GONE);
        }

        // Определение API версии Android
        currentApiOS = android.os.Build.VERSION.SDK_INT;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Устанавливаем выбранный пункт меню
        try {
            navigationView.setCheckedItem(idTopic);
        } catch (Exception ignore){
        }

        // Поиск компонента для отображения html-страниц
        webView = findViewById(R.id.webView);

        // ------------- ВАЖНАЯ СЕКЦИЯ ! ---------------------------------
        // Открытие ссылок внутри компонента без вызова внешнего браузера!
        // Чтоб работали такие ссылки в HTML для перехода из страницы в страницу:
        //</div>
        //<p class="msonormal">&nbsp;<a href="/android_asset/HTML/LEC01/lec01.htm">&gt;&gt;&gt; В НАЧАЛО  &gt;&gt;&gt;</a></p>
        //</body>
        //</html>
        webView.setWebViewClient(new WebViewClient()); // ЭТО ОБЯЗАТЕЛЬНАЯ СТРОКА !!!

        // Патч для HTML чтобы не было глюков! ЭТО ОБЯЗАТЕЛЬНЫЙ КОД !!!
        if (Build.VERSION.SDK_INT >= 24) try {
            Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
            m.invoke(null);
        } catch (Exception ignored) {
        }
        // ---------------------------------------------------------------




        initWebView(isFullSizeFont);

        // Определение пути к html-файлам
        resourceDir = getString(R.string.resource_directory);

        //Инициализация начала просмотра html-страниц
        onNavigationItemSelected(null);

        // Обработчик кнопки Поиск
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCountText.setText("");
                searchToolLayout.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.GONE);
                searchText.requestFocus();
            }
        });

        // Обработчик кнопки Поиск Вперед
        searchForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.findNext(true);
            }
        });

        // Обработчик кнопки Поиск Назад
        searchBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.findNext(false);
            }
        });

        // Обработчик нажатий кнопок в окошке поиска
        searchText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Если нажата клавиша Enter
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER))) {
                    // Скрываем клавиатуру
                    hideSoftInput();
                    // Ищем нужный текст в webView
                    webView.findAll(searchText.getText().toString());
                    // Активируем возможность отображения найденного теккста в webView
                    try {
                        Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
                        m.invoke(webView, true);
                    } catch (Exception ignored) {
                    }
                }
                return false;
            }
        });

        // Обработчик поиска в WebView
        webView.setFindListener(new WebView.FindListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                searchCountText.setText("");
                if (numberOfMatches > 0) {
                    searchCountText.setText(String.format("%d %s %d", activeMatchOrdinal + 1, getString(R.string.of), numberOfMatches));
                } else {
                    searchCountText.setText(R.string.not_found);
                }
            }
        });

        // Обработчик кнопки закрытия поиска
        searchCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.clearMatches();
                searchText.setText("");
                searchToolLayout.setVisibility(View.GONE);
                if (mysearch) {
                    searchButton.setVisibility(View.VISIBLE);
                }
                hideSoftInput();
            }
        });

        searchCloseButton.performClick();

    }

    public void createHTML() {
          String URL = "jdbc:mysql://10.0.2.2/telephone_book";
          String USER = "root";
          String PASSWORD = "";
        String HTML=" <!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\n" +
                "\n" +
                "<div class=\"divTable cinereousTable\">\n" +
                "<div class=\"divTableHeading\">\n" +
                "<div class=\"divTableRow\">\n" +
                "<div class=\"divTableHead\">ID</div>\n" +
                "<div class=\"divTableHead\">Имя</div>\n" +
                "<div class=\"divTableHead\">Фамилия</div>\n" +
                "<div class=\"divTableHead\">Номер</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "\n" +
                "<div class=\"divTableBody\">";

        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost/", "root", null)) {
            String sql = "SELECT * FROM telephons";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                HTML = HTML.concat("<div class="+"divTableRow"+">") ;
                HTML = HTML.concat("<<div class=\"divTableCell\">"+resultSet.getInt("id")+"</div>>");
                HTML = HTML.concat("<<div class=\"divTableCell\">"+resultSet.getString("name")+"</div>>");
                HTML = HTML.concat("<<div class=\"divTableCell\">"+resultSet.getString("surname")+"</div>>");
                HTML += HTML.concat("<<div class=\"divTableCell\">"+resultSet.getString("telephone_number")+"</div>>");
                HTML = HTML.concat("</div>\n");
            }
            else{
                HTML = HTML.concat("</div>\n" +
                        "</div>\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "</body>\n" +
                        "</html> \n");
                File path = getApplicationContext().getFilesDir();
                try {
                    FileOutputStream writer = new FileOutputStream(new File(path,"main.html"));
                    writer.write(HTML.getBytes(StandardCharsets.UTF_8));
                    writer.close();
                    Toast.makeText(getApplicationContext(),"Wrote to file" + "main.html",Toast.LENGTH_SHORT).show();
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }


            }

        } catch (Exception e) {
            Log.e("InfoAsyncTask", "Error reading school information", e);
            e.printStackTrace();
        }

    }

    // Сохранение данных в буфер при переворачивании экрана
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(FULL_SIZE_FONT, isFullSizeFont); // Сохраняем крупный ли шрифт
        savedInstanceState.putBoolean(MY_SEARCH, mysearch); // Сохраняем разрешение поиска
        savedInstanceState.putInt(ID_TOPIC, idTopic); // Сохраняем ID текущей темы
        super.onSaveInstanceState(savedInstanceState);
    }

    // Метод при закрытии окна
    @Override
    protected void onStop() {
        super.onStop();
        // Сохранение настроек программы в файл настроек
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(FULL_SIZE_FONT, isFullSizeFont);
        ed.putBoolean(MY_SEARCH, mysearch);
        ed.putInt(ID_TOPIC, idTopic);
        ed.apply();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Создание меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // Отключение пункта крупного шрифта для старых версий Android из-за ограничений их WebView
        MenuItem fullSizeItem = menu.findItem(R.id.full_size_font);
        try {
            if (currentApiOS < android.os.Build.VERSION_CODES.LOLLIPOP) {
                isFullSizeFont = true;
                fullSizeItem.setCheckable(false);
                fullSizeItem.setEnabled(false);
                fullSizeItem.setChecked(true);
            } else {
                fullSizeItem.setCheckable(true);
                fullSizeItem.setChecked(isFullSizeFont);
            }
        } catch (Exception ignored) {
        }

        // Отключение/включение пункта поиска
        MenuItem mySearchItem = menu.findItem(R.id.mysearch);
        try {
            mySearchItem.setChecked(mysearch);
        } catch (Exception ignored) {
        }

        return true;
    }

    // Обработка верхнего правого меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Выход из программы
        if (id == R.id.exit) {
            finish();
            return true;
        }

        // Установка крупного шрифта
        if (id == R.id.full_size_font) {
            isFullSizeFont = !item.isChecked();
            item.setChecked(isFullSizeFont);
            initWebView(isFullSizeFont);
            return true;
        }

        // Установка разрешения/отключение поиска
        if (id == R.id.mysearch) {
            mysearch = !item.isChecked();
            item.setChecked(mysearch);
            if (mysearch) {
                searchButton.setVisibility(View.VISIBLE);
            } else {
                searchButton.setVisibility(View.GONE);
                searchCloseButton.callOnClick();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id;

        if (item == null) { // Вызывается при начальном открытии окна
            id = idTopic;
        } else { // Вызывается при выборе темы из меню тем
            id = item.getItemId();
            // Сохранение выбранной темы, кроме случаев всех внешних переходов
            if (item.isCheckable()) {
                idTopic = id;
            }
        }

        // Блок выбора тем
        if (id == R.id.contacts) {
            webView.loadUrl(resourceDir + "MAIN/main.html");
        } else if (id == R.id.laws_kz_l1) {
            webView.loadUrl(resourceDir + "LAWS/KZ/L1.htm");
        } else if (id == R.id.laws_ru_l1) {
            webView.loadUrl(resourceDir + "LAWS/RU/L1.htm");

            // Блок внешних переходов
        } else {
            if (id == R.id.nav_view1) { // Переход на внешнюю html-ссылку и внешний браузер
                openLinkExternally(getString(R.string.mobile_location_url));
            }
            if (id == R.id.nav_view2) { // Переход на внешнюю html-ссылку и внешний браузер
                openLinkExternally(getString(R.string.find_telephone_url));
            }
            if (id == R.id.confident) { // Переход на внешнюю html-ссылку и внешний браузер
                openLinkExternally(getString(R.string.confident_url));
            }
            if (id == R.id.nav_send) { // Переход на отправку письма автору
                sendMail(getString(R.string.email), getString(R.string.subject), getString(R.string.textmail));
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Инициализация компонента просмотра html-страниц
    private void openLinkExternally(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.view)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    // Посылка письма автору
    private void sendMail(String email, String subject, String text) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, text);
        try {
            startActivity(Intent.createChooser(i, getString(R.string.sending_letter)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, R.string.no_installed_email_client, Toast.LENGTH_SHORT).show();
        }
    }

    // Инициализация компонента просмотра html-страниц и размера шрифта
    private void initWebView(boolean isFullScreen) {
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        if ((currentApiOS >= android.os.Build.VERSION_CODES.LOLLIPOP) && (!isFullScreen)) {
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setSupportZoom(true);
        } else {
            webView.getSettings().setLoadWithOverviewMode(false);
            webView.getSettings().setUseWideViewPort(false);
            webView.getSettings().setBuiltInZoomControls(false);
            webView.getSettings().setSupportZoom(false);
        }
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
    }

    // Скрываем клавиатуру
    private void hideSoftInput() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignored) {
        }
    }
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE


    };
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }

}

