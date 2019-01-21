import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Slave  {
    private String ip;
    private static int port = 2688;
    private static ReentrantLock lock = new ReentrantLock();

    public Slave(String ip){
        this.ip = ip;
    }

    public void connect(){
        lock.lock();
        try {
            System.out.println(ip);
            Socket s = new Socket(ip, port);
            //PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            ObjectOutputStream objectOut = new ObjectOutputStream(s.getOutputStream());
            objectOut.writeObject(new String("connect"));
            objectOut.close();
            s.close();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public Board getBoard(){
        lock.lock();
        Board b = null;

        try{
            Socket s = new Socket(ip, port);
            //PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            ObjectOutputStream objectOut = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream objectIn = new ObjectInputStream(s.getInputStream());

            //out.println("getBoard");
            objectOut.writeObject(new String("getBoard"));
            b = (Board)objectIn.readObject();
            objectOut.close();
            objectIn.close();
            s.close();


        }catch (Exception e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return b;
    }

    public void sendSolution(Board b){
        lock.lock();
        try{
            Socket s = new Socket(ip, port);
            //PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            ObjectOutputStream objectOut = new ObjectOutputStream(s.getOutputStream());

            //out.println("solution");
            objectOut.writeObject(new String("solution"));
            objectOut.writeObject(b);
            objectOut.close();
            //out.close();
            s.close();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

}
