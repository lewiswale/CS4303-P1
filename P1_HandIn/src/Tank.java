import processing.core.PApplet;

import java.util.ArrayList;

public class Tank {
    private int currentX, currentY, startX, startY;
    private final int WIDTH = 100;
    private final int HEIGHT = 50;
    private PApplet p;
    private GameMap m;
    private int currentCol, startCol;
    private int currentLevel = 0;
    String name;

    public Tank(PApplet pa, GameMap m, int x, int y, int col, String name) {
        this.p = pa;
        this.m = m;
        this.currentX = x;
        this.currentY = y;
        this.startX = currentX;
        this.startY = currentY;
        this.currentCol = col;
        this.startCol = col;
        this.name = name;
    }

    public void move(int movesMade) {
        if (movesMade == 1) {
            currentX += WIDTH;
            currentCol++;
            ArrayList<MapBlock> blocks = m.getBlocks();
            int levelCount = 0;

            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i).col == currentCol) {
                    levelCount++;
                }
            }

            currentY -= HEIGHT*(levelCount - currentLevel);
            currentLevel = levelCount;
        } else if (movesMade == -1){
            currentX -= WIDTH;
            currentCol--;
            ArrayList<MapBlock> blocks = m.getBlocks();
            int levelCount = 0;

            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i).col == currentCol) {
                    levelCount++;
                }
            }

            currentY -= HEIGHT*(levelCount - currentLevel);
            currentLevel = levelCount;
        } else if (movesMade == 0) {
            ArrayList<MapBlock> blocks = m.getBlocks();
            int levelCount = 0;

            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i).col == currentCol) {
                    levelCount++;
                }
            }

            currentY -= HEIGHT*(levelCount - currentLevel);
            currentLevel = levelCount;
        }
    }

    public void drawTank() {
        p.rect(currentX, currentY, WIDTH, HEIGHT);
        p.textSize(30);
        p.fill(0);
        p.text(name, currentX + WIDTH/2 - 10, currentY + HEIGHT/2);
        p.fill(255);
    }

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public int getCurrentCol() {
        return currentCol;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public void moveToStart() {
        currentX = startX;
        currentY = startY;
        currentCol = startCol;
        currentLevel = 0;
    }
}
