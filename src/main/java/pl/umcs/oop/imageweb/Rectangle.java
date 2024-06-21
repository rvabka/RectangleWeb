package pl.umcs.oop.imageweb;

public class Rectangle {
    private int x;
    private int y;
    private int width;
    private int height;
    private String color;

    public Rectangle(int x, int y, int width, int height, String color) {
        setColor(color);
        setY(y);
        setX(x);
        setWidth(width);
        setHeight(height);
    }
    public Rectangle(Rectangle rectangle) {
        setColor(rectangle.getColor());
        setY(rectangle.getY());
        setX(rectangle.getX());
        setWidth(rectangle.getWidth());
        setHeight(rectangle.getHeight());
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
