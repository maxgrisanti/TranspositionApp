package com.example.transposescan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.transposescan.MainActivity;
import com.example.transposescan.R;
import com.example.transposescan.Transpose;

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

        // Initialize views
        spinnerNote = findViewById(R.id.spinnerNote);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        buttonTranspose = findViewById(R.id.buttonTranspose);
        textViewResult = findViewById(R.id.textViewResult);

        // Set click listener for the transpose button
        buttonTranspose.setOnClickListener(this::transposeN);

        // Find the switch back button
        Button switchBackButton = findViewById(R.id.switchBackButton);

        // Set click listener for the switchBackButton
        switchBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                Intent intent = new Intent(QuickTransposerFragment.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to handle transpose button click
    public void transposeN(View v) {
        System.out.println("transposedNote");

        // Get the selected values from spinners
        String fromKey = spinnerFrom.getSelectedItem().toString();
        String toKey = spinnerTo.getSelectedItem().toString();
        String note = spinnerNote.getSelectedItem().toString();

        // Create an instance of Transpose class with selected keys
        Transpose transpose = new Transpose(fromKey, toKey);

        // Transpose the note
        String transposedNote = transpose.transposeNote(note);
        System.out.println(transposedNote);

        // Display the transposed note
        textViewResult.setText("Transposed Note: " + transposedNote);
        System.out.println(transposedNote);
    }
}
