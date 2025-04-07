package codes.shiftmc.minecraft.mappings;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static codes.shiftmc.minecraft.util.ProtocolHelper.writeString;

public record Property(
        String name,
        String value,
        @Nullable String signature
) {
    public Property {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }
    }

    public Property(String name, String value) {
        this(name, value, null);
    }

    public void serialize(ByteBuf buf) {
        writeString(buf, name);
        writeString(buf, value);

        boolean hasSignature = signature != null;
        buf.writeBoolean(hasSignature);
        if (hasSignature) writeString(buf, signature);
    }
}

