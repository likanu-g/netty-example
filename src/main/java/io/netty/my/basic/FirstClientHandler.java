package io.netty.my.basic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class FirstClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + ": 客户端写出数据");
        //分配内存
        ByteBuf byteBuf = ctx.alloc().buffer();
        //准备发给服务器的数据
        byte[] bytes = "你好".getBytes(StandardCharsets.UTF_8);
        //将待发送数据写入到内存
        byteBuf.writeBytes(bytes);
        ctx.channel().writeAndFlush(byteBuf);
    }

}
