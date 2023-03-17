package netty.elite;

import concurrent.ThreadPoolUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Executor;

@ChannelHandler.Sharable
public class RobotDataFrameHandler extends SimpleChannelInboundHandler<RobotDataFrame> {
    private static Executor executor = ThreadPoolUtils.newCpuIntensiveThreadExecutor("RobotMessage-Handler");
    private final RPCMessageDataFrameProcessor processor;

    public RobotDataFrameHandler(RPCMessageDataFrameProcessor processor) {
        this.processor = processor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RobotDataFrame msg) {
        executor.execute(() -> {
            try {
                processor.execute(msg);
            } catch (Exception e) {
            } finally {
                if (null != msg.getPayload()) {
                    msg.getPayload().release();
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.channel().close();
    }
}