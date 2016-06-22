package me.vemacs.fakemcserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.logging.log4j.LogManager;

public class SLPServer {

    static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    /* 2:01 PM EST - June 22 2016 */
    final SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a z - MMMM d yyyy");

    private int port;
    private SLPResponder responder;

    public SLPServer(int port, SLPResponder responder) {
        this.port = port;
        this.responder = responder;
    }

    public void run() throws Exception {
        LOGGER.info("Server started. Current time is " + dateFormat.format(Calendar.getInstance()));
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new BasePacketHandler(responder));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
