import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Logic implements ActionListener, KeyListener {

    // ukuran frame
    int frameWidth = 360;
    int frameHeight = 640;

    // posisi dan ukuran player
    int playerStartPosX = frameWidth / 2;
    int playerStartPosY = frameHeight / 2;

    int playerWidth = 34;
    int playerHeight = 24;

    Image birdImage;
    Player player;

    // posisi dan ukuran pipe
    int pipeStartPosX = frameWidth;
    int pipeWidth = 64;
    int pipeHeight = 512;
    int pipeVelocityX = -2;

    Image lowerPipeImage;
    Image upperPipeImage;

    ArrayList<Pipe> pipes;

    // timer dan gravitasi
    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;

    // status permainan
    boolean gameOver = false;
    int score = 0;

    // atribut pendukung
    View view;
    SoundManager soundManager;

    // constructor
    public Logic() {

        // load gambar burung
        birdImage = new ImageIcon(
                getClass().getResource("/assets/bird.png")
        ).getImage();

        // membuat objek Player
        player = new Player(
                playerStartPosX,
                playerStartPosY,
                playerWidth,
                playerHeight,
                birdImage
        );

        // load gambar pipa bawah
        lowerPipeImage = new ImageIcon(
                getClass().getResource("/assets/lowerPipe.png")
        ).getImage();

        // load gambar pipa atas
        upperPipeImage = new ImageIcon(
                getClass().getResource("/assets/upperPipe.png")
        ).getImage();

        // inisialisasi ArrayList Pipe
        pipes = new ArrayList<>();

        // inisialisasi Sound Manager
        soundManager = new SoundManager();

        // memulai background music
        soundManager.playBgm();

        // timer spawning pipe
        pipesCooldown = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    placePipes();

                    if (view != null) {
                        view.repaint();
                    }
                }
            }
        });

        pipesCooldown.setInitialDelay(800);
        pipesCooldown.start();

        // game loop
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    // getter dan setter
    public void setView(View view) {
        this.view = view;

        if (view != null) {
            view.updateScore(score);
        }
    }

    // getter player
    public Player getPlayer() {
        return player;
    }

    // getter pipe
    public ArrayList<Pipe> getPipes() {
        return pipes;
    }

    // getter game over
    public boolean isGameOver() {
        return gameOver;
    }

    // getter score
    public int getScore() {
        return score;
    }

    // spawning pipe
    public void placePipes() {

        // jarak antara pipa atas dan bawah
        int openingSpace = 160;

        // batas posisi celah
        int minOpeningTop = 180;
        int maxOpeningTop = 320;

        // posisi ujung bawah pipa atas secara acak
        int upperPipeBottom = minOpeningTop
                + (int) (
                Math.random() * (maxOpeningTop - minOpeningTop + 1)
        );

        // posisi Y pipa atas
        int upperPipeY = upperPipeBottom - pipeHeight;

        // posisi Y pipa bawah
        int lowerPipeY = upperPipeBottom + openingSpace;

        // membuat pipa atas
        Pipe upperPipe = new Pipe(
                pipeStartPosX,
                upperPipeY,
                pipeWidth,
                pipeHeight,
                upperPipeImage
        );

        pipes.add(upperPipe);

        // membuat pipa bawah
        Pipe lowerPipe = new Pipe(
                pipeStartPosX,
                lowerPipeY,
                pipeWidth,
                pipeHeight,
                lowerPipeImage
        );

        pipes.add(lowerPipe);
    }

    // movement player dan pipe
    public void move() {

        // movement player
        player.setVelocityY(
                player.getVelocityY() + gravity
        );

        // memperbarui posisi player
        player.setPosY(
                player.getPosY() + player.getVelocityY()
        );

        // membatasi posisi player pada batas atas
        player.setPosY(
                Math.max(player.getPosY(), 0)
        );

        // movement pipe
        for (int i = pipes.size() - 1; i >= 0; i--) {
            Pipe pipe = pipes.get(i);

            // menggerakkan Pipe ke kiri
            pipe.setPosX(
                    pipe.getPosX() + pipeVelocityX
            );

            // menghapus Pipe yang keluar layar
            if (pipe.getPosX() + pipe.getWidth() < 0) {
                pipes.remove(i);
            }
        }
    }

    // collision detection
    public void checkCollision() {

        // membuat batas area Player
        Rectangle playerBounds = new Rectangle(
                player.getPosX(),
                player.getPosY(),
                player.getWidth(),
                player.getHeight()
        );

        // cek batas bawah frame
        if (player.getPosY() + player.getHeight() >= frameHeight) {
            endGame();
            return;
        }

        // cek tabrakan dengan pipe
        for (Pipe pipe : pipes) {
            Rectangle pipeBounds = new Rectangle(
                    pipe.getPosX(),
                    pipe.getPosY(),
                    pipe.getWidth(),
                    pipe.getHeight()
            );

            if (playerBounds.intersects(pipeBounds)) {
                endGame();
                return;
            }
        }
    }

    // scoring system
    public void checkScore() {
        for (Pipe pipe : pipes) {

            // hanya menghitung pipa bawah
            if (pipe.getImage() == lowerPipeImage
                    && !pipe.isPassed()
                    && player.getPosX() > pipe.getPosX() + pipe.getWidth()) {

                // menandai pipa sudah dilewati
                pipe.setPassed(true);

                // menambahkan skor
                score++;
                soundManager.playScore();

                // update JLabel
                if (view != null) {
                    view.updateScore(score);
                }

                // menampilkan skor di console
                System.out.println("Score: " + score);
            }
        }
    }

    // game over
    public void endGame() {
        if (gameOver) {
            return;
        }

        // mengubah status permainan
        gameOver = true;

        // menghentikan game loop
        gameLoop.stop();

        // menghentikan spawning Pipe
        pipesCooldown.stop();

        // menghentikan BGM
        soundManager.stopBgm();

        // memainkan suara game over
        soundManager.playGameOver();

        // memperbarui tampilan
        if (view != null) {
            view.repaint();
        }
    }

    // restart game
    public void restartGame() {

        // reset status game over
        gameOver = false;

        // reset posisi Player
        player.setPosX(playerStartPosX);
        player.setPosY(playerStartPosY);

        // reset kecepatan Player
        player.setVelocityY(0);

        // menghapus seluruh Pipe lama
        pipes.clear();

        // reset skor
        score = 0;

        // update JLabel
        if (view != null) {
            view.updateScore(score);
        }

        // restart timer
        gameLoop.restart();
        pipesCooldown.restart();

        // memutar kembali background music
        soundManager.playBgm();

        // memperbarui tampilan
        if (view != null) {
            view.repaint();
            view.requestFocusInWindow();
        }
    }

    // action listener / game loop
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {

            // memperbarui posisi objek
            move();

            // memeriksa tabrakan
            checkCollision();

            // memeriksa skor jika belum game over
            if (!gameOver) {
                checkScore();
            }

            // memperbarui tampilan
            if (view != null) {
                view.repaint();
            }
        }
    }

    // key listener
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        // restart menggunakan tombol R
        if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
            return;
        }

        // player control menggunakan space
        if (!gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.setVelocityY(-10);
            soundManager.playFlap();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // menutup audio
    public void closeAudio() {
        if (soundManager != null) {
            soundManager.close();
        }
    }
}
