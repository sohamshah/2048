/** Main.java skeleton implemented by @author: Soham Shah. */
package game2048;

import ucb.util.CommandArgs;

import game2048.gui.Game;
import static game2048.Main.Side.*;

/** The main class for the 2048 game.
 *  @author
 */
public class Main {

    /** Size of the board: number of rows and of columns. */
    static final int SIZE = 4;
    /** Number of squares on the board. */
    static final int SQUARES = SIZE * SIZE;

    /** Symbolic names for the four sides of a board. */
    static enum Side { NORTH, EAST, SOUTH, WEST };

    /** The main program.  ARGS may contain the options --seed=NUM,
     *  (random seed); --log (record moves and random tiles
     *  selected.); --testing (take random tiles and moves from
     *  standard input); and --no-display. */
    public static void main(String... args) {
        CommandArgs options =
            new CommandArgs("--seed=(\\d+) --log --testing --no-display",
                            args);
        if (!options.ok()) {
            System.err.println("Usage: java game2048.Main [ --seed=NUM ] "
                               + "[ --log ] [ --testing ] [ --no-display ]");
            System.exit(1);
        }

        Main game = new Main(options);

        while (game.play()) {
            /* No action */
        }
        System.exit(0);
    }

    /** A new Main object using OPTIONS as options (as for main). */
    Main(CommandArgs options) {
        boolean log = options.contains("--log"),
            display = !options.contains("--no-display");
        long seed = !options.contains("--seed") ? 0 : options.getLong("--seed");
        _testing = options.contains("--testing");
        _game = new Game("2048", SIZE, seed, log, display, _testing);
    }

