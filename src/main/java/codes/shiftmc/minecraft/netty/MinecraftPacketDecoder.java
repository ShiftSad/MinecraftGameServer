package codes.shiftmc.minecraft.netty;

import codes.shiftmc.minecraft.mappings.Property;
import codes.shiftmc.minecraft.server.ProtocolState;
import codes.shiftmc.minecraft.server.packet.client.handshake.ClientHandshakePacket;
import codes.shiftmc.minecraft.server.packet.client.login.LoginStartPacket;
import codes.shiftmc.minecraft.server.packet.client.status.PingRequestPacket;
import codes.shiftmc.minecraft.server.packet.server.login.LoginSuccessPacket;
import codes.shiftmc.minecraft.server.packet.server.status.PongResponsePacket;
import codes.shiftmc.minecraft.server.packet.server.status.ResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static codes.shiftmc.minecraft.netty.NettyKeys.STATE_KEY;
import static codes.shiftmc.minecraft.util.ProtocolHelper.readVarInt;

public class MinecraftPacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        var packetId = readVarInt(in);
        if (packetId == -1) return;

        var state = ctx.channel().attr(STATE_KEY).get();
        if (state == null) {
            state = ProtocolState.HANDSHAKING;
        }

        System.out.println("Handling packet: " + packetId + " in state: " + state);

        switch (state) {
            case HANDSHAKING:
                if (packetId == 0x00) {
                    var packet = ClientHandshakePacket.deserialize(in);
                    System.out.println("Handshake: " + packet);

                    var nextState = ProtocolState.getStatusById(packet.intent().id());
                    ctx.channel().attr(STATE_KEY).set(nextState);
                }
                break;
            case STATUS:
                if (packetId == 0x00) {
                    ctx.writeAndFlush(new ResponsePacket());
                }

                if (packetId == 0x01) {
                    var packet = PingRequestPacket.deserialize(in);
                    System.out.println("Status: " + packet);

                    ctx.writeAndFlush(new PongResponsePacket(packet.time()));
                    ctx.close();
                }
                break;
            case LOGIN:
                if (packetId == 0x00) {
                    var packet = LoginStartPacket.deserialize(in);
                    System.out.println("Login: " + packet);

                    // TODO -> Handle compression
//                    ctx.writeAndFlush(new SetCompressionPacket(256));

                    final var uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + packet.name()).getBytes(StandardCharsets.UTF_8));
                    final var property = List.of(new Property(
                            "textures",
                            "ewogICJ0aW1lc3RhbXAiIDogMTc0Mzk4MjgxNTc1NywKICAicHJvZmlsZUlkIiA6ICIwYjFlNDkzN2E3OWQ0NWMyOGQxMjI1YzIxMGU0MDgwZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTaGlmdF9TYWQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjk3Nzg2NDY3NTJkODQwMGZiODM5YWRhZDA0Nzk1ODFjNjYwMWVkZmI2OGU0OWZkNDJhOTY2MzE5Zjg0OGFkYiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9LAogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVjMjk0MTAwNTdlMzJhYmVjMDJkODcwZWNiNTJlYzI1ZmI0NWVhODFlNzg1YTc4NTRhZTg0MjlkNzIzNmNhMjYiCiAgICB9CiAgfQp9",
                            "u/SYGs1RA5LnS30YFTfS8gc2t9TRyuGosQPqXCfw74brAvx7DKNmouhAXjU2LwB328mrcvIPqsSXxqalmeXyHZRcCZ5WrN5qwWRMZwFRgL8BYxF/tNsGzwyp13K6m/1MfqHBoUnRCWypP2HrUkiJST+P2OSKLj+Qz0QXeKuzJm1QV6W8Pn60ZvDZOqQZcTDOwdTCwirK+ax5QFxjEVR4GVRrLg0wBhuHepBWfWFgGDlm1rFQXSD/FRcJx5uUxhJnoo5saX8nqXVl5diLAaye4TFYoAlkRVqKZfS4JKuF2Wvgi8W6jeG5weykwz8trfRpcCeZ9zHhVScmN/B1rpCOAM6JPIWVLvjtgiqEB8KEM/9rrLlqeHqmfQtEANBAZEvKgFDEPh4MvzCw2a/geYVFPK2I79X9f4js84UuVn/7zRcKq5yuYsiizQYJfWMn5+CP1i2hc3FZbuO4zFJ+bJuZjnjzvTb/aVNyyuf5Xj0e0czOdtalgUPCzXkOwnLL+yuT4RBfauCX6WKm7rajGeu2KQaBSX+Tvq0ccwRVDFu8e3WENiN3XTy9PMlFAD0CjcrlbmWoPaxgdDOoELKmAMcRrJE6ws7K/HMo/GCAP5yEBttJhjRyyyP7rh7iD/WhOfkuST3Rz9jj667vnKJbWNyZ75q3Ys0Phuaquzi0h55Lwbg="
                    ));
                    ctx.writeAndFlush(new LoginSuccessPacket(
                            uuid,
                            packet.name(),
                            property
                    ));
                }
                break;
            case PLAY:
                break;
        }
    }

}
