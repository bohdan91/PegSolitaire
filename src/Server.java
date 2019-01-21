import javax.swing.*;
import java.net.*;
import java.io.*;

public class Server implements Runnable{
    static final int PORT = 2688;
    private JLabel slaveLabel;
    private int connected;

    public Server(JLabel slaveLabel){
        this.slaveLabel = slaveLabel;
        connected = 0;
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            for (;;) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected");
                new Thread(new ClientService(client)).start();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    class ClientService implements Runnable{
        private Socket client;

        public ClientService (Socket client){
            this.client = client;
        }

        @Override
        public void run() {
            try {

                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(client.getInputStream()));
                ObjectOutputStream objectOut = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream objectIn = new ObjectInputStream(client.getInputStream());

                String cmd = (String)objectIn.readObject();
            System.out.println("cmd:" + cmd );

                switch (cmd) {
                    case "connect":
                        System.out.println("Slave connected");
                        connected += 1;
                        slaveLabel.setText("Slaves connected: " + connected);
                        break;
                    case "getBoard":
                        Board b = BoardSolver.getBoardFromPool();
                        while(b == null){
                            try {
                                Thread.sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            b = BoardSolver.getBoardFromPool();
                        }
                        objectOut.writeObject(b);
                        break;
                    case "solution":
                        try {
                            Board board = (Board) objectIn.readObject();
                            BoardSolver.addSolution(board);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                out.close();
                in.close();
                objectOut.close();
                objectIn.close();
                client.close();


            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

    }

}