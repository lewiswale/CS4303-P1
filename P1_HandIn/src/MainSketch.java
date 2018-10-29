import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;

public class MainSketch extends PApplet{
    private final int LEFT_EDGE = -2;
    private final int RIGHT_EDGE = 9;
    private final int MAX_SCORE = 3;
    private int tankWidth;
    private int tankHeight;
    private Tank tank1, tank2;
    private GameMap map;
    private boolean[] keys = new boolean[128];
    private boolean p1Turn = true;
    private int tank1Pos = LEFT_EDGE;
    private int tank2Pos = RIGHT_EDGE;
    private int movesMade = 0;
    private int xStart, yStart, xEnd, yEnd;
    private PVector loc, dir, dirNorm, grav, wind, acc;
    private boolean shootingPhase = false;
    private boolean drawingLine = false;
    private boolean fired = false;
    private boolean outOfTank = false;
    private int p1Score, p2Score;
    private boolean win = false;
    private boolean singlePlayer = false;
    private TankAI ai;

    public void settings() {
        size(1200, 800);
        map = new GameMap(this);
        tank1 = new Tank(this, map,0, height - 50, LEFT_EDGE, "1");
        tank2 = new Tank(this, map, width - 100, height - 50, RIGHT_EDGE, "2");
        tankHeight = tank1.getHEIGHT();
        tankWidth = tank1.getWIDTH();
        loc = new PVector(0,0);
        dir = new PVector(0,0);
        dirNorm = new PVector(0,0);
        grav = new PVector(0, 0.05f);
        wind = new PVector(0, 0);
        acc = new PVector(0,0);
        p1Score = 0;
        p2Score = 0;
        ai = new TankAI();
    }

    public void draw() {
        background(128);

        fill(0);

        textSize(20);
        if (p1Turn) {
            text("Player 1's Turn", 10, 20);
        } else {
            text("Player 2's Turn", 10, 20);
        }

        if (!shootingPhase) {
            text("Movement Phase", 10, 40);
        } else {
            text("Shooting Phase", 10, 40);
        }

        if (singlePlayer) {
            text("1P Game", width/2 - 40, 20);
        } else {
            text("2P Game", width/2 - 40, 20);
        }

        text("Score: " + p1Score + " - " + p2Score, width - 130, 20);
        text("Wind: " + (wind.x * 100), width - 130, 40);

        if (wind.x > 0) {
            text("-->", width - 40, 60);
        } else if (wind.x < 0) {
            text("<--", width - 40, 60);
        }

        fill(255);

        fill(0);
        stroke(50);
        map.drawMap();

        fill(255);
        tank1.drawTank();
        tank2.drawTank();

        if (drawingLine) {
            line(xStart, yStart, mouseX, mouseY);
        }

        if (fired) {
            ellipse(loc.x, loc.y, 16, 16);
            updateDir();
            loc.add(dirNorm.mult(10));
            detectCollision();
        }

        if (win) {
            fill(0, 255, 0);
            textSize(60);
            if (p1Score == MAX_SCORE) {
                text("PLAYER 1 WINS!", width/2 - 200, height/2);
            } else {
                text("PLAYER 2 WINS!", width/2 - 200, height/2);
            }

            textSize(40);
            text("Press [r] to restart", width/2 - 150, height/2 + 60);
            fill(0);
        }
    }

    public void keyPressed() throws ArrayIndexOutOfBoundsException{
        keys[key] = true;
        if (keys['d']) {
            if (!shootingPhase) {
                if (p1Turn) {
                    if (tank1Pos != RIGHT_EDGE && tank1Pos != tank2Pos - 1 && movesMade < 1) {
                        tank1.move(1);
                        tank1Pos++;
                        movesMade++;
                    }
                } else {
                    if (tank2Pos != RIGHT_EDGE && tank2Pos != tank1Pos - 1 && movesMade < 1) {
                        tank2.move(1);
                        tank2Pos++;
                        movesMade++;
                    }
                }
            }
        } else if (keys['a']) {
            if (!shootingPhase) {
                if (p1Turn) {
                    if (tank1Pos != LEFT_EDGE && tank1Pos != tank2Pos + 1 && movesMade > -1) {
                        tank1.move(-1);
                        tank1Pos--;
                        movesMade--;
                    }
                } else {
                    if (tank2Pos != LEFT_EDGE && tank2Pos != tank1Pos + 1 && movesMade > -1) {
                        tank2.move(-1);
                        tank2Pos--;
                        movesMade--;
                    }
                }
            }
        } else if (keys[' ']) {
            if (!shootingPhase) {
                movesMade = 0;
                shootingPhase = true;
            } else {
                shootingPhase = false;
            }
        } else if (keys['p']) {
            singlePlayer = !singlePlayer;
            newGame();
        } else if (keys['r']) {
            if (win) {
                win = false;
                newGame();
            }
        } else {
            throw new ArrayIndexOutOfBoundsException(key);
        }
    }

    public void keyReleased() {
        keys[key] = false;
    }

    public void mousePressed() {
        if (shootingPhase) {
            drawingLine = true;

            if (p1Turn) {
                xStart = tank1.getCurrentX() + tankWidth / 2;
                yStart = tank1.getCurrentY() + (tankHeight / 2);
            } else {
                xStart = tank2.getCurrentX() + (tankWidth / 2);
                yStart = tank2.getCurrentY() + (tankHeight / 2);
            }

            loc = new PVector(xStart, yStart);
        }
    }

