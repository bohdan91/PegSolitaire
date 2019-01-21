import java.io.Serializable;

public class Move implements Serializable {

    private int startLine;
    private int startColumn;
    private int endLine;
    private int endColumn;
    private char direction;

    public Move(int startLine, int startColumn, int endLine, int endColumn) {
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;

        if(startLine != endLine){
            if (startLine < endLine){
                direction = 'S';
            } else if(startLine > endLine){
                direction = 'N';
            }
        } else if (startColumn != endColumn){
            if(startColumn < endColumn){
                direction = 'E';
            }else if (startColumn > endColumn){
                direction ='W';
            }
        }
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndColumn() {
        return endColumn;
    }
    public char getDirection(){
        return direction;
    }

    @Override
    public boolean equals(Object object){
        if(this == object){
            return true;
        }
        if(object instanceof  Move){
            Move move = (Move)object;
            if(startLine == move.getStartLine() && startColumn == move.getStartColumn()
                    && direction == move.getDirection() && endLine == move.getEndLine()
                    && endColumn == move.getEndColumn()){
                return true;
            }
        }
        return false;
    }


}
