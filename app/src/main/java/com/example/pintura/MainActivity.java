package com.example.pintura;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.savedstate.SavedStateRegistryOwner;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;

import com.example.pintura.DrawingView.DrawingView;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

import java.io.OutputStream;

import petrov.kristiyan.colorpicker.ColorPicker;

public class MainActivity extends AppCompatActivity {

    // creating the object of type DrawView in order to get the reference of the View
    private DrawingView paint;

    // creating objects of type button
    private ImageButton save, color, stroke, undo;

    // creating a RangeSlider object, which will help in selecting the width of the Stroke
    private RangeSlider rangeSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // getting the reference of the views from their ids
        paint = findViewById(R.id.drawing_view);
        rangeSlider = findViewById(R.id.slider);
        undo = findViewById(R.id.btn_undo);
        save = findViewById(R.id.btn_save);
        color = findViewById(R.id.btn_color);
        stroke = findViewById(R.id.btn_brush);

        // the undo button will remove the lhe last brush stroke from the canvas
        undo.setOnClickListener(view -> paint.undo());

        // the save button saves the current canvas in form of PNG, in the storage
        save.setOnClickListener(view -> {

            // getting the bitmap from DrawView class
            Bitmap bmp = paint.save();

            // opening a OutputStream to write into the file
            OutputStream imageOutStream = null;

            ContentValues cv = new ContentValues();

            // name of the file
            cv.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing.png");

            // type of the file
            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

            // location of the file to be saved
            cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            // get the Uri of the file which is to be created in the storage
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            try {
                // open the output stream with the above uri
                imageOutStream = getContentResolver().openOutputStream(uri);

                // this method writes the files in storage
                bmp.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);

                // close the output stream after use
                imageOutStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // the color button will allow the user to select the color of his brush
        color.setOnClickListener(view -> {
            final ColorPicker colorPicker = new ColorPicker(MainActivity.this);
            colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                @Override
                public void setOnFastChooseColorListener(int position, int color) {
                    // get the integer value of color
                    // selected from the dialog box and
                    // set it as the stroke color
                    paint.setColor(color);
                }
                @Override
                public void onCancel() {
                    colorPicker.dismissDialog();
                }
            })
                    // set the number of color columns
                    // you want  to show in dialog.
                    .setColumns(5)
                    // set a default color selected
                    // in the dialog
                    .setDefaultColorButton(Color.parseColor("#000000"))
                    .show();
        });
        // the button will toggle the visibility of the RangeBar/RangeSlider
        stroke.setOnClickListener(view -> {
            if (rangeSlider.getVisibility() == View.VISIBLE)
                rangeSlider.setVisibility(View.GONE);
            else
                rangeSlider.setVisibility(View.VISIBLE);
        });

        // set the range of the RangeSlider
        rangeSlider.setValueFrom(0.0f);
        rangeSlider.setValueTo(100.0f);

        // adding a OnChangeListener which will listen fr the width change of the brush
        //once the user slides the slider
        rangeSlider.addOnChangeListener((slider, value, fromUser) -> paint.setBrushWidth((int) value));

        // pass the height and width of the custom view
        // to the init method of the DrawView object
        ViewTreeObserver vto = paint.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                paint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = paint.getMeasuredWidth();
                int height = paint.getMeasuredHeight();
                paint.init(height, width);
            }
        });
    }


}