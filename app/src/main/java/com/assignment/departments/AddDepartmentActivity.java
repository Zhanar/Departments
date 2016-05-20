package com.assignment.departments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.assignment.departments.Model.Department;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AddDepartmentActivity extends AppCompatActivity {

    Department subDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_department);
    }

    public void buttonAddDepartment(View view) {

        subDepartment = (Department)getIntent().getSerializableExtra("subDepartment");
        EditText editText = (EditText) findViewById(R.id.editTextDepartment);
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        if(subDepartment == null) {
            params.put("title", editText.getText().toString());
            httpClient.post("http://orgunitapi.azurewebsites.net/OrgUnit/Create", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("HTTP", "added-result " + response);

                    // TODO: 1
                    // reload all org units
                    DepartmentActivity.loadOrgUnits();
                    Intent i = new Intent(AddDepartmentActivity.this, DepartmentActivity.class);
                    //i.putExtra("department", listDepartment.get(position));
                    startActivity(i);
                }
            });
        }
        else {
            params.put("title", editText.getText().toString());
            params.put("parrentOrgUnitId", subDepartment.getOrgUnitParrent().getId());
            httpClient.post("http://orgunitapi.azurewebsites.net/OrgUnit/Create", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("HTTP", "added-result " + response);

                    DepartmentActivity.loadOrgUnits();
                    Intent i = new Intent(AddDepartmentActivity.this, DepartmentActivity.class);
                    //i.putExtra("department", listDepartment.get(position));
                    startActivity(i);
                }
            });
        }
    }
}
