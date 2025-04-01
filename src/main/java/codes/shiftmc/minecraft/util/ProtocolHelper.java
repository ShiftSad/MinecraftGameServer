package codes.shiftmc.minecraft.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public final class ProtocolHelper {

    public static int readVarInt(ByteBuf buf) {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            if (!buf.isReadable()) {
                buf.resetReaderIndex();
                return -1;
            }
            read = buf.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) { // A VarInt is at most 5 bytes long for 32-bit integers
                buf.resetReaderIndex();
                return -1;
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public static String readString(ByteBuf buf) {
        int length = readVarInt(buf);
        if (buf.readableBytes() < length) {
            throw new IllegalArgumentException("Buffer does not have enough bytes to read the string.");
        }
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
