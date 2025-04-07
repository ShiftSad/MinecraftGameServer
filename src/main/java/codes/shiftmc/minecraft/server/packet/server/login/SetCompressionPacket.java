package codes.shiftmc.minecraft.server.packet.server.login;

import codes.shiftmc.minecraft.server.packet.server.ServerPacket;
import io.netty.buffer.ByteBuf;

import static codes.shiftmc.minecraft.util.ProtocolHelper.writeVarInt;

public record SetCompressionPacket(
        int threshold
) implements ServerPacket {

    private static final int ID = 0x03;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void serialize(ByteBuf buf) {
        writeVarInt(buf, threshold);
    }
}
