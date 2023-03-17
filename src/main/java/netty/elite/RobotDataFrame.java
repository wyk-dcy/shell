package netty.elite;

import io.netty.buffer.ByteBuf;

public class RobotDataFrame {
    private int frameSize;
    private int frameType;
    private ByteBuf payload;

    /**
     * Return the size of robot data frame
     *
     * @return the size of robot data frame
     */
    public int getFrameSize() {
        return frameSize;
    }

    /**
     * Set robot data frame size
     *
     * @param frameSize the size of robot data frame
     */
    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    /**
     * Return the type of robot data frame
     *
     * @return the type of robot data frame
     */
    public int getFrameType() {
        return frameType;
    }

    /**
     * Set the type of robot data frame
     *
     * @param frameType the type of robot data frame
     */
    public void setFrameType(int frameType) {
        this.frameType = frameType;
    }

    /**
     * Return the payload of robot data frame
     *
     * @return the payload of robot data frame
     */
    public ByteBuf getPayload() {
        return payload;
    }

    /**
     * Set the payload of robot data frame
     *
     * @param payload the payload of robot data frame
     */
    public void setPayload(ByteBuf payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "RobotDataFrame{" +
                "frameSize=" + frameSize +
                ", frameType=" + frameType +
                ", payload=" + payload +
                '}';
    }
}
