package HexGame;

public class Game {
    private final Board board;
    private boolean gameOver;
    private boolean won;
    private int[] hint;
    private boolean isFirstMove;

    public Game(Board board) {
        this.board = board;
        this.gameOver = false;
        this.won = false;
        this.hint = null;
        this.isFirstMove = true;
    }

    public void openFirstCell(int r, int c) {
        if (!board.getGrid().isValid(r, c) || !board.isActive(r, c)) return;
        if (isFirstMove) {
            board.generateBoardForFirstClick(r, c);
            isFirstMove = false;
        }
        openCell(r, c);
    }

    public void openCell(int r, int c) {
        if (gameOver || !board.getGrid().isValid(r, c) || !board.isActive(r, c)) return;
        Cell cell = board.getCell(r, c);
        if (cell.isFlagged() || cell.isRevealed()) return;
        cell.reveal();
        hint = null;
        if (cell.isBlue()) {
            gameOver = true;
            won = false;
        } else if (checkWin()) {
            gameOver = true;
            won = true;
        }
    }

    public void toggleFlag(int r, int c) {
        if (gameOver || !board.getGrid().isValid(r, c) || !board.isActive(r, c)) return;
        Cell cell = board.getCell(r, c);
        if (!cell.isRevealed()) {
            cell.toggleFlag();
            hint = null;
            if (checkWin()) {
                gameOver = true;
                won = true;
            }
        }
    }

    private boolean checkWin() {
        for (int r = 0; r < board.getGrid().getRows(); r++) {
            for (int c = 0; c < board.getGrid().getCols(); c++) {
                if (!board.isActive(r, c)) continue;
                Cell cell = board.getCell(r, c);
                if (cell.isBlue() && !cell.isFlagged()) return false;
                if (!cell.isBlue() && !cell.isRevealed()) return false;
            }
        }
        return true;
    }

    public int[] getHint() {
        if (hint != null) return hint;

        for (int r = 0; r < board.getGrid().getRows(); r++) {
            for (int c = 0; c < board.getGrid().getCols(); c++) {
                if (!board.isActive(r, c) || board.getCell(r, c).isBlue() || !board.getCell(r, c).isRevealed()) continue;
                int clue = board.getCell(r, c).getClue();
                int unrevealedNeighbors = 0;
                int unflaggedNeighbors = 0;
                for (int neighbor : board.getGrid().getNeighbors(r, c)) {
                    if (neighbor == -1) continue;
                    int nr = neighbor / board.getGrid().getCols();
                    int nc = neighbor % board.getGrid().getCols();
                    if (!board.isActive(nr, nc)) continue;
                    if (!board.getCell(nr, nc).isRevealed()) unrevealedNeighbors++;
                    if (!board.getCell(nr, nc).isFlagged() && !board.getCell(nr, nc).isRevealed()) unflaggedNeighbors++;
                }
                if (unflaggedNeighbors > 0 && clue == unflaggedNeighbors) {
                    for (int neighbor : board.getGrid().getNeighbors(r, c)) {
                        if (neighbor == -1) continue;
                        int nr = neighbor / board.getGrid().getCols();
                        int nc = neighbor % board.getGrid().getCols();
                        if (!board.isActive(nr, nc) || board.getCell(nr, nc).isRevealed() || board.getCell(nr, nc).isFlagged()) continue;
                        hint = new int[]{nr, nc, 1};
                        return hint;
                    }
                }
                if (unrevealedNeighbors > 0 && clue == 0) {
                    for (int neighbor : board.getGrid().getNeighbors(r, c)) {
                        if (neighbor == -1) continue;
                        int nr = neighbor / board.getGrid().getCols();
                        int nc = neighbor % board.getGrid().getCols();
                        if (!board.isActive(nr, nc) || board.getCell(nr, nc).isRevealed()) continue;
                        hint = new int[]{nr, nc, 0};
                        return hint;
                    }
                }
            }
        }
        return null;
    }

    public boolean isGameOver() { return gameOver; }
    public boolean isWon() { return won; }
    public Board getBoard() { return board; }
}