package com.assignment.departments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.assignment.departments.Model.Department;
import com.assignment.departments.Model.Employee;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SubDepartmentActivity extends AppCompatActivity {

    ListView listView;
    TextView textView;
    TextView textViewHead;
    ArrayList<Department> listSubDepartment;
    DepartmentActivity.ListAdapter listAdapter;
    Department department;
    AsyncHttpClient httpClient;
    Button changeHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_department);

        department = (Department)getIntent().getSerializableExtra("department");

        getSupportActionBar().setTitle(department.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeHead = (Button)findViewById(R.id.buttonChangeHead);
        textViewHead = (TextView)findViewById(R.id.textViewHead);
        textViewHead.setText("Head of Department: " + department.getHeadUser().getLogin());

        textView = (TextView)findViewById(R.id.textViewDepartments);
        //textView.setText(department.getTitle());
        listSubDepartment = new ArrayList<>();
        listView = (ListView)findViewById(R.id.listViewSubDepartment);

        if (department.getOrgUnitChilds().size() != 0) {
            textView.setText("Sub Departments:");
            for(int i = 0; i < department.getOrgUnitChilds().size(); i++) {
                listSubDepartment.add(department.getOrgUnitChilds().get(i));
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
                Intent intent = new Intent(SubDepartmentActivity.this, EmployeeActivity.class);
                intent.putExtra("subDepartment", listSubDepartment.get(position));
                startActivity(intent);
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
        params.put("parrentOrgUnitId", department.getId());
        httpClient.post("http://orgunitapi.azurewebsites.net/OrgUnit/Create", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("HTTP", "added-result " + response);
            }
        });
    }

    public void changeHead(View view) {


        Intent intent = new Intent(SubDepartmentActivity.this, ChangeTopManagerActivity.class);
        intent.putExtra("department", department);
        startActivity(intent);
    }
}
