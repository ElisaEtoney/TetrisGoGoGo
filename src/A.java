import sun.security.mscapi.CPublicKey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class A extends JFrame implements KeyListener {
    private static final int game_x = 22;
    private static final int game_y = 12;
    JTextArea[][] text;
    int[][] table;
    JLabel label1;
    JLabel label2;
    int score = 0;
    int time;


    int[] completeLine;
    int lineCounter;
    Random rand = new Random();
    int color;
    //暂留int[][] shape
    int[][] shape;
    //成就
    int[] achievement;
    int rectNumber = 0;//已获得方块数量
    boolean[] atEnd = new boolean[10];
    int rectType;//定位方块类型
    int typeNumber;
    ArrayList<int[][]> rl = new ArrayList<>();
    ArrayList<int[][]> ll = new ArrayList<>();
    ArrayList<int[][]> t = new ArrayList<>();
    ArrayList<int[][]> z1 = new ArrayList<>();
    ArrayList<int[][]> z2 = new ArrayList<>();
    ArrayList<int[][]> i = new ArrayList<>();
    ArrayList<int[][]> squ = new ArrayList<>();
    ArrayList<int[][]> help = new ArrayList<>();


    public void initWindow() {
        this.setSize(400, 600);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("Tetris Go~");
    }

    public void initGamePanel() {
        JPanel gameMain = new JPanel();
        gameMain.setLayout(new GridLayout(game_x, game_y, 1, 1));

        for (int i = 0; i < text.length; i++) {
            for (int j = 0; j < text[i].length; j++) {
                text[i][j] = new JTextArea(game_x, game_y);
                text[i][j].setBackground(Color.WHITE);
                text[i][j].addKeyListener(this);
                if (j == 0 || j == text[i].length - 1 || i == text.length - 1) {
                    text[i][j].setBackground(Color.BLACK);
                    table[i][j] = 1;
                }

                text[i][j].setEditable(false);
                gameMain.add(text[i][j]);
            }
        }
        this.setLayout(new BorderLayout());
        this.add(gameMain, BorderLayout.CENTER);
    }

    //5
    public void initExplainPanel() {
        JPanel explain_le = new JPanel();
        JPanel explain_Ri = new JPanel();

        explain_le.setLayout(new GridLayout(4, 1));
        explain_Ri.setLayout(new GridLayout(2, 1));

        explain_le.add(new JLabel("    ←    "));
        explain_le.add(new JLabel("    →    "));
        explain_le.add(new JLabel("    ↓    "));
        explain_le.add(new JLabel("    ~    "));
        label1.setForeground(Color.BLACK);

        explain_Ri.add(label1);
        explain_Ri.add(label2);

        this.add(explain_le, BorderLayout.EAST);
        this.add(explain_Ri, BorderLayout.NORTH);
    }

    public A() {

        text = new JTextArea[game_x][game_y];
        table = new int[game_x][game_y];

        label1 = new JLabel("Good boy go~");
        label2 = new JLabel("Score: " + score);

        initGamePanel();
        initExplainPanel();
        initWindow();
    }

    public static void main(String[] args) throws InterruptedException {
        A tetris1 = new A();
        tetris1.gameRun();
    }


    //gameRun包含成就6
    public void gameRun() throws InterruptedException {
        ll();
        rl();
        t();
        z1();
        z2();
        i();
        squ();
        time = 10;
        completeLine = new int[table.length];
        score = 0;//分数
        shape = new int[4][4];//方块
        //成就数据
        achievement = new int[7];
        rectNumber = 0;

        System.out.println("Go!");
        for (int i = 0; i < table.length - 1; i++) {
            for (int j = 1; j < table[i].length - 1; j++) {
                table[i][j] = 0;
            }
        }

        KeyListener listener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                int code = event.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_S:
                        goDown();
                        break;
                    case KeyEvent.VK_A:
                        goLeft();
                        break;
                    case KeyEvent.VK_D:
                        goRight();
                        break;
                    case KeyEvent.VK_SPACE://旋转方块，未完成
                        rotate();
                        break;
                    default:
                        break;
                }
            }
        };
        this.addKeyListener(listener);
        this.requestFocus();
        //游戏开始，结束时退出循环
        while (true) {
            //方块产生并运动 触底即退出运动循环
            rectRun();
            //已获得方块计数
            rectNumber++;
            //System.out.println("2");

            //触底后，状态判断
            //record记录游戏区域状态 记录lineCounter
            record();
            System.out.println("recorded");
            //分数处理
            scoreProcess();
            System.out.println("scoreProcessed");
            //消除满足条件的行 内含Repaint() 已加入时停
            erasure();
            //下移 内含Repaint() 已加入时停
            moveDown();
            tableProcess();//将colorNum + 4，改为对应颜色值 (2、3、4、5 对应 6、7、8、9）
            if (GameOver()) break;
        }
        System.out.println("over");
        label1 = new JLabel("Good boy go~");
        if (rectNumber <= 6 && achievement[5] == 0) {
            achievement[5] = 1;
            //成就提醒
        }
    }


    //去除Repaint
    public void rectAppear() {
        //随机方块颜色

    }

    //含有Repaint()
    public void rectRun() throws InterruptedException {
        //方块产生
        color = 0;
        color = rand.nextInt(4) + 2;
        rectType = 0;
        typeNumber = 0;
        rectType = rand.nextInt(19);
        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 4; k++) {
                shape[j][k] = 0;
            }
        }
        getRect();//根据rand1和rand2 对shape赋值
        System.out.print(color + " " + rectType + " " + typeNumber + "   ");

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (shape[i][j] == 1) {
                    shape[i][j] = color;//将初始色改为color编号；
                    table[i][j + 4] = color;
                }                      //以上完成方块种类以及颜色的随机生成
            }
        }
        for (int[] i : shape) {
            for (int j : i) {
                System.out.printf(j + " ");
            }
        }
        System.out.printf("\n");

        Repaint();
        Thread.sleep(90);




        while (true) {//方块移动 触底时退出
            if (!atTheEnd() && fault()) {
                fall();
                //时间间隔
                Repaint();
                Thread.sleep(500);
                //自动下移
            } else break;
//ghhjk

        }
    }

    public boolean fault() {
        for (int j = 0; j < table.length; j++) {
            for (int k = 0; k < table[0].length; k++) {
                if (table[j][k] == color) return true;
            }
        }
        return false;
    }

    public void fall() {
        for (int i = table.length - 2; i >= 0; i--) {//从下往上
            for (int j = 1; j < table[0].length - 1; j++) {
                if (table[i][j] == color && i + 1 < table.length - 1) {
                    table[i + 1][j] = table[i][j];
                    table[i][j] = 0;
                }
            }
        }
    }

    public void record() {
        int count;
        for (int i = table.length - 2; i >= 4; i--) {//从下至上，记录满足条件的整行方块
            //对单行方块  计数
            count = 0;//初始化count值，对单行方块计数
            for (int j = 1; j < table[0].length - 1; j++) {//检查每一行
                if (table[i][j] != 0) count++;
                if (table[i][j] == 0) break;
            }
            if (count == 10) {//检验是否满足消除条件
                completeLine[i] = 1;//储存满足条件的行
                lineCounter++;//计数
            }
        }
    }

    //含有Repaint()
    public void erasure() throws InterruptedException {//暂不改 可以简化
        for (int i : completeLine) {
            if (i != 0) {
                for (int j = 1; j < table[i].length - 1; j++) table[i][j] = 0;
            }
        }
        //RePaint
        Repaint();
        Thread.sleep(time);
    }

    //已优化，保证没有残影   含有Repaint()、时停sleep
    public void moveDown() throws InterruptedException {
        lineCounter = 0;
        for (int i : completeLine) lineCounter += i;
        while (lineCounter > 0) {//移动，一次一行
            int k;
            for (k = 19; k > 3; k--) {//前四行不进行判断
                if (completeLine[k] != 0) {
                    //此处改为0，标记已处理
                    completeLine[k] = 0;
                    break;
                }
            }
            for (int i = k; i >= 0; i--) {
                for (int j = 1; j < table[i].length - 1; j++) {
                    //同时将table数据和completeLine下移 同步两个数据
                    table[i][j] = table[i - 1][j];
                    completeLine[i] = completeLine[i - 1];
                }
            }
            for (int j = 1; j < table[0].length - 1; j++) {
                //清除首行，保证没有方块余留
                table[0][j] = 0;
                completeLine[0] = 0;
            }
            lineCounter--;
        }
        //Repaint
        Thread.sleep(time);
        Repaint();
    }

    //5.21 12:00 有修改
    //atTheEnd 待修改  等待确认方块产生机制
    public boolean atTheEnd() {
        boolean stop = false;
        //初始化记录值
        for (int k = 1; k < table[0].length - 2; k++) {
            if (table[table.length - 2][k] == color) {
                stop = true;//此时方块已到底
                break;
            }
        }
        if (!stop) {
            for (int j = 1; j < table[0].length - 1; j++) {//列号
                for (int i = table.length - 3; i >= 0; i--) {//行号
                    if (table[i][j] == color && table[i + 1][j] != 0 && table[i + 1][j] != color) {
                        stop = true;
                        break;
                    }
                }
            }
        }
        return stop;
    }

    //scoreProcess内包含成就7”一次性消除四行条件”,成就1、3、4、5（2000、4000、6000 须添加成就显示
    //可通过判别achievement内数据进行处理
    public void scoreProcess() {
        //当前score处理，加分

        if (lineCounter == 1) score += 40;
        if (lineCounter == 2) score += 100;
        if (lineCounter == 3) score += 500;
        if (lineCounter == 4) {
            score += 1000;
            if (achievement[6] == 0) achievement[6] = 1;
            //成就提醒
        }
        if (score >= 1000 && rectNumber <= 50 && achievement[0] == 0) {
            achievement[0] = 1;
            //成就提醒
        }
        if (score >= 2000 && achievement[2] == 0) {
            achievement[2] = 1;
            //成就提醒
        }
        if (score >= 4000 && achievement[3] == 0) {
            achievement[3] = 1;
            //成就提醒
        }
        if (score >= 2000 && achievement[4] == 0) {
            achievement[4] = 1;
            //成就提醒
        }
        //label2.setText("Score:" + score);
    }

    public void tableProcess() {
        for (int i = 0; i < table.length - 1; i++) {
            for (int j = 1; j < table[i].length - 1; j++) {
                if (table[i][j] < 6 && table[i][j] >= 2) table[i][j] = table[i][j] + 4;
            }
        }
        System.out.printf("Processed");
    }

        public void Repaint(){
        for (int i = 0; i < table.length-1; i++){
            for (int j = 1; j < table[i].length-1; j++){
                if(table[i][j] == 2 || table[i][j] == 6) text[i][j].setBackground(Color.RED);
                else if(table[i][j] == 3 || table[i][j] == 7) text[i][j].setBackground(Color.BLUE);
                else if(table[i][j] == 4 || table[i][j] == 8) text[i][j].setBackground(Color.GREEN);
                else if(table[i][j] == 5 || table[i][j] == 9) text[i][j].setBackground(Color.YELLOW);
                else {table[i][j] = 0;
                    text[i][j].setBackground(Color.WHITE);
                }


            }
        }
    }
