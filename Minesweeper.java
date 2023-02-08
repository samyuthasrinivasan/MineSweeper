import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
public class Minesweeper extends JFrame implements MouseListener {
    JToggleButton [] [] buttons;
    JPanel buttonPanel;
    int numMines = 10;
    boolean firstClick = true;
    boolean gameOver;
    ImageIcon[] numbers = new ImageIcon[8];
    ImageIcon flag, mine, smile, win, wait, dead;

    public Minesweeper() {
        setGrid(9,9);

        for(int x = 0; x < 8; x++) {
            numbers[x] = new ImageIcon((x + 1) + ".png");
            numbers[x] = new ImageIcon(numbers[x].getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        }

        flag = new ImageIcon("flag2.png");
        flag = new ImageIcon(flag.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        mine = new ImageIcon("mine0.png");
        mine = new ImageIcon(mine.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        smile = new ImageIcon("smile1.png");
        smile = new ImageIcon(smile.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        win = new ImageIcon("win1.png");
        win = new ImageIcon(win.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        wait = new ImageIcon("wait1.png");
        wait = new ImageIcon(wait.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        dead = new ImageIcon("dead1.png");
        dead = new ImageIcon(dead.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void setGrid(int rows, int cols) {
        
        if(buttonPanel != null) 
            this.remove(buttonPanel);

        gameOver = false;
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(rows, cols));
        buttons = new JToggleButton[rows][cols];
        
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                buttons[r][c] = new JToggleButton();
                buttons[r][c].putClientProperty("row", r);
                buttons[r][c].putClientProperty("col", c);
                buttons[r][c].putClientProperty("state", 0);
                buttons[r][c].addMouseListener(this);
                buttonPanel.add(buttons[r][c]);
            }
        }
        this.add(buttonPanel, BorderLayout.CENTER);
        this.setSize(cols*50, rows*50);
        this.revalidate();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int rowClicked = (int)((JToggleButton)e.getComponent()).getClientProperty("row");
        int colClicked = (int)((JToggleButton)e.getComponent()).getClientProperty("col");

        if (!gameOver) {
            if(e.getButton() == MouseEvent.BUTTON1) {
                if(firstClick) {
                dropMines(rowClicked, colClicked);
                firstClick = false;
                } 
                int state = (int)(buttons[rowClicked][colClicked].getClientProperty("state"));
                if(state == -1) {
                    gameOver = true;
                    buttons[rowClicked][colClicked].setIcon(mine);
                    JOptionPane.showMessageDialog(null, "You suck");
                } else {
                    expand(rowClicked, colClicked);
                }
            } 

            if(e.getButton() == MouseEvent.BUTTON3) {
                if(!buttons[rowClicked][colClicked].isSelected()) {
                    buttons[rowClicked][colClicked].setIcon(flag);
                    buttons[rowClicked][colClicked].setDisabledIcon(flag);
                    buttons[rowClicked][colClicked].setEnabled(false);
                } else {
                    buttons[rowClicked][colClicked].setEnabled(true);
                    buttons[rowClicked][colClicked].setIcon(null);
                }
            } 
        }
        
    }

    public void dropMines(int row, int col) {
        int count = numMines;
        while(count > 0){
            int r = (int)(Math.random()*buttons.length);
            int c = (int)(Math.random()*buttons[0].length);
            int state = (int)buttons[r][c].getClientProperty("state");
            if(Math.abs(r - row) > 1 || Math.abs(c - col) > 1 && state == 0) {
                buttons[r][c].putClientProperty("state", -1);
                
                for(int i = r - 1; i <= r + 1; i++) {
                    for(int j = c - 1; j <= c + 1; j++) {
                        try{
                            int s = (int) buttons[i][j].getClientProperty("state");
                            if(s != -1) {  
                                buttons[i][j].putClientProperty("state", s + 1);
                            }
                        } catch(ArrayIndexOutOfBoundsException e) {
                            
                        }
                    }
                }

                count--;
            } 
        }
        
    }

    public void expand(int row, int col) {
        if(!buttons[row][col].isSelected()) {
            buttons[row][col].setSelected(true);
        }
        int state = (int)(buttons[row][col].getClientProperty("state"));
        if(state > 0)
        buttons[row][col].setIcon(numbers[state - 1]);
        else {
            for(int i = row - 1; i <= row + 1; i++) {
                for(int j = col - 1; j <= col + 1; j++) {
                    try{
                        if(!buttons[i][j].isSelected()) {
                            expand(i, j);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                       
                    }
                    
                    /* 
                    else if(state1 != -1) {
                        try {
                            buttons[i][j].setText("" + state1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                    */ 
                }
            }
        }


    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }
    @Override
    public void mousePressed(MouseEvent e) {
        
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        
    }
    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    public static void main(String[]args) {
        Minesweeper minesweeper = new Minesweeper();
    }

}