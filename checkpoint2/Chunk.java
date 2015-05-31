// --------------------------------------------------------
// File:        Chunk.java
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
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;
import java.util.Random;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] blocks;
    private int vboVertexHandle;
    private int VBOTextureHandle;
    private Texture texture;
    private int vboColorHandle;
    private float startX;
    private float startY;
    private float startZ;
    private Random r;

    // Method: Chunk
    // Purpose: Chuck constructor
    

    public Chunk(float startX, float startY, float startZ) {
        try{
            texture = TextureLoader.getTexture(
                "PNG",ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch(Exception e){
            System.out.print("He's dead jim");
        }
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        r = new Random();

        initializeBlocks();
        vboColorHandle = glGenBuffers();
        vboVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        rebuildMesh(startX, startY, startZ);

    }

    public void rebuildMesh(float startX, float startY, float startZ) {
        vboVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        vboColorHandle = glGenBuffers();
        
        Random r = new Random((int)System.currentTimeMillis());
        int power = 5 + r.nextInt(9-5);
        System.out.println(power);
        SimplexNoise noise = new SimplexNoise(/*(int)Math.pow((double)2,(double)power)*/128,0.250,(int)System.currentTimeMillis());
        
        FloatBuffer vertexPositionData =
                BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer vertexColorData =
                BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        
        FloatBuffer VertexTextureData =
                    BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        int xResolution = CHUNK_SIZE;
        int yResolution = CHUNK_SIZE;
        int zResolution = CHUNK_SIZE;
        int XEnd = CHUNK_SIZE;
        int YEnd = CHUNK_SIZE;
        int ZEnd = CHUNK_SIZE;
        
        
        float x = 0;
        float y = 0;
        float z = 0;
        float width = 0;
        float length = 0;
        
        
        
        
        for ( x = 0; /*(x < width) &&*/ (x < CHUNK_SIZE); x++) {
            for (z = 0; /*z < length &&*/ z < CHUNK_SIZE; z++) {

                int i = (int)(startX/2 + x *((XEnd -startX/2)/xResolution));
                int k = (int)(startZ/2 +  z *((ZEnd-startZ/2)/zResolution));
                float height = (startY/2 + (float)(100*noise.getNoise((int)x,(int)z))/2f * CUBE_LENGTH/2);
                height = height + 15.0f;
                //System.out.println(height);
                for (y = 0;(y < height) && (y < CHUNK_SIZE); y++) {
                    VertexTextureData.put(createTexCube(
                        (float) 0, (float) 0, blocks[(int)(x)][(int)(y)][(int)(z)]));
                    vertexPositionData.put(createCube(
                                      (float)(startX + x * CUBE_LENGTH),
                                    (float)(y * CUBE_LENGTH + (int)(CHUNK_SIZE * .8)),
                                    (float)(startZ + z * CUBE_LENGTH)));
                    vertexColorData.put(createCubeVertexCol(getCubeColor(
                            blocks[(int) x][(int) y][(int) z])));
                }
            }
        }
        
        VertexTextureData.flip();
        vertexColorData.flip();
        vertexPositionData.flip();
        
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER,0);
        
        
    }

    // Method: createCube
    // Purpose:
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
                // TOP
                x + offset, y + offset, z,
                x - offset, y + offset, z,
                x - offset, y + offset, z - CUBE_LENGTH,
                x + offset, y + offset, z - CUBE_LENGTH,

                // BOTTOM
                x + offset, y - offset, z - CUBE_LENGTH,
                x - offset, y - offset, z - CUBE_LENGTH,
                x - offset, y - offset, z,
                x + offset, y - offset, z,

                // FRONT
                x + offset, y + offset, z - CUBE_LENGTH,
                x - offset, y + offset, z - CUBE_LENGTH,
                x - offset, y - offset, z - CUBE_LENGTH,
                x + offset, y - offset, z - CUBE_LENGTH,

                // BACK
                x + offset, y - offset, z,
                x - offset, y - offset, z,
                x - offset, y + offset, z,
                x + offset, y + offset, z,

                // LEFT
                x - offset, y + offset, z - CUBE_LENGTH,
                x - offset, y + offset, z,
                x - offset, y - offset, z,
                x - offset, y - offset, z - CUBE_LENGTH,

                //RIGHT
                x + offset, y + offset, z,
                x + offset, y + offset, z - CUBE_LENGTH,
                x + offset, y - offset, z - CUBE_LENGTH,
                x + offset, y - offset, z
        };
    }

    // Method: createCubeVertexCol
    // Purpose:
    private float[] createCubeVertexCol(float[] cubeColorArray) {
        float[] cubeColors = new float[cubeColorArray.length * 6 * 4];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = cubeColorArray[i % cubeColorArray.length];
        }
        return cubeColors;
    }

    // Method: getCubeColor
    // Purpose:
    private float[] getCubeColor(Block block) {
//        switch (block.getTypeID()) {
//            case 1:
//                return new float[] {0, 1, 0};
//            case 2:
//                return new float[] {1, 0.5f, 0};
//            case 3:
//                return new float[] {0, 0f, 1f};
//            default:
//                return new float[] {1, 1, 1};
//        }
         return new float[] {1, 1, 1};
        
    }
    
    //Method: createTexCube
    //Purpose:
    
    private static float[] createTexCube(float x, float y, Block block){
        float offset = (1024f/16)/1024f;
        switch(block.getTypeID()){
            case 0: //grass block
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*2, y + offset*9,
                    x + offset*3, y + offset*9,
                    x + offset*3, y + offset*10,
                    x + offset*2, y + offset*10,
                    
                    // TOP!
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    
                    // FRONT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // BACK QUAD
                    
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    // LEFT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // RIGHT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    
                };
            case 1: //sand block
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*0, y + offset*11,
                    x + offset*1, y + offset*11,
                    x + offset*1, y + offset*12,
                    x + offset*0, y + offset*12,
                    // TOP!
                    x + offset*0, y + offset*11,
                    x + offset*1, y + offset*11,
                    x + offset*1, y + offset*12,
                    x + offset*0, y + offset*12,
                    // FRONT QUAD
                    x + offset*0, y + offset*11,
                    x + offset*1, y + offset*11,
                    x + offset*1, y + offset*12,
                    x + offset*0, y + offset*12,
                    // BACK QUAD                    
                    x + offset*0, y + offset*11,
                    x + offset*1, y + offset*11,
                    x + offset*1, y + offset*12,
                    x + offset*0, y + offset*12,
                    // LEFT QUAD
                    x + offset*0, y + offset*11,
                    x + offset*1, y + offset*11,
                    x + offset*1, y + offset*12,
                    x + offset*0, y + offset*12,
                    // RIGHT QUAD
                    x + offset*0, y + offset*11,
                    x + offset*1, y + offset*11,
                    x + offset*1, y + offset*12,
                    x + offset*0, y + offset*12,
                    
                    
                };
            case 2: //water block
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*15, y + offset*12,
                    x + offset*16, y + offset*12,
                    x + offset*16, y + offset*13,
                    x + offset*15, y + offset*13,
                    // TOP!
                    x + offset*15, y + offset*12,
                    x + offset*16, y + offset*12,
                    x + offset*16, y + offset*13,
                    x + offset*15, y + offset*13,
                    // FRONT QUAD
                    x + offset*15, y + offset*12,
                    x + offset*16, y + offset*12,
                    x + offset*16, y + offset*13,
                    x + offset*15, y + offset*13,
                    // BACK QUAD
                    x + offset*16, y + offset*13,
                    x + offset*15, y + offset*13,
                    x + offset*15, y + offset*12,
                    x + offset*16, y + offset*12,
                    // LEFT QUAD
                    x + offset*15, y + offset*12,
                    x + offset*16, y + offset*12,
                    x + offset*16, y + offset*13,
                    x + offset*15, y + offset*13,
                    // RIGHT QUAD
                    x + offset*15, y + offset*12,
                    x + offset*16, y + offset*12,
                    x + offset*16, y + offset*13,
                    x + offset*15, y + offset*13,
                    
                    
                };
            case 3: //dirt block
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    // TOP!
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    // FRONT QUAD
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    // BACK QUAD
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // LEFT QUAD
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    // RIGHT QUAD
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    
                    
                };
            case 4: //stone
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*6, y + offset*0,
                    x + offset*7, y + offset*0,
                    x + offset*7, y + offset*1,
                    x + offset*6, y + offset*1,
                    // TOP!
                    x + offset*6, y + offset*0,
                    x + offset*7, y + offset*0,
                    x + offset*7, y + offset*1,
                    x + offset*6, y + offset*1,
                    // FRONT QUAD
                    x + offset*6, y + offset*0,
                    x + offset*7, y + offset*0,
                    x + offset*7, y + offset*1,
                    x + offset*6, y + offset*1,
                    // BACK QUAD
                    x + offset*7, y + offset*1,
                    x + offset*6, y + offset*1,
                    x + offset*6, y + offset*0,
                    x + offset*7, y + offset*0,
                    // LEFT QUAD
                    x + offset*6, y + offset*0,
                    x + offset*7, y + offset*0,
                    x + offset*7, y + offset*1,
                    x + offset*6, y + offset*1,
                    // RIGHT QUAD
                    x + offset*6, y + offset*0,
                    x + offset*7, y + offset*0,
                    x + offset*7, y + offset*1,
                    x + offset*6, y + offset*1,
                    
                };
            case 5: //bedrock
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    // TOP!
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    // FRONT QUAD
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    // BACK QUAD
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    // LEFT QUAD
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    // RIGHT QUAD
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                };
            
        }
        
        return new float[] { 
            //Bottom Quad
            0,0,1,0,1,1,0,1,
            //Top Quad
            0,0,1,0,1,1,0,1,
            //Front Quad
            0,0,1,0,1,1,0,1,
            //Back Quad
            0,0,1,0,1,1,0,1,
            //Left Quad
            0,0,1,0,1,1,0,1,
            //Right Quad
            0,0,1,0,1,1,0,1            
        };
    }

    // Method: initializeBlocks
    // Purpose:
    private void initializeBlocks() {
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    float f = r.nextFloat();
                    //System.out.println(f);
                    if(y <= 5){
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    }
                    else if( y > 5 && y <= 10){
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    }
                    else if( y > 10 && y <=15){
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }
                    else if ( y > 15 && y <= 20){
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    }
                    else if(y > 20 && y <= 27){
                        if( x <= 15){
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                        }
                        else if( x > 15 && x <= 25){
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                        }
                        else if( x > 25 && x <= 30){
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                        }
                    }
                    else if( y == 28){
                        if( x <= 20){
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                        }
                        else if ( x > 20 && x <=30){
                            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                        }
                    }
                    else if( y > 28 && y <=30){
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    }
                }
            }
        }
    }
    
   // Method: render
    // Purpose:
    public void render() {
        //glPushMatrix();
        glPushMatrix();
        texture.bind();
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D,1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        
        glColorPointer(3, GL_FLOAT, 0, 0L);
        
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
}