//    public void Repaint() {
//        for (int i = 0; i < table.length - 1; i++) {
//            for (int j = 1; j < table[i].length - 1; j++) {
//                if (table[i][j] == 2) text[i][j].setBackground(Color.RED);
//                else if (table[i][j] == 3) text[i][j].setBackground(Color.BLUE);
//                else if (table[i][j] == 4) text[i][j].setBackground(Color.GREEN);
//                else if (table[i][j] == 5) text[i][j].setBackground(Color.YELLOW);
//                else if (table[i][j] > 5 & table[i][j] < 10) text[i][j].setBackground(Color.GRAY);
//                else {
//                    table[i][j] = 0;
//                    text[i][j].setBackground(Color.WHITE);
//                }
//
//
//            }
//        }
//    }

    public boolean GameOver() {
        boolean isOver = false;
        for (int j = 1; j < table[3].length - 1; j++) {
            if (table[3][j] != 0) {
                isOver = true;
                break;
            }
        }
        return isOver;
    }


    public void ll() {
        int[][] ll1 = new int[4][4];
        ll1[1][1] = 1;
        ll1[2][1] = 1;
        ll1[3][1] = 1;
        ll1[3][0] = 1;
        ll.add(ll1);

        int[][] ll2 = new int[4][4];
        ll2[1][0] = 1;
        ll2[2][0] = 1;
        ll2[2][1] = 1;
        ll2[2][2] = 1;
        ll.add(ll2);
        help.add(ll2);
        int[][] ll3 = new int[4][4];
        ll3[1][2] = 1;
        ll3[1][1] = 1;
        ll3[2][1] = 1;
        ll3[3][1] = 1;
        ll.add(ll3);
        help.add(ll3);
        int[][] ll4 = new int[4][4];
        ll4[2][0] = 1;
        ll4[2][1] = 1;
        ll4[2][2] = 1;
        ll4[3][2] = 1;
        ll.add(ll4);
        help.add(ll4);
    }

    public void rl() {
        int[][] l1 = new int[4][4];
        l1[1][1] = 1;
        l1[2][1] = 1;
        l1[3][1] = 1;
        l1[3][2] = 1;
        rl.add(l1);
        help.add(l1);
        int[][] l2 = new int[4][4];
        l2[3][0] = 1;
        l2[2][0] = 1;
        l2[2][1] = 1;
        l2[2][2] = 1;
        rl.add(l2);
        help.add(l2);
        int[][] l3 = new int[4][4];
        l3[1][0] = 1;
        l3[1][1] = 1;
        l3[2][1] = 1;
        l3[3][1] = 1;
        rl.add(l3);
        help.add(l3);
        int[][] l4 = new int[4][4];
        l4[2][0] = 1;
        l4[2][1] = 1;
        l4[2][2] = 1;
        l4[1][2] = 1;
        rl.add(l4);
        help.add(l4);
    }

    public void t() {
        int[][] t1 = new int[4][4];
        t1[1][1] = 1;
        t1[2][0] = 1;
        t1[2][1] = 1;
        t1[2][2] = 1;
        t.add(t1);
        int[][] t2 = new int[4][4];
        t2[1][1] = 1;
        t2[2][1] = 1;
        t2[3][1] = 1;
        t2[2][2] = 1;
        t.add(t2);
        int[][] t3 = new int[4][4];
        t3[2][0] = 1;
        t3[2][1] = 1;
        t3[2][2] = 1;
        t3[3][1] = 1;
        t.add(t3);
        int[][] t4 = new int[4][4];
        t4[1][1] = 1;
        t4[2][1] = 1;
        t4[3][1] = 1;
        t4[2][0] = 1;
        t.add(t4);
    }

    public void z1() {
        int[][] z11 = new int[4][4];
        z11[1][0] = 1;
        z11[2][0] = 1;
        z11[2][1] = 1;
        z11[3][1] = 1;
        z1.add(z11);
        int[][] z12 = new int[4][4];
        z12[1][1] = 1;
        z12[1][2] = 1;
        z12[2][0] = 1;
        z12[2][1] = 1;
        z1.add(z12);
    }

    public void z2() {
        int[][] z21 = new int[4][4];
        z21[1][2] = 1;
        z21[2][2] = 1;
        z21[2][1] = 1;
        z21[3][1] = 1;
        z2.add(z21);
        int[][] z22 = new int[4][4];
        z22[1][0] = 1;
        z22[1][1] = 1;
        z22[2][1] = 1;
        z22[2][2] = 1;
        z2.add(z22);
        help.add(z21);
        help.add(z22);

    }

    public void i() {
        int[][] i1 = new int[4][4];
        i1[0][1] = 1;
        i1[1][1] = 1;
        i1[2][1] = 1;
        i1[3][1] = 1;
        i.add(i1);
        int[][] i2 = new int[4][4];
        i2[2][0] = 1;
        i2[2][1] = 1;
        i2[2][2] = 1;
        i2[3][2] = 1;
        i.add(i2);
        help.add(i1);
        help.add(i2);
    }

    public void squ() {
        int[][] squ1 = new int[4][4];
        squ1[1][1] = 1;
        squ1[1][2] = 1;
        squ1[2][1] = 1;
        squ1[2][2] = 1;
        squ.add(squ1);
        help.add(squ1);
    }

    public void getRect() {
        if (rectType < 4) {
            typeNumber = rand.nextInt(4);
            shape = ll.get(typeNumber);
        } else if (rectType < 8) {
            typeNumber = rand.nextInt(4);
            shape = rl.get(typeNumber);
        } else if (rectType < 12) {
            typeNumber = rand.nextInt(4);
            shape = t.get(typeNumber);
        } else if (rectType < 14) {
            typeNumber = rand.nextInt(2);
            shape = z1.get(typeNumber);
        } else if (rectType < 16) {
            typeNumber = rand.nextInt(2);
            shape = z2.get(typeNumber);
        } else if (rectType < 18) {
            typeNumber = rand.nextInt(2);
            shape = i.get(typeNumber);
        } else {
            shape = squ.get(0);
        }
    }

    public boolean canRotate(int m, int n, int[][] tableBackup) {
        if (rectType < 4) {
            if (typeNumber == 1) m = m + 1;
            if (typeNumber == 2) n = n - 1;
            if (typeNumber == 3) n = n - 2;
        } else if (rectType < 8) {
            if (typeNumber == 0) n = n - 1;
            if (typeNumber == 2) m = m - 1;
            if (typeNumber == 3) m = m + 1;
        } else if (rectType < 12) {
            if (typeNumber == 0) m = m - 1;
            if (typeNumber == 1 || typeNumber == 2 || typeNumber == 3) n = n - 1;
        } else if (rectType < 14) {
            if (typeNumber == 0) n = n - 1;
            if (typeNumber == 1) m = m + 1;
        } else if (rectType < 16) {
            if (typeNumber == 0) n = n - 1;
            if (typeNumber == 1) {
                m = m + 1;
                n = n - 1;
            }
        } else if (rectType < 18) {
            if (typeNumber == 0) n = n - 1;
            if (typeNumber == 1) m = m - 1;
        }
        int countBackground = 0;
        int countRect = 0;
        for (int i = m; i < m + 4; i++) {
            for (int j = n; j < n + 4; j++) {
                if (i < tableBackup.length - 1 && i > 0 && j > 1 && j < tableBackup[0].length - 1) {
                    if (tableBackup[i][j] == color) {
                        countRect++;
                        tableBackup[i][j] = 0;//隐藏原有方块
                    }
                    if (tableBackup[i][j] != color && tableBackup[i][j] != 0) countBackground++;
                }
            }
        }
        for (int i = m; i < m + 4; i++) {//将旋转后的方块置入 复制的游戏区域
            for (int j = n; j < n + 4; j++) {
                if (rectType < 12) typeNumber = (typeNumber + 1) % 4;
                else if (rectType < 18) typeNumber = (typeNumber + 1) % 2;
                if (i < tableBackup.length - 1 && i > 0 && j > 1 && j < tableBackup[0].length - 1) {
                    table[i][j] = shape[i - m][j - n];
                }
            }
        }
        for (int i = m; i < m + 4; i++) {
            for (int j = n; j < n + 4; j++) {
                if (i < tableBackup.length - 1 && i > 0 && j > 1 && j < tableBackup[0].length - 1) {
                    if (tableBackup[i][j] == color) {
                        countRect--;
                    }
                    if (tableBackup[i][j] != color && tableBackup[i][j] != 0) countBackground--;
                }
            }
        }
        return (countBackground == 0 && countRect == 0);
    }

    public void rotate() {
        int m, n;
        m = 0;
        n = 0;
        int[][] tableBackup = new int[table.length][table[0].length];//复制棋盘
        for (int i = 0; i < table.length; i++) {
            System.arraycopy(table[i], 0, tableBackup[i], 0, table[i].length);
        }
        for (int i = table.length - 2; i >= 0; i--) {
            for (int j = 1; j < table[0].length - 1; j++) {
                if (table[i][j] == color) {
                    m = i;
                    n = j;
                    break;
                }
            }
        }

        if (canRotate(m, n, tableBackup)) {//可以旋转，则将转后的方块赋值回到table
            for (int i = 0; i < table.length; i++) {
                System.arraycopy(tableBackup[i], 0, table[i], 0, table[i].length);
            }
        }


    }

    public boolean canGoLeft() {
        for (int i = 0; i < table.length - 1; i++) {//从左到右扫描，一旦有撞墙或撞方块，canGoLeft → false
            for (int j = 1; j < table[0].length - 1; j++) {
                if (table[i][j] == color){
                    if (table[i][j] == color && j == 1) {
                        return false;//撞墙
                    }
                    if (table[i][j] == color && j > 1 && table[i][j - 1] != 0) {
                        return false;//撞方块
                    }
                    break;
                }

            }
        }
        return true;
    }

    public void goLeft() {
        if (canGoLeft()) {
            for (int i = table.length - 2; i >= 0; i--) {//左移 j-1
                for (int j = 1; j < table[0].length - 1; j++) {
                    if (table[i][j] == color && j > 1) {
                        table[i][j - 1] = table[i][j];
                        table[i][j] = 0;
                    }
                }
            }
        }
        if (canGoLeft()) System.out.printf("CanGo~");
        else System.out.printf("NO!!");
        System.out.printf("A--");
        Repaint();
    }

    public void goRight() {
        boolean canGoRight = true;
        for (int i = table.length - 2; i >= 0; i--) {
            for (int j = table[0].length - 2; j > 0; j--) {
                if (table[i][j] == color && j == table[0].length - 2) {
                    canGoRight = false;//撞墙
                    break;
                }
                if (table[i][j] == color && j < table[0].length - 2 && table[i][j + 1] != 0) {
                    canGoRight = false;//撞方块
                    break;
                }
            }
        }

        if (canGoRight) {
            for (int i = table.length - 2; i >= 0; i--) {//右移 j+1
                for (int j = table[0].length - 2; j > 0; j--) {
                    if (table[i][j] == color) {
                        table[i][j + 1] = table[i][j];
                        table[i][j] = 0;
                    }
                }
            }
            Repaint();
        }

    }

    public void goDown() {


    }

    //public void

    //@Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
