package com.assignment.departments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.assignment.departments.Model.Department;
import com.assignment.departments.Model.Employee;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
    public static final String DEPARTMENT_COLUMN_HEAD_USER_ID = "headUserId";
    public static final String DEPARTMENT_COLUMN_PARENT_ID = "orgUnitParentId";
    public static final String DEPARTMENT_COLUMN_EMPLOYEES_ID = "employeesId";
    public static final String DEPARTMENT_COLUMN_CHILD_ID = "orgUnitChildId";
    public static final String DEPARTMENT_TABLE_CREATE = "create table " + DEPARTMENT_TABLE
            + "(" + DEPARTMENT_COLUMN_ID + " integer primary key, "
            + DEPARTMENT_COLUMN_NAME + " text "
            + DEPARTMENT_COLUMN_HEAD_USER_ID + " integer "
            + DEPARTMENT_COLUMN_PARENT_ID + " integer "
            + DEPARTMENT_COLUMN_EMPLOYEES_ID + " integer "
            + DEPARTMENT_COLUMN_CHILD_ID + " integer );";


    // имя таблицы телефонов, поля и запрос создания
    private static final String EMPLOYEE_TABLE = "UserVM";
    public static final String EMPLOYEE_COLUMN_ID = "_id";
    public static final String EMPLOYEE_COLUMN_NAME = "login";
    public static final String EMPLOYEE_COLUMN_PASSWORD = "password";
    public static final String EMPLOYEE_TABLE_CREATE = "create table "
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

    public ArrayList<Employee> GetEmployeeCollection(int id){

        ArrayList<Employee> result = new ArrayList<Employee>();

        Cursor cursor = mDB.rawQuery("SELECT * FROM " + EMPLOYEE_TABLE + "WHERE _id =" + id , null);

        if(cursor.moveToFirst()){
            int ColumnEmployeeId = cursor.getColumnIndex(EMPLOYEE_COLUMN_ID);
            int ColumnEmployeeLogin = cursor.getColumnIndex(EMPLOYEE_COLUMN_NAME);
            int ColumnEmployeePassword = cursor.getColumnIndex(EMPLOYEE_COLUMN_PASSWORD);

            do{
                Employee employee = new Employee();

                employee.setId(cursor.getInt(ColumnEmployeeId));
                employee.setLogin(cursor.getString(ColumnEmployeeLogin));
                employee.setPassword(cursor.getString(ColumnEmployeePassword));

                result.add(employee);
            } while (cursor.moveToNext());
        }

        return result;
    }

    public Employee GetEmployee(int id){

        Employee employee = new Employee();

        Cursor cursor = mDB.rawQuery("SELECT * FROM " + EMPLOYEE_TABLE + "WHERE _id =" + id , null);

        if(cursor.moveToFirst()){
            int ColumnEmployeeId = cursor.getColumnIndex(EMPLOYEE_COLUMN_ID);
            int ColumnEmployeeLogin = cursor.getColumnIndex(EMPLOYEE_COLUMN_NAME);
            int ColumnEmployeePassword = cursor.getColumnIndex(EMPLOYEE_COLUMN_PASSWORD);

            do{
                employee.setId(cursor.getInt(ColumnEmployeeId));
                employee.setLogin(cursor.getString(ColumnEmployeeLogin));
                employee.setPassword(cursor.getString(ColumnEmployeePassword));
            } while (cursor.moveToNext());
        }

        return employee;
    }

    public ArrayList<Department> GetDepartmentCollection(){

        ArrayList<Department> result = new ArrayList<Department>();

        Cursor cursor = mDB.rawQuery("select * from " + DEPARTMENT_TABLE, null);

        if(cursor.moveToFirst()){
            Department department = new Department();

            int ColumnId = cursor.getColumnIndex(DEPARTMENT_COLUMN_ID);
            int ColumnTitle = cursor.getColumnIndex(DEPARTMENT_COLUMN_NAME);
            int ColumnHeadUserId = cursor.getColumnIndexOrThrow(DEPARTMENT_COLUMN_HEAD_USER_ID);
            int ColumnOrgUnitParentId = cursor.getColumnIndexOrThrow(DEPARTMENT_COLUMN_PARENT_ID);
            int ColumnEmployeesId = cursor.getColumnIndexOrThrow(DEPARTMENT_COLUMN_EMPLOYEES_ID);
            int ColumnOrgUnitChildId = cursor.getColumnIndexOrThrow(DEPARTMENT_COLUMN_CHILD_ID);

            do {
                department.setId(cursor.getInt(ColumnId));
                department.setTitle(cursor.getString(ColumnTitle));
                department.setHeadUser(GetEmployee(cursor.getInt(ColumnHeadUserId)));
//                department.setOrgUnitParrent(cursor.getInt());
                department.setEmployees(GetEmployeeCollection(cursor.getInt(ColumnEmployeesId)));
//                department.setOrgUnitChilds(cursor.getInt());
                result.add(department);
            }
            while(cursor.moveToNext());

        }
        return result;
    }

    public void SaveDepartments(ArrayList<Department> departmentArrayList){

        for(int i = 0; i < departmentArrayList.size(); i++){
            Department currentItem = departmentArrayList.get(i);

            ContentValues contentValues = new ContentValues();
            ContentValues contentValuesEmployee = new ContentValues();

            contentValues.put(DEPARTMENT_COLUMN_ID, currentItem.getId());
            contentValues.put(DEPARTMENT_COLUMN_NAME, currentItem.getTitle());
            if(currentItem.getOrgUnitParrent() != null){
                contentValues.put(DEPARTMENT_COLUMN_PARENT_ID, currentItem.getOrgUnitParrent().getId());
            }
            if(currentItem.getHeadUser() != null) {
                contentValues.put(DEPARTMENT_COLUMN_HEAD_USER_ID, currentItem.getHeadUser().getId());
            }
            for(int j = 0; j < currentItem.getEmployees().size(); j++){
                contentValues.put(DEPARTMENT_COLUMN_EMPLOYEES_ID, currentItem.getEmployees().get(j).getId());
            }
            for(int l = 0; l < currentItem.getOrgUnitChilds().size(); l++){
                contentValues.put(DEPARTMENT_COLUMN_CHILD_ID, currentItem.getOrgUnitChilds().get(l).getId());
                contentValues.put(DEPARTMENT_COLUMN_NAME, currentItem.getOrgUnitChilds().get(l).getTitle());
                if(currentItem.getOrgUnitChilds().get(l).getHeadUser() != null ){
                    contentValues.put(DEPARTMENT_COLUMN_HEAD_USER_ID, currentItem.getOrgUnitChilds().get(l).getHeadUser().getId());
                }
                contentValues.put(DEPARTMENT_COLUMN_PARENT_ID, currentItem.getOrgUnitChilds().get(l).getOrgUnitParrent().getId());
                for(int t = 0; t < currentItem.getOrgUnitChilds().get(l).getEmployees().size(); t++ ){
                    contentValues.put(DEPARTMENT_COLUMN_EMPLOYEES_ID, currentItem.getOrgUnitChilds().get(l).getEmployees().get(t).getId());
                }
            }

            for(int k = 0; k < currentItem.getEmployees().size(); k++){
                contentValuesEmployee.put(EMPLOYEE_COLUMN_ID, currentItem.getEmployees().get(k).getId());
                contentValuesEmployee.put(EMPLOYEE_COLUMN_NAME, currentItem.getEmployees().get(k).getLogin());
                contentValuesEmployee.put(EMPLOYEE_COLUMN_PASSWORD, currentItem.getEmployees().get(k).getPassword());
            }
            //
            mDB.insert(DEPARTMENT_TABLE, null, contentValues);
            mDB.insert(EMPLOYEE_TABLE, null, contentValuesEmployee);
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
            db.execSQL(Database.EMPLOYEE_TABLE_CREATE);
            db.execSQL("DROP TABLE OrgUnitVM");
            db.execSQL("DROP TABLE UserVM");

            // названия компаний (групп)
            /*
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
            */
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}



