package io.netty.tomcat;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

public class NettyResponse {

    private ChannelHandlerContext channelHandlerContext;

    private HttpRequest httpRequest;

    public NettyResponse(ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest) {
        this.channelHandlerContext = channelHandlerContext;
        this.httpRequest = httpRequest;
    }

    public void write(String responseContent) {
        if(responseContent == null || responseContent.equals(" ")) {
            return;
        }else {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(responseContent.getBytes(StandardCharsets.UTF_8)));

            response.headers().set("Content-Type", "text/html;");
            channelHandlerContext.write(response);
        }
    }
}
