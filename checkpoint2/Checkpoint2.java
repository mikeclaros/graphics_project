// --------------------------------------------------------
// File:        Checkpoint2.java
// Authors:     Roberto Rodriguez, Sang Pham, Mike Claros
// Team:        SOF
// Class:       CS 445
//
// Assignment:  Check Point 2
// Date last modified: 5/18/2015
//
// Purpose:
// --------------------------------------------------------
package checkpoint2;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.nio.FloatBuffer;

public class Checkpoint2 {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;

    private FPCameraController2 fp;

    // Method: Checkpoint2
    // Purpose: Default constructor. Initializes camera.
    public Checkpoint2() {
        fp = new FPCameraController2(0, 0, 0);
    }

    // Method: createWindow
    // Purpose: Sets up the Display and creates the window
    private void createWindow() throws Exception {
        Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
        Display.setFullscreen(false);
        Display.setTitle("Check Point 2");
        Display.create();
    }
    
    
    //Method: initLightArrays
    //puprose: method to initialize lighting
    
    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }

    // Method: initGL
    // Purpose: Initialize the graphics components.
    private void initGL() {
        int fov = 70;
        float aspectRatio = (float)Display.getWidth() / (float)Display.getHeight();
        float near = 0.3f;
        float far = 1000;

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fov, aspectRatio, near, far);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light
        glEnable(GL_LIGHTING);//enables our lighting
        glEnable(GL_LIGHT0);//enables light0
    }

    // Method: start
    // Purpose: Begins the main functionality of the program
    public void start() {
        try {
            createWindow();
            initGL();
            fp.gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method: main
    // Purpose: Starting point of program
    public static void main(String[] args) {
        Checkpoint2 cp = new Checkpoint2();
        cp.start();
    }
}
