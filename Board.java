import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * The Board class provides all the information necessary to provide the GUI 
 * (Reversi Class) with the information it requires e.g the board state , score values
 * players turn and the game itself. The board class has the job of the game functionality
 * it is seperate from the Reversi class and deals with game features such as
 * can players move ,capturing piece, taking turns and winning etc.
 * 
 * The code has been designed so that it can be extended later on e.g. the capture
 * method doesn't rely on there being a border therefore the border can be removed
 * if needed with only minor changes to testEndGame() < size rather than size - 1 and
 * start from 0. The JButton Generation also allows other grids to be made and added 
 * to the GUI menu e.g you might want a 12x12 grid or something.
 *
 *
 * @author DTK3 Daniel Knight
 * @version (version 7 29/04/18)
 */
public class Board
{
    //Our main board used to supply to our GUI later, contains the game pieces.
    private JPanel board;
    //Create a 2-D array of tile locations (the buttons the user clicks on).
    private JButton[][] tileLocations;
    //Board size used for the constructor of board objects.
    private int size;
    //Current player Black and White tiles, format as "B" & "W".
    private String currentPlayer;
    //Is a move legal or not.
    private boolean isLegal;
    //Used for checking moves or capturing,basically is this a check or the real thing?
    private boolean isCheck;
    //Is a move possible anywhere for this player.
    private boolean canMove;
    //Can the Black player move.
    private boolean bCantMove;
    //Can the White player move.
    private boolean wCantMove;
    //Is  this game over true or false?
    private boolean gameOver;
    //Used for capturing and checking the pieces , true dont stop checking or false we do.
    private boolean notStop;
    //wWho won the game?
    private String winner;
    //Trace back to the x and y value used for capturing, then use ths arraylist values.
    private ArrayList<Integer> xVal;
    private ArrayList<Integer> yVal;
    //How many pieces have we captured so far in the given turn.
    private int captures;
    //Do we need to show the user a GUI popup 
    private boolean dialog;

    /**
     * Constructor for objects of class Board
     * Setup of the board and creation of the board.
     * 
     * @param size the size of the game board 
     */
    public Board(int size)
    {
        //Initialise instance variables.
        this.size = size;
        gameOver = false;
        xVal = new ArrayList<>();
        yVal = new ArrayList<>();

        //Player is intially set to Black ("B").
        currentPlayer = "B";

        board = new JPanel();
        //Set the array to the size of the board e.g default is 8x8 supplied by Reversi class.
        tileLocations = new JButton[size][size];
        //Set a grid space dependent on board size.
        board.setLayout(new GridLayout(size,size));
        //Create the board.
        createBoardSize();
        //Set border to the currently selected border color.
        Border(Reversi.retBorderColor());

    }

    /**
     * Return the boardSize used when saving the game file.
     * 
     * @return The size of the board
     */
    public int getBoardSize()
    {
        return size;
    }

    /**
     * Return the current player, either B or W for Black and White.
     * 
     * @return The currentPlayer of the game
     */
    public String getCurrentPlayer(){
        return currentPlayer;
    }

    /**
     * Set the curentPlayer to the specified player B or W for Black and White.
     * 
     * @param player The player you wish to set currentPlayer to
     */
    public void setCurrentPlayer(String player){
        currentPlayer = player;
    }

    /**
     * Check the entire board for any blank or empty JButtons within the 2D array
     * if there is a JButton with empty text then gameOver is false. If gameOver is true
     * then update the winner , reset and setup a new game.
     * 
     */
    private void testEndGame()
    {
        //Loop all the JButtons not including the border (outside ones).
        for(int x = 1; x < size - 1  ; x++){
            for(int y = 1; y < size -1 ; y++){
                //If this is true then potentially there could be another move. 
                if(tileLocations[x][y].getText().equals("") ){
                    gameOver = false;
                    return;
                }                
            }
        }
        //Gameover if there are no pieces left blank then do the following.
        gameOver = true;
        retWinner();
        reset();
        setUp();
    }

