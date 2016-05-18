package com.assignment.departments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.assignment.departments.Model.Department;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
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
    ArrayList<Department> listDepartment;
    ListAdapter listAdapter;
    Gson gson = new Gson();
    Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);

        listDepartment = new ArrayList<>();
        listView = (ListView)findViewById(R.id.listViewDepartment);
        buttonAdd = (Button)findViewById(R.id.buttonAdd);

        listAdapter = new ListAdapter(this, 0, listDepartment);
        listView.setAdapter(listAdapter);

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get("http://orgunitapi.azurewebsites.net/orgunit/GetMainOrgUnits", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("HTTP", "result " + response);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(DepartmentActivity.this, SubDepartmentActivity.class);
                i.putExtra("department", listDepartment.get(position));
                startActivity(i);
            }
        });

    }

    public void addDepartment(View view) {
        //Intent intent = new Intent(this, AddDepartmentActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        //String department = editText.getText().toString();

        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("title", editText.getText().toString());
        httpClient.post("http://orgunitapi.azurewebsites.net/OrgUnit/Create", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("Added!", "" + response);
            }
        });
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
