package ch.hslu.mobpro.persistenz;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String ON_RESUME_INVOKE_COUNT = "invoke_count";
    private static final int REQUEST_PREFERENCE_ACTIVITY = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 24;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 25;
    private static final String STORAGE_FILE = "PersistentMessage.txt";

    private SharedPreferences preferences;
    private File internalStorageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // prefs
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setEditTeaPrefsListener();
        setTeaDefaultPrefsListener();
        setDefaultTeaPreferences();

        // fs
        this.internalStorageDir = getApplicationContext().getFilesDir();
        setExternalStorageCheckedListener();
        setExternalStorageState();
        setSaveToStorageListener();
        setLoadFromStorageListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        incrementInvokeCount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_PREFERENCE_ACTIVITY) {
            if(resultCode == RESULT_OK) {
                final Object[] teaPrefs = readTeaPreferences();
                updateTeaPrefsTextView(teaPrefs);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission " + permissions[0] + " denied!", Toast.LENGTH_LONG).show();
                    updateStorageStateTextView("Error while loading: Permission not granted.");
                } else {
                    readFromExternalStorage();
                }
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission " + permissions[0] + " denied!", Toast.LENGTH_LONG).show();
                    updateStorageStateTextView("Error while saving: Permission not granted");
                } else {
                    writeToExternalStorage();
                }
                break;
        }
    }


    /**
     * prefs functions
     * **/

    private void setEditTeaPrefsListener() {
        final Button btnEditTeaPrefs = findViewById(R.id.btnEditTeaPrefs);
        btnEditTeaPrefs.setOnClickListener(view -> startTeaPreferenceActivity());
    }

    private void setTeaDefaultPrefsListener() {
        final Button btnTeaDefaultPrefs = findViewById(R.id.btnTeaDefaultPrefs);
        btnTeaDefaultPrefs.setOnClickListener(view -> setDefaultTeaPreferences());
    }

    private void startTeaPreferenceActivity() {
        Intent preferences = new Intent(this, AppPreferenceActivity.class);
        startActivityForResult(preferences, REQUEST_PREFERENCE_ACTIVITY);
    }

    private void incrementInvokeCount() {
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        final int resumeCount = preferences.getInt(ON_RESUME_INVOKE_COUNT, 0) + 1;
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ON_RESUME_INVOKE_COUNT, resumeCount);
        editor.apply();
        updateCountTextView(resumeCount);
    }

    private void setDefaultTeaPreferences() {
        writeTeaPreferences(false, getString(R.string.prefs_teaSweetener_default), getString(R.string.prefs_teaPreferred_default));
        final Object[] teaPrefs = readTeaPreferences();
        updateTeaPrefsTextView(teaPrefs);
    }

    private void writeTeaPreferences(final boolean isSweetened, final String sweetener, final String preference) {
        final SharedPreferences.Editor editor = this.preferences.edit();
        editor.putBoolean(getString(R.string.prefs_teaWithSugar_key), isSweetened);
        editor.putString(getString(R.string.prefs_teaSweetener_key), sweetener);
        editor.putString(getString(R.string.prefs_teaPreferred_key), preference);
        editor.apply();
    }

    private Object[] readTeaPreferences() {
        Object[] values = new Object[3];

        values[0] = this.preferences.getBoolean(getString(R.string.prefs_teaWithSugar_key), false);
        values[1] = this.preferences.getString(getString(R.string.prefs_teaSweetener_key), getString(R.string.prefs_teaSweetener_default));
        values[2] = this.preferences.getString(getString(R.string.prefs_teaPreferred_key), getString(R.string.prefs_teaPreferred_default));
        values[1] = retrieveTeaSweetenerValue(String.valueOf(values[1]));

        return values;
    }

    private String retrieveTeaSweetenerValue(final String key) {
        if(key == null || key.trim().length() == 0) {
            return null;
        }

        final String[] teaSweetener = getResources().getStringArray(R.array.teaSweetenerValues);
        final String[] teaSweetenerValues = getResources().getStringArray(R.array.teaSweetener);
        for (int i = 0; i < teaSweetener.length; i++) {
            final String sweetener = teaSweetener[i];
            if(sweetener.equals(key)) {
                return teaSweetenerValues[i];
            }
        }
        return null;
    }

    private void updateCountTextView(final int resumeCount) {
        final TextView resumeCountView = findViewById(R.id.txtResumeInvoke);
        final String viewContent = getString(R.string.mainactivity_invoke, resumeCount);
        resumeCountView.setText(viewContent);
    }

    private void updateTeaPrefsTextView(final Object[] prefs) {
        final boolean isSweetened = (boolean) prefs[0];
        final String sweetener = String.valueOf(prefs[1]);
        final String preference = String.valueOf(prefs[2]);
        final String sweetened = isSweetened ? getString(R.string.prefs_teaSweetener_sweetened, sweetener)
                                                : getString(R.string.prefs_teaSweetener_unsweetened);
        final TextView teaPrefs = findViewById(R.id.txtTeaPrefs);
        teaPrefs.setText(getString(R.string.preferred_tea, preference, sweetened));
    }


    /**
     * fs functions
     * **/

    private void setExternalStorageCheckedListener() {
        final CheckBox cbExternalStorage = findViewById(R.id.cbExternalStorage);
        cbExternalStorage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                setExternalStorageState();
            } else {
                updateStorageStateTextView("Internal storage mounted");
            }
        });
    }

    private void setExternalStorageState() {
        updateStorageStateTextView("External storage: " + Environment.getExternalStorageState());
    }

    private void setSaveToStorageListener() {
        final Button btnSaveStorage = findViewById(R.id.btnSaveToStorage);
        btnSaveStorage.setOnClickListener((view) -> {
            if(usesExternalStorage()) {
                checkExternalStoragePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_EXTERNAL_STORAGE,
                                                this::writeToExternalStorage);
            } else {
                writeToInternalStorage();
            }
        });
    }

    private void setLoadFromStorageListener() {
        final Button btnLoadStorage = findViewById(R.id.btnLoadFromStorage);
        btnLoadStorage.setOnClickListener((view) -> {
            if(usesExternalStorage()) {
                checkExternalStoragePermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE,
                                                this::readFromExternalStorage);
            } else {
                readFromInternalStorage();
            }
        });
    }

    private boolean usesExternalStorage() {
        final CheckBox cbExternalStorage = findViewById(R.id.cbExternalStorage);
        return cbExternalStorage.isChecked();
    }

    private void checkExternalStoragePermission(final String permission, final int requestCode, final Runnable whenGranted) {
        final int grant = checkSelfPermission(permission);
        if(grant != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { permission }, requestCode);
        } else {
            whenGranted.run();
        }
    }

    private void readFromInternalStorage() {
        final String value = readFromFile(this.internalStorageDir, STORAGE_FILE);
        updateReadValue(value);
    }

    private void writeToInternalStorage() {
        final boolean succeeded = writeToFile(this.internalStorageDir, STORAGE_FILE, getStorageEditText());
        updateWrittenValue(succeeded);
    }

    private void readFromExternalStorage() {
        final String value = readFromFile(Environment.getExternalStorageDirectory(), STORAGE_FILE);
        updateReadValue(value);
    }

    private void writeToExternalStorage() {
        final boolean succeeded = writeToFile(Environment.getExternalStorageDirectory(), STORAGE_FILE, getStorageEditText());
        updateWrittenValue(succeeded);
    }

    private String readFromFile(final File parentDir, final String fileName) {
        try(final BufferedReader reader = new BufferedReader(new FileReader(new File(parentDir, fileName)))) {
            return reader.readLine();
        } catch (IOException e) {
            Log.e("Persistenz", "Error while reading from file: " + parentDir.getAbsolutePath(), e);
        }
        return null;
    }

    private boolean writeToFile(final File parentDir, final String fileName, final String value) {
        try(final BufferedWriter writer = new BufferedWriter(new FileWriter(new File(parentDir, fileName)))) {
            writer.write(value);
            return true;
        } catch (IOException e) {
            Log.e("Persistenz", "Error while writing to file: " + parentDir.getAbsolutePath(), e);
        }
        return false;
    }

    private void updateReadValue(final String value) {
        String state = "Load succeeded.";
        if(value != null) {
            setStorageEditText(value);
        } else {
            state = "Error while loading. Please consider log file.";
        }
        updateStorageStateTextView(state);
    }

    private void updateWrittenValue(final boolean writtenResult) {
        String state = "Save succeeded.";
        if(!writtenResult) {
            state = "Error while saving. Please consider log file.";
        }
        updateStorageStateTextView(state);
    }

    private String getStorageEditText() {
        final EditText txtStorage = findViewById(R.id.txtSave);
        return txtStorage.getText().toString();
    }

    private void setStorageEditText(final String value) {
        final EditText txtStorage = findViewById(R.id.txtSave);
        txtStorage.setText(value);
    }

    private void updateStorageStateTextView(final String updatedState) {
        final TextView txtStorageState = findViewById(R.id.txtFSState);
        txtStorageState.setText(updatedState);
    }
}
