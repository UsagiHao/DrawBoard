import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import Graphics.*;

public class DrawBoard extends JFrame {
    private JLabel graphicLabel = new JLabel("图形的形状将会显示在此处："); //显示图形形状
    private DrawPanel drawPanel;
    private Graphic[] itemList = new Graphic[5000]; //用来存放基本图形的数组
    private int index = 0;
    private Color color = Color.black;
    private int R = color.getRed();
    private int G = color.getGreen();
    private int B = color.getBlue();
    private int count = 0;
    private String graphics = "";
    private static final int width = 800;
    private static final int height = 600;

    public DrawBoard(){
        super("画板");
        JMenuBar toolBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        JMenu functionMenu = new JMenu("功能");
        JMenu helpMenu = new JMenu("帮助");

        //新建
        JMenuItem newItem= new JMenuItem("新建");
        newItem.addActionListener(e -> { color = Color.black; R = color.getRed(); G = color.getGreen(); B = color.getBlue(); clear(); });
        fileMenu.add(newItem);

        //保存
        JMenuItem saveItem= new JMenuItem("保存");
        saveItem.addActionListener(e -> saveFile());
        fileMenu.add(saveItem);

        //加载
        JMenuItem loadItem= new JMenuItem("加载");
        loadItem.addActionListener(e -> loadFile());
        fileMenu.add(loadItem);

        //退出
        JMenuItem exitItem= new JMenuItem("退出");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        //识别
        JMenuItem identifyItem= new JMenuItem("识别");
        identifyItem.addActionListener(e -> identify());
        functionMenu.add(identifyItem);

        //清屏
        JMenuItem clearItem= new JMenuItem("清屏");
        clearItem.addActionListener(e -> clear());
        functionMenu.add(clearItem);

        //选择颜色
        JMenuItem colorItem = new JMenuItem("颜色");
        colorItem.addActionListener(e -> chooseColor());
        functionMenu.add(colorItem);

        //帮助
        JMenuItem helpItem= new JMenuItem("说明");
        helpItem.addActionListener(e -> JOptionPane.showMessageDialog(null, "一块画板可以进行多次画图，但请逐一识别", " 画图板程序说明 ", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(helpItem);

        toolBar.add(fileMenu);
        toolBar.add(functionMenu);
        toolBar.add(helpMenu);

        drawPanel = new DrawPanel();

        Container container = getContentPane();
        container.add(toolBar, BorderLayout.NORTH);
        container.add(drawPanel, BorderLayout.CENTER);
        container.add(graphicLabel, BorderLayout.SOUTH);

        createNewItem();
    }

    private void clear(){
        index = 0;
        graphics = "";
        createNewItem();
        graphicLabel.setText("图形的形状将会显示在此处：");
        repaint();
    }

    private void saveFile() {
        ObjectOutputStream output;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File fileName = fileChooser.getSelectedFile();
        if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(fileChooser, "Invalid File Name",
                    "Invalid File Name", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(fileName);
                output = new ObjectOutputStream(fos);
                output.writeInt(index);
                output.writeUTF(graphicLabel.getText());
                for (int i = 0; i < index; i++) {
                    Graphic p = itemList[i];
                    output.writeObject(p);
                    output.flush();    //将所有图形信息强制转换成父类线性化存储到文件中
                }
                output.close();
                fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void loadFile() {
        ObjectInputStream input;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File fileName = fileChooser.getSelectedFile();
        if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(fileChooser, "Invalid File Name",
                    "Invalid File Name", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                FileInputStream fis = new FileInputStream(fileName);
                input = new ObjectInputStream(fis);
                Graphic inputRecord;
                int countNumber = input.readInt();
                graphics = input.readUTF();
                graphicLabel.setText(graphics);
                for (index = 0; index < countNumber; index++) {
                    inputRecord = (Graphic) input.readObject();
                    itemList[index] = inputRecord;
                }
                createNewItem();
                input.close();
                repaint();
            } catch (EOFException endofFileException) {
                JOptionPane.showMessageDialog(this, "no more record in file",
                        "class not found", JOptionPane.ERROR_MESSAGE);
            } catch (ClassNotFoundException classNotFoundException) {
                JOptionPane.showMessageDialog(this, "Unable to Create Object",
                        "end of file", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(this, "error during read from file",
                        "read Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void identify() {
        switch (count){
            case 1:{
                itemList[index] = new Circles(count);
                count = 0;
                graphicLabel.setText(graphics += itemList[index].getShape());
                break;
            }
            case 2:{
                itemList[index] = new Triangles(count);
                count = 0;
                graphicLabel.setText(graphics += itemList[index].getShape());
                break;
            }
            case 3:{
                itemList[index] = new Squares(count);
                count = 0;
                graphicLabel.setText(graphics += itemList[index].getShape());
                break;
            }
            case 4:{
                itemList[index] = new Rectangles(count);
                count = 0;
                graphicLabel.setText(graphics += itemList[index].getShape());
                break;
            }
            case 0:{
                JOptionPane.showMessageDialog(null,
                        "未检测到新的图形",
                        "ERROR",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            }
            default:{
                count = 0;
                JOptionPane.showMessageDialog(null,
                        "笔画过多",
                        "ERROR",
                        JOptionPane.INFORMATION_MESSAGE);                            }
        }
    }

    private void chooseColor() {
        color = JColorChooser.showDialog(DrawBoard.this,
                "Choose a color", color);
        R = color.getRed();
        G = color.getGreen();
        B = color.getBlue();
        itemList[index].setColor(R, G, B);
    }

    private void createNewItem(){
        drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        itemList[index] = new Graphic();
        itemList[index].setColor(R, G, B);
    }

    class DrawPanel extends JPanel{
        DrawPanel(){
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
        void draw(Graphics2D g2d, Graphic i) {
            i.draw(g2d);//将画笔传入到各个子类中，用来完成各自的绘图
        }
    }

    class mouseA extends MouseAdapter{
        public void mousePressed(MouseEvent e){
            itemList[index].x1 = itemList[index].x2 = e.getX();
            itemList[index].y1 = itemList[index].y2 = e.getY();
            index++;
            createNewItem();
        }

        public void mouseReleased(MouseEvent e) {
            itemList[index].x1 = itemList[index].x2 = e.getX();
            itemList[index].y1 = itemList[index].y2 = e.getY();
            repaint();
            index++;
            count++;
            createNewItem();
        }
    }

    class mouseB extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
            itemList[index - 1].x1 = itemList[index].x2 = itemList[index].x1 = e.getX();
            itemList[index - 1].y1 = itemList[index].y2 = itemList[index].y1 = e.getY();
            index++;
            createNewItem();
            repaint();
        }
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        DrawBoard drawBoard = new DrawBoard();
        drawBoard.setSize(width, height);
        drawBoard.setLocationRelativeTo(null);
        drawBoard.setVisible(true);
    }
}


