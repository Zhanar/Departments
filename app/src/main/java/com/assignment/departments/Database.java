package com.assignment.departments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Жанар on 22.05.2016.
 */
public class Database {

    private static final String DB_NAME = "Database";
    private static final int DB_VERSION = 1;

    // имя таблицы компаний, поля и запрос создания
    private static final String DEPARTMENT_TABLE = "OrgUnitVM";
    public static final String DEPARTMENT_COLUMN_ID = "_id";
    public static final String DEPARTMENT_COLUMN_NAME = "title";
    public static final String EMPLOYEE_COLUMN_HEAD_USER_ID = "headUserId";
    public static final String DEPARTMENT_COLUMN_PARENT_ID = "orgUnitParrentId";
    public static final String EMPLOYEES_COLUMN_ID = "employeesId";
    public static final String DEPARTMENT_COLUMN_CHILD_ID = "orgUnitChildsId";
    private static final String DEPARTMENT_TABLE_CREATE = "create table " + DEPARTMENT_TABLE
            + "(" + DEPARTMENT_COLUMN_ID + " integer primary key, "
            + DEPARTMENT_COLUMN_NAME + " text "
            + EMPLOYEE_COLUMN_HEAD_USER_ID + " integer "
            + DEPARTMENT_COLUMN_PARENT_ID + " integer "
            + EMPLOYEES_COLUMN_ID + " integer "
            + DEPARTMENT_COLUMN_CHILD_ID + " integer );";


    // имя таблицы телефонов, поля и запрос создания
    private static final String EMPLOYEE_TABLE = "UserVM";
    public static final String EMPLOYEE_COLUMN_ID = "_id";
    public static final String EMPLOYEE_COLUMN_NAME = "login";
    public static final String EMPLOYEE_COLUMN_PASSWORD = "password";
    private static final String EMPLOYEE_TABLE_CREATE = "create table "
            + EMPLOYEE_TABLE + "(" + EMPLOYEE_COLUMN_ID + " integer primary key autoincrement, "
            + EMPLOYEE_COLUMN_NAME + " text, "
            + EMPLOYEE_COLUMN_PASSWORD + " text " + ");";

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public Database(Context ctx) {
        mCtx = ctx;
    }

    // открываем подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрываем подключение
    public void close() {
        if (mDBHelper != null)
            mDBHelper.close();
    }

    // данные по компаниям
    public Cursor getDepartmentData() {
        return mDB.query(DEPARTMENT_TABLE, null, null, null, null, null, null);
    }

    // данные по телефонам конкретной группы
    public Cursor getEmployeeData(long companyID) {
        return mDB.query(EMPLOYEE_TABLE, null, EMPLOYEE_COLUMN_PASSWORD + " = "
                + companyID, null, null, null, null);
    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            ContentValues cv = new ContentValues();

            // названия компаний (групп)
            String[] departments = new String[] { "HTC", "Samsung", "LG" };

            // создаем и заполняем таблицу компаний
            db.execSQL(DEPARTMENT_TABLE_CREATE);
            for (int i = 0; i < departments.length; i++) {
                cv.put(DEPARTMENT_COLUMN_ID, i + 1);
                cv.put(DEPARTMENT_COLUMN_NAME, departments[i]);
                cv.put(EMPLOYEE_COLUMN_HEAD_USER_ID, i + 1);
                cv.put(DEPARTMENT_COLUMN_PARENT_ID, i + 1);
                cv.put(EMPLOYEES_COLUMN_ID, i + 1);
                cv.put(DEPARTMENT_COLUMN_CHILD_ID, i + 1);
                db.insert(DEPARTMENT_TABLE, null, cv);
            }

            // названия телефонов (элементов)
            String[] phonesHTC = new String[] { "Sensation", "Desire",
                    "Wildfire", "Hero" };
            String[] phonesSams = new String[] { "Galaxy S II", "Galaxy Nexus",
                    "Wave" };
            String[] phonesLG = new String[] { "Optimus", "Optimus Link",
                    "Optimus Black", "Optimus One" };

            // создаем и заполняем таблицу телефонов
            db.execSQL(EMPLOYEE_TABLE_CREATE);
            cv.clear();
            for (int i = 0; i < phonesHTC.length; i++) {
                cv.put(EMPLOYEE_COLUMN_PASSWORD, phonesHTC[i]);
                cv.put(EMPLOYEE_COLUMN_NAME, phonesHTC[i]);
                db.insert(EMPLOYEE_TABLE, null, cv);
            }
            for (int i = 0; i < phonesSams.length; i++) {
                cv.put(EMPLOYEE_COLUMN_PASSWORD, phonesSams[i]);
                cv.put(EMPLOYEE_COLUMN_NAME, phonesSams[i]);
                db.insert(EMPLOYEE_TABLE, null, cv);
            }
            for (int i = 0; i < phonesLG.length; i++) {
                cv.put(EMPLOYEE_COLUMN_PASSWORD, phonesLG[i]);
                cv.put(EMPLOYEE_COLUMN_NAME, phonesLG[i]);
                db.insert(EMPLOYEE_TABLE, null, cv);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}



