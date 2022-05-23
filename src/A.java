import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class A extends JFrame implements KeyListener {
    private static final int game_x = 21;
    private static final int game_y = 12;
    JTextArea[][] text;
    int[][] table;
    JLabel label1,label2,label3;
    JTextField scoreField,gameField;
    JLabel hint1,hint2,hint3,hint4;
    int score = 0;
    int time;

    int[] completeLine;
    int lineCounter;
    Random rand = new Random();
    int color;
    int[][] shape;
    //成就
    ArrayList<Integer> achievement = new ArrayList<>();
    String[] achievementContent;
    int rectNumber = 0;//已获得方块数量
    int rectType;//定位方块类型
    int recType_Achievement;
    int theSameRecType_Achievement;
    int typeNumber;
    Font font1 = new Font("方正姚体",Font.PLAIN,20);
    Font font2 = new Font("Let's go Digital",Font.BOLD,20);
    Font font3 = new Font("黑体",Font.PLAIN,20);

    ArrayList<int[][]> rl = new ArrayList<>();
    ArrayList<int[][]> ll = new ArrayList<>();
    ArrayList<int[][]> t = new ArrayList<>();
    ArrayList<int[][]> z1 = new ArrayList<>();
    ArrayList<int[][]> z2 = new ArrayList<>();
    ArrayList<int[][]> bar = new ArrayList<>();
    ArrayList<int[][]> squ = new ArrayList<>();
    int[][] temporaryRect;


    public void initWindow() {
        this.setSize(600, 800);
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
        this.setLayout(new BorderLayout(1,1));
        this.add(gameMain, BorderLayout.CENTER);
    }

    //5
    public void initExplainPanel() {
        JPanel explain_le = new JPanel();
        explain_le.setLayout(new GridLayout(12, 1));
        explain_le.setBackground(Color.white);
        label1 = new JLabel("[游戏状态]",JLabel.CENTER);
        label1.setFont(font3);

        gameField = new JTextField("游戏中!!!");
        gameField.setForeground(Color.blue);
        gameField.setEditable(false);
        gameField.setFont(font3);

        label3 = new JLabel("Your Score:",JLabel.LEFT);
        label3.setFont(font2);

        scoreField = new JTextField(score + " NumOfRect: " + rectNumber);
        scoreField.setFont(font2);
        scoreField.setEditable(false);

        hint1 = new JLabel("(←)向左移动",JLabel.CENTER);hint1.setFont(font3);hint1.setForeground(Color.BLUE);
        hint2 = new JLabel("(→)向右移动",JLabel.CENTER);hint2.setFont(font3);hint2.setForeground(Color.BLUE);
        hint3 = new JLabel("(↓)加速下落",JLabel.CENTER);hint3.setFont(font3);hint3.setForeground(Color.BLUE);
        hint4 = new JLabel(" (Space)旋转方块 ",JLabel.CENTER);hint4.setFont(font3);hint4.setForeground(Color.BLUE);

        explain_le.add(hint1);
        explain_le.add(hint2);
        explain_le.add(hint3);
        explain_le.add(hint4);
        for (int i = 0; i < 1; i++) {
            explain_le.add(new JLabel());
        }
        explain_le.add(label1);
        explain_le.add(gameField);
        explain_le.add(label3);
        explain_le.add(scoreField);

        this.add(explain_le, BorderLayout.EAST);
    }

    public A() {
        text = new JTextArea[game_x][game_y];
        table = new int[game_x][game_y];

        initGamePanel();
        initExplainPanel();
        initWindow();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        A tetris1 = new A();
        tetris1.gameRun();
    }

    //gameRun包含成就6
    public void gameRun() throws InterruptedException, IOException {
        time = 700;
        initialize();
        System.out.println("Tetris Go!");
        //游戏开始，结束时退出循环

        getRandomRect();
        recType_Achievement = rectType;

        while (true) {
            //判定成就2
            if(recType_Achievement  == rectType){
                theSameRecType_Achievement++;
            } else{
                recType_Achievement = 0;
                theSameRecType_Achievement = 0;
            }
            if(theSameRecType_Achievement == 3 ) achievementsJudge(1);

            while (true) {//方块移动 触底时退出
                if (!atTheEnd() ) {
                    fall();
                    //时间间隔
                    Repaint();
                    Thread.sleep(500);
                    //自动下移
                } else break;
            }
            //已获得方块计数
            rectNumber++;
            //触底后，状态判断
            //record记录游戏区域状态 记录lineCounter
            record();
            //分数处理
            scoreProcess();
            //label2 = new JLabel("Score: " + score + "  " + rectNumber);
            //消除满足条件的行 内含Repaint() 已加入时停
            erasure();
            //下移 内含Repaint() 已加入时停
            moveDown();
            tableProcess();//将colorNum + 4，改为对应颜色值 (2、3、4、5 对应 6、7、8、9）
            if (GameOver()) break;
            //方块产生并运动 触底即退出运动循环
            getRandomRect();
        }
        label1 = new JLabel("Good boy go~");
        if (rectNumber <= 6 && achievement.get(5) == 0) {
            achievementsJudge(5);
            //成就提醒
        }
        recordAchievement();
    }



    public void initialize() throws FileNotFoundException {
        ll();
        rl();
        t();
        z1();
        z2();
        bar();
        squ();
        completeLine = new int[table.length];
        score = 0;//分数
        //成就数据
        achievementContent = new String[]{"获得不多于20个方块时分数达到1000",
                "连续三次获得同一个方块","分数首次达到2000","分数首次达到4000",
                "分数首次达到6000","游戏结束时获得了不多于6个方块","一次性消除四行"};
        rectNumber = 0;

        //获得方块所用的参数：
        color = 0;
        rectType = 0;
        typeNumber = 0;
        theSameRecType_Achievement = 0;
        for (int i = 0; i < table.length - 1; i++) {
            for (int j = 1; j < table[i].length - 1; j++) {
                table[i][j] = 0;
            }
        }
        //读取成就
        File file1 = new File("achievement.txt");
        Scanner sc = new Scanner(file1);
        while (sc.hasNext()){
            achievement.add(sc.nextInt());
        }
        sc.close();

        KeyListener listener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                int code = event.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_DOWN:
                        goDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        goLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
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
    }
    public void getRandomRect(){
        color = rand.nextInt(4) + 2;
        rectType = rand.nextInt(19);
        shape = getRect();//获取随机的一个方块
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (shape[i][j] == 1) {
                    table[i][j + 4] = color;//将初始色改为color编号；
                }                      //以上完成方块种类以及颜色的随机生成
            }
        }
        Repaint();
    }

    public void achievementsJudge(int idNumber)throws IOException {
        if(achievement.get(idNumber) == 0){
            achievement.set(idNumber,1);
            //且对应成就提醒
        }

    }

    public void recordAchievement() throws FileNotFoundException {
        java.io.File file2 = new java.io.File("achievementNumber.txt");
        PrintWriter output = new PrintWriter(file2);
        for(double i : achievement) output.print(i + " ");
        output.close();
    }
    public boolean atTheEnd() {
        boolean stop = false;
        //初始化记录值
        for (int k = 1; k < table[0].length - 1; k++) {
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
    public void record() {
        int count;
        lineCounter=0;
        for (int i = table.length - 2; i >= 4; i--) {
            //从下至上，记录满足条件的整行方块
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
        for (int i = 0; i < table.length-1; i++) {
            if(completeLine[i] == 1){
                for (int j = 1; j < table[0].length-1; j++) {
                        table[i][j] = 0;
                }
            }
        }
        Repaint();
        Thread.sleep(300);
    }
    //已优化，保证没有残影   含有Repaint()、时停sleep
    public void moveDown() throws InterruptedException {
//        lineCounter = 0;
//        for (int i : completeLine) lineCounter += i;

        while (lineCounter > 0) {//移动，一次一行
            int k;
            for (k = 19; k > 3; k--) {//前四行不进行判断
                if (completeLine[k] == 1) {
                    //此处改为0，标记已处理
                    completeLine[k] = 0;
                    break;
                }
            }

            for (int i = k; i >= 3; i--) {
                for (int j = 1; j < table[0].length - 1; j++) {
                    //同时将table数据和completeLine下移 同步两个数据
                    table[i][j] = table[i - 1][j];
                    completeLine[i] = completeLine[i - 1];
                }
            }
//            for (int j = 1; j < table[0].length - 1; j++) {
//                //清除首行，保证没有方块余留
//                table[0][j] = 0;
//                completeLine[0] = 0;
//            }
            lineCounter--;
        }
        Repaint();
        Thread.sleep(300);
    }

    //scoreProcess内包含成就7”一次性消除四行条件”,成就1、3、4、5（2000、4000、6000 须添加成就显示
    //可通过判别achievement内数据进行处理
    public void scoreProcess() throws IOException {
        //当前score处理，加分
        if (lineCounter == 1) score += 40;
        if (lineCounter == 2) score += 100;
        if (lineCounter == 3) score += 500;
        if (lineCounter == 4) {
            score += 1000;
            if (achievement.get(6) == 0) achievementsJudge(6);
        }
        if (score >= 1000 && rectNumber <= 50 && achievement.get(0) == 0) achievementsJudge(0);
        if (score >= 2000 && achievement.get(2) == 0)achievementsJudge(2);
        if (score >= 4000 && achievement.get(3) == 0) achievementsJudge(3);
        if (score >= 6000 && achievement.get(4) == 0) achievementsJudge(4);
        scoreField.setText(score + " NumOfRect: " + rectNumber);
    }
    public void tableProcess() {
        for (int i = 0; i < table.length - 1; i++) {
            for (int j = 1; j < table[i].length - 1; j++) {
                if (table[i][j] < 6 && table[i][j] >= 2) table[i][j] = table[i][j] + 4;
            }
        }
    }

        public void Repaint(){
        for (int i = 0; i < table.length-1; i++){
            for (int j = 1; j < table[i].length-1; j++){
                if(table[i][j] == 2 || table[i][j] == 6) text[i][j].setBackground(new Color(177,114,226));
                else if(table[i][j] == 3 || table[i][j] == 7) text[i][j].setBackground(new Color(000,204,177));
                else if(table[i][j] == 4 || table[i][j] == 8) text[i][j].setBackground(new Color(107,155,210));
                else if(table[i][j] == 5 || table[i][j] == 9) text[i][j].setBackground(new Color(248,210,227));
                else if(table[i][j] == 0) text[i][j].setBackground(Color.WHITE);
            }
        }
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

    public boolean canRotate(int m , int n) {
        //获取对应方块
        if (rectType < 12){
            typeNumber = (typeNumber + 1) % 4;
            if(rectType < 4) temporaryRect = ll.get(typeNumber);
            else if(rectType < 8) temporaryRect = rl.get(typeNumber);
            else temporaryRect = t.get(typeNumber);
        } else if (rectType < 18){
            typeNumber = (typeNumber + 1) % 2;
            if(rectType < 14) temporaryRect = z1.get(typeNumber);
            else if(rectType < 16) temporaryRect = z2.get(typeNumber);
            else temporaryRect = bar.get(typeNumber);
        } else temporaryRect = squ.get(0);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(temporaryRect[i][j] != 0){
                    //判断目标方块的有色区域 如果进入table是否越界
                    if( !(m-3+i >= 0 && m-3+i < table.length-1 && n+j >=1 && n + j < table[0].length-1)) return false;
                    //如果没有越界，判断 目标方块的有色区域 是否和table已经堆叠的方块重叠
                    else if (table[m-3+i][j+n] != 0 && table[m-3+i][j+n] != color) return false;
                }
            }
        }
        return true;
    }

    public void rotate() {
        int m, n;
        m = 0;
        n = 0;
        //获取方块最下面那行的首个色块的位置
        for (int i = 0; i < table.length-1; i++) {
            for (int j = 1; j < table[0].length-1; j++) {
                if(table[i][j] == color ) {
                    m = i;
                    n = j;
                    break;
                }
            }
        }
        //从左下角到右上角，内循环为j 得出（3,0）的相对位置
        if (rectType < 4) {
            if (typeNumber == 1) m++;
            else if (typeNumber == 2){
                n--;
            }else if (typeNumber == 3){
                n-=2;
            }
        }
        else if (rectType < 8) {
            if (typeNumber == 0 || typeNumber == 2) n--;
            if (typeNumber == 3) m++;
        }
        else if (rectType < 12) {
                if (typeNumber == 0) m++;
                else n--;
            }
        //z1
        else if (rectType < 14) {
                if (typeNumber == 0) {
                    n--;
                }
                else m++;
            }
        //z2
         else if (rectType < 16) {
            if (typeNumber == 0) n = n - 1;
            else{
                m++;
                n--;
            }
            //bar
        } else if (rectType < 18) {
            if (typeNumber == 0) n--;
            if (typeNumber == 1) m++;
        }


        //扫除原有方块并将新方块绘制到table上
        if(rectType != 18 && canRotate(m,n) ){
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                   if (table[m-3+i][j+n] == color) {
                       table[m-3+i][j+n] = 0;
                   }
                }
            }
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if(temporaryRect[i][j] != 0) table[m-3+i][n+j] = color;
                }
            }
            Repaint();
        }
        else {
            //还原被更改的typeNumber
            if(rectType == 18) typeNumber = 0;
            else if(rectType >= 12 && rectType < 18)typeNumber =  (typeNumber-1)%2;
            else typeNumber = (typeNumber-1)%4;
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
        Repaint();
    }

    public boolean canGoRight(){
        for (int i = table.length - 2; i >= 0; i--) {
            for (int j = table[0].length - 2; j > 0; j--) {
                if(table[i][j] == color) {
                    if (table[i][j] == color && j == table[0].length - 2) {
                        return false;//撞墙
                    }
                    if (table[i][j] == color && j < table[0].length - 2 && table[i][j + 1] != 0) {
                        return false;//撞方块
                    }
                    break;
                }
            }
        }
        return true;
    }
    public void goRight() {
        if (canGoRight()) {
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
        if(!atTheEnd()){
            fall();
            Repaint();
        }
    }

    public int[][] getRect() {
        if (rectType < 4) {
            typeNumber = rand.nextInt(4);
            return ll.get(typeNumber);
        } else if (rectType < 8) {
            typeNumber = rand.nextInt(4);
            return rl.get(typeNumber);
        } else if (rectType < 12) {
            typeNumber = rand.nextInt(4);
            return t.get(typeNumber);
        } else if (rectType < 14) {
            typeNumber = rand.nextInt(2);
            return z1.get(typeNumber);
        } else if (rectType < 16) {
            typeNumber = rand.nextInt(2);
            return z2.get(typeNumber);
        } else if (rectType < 18) {
            typeNumber = rand.nextInt(2);
            return bar.get(typeNumber);
        } else {
            return squ.get(0);
        }
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

        int[][] ll3 = new int[4][4];
        ll3[1][2] = 1;
        ll3[1][1] = 1;
        ll3[2][1] = 1;
        ll3[3][1] = 1;
        ll.add(ll3);

        int[][] ll4 = new int[4][4];
        ll4[2][0] = 1;
        ll4[2][1] = 1;
        ll4[2][2] = 1;
        ll4[3][2] = 1;
        ll.add(ll4);

    }

    public void rl() {
        int[][] l1 = new int[4][4];
        l1[1][1] = 1;
        l1[2][1] = 1;
        l1[3][1] = 1;
        l1[3][2] = 1;
        rl.add(l1);

        int[][] l2 = new int[4][4];
        l2[3][0] = 1;
        l2[2][0] = 1;
        l2[2][1] = 1;
        l2[2][2] = 1;
        rl.add(l2);

        int[][] l3 = new int[4][4];
        l3[1][0] = 1;
        l3[1][1] = 1;
        l3[2][1] = 1;
        l3[3][1] = 1;
        rl.add(l3);

        int[][] l4 = new int[4][4];
        l4[2][0] = 1;
        l4[2][1] = 1;
        l4[2][2] = 1;
        l4[1][2] = 1;
        rl.add(l4);

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
    }

    public void bar() {
        int[][] i1 = new int[4][4];
        i1[0][1] = 1;
        i1[1][1] = 1;
        i1[2][1] = 1;
        i1[3][1] = 1;
        bar.add(i1);
        int[][] i2 = new int[4][4];
        i2[2][0] = 1;
        i2[2][1] = 1;
        i2[2][2] = 1;
        i2[2][3] = 1;
        bar.add(i2);
    }
    public void squ() {
        int[][] squ1 = new int[4][4];
        squ1[1][1] = 1;
        squ1[1][2] = 1;
        squ1[2][1] = 1;
        squ1[2][2] = 1;
        squ.add(squ1);
    }




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
