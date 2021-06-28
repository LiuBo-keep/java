public class ThreadLocal<T> {

    private final int threadLocalHashCode = nextHashCode();

    private static AtomicInteger nextHashCode =
            new AtomicInteger();

    private static final int HASH_INCREMENT = 0x61c88647;

    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    // 初始化默认value
    protected T initialValue() {
        // 返回null
        return null;
    }

    public static <S> ThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        return new SuppliedThreadLocal<>(supplier);
    }

    // 无参构造器
    public ThreadLocal() {
    }

    // 获取当前线程绑定的值(threadLocals中的值)
    public T get() {
        // 获取当前线程
        Thread t = Thread.currentThread();
        // 获取当前线程的成员变量threadLocals （ThreadLocal.ThreadLocalMap threadLocals = null;）
        ThreadLocalMap map = getMap(t);
        // map不为null
        if (map != null) {
            // 在map中通过当前对象获取对应的value
            ThreadLocalMap.Entry e = map.getEntry(this);
            // value不为null
            if (e != null) {
                @SuppressWarnings("unchecked")
                //  将vaule强转为指定的泛型类型
                        T result = (T) e.value;
                // 并返回
                return result;
            }
        }
        // map为null
        return setInitialValue();
    }

    // 设置默认的value
    private T setInitialValue() {
        // 得到默认的value
        T value = initialValue();
        // 获取当前线程
        Thread t = Thread.currentThread();
        // 获取当前线程成员变量threadLocals （ThreadLocal.ThreadLocalMap threadLocals = null;）
        ThreadLocalMap map = getMap(t);
        // map不为null
        if (map != null)
            // 设置默认这
            map.set(this, value);
        else
            // map为null，创建map
            createMap(t, value);
        // 返回默认值
        return value;
    }

    // 设置当前线程绑定的值(threadLocals中的值)
    public void set(T value) {
        // 获取当前线程
        Thread t = Thread.currentThread();
        //  获取当前线程的成员变量threadLocals （ThreadLocal.ThreadLocalMap threadLocals = null;）
        ThreadLocalMap map = getMap(t);
        // map不为null
        if (map != null)
            // 设置当前线程为key，传入参数为value
            map.set(this, value);
        else
            // map为空创建map
            createMap(t, value);
    }

    // 移除当前线程绑定的值(threadLocals中的值)
    public void remove() {
        // 获取当前线程中的map
        ThreadLocalMap m = getMap(Thread.currentThread());
        // map不为null
        if (m != null)
            // 通过当前线程移除对应的value
            m.remove(this);
    }

    // 获取当前线程对应的map
    ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }

    // 创建当前线程的成员变量threadLocals
    void createMap(Thread t, T firstValue) {
        // 创建ThreadMap赋值给当前线程成员变量threadLocals
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }


    static ThreadLocalMap createInheritedMap(ThreadLocalMap parentMap) {
        return new ThreadLocalMap(parentMap);
    }

    T childValue(T parentValue) {
        throw new UnsupportedOperationException();
    }

    static final class SuppliedThreadLocal<T> extends ThreadLocal<T> {

        private final Supplier<? extends T> supplier;

        SuppliedThreadLocal(Supplier<? extends T> supplier) {
            this.supplier = Objects.requireNonNull(supplier);
        }

        @Override
        protected T initialValue() {
            return supplier.get();
        }
    }

    // ThreadLocalMap类
    static class ThreadLocalMap {

        static class Entry extends WeakReference<ThreadLocal<?>> {
            Object value;

            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }

        // 初始容量
        private static final int INITIAL_CAPACITY = 16;

        // 哈希数组(长度必须是2的幂)
        private Entry[] table;

        // 默认结点个数为0
        private int size = 0;

        // 临界点默认为0（要调整大小的下一个大小值。）
        private int threshold;

        // 设置临界点（设置调整大小阈值以在最坏情况下保持 2/3 的负载因子。）
        private void setThreshold(int len) {
            threshold = len * 2 / 3;
        }

        // 增量和模len。
        private static int nextIndex(int i, int len) {
            return ((i + 1 < len) ? i + 1 : 0);
        }

        // 减量和模len。
        private static int prevIndex(int i, int len) {
            return ((i - 1 >= 0) ? i - 1 : len - 1);
        }

        // 构造一个最初包含 (当前ThreadLocal实例, firstValue) 的新映射。ThreadLocalMaps
        // 是惰性构建的，因此我们只在至少有一个条目要放入时才创建一个。
        ThreadLocalMap(ThreadLocal<?> firstKey, Object firstValue) {
            // 创建哈希数组
            table = new Entry[INITIAL_CAPACITY];
            // 计算在哈希数组中的下标
            int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
            // 将结点放入i对应的数组位置
            table[i] = new Entry(firstKey, firstValue);
            // 结点数加1
            size = 1;
            // 调整临界点(下次扩容的值)
            setThreshold(INITIAL_CAPACITY);
        }

        // 从给定的父映射构造一个新映射(从就的map中移植到新的map中)
        private ThreadLocalMap(ThreadLocalMap parentMap) {
            // 获取旧的哈希数组
            Entry[] parentTable = parentMap.table;
            // 获取数组长度
            int len = parentTable.length;
            // 计算临界点
            setThreshold(len);
            // 创建新的哈希数组(长度和旧的一样)
            table = new Entry[len];
            // 遍历旧的哈希数组
            for (int j = 0; j < len; j++) {
                // 获取下标对应的value值(entry结点)
                Entry e = parentTable[j];
                // e不为null
                if (e != null) {
                    @SuppressWarnings("unchecked")
                    // 获取对应的key
                            ThreadLocal<Object> key = (ThreadLocal<Object>) e.get();
                    // key不为null
                    if (key != null) {
                        // 获取value值
                        Object value = key.childValue(e.value);
                        // 创建新的结点将key和value放入
                        Entry c = new Entry(key, value);
                        // 计算在哈希数组中的位置
                        int h = key.threadLocalHashCode & (len - 1);
                        // 下标对应位置不为null
                        while (table[h] != null)
                            // 重新计算下标
                            h = nextIndex(h, len);
                        // 将结点放入哈希数组
                        table[h] = c;
                        // 结点数加1
                        size++;
                    }
                }
            }
        }

        // 根据key获取节点
        private Entry getEntry(ThreadLocal<?> key) {
            // 计算在哈希数组中的下标
            int i = key.threadLocalHashCode & (table.length - 1);
            // 通过下标获取对应的节点
            Entry e = table[i];
            // 如果当前结点不为null 并且key相等
            if (e != null && e.get() == key)
                // 返回结点
                return e;
            else
                //
                return getEntryAfterMiss(key, i, e);
        }

        private Entry getEntryAfterMiss(ThreadLocal<?> key, int i, Entry e) {
            Entry[] tab = table;
            int len = tab.length;

            while (e != null) {
                ThreadLocal<?> k = e.get();
                if (k == key)
                    return e;
                if (k == null)
                    expungeStaleEntry(i);
                else
                    i = nextIndex(i, len);
                e = tab[i];
            }
            return null;
        }

        // 设置结点元素
        private void set(ThreadLocal<?> key, Object value) {
            // 哈希数组
            Entry[] tab = table;
            // 获取哈希数组的长度
            int len = tab.length;
            // 计算在哈希数组中的下标
            int i = key.threadLocalHashCode & (len - 1);
            // 通过下标获取哈希数组中对应的结点
            for (Entry e = tab[i];
                 // 结点不为null
                 e != null;
                 // 重新计算下标获取哈希数组中的元素
                 e = tab[i = nextIndex(i, len)]) {
                // 获取节点中key
                ThreadLocal<?> k = e.get();
                // 如何key相等
                if (k == key) {
                    // 将value覆盖原来的value
                    e.value = value;
                    return;
                }
                // key等于null
                if (k == null) {
                    replaceStaleEntry(key, value, i);
                    return;
                }
            }
            // 哈希数组当前下标对应位置为null，可直接将新结点插入
            tab[i] = new Entry(key, value);
            // 结点数加1
            int sz = ++size;
            // 判断是否需要进行扩容
            if (!cleanSomeSlots(i, sz) && sz >= threshold)
                //
                rehash();
        }

        private void remove(ThreadLocal<?> key) {
            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len - 1);
            for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) {
                if (e.get() == key) {
                    e.clear();
                    expungeStaleEntry(i);
                    return;
                }
            }
        }

        private void replaceStaleEntry(ThreadLocal<?> key, Object value,
                                       int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;
            Entry e;

            int slotToExpunge = staleSlot;
            for (int i = prevIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = prevIndex(i, len))
                if (e.get() == null)
                    slotToExpunge = i;

            for (int i = nextIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = nextIndex(i, len)) {
                ThreadLocal<?> k = e.get();

                if (k == key) {
                    e.value = value;

                    tab[i] = tab[staleSlot];
                    tab[staleSlot] = e;

                    if (slotToExpunge == staleSlot)
                        slotToExpunge = i;
                    cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
                    return;
                }

                if (k == null && slotToExpunge == staleSlot)
                    slotToExpunge = i;
            }

            tab[staleSlot].value = null;
            tab[staleSlot] = new Entry(key, value);

            if (slotToExpunge != staleSlot)
                cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
        }

        private int expungeStaleEntry(int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;

            tab[staleSlot].value = null;
            tab[staleSlot] = null;
            size--;

            Entry e;
            int i;
            for (i = nextIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = nextIndex(i, len)) {
                ThreadLocal<?> k = e.get();
                if (k == null) {
                    e.value = null;
                    tab[i] = null;
                    size--;
                } else {
                    int h = k.threadLocalHashCode & (len - 1);
                    if (h != i) {
                        tab[i] = null;

                        while (tab[h] != null)
                            h = nextIndex(h, len);
                        tab[h] = e;
                    }
                }
            }
            return i;
        }

        private boolean cleanSomeSlots(int i, int n) {
            boolean removed = false;
            Entry[] tab = table;
            int len = tab.length;
            do {
                i = nextIndex(i, len);
                Entry e = tab[i];
                if (e != null && e.get() == null) {
                    n = len;
                    removed = true;
                    i = expungeStaleEntry(i);
                }
            } while ((n >>>= 1) != 0);
            return removed;
        }

        private void rehash() {
            expungeStaleEntries();

            if (size >= threshold - threshold / 4)
                resize();
        }

        private void resize() {
            Entry[] oldTab = table;
            int oldLen = oldTab.length;
            int newLen = oldLen * 2;
            Entry[] newTab = new Entry[newLen];
            int count = 0;

            for (int j = 0; j < oldLen; ++j) {
                Entry e = oldTab[j];
                if (e != null) {
                    ThreadLocal<?> k = e.get();
                    if (k == null) {
                        e.value = null; // Help the GC
                    } else {
                        int h = k.threadLocalHashCode & (newLen - 1);
                        while (newTab[h] != null)
                            h = nextIndex(h, newLen);
                        newTab[h] = e;
                        count++;
                    }
                }
            }

            setThreshold(newLen);
            size = count;
            table = newTab;
        }

        private void expungeStaleEntries() {
            Entry[] tab = table;
            int len = tab.length;
            for (int j = 0; j < len; j++) {
                Entry e = tab[j];
                if (e != null && e.get() == null)
                    expungeStaleEntry(j);
            }
        }
    }
}
