package codes.shiftmc.minecraft.server.packet.client.login;

import codes.shiftmc.minecraft.server.packet.client.ClientPacket;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static codes.shiftmc.minecraft.util.ProtocolHelper.readString;

public record LoginStartPacket(
        String name,
        @Nullable UUID uuid
) implements ClientPacket {

    public static LoginStartPacket deserialize(ByteBuf buf) {
        var name = readString(buf);
        UUID uuid = null;
        if (buf.isReadable()) {
            uuid = new UUID(buf.readLong(), buf.readLong());
        }

        return new LoginStartPacket(name, uuid);
    }
}
