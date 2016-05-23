package com.assignment.departments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.departments.Model.Department;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DepartmentActivity extends AppCompatActivity {
    final String TAG = "States";

    ListView listView;
    static ArrayList<Department> listDepartment;
    static ListAdapter listAdapter;
    static Gson gson = new Gson();
    Button buttonAdd;
    static AsyncHttpClient httpClient;
    Boolean internet;
    static Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);

        // подключаемся к БД
        database = new Database(this);
        database.open();

        // готовим данные по группам для адаптера
        Cursor cursor = database.getDepartmentData();
        startManagingCursor(cursor);

        internet = isNetworkConnected();
        Toast.makeText(this, "Internet "+ internet, Toast.LENGTH_LONG).show();

        listDepartment = new ArrayList<>();
        listView = (ListView)findViewById(R.id.listViewDepartment);
        listAdapter = new ListAdapter(this, 0, listDepartment);
        listView.setAdapter(listAdapter);

        if (internet) {

            buttonAdd = (Button)findViewById(R.id.buttonAdd);
            loadOrgUnits();

            listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                    menu.setHeaderTitle("Delete selected item?");
                    String[] menuItems = new String[]{"Yes"};
                    for (int i = 0; i < menuItems.length; i++) {
                        menu.add(Menu.NONE, i, i, menuItems[i]);
                    }
                }
            });
        }
        else {

        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(DepartmentActivity.this, SubDepartmentActivity.class);
                i.putExtra("department", listDepartment.get(position));
                startActivity(i);
            }
        });
    }
