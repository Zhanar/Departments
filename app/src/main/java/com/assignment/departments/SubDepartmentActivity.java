package com.assignment.departments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
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

import com.assignment.departments.Model.Department;
import com.assignment.departments.Model.Employee;
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

public class SubDepartmentActivity extends AppCompatActivity {

    ListView listView;
    TextView textView;
    TextView textViewHead;
    ArrayList<Department> listSubDepartment;
    DepartmentActivity.ListAdapter listAdapter;
    Department d;
    AsyncHttpClient httpClient;
    Button changeHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_department);

        d = (Department)getIntent().getSerializableExtra("department");

        getSupportActionBar().setTitle(d.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewHead = (TextView)findViewById(R.id.textViewHead);
        textViewHead.setText("Head of Department: " + d.getHeadUser().getLogin());

        textView = (TextView)findViewById(R.id.textViewDepartments);
        //textView.setText(d.getTitle());
        listSubDepartment = new ArrayList<>();
        listView = (ListView)findViewById(R.id.listViewSubDepartment);

        if (d.getOrgUnitChilds().size() != 0) {
            textView.setText("Sub Departments:");
            for(int i = 0; i < d.getOrgUnitChilds().size(); i++) {
                listSubDepartment.add(d.getOrgUnitChilds().get(i));
            }
        }
        else {
            textView.setText("There are no sub-departments in that Department.");
        }
        listAdapter = new DepartmentActivity.ListAdapter(this, 0, listSubDepartment);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SubDepartmentActivity.this, EmployeeActivity.class);
                i.putExtra("subDepartment", listSubDepartment.get(position));
                startActivity(i);
            }
        });

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                menu.setHeaderTitle("Delete selected item?");
                String[] menuItems = new String[]{"Yes"};
                for (int i = 0; i<menuItems.length; i++) {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
            }
        });
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        httpClient = new AsyncHttpClient();
        final Department s = listSubDepartment.get(info.position);
        RequestParams params = new RequestParams();
        params.put("id", s.getId());

        httpClient.delete("http://orgunitapi.azurewebsites.net/orgunit/Delete", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                listAdapter.remove(s);
                listAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("Error", error.getMessage());
            }
        });
        return true;
    }

    public void addSubDepartment(View view) {
        //Intent intent = new Intent(this, AddDepartmentActivity.class);
        EditText editText = (EditText) findViewById(R.id.editTextSubDepartment);
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("title", editText.getText().toString());
        params.put("parrentOrgUnitId", d.getId());
        httpClient.post("http://orgunitapi.azurewebsites.net/OrgUnit/Create", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("Added!", "" + response);
            }
        });
    }

    public void changeHead(View view) {

        changeHead = (Button)findViewById(R.id.buttonChangeHead);

        ArrayList<Employee> head = new ArrayList<>();
        for(int i = 0; i < d.getEmployees().size(); i++) {
            head.add(d.getEmployees().get(i));
        }
    }
}