    /**
     * Loop through the entire board (2D array of JButtons) and count the pieces
     * of the specified player.
     * 
     * @param player The player W or B that you wish to check
     * @return The count as an int for the total pieces that player has
     */
    public int countPieces(String player)
    {
        int playerScores = 0;
        for(int x = 0; x < size ; x++){
            for(int y = 0; y < size ; y++){
                if(tileLocations[x][y].getText().equals(player)){
                    playerScores++;
                }
            }
        }
        return playerScores;
    }

    /**
     * Count the board state ie all the pieces and the text they hold these include
     * - B
     * - W
     * _ ""
     * The board state is added to an ArrayList and returned to the Reversi class 
     * for saving/loading the game file.
     * 
     * @return The ArrayList of the boards current state     
     */
    public ArrayList<String> countBoardState()
    {
        ArrayList <String> boardState = new ArrayList();        
        for(int x = 0; x < size  ; x++){
            for(int y = 0; y < size  ; y++){
                String text = tileLocations[x][y].getText();
                //If the text is empty then set to e (for empty) this stops any errors caused by saving/loading the file e.g a misread or write of " " instead of "" etc.
                if(text.equals("")){
                    boardState.add("e");
                }
                else{
                    //Not empty so add the text of that player B or W.
                    boardState.add(text);
                }
            }
        }
        return boardState;
    }
    
    /**
     * Count the board pieces of given color that are not empty.
     * 
     * @param playerPieces The array list of board state
     * @param player The pieces you want to count. 
     * @return the arrayList of the board pieces of the given color
     */
    public ArrayList<String> countNonEmptyBoardPieces(ArrayList<String> playerPieces, String player){
        ArrayList <String> retPlayerPieces =  new ArrayList(); 
        for(String pieces : playerPieces){
            if(pieces.equals(player)){
                retPlayerPieces.add(pieces);
            }
        }
        return retPlayerPieces;
    }

    /**
     *  Load the game board with the supplied ArrayList data, this will include 
     *  what to set for the JButtons text. Starts from the 2D array coordinate [0][0] until [size - 1] [size - 1].
     *  
     *  @param gameState The ArrayList which holds the game data for loading
     */
    public void loadGame(ArrayList<String> gameState)
    {
        int index = 0;
        for(int x = 0; x < size ; x++){
            for(int y = 0; y < size ; y++){
                //Whats the next state of the board.
                String whatNext = gameState.get(index);
                index++;
                //Set the JButton to the correct text.
                tileLocations[x][y].setText(whatNext);
                //If a tile is "" empty it can be clicked and color is set to the defualt Light Gray.
                if(whatNext.equals("")){
                    tileLocations[x][y].setBackground(Color.LIGHT_GRAY);
                    tileLocations[x][y].setEnabled(true);
                }
                //Else if the JButton text is held by a player then it has already been clicked and therefore set to that players color.
                else if(whatNext.equals("B")){
                    tileLocations[x][y].setBackground(Color.BLACK);
                    tileLocations[x][y].setEnabled(false);
                }
                else if(whatNext.equals("W")){
                    tileLocations[x][y].setBackground(Color.WHITE);
                    tileLocations[x][y].setEnabled(false);
                }
            }
        }
    }

    /**
     * Reset the current Game by setting every JButton text to empty and setting
     * the defualt background color to Light Gray. The game has been reset.
     */
    public void reset()
    {
        for(int x = 0; x < size ; x++){
            for(int y = 0; y < size ; y++){
                tileLocations[x][y].setText("");
                tileLocations[x][y].setBackground(Color.LIGHT_GRAY);
                //Set to false because we have reset the board but havent setup the game yet.
                tileLocations[x][y].setEnabled(false);
            }
        }
        //Default player is Black so currentPlayer is set to B.
        currentPlayer = "B";
        //it's not gameOver anymore as the game has been set.
        gameOver = false;
    }

