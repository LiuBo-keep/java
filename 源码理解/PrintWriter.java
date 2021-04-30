public class PrintWriter extends Writer {

    // 此字符的基本字符输出流
    protected Writer out;
    // 是否自动刷新
    private final boolean autoFlush;
    // 流是否有错误
    private boolean trouble = false;
    // 格式化程序
    private Formatter formatter;
    // 字节输出
    private PrintStream psOut = null;
    // 行分隔符字符串
    private final String lineSeparator;

    // 返回给定字符集名称的字符集对象。
    private static Charset toCharset(String csn)
            throws UnsupportedEncodingException {
        // 判断是否为null
        Objects.requireNonNull(csn, "charsetName");
        try {
            return Charset.forName(csn);
        } catch (IllegalCharsetNameException | UnsupportedCharsetException unused) {
            // UnsupportedEncodingException should be thrown
            throw new UnsupportedEncodingException(csn);
        }
    }

    // 创建一个新的PrintWriter，而不会自动刷新行。
    public PrintWriter(Writer out) {
        this(out, false);
    }

    //创建一个新的PrintWriter，用户指定是否自动刷新行。
    public PrintWriter(Writer out,
                       boolean autoFlush) {
        super(out);
        this.out = out;
        this.autoFlush = autoFlush;
        lineSeparator = java.security.AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction("line.separator"));
    }

    // 创建新的PrintWriter，而不会自动刷新行。
    public PrintWriter(OutputStream out) {
        this(out, false);
    }

    //创建一个新的PrintWriter，用户指定是否自动刷新行。
    public PrintWriter(OutputStream out, boolean autoFlush) {
        this(new BufferedWriter(new OutputStreamWriter(out)), autoFlush);

        // 保存打印流以进行错误传播
        if (out instanceof java.io.PrintStream) {
            psOut = (PrintStream) out;
        }
    }

    // 创建新的PrintWriter对象参数为文件名称
    public PrintWriter(String fileName) throws FileNotFoundException {
        // 创建一个写缓冲区对象(字节输出流写)，不自动刷新
        this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName))),
                false);
    }

    // 私有构造器(字符编码，文件名称)
    private PrintWriter(Charset charset, File file)
            throws FileNotFoundException {
        // 创建写缓冲对象()
        this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset)),
                false);
    }

    // 创建对象(文件名称，字符编码)
    public PrintWriter(String fileName, String csn)
            throws FileNotFoundException, UnsupportedEncodingException {
        this(toCharset(csn), new File(fileName));
    }

    // 创建对象(文件对象)
    public PrintWriter(File file) throws FileNotFoundException {
        this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))),
                false);
    }

    // 创建对象(文件对象，字符编码)
    public PrintWriter(File file, String csn)
            throws FileNotFoundException, UnsupportedEncodingException {
        this(toCharset(csn), file);
    }

    // 确保没有关闭流
    private void ensureOpen() throws IOException {
        // 流为null
        if (out == null)
            // 抛出IO异常
            throw new IOException("Stream closed");
    }

    // 刷新
    public void flush() {
        try {
            synchronized (lock) {
                // 判断流是否关闭
                ensureOpen();
                // 刷新
                out.flush();
            }
        } catch (IOException x) {
            trouble = true;
        }
    }

    // 关闭流
    public void close() {
        try {
            synchronized (lock) {
                // 流等于null直接返回
                if (out == null)
                    return;
                // 调用close方法
                out.close();
                // 流对象置空
                out = null;
            }
        } catch (IOException x) {
            trouble = true;
        }
    }

    // 如果未关闭，则刷新流并检查其错误状态。
    public boolean checkError() {
        if (out != null) {
            flush();
        }
        if (out instanceof java.io.PrintWriter) {
            PrintWriter pw = (PrintWriter) out;
            return pw.checkError();
        } else if (psOut != null) {
            return psOut.checkError();
        }
        return trouble;
    }

    // 表示发生了错误。
    protected void setError() {
        trouble = true;
    }

    // 清除此流的错误状态。
    protected void clearError() {
        trouble = false;
    }

    //写一个字符
    public void write(int c) {
        try {
            synchronized (lock) {
                ensureOpen();
                out.write(c);
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble = true;
        }
    }

    // 写一个字符数组的一部分
    public void write(char buf[], int off, int len) {
        try {
            synchronized (lock) {
                ensureOpen();
                out.write(buf, off, len);
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble = true;
        }
    }

    // 写一个字符
    public void write(char buf[]) {
        write(buf, 0, buf.length);
    }

    // 写一个字符串的一部分
    public void write(String s, int off, int len) {
        try {
            synchronized (lock) {
                ensureOpen();
                out.write(s, off, len);
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble = true;
        }
    }

    // 写一个字符串
    public void write(String s) {
        write(s, 0, s.length());
    }

    // 换行
    private void newLine() {
        try {
            synchronized (lock) {
                ensureOpen();
                out.write(lineSeparator);
                if (autoFlush)
                    out.flush();
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble = true;
        }
    }

    // 打印布尔值
    public void print(boolean b) {
        write(b ? "true" : "false");
    }

    // 打印一个字符
    public void print(char c) {
        write(c);
    }

    // 打印一个数字
    public void print(int i) {
        write(String.valueOf(i));
    }

    // 打印一个long类型的
    public void print(long l) {
        write(String.valueOf(l));
    }

    // 打印一个单精度的
    public void print(float f) {
        write(String.valueOf(f));
    }

    // 打印一个双精度的
    public void print(double d) {
        write(String.valueOf(d));
    }

    // 打印一个字符数组
    public void print(char s[]) {
        write(s);
    }

    // 打印一个字符串
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        write(s);
    }

    // 打印一个对象
    public void print(Object obj) {
        write(String.valueOf(obj));
    }

    /* 下面方法为换行打印*/
    public void println() {
        newLine();
    }

    public void println(boolean x) {
        synchronized (lock) {
            print(x);
            println();
        }
    }

    public void println(char x) {
        synchronized (lock) {
            print(x);
            println();
        }
    }

    public void println(int x) {
        synchronized (lock) {
            print(x);
            println();
        }
    }

    public void println(long x) {
        synchronized (lock) {
            print(x);
            println();
        }
    }

    public void println(float x) {
        synchronized (lock) {
            print(x);
            println();
        }
    }

    public void println(double x) {
        synchronized (lock) {
            print(x);
            println();
        }
    }

    public void println(char x[]) {
        synchronized (lock) {
            print(x);
            println();
        }
    }

    public void println(String x) {
        synchronized (lock) {
            print(x);
            println();
        }
    }

    public void println(Object x) {
        String s = String.valueOf(x);
        synchronized (lock) {
            print(s);
            println();
        }
    }

    /**
     * 和C一样指定格式进行打印
     */
    public PrintWriter printf(String format, Object... args) {
        return format(format, args);
    }

    public PrintWriter printf(Locale l, String format, Object... args) {
        return format(l, format, args);
    }

    public PrintWriter format(String format, Object... args) {
        try {
            synchronized (lock) {
                ensureOpen();
                if ((formatter == null)
                        || (formatter.locale() != Locale.getDefault()))
                    formatter = new Formatter(this);
                formatter.format(Locale.getDefault(), format, args);
                if (autoFlush)
                    out.flush();
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble = true;
        }
        return this;
    }

    public PrintWriter format(Locale l, String format, Object... args) {
        try {
            synchronized (lock) {
                ensureOpen();
                if ((formatter == null) || (formatter.locale() != l))
                    formatter = new Formatter(this, l);
                formatter.format(l, format, args);
                if (autoFlush)
                    out.flush();
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble = true;
        }
        return this;
    }

    public PrintWriter append(CharSequence csq) {
        if (csq == null)
            write("null");
        else
            write(csq.toString());
        return this;
    }

    public PrintWriter append(CharSequence csq, int start, int end) {
        CharSequence cs = (csq == null ? "null" : csq);
        write(cs.subSequence(start, end).toString());
        return this;
    }

    public PrintWriter append(char c) {
        write(c);
        return this;
    }
}