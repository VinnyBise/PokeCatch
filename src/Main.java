import javax.swing.*;
import Logic.GameState;
import Logic.Util;
import Model.Pokemon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    private static boolean isDialogShowing = false; // Prevent multiple dialogs
    
    public static void main(String[] args) {
        System.out.println("PokeCatch Game Starting...");

        // Launch intro and then run stage flow. All UI runs on EDT.
        Intro introScreen = new Intro();
        MusicPlayer music = new MusicPlayer();
        GameState gameState = GameState.getInstance();
        Util util = new Util();
        music.playLoop("/Music/pallet_town_theme.wav");

        introScreen.setStarterSelectionListener(starterId -> {
            // called on EDT when player selects starter
            System.out.println("Starter ID selected after intro: " + starterId);
            
            // Add starter to caught pokemon array
            Pokemon starter = util.getPokemonById(starterId);
            if (starter != null) {
                gameState.addCaughtPokemon(starter);
                System.out.println("Starter " + starter.name + " added to caught pokemon!");
            }
            
            music.stop();
            
            // start first stage sequence
            SwingUtilities.invokeLater(() -> startStageSequence(1));
        });

        introScreen.launchIntro();
    }

    // Orchestrates stages and pokedex viewing
    private static void startStageSequence(int startStage) {
        // Create and show stage, passing callback for when stage ends
        new View.StageWindow(startStage, clearedStage -> {
            // Show options dialog on EDT after stage clears
            SwingUtilities.invokeLater(() -> showPostStageOptions(clearedStage));
        });
    }

    private static void showPostStageOptions(int lastStage) {
        // Prevent multiple dialogs from showing at once
        if (isDialogShowing) {
            System.out.println("Dialog already showing, ignoring duplicate call");
            return;
        }
        
        isDialogShowing = true;
        System.out.println("showPostStageOptions called for stage: " + lastStage);
        
        String[] options = {"Next Stage", "View Pokedex", "Exit"};
        
        int choice = JOptionPane.showOptionDialog(null,
                "Stage " + lastStage + " cleared. What would you like to do next?",
                "Stage Complete",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        System.out.println("User selected choice: " + choice);
        isDialogShowing = false; // Reset flag after dialog closes

        // Handle choice based on result
        if (choice == 0) {
            // Next Stage
            startStageSequence(lastStage + 1);
        } else if (choice == 1) {
            // View Pokedex
            System.out.println("Opening Pokedex...");
            View.PokedexFrame pokedexFrame = new View.PokedexFrame();
            
            pokedexFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    System.out.println("Pokedex closed, showing options again...");
                    SwingUtilities.invokeLater(() -> showPostStageOptions(lastStage));
                }
            });
        } else {
            // Exit
            System.exit(0);
        }
    }
}