    /**
     * Create the board for players to actually play the game on and loop the generation
     * of listeners so the move method can be run on all the buttons within the board.
     */
    private void createBoardSize()
    {
        //Create a nice grid with black borders and light gray background , specify size of the grid.
        for(int x = 0; x < size  ; x++){
            for(int y = 0; y < size ; y++){
                //Create a new tile (button) in the board.
                tileLocations[x][y] = new JButton();
                tileLocations[x][y].setBackground(Color.LIGHT_GRAY);
                tileLocations[x][y].setText("");
                tileLocations[x][y].setEnabled(false);
                //Fresh final variables for the lambdas (basically copies a reference to the local variable).
                final int tempx = x;
                final int tempy = y;
                //Add to the 2D array the JButton at that given location within the board e.g [1][4].
                tileLocations[x][y].addActionListener(e -> move(tempx, tempy));
                board.add(tileLocations[x][y]);

            }
        }
        //Setup uo the border to the current color.
        Border(Reversi.retBorderColor());
    }

    /**
     * Start game by Looping all JButtons and allowing them to be clicked.
     */
    public void startGame(){
        for(int x = 0; x < size ; x++){
            for(int y = 0; y < size; y++){
                tileLocations[x][y].setEnabled(true);
            }
        }
    }

    /**
     * Change the color of the border which adds nice GUI aesthetics.
     * 
     * @param color The AWT color which you wish to use for the border.
     */
    public void Border(Color color)
    {   
        int x = 0;
        int y = 0; 
        //Top border row.
        while(y < size)
        {
            tileLocations[x][y].setBackground(color);
            tileLocations[x][y].setText("");
            tileLocations[x][y].setEnabled(false);
            tileLocations[x][y].setBorder(null);
            y++;
        }
        //Left border column.
        y = 0;
        while(x < size)
        {
            tileLocations[x][y].setBackground(color);
            tileLocations[x][y].setText("");
            tileLocations[x][y].setEnabled(false);
            tileLocations[x][y].setBorder(null);
            x++;
        }
        //Bottom border row.
        x = size - 1;
        y = 0;
        while(y < size)
        {
            tileLocations[x][y].setBackground(color);
            tileLocations[x][y].setText("");
            tileLocations[x][y].setEnabled(false);
            tileLocations[x][y].setBorder(null);
            y++;
        }
        //Right border row.
        x = 0;
        y = size - 1;
        while(x < size)
        {
            tileLocations[x][y].setBackground(color);
            tileLocations[x][y].setText("");
            tileLocations[x][y].setEnabled(false);
            tileLocations[x][y].setBorder(null);
            x++;
        }
        //Tell the GUI what the current color is.
        Reversi.setBorderColor(color);
    }

    /**
     * Setup the game ready to play , this involves disposing a 2x2 grid in the centre 
     * of the board with alternating colors B and W.
     */
    public void setUp()
    {
        //Calculations to centre the 2x2 center square on any even size board e.g 4x4 or 8x8.
        int TileMinus = (size / 2) -1;
        int TilePlus = (size / 2); 
        //Top Left of the 2x2
        tileLocations[TileMinus][TileMinus].setBackground(Color.WHITE);
        tileLocations[TileMinus ][TileMinus].setText("W");
        tileLocations[TileMinus][TileMinus].setEnabled(false);
        //Top Right of the 2x2
        tileLocations[TileMinus][TilePlus].setBackground(Color.BLACK);
        tileLocations[TileMinus][TilePlus].setText("B");
        tileLocations[TileMinus][TilePlus].setEnabled(false);
        //Bottom Left of the 2x2
        tileLocations[TilePlus][TileMinus].setBackground(Color.BLACK);
        tileLocations[TilePlus][TileMinus].setText("B");
        tileLocations[TilePlus][TileMinus].setEnabled(false);
        //Bottom Right of the 2x2
        tileLocations[TilePlus][TilePlus].setBackground(Color.WHITE);
        tileLocations[TilePlus][TilePlus].setText("W");
        tileLocations[TilePlus][TilePlus].setEnabled(false);
        //Both players can move because it is the start of the game.
        bCantMove = false;
        wCantMove = false;
        //Set the border to the most current border color slection.
        Border(Reversi.retBorderColor());
    }

    /**
     * Get the board and return it.
     * 
     * @return The JPanel Board which players play on
     */
    public JPanel getBoardPanel()
    {
        return board;
    }

