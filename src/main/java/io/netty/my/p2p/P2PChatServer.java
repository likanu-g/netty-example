package io.netty.my.p2p;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class P2PChatServer {
    private static final List<ChannelHandlerContext> clients = new ArrayList<>();
    private static final ConcurrentHashMap<ChannelHandlerContext, String> clientNames = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new StringDecoder())
                                    .addLast(new ChatServerHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(12345).sync();
            System.out.println("服务器已启动，监听端口 12345...");

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void addClient(ChannelHandlerContext ctx, String name) {
        clients.add(ctx);
        clientNames.put(ctx, name);
    }

    public static void removeClient(ChannelHandlerContext ctx) {
        clients.remove(ctx);
        clientNames.remove(ctx);
    }

    public static void broadcastMessage(String message, ChannelHandlerContext sender) {
        String senderName = clientNames.get(sender);
        String formattedMessage = senderName + ": " + message;
        for (ChannelHandlerContext ctx : clients) {
            ctx.writeAndFlush(formattedMessage + "\n");
        }
    }
}

class ChatServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("客户端已连接：" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String receivedMessage = (String) msg;
        System.out.println("收到消息：" + receivedMessage);

        P2PChatServer.broadcastMessage(receivedMessage, ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("客户端已断开连接：" + ctx.channel().remoteAddress());
        P2PChatServer.removeClient(ctx);
    }
}



