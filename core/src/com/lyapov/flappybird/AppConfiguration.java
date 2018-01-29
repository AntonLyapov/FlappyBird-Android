package com.lyapov.flappybird;

/*
 *  *  ****************************************************************
 *  *  *                  Developed by Anton Lyapov                   *
 *  *  *                     1st Online Solutions                     *
 *  *  *               http://www.1stonlinesolutions.bg               *
 *  *  *          Copyright by 1st Online Solutions, 01 2018           *
 *  *  ****************************************************************
 */
public class AppConfiguration {
    // Number of tubes
    public static final int NUMBER_OF_TUBES = 4;

    // Tube speed
    public static final float TUBE_VELOCITY = 4;

    // Positive gravity for increase bird 'y' position
    public static final float BIRD_POSITIVE_GRAVITY = -20;

    // Bird gravity
    public static final float BIRD_GRAVITY = 2;

    // Distance between tubes
    public static final float GAP = 800;


    // Image names
    public static final String IMAGE_NAME_BACKGROUND    = "bg.png";
    public static final String IMAGE_NAME_BIRD_1        = "bird.png";
    public static final String IMAGE_NAME_BIRD_2        = "bird2.png";
    public static final String IMAGE_NAME_TOP_TUBE      = "toptube.png";
    public static final String IMAGE_NAME_BOTTOM_TUBE   = "bottomtube.png";
    public static final String IMAGE_NAME_GAME_OVER     = "gameover.png";
}
