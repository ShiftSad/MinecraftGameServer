package codes.shiftmc.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;

import java.nio.charset.StandardCharsets;

public final class ProtocolHelper {

    public static boolean canReadVarInt(ByteBuf buf) {
        if (!buf.isReadable()) return false;
        buf.markReaderIndex();

        try {
            int bytesChecked = 0;
            byte read;
            do {
                if (!buf.isReadable()) {
                    return false;
                }
                read = buf.readByte(); // Temporarily consume the byte
                bytesChecked++;

                if (bytesChecked > 5) return false; // Treat as cannot read / malformed
            } while ((read & 0b10000000) != 0);

            return true;
        } finally {
            buf.resetReaderIndex();
        }
    }

    public static int readVarInt(ByteBuf buf) {
        if (buf instanceof EmptyByteBuf) {
            return -1;
        }

        int numRead = 0;
        int result = 0;
        byte read;
        int initialReaderIndex = buf.readerIndex();

        do {
            if (!buf.isReadable()) {
                buf.readerIndex(initialReaderIndex);
                return -1;
            }

            read = buf.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                // Malformed VarInt - DO NOT RESET INDEX
                return -1;
            }
        } while ((read & 0b10000000) != 0); // Check continuation bit

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

    public static void writeVarInt(ByteBuf buf, int value) {
        do {
            byte temp = (byte)(value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= (byte) 0b10000000;
            }
            buf.writeByte(temp);
        } while (value != 0);
    }

    public static void writeString(ByteBuf buf, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }
}