    /**
     * Allow player movements based on factors such as switching players based off who can and cannot move,
     * whos turn it currently is and can a move be taken.Capture the correct pieces if it iis legal, if not keep trying until the 
     * Black or White player can (if it is possible) or pass turn. If the move wasn't legal do not capture anything.
     * 
     * @param x The JButon X value
     * @param y The JButon Y value
     */
    public void move(int x, int y)
    {        
        //Intially check if it is the start of the game e.g. B and W would be 2 if so currentPlayer should be B
        if(countPieces("B") == 2 && countPieces("W") == 2)
        {
            currentPlayer = "B";
            //We want to see dialogs becuase players are playing now.
            dialog = true;
        }
        //If the current player cannot move then change to the opposite player and stop executing the move method. 
        if(!(canMove())){
            if(currentPlayer.equals("B")){
                currentPlayer = "W";
                //Enable the current tile that got clicked because the current player couldnt take a move.
                tileLocations[x][y].setEnabled(true);
                return;
            }
            else if (currentPlayer.equals("W")){
                currentPlayer = "B";
                //Enable the current tile that got clicked because the current player couldnt take a move.
                tileLocations[x][y].setEnabled(true);
                return;
            }
        }
        //Deals with the B player 
        if(currentPlayer.equals("B") ){
            //Capture the correct pieces if its legal if not keep trying until the (BLACK) player can (if it's possible) or pass turn.

            //Can the Black player move?
            canMove();
            //Capture what the Black player  clicked on supplying x & y params.
            capture(x,y);

            //Was this move a legal one?
            if(isLegal()){
                //This isn't a check we are capturing for real.
                isCheck = false;
                //Set the tile the Black player clicked to Black and also the text to B.
                tileLocations[x][y].setBackground(Color.BLACK);
                tileLocations[x][y].setText("B");
                //Not allowed to click this button again becuase it has been clicked already.
                tileLocations[x][y].setEnabled(false);
                //If black can move then assume white can if not, the canMove below will decide the next step.
                bCantMove = false;
                wCantMove = false;
                //Change player move was legal.
                currentPlayer = "W";
                //Check the next currentPlayer can move (just got swapped from B to W).
                canMove();

            }

        }
        else if(currentPlayer.equals("W")){
            //Capture the correct pieces if its legal if not keep trying until the (WHITE) player can (if it's possible) or pass turn.

            //Can the White player move?
            canMove();
            //Capture what the White player clicked on supplying x & y params.
            capture(x,y);
            //Was this move a legal one?
            if(isLegal())
            {
                //This isn't a check we are capturing for real.
                isCheck = false;
                //Set the tile the White player clicked to White and also the text to W.
                tileLocations[x][y].setBackground(Color.WHITE);
                tileLocations[x][y].setText("W");
                //Not allowed to click this button again.
                tileLocations[x][y].setEnabled(false);
                //If White can move then assume Black can if not, the canMove below will decide the next step.
                wCantMove = false;
                bCantMove = false;
                //Change player move was legal.
                currentPlayer = "B";
                canMove();
            }
        }   
        //Update the GUI and supply the current player, upodate count and check the future moves
        Reversi.setMessage(currentPlayer);
        Reversi.updateCount();
        //Check future moves is the game going to be over?
        checkBothPlayers();
    }

