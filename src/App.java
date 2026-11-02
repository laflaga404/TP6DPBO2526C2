import javax.swing.*;
import java.awt.*;

public class App {

    // frame dan cardlayout
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // game object
    private Logic logic;
    private View gameView;

    // nama setiap halaman
    private final String MENU = "MENU";
    private final String GAME = "GAME";

    // constructor
    public App() {
        frame = new JFrame("FlappyBird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // membuat CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // menambahkan main menu
        mainPanel.add(createMenuPanel(), MENU);

        // mengatur JFrame
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // menampilkan main menu pertama kali
        cardLayout.show(mainPanel, MENU);
    }

    // membuat main menu
    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(360, 640));
        menuPanel.setBackground(new Color(20, 35, 65));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        // title
        JLabel titleLabel = new JLabel("FLAPPY BIRD");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // subtitle
        JLabel subtitleLabel = new JLabel("Manuk israel!");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // start button
        JButton startButton = new JButton("START");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setPreferredSize(new Dimension(180, 50));
        startButton.setMaximumSize(new Dimension(180, 50));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);

        // aksi tombol start
        startButton.addActionListener(e -> startGame());

        // exit button
        JButton exitButton = new JButton("EXIT");
        exitButton.setFont(new Font("Arial", Font.BOLD, 20));
        exitButton.setPreferredSize(new Dimension(180, 50));
        exitButton.setMaximumSize(new Dimension(180, 50));
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);

        // aksi tombol exit
        exitButton.addActionListener(e -> {
            if (logic != null) {
                logic.closeAudio();
            }

            frame.dispose();
            System.exit(0);
        });

        // menyusun komponen menu
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(titleLabel);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(subtitleLabel);
        menuPanel.add(Box.createVerticalStrut(80));
        menuPanel.add(startButton);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(exitButton);
        menuPanel.add(Box.createVerticalGlue());

        return menuPanel;
    }

    // memulai game
// memulai game
    private void startGame() {

        // membuat game hanya ketika start ditekan
        if (logic == null) {

            // membuat Logic
            logic = new Logic();

            // membuat View dan menghubungkan tombol main menu
            gameView = new View(logic, () ->
                    cardLayout.show(mainPanel, MENU)
            );

            // menghubungkan Logic dengan View
            logic.setView(gameView);

            // menambahkan game ke CardLayout
            mainPanel.add(gameView, GAME);

        } else if (logic.isGameOver()) {

            // reset game ketika memulai kembali dari main menu
            logic.restartGame();
        }

        // berpindah ke tampilan game
        cardLayout.show(mainPanel, GAME);

        // meminta fokus keyboard
        SwingUtilities.invokeLater(() ->
                gameView.requestFocusInWindow()
        );
    }

    // main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App());
    }
}
