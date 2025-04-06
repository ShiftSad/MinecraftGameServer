package codes.shiftmc.minecraft.server.packet.client.status;

import codes.shiftmc.minecraft.server.packet.client.ClientPacket;
import io.netty.buffer.ByteBuf;

public record PingRequestPacket(
        long time
) implements ClientPacket {

    public static PingRequestPacket deserialize(ByteBuf buf) {
        return new PingRequestPacket(buf.readLong());
    }
}
