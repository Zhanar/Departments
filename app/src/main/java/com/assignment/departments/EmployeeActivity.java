package com.assignment.departments;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.assignment.departments.Model.Department;
import com.assignment.departments.Model.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Employee> listEmployee;
    ListAdapter listAdapter;
    TextView textViewEmployees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        Department d = (Department)getIntent().getSerializableExtra("subDepartment");

        getSupportActionBar().setTitle(d.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listEmployee = new ArrayList<>();
        listView = (ListView)findViewById(R.id.listViewEmployee);
        textViewEmployees = (TextView)findViewById(R.id.textViewEmployees);

        if(d.getEmployees().size() != 0){
            textViewEmployees.setText("Employees:");
            for(int i = 0; i < d.getEmployees().size(); i++){
                listEmployee.add(d.getEmployees().get(i));
            }
        }
        else {
            textViewEmployees.setText("There are no employees in that sub-department.");
        }
        listAdapter = new ListAdapter(this, 0, listEmployee);
        listView.setAdapter(listAdapter);
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
