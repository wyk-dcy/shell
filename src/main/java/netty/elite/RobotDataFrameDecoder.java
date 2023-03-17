package netty.elite;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RobotDataFrameDecoder extends ByteToMessageDecoder {
    private static final int ROBOT_DATA_FRAME_HEADER_SIZE = 5;


    private int frameLength = 0;
    private int frameType = 0;
    private int payloadLength = 0;

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        RobotDataFrame dataFrame = decode(ctx, in);
        if (dataFrame != null) {
            out.add(dataFrame);
        }
    }

    /**
     * Create a frame out of the {@link ByteBuf} and return it.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param in  the {@link ByteBuf} from which to read data
     * @return frame           the {@link ByteBuf} which represent the frame or {@code null} if no frame could
     * be created.
     */
    protected RobotDataFrame decode(
            @SuppressWarnings("UnusedParameters") ChannelHandlerContext ctx, ByteBuf in) {
        if (in.readableBytes() < ROBOT_DATA_FRAME_HEADER_SIZE) {
            return null;
        }

        if (frameLength <= 0) {
            frameLength = in.readInt();
            frameType = in.readUnsignedByte();
            payloadLength = frameLength - ROBOT_DATA_FRAME_HEADER_SIZE;

            if (payloadLength <= 0) {
                frameLength = 0;
            }
        }

        if (in.readableBytes() >= payloadLength) {
            ByteBuf payload = BufferUtils.newByteBuf(in, payloadLength);

            RobotDataFrame robotDataFrame = new RobotDataFrame();
            robotDataFrame.setFrameSize(frameLength);
            robotDataFrame.setFrameType(frameType);
            robotDataFrame.setPayload(payload);

            frameLength = 0;

            return robotDataFrame;
        }

        return null;
    }
}