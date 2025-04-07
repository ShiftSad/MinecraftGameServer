package codes.shiftmc.minecraft.server.packet.server.login;

import codes.shiftmc.minecraft.mappings.Property;
import codes.shiftmc.minecraft.server.packet.server.ServerPacket;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static codes.shiftmc.minecraft.util.ProtocolHelper.writeString;
import static codes.shiftmc.minecraft.util.ProtocolHelper.writeVarInt;

public record LoginSuccessPacket(
        @NotNull UUID uuid,
        @NotNull String username,
        @NotNull List<Property> properties // Use a List, ensure it's never null
) implements ServerPacket {

    public LoginSuccessPacket(@NotNull UUID uuid, @NotNull String username) {
        this(uuid, username, Collections.emptyList());
    }

    public LoginSuccessPacket(@NotNull UUID uuid, @NotNull String username, @Nullable Property property) {
        this(uuid, username, property == null ? Collections.emptyList() : List.of(property));
    }

    public static final int ID = 0x02;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void serialize(ByteBuf buf) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        writeString(buf, username);

        writeVarInt(buf, properties.size());

        // Serialize each property
        for (Property prop : properties) {
            prop.serialize(buf);
        }
    }
}
