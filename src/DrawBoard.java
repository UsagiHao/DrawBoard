import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import Graphics.*;
public class DrawBoard extends JFrame {
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private JLabel graphicLabel; //显示图形形状
    private DrawPanel drawPanel;
    private int width = 800;
    private int height = 550;
    Graphic[] itemList = new Graphic[5000]; //用来存放基本图形的数组
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

        //新建
        JMenuItem newItem= new JMenuItem("新建");
        newItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        color = Color.black;
                        R = color.getRed();
                        G = color.getGreen();
                        B = color.getBlue();
                        clear();
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
                        saveFile();
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
                        loadFile();
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

        //识别
        JMenuItem identifyItem= new JMenuItem("识别");
        identifyItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Graphic graphic;
                        switch (count){
                            case 1:{
                                graphic = new Circles(count);
                                count = 0;
                                graphicLabel.setText(graphic.getShape());
                                break;
                            }
                            case 2:{
                                graphic = new Triangles(count);
                                count = 0;
                                graphicLabel.setText(graphic.getShape());
                                break;
                            }
                            case 3:{
                                graphic = new Squares(count);
                                count = 0;
                                graphicLabel.setText(graphic.getShape());
                                break;
                            }
                            case 4:{
                                graphic = new Rectangles(count);
                                count = 0;
                                graphicLabel.setText(graphic.getShape());
                                break;
                            }
                            case 0:{
                                graphicLabel.setText("未检测到");
                                break;
                            }
                            default:{
                                count = 0;
                                graphicLabel.setText("笔画过多");
                            }
                        }
                    }
                }
        );
        functionMenu.add(identifyItem);

        //清屏
        JMenuItem clearItem= new JMenuItem("清屏");
        clearItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        clear();
                    }
                }
        );
        functionMenu.add(clearItem);

        //选择颜色
        JMenuItem colorItem = new JMenuItem("颜色");
        colorItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        chooseColor();
                    }
                });
        functionMenu.add(colorItem);

        //帮助
        JMenuItem helpItem= new JMenuItem("说明");
        helpItem.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //help();
                        JOptionPane.showMessageDialog(null,
                                "一块画板可以进行多次画图，但请逐一识别",
                                " 画图板程序说明 ",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
        );
        helpMenu.add(helpItem);

        toolBar.add(fileMenu);
        toolBar.add(functionMenu);
        toolBar.add(helpMenu);

        drawPanel = new DrawPanel();

        graphicLabel = new JLabel("图形的形状将会显示在此处：");

        Container container = getContentPane();
        container.add(toolBar, BorderLayout.NORTH);
        container.add(drawPanel, BorderLayout.CENTER);
        container.add(graphicLabel, BorderLayout.SOUTH);

        createNewItem();
        setSize(width, height);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void clear(){
        index = 0;
        createNewItem();
        graphicLabel.setText("图形的形状将会显示在此处：");
        repaint();//将有关值设置为初始状态，并且重画
    }

    public void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File fileName = fileChooser.getSelectedFile();
        fileName.canWrite();
        if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(fileChooser, "Invalid File Name",
                    "Invalid File Name", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                fileName.delete();
                FileOutputStream fos = new FileOutputStream(fileName);
                output = new ObjectOutputStream(fos);
                Graphic record;
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

    public void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File fileName = fileChooser.getSelectedFile();
        fileName.canRead();
        if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(fileChooser, "Invalid File Name",
                    "Invalid File Name", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                FileInputStream fis = new FileInputStream(fileName);
                input = new ObjectInputStream(fis);
                Graphic inputRecord;
                int countNumber = 0;
                countNumber = input.readInt();
                graphicLabel.setText(input.readUTF());
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

    public void chooseColor() {
        color = JColorChooser.showDialog(DrawBoard.this,
                "Choose a color", color);
        R = color.getRed();
        G = color.getGreen();
        B = color.getBlue();
        itemList[index].setColor(R, G, B);
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

    void createNewItem(){
        drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        itemList[index] = new Graphic();
        itemList[index].setColor(R, G, B);
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        DrawBoard drawBoard = new DrawBoard();
    }
}


