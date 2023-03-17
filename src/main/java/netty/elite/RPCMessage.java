package netty.elite;

import io.netty.buffer.ByteBuf;

import java.util.Objects;

public class RPCMessage {
    private final int id;
    private final ByteBuf payload;
    private final RobotDataFrame robotDataFrame;

    public RPCMessage(RobotDataFrame robotDataFrame) {
        Objects.requireNonNull(robotDataFrame);
        Objects.requireNonNull(robotDataFrame.getPayload());

        this.robotDataFrame = robotDataFrame;

        ByteBuf payload = robotDataFrame.getPayload();
        this.id = payload.readInt();
        this.payload = BufferUtils.newByteBuf(payload, payload.readableBytes()).asReadOnly();
    }

    /**
     * Return the id of the RPC message
     *
     * @return the id of the RPC message
     */
    public int getId() {
        return id;
    }

    /**
     * Return the payload of RPC message
     *
     * @return the payload of RPC message
     */
    public ByteBuf getPayload() {
        return payload;
    }

    /**
     * Return the robot data frame
     *
     * @return the robot data frame
     */
    public RobotDataFrame getRobotDataFrame() {
        return robotDataFrame;
    }

    @Override
    public String toString() {
        return "RPCMessage{" +
                "id=" + id +
                ", payload=" + payload +
                ", robotDataFrame=" + robotDataFrame +
                '}';
    }
}