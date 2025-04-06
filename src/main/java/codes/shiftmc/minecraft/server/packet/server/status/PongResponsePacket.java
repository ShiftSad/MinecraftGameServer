package codes.shiftmc.minecraft.server.packet.server.status;

import codes.shiftmc.minecraft.server.packet.server.ServerPacket;
import io.netty.buffer.ByteBuf;

public record PongResponsePacket(
        long time
) implements ServerPacket {

    private static final int ID = 0x01;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void serialize(ByteBuf buf) {
        buf.writeLong(time);
    }
}