    public void mouseReleased() {
        if (shootingPhase) {
            xEnd = mouseX;
            yEnd = mouseY;

            dir = new PVector((xEnd - xStart), (yEnd - yStart));

            drawingLine = false;
            fired = true;
        }
    }

    public void updateDir() {
        acc.add(grav);
        acc.add(wind);

        float airResX = 0;
        float airResY = 0;
        float dirX = loc.x - dir.x;
        float dirY = loc.y - dir.y;

        if (dirX < 0) {
            airResX = -0.01f;
        } else if (dirX > 0) {
            airResX = 0.01f;
        }

        if (dirY < 0) {
            airResY = -0.01f;
        } else if (dirY > 0) {
            airResY = 0.01f;
        }

        PVector airRes = new PVector(airResX, airResY);
        dir.add(airRes);
        dir.add(acc);

        dirNorm = dir.get();
        dirNorm.normalize();
    }

    public void detectCollision() {
        int blockWidth = map.getBLOCK_WIDTH();
        int blockHeight = map.getBLOCK_HEIGHT();
        ArrayList<MapBlock> blocks = map.getBlocks();

        boolean collision = false;
        boolean hit = false;

        //Hits map blocks
        for (int i = 0; i < blocks.size(); i++) {
            MapBlock block = blocks.get(i);

            if ((loc.x > block.x && loc.x < block.x + blockWidth) &&
                    (loc.y > block.y && loc.y < block.y + blockHeight)) {
                collision = true;
                map.collapseColumn(i);
                break;
            }
        }

        //Hits edges
        if (loc.x < 0 || loc.x > width) {
            collision = true;
        }

        //Hits floor
        if (loc.y > height) {
            collision = true;
        }

        //Friendly fire
        if (p1Turn) {
            if (!((loc.x > tank1.getCurrentX() && loc.x < tank1.getCurrentX() + tankWidth) &&
                    (loc.y > tank1.getCurrentY() && loc.y < tank1.getCurrentY() + tankHeight))) {
                if (!outOfTank) {
                    outOfTank = true;
                }
            } else {
                if (outOfTank) {
                    p2Score++;
                    collision = true;
                    hit = true;
                }
            }
        } else {
            if (!((loc.x > tank2.getCurrentX() && loc.x < tank2.getCurrentX() + tankWidth) &&
                    (loc.y > tank2.getCurrentY() && loc.y < tank2.getCurrentY() + tankHeight))) {
                if (!outOfTank) {
                    outOfTank = true;
                }
            } else {
                if (outOfTank) {
                    p1Score++;
                    collision = true;
                    hit = true;
                }
            }
        }

        //Hit opponent
        if (p1Turn) {
            if ((loc.x > tank2.getCurrentX() && loc.x < tank2.getCurrentX() + tankWidth) &&
                    (loc.y > tank2.getCurrentY() && loc.y < tank2.getCurrentY() + tankHeight)) {
                p1Score++;
                collision = true;
                hit = true;
            }
        } else {
            if ((loc.x > tank1.getCurrentX() && loc.x < tank1.getCurrentX() + tankWidth) &&
                    (loc.y > tank1.getCurrentY() && loc.y < tank1.getCurrentY() + tankHeight)) {
                p2Score++;
                collision = true;
                hit = true;
            }
        }

        if (collision) {
            shootingPhase = false;
            fired = false;
            outOfTank = false;
            tank1.move(0);
            tank2.move(0);
            setWind();
            acc = new PVector(0,0);


            if (hit) {
                if (p1Score == MAX_SCORE || p2Score == MAX_SCORE) {
                    win = true;
                } else {
                    reset();
                }
            }

            if (!win) {
                if (p1Turn && singlePlayer) {
                    ai.checkToMove(loc, tank2);
                    goAI();
                } else if (!p1Turn && singlePlayer) {
                    ai.updateShotParameters(loc, tank1);
                }
            }

            p1Turn = !p1Turn;
        }
    }

    public void setWind() {
        Random r = new Random();
        int chanceToChange = r.nextInt(5);

        if (chanceToChange > 1) {
            float windPower = r.nextFloat() / 100;
            int coinFlip = r.nextInt(2);

            if (coinFlip == 1) {
                windPower *= -1;
            }

            wind = new PVector(windPower, 0);
        } else if (chanceToChange == 1){
            wind = new PVector(0, 0);
        }
    }

    public void reset() {
        map.buildMap();
        tank1.moveToStart();
        tank2.moveToStart();
        tank1Pos = LEFT_EDGE;
        tank2Pos = RIGHT_EDGE;
        wind = new PVector(0,0);
        if (singlePlayer) {
            ai.resetAI();
        }
    }

    public void newGame() {
        p1Score = 0;
        p2Score = 0;
        p1Turn = true;
        reset();
    }

    public void goAI() {
        xStart = tank2.getCurrentX() + tankWidth / 2;
        yStart = tank2.getCurrentY() + tankHeight / 2;

        loc = new PVector(xStart, yStart);

        dir = ai.makeShot();

        fired = true;
    }

    public static void main(String[] args) {
        String[] processingArgs = {"Main"};
        MainSketch main = new MainSketch();
        PApplet.runSketch(processingArgs, main);
    }
}
