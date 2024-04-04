package com.lazycoder.cakevpn.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lazycoder.cakevpn.R;
import com.lazycoder.cakevpn.pdfClass;

/**
 * Класс PdfViewer представляет активность для просмотра и загрузки PDF-файлов.
 */
public class PdfViewer extends AppCompatActivity {

    Button openPdf;
    EditText filename;
    String filenameee;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        openPdf = findViewById(R.id.open_pdf);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");

        openPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFiles();
            }
        });
    }

    /**
     * Метод для выбора файлов PDF.
     */
    private void selectFiles() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose files..."), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            UploadFiles(data.getData());
            filenameee = data.toString();
        }
    }

    /**
     * Метод для загрузки выбранных файлов в хранилище Firebase.
     *
     * @param data URI выбранных файлов
     */
    private void UploadFiles(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Opening...");
        progressDialog.show();

        StorageReference reference = storageReference.child("Uploads/" + System.currentTimeMillis() + ".pdf");

        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while (!uriTask.isComplete()) ;
                Uri url = uriTask.getResult();
                pdfClass pdfClass = new pdfClass(filenameee, url.toString());
                databaseReference.child(databaseReference.push().getKey()).setValue(pdfClass);

                Toast.makeText(PdfViewer.this, "Opened!", Toast.LENGTH_SHORT).show();
                String pdfurl = url.toString();
                Intent i = new Intent(PdfViewer.this, PdfWebView.class);
                i.putExtra("PdfURL", pdfurl);
                startActivity(i);
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage("Opened " + (int) progress + "%");
            }
        });
    }
}
