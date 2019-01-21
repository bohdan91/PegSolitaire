import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;

public class GUI {

    private static int numOfThreads;
    private static int solutionsFound = 0; //for slaves only
    public static JLabel solutionsFoundLabel;
    private static ReentrantLock solutionLock = new ReentrantLock();

    public static void main(String[] args) {
        startModeFrame();

    }

    private static void startModeFrame(){
        JFrame modeFrame = new JFrame("Peg Solitaire");
        modeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        modeFrame.setSize(600,100);
        modeFrame.setLocationRelativeTo(null);
        modeFrame.setResizable(false);

        JPanel connectPanel = new JPanel();

        JLabel threadLabel = new JLabel("Number of Threads");
        JTextField threadField = new JTextField(10);
        JButton singleButton = new JButton("Single Computer");
        JButton serverButton = new JButton("Server");
        JButton slaveButton = new JButton("Slave");

        singleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    numOfThreads = Integer.parseInt(threadField.getText());
                    modeFrame.setVisible(false);
                    modeFrame.dispose();
                    startMainFrame();
                }catch(NumberFormatException ex){
                    ex.printStackTrace();
                }
            }
        });

        serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    numOfThreads = Integer.parseInt(threadField.getText());
                    modeFrame.setVisible(false);
                    modeFrame.dispose();
                    startServerFrame();
                }catch(NumberFormatException ex){
                    ex.printStackTrace();
                }
            }
        });

        slaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    numOfThreads = Integer.parseInt(threadField.getText());
                    modeFrame.setVisible(false);
                    modeFrame.dispose();
                    startSlaveFrame();
                }catch (NumberFormatException ex){
                    ex.printStackTrace();
                }
            }
        });

        connectPanel.add(threadLabel);
        connectPanel.add(threadField);
        connectPanel.add(singleButton);
        connectPanel.add(serverButton);
        connectPanel.add(slaveButton);

        modeFrame.getContentPane().add(connectPanel);
        modeFrame.setVisible(true);
    }

    private static void startSlaveFrame(){
        JFrame slaveFrame = new JFrame("Peg Solitaire");
        slaveFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        slaveFrame.setSize(600,100);
        slaveFrame.setLocationRelativeTo(null);
        slaveFrame.setResizable(false);

        JPanel connectPanel = new JPanel();

        JLabel ipLabel = new JLabel("IP:");
        JTextField ipField = new JTextField(15);
        JButton connectButton = new JButton("Connect");

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Slave slave = new Slave(ipField.getText());
                    slave.connect();

                    for (int i = 0; i < numOfThreads; i++) {
                        new Thread(new BoardSolver(slave)).start();
                    }
                    connectButton.setVisible(false);
                    solutionsFoundLabel = new JLabel("Solutions found: 0");
                    connectPanel.add(solutionsFoundLabel);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        connectPanel.add(ipLabel);
        connectPanel.add(ipField);
        connectPanel.add(connectButton);

        slaveFrame.getContentPane().add(connectPanel);
        slaveFrame.setVisible(true);

    }

    private static void startServerFrame(){
        JFrame serverFrame = new JFrame("Peg Solitaire");
        serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverFrame.setSize(600,100);
        serverFrame.setLocationRelativeTo(null);
        serverFrame.setResizable(false);

        JPanel connectPanel = new JPanel();

        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        }catch (UnknownHostException e){
            e.printStackTrace();
        }
        JLabel ipLabel = new JLabel("IP: " + ip);
        JLabel slaveLabel = new JLabel("Slaves connected: ");
        JButton startButton = new JButton("Start");

        new Thread(new Server(slaveLabel)).start();

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverFrame.setVisible(false);
                serverFrame.dispose();
                startMainFrame();
            }
        });

        connectPanel.setLayout(new BoxLayout(connectPanel, BoxLayout.LINE_AXIS));
        connectPanel.add(ipLabel);
        connectPanel.add(Box.createHorizontalGlue());
        connectPanel.add(slaveLabel);
        connectPanel.add(Box.createHorizontalGlue());
        connectPanel.add(startButton);

        serverFrame.getContentPane().add(connectPanel);
        serverFrame.setVisible(true);
    }

    private static void startMainFrame(){

        BoardFrame boardFrame = new BoardFrame();

        for(int i = 0; i < numOfThreads; i++) {
            new Thread(new BoardSolver(boardFrame)).start();
        }


    }

    public static void addSolutionCount(){
        solutionLock.lock();
        try {
            solutionsFound += 1;
            solutionsFoundLabel.setText("Solutions found: " + solutionsFound);
        }finally {
            solutionLock.unlock();
        }
    }


}
