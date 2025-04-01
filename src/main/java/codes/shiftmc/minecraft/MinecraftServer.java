package codes.shiftmc.minecraft;

public class MinecraftServer {

    public static void main(String[] args) throws Exception {
        var server = new NettyServer(8080);
        server.run();
    }
}
