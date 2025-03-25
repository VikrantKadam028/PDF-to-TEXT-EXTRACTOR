package com.example.pdftextextractor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final int PDF_PICK_CODE = 1000;
    private Button btnSelectPdf, btnExtract;
    private TextView tvExtractedText;
    private MaterialToolbar topAppBar;
    private Uri selectedPdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        btnSelectPdf = findViewById(R.id.btnSelectPdf);
        btnExtract = findViewById(R.id.btnExtract);
        tvExtractedText = findViewById(R.id.tvExtractedText);
        topAppBar = findViewById(R.id.topAppBar);
    }

    private void setupListeners() {
        btnSelectPdf.setOnClickListener(v -> openPdfPicker());

        btnExtract.setOnClickListener(v -> {
            if (selectedPdfUri != null) {
                extractTextFromPdf();
            } else {
                Toast.makeText(this, "Please select a PDF first", Toast.LENGTH_SHORT).show();
            }
        });

        topAppBar.setOnMenuItemClickListener(item -> {
            // Optional: Add menu items like save, share, etc.
            return true;
        });
    }

    private void openPdfPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_PICK_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedPdfUri = data.getData();
                btnSelectPdf.setText("PDF Selected");
            }
        }
    }

    private void extractTextFromPdf() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedPdfUri);
            PdfReader reader = new PdfReader(inputStream);
            StringBuilder extractedText = new StringBuilder();

            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                extractedText.append("--- Page ").append(i).append(" ---\n");
                extractedText.append(PdfTextExtractor.getTextFromPage(reader, i)).append("\n\n");
            }

            tvExtractedText.setText(extractedText.toString());
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error extracting text", Toast.LENGTH_SHORT).show();
        }
    }
}