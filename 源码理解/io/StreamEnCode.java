import sun.nio.cs.HistoricallyNamedCharset;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.*;

public class StreamEncoder extends Writer {
    // 默认的字节缓冲区大小
    private static final int DEFAULT_BYTE_BUFFER_SIZE = 8192;
    // 流是否打开
    private volatile boolean isOpen;
    // 字符
    private Charset cs;
    // 字符集编码器
    private CharsetEncoder encoder;
    // 字节缓冲区
    private ByteBuffer bb;
    // 字节输出流
    private final OutputStream out;
    // 可写字节通道
    private WritableByteChannel ch;
    // 有左边超出字节
    private boolean haveLeftoverChar;
    // 左边超出字符
    private char leftoverChar;
    // 字符缓冲区
    private CharBuffer lcb;

    // 确保流处于打开
    private void ensureOpen() throws IOException {
        // 流关闭则报错
        if (!this.isOpen) {
            throw new IOException("Stream closed");
        }
    }
    // 用于输出流编写器(输出流var0,,字符类型var2)
    public static sun.nio.cs.StreamEncoder forOutputStreamWriter(OutputStream var0, Object var1, String var2) throws UnsupportedEncodingException {
        String var3 = var2;
        // 如果没有传字符类型则使用默认字符
        if (var2 == null) {
            var3 = Charset.defaultCharset().name();
        }

        try {
            // 判断是否支持var3字符类型
            if (Charset.isSupported(var3)) {
                // 支持则创建StreamEncode对象(输出流var0,，字符编码var3)
                return new sun.nio.cs.StreamEncoder(var0, var1, Charset.forName(var3));
            }
        } catch (IllegalCharsetNameException var5) {
            ;
        }

        throw new UnsupportedEncodingException(var3);
    }
    // 用于输出流编写器(输出流var0,,字符类型var2)
    public static sun.nio.cs.StreamEncoder forOutputStreamWriter(OutputStream var0, Object var1, Charset var2) {
        return new sun.nio.cs.StreamEncoder(var0, var1, var2);
    }
    // 用于输出流编写器(输出流var0,,字符集编码器var2)
    public static sun.nio.cs.StreamEncoder forOutputStreamWriter(OutputStream var0, Object var1, CharsetEncoder var2) {
        return new sun.nio.cs.StreamEncoder(var0, var1, var2);
    }
    // 用于输出流编写器(写字节通道var0,字符集编码器var1,)
    public static sun.nio.cs.StreamEncoder forEncoder(WritableByteChannel var0, CharsetEncoder var1, int var2) {
        return new sun.nio.cs.StreamEncoder(var0, var1, var2);
    }
    // 获取编码
    public String getEncoding() {
        // 如果流没有关闭则返回编码反转返回null
        return this.isOpen() ? this.encodingName() : null;
    }
   // 刷新缓冲区
    public void flushBuffer() throws IOException {
        // 获取锁
        Object var1 = this.lock;
        // 同步代码块
        synchronized(this.lock) {
            // 判断流是否开启
            if (this.isOpen()) {
                // 实现刷新缓冲区
                this.implFlushBuffer();
            } else {
                throw new IOException("Stream closed");
            }
        }
    }
    // 写入一个字节
    public void write(int var1) throws IOException {
        // 将字节转换为char并放入char缓冲区
        char[] var2 = new char[]{(char)var1};
        // 写入char缓冲区中的0到1
        this.write((char[])var2, 0, 1);
    }
   // 写入缓冲区的一部分(char缓冲区var1,起始位置 var2，写入个数var3)
    public void write(char[] var1, int var2, int var3) throws IOException {
        // 获取锁对象
        Object var4 = this.lock;
        // 进入同步代码块
        synchronized(this.lock) {
            // 判断流是否处于打开状态，处于打开状态执行继续执行，反之抛出异常
            this.ensureOpen();
            // 判断起始位置是否大于等并且
            // 起始位置小于等于缓冲区长度并且
            // 写入个数大于等并且
            // 起始位置加写入个数小于等缓冲区长度并且
            // 起始位置加写入个数大于等
            if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
                // 写入个数不为0
                if (var3 != 0) {
                    // 写入字节数组的规定长度
                    this.implWrite(var1, var2, var3);
                }
            } else {
                throw new IndexOutOfBoundsException();
            }
        }
    }
   // 写入字符串var1，起始位置var2,写入长度var3
    public void write(String var1, int var2, int var3) throws IOException {
        // 写入长度小于0
        if (var3 < 0) {
            // 抛出下标越界异常
            throw new IndexOutOfBoundsException();
        } else {
            // 以写入长度创建一个字符数组var4
            char[] var4 = new char[var3];
            // 将字符串中字符放入var4
            var1.getChars(var2, var2 + var3, var4, 0);
            // 写入字节数组的规定长度
            this.write((char[])var4, 0, var3);
        }
    }
   // 刷新
    public void flush() throws IOException {
        // 获取锁对象
        Object var1 = this.lock;
        // 进入同步代码块
        synchronized(this.lock) {
            // 判断当前流是否处于开启
            this.ensureOpen();
            // 开启则刷新缓冲区
            this.implFlush();
        }
    }
    // 关闭
    public void close() throws IOException {
        // 获取锁对象
        Object var1 = this.lock;
        // 进入同步代码块
        synchronized(this.lock) {
            // 判断当前流是否处于开启
            if (this.isOpen) {
                // 实现关闭
                this.implClose();
                // 将当前流的状态改为关闭
                this.isOpen = false;
            }
        }
    }
    // 当前流状态是否为打开
    private boolean isOpen() {
        return this.isOpen;
    }
    // 私有带参构造(输入流var1,,字符编码var3)
    private StreamEncoder(OutputStream var1, Object var2, Charset var3) {
        //
        this(var1, var2, var3.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE));
    }
   //  私有带参构造(输入流var1,,字符集编码器var3)
    private StreamEncoder(OutputStream var1, Object var2, CharsetEncoder var3) {
        super(var2);
        // 默认当前流属于打开状态
        this.isOpen = true;
        // 默认没有左边超出字符
        this.haveLeftoverChar = false;
        // 默认字符缓冲区为null
        this.lcb = null;
        // 输出流为用户传入
        this.out = var1;
        // 默认可写字节通道为null
        this.ch = null;
        // 获取用户指定字符编码
        this.cs = var3.charset();
        // 获取用户指定字符集编码器
        this.encoder = var3;
        // 判断字节通道是否为空
        if (this.ch == null) {
            // 默认字节缓冲区大小为8192
            this.bb = ByteBuffer.allocate(8192);
        }

    }
    //私有带参构造器(写字节通道，字符集编码器，用户指定缓冲区大小)
    private StreamEncoder(WritableByteChannel var1, CharsetEncoder var2, int var3) {
        // 默认当前流属于打开状态
        this.isOpen = true;
        // 默认没有左边超出字符
        this.haveLeftoverChar = false;
        // 默认字符缓冲区为null
        this.lcb = null;
        // 默认输出流为null
        this.out = null;
        // 默认写字节通道用户指定
        this.ch = var1;
        // 获取编码
        this.cs = var2.charset();
        // 默认字符集编码器为用户指定
        this.encoder = var2;
        // 初始化缓冲区大小
        this.bb = ByteBuffer.allocate(var3 < 0 ? 8192 : var3);
    }
    // 写多个字节
    private void writeBytes() throws IOException {
        // 翻转此缓冲区。限制设置为当前位置，然后位置设置为零。如果标记已定义，则将丢弃。将从写状态转到读状态
        this.bb.flip();
        // 获取缓冲区可读有效字节长度
        int var1 = this.bb.limit();
        // 获取缓冲区当前读取位置
        int var2 = this.bb.position();
        // 断言 判断当前读取位置是否小于等下有效长度
        assert var2 <= var1
         // 如果当前读取位置小于等于有效长度则返回有效长度华为当前读取位置直接的长度否则返回0
        int var3 = var2 <= var1 ? var1 - var2 : 0;
        // var3大于0
        if (var3 > 0) {
            // 可写字节通道不为null
            if (this.ch != null) {
                assert this.ch.write(this.bb) == var3 : var3;
            } else {
                this.out.write(this.bb.array(), this.bb.arrayOffset() + var2, var3);
            }
        }
        // 清空缓冲区,但是缓冲区中的数据依然存在，但是处于“被遗忘”状态
        this.bb.clear();
    }
    // 刷新最左超出字节(字符缓冲区var1,)
    private void flushLeftoverChar(CharBuffer var1, boolean var2) throws IOException {
        // 判断是否有超出或者
        if (this.haveLeftoverChar || var2) {
            // 判断字符缓冲区为null
            if (this.lcb == null) {
                // 获取大小为2的字符缓冲区
                this.lcb = CharBuffer.allocate(2);
            } else {
                // 清空字符缓冲区
                this.lcb.clear();
            }
            //判断是否有左超出字符
            if (this.haveLeftoverChar) {
                // 将字符加入字符缓冲区
                this.lcb.put(this.leftoverChar);
            }
            // 字符串缓冲区不为null并且当前读取位置小于可读有效位置时
            if (var1 != null && var1.hasRemaining()) {
                //从var获取字符再放入成员变量字符缓冲区
                this.lcb.put(var1.get());
            }
            // 切换字符缓冲区读写模式(从写模式切换到)
            this.lcb.flip();
            //
            while(this.lcb.hasRemaining() || var2) {
                CoderResult var3 = this.encoder.encode(this.lcb, this.bb, var2);
                if (var3.isUnderflow()) {
                    if (this.lcb.hasRemaining()) {
                        this.leftoverChar = this.lcb.get();
                        if (var1 != null && var1.hasRemaining()) {
                            this.flushLeftoverChar(var1, var2);
                        }

                        return;
                    }
                    break;
                }

                if (var3.isOverflow()) {
                    assert this.bb.position() > 0;

                    this.writeBytes();
                } else {
                    var3.throwException();
                }
            }

            this.haveLeftoverChar = false;
        }
    }
    // 实现写方法(字符数组var1,起始位置var2，长度var3)
    void implWrite(char[] var1, int var2, int var3) throws IOException {
        // 将字符数组中指定数据放入字符缓冲区var4
        CharBuffer var4 = CharBuffer.wrap(var1, var2, var3);
        // 判断是否有左超出字符
        if (this.haveLeftoverChar) {
            // 刷新左超出字符（）
            this.flushLeftoverChar(var4, false);
        }

        while(var4.hasRemaining()) {
            CoderResult var5 = this.encoder.encode(var4, this.bb, false);
            if (var5.isUnderflow()) {
                assert var4.remaining() <= 1 : var4.remaining();

                if (var4.remaining() == 1) {
                    this.haveLeftoverChar = true;
                    this.leftoverChar = var4.get();
                }
                break;
            }

            if (var5.isOverflow()) {
                assert this.bb.position() > 0;

                this.writeBytes();
            } else {
                var5.throwException();
            }
        }

    }
    // 实现刷新缓冲区
    void implFlushBuffer() throws IOException {
        // 字节缓冲区当前读取位置大于0
        if (this.bb.position() > 0) {
            // 调用写字节方法
            this.writeBytes();
        }

    }
   // 实现刷新
    void implFlush() throws IOException {
        // 调用实现刷新缓冲区方法
        this.implFlushBuffer();
        // 判断字节输出流不为空时
        if (this.out != null) {
            // 刷新此字节输出流
            this.out.flush();
        }

    }
   // 实现关闭流
    void implClose() throws IOException {
        // 调用刷新左超出字符（字符缓冲区为null，true）
        this.flushLeftoverChar((CharBuffer)null, true);

        try {
            // while循环
            while(true) {
                CoderResult var1 = this.encoder.flush(this.bb);
                if (var1.isUnderflow()) {
                    if (this.bb.position() > 0) {
                        this.writeBytes();
                    }

                    if (this.ch != null) {
                        this.ch.close();
                    } else {
                        this.out.close();
                    }

                    return;
                }

                if (var1.isOverflow()) {
                    assert this.bb.position() > 0;

                    this.writeBytes();
                } else {
                    var1.throwException();
                }
            }
        } catch (IOException var2) {
            this.encoder.reset();
            throw var2;
        }
    }
   // 获取字符编码名称
    String encodingName() {
        return this.cs instanceof HistoricallyNamedCharset ? ((HistoricallyNamedCharset)this.cs).historicalName() : this.cs.name();
    }
}