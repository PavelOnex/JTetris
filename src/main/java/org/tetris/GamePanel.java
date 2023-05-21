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
    private static final int DELAY = 175;
    private boolean isRunning = false;
    private int score = 0;
    private final Timer timer = new Timer(DELAY, this);
    private final Random random = new Random();
    private static final int FIGURE_SIZE = 4;
    int[] figureX = new int[FIGURE_SIZE];
    int[] figureY = new int[FIGURE_SIZE];
    int figureType = 0;
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

        filledFields.forEach(coordinates -> {
            Integer x = coordinates.get(0);
            Integer y = coordinates.get(1);
            graphics.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
        });
    }

    private void createFigure() {
        createFigureByType(random.nextInt(7));
        int randomShift = random.nextInt((WIN_WIDTH / UNIT_SIZE) - FIGURE_SIZE + 1) * UNIT_SIZE;
        for (int i = 0; i < FIGURE_SIZE; i++) {
            figureX[i] += randomShift;
        }
    }

    private void createFigureByType(int type) {

        if (type < 0) {
            type = 0;
        } else if (type > 7) {
            type = 7;
        }

        figureType = type;

        switch (type) {
            case 0: {
                for (int i = 0; i < FIGURE_SIZE; i++) {
                    figureX[i] = i * UNIT_SIZE;
                    figureY[i] = 0;
                }
                break;
            }

            case 1: {
                for (int i = 0; i < FIGURE_SIZE - 1; i++) {
                    figureX[i] = i * UNIT_SIZE;
                    figureY[i] = 0;
                }
                figureX[3] = 2 * UNIT_SIZE;
                figureY[3] = UNIT_SIZE;
                break;
            }

            case 2: {
                for (int i = 0; i < FIGURE_SIZE - 1; i++) {
                    figureX[i] = i * UNIT_SIZE;
                    figureY[i] = 0;
                }
                figureX[3] = UNIT_SIZE;
                figureY[3] = UNIT_SIZE;
                break;
            }

            case 3: {
                for (int i = 0; i < FIGURE_SIZE - 1; i++) {
                    figureX[i] = i * UNIT_SIZE;
                    figureY[i] = 0;
                }
                figureX[3] = 0;
                figureY[3] = UNIT_SIZE;
                break;
            }

            case 4: {
                for (int i = 0; i < FIGURE_SIZE - 2; i++) {
                    figureX[i] = (1 + i) * UNIT_SIZE;
                    figureY[i] = 0;
                }
                figureX[2] = 0;
                figureY[2] = UNIT_SIZE;
                figureX[3] = UNIT_SIZE;
                figureY[3] = UNIT_SIZE;
                break;
            }
            case 5: {
                for (int i = 0; i < FIGURE_SIZE - 2; i++) {
                    figureX[i] = i * UNIT_SIZE;
                    figureY[i] = 0;
                }
                figureX[3] = 2 * UNIT_SIZE;
                figureY[3] = UNIT_SIZE;
                figureX[2] = UNIT_SIZE;
                figureY[2] = UNIT_SIZE;
                break;
            }

            case 6: {
                for (int i = 0; i < FIGURE_SIZE - 2; i++) {
                    figureX[i] = i * UNIT_SIZE;
                    figureY[i] = 0;
                }
                figureX[3] = UNIT_SIZE;
                figureY[3] = UNIT_SIZE;
                figureX[2] = 0;
                figureY[2] = UNIT_SIZE;
                break;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isRunning) {
            moveFigureDown();
        } else {
            score = 0;
            filledFields = new HashSet<>();
            isRunning = true;
        }
        repaint();
    }

    private void checkBottom() {
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
                score += 1;
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
        if (isGameOver()) {
            JOptionPane.showMessageDialog(null, "Game over!\nYour score is: " + score);
        }
    }

    private boolean isGameOver() {
        for (ArrayList<Integer> figure : filledFields) {
            if (figure.get(1) == UNIT_SIZE) {
                isRunning = false;
                return true;
            }
        }
        return false;
    }

    private boolean checkLeft() {
        for (int i = 0; i < FIGURE_SIZE; i++) {
            if (figureX[i] <= 0)
                return false;

            for (ArrayList<Integer> figure : filledFields) {
                if (figure.get(0) + UNIT_SIZE == figureX[i]
                        && figure.get(1) == figureY[1]) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkRight() {
        for (int i = 0; i < FIGURE_SIZE; i++) {
            if (figureX[i] + UNIT_SIZE >= WIN_WIDTH)
                return false;

            for (ArrayList<Integer> figure : filledFields) {
                if (figure.get(0) == figureX[i] + UNIT_SIZE
                        && figure.get(1) == figureY[1]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void moveFigureDown() {
        for (int i = 0; i < FIGURE_SIZE; i++) {
            figureY[i] = figureY[i] + UNIT_SIZE;
        }
        checkBottom();
        checkAndRemoveRows();
    }
    private void moveFigureRight() {
        if (!checkRight()) return;
        for (int i = 0; i < FIGURE_SIZE; i++) {
            figureX[i] = figureX[i] + UNIT_SIZE;
        }
    }
    private void moveFigureLeft() {
        if (!checkLeft()) return;
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
                case KeyEvent.VK_SPACE:
                    if (isRunning)
                        timer.stop();
                    else timer.start();
                    isRunning = !isRunning;
                    break;
            }
            repaint();
        }
    }
}
