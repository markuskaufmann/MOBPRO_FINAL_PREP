package ch.hslu.mobpro.ui_demo;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CourseListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] courses = getResources().getStringArray(R.array.it_courses);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, courses);
        this.setListAdapter(adapter);
        ListView view = getListView();
        view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent result = new Intent();
        result.putExtra(getString(R.string.intent_extra_course), position);
        setResult(RESULT_OK, result);
        finish();
    }
}
