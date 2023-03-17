package netty.elite;

import io.netty.buffer.ByteBuf;

public class RPCService {
    private static final int DEFAULT_TIMEOUT_MILLIS = 1000;
    private final RPCMessageDataFrameProcessor processor;

    public RPCService(RPCMessageDataFrameProcessor processor) {
        this.processor = processor;
    }

    public ByteBuf call(String request) {
        return this.call(request, DEFAULT_TIMEOUT_MILLIS);
    }

    public ByteBuf call(String request, int timeout) {
        RPCMessage message = processor.call(request, timeout);
        if (message == null
                || message.getPayload() == null
                || message.getPayload().readableBytes() == 0) {

            if (message != null && message.getPayload() != null) {
                message.getPayload().release();
            }

            return null;
        }

        return message.getPayload();
    }
}