    /** Reset the score for the current game to 0 and clear the board. */
    void clear() {
        _score = 0;
        _count = 0;
        _game.clear();
        _game.setScore(_score, _maxScore);
        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                _board[r][c] = 0;
            }
        }
    }

    /** Play one game of 2048, updating the maximum score. Return true
     *  iff play should continue with another game, or false to exit. */
    boolean play() {
        clear();
        while (true) {

            setRandomPiece();
            if (has2048()) {
                if (_score >= _maxScore) {
                    _maxScore = _score;
                }
                _game.setScore(_score, _maxScore);
                _game.endGame();
            }
            if (gameOver()) {
                if (_score >= _maxScore) {
                    _maxScore = _score;
                }
                _game.setScore(_score, _maxScore);
                _game.endGame();
            }

        GetMove:
            while (true) {
                String key = _game.readKey();
                if (key.equals("\u2191")) {
                    key = "Up";
                } else if (key.equals("\u2193")) {
                    key = "Down";
                } else if (key.equals("\u2190")) {
                    key = "Left";
                } else if (key.equals("\u2192")) {
                    key = "Right";
                }

                switch (key) {
                case "Up": case "Down": case "Left": case "Right":
                    if (!gameOver() && tiltBoard(keyToSide(key))) {

                        break GetMove;
                    }
                    break;
                case "New Game":
                    clear();
                    setRandomPiece();
                    break;

                case "Quit":
                    return false;
                default:
                    break;
                }
            }
        }
    }

    /** Check if board has a 2048 tile.
    * Returns @true if 2048 tile is made, otherwise @false. */
    boolean has2048() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (_board[i][j] == 2048) {
                    _game.endGame();
                    return true;
                }
            }
        }
        return false;
    }

    /** countChanger counts the number of tiles on the board.
    *   return value: @cnt with is of type int. */
    int countChanger() {
        int cnt = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (_board[i][j] != 0) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    /** Return true iff the current game is over (no more moves
     *  possible). */
    boolean gameOver() {
        int c = countChanger();
        if (c < 16) {
            return false;
        }
        if (!canMerge()) {
            return true;
        }
        else {
            return false;
        }
    }

    /** Check if any tiles around any tile are the same.
    * returns @true if merge is possible. */
    boolean canMerge() {
        for (int i = 1; i < SIZE - 1; i++) {
            for (int j = 1; j < SIZE - 1; j++) {
                if (_board[i][j] == _board[i + 1][j]
                    || _board[i][j] == _board[i - 1][j]
                    || _board[i][j] == _board[i][j + 1]
                    || _board[i][j] == _board[i][j - 1]) {
                    return true;
                }
            }
        }
        if (_board[0][0] == _board[0][1] || _board[0][0] == _board[1][0]
            || _board[3][3] == _board[3][2] || _board[3][3] == _board[2][3]
            || _board[3][0] == _board[3][1] || _board[3][0] == _board[2][0]
            || _board[0][3] == _board[0][2] || _board[0][3] == _board[1][3]) {
            return true;
        }
        if (_board[0][1] == _board[0][2] || _board[1][0] == _board[2][0]
            || _board[1][3] == _board[2][3] || _board[3][1] == _board[3][2]) {
            return true;
        }
        else {
            return false;
        }
    }

    /** Add a tile to a random, empty position, choosing a value (2 or
     *  4) at random.  Has no effect if the board is currently full. */
    void setRandomPiece() {
        if (_count == SQUARES) {
            return;
        }

        if (countChanger() == 0) {
            int[] random = _game.getRandomTile();
            _game.addTile(random[0], random[1], random[2]);
            _board[random[1]][random[2]] = random[0];

            while (_board[random[1]][random[2]] != 0) {
                random = _game.getRandomTile();
            }
            _game.addTile(random[0], random[1], random[2]);
            _board[random[1]][random[2]] = random[0];
        }


        if (moves > 0) {
            int[] random = _game.getRandomTile();
            if (_board[random[1]][random[2]] != 0) {
                setRandomPiece();
            }
            else {
                _game.addTile(random[0], random[1], random[2]);
                _board[random[1]][random[2]] = random[0];
            }
        }
        _game.displayMoves();
        moves = 0;
    }

    /** 'moves' keeps track of the number of moves made
    *   each time a key is pressed. */
    private int moves = 0;

    /** Perform the result of tilting the board toward SIDE.
     *  Returns true iff the tilt changes the board. **/
    boolean tiltBoard(Side side) {
        /* As a suggestion (see the project text), you might try copying
         * the board to a local array, turning it so that edge SIDE faces
         * north. That way, you can re-use the same logic for all
         * directions.  (As usual, you don't have to). */
        int[][] board = new int[SIZE][SIZE];

        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                board[r][c] =
                    _board[tiltRow(side, r, c)][tiltCol(side, r, c)];
            }
        }
        for (int i = 1; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != 0) {
                    if (i == 1) {
                        if (board[0][j] == 0) {
                            _game.moveTile(board[i][j], tiltRow(side, i, j),
                                tiltCol(side, i, j), tiltRow(side, 0, j),
                                tiltCol(side, 0, j));
                            board[0][j] = board[i][j];
                            board[i][j] = 0;
                            moves++;
                        }
                        else if (board[0][j] == board[i][j]) {
                            _game.mergeTile(board[i][j],
                                board[0][j] * 2, tiltRow(side, i, j),
                                tiltCol(side, i, j), tiltRow(side, i - 1, j),
                                tiltCol(side, i - 1, j));
                            board[0][j] += board[i][j] + 1;
                            _score += board[i][j] * 2;
                            board[i][j] = 0;
                            moves++;
                        }
                    }
                    if (i == 2) {
                        secondRow(board, i, j, side);
                    }
                    if (i == 3) {
                        thirdRow(board, i, j, side);
                    }

                }
            }
        }
        _game.setScore(_score, _maxScore);
        _game.displayMoves();
        cancelOdds(board);
        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                _board[tiltRow(side, r, c)][tiltCol(side, r, c)]
                    = board[r][c];
            }
        }
        if (moves == 0) {
            return false;
        }
        return true;
    }


    /** secondRow is a helper method that moves all tiles
    *   in the second row, for whatever direction 'key'
    *   being pressed.
    *   @param board
    *   @param i
    *   @param j
    *   @param side
    */
    void secondRow(int[][] board, int i, int j, Side side) {
        if (board[0][j] == 0 && board[1][j] == 0) {
            _game.moveTile(board[i][j], tiltRow(side, i, j),
                tiltCol(side, i, j), tiltRow(side, 0, j), tiltCol(side, 0, j));
            board[0][j] = board[i][j];
            board[i][j] = 0;
            moves++;
        }
        else if (board[0][j] != 0 && board[1][j] == 0) {
            if (board[0][j] == board[i][j]) {
                _game.mergeTile(board[i][j], board[0][j] * 2,
                    tiltRow(side, i, j), tiltCol(side, i, j),
                    tiltRow(side, i - 2, j), tiltCol(side, i - 2, j));
                board[0][j] += board[i][j] + 1;
                _score += board[i][j] * 2;
                board[i][j] = 0;
                moves++;
            }
            else {
                _game.moveTile(board[i][j], tiltRow(side, i, j),
                    tiltCol(side, i, j), tiltRow(side, 1, j),
                    tiltCol(side, 1, j));
                board[1][j] = board[i][j];
                board[i][j] = 0;
                moves++;
            }
        }
        else if (board[0][j] != 0 && board[1][j] != 0) {
            if (board[1][j] == board[i][j]) {
                _game.mergeTile(board[i][j], board[1][j] * 2,
                    tiltRow(side, i, j), tiltCol(side, i, j),
                    tiltRow(side, i - 1, j), tiltCol(side, i - 1, j));
                board[1][j] += board[i][j] + 1;
                _score += board[i][j] * 2;
                board[i][j] = 0;
                moves++;
            }
        }
    }




    /** thirdRow is a helper method that moves all tiles
    *   in the third or bottom row, for whatever direction 'key'
    *   being pressed.
    *   @param board
    *   @param i
    *   @param j
    *   @param side
    */
    void thirdRow(int[][] board, int i, int j, Side side) {
        if (board[0][j] == 0 && board[1][j] == 0 && board[2][j] == 0) {
            _game.moveTile(board[i][j], tiltRow(side, i, j),
                tiltCol(side, i, j), tiltRow(side, 0, j), tiltCol(side, 0, j));
            board[0][j] = board[i][j];
            board[i][j] = 0;
            moves++;
        }
        else if (board[0][j] != 0 && board[1][j] == 0 && board[2][j] == 0) {
            if (board[i][j] == board[0][j]) {
                _game.mergeTile(board[i][j], board[i][j] * 2,
                    tiltRow(side, i, j), tiltCol(side, i, j),
                    tiltRow(side, 0, j),
                    tiltCol(side, 0, j));
                board[0][j] *= 2;
                board[0][j] += 1;
                _score += board[i][j] * 2;
                board[i][j] = 0;
                moves++;
            }
            else {
                _game.moveTile(board[i][j], tiltRow(side, i, j),
                    tiltCol(side, i, j), tiltRow(side, 1, j),
                    tiltCol(side, 1, j));
                board[1][j] = board[i][j];
                board[i][j] = 0;
                moves++;
            }
        }
        else if (board[0][j] != 0 && board[1][j] != 0 && board[2][j] == 0) {
            if (board[i][j] == board[1][j]) {
                _game.mergeTile(board[i][j], board[i][j] * 2,
                    tiltRow(side, i, j), tiltCol(side, i, j),
                    tiltRow(side, 1, j), tiltCol(side, 1, j));
                board[1][j] *= 2;
                board[1][j] += 1;
                _score += board[i][j] * 2;
                board[i][j] = 0;
                moves++;
            }
            else {
                _game.moveTile(board[i][j], tiltRow(side, i, j), tiltCol(side, i, j), tiltRow(side, 2, j), tiltCol(side, 2, j));
                board[2][j] = board[i][j];
                board[i][j] = 0;
                moves++;
            }
        }
        else if (board[0][j] != 0 && board[1][j] != 0 && board[2][j] != 0) {
            if (board[i][j] == board[2][j]) {
                _game.mergeTile(board[i][j], board[i][j] * 2,
                    tiltRow(side, i, j), tiltCol(side, i, j), tiltRow(side, 2, j), tiltCol(side, 2, j));
                board[2][j] *= 2;
                board[2][j] += 1;
                _score += board[i][j] * 2;
                board[i][j] = 0;
                moves++;
            }
        }
    }

    /** Cancels the odd numbers that were
    *   made due to incrementing the tiles by 1. */
    void cancelOdds(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] % 2 != 0) {
                    board[i][j] = board[i][j] - 1;
                }
            }
        }
    }

    /** Return the row number on a playing board that corresponds to row R
     *  and column C of a board turned so that row 0 is in direction SIDE (as
     *  specified by the definitions of NORTH, EAST, etc.).  So, if SIDE
     *  is NORTH, then tiltRow simply returns R (since in that case, the
     *  board is not turned).  If SIDE is WEST, then column 0 of the tilted
     *  board corresponds to row SIZE - 1 of the untilted board, and
     *  tiltRow returns SIZE - 1 - C. */
    int tiltRow(Side side, int r, int c) {
        switch (side) {
        case NORTH:
            return r;
        case EAST:
            return c;
        case SOUTH:
            return SIZE - 1 - r;
        case WEST:
            return SIZE - 1 - c;
        default:
            throw new IllegalArgumentException("Unknown direction");
        }
    }

    /** Return the column number on a playing board that corresponds to row
     *  R and column C of a board turned so that row 0 is in direction SIDE
     *  (as specified by the definitions of NORTH, EAST, etc.). So, if SIDE
     *  is NORTH, then tiltCol simply returns C (since in that case, the
     *  board is not turned).  If SIDE is WEST, then row 0 of the tilted
     *  board corresponds to column 0 of the untilted board, and tiltCol
     *  returns R. */
    int tiltCol(Side side, int r, int c) {
        switch (side) {
        case NORTH:
            return c;
        case EAST:
            return SIZE - 1 - r;
        case SOUTH:
            return SIZE - 1 - c;
        case WEST:
            return r;
        default:
            throw new IllegalArgumentException("Unknown direction");
        }
    }

    /** Return the side indicated by KEY ("Up", "Down", "Left",
     *  or "Right"). */
    Side keyToSide(String key) {
        switch (key) {
        case "Up":
            return NORTH;
        case "Down":
            return SOUTH;
        case "Left":
            return WEST;
        case "Right":
            return EAST;
        default:
            throw new IllegalArgumentException("unknown key designation");
        }
    }

    /** Represents the board: _board[r][c] is the tile value at row R,
     *  column C, or 0 if there is no tile there. */
    private final int[][] _board = new int[SIZE][SIZE];

    /** True iff --testing option selected. */
    private boolean _testing;
    /** THe current input source and output sink. */
    private Game _game;
    /** The score of the current game, and the maximum final score
     *  over all games in this session. */
    private int _score, _maxScore;
    /** Number of tiles on the board. */
    private int _count;
}
