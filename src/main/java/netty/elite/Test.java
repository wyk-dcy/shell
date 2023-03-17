package netty.elite;

import io.netty.buffer.ByteBuf;
import netty.NettyClient;

import javax.swing.*;

/**
 * @author wuyongkang
 * @date 2023年03月09日 11:54
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        RPCMessageDataFrameProcessor robotDataFrameProcessor = new RPCMessageDataFrameProcessor();
        new Thread(() -> {
            try {
                new NettyClient(30001, "192.168.51.232", robotDataFrameProcessor);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        RPCService rpcService = new RPCService(robotDataFrameProcessor);
        JFrame frame = new JFrame("Button Click Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 150);

        JButton button = new JButton("Click me");
        // 设置按钮的位置和大小
        // 添加按钮的点击事件
        // ...
        frame.add(button);

        // 添加其他组件和布局
        frame.setVisible(true);
        button.addActionListener(e -> {
            int result = 0;
            String req = "get_target_payload_mass()";
            ByteBuf response = rpcService.call(req);
            if (response == null) {
                result = -1;
            }

            result = -1;
            try {
                result = (int) response.readLong();
            } catch (Exception exception) {
            } finally {
                response.release();
            }
            System.out.println(result);
        });
    }
}