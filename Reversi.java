import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.ArrayList;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 * Reversi class has the main role of creating the GUI elements which the user sees 
 * it also updates the scores , player names etc. The class also allows the creation
 * of different size Reversi boards to play and the ability to save and load txt files
 * of the saved games.
 * 
 *
 * @author DTK3 Daniel Knight
 * @version (version 7 29/04/18)
 */
public class Reversi
{
    //The JFrame for the entire Reversi game.
    private static JFrame frame;
    private Container content;
    //Private JPanels for the main elements like scores, board and status used in the Reversi class.
    private JPanel scores;
    private JPanel board;
    private JPanel status;
    //Status message for the current play etc.
    private static  JLabel statusMessage;
    //Player Names entered by the players.
    private static JTextField p1Name; 
    private static JTextField p2Name;
    //player 1's score for the session.
    private JLabel p1scoreName;
    private static JLabel p1Score;
    private static int p1ScoreInt;
    //Player 2's score for the session.
    private JLabel p2scoreName;
    private static JLabel p2Score;
    private static int p2ScoreInt;
    //CurrentDisks private incase of multiple instances of the game, static so it can be called by class Reversi.etc.....
    private static JLabel BDisks;
    private static JLabel WDisks;
    //JButton for starting the game 
    private static JButton startGame;
    //First time playing the game?
    private  boolean firstTimePlaying;

    //Boolean to only allow one instance of the GUI (wouldn't make sense to have multiple).
    public static boolean oneGameGUI;
    //The game board and actual Reversi game code itself not the GUI code.
    private static Board boards;
    //Border color for the GUI border selectable in the menu
    public static Color borderColor;

    /**
     * Constructor for objects of class Reversi which sets a lock 
     * on one GUI at any one time.
     */
    public Reversi()
    {     
        if(!(isActive())){
            //The GUI is going to be created.
            Reversi.oneGameGUI = true;  
            makeFrame();
            p1ScoreInt = 0;
            p2ScoreInt = 0;
            updateCount();
            //This is the firstTime playing this game.
            firstTimePlaying = true;
        }
        else{
            //A GUI already exists so don't create another.
            return;
        }
    }

    /**
     * Return whether tyhe GUI has already been created.
     * 
     * @return Whether the GUI has already been created
     */
    public boolean isActive()   
    {
        //Only one GUI as multiple doesn't make sense.
        return oneGameGUI;
    }

    /**
     * Return the JFrame for the GUI
     * 
     * @return The JFrame
     */
    public static JFrame getFrame()
    {
        return frame;
    }

