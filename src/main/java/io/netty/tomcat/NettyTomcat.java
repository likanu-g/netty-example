package io.netty.tomcat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NettyTomcat {
    private int port = 8080;

    private Map<String, NettyServlet> servletMapping = new HashMap<>();

    private Properties webXml = new Properties();

    private void init() {
        //初始化加载web.xml配置文件
        String WEB_INF = this.getClass().getResource("/").getPath();
        try (FileInputStream fileInputStream = new FileInputStream(WEB_INF + "web.properties")) {
            webXml.load(fileInputStream);
            for (Object object : webXml.keySet()) {
                String key = object.toString();
                if (key.endsWith(".url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webXml.getProperty(key);
                    String classname = webXml.getProperty(servletName + ".className");
                    NettyServlet servlet = (NettyServlet) Class.forName(classname).newInstance();
                    servletMapping.put(url, servlet);
                }
            }
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void start() {

        init();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            channel.pipeline()
                                    .addLast(new HttpResponseEncoder())
                                    .addLast(new HttpRequestDecoder())
                                    .addLast(new NettyTomcatHandler());
                        }
                    })
                    //分配线程数量
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("服务器已经启动，监听端口为：" + port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    class NettyTomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof HttpRequest) {
                System.out.println("request connecting!");
                HttpRequest req = (HttpRequest) msg;
                NettyRequest request = new NettyRequest(ctx, req);
                NettyResponse response = new NettyResponse(ctx, req);
                String url = request.getURI();
                if(servletMapping.containsKey(url)) {
                    servletMapping.get(url).service(request,response);
                    System.out.println();
                }else {
                    response.write("<h1>404 not found!</h1>");
                }
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Close the connection when an exception is raised.
            cause.printStackTrace();
            ctx.close();
        }
    }

    public static void main(String[] args) {
        new NettyTomcat().start();
    }

}
