package netty.elite;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

public final class BufferUtils {
    /**
     * New {@link ByteBuf} instance
     *
     * @param in     input buffer
     * @param length the length data to read
     * @return a new buffer instance
     */
    public static ByteBuf newByteBuf(ByteBuf in, int length) {
        ByteBuf buffer = in.alloc().buffer(in.alloc().calculateNewCapacity(length, Integer.MAX_VALUE));
        buffer.writeBytes(in, length);

        return buffer;
    }

    /**
     * New {@link ByteBuffer} instance
     *
     * @param in     input buffer
     * @param length the length data to read
     * @return a new buffer instance
     */
    public static ByteBuffer newByteBuffer(ByteBuf in, int length) {
        byte[] bytes = new byte[Math.min(length, Integer.MAX_VALUE)];
        in.readBytes(bytes);

        return ByteBuffer.wrap(bytes);
    }
}