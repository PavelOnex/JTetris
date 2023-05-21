package org.tetris;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

public class GamePanel extends JPanel implements ActionListener {
    private static final int WIN_HEIGHT = 600;
    private static final int WIN_WIDTH = 200;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS_MAX_COUNT = (WIN_HEIGHT * WIN_WIDTH) / UNIT_SIZE;
    private static final int DELAY = 175;
    private boolean isRunning = false;
    private Timer timer = new Timer(DELAY, this);
    private Random random = new Random();
    private static int FIGURE_SIZE = 4;
    int[][] figure = new int[FIGURE_SIZE][FIGURE_SIZE];
    int[] figureX = new int[FIGURE_SIZE];
    int[] figureY = new int[FIGURE_SIZE];
    HashSet<ArrayList<Integer>> filledFields = new HashSet<>();


    GamePanel() {
        this.setPreferredSize(new Dimension(WIN_WIDTH, WIN_HEIGHT));
        this.setFocusable(true);
        this.setBackground(Color.BLACK);
        this.addKeyListener(new UserKeyAdapter());
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
        }
        for (int i = 0; i < WIN_HEIGHT / UNIT_SIZE; i++) {
            graphics.drawLine(0, i * UNIT_SIZE, WIN_WIDTH, i * UNIT_SIZE);
        }
        graphics.setColor(Color.RED);
        for (int i = 0; i < FIGURE_SIZE; i++) {
            graphics.fillRect(figureX[i], figureY[i], UNIT_SIZE, UNIT_SIZE);
        }

        filledFields.forEach(coordinats -> {
            Integer x = coordinats.get(0);
            Integer y = coordinats.get(1);
            graphics.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
        });
    }

    private void createFigure() {
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isRunning) {
            moveFigureDown();
        }
        repaint();
    }

    public void checkBottom() {
        for (int i = 0; i < FIGURE_SIZE; i++) {
            if (figureY[i] + UNIT_SIZE == WIN_HEIGHT) {
                addCurrentFigureToBottom();
                createFigure();
                return;
            }
            for (ArrayList<Integer> figure : filledFields) {
                if (figure.get(0) == figureX[i]
                        && figure.get(1) == (figureY[i] + UNIT_SIZE)) {
                    addCurrentFigureToBottom();
                    createFigure();
                    return;
                }
            }
        }
    }

    private void checkAndRemoveRows() {
        // Looking for filled rows
        HashMap<Integer, HashSet<Integer>> rowsFilled = new HashMap<>();
        filledFields.forEach(field -> {
            Integer y = field.get(1);
            HashSet<Integer> hashSet = rowsFilled.get(y);
            if (hashSet == null) {
                hashSet = new HashSet<>();
            }
            hashSet.add(field.get(0));
            rowsFilled.put(y, hashSet);
        });

        for (Map.Entry<Integer, HashSet<Integer>> entry : rowsFilled.entrySet()) {
            // if row is filled - removing it and shifting the others
            if (entry.getValue().size() == WIN_WIDTH / UNIT_SIZE) {
                HashSet<ArrayList<Integer>> recordsToRemove = new HashSet<>();
                HashSet<ArrayList<Integer>> recordsToInsertAfterChanging = new HashSet<>();
                filledFields.forEach(field -> {
                    if (field.get(1).equals(entry.getKey())) {
                        ArrayList<Integer> recordToRemove = new ArrayList<>();
                        recordToRemove.add(field.get(0));
                        recordToRemove.add(field.get(1));
                        recordsToRemove.add(recordToRemove);
                    } else if (field.get(1) < entry.getKey()) {
                        ArrayList<Integer> recordToRemove = new ArrayList<>();
                        recordToRemove.add(field.get(0));
                        recordToRemove.add(field.get(1));
                        recordsToRemove.add(recordToRemove);
                        ArrayList<Integer> recordToInsert = new ArrayList<>();
                        recordToInsert.add(field.get(0));
                        recordToInsert.add(field.get(1) + UNIT_SIZE);
                        recordsToInsertAfterChanging.add(recordToInsert);
                    }
                });
                filledFields.removeAll(recordsToRemove);
                filledFields.addAll(recordsToInsertAfterChanging);
            }
        }
    }


    private void addCurrentFigureToBottom() {
        for (int j = 0; j < FIGURE_SIZE; j++) {
            ArrayList<Integer> coordinates = new ArrayList<>();
            coordinates.add(figureX[j]);
            coordinates.add(figureY[j]);
            filledFields.add(coordinates);
        }
    }

    private void moveFigureDown() {
        for (int i = 0; i < FIGURE_SIZE; i++) {
            figureY[i] = figureY[i] + UNIT_SIZE;
        }
        checkBottom();
        checkAndRemoveRows();
    }
    private void moveFigureRight() {
        for (int i = 0; i < FIGURE_SIZE; i++) {
            figureX[i] = figureX[i] + UNIT_SIZE;
        }
    }
    private void moveFigureLeft() {
        for (int i = 0; i < FIGURE_SIZE; i++) {
            figureX[i] = figureX[i] - UNIT_SIZE;
        }
    }
    public class UserKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            switch(event.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    moveFigureLeft();
                    break;
                case KeyEvent.VK_RIGHT:
                    moveFigureRight();
                    break;
//                case KeyEvent.VK_UP:
//                    if(direction != 'D') {
//                        direction = 'U';
//                    }
//                    break;
                case KeyEvent.VK_DOWN:
                    moveFigureDown();
                    break;
            }
            repaint();
        }
    }
}
