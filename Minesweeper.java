import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Minesweeper extends JFrame implements MouseListener {
    JToggleButton[][] buttons;
    JPanel buttonPanel;
    int numMines = 10;
    boolean firstClick = true;
    boolean gameOver;
    ImageIcon[] numbers = new ImageIcon[8];
    ImageIcon flag, mine, smile, win, wait, dead;
    int selectedCount = 0;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem beginner, intermediate, expert;
    JButton reset;
    //JLabel time;
    Timer timer;
    int timePassed = 0;
    JTextField timeField;
    GraphicsEnvironment ge;
    Font clockFont;

    public Minesweeper() {

        setGrid(9, 9, 10);

        for (int x = 0; x < 8; x++) {
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

        try {
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            clockFont = Font.createFont(Font.TRUETYPE_FONT, new File("digital-7.ttf"));
            ge.registerFont(clockFont);
        } catch (IOException|FontFormatException e) {
        }

        timer = new Timer();
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
    }

    public void setGrid(int rows, int cols, int numOfMines) {
        if (buttonPanel != null) {
            this.remove(buttonPanel);
            this.remove(menuBar);
        }
            
        gameOver = false;
        numMines = numOfMines;
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(rows, cols));
        buttons = new JToggleButton[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                buttons[r][c] = new JToggleButton();
                buttons[r][c].putClientProperty("row", r);
                buttons[r][c].putClientProperty("col", c);
                buttons[r][c].putClientProperty("state", 0);
                buttons[r][c].addMouseListener(this);
                buttonPanel.add(buttons[r][c]);
            }
        }

        menuBar = new JMenuBar();
        menu = new JMenu("Menu");

        timeField = new JTextField("   " + timePassed);
        //timeField.setFont(clockFont.deriveFont(18f));

        timeField.setBackground(Color.BLACK);
	    timeField.setForeground(Color.GREEN);

        beginner = new JMenuItem("Beginner");
        intermediate = new JMenuItem("Intermediate");
        expert = new JMenuItem("Expert");

        beginner.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setGrid(9, 9, 10);
                firstClick = true;
                selectedCount = 0;
                if(timer != null) {
                    timer.cancel();
                    timer = new Timer();
                }
                timePassed = 0;
	            timeField.setText("   " + timePassed);
            }
        });

        intermediate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setGrid(16, 16, 40);
                firstClick = true;
                selectedCount = 0;
                if(timer != null) {
                    timer.cancel();
                    timer = new Timer();
                }
                timePassed = 0;
	            timeField.setText("   " + timePassed);
            }
        });

        expert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setGrid(16, 40, 99);
                firstClick = true;
                selectedCount = 0;
                if(timer != null) {
                    timer.cancel();
                    timer = new Timer();
                }
                timePassed = 0;
	            timeField.setText("   " + timePassed);
            }
        });

        //time = new JLabel();

        menu.add(beginner);
        menu.add(intermediate);
        menu.add(expert);
        menuBar.add(menu);
        menuBar.add(timeField);
        
        this.add(menuBar, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.CENTER);
        this.setSize(cols * 50, rows * 50);
        this.revalidate();
        
    }

    public class UpdateTimer extends TimerTask {
		public void run() {
			if(!gameOver){
				timePassed++;
				timeField.setText("  " + timePassed);
			}
		}
	}

    public void disableButtons() {
        for (int r = 0; r < buttons.length; r++) {
            for (int c = 0; c < buttons[0].length; c++) {
                buttons[r][c].setEnabled(false);
                if ((int) buttons[r][c].getClientProperty("state") == -1) {
                    buttons[r][c].setIcon(mine);
                    buttons[r][c].setDisabledIcon(mine);
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int rowClicked = (int) ((JToggleButton) e.getComponent()).getClientProperty("row");
        int colClicked = (int) ((JToggleButton) e.getComponent()).getClientProperty("col");

        if (!gameOver) {
            if (e.getButton() == MouseEvent.BUTTON1 && buttons[rowClicked][colClicked].isEnabled()) {
                if (firstClick) {
                    dropMines(rowClicked, colClicked);
                    firstClick = false;
                    timer.schedule(new UpdateTimer(),0,1000);
                }
                int state = (int) (buttons[rowClicked][colClicked].getClientProperty("state"));
                if (state == -1) {
                    gameOver = true;
                    buttons[rowClicked][colClicked].setIcon(mine);
                    disableButtons();
                    timer.cancel();
                    JOptionPane.showMessageDialog(null, "You suck");
                } else {
                    if(buttons[rowClicked][colClicked].isEnabled()) {
                        selectedCount++;
                        buttons[rowClicked][colClicked].setEnabled(false);
                    } else {
                        selectedCount--;
                    }
                    
                    expand(rowClicked, colClicked);
                    if (selectedCount == buttons.length * buttons[0].length - numMines) {
                        disableButtons();
                        gameOver = true;
                        timer.cancel();
                        JOptionPane.showMessageDialog(null, "You won wow");
                    }
                }
            }

            if (e.getButton() == MouseEvent.BUTTON3) {

                try {
                    if (buttons[rowClicked][colClicked].isEnabled()
                            && buttons[rowClicked][colClicked].getIcon() == null) {
                        buttons[rowClicked][colClicked].setIcon(flag);
                        buttons[rowClicked][colClicked].setDisabledIcon(flag);
                        buttons[rowClicked][colClicked].setEnabled(false);

                    } else if (buttons[rowClicked][colClicked].getIcon().equals(flag)) {
                        buttons[rowClicked][colClicked].setIcon(null);
                        buttons[rowClicked][colClicked].setDisabledIcon(null);
                        buttons[rowClicked][colClicked].setEnabled(true);
                    }

                } catch (NullPointerException n) {

                }
            }

            
        }

    }

    public void dropMines(int row, int col) {
        int count = numMines;
        while (count > 0) {
            int r = (int) (Math.random() * buttons.length);
            int c = (int) (Math.random() * buttons[0].length);
            
            int state = (int) buttons[r][c].getClientProperty("state");
            if ((Math.abs(r - row) > 1 || Math.abs(c - col) > 1) && state != -1) {
                buttons[r][c].putClientProperty("state", -1);

                for (int i = r - 1; i <= r + 1; i++) {
                    for (int j = c - 1; j <= c + 1; j++) {
                        try {
                            int s = (int) buttons[i][j].getClientProperty("state");
                            if (s != -1) {
                                buttons[i][j].putClientProperty("state", s + 1);
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {

                        }
                    }
                }

                count--;
            }
        }

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {

                int s = (int) buttons[i][j].getClientProperty("state");
                // buttons[i][j].setText(s + "");

            }
        }

    }

    public void expand(int row, int col) {
        if (!buttons[row][col].isSelected()) {
            buttons[row][col].setSelected(true);
            buttons[row][col].setEnabled(false);
            selectedCount++;
        }
        int state = (int) (buttons[row][col].getClientProperty("state"));
        if (state > 0) {
            buttons[row][col].setIcon(numbers[state - 1]);
            buttons[row][col].setDisabledIcon(numbers[state - 1]);
        } else {
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    try {
                        if (!buttons[i][j].isSelected()) {
                            expand(i, j);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {

                    }

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

    public static void main(String[] args) {
        Minesweeper minesweeper = new Minesweeper();
    }

}
