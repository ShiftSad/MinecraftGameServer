package codes.shiftmc.minecraft.netty;

import codes.shiftmc.minecraft.server.ProtocolState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static codes.shiftmc.minecraft.netty.NettyKeys.STATE_KEY;

public class MinecraftPacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 1) return;
        in.markReaderIndex();

        var packetId = readVarInt(in);
        if (packetId == -1) {
            return;
        }

        var state = ctx.channel().attr(STATE_KEY).get();
        if (state == null) {
            state = ProtocolState.HANDSHAKING;
        }

        switch (state) {
            case HANDSHAKING:
                if (packetId == 0x00) {
                    var protocolVersion = readVarInt(in);
                    var serverAddress = readString(in);
                    var serverPort = in.readUnsignedShort();
                    var nextState = readVarInt(in);

                    System.out.println("Protocol Version: " + protocolVersion);
                    System.out.println("Server Address: " + serverAddress);
                    System.out.println("Server Port: " + serverPort);
                    System.out.println("Next State: " + nextState);

                    ctx.channel().attr(STATE_KEY).set(ProtocolState.values()[nextState]);
                }
                break;
            case STATUS:
                break;
            case LOGIN:
                break;
            case PLAY:
                break;
        }
    }

    private int readVarInt(ByteBuf buf) {
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

    private String readString(ByteBuf buf) {
        int length = readVarInt(buf);
        if (buf.readableBytes() < length) {
            throw new IllegalArgumentException("Buffer does not have enough bytes to read the string.");
        }
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
