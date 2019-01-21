import java.io.Serializable;

public class Hole implements Serializable {

    //0 - empty, 1 - taken
    private int status;
    private int color;

    public Hole(boolean isTaken){
        if (isTaken){
            status = 1;
        } else {
            status = 0;
        }
    }

    public void setTaken(){
        status = 1;
    }

    public void setEmpty(){
        status = 0;
    }

    public boolean isTaken(){
        if(status == 1){
            return true;
        } else {
            return false;
        }
    }

}