    /**
     * Color placement is used with the Capture method it decides whether the area the player clicked
     * will at least capture one piece  therefore making it a valid capture.
     * 
     * @param x The JButon X value
     * @param y The JButon Y value
     * @param position How much do we need to add or subtract from our current location to check other JButtons 
     *        horizontally, vertically or diagonally
     * 
     */
    public  void colorPlacement(int x , int y, int position)
    {      
        //If the x or y are not negative and less than the size of the board this is a valid check else it is outside the size of the board and therefore theres no point continuing.
        if(x >= 0 && x < size  && y >= 0 && y < size){
            //If the color of the tile is the opposite color of the currentplayer then store the values as a capture might be possible, proceed to check.
            if(!(tileLocations[x][y].getText().equals(currentPlayer)) && tileLocations[x][y].getText() != ""){
                xVal.add(x);
                yVal.add(y);
                //Continue checking in the capture method.
                notStop = true;
            }
            //If the color is the same as the current player then the capture is possible
            else if(tileLocations[x][y].getText().equals(currentPlayer)){
                //Temporarily store the current position 
                int changeColorAmount = position;
                //If this was canMove() then we can return as this was just a check and not an actual capture.
                if(isCheck == true && position > 1){
                    //Position greater than means this is not the first tile next to the JButton clicked.
                    canMove = true;
                    return;
                }
                //Set back to 1.
                position = 1;
                //The index of our stored x and y values.
                int index = 0;
                while(position < changeColorAmount){
                    //Trace back to the first tile and change its color to either Black or White depending on the current player. 
                    tileLocations[xVal.get(index)][yVal.get(index)].setText(currentPlayer);
                    if(currentPlayer.equals("B")){
                        tileLocations[xVal.get(index)][yVal.get(index)].setBackground(Color.BLACK);
                    }
                    else if (currentPlayer.equals("W")){
                        tileLocations[xVal.get(index)][yVal.get(index)].setBackground(Color.WHITE);
                    }
                    //++ the index and position and move onto the next index and repeat until the while is false.
                    index ++;
                    position++;
                }
                //How many have we caught, this determines if its a legal move.
                captures = position;
                //We can stop now the checking/capturing has finished for this area.
                notStop = false;
            }
            else if(tileLocations[x][y].getText().equals(""))
            {
                //If the tile is empty then we can stop the capture is not possible.
                notStop = false;
            }
        }
        else{
            //Out of bounds location so therefore not possible.
            notStop = false;
        }

    }

    /**
     * Clear the list of x and y locations used at the end of a capture to remove these values.
     */
    public void clearList()
    {
        Iterator<Integer> XRemove = xVal.iterator();
        while(XRemove.hasNext()){
            Integer x = XRemove.next();
            XRemove.remove();
        }
        Iterator<Integer> YRemove = yVal.iterator();
        while(YRemove.hasNext()){
            Integer y = YRemove.next();
            YRemove.remove();
        }
    }
    
