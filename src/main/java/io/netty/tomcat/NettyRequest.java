package io.netty.tomcat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class NettyRequest {
    private ChannelHandlerContext channelHandlerContext;

    private HttpRequest httpRequest;

    public NettyRequest(ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest) {
        this.channelHandlerContext = channelHandlerContext;
        this.httpRequest = httpRequest;
    }

    public String getURI() {
        return httpRequest.uri();
    }

    public String getRequestMethod(){
        return httpRequest.method().name();
    }


}
