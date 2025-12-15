package ui;

import Logic.FileHandler;
import Model.PlayerData;
import java.awt.*;
import java.io.InputStream;
import javax.swing.*;

public class PlayerStats extends JPanel {
    private PlayerData playerData;
    private Font eightBit;
    private final JButton backButton;

    public PlayerStats(Runnable onBack) {
        setBackground(new Color(0x1a1a2e));
        setLayout(null);
        
        loadFont();
        loadPlayerData();
        
        backButton = makeButton("BACK");
        backButton.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });
        add(backButton);
    }
    
    private void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/8bit.ttf");
            if (is != null) {
                eightBit = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(18f);
            } else {
                eightBit = new Font("Monospaced", Font.BOLD, 18);
            }
        } catch (Exception e) {
            eightBit = new Font("Monospaced", Font.BOLD, 18);
        }
    }
    
    private void loadPlayerData() {
        playerData = FileHandler.loadPlayerData();
        
        // If no save file exists, create default data
        if (playerData == null) {
            playerData = new PlayerData();
            playerData.playerName = "No Save Data";
            playerData.starterPokemonId = 0;
            playerData.currentStage = 0;
            playerData.score = 0;
        }
    }
    
    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0xD32F2F));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(eightBit.deriveFont(14f));
        return b;
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        layoutButtons();
    }
    
    private void layoutButtons() {
        int btnW = 180;
        int btnH = 40;
        int x = (getWidth() - btnW) / 2;
        int y = getHeight() - 80;
        backButton.setBounds(x, y, btnW, btnH);
    }
    
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // Background gradient
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(0x1a1a2e),
            0, h, new Color(0x16213e)
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, w, h);
        
        // Title
        g.setFont(eightBit.deriveFont(Font.BOLD, 48f));
        g.setColor(new Color(0x00d4ff));
        String title = "PLAYER STATS";
        FontMetrics titleFm = g.getFontMetrics();
        int titleX = (w - titleFm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 80);
        
        // Decorative line
        g.setColor(new Color(0x00d4ff));
        g.fillRect(w / 4, 100, w / 2, 3);
        
        // Display stats
        if (playerData != null) {
            int startY = 160;
            int lineHeight = 60;
            
            String[][] stats = {
                {"PLAYER NAME", playerData.playerName},
                {"STARTER POKEMON", getStarterName(playerData.starterPokemonId)},
                {"CURRENT STAGE", String.valueOf(playerData.currentStage)},
                {"UNLOCKED STAGES", String.valueOf(playerData.unlockedStages.size())},
                {"UNIQUE POKEMON CAUGHT", String.valueOf(playerData.caughtPokemonIds.size())},
                {"TOTAL SCORE", String.valueOf(playerData.score)}
            };
            
            for (int i = 0; i < stats.length; i++) {
                drawStatLine(g, stats[i][0], stats[i][1], startY + i * lineHeight, w);
            }
        }
        
        g.dispose();
    }
    
    private void drawStatLine(Graphics2D g, String label, String value, int y, int w) {
        int boxW = (int)(w * 0.7);
        int boxH = 50;
        int boxX = (w - boxW) / 2;
        
        // Box background
        g.setColor(new Color(0x0f3460));
        g.fillRoundRect(boxX, y - 35, boxW, boxH, 15, 15);
        
        // Box border
        g.setColor(new Color(0x00d4ff));
        g.drawRoundRect(boxX, y - 35, boxW, boxH, 15, 15);
        
        // Label
        g.setFont(eightBit.deriveFont(Font.BOLD, 16f));
        g.setColor(new Color(0x00d4ff));
        g.drawString(label, boxX + 20, y - 10);
        
        // Value
        g.setFont(eightBit.deriveFont(Font.PLAIN, 20f));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int valueX = boxX + boxW - fm.stringWidth(value) - 20;
        g.drawString(value, valueX, y - 10);
    }
    
    private String getStarterName(int id) {
        switch (id) {
            case 1: return "Bulbasaur";
            case 4: return "Charmander";
            case 7: return "Squirtle";
            case 25: return "Pikachu";
            default: return id == 0 ? "None" : "Pokemon #" + id;
        }
    }
}