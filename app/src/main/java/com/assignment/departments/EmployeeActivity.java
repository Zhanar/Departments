package com.assignment.departments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

import com.assignment.departments.Model.Department;
import com.assignment.departments.Model.Employee;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class EmployeeActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Employee> listEmployee;
    ListAdapter listAdapter;
    TextView textViewEmployees;
    Department department;
    AsyncHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        department = (Department)getIntent().getSerializableExtra("subDepartment");

        getSupportActionBar().setTitle(department.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listEmployee = new ArrayList<>();
        listView = (ListView)findViewById(R.id.listViewEmployee);
        textViewEmployees = (TextView)findViewById(R.id.textViewEmployees);

        if(department.getEmployees().size() != 0) {
            textViewEmployees.setText("Employees:");
            for(int i = 0; i < department.getEmployees().size(); i++){
                listEmployee.add(department.getEmployees().get(i));
            }
        }
        else {
            textViewEmployees.setText("There are no employees in that sub-department.");
        }
        listAdapter = new ListAdapter(this, 0, listEmployee);
        listView.setAdapter(listAdapter);

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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        httpClient = new AsyncHttpClient();
        final Employee s = listEmployee.get(info.position);
        RequestParams params = new RequestParams();
        params.put("orgUnitId", department.getId());
        params.put("userId", department.getEmployees().get(0).getId());

        httpClient.delete("http://orgunitapi.azurewebsites.net/orgunit/RemoveEmployee", params, new AsyncHttpResponseHandler() {
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

    public void addEmployee(View view) {
        Intent i = new Intent(EmployeeActivity.this, AddEmployeeActivity.class);
        i.putExtra("department", department);
        startActivity(i);
    }

    static class ListAdapter extends ArrayAdapter<Employee> {
        LayoutInflater inflater;

        public ListAdapter(Context context, int resource, List<Employee> objects) {
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
            Employee r = getItem(position);
            vh.item.setText(r.getLogin());
            vh.position.setText("" + position);
            return v;
        }

        static class ViewHolder {
            TextView position;
            TextView item;
        }
    }
}
