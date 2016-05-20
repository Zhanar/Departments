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

    ListView listView;
    static ArrayList<Department> listDepartment;
    static ListAdapter listAdapter;
    static Gson gson = new Gson();
    Button buttonAdd;
    static AsyncHttpClient httpClient;
    Boolean internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);

        internet = isNetworkConnected();
        Toast.makeText(this, "Internet "+ internet, Toast.LENGTH_LONG).show();

        if (internet) {
            listDepartment = new ArrayList<>();
            listView = (ListView)findViewById(R.id.listViewDepartment);
            buttonAdd = (Button)findViewById(R.id.buttonAdd);

            listAdapter = new ListAdapter(this, 0, listDepartment);
            listView.setAdapter(listAdapter);

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
            SQLiteDatabase myDB = null;
            String TableName = "Departments";
            String Data = "";

            /* Create a Database. */
            try {
                myDB = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);

                /* Create a Table in the Database. */
                myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                        + TableName
                        + " (id VARCHAR, Field2 INT(3));");

                /* Insert data to a Table*/
                myDB.execSQL("INSERT INTO "
                        + TableName
                        + " (Field1, Field2)"
                        + " VALUES ('Saranga', 22);");

                /*retrieve data from database */
                Cursor c = myDB.rawQuery("SELECT * FROM " + TableName , null);

                int Column1 = c.getColumnIndex("Field1");
                int Column2 = c.getColumnIndex("Field2");

                // Check if our result was valid.
                c.moveToFirst();
                if (c != null) {
                    // Loop through all Results
                    do {
                        String Name = c.getString(Column1);
                        int Age = c.getInt(Column2);
                        Data =Data +Name+"/"+Age+"\n";
                    }while(c.moveToNext());
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(DepartmentActivity.this, SubDepartmentActivity.class);
                i.putExtra("department", listDepartment.get(position));
                startActivity(i);
            }
        });
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
