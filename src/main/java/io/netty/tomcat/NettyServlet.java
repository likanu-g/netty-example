package io.netty.tomcat;

public abstract class NettyServlet {
    protected void service(NettyRequest request, NettyResponse response) throws Exception{
        if("GET".equalsIgnoreCase(request.getRequestMethod())) {
            this.doGet(request, response);
        }else if("POST".equalsIgnoreCase(request.getRequestMethod())) {
            this.doPost(request, response);
        }else {
            throw new UnsupportedOperationException();
        }
    }

    protected abstract void doGet(NettyRequest request, NettyResponse response);

    protected abstract void doPost(NettyRequest request, NettyResponse response);
}
