package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author wuyongkang
 * @date 2022年01月08日 17:27
 */
public class NettyServer {
    private int port;

    public NettyServer(int port) {
        this.port = port;
        bind();
    }

    private void bind() {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss, worker);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024); // 连接数
            bootstrap.option(ChannelOption.TCP_NODELAY, true); // 不延迟，消息立即发送
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); // 长连接
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel)
                        throws Exception {
                    ChannelPipeline p = socketChannel.pipeline();
                    p.addLast(new NettyServerHandler());// 添加NettyServerHandler，用来处理Server端接收和处理消息的逻辑
                }
            });
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            if (channelFuture.isSuccess()) {
                System.err.println("启动Netty服务成功，端口号：" + this.port);
            }
            // 关闭连接
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            System.err.println("启动Netty服务异常，异常信息：" + e.getMessage());
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            String i= "新的测试哦";
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(new NettyServer(10086));
    }
}