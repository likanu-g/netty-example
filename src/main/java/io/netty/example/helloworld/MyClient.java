package io.netty.example.helloworld;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class MyClient {

    public static void main(String[] args) throws InterruptedException {
        MyClient myClient = new MyClient();
        myClient.start();
    }

    public void start() throws InterruptedException {
        // 创建 EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 创建 Bootstrap
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("localhost", 8080))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MyClientHandler());
                        }
                    });

            // 连接服务器
            ChannelFuture future = bootstrap.connect().sync();
            System.out.println("已连接服务器");

            // 等待连接关闭
            future.channel().closeFuture().sync();
        } finally {
            // 关闭 EventLoopGroup
            group.shutdownGracefully();
        }
    }


}
