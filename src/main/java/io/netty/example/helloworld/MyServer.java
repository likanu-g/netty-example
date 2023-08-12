package io.netty.example.helloworld;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class MyServer {

    public static void main(String[] args) throws InterruptedException {
        MyServer myServer = new MyServer();
        myServer.start();
    }

    public void start() throws InterruptedException {
        // 创建 EventLoopGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建 ServerBootstrap
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(8080))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new MyServerHandler());
                        }
                    });

            // 绑定端口号并启动服务器
            ChannelFuture future = bootstrap.bind().sync();
            System.out.println("服务器已启动，监听端口 8080");

            // 等待服务器关闭
            future.channel().closeFuture().sync();
        } finally {
            // 关闭 EventLoopGroup
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

