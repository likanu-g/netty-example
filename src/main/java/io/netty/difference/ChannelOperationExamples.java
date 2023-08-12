package io.netty.difference;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ChannelOperationExamples {
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    /**
     * Listing 4.5 Writing to a Channel
     */
    public static void writingToChannel() {
        ByteBuf buf = Unpooled.copiedBuffer("your data", CharsetUtil.UTF_8);
        ChannelFuture cf = CHANNEL_FROM_SOMEWHERE.writeAndFlush(buf);
        cf.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                System.out.println("Write successful");
            } else {
                System.err.println("Write error");
                future.cause().printStackTrace();
            }
        });
    }

    /**
     * Listing 4.6 Using a Channel from many threads
     */
    public static void writingToChannelFromManyThreads() {
        final ByteBuf buf = Unpooled.copiedBuffer("your data",
                CharsetUtil.UTF_8);
        Runnable writer = () -> CHANNEL_FROM_SOMEWHERE.write(buf.duplicate());
        Executor executor = Executors.newCachedThreadPool();

        // write in one thread
        executor.execute(writer);

        // write in another thread
        executor.execute(writer);
        //...
    }
}
