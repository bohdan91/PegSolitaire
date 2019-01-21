import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Cloneable, Serializable {

    private Hole[][] board;
    private ArrayList<Move> moves;
    private int pegsLeft;


    public Board(){
        moves = new ArrayList<>();
        board = new Hole[7][7];

        pegsLeft = 32;

        for(int l = 0; l < board.length; l++){
            for(int c = 0; c < board[l].length; c++){
                if((l < 2 || l > 4) && (c < 2 || c > 4)){
                    board[l][c] = null;

                }else if(l == 3 && c == 3){
                    board[l][c] = new Hole(false);

                }
                else {
                    board[l][c] = new Hole(true);
                }
            }
        }
    }

    public Board(Board b){
        this.moves = (ArrayList<Move>) b.moves.clone();
        this.board = new Hole[7][7];
        this.pegsLeft = b.pegsLeft;
        Hole[][] board = b.getBoard();
        for(int l = 0; l < board.length; l++) {
            for (int c = 0; c < board[l].length; c++) {
                if(board[l][c] != null) {
                    this.board[l][c] = new Hole(board[l][c].isTaken());
                }
            }
        }
    }


    public void printBoard(){
        for(int l = 0; l < board.length; l++) {
            for (int c = 0; c < board[l].length; c++) {
                if(board[l][c] == null){
                    System.out.print(" ");
                } else if (board[l][c].isTaken()){
                    System.out.print("*");
                }else if(!board[l][c].isTaken()){
                    System.out.print(".");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }



    public boolean canMakeMove(Move move){
        int startLine = move.getStartLine();
        int startColumn = move.getStartColumn();
        int endLine = move.getEndLine();
        int endColumn = move.getEndColumn();

        if(startLine < 0 || startLine > board.length - 1 || startColumn < 0 || startColumn > board[startLine].length){
            return false;
        }
        if((startLine < 2 || startLine > 4) && (startColumn < 2 || startColumn > 4)){
            return false;
        }
        if((endLine < 2 || endLine > 4) && (endColumn < 2 || endColumn > 4)){
            return false;
        }

        if (board[startLine][startColumn].isTaken()){
            switch (move.getDirection()) {
                case 'S':
                    if (board[startLine + 1][startColumn].isTaken()) {
                        if (!board[startLine + 2][startColumn].isTaken()) {
                            if (startLine + 2 == endLine && startColumn == endColumn) {
                                return true;
                            }
                        }
                    }
                    break;
                case 'N':
                    if (board[startLine - 1][startColumn].isTaken()) {
                        if (!board[startLine - 2][startColumn].isTaken()) {
                            if (startLine - 2 == endLine && startColumn == endColumn) {
                                return true;
                            }
                        }
                    }
                    break;
                case 'W':
                    if (board[startLine][startColumn - 1].isTaken()) {
                        if (!board[startLine][startColumn - 2].isTaken()) {
                            if (startLine == endLine && startColumn - 2 == endColumn) {
                                return true;
                            }
                        }
                    }
                    break;
                case 'E':
                    if (board[startLine][startColumn + 1].isTaken()) {
                        if (!board[startLine][startColumn + 2].isTaken()) {
                            if (startLine == endLine && startColumn + 2 == endColumn) {
                                return true;
                            }
                        }
                    }
                    break;

            }
        }

        return false;
    }

    public boolean makeMove(Move move){
//        if(canMakeMove(move)){
            int startLine = move.getStartLine();
            int startColumn = move.getStartColumn();
            int endLine = move.getEndLine();
            int endColumn = move.getEndColumn();

            board[startLine][startColumn].setEmpty();
            board[endLine][endColumn].setTaken();
            switch (move.getDirection()){
                case 'N':
                    board[startLine - 1][startColumn].setEmpty();
                    break;
                case 'S':
                    board[startLine + 1][startColumn].setEmpty();
                    break;
                case 'E':
                    board[startLine][startColumn + 1].setEmpty();
                    break;
                case'W':
                    board[startLine][startColumn - 1].setEmpty();
                    break;
            }
            moves.add(move);
            pegsLeft -= 1;
            return true;
//        } else{
//            return false;
//        }
    }

    public void undoLastMove(){
        if(moves.size() > 0){
            Move move = moves.get(moves.size() - 1);
            int startLine = move.getStartLine();
            int startColumn = move.getStartColumn();
            int endLine = move.getEndLine();
            int endColumn = move.getEndColumn();

            board[startLine][startColumn].setTaken();
            board[endLine][endColumn].setEmpty();

            switch (move.getDirection()){
                case 'N':
                    board[startLine - 1][startColumn].setTaken();
                    break;
                case 'S':
                    board[startLine + 1][startColumn].setTaken();
                    break;
                case 'E':
                    board[startLine][startColumn + 1].setTaken();
                    break;
                case'W':
                    board[startLine][startColumn - 1].setTaken();
                    break;
            }
            moves.remove(moves.size() - 1);
            pegsLeft +=1;
        }
    }

    public Hole[][] getBoard() {
        return board;
    }

    public ArrayList<Move> getMoves() {
        return moves;
    }

    private void setBoard(Hole[][] board){
        this.board = board;
    }

    public Object clone(){

        Hole[][] board = new Hole[7][7];

        for(int l = 0; l < board.length; l++) {
            for (int c = 0; c < board[l].length; c++) {
                board[l][c] = this.board[l][c];
            }
        }

        Board b = new Board();
        b.setBoard(board);

        return b;
    }

    public int getPegsLeft(){
        return pegsLeft;
    }


    public void printMoves(){
        for(Move move : moves){
            System.out.print(move.getStartLine()+","+move.getStartColumn()+move.getDirection() + " ");
        }
    }
}













