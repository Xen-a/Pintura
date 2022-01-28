package com.example.pintura.Stroke;

import android.graphics.Path;

public class Brush {

    //Brush color
    public int color;

    //brush width
    public int brushWidth;

    //Path Object for path drawn
    public Path path;

    // Constructor to initialize attributes
    public Brush(int color, int brushWidth, Path path) {
        this.color = color;
        this.brushWidth = brushWidth;
        this.path = path;
    }


}
