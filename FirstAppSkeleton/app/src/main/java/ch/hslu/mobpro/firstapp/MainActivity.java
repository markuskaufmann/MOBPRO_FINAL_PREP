package ch.hslu.mobpro.firstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * The main activity, displays some buttons.
 *
 * @author Ruedi Arnold
 */


public class MainActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 23; // Arbitrary number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void startLogActivity(View v) {
        final Intent logIntent = new Intent(this, LifecycleLogActivity.class);
        startActivity(logIntent);
    }

    public void startBrowser(View v) {
        final Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse("https://www.hslu.ch"));
        startActivity(browserIntent);
    }

    public void startQuestionActivity(View v) {
        final Intent questionIntent = new Intent(this, QuestionActivity.class);
        questionIntent.putExtra("question", "Und, wie läufts so mit der Android-Programmierung bis jetzt?");
        startActivityForResult(questionIntent, MY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Use return data and print to log
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK) {
            TextView textView = (TextView) findViewById(R.id.main_textView_result);
            String answer = getResources().getString(R.string.main_text_gotAnswer) + "'" + data.getStringExtra("answer") + "'";
            textView.setText(answer);
        }
    }
}
