package com.assignment.departments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.assignment.departments.Model.Department;
import com.assignment.departments.Model.Employee;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ChangeTopManagerActivity extends AppCompatActivity {

    TextView textView;
    ArrayList<Employee> listTopManagers;
    ListView listView;
    EmployeeActivity.ListAdapter listAdapter;
    Department department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_top_manager);

        department = (Department)getIntent().getSerializableExtra("department");
        listView = (ListView)findViewById(R.id.listViewChangeTopManager);
        textView = (TextView)findViewById(R.id.textViewTopManager);
        if (department == null){
            textView.setText("There are no top managers in that department.");
        }
        else {
            textView.setText("Choose top manager:");
            listTopManagers = new ArrayList<>();
            for(int i = 0; i < department.getEmployees().size(); i++) {
                listTopManagers.add(department.getEmployees().get(i));
            }
            listAdapter = new EmployeeActivity.ListAdapter(this, 0, listTopManagers);
            listView.setAdapter(listAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AsyncHttpClient httpClient = new AsyncHttpClient();
                    RequestParams params = new RequestParams();

                    params.put("userId", listTopManagers.get(position).getId());
                    params.put("orgUnitId", department.getId());

                    httpClient.post("http://orgunitapi.azurewebsites.net/orgunit/AddHeader", params, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("HTTP", "added-result " + response);
                            Intent intent = new Intent(ChangeTopManagerActivity.this, SubDepartmentActivity.class);
                            intent.putExtra("department", department);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Log.d("Fail HTTP", "" + responseString);
                        }
                    });
                }
            });
        }
    }
}
