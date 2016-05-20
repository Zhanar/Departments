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

    ListView listViewSubDepartment;
    ListView listViewEmployee;
    TextView textView;
    TextView textViewHead;
    ArrayList<Department> listSubDepartment;
    ArrayList<Employee> listEmployee;
    DepartmentActivity.ListAdapter listAdapterDepartment;
    EmployeeActivity.ListAdapter listAdapterEmployee;
    Department department;
    Boolean internet;

    AsyncHttpClient httpClient;
    Button changeHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_department);

//        internet = DepartmentActivity.isNetworkConnected();
        department = (Department)getIntent().getSerializableExtra("department");

        getSupportActionBar().setTitle(department.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeHead = (Button)findViewById(R.id.buttonChangeHead);
        textViewHead = (TextView)findViewById(R.id.textViewHead);
        textViewHead.setText("Head of Department: " + department.getHeadUser().getLogin());

        textView = (TextView)findViewById(R.id.textViewDepartments);
        //textView.setText(department.getTitle());
        listSubDepartment = new ArrayList<>();
        listEmployee = new ArrayList<>();

        listViewSubDepartment = (ListView)findViewById(R.id.listViewSubDepartment);
        listViewEmployee = (ListView)findViewById(R.id.listViewEmployeeInSubDepartment);

        if (department.getOrgUnitChilds().size() != 0) {
            textView.setText("Sub Departments:");
            for(int i = 0; i < department.getOrgUnitChilds().size(); i++) {
                listSubDepartment.add(department.getOrgUnitChilds().get(i));
            }
        }
        else {
            textView.setText("There are no sub-departments in that Department.");
        }

        if(department.getEmployees().size() != 0){
            for(int i = 0; i < department.getEmployees().size(); i++) {
                listEmployee.add(department.getEmployees().get(i));
            }
        }
        listAdapterEmployee = new EmployeeActivity.ListAdapter(this, 0, listEmployee);
        listViewEmployee.setAdapter(listAdapterEmployee);

        listAdapterDepartment = new DepartmentActivity.ListAdapter(this, 0, listSubDepartment);
        listViewSubDepartment.setAdapter(listAdapterDepartment);

        listViewSubDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SubDepartmentActivity.this, EmployeeActivity.class);
                intent.putExtra("subDepartment", listSubDepartment.get(position));
                startActivity(intent);
            }
        });

        listViewSubDepartment.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
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
                listAdapterDepartment.remove(s);
                listAdapterDepartment.notifyDataSetChanged();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("Error", error.getMessage());
            }
        });
        return true;
    }

    public void addSubDepartment(View view) {
        Intent i = new Intent(SubDepartmentActivity.this, AddDepartmentActivity.class);
        i.putExtra("subDepartment", listSubDepartment.get(0));
        startActivity(i);
    }

    public void changeHead(View view) {
        Intent intent = new Intent(SubDepartmentActivity.this, ChangeTopManagerActivity.class);
        intent.putExtra("department", department);
        startActivity(intent);
    }

    public void addEmployeeInSubDepartment(View view) {
        Intent i = new Intent(SubDepartmentActivity.this, AddEmployeeActivity.class);
        i.putExtra("department", department);
        startActivity(i);
    }
}
