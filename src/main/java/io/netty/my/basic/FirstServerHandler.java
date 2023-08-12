package io.netty.my.basic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.Buffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class FirstServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String byteBuf = (String) msg;
        System.out.println(new Date() + " ：服务端读到数据 -> " + byteBuf);
        System.out.println("服务端写出数据");
        //分配内存
        ByteBuf buf = ctx.alloc().buffer();
        //定义返回客户端的数据
        byte[] bytes = "你好，服务端已经接收到你发送的数据！".getBytes(StandardCharsets.UTF_8);
        //将数据写入到内存
        buf.writeBytes(bytes);
        //写入数据到Channel
        ctx.channel().writeAndFlush(buf);
    }
}
