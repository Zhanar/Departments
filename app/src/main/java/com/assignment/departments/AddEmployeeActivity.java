package com.assignment.departments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.assignment.departments.Model.Department;
import com.assignment.departments.Model.Employee;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AddEmployeeActivity extends AppCompatActivity {

    EmployeeActivity.ListAdapter listAdapter;
    Department department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
    }

    public void buttonAddEmployee(View view) {

        department = (Department)getIntent().getSerializableExtra("department");
        EditText editText = (EditText) findViewById(R.id.editTextEmployee);
        AsyncHttpClient httpClient = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("login", editText.getText().toString());
        params.put("password", editText.getText().toString());

        httpClient.post("http://orgunitapi.azurewebsites.net/User/Register", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("HTTP", "added-result " + response);
                RequestParams params2 = new RequestParams();
                params2.put("orgUnitId", department.getId());
                try {
                    params2.put("userId", response.getInt("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AsyncHttpClient httpClient2 = new AsyncHttpClient();
                httpClient2.post("http://orgunitapi.azurewebsites.net/OrgUnit/AddEmployee", params2, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("HTTP", "added-result " + response);
//                        listAdapter.notifyDataSetChanged();

                        DepartmentActivity.loadOrgUnits();
                        Intent i = new Intent(AddEmployeeActivity.this, DepartmentActivity.class);
                        //i.putExtra("department", listDepartment.get(position));
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                int s = 8;
            }
        });
    }
}
