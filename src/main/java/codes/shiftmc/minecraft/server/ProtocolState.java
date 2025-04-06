package codes.shiftmc.minecraft.server;

public enum ProtocolState {
    HANDSHAKING, STATUS, LOGIN, PLAY;

    public static ProtocolState getStatusById(int id) {
        return switch (id) {
            case 1 -> STATUS;
            case 2 -> LOGIN;
            case 3 -> PLAY;
            default -> throw new IllegalArgumentException("Invalid protocol state id: " + id);
        };
    }
}