    /**
     *  Capture pieces run by the Move method, this method captures pieces horizontally, vertically or diagonally
     *  in a straight line based on the currentplayer. The method computes if a move is legal by calling the color
     *  placement method and carrying out the necessary checks.
     *  
     *  @param x The JButon X value
     *  @param y The JButon Y value
     */
    private void capture(int x, int y)
    {
        //Stores the colors (text) of the sorrounding JButtons the array list will always contain 8 values (0-7).
        ArrayList<String> check = new ArrayList();    
        //Checks if the sorrounding JButtons text is outside the bounds of the board if so store blank for later.

        //Top left location from current JButton.
        if(x - 1 >= 0 && x - 1 < size && y - 1 >= 0 && y - 1 < size){
            check.add(tileLocations[x - 1][y -1].getText());
        }
        else{
            check.add("");
        }

        //Above location from current JButton.
        if(x - 1 >= 0 && x - 1 < size && y >= 0 && y < size){
            check.add(tileLocations[x - 1][y].getText());
        }
        else{
            check.add("");
        }

        //Top right location from current JButton.
        if(x - 1 >= 0 && x - 1 < size && y + 1 >= 0 && y + 1 < size){
            check.add(tileLocations[x - 1][y + 1].getText());
        }
        else{
            check.add("");
        }

        //Left adjacent location from current JButton.
        if(x >= 0 && x  < size && y - 1 >= 0 && y - 1 < size){
            check.add(tileLocations[x][y -1].getText());
        }
        else{
            check.add("");
        }

        //Right adjacent location from current JButton 
        if(x  >= 0 && x  < size && y + 1 >= 0 && y + 1 < size){
            check.add(tileLocations[x][y + 1].getText());
        }
        else{
            check.add("");
        }

        //Bottom left location from current JButton.
        if(x + 1 >= 0 && x + 1 < size && y - 1 >= 0 && y - 1 < size){
            check.add(tileLocations[x + 1][y -1].getText());
        }
        else{
            check.add("");
        }

        //Below location from current JButton.
        if(x + 1 >= 0 && x + 1 < size && y >= 0 && y < size){
            check.add(tileLocations[x +1][y].getText());
        }
        else{
            check.add("");
        }

        //Bottom right location from current JButton.
        if(x + 1 >= 0 && x + 1 < size && y + 1 >= 0 && y + 1 < size){
            check.add(tileLocations[x + 1][y +1].getText());
        }
        else{
            check.add("");
        }

        //Skipped colors decides which case to run based of the value from the previous array list.
        ArrayList<Integer> skipped = new ArrayList();
        int i = 0;
        for(String color : check)
        {
            //If color text is "" empty do case 8 (basically skip in the next section).
            if(color.equals("")){

                skipped.add(8);
            }
            //If the color is same as current player e.g B and player is B then case is 8 as you cant capture your own piece.
            else if(color.equals(currentPlayer)){
                skipped.add(8);
            }
            //If current player is B and found disk is W then add the location where it was found and same for vice versa.
            else if(!(color.equals(currentPlayer))){
                skipped.add(i);
            }
            i++;
        }

        //Check all eight positions topleft, above , topright etc for further of the opposite color (captures) from the skipped array list values.
        captures = 0;
        for(Integer location : skipped){
            //Intially continue checking and don't stop until told otherwise , postion is 1 (the first area we want to check);
            boolean continueCheck = true;
            notStop = true;
            int position = 1;
            
            //Switch the value in skipped and do the checking of all sorrounding tiles e.g if the skipped value was 3 then go to case 3 and check all tiles left adjacent 
            //this will decide in colorPlacent(x, y , position) if the capture is valid.
            switch(location){
                //Location 0 top left.
                case 0:
                while(notStop){

                    int xC = x - position;
                    int yC = y - position;

                    colorPlacement(xC, yC, position);
                    position++;
                }
                clearList();
                break;
                //Location 1 above.
                case 1: 
                while(notStop){
                    int xC = x - position;
                    int yC = y;

                    colorPlacement(xC, yC, position);
                    position++;
                }
                clearList();
                break;
                //Location 2 top right.
                case 2:
                while(notStop){
                    int xC = x - position;
                    int yC = y + position;

                    colorPlacement(xC, yC, position);
                    position++;
                }
                clearList();
                break;
                //Location left adjacent.  
                case 3: 
                while(notStop){
                    int xC = x;
                    int yC = y - position;

                    colorPlacement(xC, yC, position);
                    position++;
                }
                clearList();
                break;
                //Location right adjacent.
                case 4: 
                while(notStop){
                    int xC = x;
                    int yC = y + position;

                    colorPlacement(xC, yC, position);
                    position++;
                }
                clearList();
                break;
                //Location is bottom left.
                case 5: 
                while(notStop){
                    int xC = x + position;
                    int yC = y - position;

                    colorPlacement(xC, yC, position);
                    position++;
                }
                clearList();
                break;
                //Location is below.
                case 6: 
                while(notStop){
                    int xC = x + position;
                    int yC = y;

                    colorPlacement(xC, yC, position);
                    position++;
                }
                clearList();
                break;
                //Location is bottom right.
                case 7: 
                while(notStop){
                    int xC = x + position;
                    int yC = y + position;
                    colorPlacement(xC, yC, position);
                    position++;
                }
                clearList();
                break;
                case 8: //Do nothing this is not a legal capture or/and legal location of a capturing piece.
                break;
            }
        }
        
        //If captures is greater than 0 then this capture was possible.
        if(captures > 0)
        {
            isLegal = true;
        }
        else{
            isLegal = false;
        }

    }
    
    /**
     *  Return is the  move is a legal one.
     *  
     *  @return The move is legal or not
     */
    public boolean isLegal()
    {
        return isLegal;
    }
    
