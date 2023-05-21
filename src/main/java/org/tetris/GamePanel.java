package org.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private static final int WIN_HEIGHT = 600;
    private static final int WIN_WIDTH = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS_MAX_COUNT = (WIN_HEIGHT * WIN_WIDTH) / UNIT_SIZE;
    private static final int DELAY = 75;
    private boolean isRunning = false;
    private Timer timer = new Timer(DELAY, this);
    private Random random = new Random();
    private static int FIGURE_SIZE = 4;
    int[][] figure = new int[FIGURE_SIZE][FIGURE_SIZE];
    int[] figureX = new int[FIGURE_SIZE];
    int[] figureY = new int[FIGURE_SIZE];
    GamePanel() {
        this.setPreferredSize(new Dimension(WIN_WIDTH, WIN_HEIGHT));
        this.setFocusable(true);
        this.setBackground(Color.BLACK);
        startGame();
    }

    private void startGame() {
        isRunning = true;
        createFigure();

        timer.start();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        draw(graphics);
    }

    private void draw(Graphics graphics) {
        for (int i = 0; i < WIN_WIDTH / UNIT_SIZE; i++) {
            graphics.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, WIN_HEIGHT);
            graphics.drawLine(0, i * UNIT_SIZE, WIN_WIDTH, i * UNIT_SIZE);
        }
        graphics.setColor(Color.RED);
        for (int i = 0; i < FIGURE_SIZE; i++) {
            graphics.fillRect(figureX[i], figureY[i], UNIT_SIZE, UNIT_SIZE);
        }
//        graphics.fillRect(random.nextInt(300) + 1, random.nextInt(550) + 4, 20, 50);
    }

    private void createFigure() {
        Arrays.fill(figureX, -1);
        Arrays.fill(figureY, -1);
        int figureLength = random.nextInt(FIGURE_SIZE) + 1;
        if (figureLength != FIGURE_SIZE) {
            for (int i = 0; i < figureLength; i++) {
                figureX[i] = i * UNIT_SIZE;
                figureY[i] = 0;
            }
            // TODO не работает генерация квадрата и зигзага
//
//            if (figureLength == 2) {
//                int columnCount = random.nextInt(3) + 1;
//                if (columnCount == 2) {
//                    figureX[2] = figureX[0] + UNIT_SIZE;
//                    figureY[2] = figureY[0] + UNIT_SIZE;
//                    figureX[3] = figureX[1] + UNIT_SIZE;
//                    figureY[3] = figureY[1] + UNIT_SIZE;
//                    return;
//                }
//            }
            int figureColumnPosition = random.nextInt(figureLength);
            int columnLength = FIGURE_SIZE - figureLength;

            int columnCounter = 1;
            for (int i = figureLength; i < FIGURE_SIZE; i ++) {
                figureX[i] = figureColumnPosition * UNIT_SIZE;
                figureY[i] = columnCounter * UNIT_SIZE;
                columnCounter++;
            }
        } else {
            for (int i = 0; i < FIGURE_SIZE; i++) {
                figureX[i] = i * UNIT_SIZE;
                figureY[i] = 0;
            }
        }
        System.out.println(figureLength);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isRunning) {
            moveFigure();
        }
        repaint();
    }

    private void moveFigure() {
        for (int i = 0; i < FIGURE_SIZE; i++) {
            figureY[i] = figureY[i] + UNIT_SIZE;
        }
    }

    public class UserKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {

        }
    }
}
