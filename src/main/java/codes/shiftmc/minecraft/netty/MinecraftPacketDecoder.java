package codes.shiftmc.minecraft.netty;

import codes.shiftmc.minecraft.server.ProtocolState;
import codes.shiftmc.minecraft.server.packet.client.handshake.ClientHandshakePacket;
import codes.shiftmc.minecraft.server.packet.server.status.ResponsePacket;
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
                    System.out.println("Handshake: " + packet);

                    ctx.writeAndFlush(new ResponsePacket());
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
