package netty;

import concurrent.NamedThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import netty.elite.RPCMessageDataFrameProcessor;
import netty.elite.RobotDataFrameDecoder;
import netty.elite.RobotDataFrameHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author wuyongkang
 * @date 2022年01月08日 17:27
 */
public class NettyClient {
    private static final int CONNECT_TIMEOUT_MILLIS = 200;
    private boolean tryingToConnect = false;

    /*
     * 服务器端口号
     */
    private int port;

    /*
     * 服务器IP
     */
    private String host;
    private EventLoopGroup workerGroup;
    protected Bootstrap bootstrap;

    private RPCMessageDataFrameProcessor robotDataFrameProcessor;

    public NettyClient(int port, String host, RPCMessageDataFrameProcessor robotDataFrameProcessor) throws InterruptedException {
        this.port = port;
        this.host = host;
        this.robotDataFrameProcessor = robotDataFrameProcessor;
        start();
    }

    private boolean start() {

        try {
            workerGroup = new NioEventLoopGroup(new NamedThreadFactory("Primary-Net-Controller"));
            bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new RobotDataFrameHandler(robotDataFrameProcessor))
                    .handler(new RobotDataFrameDecoder())
                    .handler(new IdleStateHandler(4000, 0, 0, TimeUnit.MILLISECONDS));

            doConnect();

            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    private void doConnect() {
        ChannelFuture future = bootstrap.connect(host, port);
        future.addListener((ChannelFutureListener) futureListener -> {
            if (futureListener.isSuccess()) {
                tryingToConnect = false;
            } else {
                if (!tryingToConnect) {
                    tryingToConnect = true;
                }
                futureListener.channel().eventLoop().schedule(this::doConnect, 2000, TimeUnit.MILLISECONDS);
            }
        });
    }
}