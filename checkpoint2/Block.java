// --------------------------------------------------------
// File:        Block.java
// Authors:     Roberto Rodriguez, Sang Pham, Mike Claros
// Team:        SOF
// Class:       CS 445
//
// Assignment:  Check Point 2
// Date last modified: 5/18/2015
//
// Purpose:
// --------------------------------------------------------

// Class: Block
// Purpose:

package checkpoint2;

public class Block {
    private boolean isActive;
    private BlockType type;
    private float x, y, z;

    // Enum: BlockType
    // Purpose:
    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);

        private int blockID;

        BlockType(int blockID) {
            this.blockID = blockID;
        }

        public int getBlockID() {
            return blockID;
        }

        public void setBlockID(int blockID) {
            this.blockID = blockID;
        }
    }

    public Block(BlockType type) {
        this.type = type;
        isActive = false;
        x = 0;
        y = 0;
        z = 0;
    }

    private void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getTypeID() {
        return type.getBlockID();
    }
}
