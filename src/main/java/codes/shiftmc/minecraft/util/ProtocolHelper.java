package codes.shiftmc.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;

import java.nio.charset.StandardCharsets;

public final class ProtocolHelper {

    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    public static boolean canReadVarInt(ByteBuf buf) {
        if (!buf.isReadable()) return false;
        buf.markReaderIndex();

        try {
            int position = 0;
            byte currentByte;

            while (true) {
                if (!buf.isReadable()) return false;
                currentByte = buf.readByte();

                if ((currentByte & CONTINUE_BIT) == 0) break;
                position += 7;

                if (position >= 32) return false; // VarInt is too big
            }

            return true;
        } finally {
            buf.resetReaderIndex();
        }
    }

    public static int readVarInt(ByteBuf buf) {
        if (buf instanceof EmptyByteBuf) {
            return -1;
        }

        int value = 0;
        int position = 0;
        byte currentByte;
        int initialReaderIndex = buf.readerIndex();

        while (true) {
            if (!buf.isReadable()) {
                buf.readerIndex(initialReaderIndex);
                return -1;
            }

            currentByte = buf.readByte();
            value |= (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) break;
            position += 7;

            if (position >= 32) {
                // Malformed VarInt - DO NOT RESET INDEX
                return -1;
            }
        }

        return value;
    }

    public static long readVarLong(ByteBuf buf) {
        if (buf instanceof EmptyByteBuf) {
            return -1;
        }

        long value = 0;
        int position = 0;
        byte currentByte;
        int initialReaderIndex = buf.readerIndex();

        while (true) {
            if (!buf.isReadable()) {
                buf.readerIndex(initialReaderIndex);
                return -1;
            }

            currentByte = buf.readByte();
            value |= (long) (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) break;
            position += 7;

            if (position >= 64) {
                // Malformed VarLong - DO NOT RESET INDEX
                return -1;
            }
        }

        return value;
    }

    public static String readString(ByteBuf buf) {
        int length = readVarInt(buf);
        if (length < 0) {
            throw new IllegalArgumentException("Cannot read string length");
        }
        if (buf.readableBytes() < length) {
            throw new IllegalArgumentException("Buffer does not have enough bytes to read the string.");
        }
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void writeVarInt(ByteBuf buf, int value) {
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                buf.writeByte(value);
                return;
            }

            buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
            value >>>= 7;
        }
    }

    public static void writeVarLong(ByteBuf buf, long value) {
        while (true) {
            if ((value & ~((long) SEGMENT_BITS)) == 0) {
                buf.writeByte((int) value);
                return;
            }

            buf.writeByte((int) ((value & SEGMENT_BITS) | CONTINUE_BIT));
            value >>>= 7;
        }
    }

    public static void writeString(ByteBuf buf, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }
}