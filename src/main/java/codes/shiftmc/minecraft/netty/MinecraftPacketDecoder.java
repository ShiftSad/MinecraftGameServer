package codes.shiftmc.minecraft.netty;

import codes.shiftmc.minecraft.server.ProtocolState;
import codes.shiftmc.minecraft.server.packet.client.handshake.ClientHandshakePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static codes.shiftmc.minecraft.netty.NettyKeys.STATE_KEY;
import static codes.shiftmc.minecraft.util.ProtocolHelper.readVarInt;

public class MinecraftPacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 1) return;
        in.markReaderIndex();

        var packetId = readVarInt(in);
        if (packetId == -1) {
            return;
        }

        var state = ctx.channel().attr(STATE_KEY).get();
        if (state == null) {
            state = ProtocolState.HANDSHAKING;
        }

        switch (state) {
            case HANDSHAKING:
                if (packetId == 0x00) {
                    var packet = ClientHandshakePacket.deserialize(in);

                    System.out.println("ProtocolVersion: " + packet.protocolVersion());
                    System.out.println("ServerAddress: " + packet.serverAddress());
                    System.out.println("ServerPort: " + packet.serverPort());
                    System.out.println("Intent: " + packet.intent());

                    ctx.channel().attr(STATE_KEY).set(ProtocolState.values()[packet.intent().id()]);
                }
                break;
            case STATUS:
                break;
            case LOGIN:
                break;
            case PLAY:
                break;
        }
    }

}
