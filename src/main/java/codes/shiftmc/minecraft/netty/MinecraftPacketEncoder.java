package codes.shiftmc.minecraft.netty;

import codes.shiftmc.minecraft.server.packet.server.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static codes.shiftmc.minecraft.util.ProtocolHelper.writeVarInt;

public class MinecraftPacketEncoder extends MessageToByteEncoder<ServerPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ServerPacket packet, ByteBuf out) {
        var temp = ctx.alloc().buffer();
        try {
            writeVarInt(temp, packet.getId());
            packet.serialize(temp);

            // Write the length prefix to the final buffer
            writeVarInt(out, temp.readableBytes());
            out.writeBytes(temp);
        } finally {
            temp.release();
        }
    }
}
