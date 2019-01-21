import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Board b = new Board();

        BoardFrame bf = new BoardFrame();
        BoardSolver bs = new BoardSolver(bf);

        ArrayList<Board> boardPool = BoardSolver.getPool();
        ArrayList<Board> boards = bs.splitBoard(b);

        for(Board board : boardPool){
            board.printBoard();
        }

        //bs.solve(b);
  }
}
