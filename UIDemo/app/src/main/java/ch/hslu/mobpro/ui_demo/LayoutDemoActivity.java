package ch.hslu.mobpro.ui_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LayoutDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int layoutId = getIntent().getIntExtra(getString(R.string.intent_extra_layout), 0);
        final int layout = layoutId == 0 ? R.layout.layoutdemo_linearlayout : R.layout.layoutdemo_constraintlayout;
        setContentView(layout);
        setResult(RESULT_OK);
    }
}
