package me.vemacs.fakemcserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.IOException;

import me.vemacs.fakemcserver.data.StatusResponse;
import me.vemacs.fakemcserver.streams.MojewInputStream;
import me.vemacs.fakemcserver.streams.MojewOutputStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BasePacketHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private final SLPResponder responder;

    public BasePacketHandler(SLPResponder responder) {
        this.responder = responder;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MojewInputStream in = new MojewInputStream((ByteBuf) msg);
        // handshake
        int length = in.readInt();
        int packetID = in.readInt();
        if (packetID == 0) {
            int protocolVersion = -1;

            // status request
            try {
                protocolVersion = in.readInt();
                String serverAddress = in.readUTF();
                int serverPort = in.readUnsignedShort();
                int state = in.readInt();
                String addressPlusPort = serverAddress + ":" + serverPort;
                LOGGER.log(Level.INFO, "Received status request for ({}): length={}, version={}, state={}", addressPlusPort, length, protocolVersion, state);
            } catch (IOException ignored) {
                // status request packet is sent inconsistently, so we ignore it
            } finally {
                in.close();
            }

            // status response
            StatusResponse responsedata = responder.getStatusResponse(protocolVersion);
            if (responsedata != null) {
                String response = gson.toJson(responsedata).replace(
                        ChatConverter.ESCAPE + "", "\\u00A7"); // Mojew's parser needs this escaped (classic)

                LOGGER.log(Level.INFO, "Sending response: " + response);
                MojewOutputStream out = new MojewOutputStream(Unpooled.buffer());
                MojewOutputStream data = new MojewOutputStream(Unpooled.buffer());
                data.writeInt(0);
                data.writeUTF(response);
                data.close();
                out.writeInt(data.writtenBytes());
                out.write(data.getData());
                out.close();
                ctx.writeAndFlush(out.buffer());
            }
        } else if (packetID == 1) {
            // ping request
            long time = in.readLong();
            LOGGER.log(Level.INFO, "Received ping packet: length={}, time={}", length, time);

            // ping response
            MojewOutputStream out = new MojewOutputStream(Unpooled.buffer());
            MojewOutputStream data = new MojewOutputStream(Unpooled.buffer());
            data.writeInt(1);
            data.writeLong(time);
            data.close();
            out.writeInt(data.writtenBytes());
            out.write(data.getData());
            out.close();
            ctx.writeAndFlush(out.buffer());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
