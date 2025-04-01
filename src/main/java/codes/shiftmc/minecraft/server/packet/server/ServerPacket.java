package codes.shiftmc.minecraft.server.packet.server;

import io.netty.buffer.ByteBuf;

public interface ServerPacket {

    int getId();
    void serialize(ByteBuf buf);
}
