package io.netty.tomcat.servlet;

import io.netty.tomcat.NettyRequest;
import io.netty.tomcat.NettyResponse;
import io.netty.tomcat.NettyServlet;

public class SecondServlet extends NettyServlet {
    @Override
    protected void doGet(NettyRequest request, NettyResponse response) {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(NettyRequest request, NettyResponse response) {
        response.write("<h1>this second servlet</h1>");
    }
}
