public abstract class Buffer {

    /**
     * The characteristics of Spliterators that traverse and split elements
     * maintained in Buffers.
     */
    static final int SPLITERATOR_CHARACTERISTICS =
            Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED;

    // 标志位: mark(标记位) <= position(当前操作位) <= limit(可操作位) <= capacity(缓冲区容量)
    private int mark = -1;
    private int position = 0;
    private int limit;
    private int capacity;

    // Used only by direct buffers
    // NOTE: hoisted here for speed in JNI GetDirectBufferAddress
    long address;

    // 创建缓冲区(标志位，当前位，可操作位，容量)
    Buffer(int mark, int pos, int lim, int cap) {
        // 容量小于0抛出异常
        if (cap < 0)
            throw new IllegalArgumentException("Negative capacity: " + cap);
        this.capacity = cap;
        limit(lim);
        position(pos);
        // 标记位大于等于0
        if (mark >= 0) {
            // 如果标记位大于当前位则抛出异常
            if (mark > pos)
                throw new IllegalArgumentException("mark > position: ("
                        + mark + " > " + pos + ")");
            this.mark = mark;
        }
    }

    // 返回缓冲区容量
    public final int capacity() {
        return capacity;
    }

    // 返回当前操作位
    public final int position() {
        return position;
    }

    // 指定操作位
    public final java.nio.Buffer position(int newPosition) {
        // 当前操作位大于可操作的位或者小于0抛出异常
        if ((newPosition > limit) || (newPosition < 0))
            throw new IllegalArgumentException();
        // 初始化
        position = newPosition;
        // 标记位大于当前位，标记位等于-1
        if (mark > position) mark = -1;
        return this;
    }

    // 返回当前可操作位
    public final int limit() {
        return limit;
    }

    // 指定可操作位
    public final java.nio.Buffer limit(int newLimit) {
        // 可操作数大于容量或者小于0抛出异常
        if ((newLimit > capacity) || (newLimit < 0))
            throw new IllegalArgumentException();
        // 初始化
        limit = newLimit;
        // 如果当前操作位大于可操作位时，操作位等于可操作位
        if (position > limit) position = limit;
        // 标记位大于可操作位 标记位等于-1
        if (mark > limit) mark = -1;
        return this;
    }

    // 对当前位置进行标记
    public final java.nio.Buffer mark() {
        mark = position;
        return this;
    }

    // 返回mark标记的位置
    public final java.nio.Buffer reset() {
        int m = mark;
        // 如果当前标记位小于0，则抛出异常
        if (m < 0)
            throw new InvalidMarkException();
        // 将标记位赋值给当前操作位
        position = m;
        return this;
    }

    // 清除此缓冲区。 位置设置为零，限制设置为容量，标记被丢弃。
    // 这个方法实际上并不会清除缓冲区中的数据，但是它被命名为它的确是因为它最常用于情况也是如此。
    public final java.nio.Buffer clear() {
        position = 0;
        limit = capacity;
        mark = -1;
        return this;
    }

    // 转换模式(默认是写模式转换为读模式)
    public final java.nio.Buffer flip() {
        // 当前操作位赋值给可操作位
        limit = position;
        // 当前操作位赋值为0
        position = 0;
        // 标记赋值为-1
        mark = -1;
        return this;
    }

    // 它单纯的将当前位置置0，同时取消mark标记，仅此而已(重头开始读或写)
    public final java.nio.Buffer rewind() {
        position = 0;
        mark = -1;
        return this;
    }

    // 返回可操作为和当前操作位之间的值(计算可以操作几个)
    public final int remaining() {
        return limit - position;
    }

    // 判断当前操作位是否小于可操作的位(判断在读模式在是否还有可读的数据)
    public final boolean hasRemaining() {
        return position < limit;
    }

    // 告知此缓冲区是否为只读。
    public abstract boolean isReadOnly();

    // 告诉此缓冲区是否由可访问数组支持。
    public abstract boolean hasArray();

    // 返回支持此功能的数组
    public abstract Object array();

    // 在缓冲区的后备数组中返回缓冲区的第一个元素的偏移量<i>（可选操作）</ i>。
    public abstract int arrayOffset();

    // 告诉此缓冲区是否为直接缓冲区
    public abstract boolean isDirect();


    // -- Package-private methods for bounds checking, etc. --

    /**
     * Checks the current position against the limit, throwing a {@link
     * BufferUnderflowException} if it is not smaller than the limit, and then
     * increments the position.
     *
     * @return The current position value, before it is incremented
     */
    final int nextGetIndex() {                          // package-private
        if (position >= limit)
            throw new BufferUnderflowException();
        return position++;
    }

    final int nextGetIndex(int nb) {                    // package-private
        if (limit - position < nb)
            throw new BufferUnderflowException();
        int p = position;
        position += nb;
        return p;
    }

    /**
     * Checks the current position against the limit, throwing a {@link
     * BufferOverflowException} if it is not smaller than the limit, and then
     * increments the position.
     *
     * @return The current position value, before it is incremented
     */
    final int nextPutIndex() {                          // package-private
        if (position >= limit)
            throw new BufferOverflowException();
        return position++;
    }

    final int nextPutIndex(int nb) {                    // package-private
        if (limit - position < nb)
            throw new BufferOverflowException();
        int p = position;
        position += nb;
        return p;
    }

    /**
     * Checks the given index against the limit, throwing an {@link
     * IndexOutOfBoundsException} if it is not smaller than the limit
     * or is smaller than zero.
     */
    final int checkIndex(int i) {                       // package-private
        if ((i < 0) || (i >= limit))
            throw new IndexOutOfBoundsException();
        return i;
    }

    final int checkIndex(int i, int nb) {               // package-private
        if ((i < 0) || (nb > limit - i))
            throw new IndexOutOfBoundsException();
        return i;
    }

    final int markValue() {                             // package-private
        return mark;
    }

    final void truncate() {                             // package-private
        mark = -1;
        position = 0;
        limit = 0;
        capacity = 0;
    }

    final void discardMark() {                          // package-private
        mark = -1;
    }

    static void checkBounds(int off, int len, int size) { // package-private
        if ((off | len | (off + len) | (size - (off + len))) < 0)
            throw new IndexOutOfBoundsException();
    }

}
