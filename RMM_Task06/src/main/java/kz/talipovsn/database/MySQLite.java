package kz.talipovsn.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class MySQLite extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2; // НОМЕР ВЕРСИИ БАЗЫ ДАННЫХ И ТАБЛИЦ !

    static final String DATABASE_NAME = "animal"; // Имя базы данных

    static final String TABLE_NAME = "emergency_service"; // Имя таблицы
    static final String ID = "id"; // Поле с ID
    static final String NAME = "name";
    static final String ORDERING = "ordering";
    static final String MOVETYPE = "movetype";
    static final String MOVING = "moving";
    static final String EATTYPE = "eattype";
    static final String WEIGHT ="weight";
    static final String ENGNAME = "engname";


    static final String ASSETS_FILE_NAME = "animals.txt"; // Имя файла из ресурсов с данными для БД
    static final String DATA_SEPARATOR = "|"; // Разделитель данных в файле ресурсов с телефонами

    private Context context; // Контекст приложения

    public MySQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Метод создания базы данных и таблиц в ней
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " TEXT,"
                + ORDERING + " TEXT,"
                + MOVETYPE + " TEXT,"
                + MOVING + " TEXT,"
                + EATTYPE + " TEXT,"
                + WEIGHT + " TEXT,"
                + ENGNAME + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        System.out.println(CREATE_CONTACTS_TABLE);
        loadDataFromAsset(context, ASSETS_FILE_NAME,  db);
    }

    // Метод при обновлении структуры базы данных и/или таблиц в ней
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        System.out.println("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Добавление нового контакта в БД
    public void addData(SQLiteDatabase db, String name, String ord, String move, String moving, String eat ,String weight, String engname) {
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(ORDERING, ord);
        values.put(MOVETYPE, move);
        values.put(MOVING, moving);
        values.put(EATTYPE, eat);
        values.put(WEIGHT,weight);
        values.put(ENGNAME, engname);
        db.insert(TABLE_NAME, null, values);
    }

    // Добавление записей в базу данных из файла ресурсов
    public void loadDataFromAsset(Context context, String fileName, SQLiteDatabase db) {
        BufferedReader in = null;

        try {
            // Открываем поток для работы с файлом с исходными данными
            InputStream is = context.getAssets().open(fileName);
            // Открываем буфер обмена для потока работы с файлом с исходными данными
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            while ((str = in.readLine()) != null) { // Читаем строку из файла
                String strTrim = str.trim(); // Убираем у строки пробелы с концов
                if (!strTrim.equals("")) { // Если строка не пустая, то
                    StringTokenizer st = new StringTokenizer(strTrim, DATA_SEPARATOR); // Нарезаем ее на части
                    String name = st.nextToken().trim(); // Извлекаем из строки название организации без пробелов на концах
                    String ordering = st.nextToken().trim(); // Извлекаем из строки номер организации без пробелов на концах
                    String movetype= st.nextToken().trim(); // Извлекаем из строки номер организации без пробелов на концах
                    String moving = st.nextToken().trim(); // Извлекаем из строки номер организации без пробелов на концах
                    String eattype = st.nextToken().trim(); // Извлекаем из строки номер организации без пробелов на концах
                    String weight = st.nextToken().trim();
                    String engname = st.nextToken().trim(); // Извлекаем из строки номер организации без пробелов на концах


                    addData(db,name,ordering,movetype,moving,eattype,weight,engname); // Добавляем название и телефон в базу данных
                }
            }

        // Обработчики ошибок
        } catch (IOException ignored) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }

    }

    // Получение значений данных из БД в виде строки с фильтром
    public String getData(String filter, long type) {

        String selectQuery; // Переменная для SQL-запроса
        String typeQuery="";
        switch((int)type){
            case(0):
                typeQuery=NAME;
                break;
            case(1):
                typeQuery=ORDERING;
                break;
            case(2):
                typeQuery=MOVETYPE;
                break;
            case(3):
                typeQuery=MOVING;
                break;
            case(4):
                typeQuery=EATTYPE;
                break;
            case(5):
                typeQuery=WEIGHT;
                break;
            case(6):
                typeQuery=ENGNAME;
                break;
            default:
                typeQuery="";
                break;
        }
       if (filter.equals("")) {
           selectQuery = "SELECT  * FROM " + TABLE_NAME;
      } else {
               selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + typeQuery + " LIKE " + "'%" + filter.toLowerCase() + "%'" + " ORDER BY " + typeQuery;


       }
       System.out.println(selectQuery);
        SQLiteDatabase db = this.getReadableDatabase(); // Доступ к БД
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null); // Выполнение SQL-запроса

        StringBuilder data = new StringBuilder(); // Переменная для формирования данных из запроса

        int num = 0;
        if (cursor.moveToFirst()) { // Если есть хоть одна запись, то

            do { // Цикл по всем записям результата запроса
                int n = cursor.getColumnIndex(NAME);
                int o = cursor.getColumnIndex(ORDERING);
                int mt = cursor.getColumnIndex(MOVETYPE);
                int mv = cursor.getColumnIndex(MOVING);
                int et  = cursor.getColumnIndex(EATTYPE);
                int wh = cursor.getColumnIndex(WEIGHT);
                int eng_n  = cursor.getColumnIndex(ENGNAME);

                String name = cursor.getString(n); // Чтение названия организации
                System.out.println(name);
                String ordering = cursor.getString(o); // Чтение названия организации
                String movetype = cursor.getString(mt); // Чтение телефонного номера
                String moving = cursor.getString(mv); // Чтение телефонного номера
                String eattype = cursor.getString(et); // Чтение телефонного номера
                String weight = cursor.getString(wh);
                String engname = cursor.getString(eng_n); // Чтение телефонного номера

                data.append(String.valueOf(++num)).append(") ").append(name).append(": ").append(ordering).append(" ").append(movetype).append(" ").append(moving).append(" ").append(eattype).append(" ").append(weight).append(" ").append(engname).append("\n");
            } while (cursor.moveToNext()); // Цикл пока есть следующая запись
        }
        return data.toString(); // Возвращение результата
    }

}