    /**
     * Supply the instructions on how to setup a game in reversi.
     */
    private void howToSetup(){
        String setUp = "1) Enter the player Names p1 is Black tiles \r\n    and p2 is White tiles. \r\n"
            + "2) Hit Play, This will start the game.";
        JOptionPane.showMessageDialog(frame, setUp, "How to Setup a game", JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * Supply the instructions on how to play a game in reversi.
     */
    private void howToPlay(){
        String howToPlay = "1) Black starts (player1) , by clicking a Button on the board.  \r\n"

            +"2) To capture, the end piece from where you clicked must be the same \r\n"
            +"     color as current players either vertical, horizontal or diagonal. \r\n"
            +"3) If a player cannot make a move, the turn passes to the opposite player. \r\n"
            +"4) Continue taking turns until no player can make a move or the board is filled. \r\n"
            +"5) The player with the most disks wins , a new game can now be started.";
        JOptionPane.showMessageDialog(frame, howToPlay, "How to Play a game", JOptionPane.INFORMATION_MESSAGE);                  
    }

    /**
     * Supply the instructions on how to save/load a game in Reversi.
     */
    private void howToSaveLoad(){
        String howToSaveLoad = "1) Click SaveGame from the menu bar above. \r\n"
            + "2) Click SaveGame File menu item. \r\n"  
            + "3) Input the name of the file you want to save. \r\n"
            + "4) To Load the file you saved click LoadGame. \r\n"
            + "5) Click LoadGame File and select the text file.";
        JOptionPane.showMessageDialog(frame, howToSaveLoad, "How to Save/Load a game", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Create all the menu options the user can select such as the 
     * ability to save /load games , chnage border color and how to play
     * the game.
     */
    private void makeMenuOptions()
    {
        //Our main menu which will hold the menu content of the Reversi game.
        JMenuBar mainMenu = new JMenuBar();
        frame.setJMenuBar(mainMenu);

        //JMenus/listener For saving the game.
        JMenu SaveGame = new JMenu("SaveGame");
        JMenuItem save = new JMenuItem("SaveGame File");
        SaveGame.add(save);
        save.addActionListener(e -> saveGameFile());
        //JMenus/listener For loading the game.
        JMenu LoadGame = new JMenu("LoadGame");
        JMenuItem load = new JMenuItem("LoadGame File");
        LoadGame.add(load);
        load.addActionListener(e -> loadGameFile());

        //JMenu for help wthin the game e.g. how to play.
        JMenu Help = new JMenu("Help?");
        JMenuItem setUp = new JMenuItem("How to Setup a game");
        JMenuItem howToPlay = new JMenuItem("How to Play a game");
        JMenuItem howToSaveLoad = new JMenuItem("How to Save/Load a game");
        //Help listeners added to run the given methods.
        setUp.addActionListener(e -> howToSetup());
        howToPlay.addActionListener(e -> howToPlay());
        howToSaveLoad.addActionListener(e -> howToSaveLoad());

        //Add the help menu items to the help JMenu
        Help.add(setUp);
        Help.add(howToPlay);
        Help.add(howToSaveLoad);

        JMenu NewSession = new JMenu("NewSession");
        JMenuItem newSession = new JMenuItem("Create New Session");
        NewSession.add(newSession );
        newSession.addActionListener(e -> newSession());

        //BoardSize Code.
        JMenu boardSize = new JMenu("BoardSize");
        //Board options (size).
        JMenuItem four = new JMenuItem("4x4 Size");
        JMenuItem six = new JMenuItem("6x6 Size");
        JMenuItem eight = new JMenuItem("8x8 Size");
        //allow the change of baords.
        four.addActionListener(e -> makeBoard(6, false));
        six.addActionListener(e -> makeBoard(8, false));
        eight.addActionListener(e -> makeBoard(10, false));       
        //Add them all.
        boardSize.add(four);
        boardSize.add(six);
        boardSize.add(eight);

        //BorderOptions Code.
        JMenu Options = new JMenu("BorderOptions");
        JMenuItem red = new JMenuItem("RED");
        JMenuItem green = new JMenuItem("GREEN");
        JMenuItem blue = new JMenuItem("BLUE");
        JMenuItem orange = new JMenuItem("ORANGE");
        JMenuItem magenta = new JMenuItem("MAGENTA");
        JMenuItem yellow = new JMenuItem("YELLOW");
        JMenuItem defaultCol = new JMenuItem("DEFAULT");
        //Add listeners to change the border.
        red.addActionListener(e -> boards.Border(Color.RED));
        green.addActionListener(e -> boards.Border(Color.GREEN));
        blue.addActionListener(e -> boards.Border(Color.BLUE));
        orange.addActionListener(e -> boards.Border(Color.ORANGE));
        magenta.addActionListener(e -> boards.Border(Color.MAGENTA));
        yellow.addActionListener(e -> boards.Border(Color.YELLOW));
        defaultCol.addActionListener(e -> boards.Border(Color.DARK_GRAY));
        //Add to the options menu, the options.
        Options.add(red);
        Options.add(green);
        Options.add(blue);
        Options.add(orange);
        Options.add(magenta);
        Options.add(yellow);
        Options.add(defaultCol);  

        //Add all our JMenus.
        mainMenu.add(SaveGame);
        mainMenu.add(LoadGame);
        mainMenu.add(boardSize);
        mainMenu.add(Options);
        mainMenu.add(NewSession);
        mainMenu.add(Help);
    }

    /**
     * Create a new session which resets all the current scores and restarts
     * the entire board. This allows players to select names again and start 
     * a fresh game.
     */
    private void newSession()
    {
        //Decides if the user has clicked create new session (will be 0) else they cancelled and we dont need to do anymore.
        int whatNext = JOptionPane.showOptionDialog(board, " Reseting the session will result in all scores being reset!", "RESET CURRENT SESSION, ARE YOU SURE? ", JOptionPane.INFORMATION_MESSAGE, 0, null, new String[]{"Create New Session", "Continue Playing"}, null);
        if(whatNext == 0)
        { 
            //Reset everything to a completely new session all scores are 0.
            p1Name.setText("Player1");
            p2Name.setText("Player2");
            p1scoreName.setText("Player1 Score:");
            p2scoreName.setText("Player2 Score:");
            p1ScoreInt = 0;
            p2ScoreInt = 0;
            p1Score.setText("0");
            p2Score.setText("0");
            WDisks.setText("0");
            BDisks.setText("0");
            p1Name.setEditable(true);
            p2Name.setEditable(true);
            makeBoard(10, true);
            statusMessage.setText("Come and play Reversi by entering your PLAYERNAMES and clicking PLAY!");
        }
    }

    /**
     * Return the current border color.
     * 
     * @return The current AWT border color 
     */
    public static Color retBorderColor()
    {
        return borderColor;
    }

    /**
     * Set the border color to the specified color.
     * 
     * @param The AWT color you want to use for the border
     */
    public static void setBorderColor(Color color)
    {
        borderColor =  color;
    }

    /**
     * Create a new board for the Reversi game.
     * 
     * @param size The specified board size
     * @param resetSession Are you reseting the session or changing the board size via the menu option
     */
    private void makeBoard(int size,boolean resetSession)
    {
        int whatNext;   
        if(resetSession){
            //We are reseting the session so we dont need to have a dialog for changing board size.
            whatNext = 0;
        }
        else{
            //We are changing the board size and not the reseting the session so tell the user that changing the board size will reset current game.
            whatNext = JOptionPane.showOptionDialog(board, "Changing BOARD SIZE will RESET the current GAME, ARE YOU SURE?", "Change Board Size? ", JOptionPane.INFORMATION_MESSAGE, 0, null, new String[]{"Change BoardSize", "Continue Playing"}, null);
        }

        if(whatNext == 0)
        { 
            //Remove the old board and create a new board with the following GUI aesthetics.
            startGame.setVisible(true);
            frame.remove(board);
            boards = new Board(size);
            board = boards.getBoardPanel();
            board.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, Color.BLACK));     
            content.add(board, BorderLayout.CENTER);  
            //Reset the game score (disk count). 
            WDisks.setText("0");
            BDisks.setText("0");
            statusMessage.setText("Come and play Reversi by clicking PLAY!");
            //Show the new frame.
            showFrame();
        }
    }

    /**
     * Create the GUI frame , the main setup of what the user will see.
     */
    private void makeFrame()
    {
        //Intialise the frame for Reversi game
        frame = new JFrame("Reversi");
        //Set the border to defualt Dark Gray.
        borderColor = Color.DARK_GRAY;

        //Invite players to enter name and play, add to status panel the welcome message.
        status = new JPanel();
        statusMessage = new JLabel("Come and play Reversi by entering your PLAYERNAMES and clicking PLAY!");     
        status.add(statusMessage);

        //Board of 8x8 with a nice border to see the playing area (note its 10 becuase theres a border)
        boards = new Board(10);
        board = boards.getBoardPanel();
        
        //Make the player panel with all the scores etc.
        scores = makePlayerPanel();

        //Set some nice borders make the GUI look better (more like a game)
        board.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, Color.BLACK));
        scores.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.BLACK));
        status.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

        //Main container content.
        content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        //Add JPanels to the GUI in the border layouts below.
        content.add(scores, BorderLayout.EAST);
        content.add(board, BorderLayout.CENTER);
        content.add(status, BorderLayout.SOUTH);

        //Resize the frame and configure components. 
        makeMenuOptions();
        showFrame();
    }

    /**
     * Show the frame by packing it, resize to defualt size and set visible.
     */
    private void showFrame()
    {
        frame.pack();
        frame.setSize(633,512);
        frame.setVisible(true);
    }

    /**
     * Create the player panel which contains the following information:
     * - Player names, text fields
     * - Session scores
     * - White and Black disk count
     * @return The playerPanel with player information/scores.
     */
    private JPanel makePlayerPanel()
    {
        //Create and later return a panel for player details e.g names , scores, start and current disks.        
        JPanel newPlayerPanel = new JPanel();
        //Will resize so all the sub heading e.g. scores and counts are the same size.
        newPlayerPanel.setLayout(new GridLayout(4,0));

        //Player names
        JPanel playerNames = new JPanel();
        //Gridlayout for the input boxes.
        playerNames.setLayout(new GridLayout(0, 1));
        playerNames.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),"Player Names"));
        //Two text fields to enter names,or use default names(Player1 & Player2).
        JLabel p1 = new JLabel("Player One:" , JLabel.CENTER);
        p1Name = new JTextField("Player1", 10);  
        //Player2.
        JLabel p2 = new JLabel("Player Two:" , JLabel.CENTER);
        p2Name = new JTextField("Player2", 10);
        //Add them to the player names part of the JPanel.
        playerNames.add(p1);
        playerNames.add(p1Name);
        playerNames.add(p2);
        playerNames.add(p2Name);

        //Player scores.
        JPanel scores = new JPanel();
        scores.setLayout(new GridLayout(0,1));
        TitledBorder score = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),"Session Scores");
        scores.setBorder(score);

        //Scores area .     
        p1scoreName = new JLabel("Player One Score:", JLabel.CENTER);
        scores.add(p1scoreName);
        p1Score = new JLabel("0", JLabel.CENTER);
        scores.add(p1Score);

        p2scoreName = new JLabel("Player Two Score:", JLabel.CENTER);
        scores.add(p2scoreName);
        p2Score = new JLabel("0", JLabel.CENTER);
        scores.add(p2Score);

        //Disks panel.
        JPanel disks = new JPanel();
        disks.setLayout(new GridLayout(0,1));
        disks.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),"Current Disk Count"));
        //Labels and curent disks White.
        JLabel white = new JLabel("White: ", JLabel.CENTER);
        disks.add(white);
        WDisks = new JLabel("2", JLabel.CENTER);

        disks.add(WDisks);
        //Labels and curent disks Black.
        JLabel black = new JLabel("Black:", JLabel.CENTER);
        disks.add(black);
        BDisks = new JLabel("2", JLabel.CENTER);

        disks.add(BDisks);

        //Start Gamem Panel & button.
        JPanel start = new JPanel();
        start.setLayout(new GridLayout(0,1));
        start.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),"Click to start game"));
        startGame = new JButton("Play");
        startGame.addActionListener(e ->playGame());
        start.add(startGame);
        

        //Add the components together.
        newPlayerPanel.add(playerNames);
        newPlayerPanel.add(scores);
        newPlayerPanel.add(disks);
        newPlayerPanel.add(start);

        
        return newPlayerPanel;
    }

    /**
     * Stop the user from changing names once the game has started.
     */
    private void stopEditingNames()
    {
        p1scoreName.setText(p1Name.getText());
        p2scoreName.setText(p2Name.getText());
        p1Name.setEditable(false);
        p2Name.setEditable(false);
    }

    /**
     * Update the count on the GUI for the White and Black disks.
     */
    public static void updateCount()
    {
        WDisks.setText("" + boards.countPieces("W"));
        BDisks.setText("" + boards.countPieces("B"));
    }

    /**
     * Update the session score after a player wins.
     * 
     * @param winner The player who W or B
     */
    public static void updateSeshScore(String winner)
    {
        if(winner.equals("B")){
            p1ScoreInt++;
            p1Score.setText(" "+ p1ScoreInt);
        }
        else if (winner.equals("W")){
            p2ScoreInt++;
            p2Score.setText(" "+ p2ScoreInt);
        }
    }

    /**
     * Play the game , this starts the game so players can play Reversi.
     */
    public void playGame()
    {
        //Have started the game session, so you can't edit names anymore.
        stopEditingNames();
        boards.reset();
        boards.startGame();
        boards.setUp();
        //Hide the play button because a game is in progress.
        startGame.setVisible(false);
        updateCount();
        //Black starts first always
        setMessage("B");        
    }

    /**
     * Play again will set the Play button back to visible so players can click play again.
     */
    public static void playAgain()
    {
        startGame.setVisible(true);
        statusMessage.setText("Come and play Reversi by clicking PLAY!");
    }

    /**
     * Return the Black disk count in the current game.
     * @return The Black disk count.
     */
    public static int retB()
    {
        int b =  Integer.parseInt(BDisks.getText());
        return b;
    }

    /**
     * Return the White disk count in the current game.
     * @return The White disk count.
     */
    public static int retW()
    {
        int w =  Integer.parseInt(WDisks.getText());
        return w;
    }

    /**
     * Set the status bar message to the current player either White or Black.
     * 
     * @param currentPlayer The current player taking the turn
     */
    public static void setMessage(String currentPlayer)
    {
        if(currentPlayer.equals("B")){
            statusMessage.setText("It's " + p1Name.getText() +"'s turn, they control the (Black) pieces");
        }
        else if (currentPlayer.equals("W")){
            statusMessage.setText("It's " + p2Name.getText() +"'s turn, they control the (White) pieces");
        }
    }
    
    /**
     * Set the status bar message to the current player either White or Black
     * that they cannot take a turn.
     * 
     * @param currentPlayer The current player who cannot take the turn
     */
    public static void setMessageCantTakeTurn(String currentPlayer){
        if(currentPlayer.equals("B")){
            statusMessage.setText( p1Name.getText() +", cannot take their turn, they control the (Black) pieces");
        }
        else if (currentPlayer.equals("W")){
            statusMessage.setText( p2Name.getText() +", cannot take their turn, they control the (White) pieces");
        }
    }
    

    /**
     * Save game file create and writes to a new txt file which stores information 
     * about the current game in the format given below(all followed by new line):
     * - START OF FILE
     * - Board Size e.g. 8
     * - START OF BOARD
     * - The current board state including all W and B disk and empty tiles
     * - END OF BOARD
     * - Current Play (e.g. B , W or empty text)
     * - Player 1 Name 
     * - Player 2 Name
     * - Player 1 Score
     * - Player 2 Score
     * - Current White Disks
     * - Current Black Disks
     * 
     * The file is marked as read only once writing has finished this prevents tampering
     * of the file.
     * 
     * @throws IOException usually in the form of java.io.IOException: There is not enough space on the disk
     * 
     */
    public void saveGameFile () 
    {
        //What is the file going to be called?
        String fileName = JOptionPane.showInputDialog(board,"What name do you want to give the file?");
        try{
            //Create a new file with the given name and add the txt file extension.
            File file = new File( fileName + "" + ".txt");
            //File is writable
            file.setWritable(true);
            //File Writer will writer to our txt file the current game.
            FileWriter writer = new FileWriter(file);
            //Our checking mechanism to check when loading valid files.
            writer.write("START OF FILE" + "\r\n");
            //Board size.
            writer.write(boards.getBoardSize() + "\r\n");
            //Clarity just shows start of file when viewing the txt.
            writer.write("START OF BOARD" + "\r\n");
            //Collection of the current board state (all disk pieces).
            ArrayList<String> boardState = boards.countBoardState();
            for(String text : boardState){
                writer.write(text + "\r\n");
            }
            //Clarity just stating the end of the Board when viewing the txt.
            writer.write("END OF BOARD" + "\r\n");
            //CurrentPlayer.
            writer.write(boards.getCurrentPlayer() + "\r\n");
            //Player Names.
            writer.write(p1Name.getText() + "\r\n");
            writer.write(p2Name.getText() + "\r\n");
            //Session Scores.
            writer.write(p1ScoreInt + "\r\n");
            writer.write(p2ScoreInt + "\r\n");
            //W and B disks count
            writer.write(retW() + "\r\n");
            writer.write(retB() + "\r\n");  
            //Close the file we are done saving the contents.
            writer.close();
            //File cannot be changed now its read only.
            file.setReadOnly();
        }
        catch(IOException e)
        {
            //Most likely error is/returns java.io.IOException: There is not enough space on the disk.
            JOptionPane.showOptionDialog(frame, e.toString(), "Problem Saving File", JOptionPane.ERROR_MESSAGE, 0, null, new String[]{"Ok"}, null);
            //toString print the stack to see where the error occurred testing purposes.
            //e.printStackTrace();
        }
    }

    /**
     * Load the given text file if its a valid one created via the save game
     * method. This has error handling to deal with malformed, corrupted and 
     * potentially dodgy files. The loading also changes the board sized to
     * match that of the one save in the file ensuring both boards are the same size.
     * 
     * @throws FileIssueException if the file doesn't contain our check mechanism START OF FILE
     * @throws FileNotFoundException if the file cannot be found (possibly could have no name or not there in the directory)
     * @throws IOException if there is an I/O error when reading the file.
     */
    public void loadGameFile (){
        boolean continueCheck = true;
        //Do until the user hits cancel or finds a valid file.
        do{
            //Filter 'list' , simply say we only want txt files.
            FileNameExtensionFilter filterTextFile = new FileNameExtensionFilter("Text File",  "txt");
            JFileChooser fileName = new JFileChooser();
            //Add the filters on what the user can pick (txt files).
            fileName.addChoosableFileFilter(filterTextFile);
            //We only want the txt files so completely ignore the other file types, don't accept all.
            fileName.setAcceptAllFileFilterUsed(false);
            //Holds what the user clicked on
            int whatNext = fileName.showOpenDialog(frame);
            String name = "";
            if(whatNext == JFileChooser.APPROVE_OPTION){
                //Get the file the user picked via JFileChooser, whatNext is equal to 0.
                File file = fileName.getSelectedFile();
                //Get its name and use it for reading later.
                name = file.getName();
                //ACCEPTED
            }
            else if(whatNext == JFileChooser.CANCEL_OPTION){
                //If cancel is hit we cannot open the file, whatNext is equal to 1
                //CANCELLED
                return;           
            }
            //Set to current border color.
            Color border = Reversi.retBorderColor();
            //Try to do the following.
            try(BufferedReader reader = new BufferedReader(new FileReader(name))){
                //First line is the checking mechanism START OF FILE.
                String validFile = reader.readLine();
                if(!(validFile.equals("START OF FILE"))){
                    //This file doesn't start with our check so it's not valid.
                    throw new FileIssueException(name);
                }
                //Check the current board size. 
                int size = boards.getBoardSize();
                //The board size from file match it if current board size is not the same.
                String boardSize = reader.readLine(); 
                //Convert to a number for comparison.
                int intBoardSize = Integer.parseInt(boardSize);
                //If the current board size is not the same as the one in the file then create a new board to hold the game.            
                if(!(intBoardSize == size)){
                    makeBoard(intBoardSize, true);
                }

                //The start of all saveFile is START OF BOARD , just for clarity if you view the file.
                String StartOfBoard = reader.readLine();
                //The first tile color of the game board. 
                String color = reader.readLine();
                //List of the boardState
                ArrayList<String> boardState = new ArrayList();
                //While this hasnt reached the end of the board contents in the file keep adding the colors to the array list.
                while(color != "END OF BOARD"){
                    //We have finished looking at the board if equals true , next is currentPlayer.
                    if(color.equals("END OF BOARD")){
                        //This line is currentPlayer, we are done with the board.
                        String currentPlayer = reader.readLine();
                        //Set the message to that player.
                        boards.setCurrentPlayer(currentPlayer);
                        setMessage(currentPlayer);
                        break;
                    }
                    //Removes the e and sets text back to "" for empty.
                    if(color.equals("e")){
                        boardState.add("");
                    }
                    else{
                        boardState.add(color);
                    }
                    //Color is equal to next line of the file.
                    color = reader.readLine();

                }

                //Readlines for player names, set text back to these values.
                String p1 = reader.readLine();
                String p2 = reader.readLine();
                p1Name.setText(p1);
                p1scoreName.setText(p1);
                p2Name.setText(p2);
                p2scoreName.setText(p2);

                //Readlines for session score,set text back to these values.
                String p1scores = reader.readLine();
                p1Score.setText(p1scores);
                String p2scores = reader.readLine();
                p2Score.setText(p2scores);

                //Convert the string back to int, so the loaded games session score is up to date otherwise they will be 0.
                p1ScoreInt = Integer.parseInt(p1scores);               
                p2ScoreInt = Integer.parseInt(p2scores);

                //Disk count for that game w and B disks,set text back to these values.
                String WCount = reader.readLine();
                String BCount = reader.readLine();
                
                WDisks.setText(WCount);
                BDisks.setText(BCount);

                //Check the number pieces are they the same as before?
                
                //Does W disks on the board match the count saved in the file.
                String WCountCheck = boards.countNonEmptyBoardPieces(boardState,"W").size() + "";                
                //Does B disks on the board match the count saved in the file.
                String BCountCheck = boards.countNonEmptyBoardPieces(boardState,"B").size() + "";          
                if(!(WCount.equals(WCountCheck) && BCount.equals(BCountCheck))){
                    //If they don't match then the file has gone wrong and stands inconsistent.
                    throw new InconsistentFileException(name);
                }
                
                //Like any other game once started you can't change names and the play button disappears.
                p1Name.setEditable(false);
                p2Name.setEditable(false);
                startGame.setVisible(false);

                //Load the game from the array list.
                boards.loadGame(boardState);
                boards.Border(border);

                //We have finished with the loading of the file we can stop the do while.
                continueCheck = false;
            }  
            
            //Malformed ,Corrupted, Inconsistent and Invalid files will be caught here. 
            catch(FileIssueException e){
                JOptionPane.showOptionDialog(frame, e.toString(), "Problem Opening File", JOptionPane.ERROR_MESSAGE, 0, null, new String[]{"Choose Another File"}, null);
                //e.printStackTrace();
            }

            catch(FileNotFoundException e){
                JOptionPane.showOptionDialog(frame, e.toString(), "Attempt To Open File Has Failed File Not Found", JOptionPane.ERROR_MESSAGE, 0, null, new String[]{"Choose Another File"}, null);               
                //e.printStackTrace();
            }
            
            catch(InconsistentFileException e){
                JOptionPane.showOptionDialog(frame, e.toString(), "Inconsistent File Attempt To Open File Has Failed", JOptionPane.ERROR_MESSAGE, 0, null, new String[]{"Choose Another File"}, null);
                //e.printStackTrace();
            }

            catch(IOException e){
                JOptionPane.showOptionDialog(frame, e.toString(), "Attempt To Open File Has Failed", JOptionPane.ERROR_MESSAGE, 0, null, new String[]{"Choose Another File"}, null);
                //e.printStackTrace();
            }
        } while(continueCheck);
    }


}
