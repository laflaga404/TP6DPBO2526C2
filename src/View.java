import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class View extends JPanel {

    // ukuran canvas
    int width = 360;
    int height = 640;

    // logic dan score label
    private Logic logic;
    private JLabel scoreLabel;

    // callback kembali ke main menu
    private Runnable onBackToMenu;

    // area tombol game over
    private Rectangle restartButton = new Rectangle(75, 365, 210, 48);
    private Rectangle menuButton = new Rectangle(75, 425, 210, 48);

    // constructor
    public View(Logic logic, Runnable onBackToMenu) {
        this.logic = logic;
        this.onBackToMenu = onBackToMenu;

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.cyan);
        setLayout(null);

        // keyboard listener
        setFocusable(true);
        addKeyListener(logic);

        // mouse listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();

                // mendeteksi klik ketika game over
                if (logic.isGameOver() && SwingUtilities.isLeftMouseButton(e)) {

                    // restart menggunakan tombol
                    if (restartButton.contains(e.getPoint())) {
                        logic.restartGame();
                    }

                    // kembali ke main menu
                    else if (menuButton.contains(e.getPoint())) {
                        onBackToMenu.run();
                    }
                }
            }
        });

        // score label
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBackground(new Color(0, 0, 0, 150));
        scoreLabel.setOpaque(true);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setBounds(125, 15, 110, 35);

        add(scoreLabel);
    }

    // update score
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    // paint component
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // menggambar teks di tengah
    private void drawCenteredText(Graphics2D g2d, String text, int y) {
        FontMetrics metrics = g2d.getFontMetrics();

        int x = (width - metrics.stringWidth(text)) / 2;

        g2d.drawString(text, x, y);
    }

    // menggambar tombol game over
    private void drawButton(Graphics2D g2d, String text, Rectangle button, Color color) {

        // background tombol
        g2d.setColor(color);
        g2d.fillRoundRect(
                button.x,
                button.y,
                button.width,
                button.height,
                12,
                12
        );

        // teks tombol
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));

        FontMetrics metrics = g2d.getFontMetrics();

        int textX = button.x + (button.width - metrics.stringWidth(text)) / 2;
        int textY = button.y + (button.height + metrics.getAscent() - metrics.getDescent()) / 2;

        g2d.drawString(text, textX, textY);
    }

    // draw game objects
    public void draw(Graphics g) {

        // menggambar player
        Player player = logic.getPlayer();

        if (player != null) {
            g.drawImage(
                    player.getImage(),
                    player.getPosX(),
                    player.getPosY(),
                    player.getWidth(),
                    player.getHeight(),
                    null
            );
        }

        // menggambar pipe
        ArrayList<Pipe> pipes = logic.getPipes();

        if (pipes != null) {
            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i);

                g.drawImage(
                        pipe.getImage(),
                        pipe.getPosX(),
                        pipe.getPosY(),
                        pipe.getWidth(),
                        pipe.getHeight(),
                        null
                );
            }
        }

        // game over screen
        if (logic.isGameOver()) {
            Graphics2D g2d = (Graphics2D) g.create();

            // background transparan gelap
            g2d.setColor(new Color(0, 0, 0, 170));
            g2d.fillRect(0, 0, width, height);

            // judul game over
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            drawCenteredText(g2d, "GAME OVER", 245);

            // menampilkan skor akhir
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            drawCenteredText(g2d, "Final Score: " + logic.getScore(), 285);

            // informasi restart
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            drawCenteredText(g2d, "Tekan R untuk mengulang permainan", 325);

            // tombol restart
            drawButton(
                    g2d,
                    "RESTART (R)",
                    restartButton,
                    new Color(46, 204, 113)
            );

            // tombol main menu
            drawButton(
                    g2d,
                    "MAIN MENU",
                    menuButton,
                    new Color(231, 76, 60)
            );

            // membersihkan Graphics2D
            g2d.dispose();
        }
    }
}
