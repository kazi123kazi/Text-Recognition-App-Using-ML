package com.example.textrecognisationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.text.FirebaseVisionCloudDocumentTextDetector;
import com.google.firebase.ml.vision.cloud.text.FirebaseVisionCloudText;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button detect,capture;
    private  ImageView imageView;
    private TextView textView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private  Bitmap imageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detect=(Button)findViewById(R.id.detect_image_Text);
        capture=(Button)findViewById(R.id.capture_image_text);
        imageView=(ImageView)findViewById(R.id.imageView);
        textView=(TextView) findViewById(R.id.textView);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                textView.setText("");

            }
        });
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetectTextFromImage();

            }
        });


    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            
            Bitmap imageBitmap = (Bitmap) (extras != null ? extras.get("data") : null);
            imageView.setImageBitmap(imageBitmap);
        }
    }


    private void DetectTextFromImage()
    {
        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector=FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                    displayText(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this , "Error : "+e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void displayText(FirebaseVisionText firebaseVisionText)
    {

        List<FirebaseVisionText.Block> blocks = firebaseVisionText.getBlocks();
        if (blocks.size() == 0) {
            Log.d("TAG", "No text found");
            return;
        }
        else
        {
            for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks())
            {
                String text=block.getText();
                textView.setText(text);

            }
        }
    }


}
