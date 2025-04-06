package codes.shiftmc.minecraft.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

import static codes.shiftmc.minecraft.util.ProtocolHelper.readVarInt;

public class Varint21FrameDecoder extends ByteToMessageDecoder {

    private static final int MAX_PACKET_LENGTH = 2 * 1024 * 1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) {
            return;
        }

        in.markReaderIndex();
        int packetLength = readVarInt(in);

        if (packetLength == -1) {
            in.resetReaderIndex();
            return;
        }

        if (packetLength < 0) {
            throw new CorruptedFrameException("Invalid packet length: " + packetLength);
        }

        if (packetLength == 0) {
            return;
        }

        if (packetLength > MAX_PACKET_LENGTH) {
            throw new CorruptedFrameException("Packet length of " + packetLength + " is larger than the maximum of " + MAX_PACKET_LENGTH);
        }

        if (in.readableBytes() < packetLength) {
            in.resetReaderIndex();
            return;
        }

        var frame = in.readSlice(packetLength).retain();
        out.add(frame);
    }
}