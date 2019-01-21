import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class BoardSolver implements Runnable{

    private static ArrayList<Board> boardPool;
    private static ArrayList<Board> solutionBoards;
    private static final int initIterations = 4;
    private static BoardFrame boardFrame;
    private static boolean serverMode;
    private static ReentrantLock poolLock = new ReentrantLock();
    private static ReentrantLock solutionLock = new ReentrantLock();
    private static ReentrantLock frameLock = new ReentrantLock();
    private Slave server;


    //constructor for server
    public BoardSolver(BoardFrame bf){
        synchronized (this) {
            serverMode = true;
            boardFrame = bf;
            if (boardPool == null) {
                initPool();
                System.out.println("Pool size:" + boardPool.size());
            }
        }
    }

    //constructor for slave
    public BoardSolver(Slave server){
        serverMode = false;
        this.server = server;
    }

    @Override
    public void run() {

        if(serverMode) {
            for (Board board; (board = getBoardFromPool()) != null; ) {
                frameLock.lock();
                try{
                    boardFrame.setPoolSizeLabel(getPoolSize());
                }finally {
                    frameLock.unlock();
                }
                //solveBoard(board);
                solve(board);
            }
        } else { //slave mode
            for(;;){
                //solveBoard(server.getBoard());
                solve(server.getBoard());
            }
        }


    }

    private void initPool(){
        solutionBoards = new ArrayList<>();
        boardPool = new ArrayList<>();
        Board b = new Board();
        ArrayList<Board> finalBoards = new ArrayList<>();
        finalBoards.add(b);


        for(int i = 0; i < initIterations; i++){
            ArrayList<Board> tempBoards = new ArrayList<>();

            for(Board board : finalBoards){
                tempBoards.addAll(splitBoard(board));
            }
            finalBoards = tempBoards;
        }
        boardPool.addAll(finalBoards);
    }

    public ArrayList<Board> splitBoard(Board board){
        ArrayList<Board> boards = new ArrayList<>();
        ArrayList<Move> moves = findMoves(board);

        for(int i = 0; i < moves.size(); i++){
            Board copyBoard = new Board(board);
                copyBoard.makeMove(moves.get(i));
                boards.add(copyBoard);

        }
        return boards;
    }



    public ArrayList<Move> findMoves(Board b){
        Hole[][] board = b.getBoard();
        ArrayList<Move> moves = new ArrayList();
        //iterate all spots on the board
        for(int l = 0; l < board.length; l++) {
            for (int c = 0; c < board[l].length; c++) {
                //ignore not used spots
                if(board[l][c] == null){
                    continue;
                } else if(!board[l][c].isTaken()){
                    //check north direction
                    if(l-2 >= 0 && board[l-2][c] != null && board[l-2][c].isTaken() && board[l-1][c].isTaken()){
                        moves.add(new Move(l-2, c, l, c));
                    }
                    //south direction
                    if(l+2 < board.length && board[l+2][c] != null && board[l+2][c].isTaken() && board[l+1][c].isTaken()){
                        moves.add(new Move(l+2, c, l, c));
                    }
                    //west direction
                    if(c-2 >= 0 && board[l][c-2] != null && board[l][c-2].isTaken() && board[l][c-1].isTaken()){
                        moves.add(new Move(l, c-2, l, c));
                    }
                    //east direction
                    if(c+2 < board[l].length && board[l][c+2] != null && board[l][c+2].isTaken() && board[l][c+1].isTaken()){
                        moves.add(new Move(l, c+2, l, c));
                    }

                }


            }
        }


        return moves;
    }

    public void solveBoard(Board board){


        //if(pegCount(board) == 1){

            //solution found
            if(board.getBoard()[3][3].isTaken() && board.getPegsLeft() == 1) {
                System.out.println("Solutions found: " + solutionBoards.size());
                if (serverMode) {
                    addSolution(board);
                } else {//TODO: slave mode
                    server.sendSolution(board);
                    GUI.addSolutionCount();
                }
            //}

        }else{ //find possible moves and run them recursively
            ArrayList<Move> moves = findMoves(board);
            for(int i = 0; i < moves.size(); i++){
                Board copyBoard = new Board(board);
                    copyBoard.makeMove(moves.get(i));
                    solveBoard(copyBoard);

            }
        }


    }

    public boolean solve(Board b){
        Hole[][] board = b.getBoard();
        //b.printBoard();
        if(b.getPegsLeft() == 1){
            if(board[3][3].isTaken()){
                System.out.println("Solution found!");
                if(serverMode) {
                    addSolution(b);
                }else {
                    server.sendSolution(b);
                    GUI.addSolutionCount();
                }
                return true;
            }else {
                return false;
            }
        }


        for(int l = 0; l < board.length; l++) {
            for (int c = 0; c < board[l].length; c++) {
                if(board[l][c] != null){
                    if (board[l][c].isTaken()){
                        //check north direction
                        if (l - 2 >= 0 && board[l - 2][c] != null && !board[l - 2][c].isTaken() && board[l - 1][c].isTaken()) {
                            //moves.add(new Move(l-2, c, l, c));
                            b.makeMove(new Move(l, c, l - 2, c));
                            if (solve(b)) {
                                return true;
                            }
                                b.undoLastMove();

                        }
                    //south direction
                    if (l + 2 < board.length && board[l + 2][c] != null && !board[l + 2][c].isTaken() && board[l + 1][c].isTaken()) {
                        //moves.add(new Move(l+2, c, l, c));
                        b.makeMove(new Move(l, c, l + 2, c));
                        if (solve(b)) {
                            return true;
                        }
                            b.undoLastMove();

                    }
                    //west direction
                    if (c - 2 >= 0 && board[l][c - 2] != null && !board[l][c - 2].isTaken() && board[l][c - 1].isTaken()) {
                        //moves.add(new Move(l, c-2, l, c));
                        b.makeMove(new Move(l, c, l, c - 2));
                        if (solve(b)) {
                            return true;
                        }
                            b.undoLastMove();

                    }
                    //east direction
                    if (c + 2 < board[l].length && board[l][c + 2] != null && !board[l][c + 2].isTaken() && board[l][c + 1].isTaken()) {
                        //moves.add(new Move(l, c+2, l, c));
                        b.makeMove(new Move(l, c, l, c + 2));
                        if (solve(b)) {
                            return true;
                        }
                            b.undoLastMove();

                    }
                }
            }
        }
    }
        return false;
    }

    private int pegCount(Board b){
        int count = 0;
        Hole[][] board = b.getBoard();

        for(int l = 0; l < board.length; l++) {
            for (int c = 0; c < board[l].length; c++) {
                if(board[l][c] != null && board[l][c].isTaken()){
                    ++count;
                }
            }
        }
        return count;
    }


    public void addToPool(Board board){
        poolLock.lock();
        boardPool.add(board);
        poolLock.unlock();
    }

    public static  int getPoolSize(){
        poolLock.lock();
        try {
            return boardPool.size();
        }finally {
            poolLock.unlock();
        }
    }

    public int getSolutionSize(){
        solutionLock.lock();
        try {
            return solutionBoards.size();
        }finally {
            solutionLock.unlock();
        }
    }

    public static ArrayList<Move> getSolution(){
        solutionLock.lock();
        try {
            if(solutionBoards.isEmpty()){
                return null;
            }
            int i = ThreadLocalRandom.current().nextInt(solutionBoards.size());
            Board b = solutionBoards.get(i);
            solutionBoards.remove(i);
            return b.getMoves();
        }finally {
            solutionLock.unlock();
        }
    }

    public static Board getBoardFromPool() {
        Board b = null;
        poolLock.lock();
        try {
            if(boardPool == null || boardPool.size() == 0){
                return null;
            }

            //int i = ThreadLocalRandom.current().nextInt(boardPool.size());
            int i = 0;
            b = boardPool.get(i);
            boardPool.remove(i);
            boardFrame.setPoolSizeLabel(getPoolSize());
        } finally {
            poolLock.unlock();
        }
        return b;
    }


    public static void addSolution(Board b){
        solutionLock.lock();
        try {

            solutionBoards.add(b);
            int size = solutionBoards.size();

//            ArrayList<Move> newMoves = b.getMoves();
//            for(Board board : solutionBoards){
//                ArrayList<Move> moves = board.getMoves();
//                if(moves.size() == newMoves.size()) {
//                    boolean repetition = true;
//                    for (int i = 0; i < moves.size(); i++) {
//                        if (!moves.get(i).equals(newMoves.get(i))) {
//                            repetition = false;
//                        }
//                    }
//                    if (repetition) {
//                        System.out.println("Repetition Found!!!");
//                        try {
//                            Thread.sleep(100000000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }else{
//                        solutionBoards.add(b);
//                        size = solutionBoards.size();
//                    }
//                }
//            }


//            if(size >= 100){
//                for(Board board : solutionBoards){
//                    board.printMoves();
//                    System.out.println();
//                }
//            }

            if (serverMode) {
                boardFrame.setSolutionsLabel(size);
            }
        }finally {
            solutionLock.unlock();
        }



    }

    public static ArrayList<Board> getPool(){
        return boardPool;
    }



}
