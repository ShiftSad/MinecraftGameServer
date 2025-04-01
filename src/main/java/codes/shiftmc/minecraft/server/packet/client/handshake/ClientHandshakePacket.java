package codes.shiftmc.minecraft.server.packet.client.handshake;

import codes.shiftmc.minecraft.server.packet.client.ClientPacket;
import io.netty.buffer.ByteBuf;

import static codes.shiftmc.minecraft.util.ProtocolHelper.readString;
import static codes.shiftmc.minecraft.util.ProtocolHelper.readVarInt;

public record ClientHandshakePacket(
        int protocolVersion,
        String serverAddress,
        int serverPort,
        Intent intent
) implements ClientPacket {

    public ClientHandshakePacket {
        if (serverAddress.length() > 255) {
            throw new IllegalArgumentException("Server address is too long");
        }
    }

    public static ClientHandshakePacket deserialize(ByteBuf buf) {
        var protocolVersion = readVarInt(buf);
        var serverAddress = readString(buf);
        var serverPort = buf.readUnsignedShort();
        var intent = Intent.fromId(readVarInt(buf));

        return new ClientHandshakePacket(protocolVersion, serverAddress, serverPort, intent);
    }

    public enum Intent {
        STATUS, LOGIN, TRANSFER;

        public static Intent fromId(int id) {
            return switch (id) {
                case 1 -> STATUS;
                case 2 -> LOGIN;
                case 3 -> TRANSFER;
                default -> throw new IllegalArgumentException("Invalid intent id: " + id);
            };
        }

        public int id() {
            return ordinal() + 1;
        }
    }
}
