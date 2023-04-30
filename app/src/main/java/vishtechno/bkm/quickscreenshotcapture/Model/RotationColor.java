package vishtechno.bkm.quickscreenshotcapture.Model;

import android.view.View;

public class RotationColor {
    View view;
    int rotationX;
    int rotationY;
    int rotationZ;
    int color;
    String thing;

    public RotationColor(View view, int rotationX, int rotationY, int rotationZ, int color, String thing) {
        this.view = view;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.color = color;
        this.thing = thing;
    }

    public String getThing() {
        return thing;
    }

    public void setThing(String thing) {
        this.thing = thing;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getRotationX() {
        return rotationX;
    }

    public void setRotationX(int rotationX) {
        this.rotationX = rotationX;
    }

    public int getRotationY() {
        return rotationY;
    }

    public void setRotationY(int rotationY) {
        this.rotationY = rotationY;
    }

    public int getRotationZ() {
        return rotationZ;
    }

    public void setRotationZ(int rotationZ) {
        this.rotationZ = rotationZ;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}