import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Scanner; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class CGRA_Project_Submission___stanleluka___300479577 extends PApplet {

//ALL CODE AND ART INCLUDED WRITTEN AND OWNED BY LUKAS ROLF STANLEY

//Scaling variables - must be changed in sync due to limitations
float pixelXScale;
float pixelYScale;
float screenDenominator = 1.25f;
int effectiveHeightPixels = 192;
int effectiveWidthPixels = 360;
int distanceBetweenButtons = 10;

//Gameplay related variables
level currentLevel;
boolean isLevelActive = false;
int numberOfLevels = 2;
boolean victoryState[] = new boolean[numberOfLevels];
button levelButtonArray[] = new button[numberOfLevels];

//Tables that hold the CSV information of the sprites for when they need to be drawn
int[][] menuBackGroundRGB = { {0, 162, 232}, {255, 242, 0}, {255, 201, 14}, {163, 163, 163}, {125, 125, 125} }; //RGB for the menus's colours (stored internally, could be put in a settings file)
int menuBackGroundArray[][];       //locations of background image's pixels
int buttonNotCompleteArray[][];   //locations of button image's pixels
int buttonCompleteArray[][];     //locations of button image's pixels
int buttonDimensions = 20;

//Initializes and loads as needed for the main menu
public void setup() {

  //sets up pixel scale
  pixelYScale = (displayHeight/effectiveHeightPixels)/screenDenominator;
  pixelXScale = (displayWidth/effectiveWidthPixels)/screenDenominator;

  //Sets up temporary variables to make equation more visually clear
  int buttonAreaOverallWidth = levelButtonArray.length*(distanceBetweenButtons-1)+levelButtonArray.length*buttonDimensions;
  int buttonInitialOffset = effectiveWidthPixels/2-(buttonAreaOverallWidth/2);
  for (int i = 0; i < victoryState.length; i++) {

    victoryState[i]=false;
    levelButtonArray[i]=new button(buttonInitialOffset+i*buttonDimensions+i*distanceBetweenButtons, effectiveHeightPixels/2, buttonDimensions, buttonDimensions, pixelXScale, pixelYScale);
  }

  //Load the CSV images into arrays
  menuBackGroundArray = readCSVIntoArray("images/Backgrounds/mainMenuBackGround.csv", effectiveWidthPixels, effectiveHeightPixels); //locations of background image's pixels
  buttonNotCompleteArray = readCSVIntoArray("images/widget/buttonUncomplete.csv", buttonDimensions, buttonDimensions);
  buttonCompleteArray = readCSVIntoArray("images/widget/buttonComplete.csv", buttonDimensions, buttonDimensions);

  //Ensure everything is drawn as wanted - noStroke is essential for the game's look, although a stroke can look visually interesting
  noStroke();
  
  background(255);
  //activateLevel(1);
}

/** 
 * Does everything.
 */
public void draw() {

  if (isLevelActive == true) {    

    if (currentLevel.updateHasWon()) {

      isLevelActive=false;
      victoryState[currentLevel.getCurrentLevel()-1]=true;
    } else if (currentLevel.updateHasLost()) {

      isLevelActive=false;
    } else {

      currentLevel.drawLevel();
    }
  }

  if (isLevelActive == false) {

    drawMenuItem(menuBackGroundArray, 0, 0);
    drawButtons();
  }
}

/** 
 * Button event that loads the level selected
 */
public void activateLevel(int levelNumber) {

  isLevelActive=true;
  currentLevel = new level(levelNumber, effectiveHeightPixels, effectiveWidthPixels, screenDenominator);
  currentLevel.loadLevel();
}

public void drawButtons() {

  for (int i = 0; i < numberOfLevels; i++) {

    if (victoryState[i]==true) {

      drawMenuItem(buttonCompleteArray, levelButtonArray[i].getNonScaledX(), levelButtonArray[i].getNonScaledY());
    } else {

      drawMenuItem(buttonNotCompleteArray, levelButtonArray[i].getNonScaledX(), levelButtonArray[i].getNonScaledY());
    }
  }
}

/** 
 * Draws a loaded image in the correct pixel format
 */
public void drawMenuItem(int[][] imageToDraw, int xPos, int yPos) {

  // Currently loaded int from screen representing tile - used to draw correct tile color
  int currentPlannedTile;

  for ( int widthIndex=0; widthIndex < imageToDraw.length; widthIndex++ ) {

    for ( int heightIndex=0; heightIndex < imageToDraw[0].length; heightIndex++ ) {


      currentPlannedTile = imageToDraw[widthIndex][heightIndex];

      if (currentPlannedTile>=0) {

        drawPixel(xPos+widthIndex, yPos+heightIndex, menuBackGroundRGB[currentPlannedTile][0], menuBackGroundRGB[currentPlannedTile][1], menuBackGroundRGB[currentPlannedTile][2]);
      }
    }
  }
}

/** 
 * Loads an image (stored as CSV) for UI into 2D array, returning said array
 */
public int[][] readCSVIntoArray(String fname, int imageWidth, int imageHeight) {

  String[] lines = loadStrings(fname);
  int screenArray[][] = new int[imageWidth][imageHeight];

  for ( int heightIndex=0; heightIndex < imageHeight; heightIndex++ ) {



    Scanner scan = new Scanner(lines[heightIndex]);
    scan.useDelimiter(",");

    for ( int widthIndex=0; widthIndex < imageWidth; widthIndex++ ) {

      if (scan.hasNextInt()) {

        screenArray[widthIndex][heightIndex] = scan.nextInt();
      } else { 

        println("File reading failed - missing tile on index height = "+heightIndex+" width = "+widthIndex);
        scan.close();
        return null;
      }
    }

    scan.close();
  }


  return screenArray;
}

/** 
 * Helper method to draw pixels using accurate scaling
 */
public void drawPixel(int xLoc, int yLoc, float red, float green, float blue) {

  fill(red, green, blue);
  rect(xLoc*pixelXScale, yLoc*pixelYScale, pixelXScale, pixelYScale);
}

/** 
 * Mouse click events handling - checks if any buttons were pressed for level loading
 */

public void mouseClicked() {

  if (isLevelActive==false) {

    for (int i = 0; i < levelButtonArray.length; i++) {

      if (levelButtonArray[i].wasPressed(mouseX, mouseY)) {

        activateLevel(i+1);
        return;
      }
    }
  }
  return;
}

/** 
 * KEY PRESS HANDLING BELOW - PASSES ALL INPUTS TO CURRENT LEVEL
 */

public void keyPressed()
{
  if (isLevelActive==true) {
    if (key == 'w'||key == ' '||key == 'W')
    {
      currentLevel.keyBeenPressed("jump", true);
    }
    if (key == 'a'||key == 'A')
    {
      currentLevel.keyBeenPressed("left", true);
    } else if (key == 'd'||key == 'D')
    {
      currentLevel.keyBeenPressed("right", true);
    }
    //else if (key == 's')
    //{
    //Unused ducking input
    //}
  }
}
public void keyReleased()
{
  if (isLevelActive==true) {
    if (key == 'w'||key == ' '||key == 'W')
    {
      currentLevel.keyBeenPressed("jump", false);
    }
    if (key == 'a'||key == 'A')
    {
      currentLevel.keyBeenPressed("left", false);
    } else if (key == 'd'||key == 'D')
    {
      currentLevel.keyBeenPressed("right", false);
    }
    //else if (key == 's')
    //{
    //Unused ducking input 
    //}
  }
}

// All self explanatory, and all self written in order to have pixel scaling function (despite how generic code is)
class button {
  
  float startX;
  float startY;
  int nonScaledX;
  int nonScaledY;
  float buttonWidth;
  float buttonHeight;
  
  button(int startXPassed, int startYPassed, int buttonWidthPassed, int buttonHeightPassed, float pixelXScale, float pixelYScale){
  
    startX=startXPassed*pixelXScale;
    startY=startYPassed*pixelYScale;
    nonScaledX=startXPassed;
    nonScaledY=startYPassed;
    buttonWidth=buttonWidthPassed*pixelXScale;
    buttonHeight=buttonHeightPassed*pixelYScale;
  
  }
  
  public boolean wasPressed(float mouseXLoc, float mouseYLoc){
  
    boolean isPressed = false;

    if(mouseXLoc<=startX+buttonWidth&&mouseXLoc>=startX){
      
      if(mouseYLoc<=startY+buttonHeight&&mouseYLoc>=startY){
      
        isPressed=true;
      
      }
      
    }
    
    return isPressed;
  
  }
  
  public int getNonScaledX(){
    
    return nonScaledX;
    
  }
  
  public int getNonScaledY(){
    
    return nonScaledY;
    
  }
  
}


class level {

  int numberOfScreens = 1;
  int numberOfScreenTypes = 1;
  int levelWidth;
  int tileArray[][][];  //Screen Index, Screen width, screen height (collection of "screens")
  int screenSelectionPool[];
  int backGroundArray[][];  //width, height (single "screen" for background)
  int levelNumber;
  int effectiveWidthPixels;
  int effectiveHeightPixels;
  int playerStartPixel;

  int[] screenOrder = new int[numberOfScreens];
  int[] winningTiles;
  int[][] tileRGB; //RGB of tiles - loaded from level's file
  int[][] backGroundRGB = { {157, 217, 234}, {255, 255, 255} }; //Not loaded from the level's file 
  int[][][] dinoCollisionArrays;
  String levelName;
  float pixelXScale;
  float pixelYScale;
  player levelPlayer;

  /** 
   * Initialise the level
   */
  level(int levelIndex, int effectiveHPixels, int effectiveWPixels, float screenDenominator) {

    readLevelSettings("levels/level"+levelIndex+"/levelInfo.txt");
    effectiveWidthPixels = effectiveWPixels;
    effectiveHeightPixels = effectiveHPixels;
    levelWidth = numberOfScreens*effectiveWidthPixels;
    tileArray = new int[numberOfScreens][][];
    screenSelectionPool = new int[numberOfScreenTypes]; //The first Two screens are dedicated start and end screens - to ensure the player is spawned above ground, and the player cannot end the level early
    dinoCollisionArrays = new int[3][][];
    levelNumber = levelIndex;
    pixelYScale = (displayHeight/effectiveHeightPixels)/screenDenominator;
    pixelXScale = (displayWidth/effectiveWidthPixels)/screenDenominator;
    levelPlayer = new player(effectiveHeightPixels, effectiveWidthPixels, screenDenominator, playerStartPixel, levelWidth, winningTiles);
  }

  /** 
   * Load all the screen files to be used in the level into an array of screens for efficient access
   */
  public void loadLevel() {

    String filePath;

    filePath = "images/Backgrounds/"+levelNumber+".csv";
    backGroundArray = readScreenIntoArray(filePath);

    for ( int i=0; i < numberOfScreenTypes; i++ ) {

      screenSelectionPool[i]=i+2;
    }

    randomizeArrayOrder(screenSelectionPool);
    filePath = "levels/level"+levelNumber+"/"+0+".csv";
    tileArray[0] = readScreenIntoArray(filePath);
    filePath = "levels/level"+levelNumber+"/"+1+".csv";
    tileArray[tileArray.length-1] = readScreenIntoArray(filePath);
    if (numberOfScreenTypes>=1) {
      for ( int i=1; i < numberOfScreens-1; i++ ) {
        println("added screen "+screenSelectionPool[i]);
        filePath = "levels/level"+levelNumber+"/"+screenSelectionPool[i]+".csv";
        tileArray[i] = readScreenIntoArray(filePath);
      }
    }
  }

  public void randomizeArrayOrder(int[]arrayToRandomize) {

    int temp;
    int randomPick;

    for (int i=0; i<arrayToRandomize.length; i++) {

      randomPick  = (int)random(arrayToRandomize.length); 
      temp = arrayToRandomize[i]; 
      arrayToRandomize[i] = arrayToRandomize[randomPick]; 
      arrayToRandomize[randomPick]= temp;
    }
  }

  /** 
   * Draws the level
   */
  public void drawLevel() {
    //Update the arrays used for collision with dinosaur according to new position of player
    dinoCollisionArrays[0] = calculateLocalTiles(levelPlayer.getX(), levelPlayer.getDinoWidth());
    dinoCollisionArrays[1] = calculateLocalTiles(levelPlayer.getX()-levelPlayer.getDinoWidth(), levelPlayer.getDinoWidth());
    dinoCollisionArrays[2] = calculateLocalTiles(levelPlayer.getX()+levelPlayer.getDinoWidth(), levelPlayer.getDinoWidth());

    //Pass the updated arrays to the player object, then update the player's state before drawing everything
    levelPlayer.giveLocalArea(dinoCollisionArrays);
    levelPlayer.updatePlayer();
    drawBackGround();
    drawTiles();

    //drawTEST_AREA();  // call for test function

    levelPlayer.drawPlayer();
  }

  public int getCurrentLevel() {

    return levelNumber;
  }

  public boolean updateHasWon() {

    return levelPlayer.hasWon();
  }

  public boolean updateHasLost() {

    return levelPlayer.hasLost();
  }

  /** 
   * Passes any keyboard inputs on to the level's player (acts as a middleman to the player object from the main loop)
   */
  public void keyBeenPressed(String action, boolean actionType) {

    if (action=="jump") {
      levelPlayer.jumpInput(actionType);
    } else if (action=="left") {

      levelPlayer.moveLRInput(0, actionType);
    } else if (action=="right") {

      levelPlayer.moveLRInput(1, actionType);
    }
  }


  /** 
   * Calculates the local array of tiles to calculate both the area visible on screen to reduce the amount drawn, 
   * and return the areas ajacent to the player for collision checking. Max width = 1 screen.
   */
  public int[][] calculateLocalTiles(int startX, int areaWidth) {

    if (areaWidth>effectiveWidthPixels) {
      println("Area too large to print!"); 
      return new int[0][0];
    }

    int correctedStartX = startX;
    if (startX<0) {

      correctedStartX = 0;
    }

    int returnArray[][] = new int[areaWidth][effectiveHeightPixels];
    int currentArrayXIndex = 0;
    int screenIndexNumber = correctedStartX/effectiveWidthPixels;
    int screenStartPoint = correctedStartX%effectiveWidthPixels;
    int numberToDoInFirst = effectiveWidthPixels-screenStartPoint;
    int numberToDoInFinal = areaWidth-numberToDoInFirst;

    if (startX+areaWidth>levelWidth) {

      int offset = (startX+areaWidth)-levelWidth;
      correctedStartX-=offset;
      screenIndexNumber = correctedStartX/effectiveWidthPixels;
      screenStartPoint = correctedStartX%effectiveWidthPixels;
      numberToDoInFirst = effectiveWidthPixels-screenStartPoint;
      numberToDoInFinal = areaWidth-numberToDoInFirst;
    }

    for ( int firstScreenIndex = screenStartPoint; firstScreenIndex < effectiveWidthPixels; firstScreenIndex++ ) {
      if (currentArrayXIndex<areaWidth) {
        for ( int heightIndex=0; heightIndex < effectiveHeightPixels; heightIndex++ ) {

          returnArray[currentArrayXIndex][heightIndex] = tileArray[screenIndexNumber][firstScreenIndex][heightIndex];
        }
      }
      currentArrayXIndex++;
    }

    for ( int lastScreenIndex = 0; lastScreenIndex < numberToDoInFinal; lastScreenIndex++ ) {

      for ( int heightIndex=0; heightIndex < effectiveHeightPixels; heightIndex++ ) {

        returnArray[currentArrayXIndex][heightIndex] = tileArray[screenIndexNumber+1][lastScreenIndex][heightIndex];
      }
      currentArrayXIndex++;
    }

    return returnArray;
  }

  /** 
   * TEST FUNCTION (not called normally) - draws the areas for which the dinosaur checks for collision, used for visualization of physics checks
   */
  public void drawTEST_AREA() {

    if (levelPlayer.getX()<effectiveWidthPixels/2) {
      translate(levelPlayer.getX()*pixelXScale, 0);
    } else if (levelPlayer.getX()>levelWidth-effectiveWidthPixels/2) {
      translate((effectiveWidthPixels+levelPlayer.getX()-levelWidth)*pixelXScale, 0);
    } else {
      translate((effectiveWidthPixels/2)*pixelXScale, 0);
    }

    // Currently loaded int from screen representing tile - used to draw correct tile color
    int currentPlannedTile;
    int tilesOnScreen[][] = dinoCollisionArrays[0];

    for ( int widthIndex=0; widthIndex < tilesOnScreen.length; widthIndex++ ) {

      for ( int heightIndex=0; heightIndex < tilesOnScreen[0].length; heightIndex++ ) {

        currentPlannedTile = tilesOnScreen[widthIndex][heightIndex];
        //print(currentPlannedTile);
        if (currentPlannedTile>=0) {

          drawPixel(widthIndex, heightIndex, 255, 0, 255);
        }
      }
    }

    translate(-levelPlayer.getDinoWidth()*pixelXScale, 0);
    tilesOnScreen= dinoCollisionArrays[1];

    for ( int widthIndex=0; widthIndex < tilesOnScreen.length; widthIndex++ ) {

      for ( int heightIndex=0; heightIndex < tilesOnScreen[0].length; heightIndex++ ) {

        currentPlannedTile = tilesOnScreen[widthIndex][heightIndex];

        if (currentPlannedTile>=0) {

          drawPixel(widthIndex, heightIndex, 0, 255, 0);
        }
      }
    }

    translate(2*levelPlayer.getDinoWidth()*pixelXScale, 0);

    tilesOnScreen = dinoCollisionArrays[2];

    for ( int widthIndex=0; widthIndex < tilesOnScreen.length; widthIndex++ ) {

      for ( int heightIndex=0; heightIndex < tilesOnScreen[0].length; heightIndex++ ) {

        currentPlannedTile = tilesOnScreen[widthIndex][heightIndex];

        if (currentPlannedTile>=0) {

          drawPixel(widthIndex, heightIndex, 255, 255, 0);
        }
      }
    }
    translate(-levelPlayer.getDinoWidth()*pixelXScale, 0);

    if (levelPlayer.getX()<effectiveWidthPixels/2) {
      translate(-levelPlayer.getX()*pixelXScale, 0);
    } else if (levelPlayer.getX()>levelWidth-effectiveWidthPixels/2) {
      translate((-(effectiveWidthPixels+levelPlayer.getX()-levelWidth)*pixelXScale), 0);
    } else {
      translate(-(effectiveWidthPixels/2)*pixelXScale, 0);
    }
  }

  /** 
   * Draws the level's tiles from the loaded array
   */
  public void drawTiles() {

    // Currently loaded int from screen representing tile - used to draw correct tile color
    int currentPlannedTile;
    int tilesOnScreen[][] = calculateLocalTiles((levelPlayer.getX()-PApplet.parseInt((effectiveWidthPixels)/2)), effectiveWidthPixels);

    for ( int widthIndex=0; widthIndex < effectiveWidthPixels; widthIndex++ ) {

      for ( int heightIndex=0; heightIndex < effectiveHeightPixels; heightIndex++ ) {

        currentPlannedTile = tilesOnScreen[widthIndex][heightIndex];

        if (currentPlannedTile>=0) {

          drawPixel(widthIndex, heightIndex, tileRGB[currentPlannedTile][0], tileRGB[currentPlannedTile][1], tileRGB[currentPlannedTile][2]);
        }
      }
    }
  }

  /** 
   * Draws the level's background from the loaded array
   */
  public void drawBackGround() {

    // Currently loaded int from screen representing tile - used to draw correct tile color
    int currentPlannedTile;

    for ( int widthIndex=0; widthIndex < effectiveWidthPixels; widthIndex++ ) {

      for ( int heightIndex=0; heightIndex < effectiveHeightPixels; heightIndex++ ) {


        currentPlannedTile = backGroundArray[widthIndex][heightIndex];

        if (currentPlannedTile>=0) {

          drawPixel(widthIndex, heightIndex, tileRGB[currentPlannedTile][0], tileRGB[currentPlannedTile][1], tileRGB[currentPlannedTile][2]);
        }
      }
    }
  }

  /** 
   * Loads the level settings file
   */
  public void readLevelSettings(String fname) {

    String[] lines = loadStrings(fname);

    for ( int count=0; count < lines.length; count++ ) {
      Scanner scan = new Scanner(lines[count]);
      if (scan.hasNextInt()) {

        if (count==0) { 
          numberOfScreens = scan.nextInt();
        } else if (count==1) { 
          numberOfScreenTypes = scan.nextInt();
        } else if (count==2) { 
          playerStartPixel = scan.nextInt();
        } else if (count==3) { 
          winningTiles = new int[scan.nextInt()];
        } else if (count==4) { 

          for ( int winningTileCount=0; winningTileCount < winningTiles.length; winningTileCount++ ) {
            winningTiles[winningTileCount] = scan.nextInt();
          }
        } else if (count==5) { 

          tileRGB = new int[scan.nextInt()][3];
        } else if (count>5&&count<tileRGB.length+5) { 

          for ( int RGBVal=0; RGBVal < 3; RGBVal++ ) {
            tileRGB[count-6][RGBVal] = scan.nextInt();
          }
        }
      } 

      scan.close();
    }
  }



  /** 
   * Loads a given screen from a file given a string into a 2D array, returning said array
   */
  public int[][] readScreenIntoArray(String fname) {

    String[] lines = loadStrings(fname);
    int screenArray[][] = new int[effectiveWidthPixels][effectiveHeightPixels];

    for ( int heightIndex=0; heightIndex < effectiveHeightPixels; heightIndex++ ) {



      Scanner scan = new Scanner(lines[heightIndex]);
      scan.useDelimiter(",");

      for ( int widthIndex=0; widthIndex < effectiveWidthPixels; widthIndex++ ) {

        if (scan.hasNextInt()) {

          screenArray[widthIndex][heightIndex] = scan.nextInt();
        } else { 

          println("File reading failed - missing tile on index height = "+heightIndex+" width = "+widthIndex);
          scan.close();
          return null;
        }
      }

      scan.close();
    }


    return screenArray;
  }

  /** 
   * Helper method to draw pixels using accurate scaling
   */
  public void drawPixel(int xLoc, int yLoc, float red, float green, float blue) {

    fill(red, green, blue);
    rect(xLoc*pixelXScale, yLoc*pixelYScale, pixelXScale, pixelYScale);
  }
}
class player {

  float playerX;
  float playerY;
  float playerXVelocity = 0;
  float playerYVelocity = 0;
  float playerXAcceleration = 0.05f; //for walking
  float playerXMaxSpeed = 1.2f;
  float playerXDeAcceleration = 0.4f;
  float playerYAcceleration = 0.7f; //For jumping
  float playerGravity = 0.2f;
  int maxIncline = 2; //Max rise/run of slope dino can scale

  boolean jumpBeenPressed = false;
  boolean isTouchingGround = false;
  boolean inputAllowed;
  boolean movingLeft = false;
  boolean movingRight = false;
  boolean isGroundBelow = false;


  //win/loss scenario related values
  boolean hasDied = false;
  boolean hasWon = false;
  int[] winningTiles;

  boolean testFlag = true;
  boolean testFlag1 = true;

  //counter related variables (how long button has been held for)
  int jumpMaxPower = 15;
  int jumpCurrentPower = 0;
  int walkMaxPower = 15;
  int walkCurrentPower = 0;

  int effectiveWidthPixels;
  int effectiveHeightPixels;
  int maxLevelDistance;
  int playerCenterOffset;
  int highestValidY;
  int facingDirection = 0; //0 = left, 1 = right
  float pixelYScale;
  float pixelXScale;

  //Tileset of dino
  int[][] dinoRGB = { {0, 0, 0}, {255, 255, 255}, {237, 27, 36} }; //Black, White, Red

  //Collision and size related variables
  int dinoCollisionArrays[][][]; //Array of "tiles" close to dino to keep track of which "tiles" to collide with
  int dinoWidth = 11;
  int dinoHeight = 17;

  //visual offsets to allow visuals of dino to be larger than dino itself
  int dinoVisualWidth = 23;
  int dinoVisualHeight = 17;
  int dinoWidthVisualOffset = PApplet.parseInt((dinoVisualWidth-dinoWidth)/2);
  int dinoHeightVisualOffset = dinoVisualHeight-dinoHeight;
  int numberOfAnimationFrames = 6;
  int currentAnimationFrame = 0;
  float animationTimer = 0;
  float animationTime = 0.2f;


  player(int effectiveHeightPixelsGiven, int effectiveWidthPixelsGiven, float screenDenominator, int startY, int givenLevelWidth, int[] winningTilesPassed) {

    //Setpup of player = convinience/location/other variable setup based on passed information

    //size of a screen
    effectiveWidthPixels= effectiveWidthPixelsGiven;
    effectiveHeightPixels=effectiveHeightPixelsGiven;

    //level overall size
    maxLevelDistance = givenLevelWidth;

    //starting y position
    playerY = startY;

    //Pixel Scaling
    pixelYScale = (displayHeight/effectiveHeightPixels)/screenDenominator;
    pixelXScale = (displayWidth/effectiveWidthPixels)/screenDenominator;

    //Starting X location
    playerX = effectiveWidthPixels/2;

    //Tiles that signify the player having won if above them
    winningTiles = winningTilesPassed;

    //Center constant (player x is varies)
    playerCenterOffset = effectiveWidthPixels/2;

    inputAllowed = true;
  }

  public void giveLocalArea(int[][][] passedAreas) {

    dinoCollisionArrays = passedAreas;
  }

  public void updatePlayer() {

    isGroundBelow();
    updateGravity();
    updateDeathState();
    updateJump();
    updateWalk();
    updateXpos();
    updateYpos();
  }

  public boolean hasWon() {

    return hasWon;
  }

  public boolean hasLost() {

    return hasDied;
  }

  public void updateDeathState() {

    if (hasDied) {

      inputAllowed=false;
    }
  }

  /** 
   * Update the player's Y position based on their current y velocity - checks if this moves player out of stage, and prevents it if true
   */
  public void updateXpos() {

    if (playerX+playerXVelocity<0) {

      playerX=0;
      currentAnimationFrame = 0;
      animationTimer = 0;
    } else if (playerX+playerXVelocity>maxLevelDistance-dinoWidth) {

      playerX=maxLevelDistance-dinoWidth;
      currentAnimationFrame = 0;
      animationTimer = 0;
      hasWon = true;
    } else {

      playerX+=playerXVelocity;
    }

    if (playerXVelocity==0) {  
      animationTimer = 0;
      currentAnimationFrame = 0;
    } else {
      
      float playerSpeedNoDir = playerXVelocity;
      if(playerSpeedNoDir<0){playerSpeedNoDir=-playerSpeedNoDir;}
      animationTimer+=(playerSpeedNoDir/playerXMaxSpeed)*animationTime;
      if (PApplet.parseInt(animationTimer)>numberOfAnimationFrames-1) {

        animationTimer=0;
      }
    }
  }

  /** 
   * Update the player's Y position based on their current y velocity - checks if this moves player below stage, and results in a death if true
   */
  public void updateYpos() {

    playerY+=playerYVelocity;
    if (playerY>effectiveHeightPixels) {

      hasDied=true;
    }
  }

  /** 
   * Update the player's Y velocity based on gravity
   */
  public void updateGravity() {

    isTouchingGround();
    if (isTouchingGround==true) { 
      playerYVelocity=0;
      jumpCurrentPower = 0;
    }
    if (isTouchingGround==false) {

      playerYVelocity += playerGravity;
      if (playerY+playerYVelocity+dinoHeight > highestValidY && isGroundBelow) {
        //print(highestValidY);

        playerY=highestValidY-dinoHeight;
        playerYVelocity = 0;
        isTouchingGround=true;
      }
    }
  }

  /** 
   * Update the player's Y velocity based on their current jump inputs and jumping power
   */
  public void updateJump() {

    if ((jumpBeenPressed)&&(isTouchingGround)&&(inputAllowed)) {

      if (jumpCurrentPower==0) {
        jumpCurrentPower = jumpMaxPower;
      }
    }

    playerYVelocity-=jumpCurrentPower*(playerYAcceleration/jumpMaxPower);

    if (jumpBeenPressed&&jumpCurrentPower>0) {

      jumpCurrentPower--;
    } else {
      jumpCurrentPower=0;
    }
  }

  /** 
   * Update the player's X velocity based on their current walking inputs and walking power
   */
  public void updateWalk() {

    if ((movingLeft||movingRight)&&inputAllowed) {

      if (walkCurrentPower<walkMaxPower) {
        walkCurrentPower++;
        //println(walkCurrentPower);
      }
    }

    if (movingLeft&&playerXVelocity>-playerXMaxSpeed) {
      playerXVelocity+=-walkCurrentPower*(playerXAcceleration/walkMaxPower);
    } else if (movingRight&&playerXVelocity<playerXMaxSpeed) {
      playerXVelocity+=walkCurrentPower*(playerXAcceleration/walkMaxPower);
    }


    if (movingLeft==false&&movingRight==false&&walkCurrentPower>0) {

      walkCurrentPower-=3;

      if (walkCurrentPower<0) {
        walkCurrentPower=0;
      }

      //isTouchingGround();
      //if (isTouchingGround) {
      if (playerXVelocity<0) {
        playerXVelocity+=playerXDeAcceleration;
        if (playerXVelocity>0) {
          playerXVelocity=0;
        }
      } else if (playerXVelocity>0) {
        playerXVelocity-=playerXDeAcceleration;
        if (playerXVelocity<0) {
          playerXVelocity=0;
        }
      }
      //}
    } 

    if (playerXVelocity<0) {

      double distanceToLeft=findValidDistance(1);

      if (-playerXVelocity>distanceToLeft) {

        playerX-=distanceToLeft;
        playerXVelocity=0;
        //println("corrected");
      }
    } else if (playerXVelocity>0) {

      double distanceToRight=findValidDistance(2);

      if (playerXVelocity>distanceToRight) {

        playerX+=distanceToRight;
        playerXVelocity=0;
        //println("correctedRight");
      }
    }
  }

  /** 
   * See if the player has any ground tiles directly below them to determine if a jump input should result in a jump, among other things
   */
  public void isTouchingGround() {

    int currentlyEvaluatingTile;
    int maxY = PApplet.parseInt(playerY);
    if (maxY<0) {
      maxY=0;
    } else if (maxY>effectiveHeightPixels) {
      maxY=effectiveHeightPixels-1;
    }

    for ( int widthIndex=0; widthIndex < dinoWidth; widthIndex++ ) {

      currentlyEvaluatingTile = dinoCollisionArrays[0][widthIndex][maxY];

      if (currentlyEvaluatingTile>=0) {

        isTouchingGround=true;
        return;
      }
    }
    isTouchingGround=false;
    return;
  }

  /** 
   * See if the player has any ground tiles below them at all,
   * to determine if y position should be changed by a lesser amount to avoid clipping into the ground,
   * As well as see if the player is above the win condition tiles.
   */
  public void isGroundBelow() {

    int currentlyEvaluatingTile;
    int maxY = PApplet.parseInt(playerY);
    if (maxY<0) {

      maxY=0;
    } 

    highestValidY = dinoCollisionArrays[0][dinoWidth-1].length;
    boolean highestValidFound = false;

    for ( int widthIndex=0; widthIndex < dinoWidth; widthIndex++ ) {

      for ( int heightIndex=maxY; heightIndex < effectiveHeightPixels; heightIndex++ ) {

        currentlyEvaluatingTile = dinoCollisionArrays[0][widthIndex][heightIndex];

        if ((currentlyEvaluatingTile>=0)&&(heightIndex<highestValidY)) {

          highestValidFound = true;
          highestValidY = heightIndex;
          for ( int winConIndex=0; winConIndex < winningTiles.length; winConIndex++ ) {
            if (currentlyEvaluatingTile==winningTiles[winConIndex]) {

              hasWon = true;
              inputAllowed = false;
            }
          }
        }
      }

      if (highestValidFound) {

        isGroundBelow = true;
      } else {

        isGroundBelow = false;
      }
    }
  }

  /** 
   * Find the maximum number of tiles the dinosaur can travel before the incline is too steep - uses stepheight and the area directly ahead or behind of player.
   */
  public int findValidDistance(int direction) {

    int maxDistanceWithAppropriateIncline = 0;
    int currentlyEvaluatingTile;
    int currentStepPoints = maxIncline;
    int highestPoints[] = findHighestPoints(direction);
    int currentHeight = PApplet.parseInt(playerY+dinoHeight);
    double currentIncline;

    if (direction==2) {
      for ( int highPointIndex=0; highPointIndex < highestPoints.length; highPointIndex++ ) {

        if (highestPoints[highPointIndex]==-1) {
          //println("should not print");
          currentStepPoints = 0;
          maxDistanceWithAppropriateIncline++;
        } else {

          if (currentHeight-currentStepPoints<=highestPoints[highPointIndex]) {
            //println("leftwith with"+highPointIndex+" and "+currentHeight);
            currentHeight = highestPoints[highPointIndex];
            maxDistanceWithAppropriateIncline++;
            currentStepPoints = PApplet.parseInt(maxIncline);
          } else {
            if (testFlag1) {
              //println("was not "+currentHeight+currentStepPoints+" bigger than: "+highestPoints[highPointIndex]+" :]");
            }
            return maxDistanceWithAppropriateIncline;
          }
        }
      }
    } else if (direction==1) {

      for ( int highPointIndex=highestPoints.length-1; highPointIndex > -1; highPointIndex-- ) {

        if (highestPoints[highPointIndex]==-1) {
          // println("should not print");
          currentStepPoints = 0;
          maxDistanceWithAppropriateIncline++;
        } else {

          if (currentHeight-currentStepPoints<=highestPoints[highPointIndex]) {
            currentHeight = highestPoints[highPointIndex];
            //println("was "+currentHeight+currentStepPoints+" bigger than: "+highestPoints[highPointIndex]+" :]");
            //println("right with"+highPointIndex+" and "+currentHeight);
            maxDistanceWithAppropriateIncline++;
            currentStepPoints = PApplet.parseInt(maxIncline);
          } else {
            //println("was not "+currentHeight+currentStepPoints+" bigger than: "+highestPoints[highPointIndex]+" :]");
            return maxDistanceWithAppropriateIncline;
          }
        }
      }
    }
    testFlag1=false;
    return maxDistanceWithAppropriateIncline;
  }

  /** 
   * Find and return the highest points of the area ajacent to the player - to help in checking slope steepness
   */
  public int[] findHighestPoints(int direction) {

    int highestPoints[] = new int[dinoCollisionArrays[direction].length];
    int currentlyEvaluatingTile;

    for ( int widthIndex=0; widthIndex < dinoCollisionArrays[direction].length; widthIndex++ ) {

      highestPoints[widthIndex]=-1;

      for ( int heightIndex=0; heightIndex < effectiveHeightPixels; heightIndex++ ) {

        currentlyEvaluatingTile = dinoCollisionArrays[direction][widthIndex][heightIndex];

        if (currentlyEvaluatingTile>=0) {

          if (highestPoints[widthIndex]==-1) {

            highestPoints[widthIndex]=heightIndex;
          } else if (heightIndex<highestPoints[widthIndex]) {

            highestPoints[widthIndex]=heightIndex;
          }
        }
      }
    }

    return highestPoints;
  }

  /** 
   * Returns X pos of player
   */
  public int getX() { 
    return PApplet.parseInt(playerX);
  }

  /** 
   * Returns height of player
   */
  public int getY() { 
    return PApplet.parseInt(playerY);
  }

  public int getDinoWidth() { 
    return dinoWidth;
  }

  /** 
   * Returns width of player
   */
  public int getDinoHeight() { 
    return dinoHeight;
  }

  /** 
   * updates what direction the player is currently trying to move in based on keypress events
   */
  public void moveLRInput(int direction, boolean holdOrRelease) {

    if (holdOrRelease==true) {

      if (direction==0 && movingLeft==false && movingRight==false) {

        movingLeft = true;
      } else if (direction==1 && movingRight==false && movingLeft==false) {

        movingRight = true;
      }
    } else if (holdOrRelease==false) {

      if (direction==0 && movingLeft==true) {

        movingLeft = false;
      } else if (direction==1 && movingRight==true) {

        movingRight = false;
      }
    }
  }

  /** 
   * receives jump input and updates state of player inputs accordingly
   */
  public void jumpInput(boolean holdOrRelease) {
    if (holdOrRelease==true&&jumpBeenPressed==false) {

      jumpBeenPressed=true;
    } else if (holdOrRelease==false&&jumpBeenPressed==true) {

      jumpBeenPressed=false;
    }
  }


  /** 
   * Loads and draws player dinosaur images
   */
  public void drawPlayer() {

    currentAnimationFrame = PApplet.parseInt(animationTimer);
    String[] lines = loadStrings("images/DinoImages/dino"+currentAnimationFrame+".csv");

    int plannedPixel;
    int lastScreenCenterOffset = effectiveWidthPixels+PApplet.parseInt(playerX-dinoWidthVisualOffset)-maxLevelDistance;

    if (playerX<playerCenterOffset) {
      translate(PApplet.parseInt(playerX-dinoWidthVisualOffset)*pixelXScale, PApplet.parseInt(playerY)*pixelYScale);
    } else if (playerX>maxLevelDistance-playerCenterOffset) {
      translate(PApplet.parseInt(lastScreenCenterOffset)*pixelXScale, PApplet.parseInt(playerY)*pixelYScale);
    } else {
      translate((playerCenterOffset-dinoWidthVisualOffset)*pixelXScale, PApplet.parseInt(playerY)*pixelYScale);
    }
    for ( int heightIndex=0; heightIndex < dinoVisualHeight; heightIndex++ ) {

      Scanner scan = new Scanner(lines[heightIndex]);
      scan.useDelimiter(",");
      if(movingRight){ facingDirection=1;}
      else if(movingLeft){ facingDirection=0;}
      if (facingDirection==1) { //Draw the player facing right 
        for ( int widthIndex=0; widthIndex < dinoVisualWidth; widthIndex++ ) {

          if (scan.hasNextInt()) {

            plannedPixel = scan.nextInt();
            if (plannedPixel>=0) {

              drawPixel(widthIndex, heightIndex, dinoRGB[plannedPixel][0], dinoRGB[plannedPixel][1], dinoRGB[plannedPixel][2]);
            }
          } else { 

            println("File reading failed - missing pixel on index height = "+heightIndex+" width = "+widthIndex);
          }
        }
      } else if(facingDirection==0){   //Draw the player facing left  
        for ( int widthIndex=0; widthIndex < dinoVisualWidth; widthIndex++ ) {

          if (scan.hasNextInt()) {

            plannedPixel = scan.nextInt();
            if (plannedPixel>=0) {

              drawPixel(dinoVisualWidth-widthIndex, heightIndex, dinoRGB[plannedPixel][0], dinoRGB[plannedPixel][1], dinoRGB[plannedPixel][2]);
            }
          } else { 

            println("File reading failed - missing pixel on index height = "+heightIndex+" width = "+widthIndex);
          }
        }
      }
      scan.close();
    }
    if (playerX<playerCenterOffset) {
      translate(-PApplet.parseInt(playerX-dinoWidthVisualOffset)*pixelXScale, -PApplet.parseInt(playerY)*pixelYScale);
    } else if (playerX>maxLevelDistance-playerCenterOffset) {
      translate(-PApplet.parseInt(lastScreenCenterOffset)*pixelXScale, -PApplet.parseInt(playerY)*pixelYScale);
    } else {
      translate(-(playerCenterOffset-dinoWidthVisualOffset)*pixelXScale, -PApplet.parseInt(playerY)*pixelYScale);
    }
  }


  /** 
   * Helper method to draw pixels using accurate scaling
   */
  public void drawPixel(int xLoc, int yLoc, float red, float green, float blue) {

    fill(red, green, blue);
    rect(xLoc*pixelXScale, yLoc*pixelYScale, pixelXScale, pixelYScale);
  }
}
  public void settings() {  size(1440, 768) ; }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#FC00A5", "CGRA_Project_Submission___stanleluka___300479577" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
