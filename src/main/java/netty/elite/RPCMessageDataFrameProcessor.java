package netty.elite;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class RPCMessageDataFrameProcessor {
    public static final int ROBOT_DATA_FRAME_TYPE_RPC = 22;
    public static final int RPC_MESSAGE_ID_SIZE = 4;
    private final AtomicInteger id = new AtomicInteger(1);
    private final ConcurrentHashMap<Integer, ReqEntry> calls = new ConcurrentHashMap<>();

    public synchronized boolean execute(RobotDataFrame frame) {
        if (frame == null
                || frame.getPayload() == null
                || frame.getPayload().readableBytes() < RPC_MESSAGE_ID_SIZE) {
            return false;
        }

        RPCMessage result = new RPCMessage(frame);
        Integer reqId = result.getId();
        if (calls.containsKey(reqId)) {
            ReqEntry request = calls.get(reqId);
            request.setMessage(result);
            calls.remove(reqId);
            request.getLatch().countDown();

            return true;
        } else {
            if (null != result.getPayload()) {
                result.getPayload().release();
            }
        }

        return false;
    }

    public int getRobotDataFrameType() {
        return ROBOT_DATA_FRAME_TYPE_RPC;
    }

    /**
     * Call request for RPC
     *
     * @param request    the RPC request
     * @param timeout    the timeout of the request
     * @return a {@link RPCMessage} instance
     */
    public RPCMessage call(String request, int timeout) {
        ReqEntry req = createRequest(request);
        return req.getMessage();
    }

    /**
     * Create a new RPC request instance
     *
     * @param request the request string
     * @return a new RPC request instance
     */
    private ReqEntry createRequest(String request) {
        StringBuilder req;
        int messageId = getNextMessageID();
        req = new StringBuilder()
                .append(String.format("rpc %d ", messageId))
                .append(request)
                .append("\n");

        return new ReqEntry(messageId, req);
    }

    /**
     * Return a new RPC message id
     *
     * @return a new RPC message id
     */
    private int getNextMessageID() {
        return id.incrementAndGet();
    }

    /**
     * Check unanswered RPC calls
     */
    private void checkUnansweredRpcCalls() {
    }

    /**
     * RPC Request Entry
     */
    static class ReqEntry {
        private final int id;
        private final StringBuilder req;
        private final CountDownLatch latch;
        private RPCMessage message;

        public ReqEntry(int id, StringBuilder req) {
            this.id = id;
            this.req = req;
            this.latch = new CountDownLatch(1);
        }

        public int getId() {
            return id;
        }

        public RPCMessage getMessage() {
            return message;
        }

        public void setMessage(RPCMessage message) {
            this.message = message;
        }

        public CountDownLatch getLatch() {
            return latch;
        }

        public StringBuilder getReq() {
            return req;
        }
    }
}
