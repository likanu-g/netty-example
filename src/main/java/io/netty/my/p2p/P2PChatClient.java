package io.netty.my.p2p;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class P2PChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new StringEncoder())
                                    .addLast(new ChatClientHandler());
                        }
                    });

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter your name: ");
            String name = reader.readLine();
            System.out.print("Enter peer IP address: ");
            String peerIp = reader.readLine();
            System.out.print("Enter peer port: ");
            int peerPort = Integer.parseInt(reader.readLine());

            NioSocketChannel channel = (NioSocketChannel) bootstrap.connect(peerIp, peerPort).sync().channel();

            Thread senderThread = new Thread(new MessageSender(channel));
            Thread receiverThread = new Thread(new MessageReceiver(channel));

            senderThread.start();
            receiverThread.start();

            senderThread.join();
            receiverThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}

class ChatClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        System.out.println("Peer: " + new String(bytes));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

class MessageSender implements Runnable {
    private final NioSocketChannel channel;

    public MessageSender(NioSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if ("exit".equalsIgnoreCase(line)) {
                    channel.close();
                    break;
                }
                ByteBuf buf = Unpooled.copiedBuffer(line.getBytes());
                channel.writeAndFlush(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class MessageReceiver implements Runnable {
    private final NioSocketChannel channel;

    public MessageReceiver(NioSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        try {
            channel.pipeline().fireChannelActive();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

