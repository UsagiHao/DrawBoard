package Graphics;

import java.awt.*;
import java.io.Serializable;

public class Graphic implements Serializable {
    public int x1, y1, x2, y2; //定义坐标属性
    int R, G, B;        //定义色彩属性
    private int drawNums;
    private String shape;
    public Graphic(int num){

    }

    public void setColor(int r, int g, int b){
        R = r;
        G = g;
        B = b;
    }

    public Graphic() {
    }

    public String getShape(){
        return shape;
    }

    public void draw(Graphics2D g2d) {
        g2d.setPaint(new Color(R, G, B));
        g2d.drawLine(x1, y1, x2, y2);
    }
}
