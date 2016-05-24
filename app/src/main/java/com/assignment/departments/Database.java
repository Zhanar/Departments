package com.assignment.departments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.assignment.departments.Model.Department;
import com.assignment.departments.Model.Employee;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Жанар on 22.05.2016.
 */
public class Database {

    private static final String DB_NAME = "Database1";
    private static final int DB_VERSION = 7;

    // имя таблицы компаний, поля и запрос создания
    private static final String DEPARTMENT_TABLE = "OrgUnitVM";
    public static final String DEPARTMENT_COLUMN_ID = "_id";
    public static final String DEPARTMENT_COLUMN_NAME = "title";
    public static final String DEPARTMENT_COLUMN_HEAD_USER = "headUser";
    public static final String DEPARTMENT_COLUMN_PARENT = "orgUnitParent";
    public static final String DEPARTMENT_COLUMN_EMPLOYEES = "employees";
    public static final String DEPARTMENT_COLUMN_CHILD = "orgUnitChild";
    public static final String DEPARTMENT_TABLE_CREATE = " create table if not exists " + DEPARTMENT_TABLE
            + " ( " + DEPARTMENT_COLUMN_ID + " integer primary key , "
            + DEPARTMENT_COLUMN_NAME + " text null , "
            + DEPARTMENT_COLUMN_HEAD_USER + " text null , "
            + DEPARTMENT_COLUMN_PARENT + " text null , "
            + DEPARTMENT_COLUMN_EMPLOYEES + " text null , "
            + DEPARTMENT_COLUMN_CHILD + " text null );";

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    Gson gson = new Gson();

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

    public ArrayList<Department> GetDepartmentCollection(){

        ArrayList<Department> result = new ArrayList<Department>();

        Cursor cursor = mDB.rawQuery("SELECT * FROM " + DEPARTMENT_TABLE, null);

        if(cursor.moveToFirst()){
            Department department;

            int ColumnId = cursor.getColumnIndex(DEPARTMENT_COLUMN_ID);
            int ColumnTitle = cursor.getColumnIndex(DEPARTMENT_COLUMN_NAME);
            int ColumnHeadUser = cursor.getColumnIndexOrThrow(DEPARTMENT_COLUMN_HEAD_USER);
            int ColumnOrgUnitParentId = cursor.getColumnIndexOrThrow(DEPARTMENT_COLUMN_PARENT);
            int ColumnEmployees = cursor.getColumnIndexOrThrow(DEPARTMENT_COLUMN_EMPLOYEES);
            int ColumnOrgUnitChild = cursor.getColumnIndexOrThrow(DEPARTMENT_COLUMN_CHILD);

            do {

                department = new Department();

                department.setId(cursor.getInt(ColumnId));
                department.setTitle(cursor.getString(ColumnTitle));

                Employee headUser = gson.fromJson(cursor.getString(ColumnHeadUser), Employee.class);
                department.setHeadUser(headUser);

                Department parent = gson.fromJson(cursor.getString(ColumnOrgUnitParentId), Department.class);
                department.setOrgUnitParrent(parent);

                ArrayList<Employee> employeeArrayList = gson.fromJson(cursor.getString(ColumnEmployees), new TypeToken<ArrayList<Employee>>(){}.getType());
                department.setEmployees(employeeArrayList);

                ArrayList<Department> departmentArrayList = gson.fromJson(cursor.getString(ColumnOrgUnitChild), new TypeToken<ArrayList<Department>>(){}.getType());
                department.setOrgUnitChilds(departmentArrayList);

                result.add(department);
            }
            while(cursor.moveToNext());

        }
        return result;
    }

    public void SaveDepartments(ArrayList<Department> departmentArrayList){
        ContentValues contentValues;

        for(int i = 0; i < departmentArrayList.size(); i++){
            Department currentItem = departmentArrayList.get(i);
            contentValues = new ContentValues();
            contentValues.put(DEPARTMENT_COLUMN_ID, currentItem.getId());
            contentValues.put(DEPARTMENT_COLUMN_NAME, currentItem.getTitle());

            String departmentParent = gson.toJson(currentItem.getOrgUnitParrent());
            contentValues.put(DEPARTMENT_COLUMN_PARENT, departmentParent);

            String headUser = gson.toJson(currentItem.getHeadUser());
            contentValues.put(DEPARTMENT_COLUMN_HEAD_USER, headUser);

            String departmentChild = gson.toJson(currentItem.getOrgUnitChilds());
            contentValues.put(DEPARTMENT_COLUMN_CHILD, departmentChild);

            String departmentEmployees = gson.toJson(currentItem.getEmployees());
            contentValues.put(DEPARTMENT_COLUMN_EMPLOYEES, departmentEmployees);

            //mDB.insertWithOnConflict(DEPARTMENT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            mDB.insert(DEPARTMENT_TABLE, null, contentValues);
//            mDB.insertWithOnConflict(EMPLOYEE_TABLE, null, contentValuesEmployee, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            ContentValues cv = new ContentValues();

            db.execSQL(Database.DEPARTMENT_TABLE_CREATE);
//            db.execSQL(Database.EMPLOYEE_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + DEPARTMENT_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS " + EMPLOYEE_TABLE);
            this.onCreate(db);
        }
    }
}



