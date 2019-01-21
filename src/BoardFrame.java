import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BoardFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel boardPanel;
    private JPanel controlPanel;
    private JLabel poolSizeLabel;
    private JLabel solutionsLabel;
    private JButton playBtn;
    private JButton stopBtn;
    private static int solutionsNum = 0;
    private Thread playThread;


    public BoardFrame(){
        setTitle("Peg Solitaire");
        setSize(800,600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(7,7,1,1));
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //boardPanel.setMinimumSize(new Dimension(600,600));

       // mainPanel.add(BorderLayout.CENTER, boardPanel);

//        for(int i =0; i < 7; i ++){
//            for(int k=0; k < 7; k++){
//                    JPanel panel = new JPanel(new BorderLayout());
//                    if(i > 0) {
//                        Circle circle = new Circle(true);
//                        panel.add(circle);
//                    }
//                    //panel.setBackground(Color.GREEN);
//                    panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//                    boardPanel.add(panel);
//
//            }
//        }
        drawBoard(new Board());

        mainPanel.add(BorderLayout.CENTER, boardPanel);

        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(200,600));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        poolSizeLabel = new JLabel("Pool Size: 0");
        solutionsLabel = new JLabel("Solutions found: 0");
        stopBtn = new JButton("Stop Searching");
        playBtn = new JButton("Play Next");


        stopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        playBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(solutionsNum > 0){
                    if(playThread != null){
                        playThread.interrupt();
                    }
                    playThread = new MovePlayer(BoardSolver.getSolution());
                    playThread.start();

                }
            }
        });

        controlPanel.add(poolSizeLabel);
        controlPanel.add(Box.createVerticalStrut(20));
        controlPanel.add(solutionsLabel);
        controlPanel.add(Box.createVerticalGlue());
        //controlPanel.add(stopBtn);
        controlPanel.add(playBtn);

        mainPanel.add(BorderLayout.EAST, controlPanel);

        add(mainPanel);
        setVisible(true);
    }


    public void playMoves(ArrayList<Move> moves){

        Board b = new Board();
        drawBoard(b);
        for(Move move: moves){
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            b.makeMove(move);
            drawBoard(b);
        }

    }

    public void drawBoard(Board b){
        Hole[][] board = b.getBoard();
        boardPanel.removeAll();

        for(int l = 0; l < board.length; l++) {
            for (int c = 0; c < board[l].length; c++) {
                JPanel panel = new JPanel(new BorderLayout());
                if(board[l][c] == null){

                }else if(board[l][c].isTaken()){
                    Circle circle = new Circle(true);
                    panel.add(circle);
                }else if(!board[l][c].isTaken()){
                    Circle circle = new Circle(false);
                    panel.add(circle);
                }
                panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                boardPanel.add(panel);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public void setPoolSizeLabel(int size){
        poolSizeLabel.setText("Pool Size: " + size);
    }

    public void setSolutionsLabel(int size){
        solutionsNum = size;
        solutionsLabel.setText("Solutions found: " + size);
    }

    class MovePlayer extends Thread{

        ArrayList<Move> moves;
        boolean interrupted = false;
        public MovePlayer(ArrayList<Move> moves){
            this.moves = moves;
        }

        @Override
        public void run() {
            Board b = new Board();
            drawBoard(b);
            for(Move move: moves) {
                if (!interrupted) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    b.makeMove(move);
                    drawBoard(b);
                } else {
                    return;
                }
            }
        }

        public void interrupt(){
            interrupted = true;
        }

    }

}
