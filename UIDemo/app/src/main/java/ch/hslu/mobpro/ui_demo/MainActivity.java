package ch.hslu.mobpro.ui_demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT_DEMO_REQUEST = 24;
    private static final int COURSE_LIST_REQUEST = 42;

    private int stateCounter = 0;

    private RadioGroup radioGroup;
    private RadioButton radLinLayout;
    private RadioButton radRelLayout;
    private Spinner spinner;
    private Spinner spinnerNotificationDialog;

    /**
     * Overridden / lifecycle methods
     * **/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRadioChangedListener();
        setViewDemoListener();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setSpinnerSelectionListener();
            setSpinnerNotificationDialogListener();
            setCourseListActivityListener();
        }
        setCountStateListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(super.onOptionsItemSelected(item)) {
            return true;
        }
        switch(item.getItemId()) {
            case R.id.main_menu_finish:
                finishActivity();
                break;
            case R.id.main_menu_viewsdemo:
                startViewDemo();
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.bundle_counter), this.stateCounter);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.stateCounter = savedInstanceState.getInt(getString(R.string.bundle_counter));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LAYOUT_DEMO_REQUEST) {
            this.radioGroup.clearCheck();
        }
        else if(requestCode == COURSE_LIST_REQUEST) {
            if(resultCode == RESULT_OK) {
                final int position = data.getIntExtra(getString(R.string.intent_extra_course), 0);
                this.spinner.setSelection(position);
            }
        }
    }


    /**
     * private methods
     * **/

    private void setCourseListActivityListener() {
        final Button btnCourseListActivity = findViewById(R.id.btnCourseListActivity);
        btnCourseListActivity.setOnClickListener((view) -> startCourseListActivity());
    }

    private void setCountStateListener() {
        final int btnId = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                            ? R.id.btnCountState
                            : R.id.btnCountStateLS;
        final Button btnViewDemo = findViewById(btnId);
        btnViewDemo.setOnClickListener((view) -> incrementStateCounter());
    }

    private void setSpinnerSelectionListener() {
        this.spinner = findViewById(R.id.main_spinner);
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                if(position != 0) {
                    showToast("Item selected: " + selectedItem, Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setSpinnerNotificationDialogListener() {
        this.spinnerNotificationDialog = findViewById(R.id.main_spinner_notification_dialog);
        this.spinnerNotificationDialog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        showToast("Sample Toast", Toast.LENGTH_LONG);
                        break;
                    case 2:
                        showDialogWithoutData();
                        break;
                    case 3:
                        showDialogWithData();
                        break;
                    case 4:
                        showDialogWithLayout();
                        break;
                    case 5:
                        showNotification();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setViewDemoListener() {
        final Button btnViewDemo = findViewById(R.id.btnViewDemo);
        btnViewDemo.setOnClickListener((view) -> startViewDemo());
    }

    private void setRadioChangedListener() {
        this.radioGroup = findViewById(R.id.radGroup);
        this.radLinLayout = findViewById(R.id.radLinLayout);
        this.radRelLayout = findViewById(R.id.radRelLayout);
        this.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int checked = -1;

            if(this.radLinLayout.isChecked()) {
                checked = 0;
            }
            else if(this.radRelLayout.isChecked()) {
                checked = 1;
            }

            if(checked != -1) {
                startLayoutDemo(checked);
            }
        });
    }

    private void startCourseListActivity() {
        final Intent courseList = new Intent(this, CourseListActivity.class);
        startActivityForResult(courseList, COURSE_LIST_REQUEST);
    }

    private void startViewDemo() {
        final Intent startViewDemo = new Intent(this, ViewsDemoActivity.class);
        startActivity(startViewDemo);
    }

    private void startLayoutDemo(final int checked) {
        final Intent startLayoutDemo = new Intent(this, LayoutDemoActivity.class);
        startLayoutDemo.putExtra(getString(R.string.intent_extra_layout), checked);
        startActivityForResult(startLayoutDemo, LAYOUT_DEMO_REQUEST);
    }

    private void finishActivity() {
        finish();
    }

    private void showDialogWithoutData() {
        new AlertDialog.Builder(this)
                .setTitle("Reaktorproblem")
                .setMessage("Kühlwasserzufuhr unterbrochen!\nWas nun?")
                .setPositiveButton("Abwarten", (dialog, which) -> {
                    showToast("Abwarten", Toast.LENGTH_SHORT);
                })
                .setNeutralButton("Weiss nicht", (dialog, which) -> {
                    showToast("Weiss nicht", Toast.LENGTH_SHORT);
                })
                .setNegativeButton("Abschalten", (dialog, which) -> {
                    showToast("Abschalten", Toast.LENGTH_SHORT);
                })
                .create().show();
    }

    private void showDialogWithData() {
        final String[] beverages = new String[]{"Kaffee", "Tee", "Wasser"};
        new AlertDialog.Builder(this)
                .setTitle("Wähle ein Getränk")
                .setItems(beverages, (dialog, which) -> {
                    showToast(beverages[which], Toast.LENGTH_LONG);
                })
                .setNeutralButton("Keinen Durst", (dialog, which) -> {
                    showToast("Keinen Durst", Toast.LENGTH_LONG);
                })
                .create().show();
    }

    private void showDialogWithLayout() {

    }

    private void showNotification() {

    }

    private void incrementStateCounter() {
        this.stateCounter +=1;
        showToast("Zähler = " + this.stateCounter, Toast.LENGTH_SHORT);
    }

    private void showToast(final String text, final int length) {
        Toast.makeText(getApplicationContext(), text, length).show();
    }
}
