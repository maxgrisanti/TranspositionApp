package com.example.transposescan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class QuickTransposerFragment extends AppCompatActivity {


    private Spinner spinnerNote;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private Button buttonTranspose;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quicktransposeinput);
        System.out.println("monkey");

        spinnerNote = findViewById(R.id.spinnerNote);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        buttonTranspose = findViewById(R.id.buttonTranspose);
        textViewResult = findViewById(R.id.textViewResult);

        // Populate the spinners with some dummy data

        // Set click listener for the button
        buttonTranspose.setOnClickListener(this::transposeN);

        Button switchBackButton = findViewById(R.id.switchBackButton);

    }

    public void transposeN(View v) {
        System.out.println("transposedNote");
        // Get the selected values from spinners
        String fromKey = spinnerFrom.getSelectedItem().toString();
        String toKey = spinnerTo.getSelectedItem().toString();
        String note = spinnerNote.getSelectedItem().toString();

        // Create an instance of com.example.transposescan.Transpose class with selected keys
        Transpose transpose = new Transpose(fromKey, toKey);

        // com.example.transposescan.Transpose the note
        String transposedNote = transpose.transposeNote(note);
        System.out.println(transposedNote);
        // Display the transposed note
        textViewResult.setText("Transposed Note: " + transposedNote);
        System.out.println(transposedNote);
    }
}
