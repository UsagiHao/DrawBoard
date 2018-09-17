import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

public class DrawBoard extends JFrame {
//    private JButton funtions;
  //  private String functionNames[] = {"new", "save", "load", "draw"};
 //   JToolBar buttonPanel;
    private JLabel statusBar;  //显示鼠标状态的提示条
    private JLabel identifyLabel = new JLabel("形状");
    private DrawPanel drawPanel;
    private int width = 800;
    private int height = 550;
    drawings[] itemList = new drawings[5000]; //用来存放基本图形的数组
    int index = 0;
    private Color color = Color.black;
    int R = color.getRed();
    int G = color.getGreen();
    int B = color.getBlue();
    int count = 0;
    public DrawBoard(){
        super("画板");
        JMenuBar toolBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        JMenu functionMenu = new JMenu("功能");
        JMenu helpMenu = new JMenu("帮助");
        //fileMenu.setMnemonic('F');//设置快捷键

        //新建
        JMenuItem newItem= new JMenuItem("新建");
        newItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //   newFile();
                    }
                }
        );
        fileMenu.add(newItem);

        //保存
        JMenuItem saveItem= new JMenuItem("保存");
        saveItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //    saveFile();
                    }
                }
        );
        fileMenu.add(saveItem);

        //加载
        JMenuItem loadItem= new JMenuItem("加载");
        loadItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // loadFile();
                    }
                }
        );
        fileMenu.add(loadItem);

        //退出
        JMenuItem exitItem= new JMenuItem("退出");
        exitItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                }
        );
        fileMenu.add(exitItem);


        //绘制
        JMenuItem drawItem= new JMenuItem("绘图");
        drawItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //draw();
                    }
                }
        );
        functionMenu.add(drawItem);

        //识别
        JMenuItem identifyItem= new JMenuItem("识别");
        identifyItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (count == 1){
                            identifyLabel.setText("圆形");
                        }else if (count == 2){
                            identifyLabel.setText("三角形");
                        }else {
                            identifyLabel.setText("笔画过多");
                        }
                    }
                }
        );
        functionMenu.add(identifyItem);

        //识别全部
        JMenuItem identifyAllItem= new JMenuItem("识别全部");
        identifyAllItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //identifyAll();
                    }
                }
        );
        functionMenu.add(identifyAllItem);

        //帮助
        JMenuItem helpItem= new JMenuItem("说明");
        helpItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //identifyAll();
                    }
                }
        );
        helpMenu.add(helpItem);

        toolBar.add(fileMenu);
        toolBar.add(functionMenu);
        toolBar.add(helpMenu);

        drawPanel = new DrawPanel();

        statusBar = new JLabel("mouse status will be showed here");

        Container container = getContentPane();
        container.add(toolBar, BorderLayout.NORTH);
        container.add(drawPanel, BorderLayout.CENTER);
        container.add(statusBar, BorderLayout.SOUTH);
        container.add(identifyLabel, BorderLayout.EAST);

        createNewItem();
        setSize(width, height);
        setVisible(true);
    }

    class DrawPanel extends JPanel{
        public DrawPanel(){
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            setBackground(Color.white);
            addMouseListener(new mouseA());//鼠标点击时
            addMouseMotionListener(new mouseB());//鼠标移动时
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;    //定义画笔
            int j = 0;
            while (j <= index) {
                draw(g2d, itemList[j]);
                j++;
            }
        }
        void draw(Graphics2D g2d, drawings i) {
            i.draw(g2d);//将画笔传入到各个子类中，用来完成各自的绘图
        }
    }

    class mouseA extends MouseAdapter{
        public void mousePressed(MouseEvent e){
            statusBar.setText("鼠标停留在当前位置:[" + e.getX() +
                    ", " + e.getY() + "]");//设置状态提示

            itemList[index].x1 = itemList[index].x2 = e.getX();
            itemList[index].y1 = itemList[index].y2 = e.getY();
            index++;
            createNewItem();
        }

        public void mouseReleased(MouseEvent e) {
            statusBar.setText("     Mouse Released @:[" + e.getX() +
                    ", " + e.getY() + "]");
            itemList[index].x1 = itemList[index].x2 = e.getX();
            itemList[index].y1 = itemList[index].y2 = e.getY();
            repaint();
            index++;
            count++;
            createNewItem();
        }
        public void mouseEntered(MouseEvent e) {
            statusBar.setText("     Mouse Entered @:[" + e.getX() +
                    ", " + e.getY() + "]");
        }
        public void mouseExited(MouseEvent e) {
            statusBar.setText("     Mouse Exited @:[" + e.getX() +
                    ", " + e.getY() + "]");
        }
    }

    class mouseB extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
            statusBar.setText("     Mouse Dragged @:[" + e.getX() +
                    ", " + e.getY() + "]");
            itemList[index - 1].x1 = itemList[index].x2 = itemList[index].x1 = e.getX();
            itemList[index - 1].y1 = itemList[index].y2 = itemList[index].y1 = e.getY();
            index++;
            createNewItem();
            repaint();
        }
        public void mouseMoved(MouseEvent e) {
            statusBar.setText("     Mouse Moved @:[" + e.getX() +
                    ", " + e.getY() + "]");
        }
    }

    void createNewItem(){
        drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        itemList[index] = new drawings();
        itemList[index].R = R;
        itemList[index].G = G;
        itemList[index].B = B;
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }//将界面设置为当前windows风格

        DrawBoard drawBoard = new DrawBoard();
    }
}

class drawings implements Serializable//父类，基本图形单元，用到串行化接口，保存时所用
{
    int x1, y1, x2, y2; //定义坐标属性
    int R, G, B;        //定义色彩属性

    void draw(Graphics2D g2d) {
        g2d.drawLine(x1, y1, x2, y2);
    }
    ;//定义绘图函数
}
