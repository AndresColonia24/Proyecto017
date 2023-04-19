package com.example.material;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutNote, textInputLayoutDate;
    private EditText editTextNote, editTextDate;
    private TextView noteDate;
    private Button themeButton;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInputLayoutNote = findViewById(R.id.textField);
        editTextNote = textInputLayoutNote.getEditText();

        textInputLayoutDate = findViewById(R.id.dateField);
        editTextDate = textInputLayoutDate.getEditText();

        noteDate = findViewById(R.id.noteDate);

        themeButton = findViewById(R.id.theme_button);

        int themeId = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (themeId == Configuration.UI_MODE_NIGHT_YES) {
            themeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_dark_mode_24), null, null, null);
        } else {
            themeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_light_mode_24), null, null, null);
        }
        editTextDate.addTextChangedListener(new TextWatcher() {
            private final String DATE_PATTERN = "([0-9]{2})/([0-9]{2})/([0-9]{2})";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputText = s.toString();
                if (!inputText.matches(DATE_PATTERN)) {
                    textInputLayoutDate.setError("Formato de fecha inválido. El formato correcto es dd/mm/aa.");
                } else {
                    textInputLayoutDate.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void saveNote(View view) {
        String fileName = editTextDate.getText().toString();
        fileName = fileName.replace('/', '-');
        if (fileName.trim().equals("")) {
            Toast.makeText(this, "Rellene el campo de fecha para continuar", Toast.LENGTH_SHORT).show();
        } else {
            try {
                OutputStreamWriter file = new OutputStreamWriter(openFileOutput(fileName, Activity.MODE_PRIVATE));
                file.write(editTextNote.getText().toString());
                file.flush();
                file.close();
                Toast.makeText(this, "Los datos fueron grabados", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void searchFile(View view) {
        String fileName = editTextDate.getText().toString();
        String noteTitle = fileName;
        fileName = fileName.replace('/', '-');
        boolean foud = false;
        String[] files = fileList();
        if(fileName.trim().equals("")){
            Toast.makeText(this, "Rellene el campo de fecha para continuar", Toast.LENGTH_SHORT).show();
        } else {
            for (int f = 0; f < files.length; f++) {
                if (fileName.equals(files[f])) {
                    foud = true;
                }
            }
            if (foud) {
                try {
                    InputStreamReader file = new InputStreamReader(openFileInput(fileName));
                    BufferedReader br = new BufferedReader(file);
                    String line = br.readLine();
                    String all = "";
                    while (line != null) {
                        all = all + line + "\n";
                        line = br.readLine();
                    }
                    br.close();
                    file.close();
                    editTextNote.setText(all);
                    noteDate.setText(noteTitle);
                } catch (IOException e) {
                    Toast.makeText(this, "No se pudieron recuperar los datos", Toast.LENGTH_LONG).show();
                    noteDate.setText("");
                }
            } else {
                Toast.makeText(this, "No hay datos grabados para dicha fecha", Toast.LENGTH_LONG).show();
                editTextNote.setText("");
                noteDate.setText("");
            }
        }
    }

    public void cambiarTema(View view) {
        // Obtén la preferencia actual del tema de la aplicación
        int temaActual = getSharedPreferences("MiApp", MODE_PRIVATE).getInt("tema", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        // Calcula el nuevo tema de la aplicación
        int nuevoTema = temaActual == AppCompatDelegate.MODE_NIGHT_YES ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES;

        // Guarda la preferencia del nuevo tema de la aplicación
        getSharedPreferences("MiApp", MODE_PRIVATE).edit().putInt("tema", nuevoTema).apply();

        // Establece el nuevo tema de la aplicación
        AppCompatDelegate.setDefaultNightMode(nuevoTema);

        // Recrea la actividad con el nuevo tema
        recreate();
    }
}