/*
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity: onStart()");

        if(listDepartment.size() != 0){
            SQLiteDatabase myDB = null;
            String Data = "";

            // Create a Database.
            try {
                myDB = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);

                myDB.execSQL("DROP TABLE OrgUnitVM");
                myDB.execSQL("DROP TABLE UserVM");

                // Create a Table in the Database.
                myDB.execSQL("CREATE TABLE IF NOT EXISTS OrgUnitVM"
                        + " (_id INTEGER, title TEXT, headUserId INTEGER, orgUnitParentId INTEGER, employeesId INTEGER, orgUnitChildId INTEGER);");

                myDB.execSQL("CREATE TABLE IF NOT EXISTS UserVM"
                        + " (_id INTEGER, login TEXT, password TEXT);");


                // Insert data to a Table
                for(int i = 0; i < listDepartment.size(); i++){
                    if(listDepartment.get(i).getId()!= 92){
                        for(int j = 0; j < listDepartment.get(i).getEmployees().size(); j++){
                            for (int l = 0; l < listDepartment.get(i).getOrgUnitChilds().size(); l++){
//                                if(listDepartment.get(i).getOrgUnitParrent() != null){
                                    myDB.execSQL("INSERT INTO OrgUnitVM"
                                            + " (_id, title, headUserId, employeesId, orgUnitChildId)"
                                            + " VALUES ("+ listDepartment.get(i).getId() + ", ' "
                                            + listDepartment.get(i).getTitle() + " ' , "
                                            + listDepartment.get(i).getHeadUser().getId() + " , "
//                                            + " ISNULL( "+listDepartment.get(i).getOrgUnitParrent() + ", 0) , "
                                            + listDepartment.get(i).getEmployees().get(j).getId() + " , "
                                            + listDepartment.get(i).getOrgUnitChilds().get(l).getId() +" );");
//                                }

                            }

                            myDB.execSQL("INSERT INTO UserVM"
                                    + " (_id, login, password)"
                                    + " VALUES ("+ listDepartment.get(i).getEmployees().get(j).getId() + " , ' "
                                    + listDepartment.get(i).getEmployees().get(j).getLogin() + " ' , ' "
                                    + listDepartment.get(i).getEmployees().get(j).getPassword() + " ' );");
                        }
                    }
                }

//                myDB.execSQL("INSERT INTO OrgUnitVM"
//                        + " (id, title)"
//                        + " VALUES ('1', 'department');");


            // retrieve data from database
                Cursor cursorDepartment = myDB.rawQuery("SELECT * FROM OrgUnitVM" , null);
                Cursor cursorEmployee = myDB.rawQuery("SELECT * FROM Employee" , null);

                int ColumnId = cursorDepartment.getColumnIndex("_id");
                int ColumnTitle = cursorDepartment.getColumnIndex("title");
                int ColumnHeadUserId = cursorDepartment.getColumnIndex("headUserId");
                int ColumnOrgUnitParentId = cursorDepartment.getColumnIndex("orgUnitParentId");
                int ColumnEmployeesId = cursorDepartment.getColumnIndex("employeesId");
                int ColumnOrgUnitChildId = cursorDepartment.getColumnIndex("orgUnitChildId");

                int ColumnEmployeeId = cursorEmployee.getColumnIndex("_id");
                int ColumnEmployeeLogin = cursorEmployee.getColumnIndex("login");
                int ColumnEmployeePassword = cursorEmployee.getColumnIndex("password");


                // Check if our result was valid.
                cursorDepartment.moveToFirst();
                if (cursorDepartment != null) {
                    // Loop through all Results
                    do {
                        int id = cursorDepartment.getInt(ColumnId);
                        String title = cursorDepartment.getString(ColumnTitle);
                        int headUserId = cursorDepartment.getInt(ColumnHeadUserId);
                        int orgUnitParentId = cursorDepartment.getInt(ColumnOrgUnitParentId);
                        int employeesId = cursorDepartment.getInt(ColumnEmployeesId);
                        int orgUnitChildId = cursorDepartment.getInt(ColumnOrgUnitChildId);

                        Data = Data + id + " / " + title + " / " + headUserId + " / " + orgUnitParentId + " / " + employeesId + " / " + orgUnitChildId + "\n";
                    } while (cursorDepartment.moveToNext());
                }

                cursorEmployee.moveToFirst();
                if(cursorEmployee != null){
                    do {
                        int employeeId = cursorDepartment.getInt(ColumnEmployeeId);
                        String employeeLogin = cursorDepartment.getString(ColumnEmployeeLogin);
                        String employeePassword = cursorDepartment.getString(ColumnEmployeePassword);
                        Data += "Employee: " + employeeId + " / " + employeeLogin + " / " + employeePassword + "\n";
                    }
                    while(cursorEmployee.moveToNext());
                }
                TextView tv = new TextView(this);
                tv.setText(Data);
                setContentView(tv);
            }
            catch(Exception e) {
                Log.e("Error", "Error", e);
            } finally {
                if (myDB != null)
                    myDB.close();
            }
        }
    }
*/
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        // получаем курсор по элементам для конкретной группы
        int idColumn = groupCursor.getColumnIndex(Database.DEPARTMENT_COLUMN_ID);
        return database.getEmployeeData(groupCursor.getInt(idColumn));
    }

    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        httpClient = new AsyncHttpClient();
        final Department s = listDepartment.get(info.position);
        RequestParams params = new RequestParams();
        params.put("id", s.getId());

        httpClient.delete("http://orgunitapi.azurewebsites.net/orgunit/Delete", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                listAdapter.remove(s);
                listAdapter.notifyDataSetChanged();
                loadOrgUnits();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("Error", error.getMessage());
            }
        });
        return true;
    }

    public static void loadOrgUnits()
    {
        httpClient = new AsyncHttpClient();

        httpClient.get("http://orgunitapi.azurewebsites.net/orgunit/GetMainOrgUnits", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("HTTP", "result " + response);
                listDepartment.clear();
                try {
                    for (int i = 0; i < response.length(); ++i) {
                        Department department = gson.fromJson(response.getString(i), Department.class);
                        listDepartment.add(department);
                    }
                    database.SaveDepartments(listDepartment);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    public void addDepartment(View view) {
        Intent i = new Intent(DepartmentActivity.this, AddDepartmentActivity.class);
        //i.putExtra("department", listDepartment.get(position));
        startActivity(i);
    }

    static class ListAdapter extends ArrayAdapter<Department> {
        LayoutInflater inflater;

        public ListAdapter(Context context, int resource, List<Department> objects) {
            super(context, resource, objects);
            inflater = ((Activity)context).getLayoutInflater();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            ViewHolder vh;
            if (convertView == null) {
                v = inflater.inflate(R.layout.list_item, null);
                vh = new ViewHolder();
                vh.item = (TextView)v.findViewById(R.id.item);
                vh.position = (TextView)v.findViewById(R.id.position);
                v.setTag(vh);
            } else {
                v = convertView;
                vh = (ViewHolder)v.getTag();
            }
            Department r = getItem(position);
            vh.item.setText(r.getTitle());
            vh.position.setText("" + position);

            return v;
        }

        static class ViewHolder {
            TextView position;
            TextView item;
        }
    }
}
