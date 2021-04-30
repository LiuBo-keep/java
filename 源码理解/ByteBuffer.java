public abstract class ByteBuffer
        extends Buffer
        implements Comparable<java.nio.ByteBuffer> {
    final byte[] hb;                  // Non-null only for heap buffers
    final int offset;
    boolean isReadOnly;                 // Valid only for heap buffers

    // 创建实例(标记位，当前操作位，当前可操作位，容量，缓冲区，偏移量)
    ByteBuffer(int mark, int pos, int lim, int cap,
               byte[] hb, int offset) {
        // 父类构造
        super(mark, pos, lim, cap);
        // 初始化
        this.hb = hb;
        this.offset = offset;
    }

    // 创建实例(标记位，当前操作位，当前可操作位，容量，缓冲区为null，偏移量为0)
    ByteBuffer(int mark, int pos, int lim, int cap) {
        this(mark, pos, lim, cap, null, 0);
    }

    // 直接分配缓冲区大小并返回缓冲区（直接字节缓冲区）
    public static java.nio.ByteBuffer allocateDirect(int capacity) {
        return new DirectByteBuffer(capacity);
    }

    // 分配缓冲区容量并返回缓冲区(堆字节缓冲区)
    public static java.nio.ByteBuffer allocate(int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException();
        return new HeapByteBuffer(capacity, capacity);
    }

    public static java.nio.ByteBuffer wrap(byte[] array,
                                           int offset, int length) {
        try {
            return new HeapByteBuffer(array, offset, length);
        } catch (IllegalArgumentException x) {
            throw new IndexOutOfBoundsException();
        }
    }

    public static java.nio.ByteBuffer wrap(byte[] array) {
        return wrap(array, 0, array.length);
    }

    public abstract java.nio.ByteBuffer slice();

    public abstract java.nio.ByteBuffer duplicate();

    public abstract java.nio.ByteBuffer asReadOnlyBuffer();

    public abstract byte get();

    public abstract java.nio.ByteBuffer put(byte b);

    public abstract byte get(int index);

    public abstract java.nio.ByteBuffer put(int index, byte b);

    public java.nio.ByteBuffer get(byte[] dst, int offset, int length) {
        checkBounds(offset, length, dst.length);
        if (length > remaining())
            throw new BufferUnderflowException();
        int end = offset + length;
        for (int i = offset; i < end; i++)
            dst[i] = get();
        return this;
    }

    public java.nio.ByteBuffer get(byte[] dst) {
        return get(dst, 0, dst.length);
    }

    public java.nio.ByteBuffer put(java.nio.ByteBuffer src) {
        if (src == this)
            throw new IllegalArgumentException();
        if (isReadOnly())
            throw new ReadOnlyBufferException();
        int n = src.remaining();
        if (n > remaining())
            throw new BufferOverflowException();
        for (int i = 0; i < n; i++)
            put(src.get());
        return this;
    }

    public java.nio.ByteBuffer put(byte[] src, int offset, int length) {
        checkBounds(offset, length, src.length);
        if (length > remaining())
            throw new BufferOverflowException();
        int end = offset + length;
        for (int i = offset; i < end; i++)
            this.put(src[i]);
        return this;
    }

    public final java.nio.ByteBuffer put(byte[] src) {
        return put(src, 0, src.length);
    }

    public final boolean hasArray() {
        return (hb != null) && !isReadOnly;
    }

    public final byte[] array() {
        if (hb == null)
            throw new UnsupportedOperationException();
        if (isReadOnly)
            throw new ReadOnlyBufferException();
        return hb;
    }

    public final int arrayOffset() {
        if (hb == null)
            throw new UnsupportedOperationException();
        if (isReadOnly)
            throw new ReadOnlyBufferException();
        return offset;
    }

    public abstract java.nio.ByteBuffer compact();

    public abstract boolean isDirect();

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append("[pos=");
        sb.append(position());
        sb.append(" lim=");
        sb.append(limit());
        sb.append(" cap=");
        sb.append(capacity());
        sb.append("]");
        return sb.toString();
    }

    public int hashCode() {
        int h = 1;
        int p = position();
        for (int i = limit() - 1; i >= p; i--)


            h = 31 * h + (int) get(i);

        return h;
    }

    public boolean equals(Object ob) {
        if (this == ob)
            return true;
        if (!(ob instanceof java.nio.ByteBuffer))
            return false;
        java.nio.ByteBuffer that = (java.nio.ByteBuffer) ob;
        if (this.remaining() != that.remaining())
            return false;
        int p = this.position();
        for (int i = this.limit() - 1, j = that.limit() - 1; i >= p; i--, j--)
            if (!equals(this.get(i), that.get(j)))
                return false;
        return true;
    }

    private static boolean equals(byte x, byte y) {


        return x == y;

    }

    public int compareTo(java.nio.ByteBuffer that) {
        int n = this.position() + Math.min(this.remaining(), that.remaining());
        for (int i = this.position(), j = that.position(); i < n; i++, j++) {
            int cmp = compare(this.get(i), that.get(j));
            if (cmp != 0)
                return cmp;
        }
        return this.remaining() - that.remaining();
    }

    private static int compare(byte x, byte y) {

        return Byte.compare(x, y);

    }

    boolean bigEndian                                   // package-private
            = true;
    boolean nativeByteOrder                             // package-private
            = (Bits.byteOrder() == ByteOrder.BIG_ENDIAN);


    public final ByteOrder order() {
        return bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    public final java.nio.ByteBuffer order(ByteOrder bo) {
        bigEndian = (bo == ByteOrder.BIG_ENDIAN);
        nativeByteOrder =
                (bigEndian == (Bits.byteOrder() == ByteOrder.BIG_ENDIAN));
        return this;
    }

    // Unchecked accessors, for use by ByteBufferAs-X-Buffer classes
    //
    abstract byte _get(int i);                          // package-private

    abstract void _put(int i, byte b);                  // package-private

    public abstract char getChar();

    public abstract java.nio.ByteBuffer putChar(char value);

    public abstract char getChar(int index);

    public abstract java.nio.ByteBuffer putChar(int index, char value);

    public abstract CharBuffer asCharBuffer();

    public abstract short getShort();

    public abstract java.nio.ByteBuffer putShort(short value);

    public abstract short getShort(int index);

    public abstract java.nio.ByteBuffer putShort(int index, short value);

    public abstract ShortBuffer asShortBuffer();

    public abstract int getInt();

    public abstract java.nio.ByteBuffer putInt(int value);

    public abstract int getInt(int index);

    public abstract java.nio.ByteBuffer putInt(int index, int value);

    public abstract IntBuffer asIntBuffer();

    public abstract long getLong();

    public abstract java.nio.ByteBuffer putLong(long value);

    public abstract long getLong(int index);

    public abstract java.nio.ByteBuffer putLong(int index, long value);

    public abstract LongBuffer asLongBuffer();

    public abstract float getFloat();

    public abstract java.nio.ByteBuffer putFloat(float value);

    public abstract float getFloat(int index);

    public abstract java.nio.ByteBuffer putFloat(int index, float value);

    public abstract FloatBuffer asFloatBuffer();

    public abstract double getDouble();

    public abstract java.nio.ByteBuffer putDouble(double value);

    public abstract double getDouble(int index);

    public abstract java.nio.ByteBuffer putDouble(int index, double value);

    public abstract DoubleBuffer asDoubleBuffer();

}
