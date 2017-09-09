package pl.edu.uj.laciak.gesturedetector.utility;

import android.graphics.Bitmap;

/**
 * Created by darek on 02.09.17.
 */

public class DrawingItem {

    int id;
    String title;
    Bitmap image;
    String operation;

    // Empty Constructor
    public DrawingItem() {

    }

    // Constructor
    public DrawingItem(int id, String title, Bitmap image, String operation) {
        super();
        this.id = id;
        this.title = title;
        this.image = image;
        this.operation = operation;
    }

    // Getter and Setter Method
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
