package codes.shiftmc.minecraft.netty;

import codes.shiftmc.minecraft.server.ProtocolState;
import io.netty.util.AttributeKey;

public class NettyKeys {

    public static final AttributeKey<ProtocolState> STATE_KEY = AttributeKey.newInstance("protocolState");
}