    /**
     *  Computes if a move is legal or not used in the move method when checking both players.
     *  
     *  @return The move was possible or the move was not possible on the board
     */
    public boolean canMove()
    {
        //Assume that there is not a move possible
        canMove = false;
        //We are checking and not capturing for real.
        isCheck = true;
        
        boolean continueCheck = true;
        Reversi.updateCount();
        //Has the time come to stop playing? has someone won the game if not continue.
        testEndGame();

        while(!(canMove) && continueCheck){
            for(int x = 0; x < size; x ++){
                for(int y = 0; y < size; y ++){
                    // Is this button empty  by checking its text.
                    String text = tileLocations[x][y].getText();
                    //Has the button already been pressed? return true if enabled and clickable
                    boolean clickable = tileLocations[x][y].isEnabled();
                    if(text.equals("") && clickable)
                    {
                        //Periodically check every JButton location
                        capture(x, y);
                    }
                    //If we reach the end of the board (not including the outer border) and can move is still false then we don't need to keep checking.
                    if(x == size - 1  && y == size - 1  && canMove == false){
                        continueCheck = false;
                    }
                }
            }
        }

        if(canMove == true){
            //Legal move for that player.
            isLegal = true;
            //There is at least one move possible so we dont need to check instead we capture next.
            isCheck = false;

        }
        else if(canMove == false){
            //Decide which player cannot move and notify via a dialog if we need to, but not if its the end of game.
            if(currentPlayer.equals("B") && !(gameOver)){
                if( dialog){
                    Reversi.setMessageCantTakeTurn("B");
                    JOptionPane.showOptionDialog(Reversi.getFrame(), "Black cannot take a move", "Cannot Take Turn", JOptionPane.INFORMATION_MESSAGE, 0, null, new String[]{"SKIP TURN"}, null);
                }
                currentPlayer = "W";
                bCantMove= true;
                isLegal = false;
            }

            else if(currentPlayer.equals("W") && !(gameOver)){
                if(  dialog){
                    Reversi.setMessageCantTakeTurn("W");
                    JOptionPane.showOptionDialog(Reversi.getFrame(), "White cannot take a move", "Cannot Take Turn", JOptionPane.INFORMATION_MESSAGE, 0, null, new String[]{"SKIP TURN"}, null);
                }
                currentPlayer = "B";
                wCantMove = true;
                isLegal = false;
            }
        }
        //If both players cannot move then it is game over.
        if(bCantMove && wCantMove)
        {
            gameOver = true;
            bCantMove = false;
            wCantMove = false;
            Reversi.updateCount();
            retWinner();
            //Start the new game and wait for the user to click Play again.
            reset();
            setUp();
        }
        dialog = true;
        //Update counts after every check.
        Reversi.updateCount();
        return canMove;
    }

    /**
     * Check if both players can take a move one after the other this will decide end game
     * scenarios when the board is not actaully full especially on smaller boards such as 4x4
     * where players cannot take a turn even if there are free spaces.
     */
    public void checkBothPlayers()
    {
        //We don't need to show dialogs here if it is end game then the end game dialog will show.
        dialog = false;
        //Store the current player and alternate from W to B and change current player back.
        String original = currentPlayer;
        currentPlayer = "W";
        dialog = false;
        canMove();
        currentPlayer = "B";
        dialog = false;
        canMove();       
        currentPlayer = original;
    }
    
    /**
     * Return the winner so the GUI can do its work in the Reversi class , tell the user who won the game.
     */
    public void retWinner()
    {
        if(Reversi.retW() > Reversi.retB()){
            winner ="W";
            Reversi.updateSeshScore(winner);
            JOptionPane.showOptionDialog(Reversi.getFrame(), "GAME OVER White Wins ", "White Wins", JOptionPane.INFORMATION_MESSAGE, 0, null, new String[]{"END GAME"}, null);
        }
        else if (Reversi.retB() > Reversi.retW()){
            winner ="B";
            Reversi.updateSeshScore(winner);
            JOptionPane.showOptionDialog(Reversi.getFrame(), "GAME OVER Black Wins ", "Black Wins", JOptionPane.INFORMATION_MESSAGE, 0, null, new String[]{"END GAME"}, null);
        }
        //Allow player to play agiain.
        dialog = false;
        Reversi.playAgain();
        Reversi.updateCount();
    }

}
