package java.util.concurrent;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;


public class ConcurrentHashMap<K, V> extends AbstractMap<K, V>
        implements ConcurrentMap<K, V>, Serializable {
    private static final long serialVersionUID = 7249069246763182397L;

    // 最大容量
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    // 默认容量
    private static final int DEFAULT_CAPACITY = 16;

    // 最大的数组大小（非2的幂）
    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    // 默认并发级别
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    // 负载因子
    private static final float LOAD_FACTOR = 0.75f;

    // 链表变成红黑树的阈值阈值
    static final int TREEIFY_THRESHOLD = 8;

    // 红黑树变成链表的取消阈值
    static final int UNTREEIFY_THRESHOLD = 6;

    // 最小树容量
    static final int MIN_TREEIFY_CAPACITY = 64;

    /**
     * Minimum number of rebinnings per transfer step. Ranges are
     * subdivided to allow multiple resizer threads.  This value
     * serves as a lower bound to avoid resizers encountering
     * excessive memory contention.  The value should be at least
     * DEFAULT_CAPACITY.
     */
    private static final int MIN_TRANSFER_STRIDE = 16;

    /**
     * The number of bits used for generation stamp in sizeCtl.
     * Must be at least 6 for 32bit arrays.
     */
    private static int RESIZE_STAMP_BITS = 16;

    /**
     * The maximum number of threads that can help resize.
     * Must fit in 32 - RESIZE_STAMP_BITS bits.
     */
    private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;

    /**
     * The bit shift for recording size stamp in sizeCtl.
     */
    private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;

    /*
     * Encodings for Node hash fields. See above for explanation.
     */
    static final int MOVED = -1; // hash for forwarding nodes
    static final int TREEBIN = -2; // hash for roots of trees
    static final int RESERVED = -3; // hash for transient reservations
    static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash

    // CPUS的数量，用于限制某些尺寸
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    // 用于序列化兼容性。
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("segments", java.util.concurrent.ConcurrentHashMap.Segment[].class),
            new ObjectStreamField("segmentMask", Integer.TYPE),
            new ObjectStreamField("segmentShift", Integer.TYPE)
    };

    /* ---------------- Nodes -------------- */

    // 结点对象
    static class Node<K, V> implements Map.Entry<K, V> {
        // 哈希值
        final int hash;
        // key
        final K key;
        // value
        volatile V val;
        // 下一个结点
        volatile java.util.concurrent.ConcurrentHashMap.Node<K, V> next;

        // 带参构造器
        Node(int hash, K key, V val, java.util.concurrent.ConcurrentHashMap.Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        // 获取key
        public final K getKey() {
            return key;
        }

        // 获取value
        public final V getValue() {
            return val;
        }

        // 获取hashCode
        public final int hashCode() {
            return key.hashCode() ^ val.hashCode();
        }

        // toString
        public final String toString() {
            return key + "=" + val;
        }

        // 设置value
        public final V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        // equals方法
        public final boolean equals(Object o) {
            Object k, v, u;
            Map.Entry<?, ?> e;
            return ((o instanceof Map.Entry) &&
                    (k = (e = (Map.Entry<?, ?>) o).getKey()) != null &&
                    (v = e.getValue()) != null &&
                    (k == key || k.equals(key)) &&
                    (v == (u = val) || v.equals(u)));
        }


        java.util.concurrent.ConcurrentHashMap.Node<K, V> find(int h, Object k) {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> e = this;
            if (k != null) {
                do {
                    K ek;
                    if (e.hash == h &&
                            ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                } while ((e = e.next) != null);
            }
            return null;
        }
    }

    /* ---------------- Static utilities -------------- */

    // 计算哈希值
    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    // 给定表的大小 返回两个表的大小
    private static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /**
     * Returns x's Class if it is of the form "class C implements
     * Comparable<C>", else null.
     */
    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c;
            Type[] ts, as;
            Type t;
            ParameterizedType p;
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
            if ((ts = c.getGenericInterfaces()) != null) {
                for (int i = 0; i < ts.length; ++i) {
                    if (((t = ts[i]) instanceof ParameterizedType) &&
                            ((p = (ParameterizedType) t).getRawType() ==
                                    Comparable.class) &&
                            (as = p.getActualTypeArguments()) != null &&
                            as.length == 1 && as[0] == c) // type arg is c
                        return c;
                }
            }
        }
        return null;
    }

    /**
     * Returns k.compareTo(x) if x matches kc (k's screened comparable
     * class), else 0.
     */
    @SuppressWarnings({"rawtypes", "unchecked"}) // for cast to Comparable
    static int compareComparables(Class<?> kc, Object k, Object x) {
        return (x == null || x.getClass() != kc ? 0 :
                ((Comparable) k).compareTo(x));
    }

    /* ---------------- Table element access -------------- */

    /*
     * Volatile access methods are used for table elements as well as
     * elements of in-progress next table while resizing.  All uses of
     * the tab arguments must be null checked by callers.  All callers
     * also paranoically precheck that tab's length is not zero (or an
     * equivalent check), thus ensuring that any index argument taking
     * the form of a hash value anded with (length - 1) is a valid
     * index.  Note that, to be correct wrt arbitrary concurrency
     * errors by users, these checks must operate on local variables,
     * which accounts for some odd-looking inline assignments below.
     * Note that calls to setTabAt always occur within locked regions,
     * and so in principle require only release ordering, not
     * full volatile semantics, but are currently coded as volatile
     * writes to be conservative.
     */

    @SuppressWarnings("unchecked")
    static final <K, V> java.util.concurrent.ConcurrentHashMap.Node<K, V> tabAt(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, int i) {
        return (java.util.concurrent.ConcurrentHashMap.Node<K, V>) U.getObjectVolatile(tab, ((long) i << ASHIFT) + ABASE);
    }

    // 将结点放入指定哈希桶位置位置
    static final <K, V> boolean casTabAt(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, int i,
                                         java.util.concurrent.ConcurrentHashMap.Node<K, V> c, java.util.concurrent.ConcurrentHashMap.Node<K, V> v) {
        return U.compareAndSwapObject(tab, ((long) i << ASHIFT) + ABASE, c, v);
    }

    static final <K, V> void setTabAt(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, int i, java.util.concurrent.ConcurrentHashMap.Node<K, V> v) {
        U.putObjectVolatile(tab, ((long) i << ASHIFT) + ABASE, v);
    }

    /* ---------------- Fields -------------- */

    /**
     * The array of bins. Lazily initialized upon first insertion.
     * Size is always a power of two. Accessed directly by iterators.
     */
    transient volatile java.util.concurrent.ConcurrentHashMap.Node<K, V>[] table;

    /**
     * The next table to use; non-null only while resizing.
     */
    private transient volatile java.util.concurrent.ConcurrentHashMap.Node<K, V>[] nextTable;

    /**
     * Base counter value, used mainly when there is no contention,
     * but also as a fallback during table initialization
     * races. Updated via CAS.
     */
    private transient volatile long baseCount;


    //控制标识符，用来控制table的初始化和扩容的操作，不同的值有不同的含义
    //当为负数时：-1代表正在初始化，-N代表有N-1个线程正在 进行扩容
    //当为0时：代表当时的table还没有被初始化
    //当为正数时：表示初始化或者下一次进行扩容的大小
    private transient volatile int sizeCtl;

    /**
     * The next table index (plus one) to split while resizing.
     */
    private transient volatile int transferIndex;

    /**
     * Spinlock (locked via CAS) used when resizing and/or creating CounterCells.
     */
    private transient volatile int cellsBusy;

    /**
     * Table of counter cells. When non-null, size is a power of 2.
     */
    private transient volatile java.util.concurrent.ConcurrentHashMap.CounterCell[] counterCells;

    // views
    private transient java.util.concurrent.ConcurrentHashMap.KeySetView<K, V> keySet;
    private transient java.util.concurrent.ConcurrentHashMap.ValuesView<K, V> values;
    private transient java.util.concurrent.ConcurrentHashMap.EntrySetView<K, V> entrySet;


    // 创建一个新的实例，默认大小为16
    public ConcurrentHashMap() {
    }

    // 创建一个新的实例，指定初始化大小
    public ConcurrentHashMap(int initialCapacity) {
        // 指定大小小于0
        if (initialCapacity < 0)
            // 抛出异常
            throw new IllegalArgumentException();
        int cap = ((initialCapacity >= (MAXIMUM_CAPACITY >>> 1)) ?
                MAXIMUM_CAPACITY :
                tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1));
        // 当前容量
        this.sizeCtl = cap;
    }

    /**
     * Creates a new map with the same mappings as the given map.
     *
     * @param m the map
     */
    // 创建一个新的实例 并添加一个集合
    public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
        // 表的容量
        this.sizeCtl = DEFAULT_CAPACITY;
        // 添加一个map
        putAll(m);
    }

    /**
     * Creates a new, empty map with an initial table size based on
     * the given number of elements ({@code initialCapacity}) and
     * initial table density ({@code loadFactor}).
     *
     * @param initialCapacity the initial capacity. The implementation
     *                        performs internal sizing to accommodate this many elements,
     *                        given the specified load factor.
     * @param loadFactor      the load factor (table density) for
     *                        establishing the initial table size
     * @throws IllegalArgumentException if the initial capacity of
     *                                  elements is negative or the load factor is nonpositive
     * @since 1.6
     */
    public ConcurrentHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 1);
    }

    /**
     * Creates a new, empty map with an initial table size based on
     * the given number of elements ({@code initialCapacity}), table
     * density ({@code loadFactor}), and number of concurrently
     * updating threads ({@code concurrencyLevel}).
     *
     * @param initialCapacity  the initial capacity. The implementation
     *                         performs internal sizing to accommodate this many elements,
     *                         given the specified load factor.
     * @param loadFactor       the load factor (table density) for
     *                         establishing the initial table size
     * @param concurrencyLevel the estimated number of concurrently
     *                         updating threads. The implementation may use this value as
     *                         a sizing hint.
     * @throws IllegalArgumentException if the initial capacity is
     *                                  negative or the load factor or concurrencyLevel are
     *                                  nonpositive
     */
    public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();

        if (initialCapacity < concurrencyLevel)
            initialCapacity = concurrencyLevel;

        long size = (long) (1.0 + (long) initialCapacity / loadFactor);

        int cap = (size >= (long) MAXIMUM_CAPACITY) ?
                MAXIMUM_CAPACITY : tableSizeFor((int) size);
        this.sizeCtl = cap;
    }

    // Original (since JDK1.2) Map methods

    /**
     * {@inheritDoc}
     */
    public int size() {
        long n = sumCount();
        return ((n < 0L) ? 0 :
                (n > (long) Integer.MAX_VALUE) ? Integer.MAX_VALUE :
                        (int) n);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return sumCount() <= 0L; // ignore transient negative values
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code key.equals(k)},
     * then this method returns {@code v}; otherwise it returns
     * {@code null}.  (There can be at most one such mapping.)
     *
     * @throws NullPointerException if the specified key is null
     */
    public V get(Object key) {
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab;
        java.util.concurrent.ConcurrentHashMap.Node<K, V> e, p;
        int n, eh;
        K ek;
        int h = spread(key.hashCode());
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (e = tabAt(tab, (n - 1) & h)) != null) {
            if ((eh = e.hash) == h) {
                if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                    return e.val;
            } else if (eh < 0)
                return (p = e.find(h, key)) != null ? p.val : null;
            while ((e = e.next) != null) {
                if (e.hash == h &&
                        ((ek = e.key) == key || (ek != null && key.equals(ek))))
                    return e.val;
            }
        }
        return null;
    }

    /**
     * Tests if the specified object is a key in this table.
     *
     * @param key possible key
     * @return {@code true} if and only if the specified object
     * is a key in this table, as determined by the
     * {@code equals} method; {@code false} otherwise
     * @throws NullPointerException if the specified key is null
     */
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the
     * specified value. Note: This method may require a full traversal
     * of the map, and is much slower than method {@code containsKey}.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the
     * specified value
     * @throws NullPointerException if the specified value is null
     */
    public boolean containsValue(Object value) {
        if (value == null)
            throw new NullPointerException();
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
        if ((t = table) != null) {
            java.util.concurrent.ConcurrentHashMap.Traverser<K, V> it = new java.util.concurrent.ConcurrentHashMap.Traverser<K, V>(t, t.length, 0, t.length);
            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = it.advance()) != null; ) {
                V v;
                if ((v = p.val) == value || (v != null && value.equals(v)))
                    return true;
            }
        }
        return false;
    }

    // 向集合中添加元素
    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    // 添加元素的实现
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        // key 和 value 不能为空
        if (key == null || value == null) throw new NullPointerException();
        // 计算哈希值
        int hash = spread(key.hashCode());
        int binCount = 0;
        for (java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab = table; ; ) {
            // f = 目标位置元素
            java.util.concurrent.ConcurrentHashMap.Node<K, V> f;
            int n, i, fh;
            // 如果哈希表为空
            if (tab == null || (n = tab.length) == 0)
                // 初始化哈希表(数组桶为空，初始化数组桶（自旋+CAS))
                tab = initTable();
                // 计算哈希值获(i)取对应位置的哈希结点赋值给f，判断f是否为null(当前位置是否有元素存在)
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                // 桶内为空，CAS 放入，不加锁，成功了就直接 break 跳出
                if (casTabAt(tab, i, null, new java.util.concurrent.ConcurrentHashMap.Node<K, V>(hash, key, value, null)))
                    break;
                // f不为null时,fh等于f结点中的hash值,并判断hash是否等于MOVED((static final int MOVED = -1))
            } else if ((fh = f.hash) == MOVED)
                // 让当前线程调用helpTransfer也参与到扩容过程中来，扩容完毕后tab指向新table。
                tab = helpTransfer(tab, f);
            else {
                // 定义oldVal
                V oldVal = null;
                // 进入同步代码块(锁对象为当前结点对象)，对每个数组桶加锁
                synchronized (f) {
                    // 判断当前哈希值对应的结点是否等于f(肯定是等于的，因为在数组桶上冲突才到这)
                    if (tabAt(tab, i) == f) {
                        // 判断当前结点哈希值是否大于等0
                        if (fh >= 0) {
                            // 当前哈希桶插入元素的操作数
                            binCount = 1;
                            // 循环(将冲突的f赋值给e)，如果哈希数组不冲突则循环链表
                            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> e = f; ; ++binCount) {
                                // 定义key
                                K ek;
                                // 判断当前冲突结点和将要插入的结点进行判断(哈希值，key，value)
                                if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                                    // 能进来说明将要插入结点在哈希数组中已经存在(将value赋值给oldVal)
                                    oldVal = e.val;
                                    // onlyIfAbsent默认为false
                                    if (!onlyIfAbsent)
                                        // 当前结点value等于将要插入的value(就是替换value值)
                                        e.val = value;
                                    // 跳出循环
                                    break;
                                }
                                // 到这说明判断当前冲突结点和将要插入的结点进行判断不冲突(将哈希数组对应的结点赋值给pred，当做链表的头结点)
                                java.util.concurrent.ConcurrentHashMap.Node<K, V> pred = e;
                                // 获取当前哈希数组的下一个结点判断是否为null(链表的下一个结点)
                                if ((e = e.next) == null) {
                                    // 如果等于null 将插入结点作为链表的下一个结点
                                    pred.next = new java.util.concurrent.ConcurrentHashMap.Node<K, V>(hash, key,
                                            value, null);
                                    // 跳出循环
                                    break;
                                }
                            }
                            //到这块说明当前结点哈希值小于0判断 当前结点是否属于树结点
                        } else if (f instanceof java.util.concurrent.ConcurrentHashMap.TreeBin) {
                            // 定义结点 p
                            java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
                            // 操作数为2
                            binCount = 2;
                            if ((p = ((java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>) f).putTreeVal(hash, key,
                                    value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    // 当操作数大于8时转为树
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    // 由于hash值或key相同替换value，将原来的value返回
                    if (oldVal != null)
                        return oldVal;
                    // 跳出循环
                    break;
                }
            }
        }
        //更新size，检测扩容
        addCount(1L, binCount);
        return null;
    }

    // 将所有映射从指定的映射复制到此映射。
    // 这些映射替换了此映射对于指定映射中当前存在的任何*键的映射。
    public void putAll(Map<? extends K, ? extends V> m) {
        // 尝试调整表的大小以容纳给定数量的元素。
        tryPresize(m.size());
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            putVal(e.getKey(), e.getValue(), false);
    }

    /**
     * Removes the key (and its corresponding value) from this map.
     * This method does nothing if the key is not in the map.
     *
     * @param key the key that needs to be removed
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key}
     * @throws NullPointerException if the specified key is null
     */
    public V remove(Object key) {
        return replaceNode(key, null, null);
    }

    /**
     * Implementation for the four public remove/replace methods:
     * Replaces node value with v, conditional upon match of cv if
     * non-null.  If resulting value is null, delete.
     */
    final V replaceNode(Object key, V value, Object cv) {
        int hash = spread(key.hashCode());
        for (java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab = table; ; ) {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> f;
            int n, i, fh;
            if (tab == null || (n = tab.length) == 0 ||
                    (f = tabAt(tab, i = (n - 1) & hash)) == null)
                break;
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                boolean validated = false;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            validated = true;
                            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> e = f, pred = null; ; ) {
                                K ek;
                                if (e.hash == hash &&
                                        ((ek = e.key) == key ||
                                                (ek != null && key.equals(ek)))) {
                                    V ev = e.val;
                                    if (cv == null || cv == ev ||
                                            (ev != null && cv.equals(ev))) {
                                        oldVal = ev;
                                        if (value != null)
                                            e.val = value;
                                        else if (pred != null)
                                            pred.next = e.next;
                                        else
                                            setTabAt(tab, i, e.next);
                                    }
                                    break;
                                }
                                pred = e;
                                if ((e = e.next) == null)
                                    break;
                            }
                        } else if (f instanceof java.util.concurrent.ConcurrentHashMap.TreeBin) {
                            validated = true;
                            java.util.concurrent.ConcurrentHashMap.TreeBin<K, V> t = (java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>) f;
                            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r, p;
                            if ((r = t.root) != null &&
                                    (p = r.findTreeNode(hash, key, null)) != null) {
                                V pv = p.val;
                                if (cv == null || cv == pv ||
                                        (pv != null && cv.equals(pv))) {
                                    oldVal = pv;
                                    if (value != null)
                                        p.val = value;
                                    else if (t.removeTreeNode(p))
                                        setTabAt(tab, i, untreeify(t.first));
                                }
                            }
                        }
                    }
                }
                if (validated) {
                    if (oldVal != null) {
                        if (value == null)
                            addCount(-1L, -1);
                        return oldVal;
                    }
                    break;
                }
            }
        }
        return null;
    }

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        long delta = 0L; // negative number of deletions
        int i = 0;
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab = table;
        while (tab != null && i < tab.length) {
            int fh;
            java.util.concurrent.ConcurrentHashMap.Node<K, V> f = tabAt(tab, i);
            if (f == null)
                ++i;
            else if ((fh = f.hash) == MOVED) {
                tab = helpTransfer(tab, f);
                i = 0; // restart
            } else {
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        java.util.concurrent.ConcurrentHashMap.Node<K, V> p = (fh >= 0 ? f :
                                (f instanceof java.util.concurrent.ConcurrentHashMap.TreeBin) ?
                                        ((java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>) f).first : null);
                        while (p != null) {
                            --delta;
                            p = p.next;
                        }
                        setTabAt(tab, i++, null);
                    }
                }
            }
        }
        if (delta != 0L)
            addCount(delta, -1);
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa. The set supports element
     * removal, which removes the corresponding mapping from this map,
     * via the {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear}
     * operations.  It does not support the {@code add} or
     * {@code addAll} operations.
     *
     * <p>The view's iterators and spliterators are
     * <a href="package-summary.html#Weakly"><i>weakly consistent</i></a>.
     *
     * <p>The view's {@code spliterator} reports {@link Spliterator#CONCURRENT},
     * {@link Spliterator#DISTINCT}, and {@link Spliterator#NONNULL}.
     *
     * @return the set view
     */
    public java.util.concurrent.ConcurrentHashMap.KeySetView<K, V> keySet() {
        java.util.concurrent.ConcurrentHashMap.KeySetView<K, V> ks;
        return (ks = keySet) != null ? ks : (keySet = new java.util.concurrent.ConcurrentHashMap.KeySetView<K, V>(this, null));
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  The collection
     * supports element removal, which removes the corresponding
     * mapping from this map, via the {@code Iterator.remove},
     * {@code Collection.remove}, {@code removeAll},
     * {@code retainAll}, and {@code clear} operations.  It does not
     * support the {@code add} or {@code addAll} operations.
     *
     * <p>The view's iterators and spliterators are
     * <a href="package-summary.html#Weakly"><i>weakly consistent</i></a>.
     *
     * <p>The view's {@code spliterator} reports {@link Spliterator#CONCURRENT}
     * and {@link Spliterator#NONNULL}.
     *
     * @return the collection view
     */
    public Collection<V> values() {
        java.util.concurrent.ConcurrentHashMap.ValuesView<K, V> vs;
        return (vs = values) != null ? vs : (values = new java.util.concurrent.ConcurrentHashMap.ValuesView<K, V>(this));
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  The set supports element
     * removal, which removes the corresponding mapping from the map,
     * via the {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear}
     * operations.
     *
     * <p>The view's iterators and spliterators are
     * <a href="package-summary.html#Weakly"><i>weakly consistent</i></a>.
     *
     * <p>The view's {@code spliterator} reports {@link Spliterator#CONCURRENT},
     * {@link Spliterator#DISTINCT}, and {@link Spliterator#NONNULL}.
     *
     * @return the set view
     */
    public Set<Map.Entry<K, V>> entrySet() {
        java.util.concurrent.ConcurrentHashMap.EntrySetView<K, V> es;
        return (es = entrySet) != null ? es : (entrySet = new java.util.concurrent.ConcurrentHashMap.EntrySetView<K, V>(this));
    }

    /**
     * Returns the hash code value for this {@link Map}, i.e.,
     * the sum of, for each key-value pair in the map,
     * {@code key.hashCode() ^ value.hashCode()}.
     *
     * @return the hash code value for this map
     */
    public int hashCode() {
        int h = 0;
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
        if ((t = table) != null) {
            java.util.concurrent.ConcurrentHashMap.Traverser<K, V> it = new java.util.concurrent.ConcurrentHashMap.Traverser<K, V>(t, t.length, 0, t.length);
            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = it.advance()) != null; )
                h += p.key.hashCode() ^ p.val.hashCode();
        }
        return h;
    }

    /**
     * Returns a string representation of this map.  The string
     * representation consists of a list of key-value mappings (in no
     * particular order) enclosed in braces ("{@code {}}").  Adjacent
     * mappings are separated by the characters {@code ", "} (comma
     * and space).  Each key-value mapping is rendered as the key
     * followed by an equals sign ("{@code =}") followed by the
     * associated value.
     *
     * @return a string representation of this map
     */
    public String toString() {
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
        int f = (t = table) == null ? 0 : t.length;
        java.util.concurrent.ConcurrentHashMap.Traverser<K, V> it = new java.util.concurrent.ConcurrentHashMap.Traverser<K, V>(t, f, 0, f);
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
        if ((p = it.advance()) != null) {
            for (; ; ) {
                K k = p.key;
                V v = p.val;
                sb.append(k == this ? "(this Map)" : k);
                sb.append('=');
                sb.append(v == this ? "(this Map)" : v);
                if ((p = it.advance()) == null)
                    break;
                sb.append(',').append(' ');
            }
        }
        return sb.append('}').toString();
    }

    /**
     * Compares the specified object with this map for equality.
     * Returns {@code true} if the given object is a map with the same
     * mappings as this map.  This operation may return misleading
     * results if either map is concurrently modified during execution
     * of this method.
     *
     * @param o object to be compared for equality with this map
     * @return {@code true} if the specified object is equal to this map
     */
    public boolean equals(Object o) {
        if (o != this) {
            if (!(o instanceof Map))
                return false;
            Map<?, ?> m = (Map<?, ?>) o;
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
            int f = (t = table) == null ? 0 : t.length;
            java.util.concurrent.ConcurrentHashMap.Traverser<K, V> it = new java.util.concurrent.ConcurrentHashMap.Traverser<K, V>(t, f, 0, f);
            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = it.advance()) != null; ) {
                V val = p.val;
                Object v = m.get(p.key);
                if (v == null || (v != val && !v.equals(val)))
                    return false;
            }
            for (Map.Entry<?, ?> e : m.entrySet()) {
                Object mk, mv, v;
                if ((mk = e.getKey()) == null ||
                        (mv = e.getValue()) == null ||
                        (v = get(mk)) == null ||
                        (mv != v && !mv.equals(v)))
                    return false;
            }
        }
        return true;
    }

    /**
     * Stripped-down version of helper class used in previous version,
     * declared for the sake of serialization compatibility
     */
    static class Segment<K, V> extends ReentrantLock implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        final float loadFactor;

        Segment(float lf) {
            this.loadFactor = lf;
        }
    }

    /**
     * Saves the state of the {@code ConcurrentHashMap} instance to a
     * stream (i.e., serializes it).
     *
     * @param s the stream
     * @throws java.io.IOException if an I/O error occurs
     * @serialData the key (Object) and value (Object)
     * for each key-value mapping, followed by a null pair.
     * The key-value mappings are emitted in no particular order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // For serialization compatibility
        // Emulate segment calculation from previous version of this class
        int sshift = 0;
        int ssize = 1;
        while (ssize < DEFAULT_CONCURRENCY_LEVEL) {
            ++sshift;
            ssize <<= 1;
        }
        int segmentShift = 32 - sshift;
        int segmentMask = ssize - 1;
        @SuppressWarnings("unchecked")
        java.util.concurrent.ConcurrentHashMap.Segment<K, V>[] segments = (java.util.concurrent.ConcurrentHashMap.Segment<K, V>[])
                new java.util.concurrent.ConcurrentHashMap.Segment<?, ?>[DEFAULT_CONCURRENCY_LEVEL];
        for (int i = 0; i < segments.length; ++i)
            segments[i] = new java.util.concurrent.ConcurrentHashMap.Segment<K, V>(LOAD_FACTOR);
        s.putFields().put("segments", segments);
        s.putFields().put("segmentShift", segmentShift);
        s.putFields().put("segmentMask", segmentMask);
        s.writeFields();

        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
        if ((t = table) != null) {
            java.util.concurrent.ConcurrentHashMap.Traverser<K, V> it = new java.util.concurrent.ConcurrentHashMap.Traverser<K, V>(t, t.length, 0, t.length);
            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = it.advance()) != null; ) {
                s.writeObject(p.key);
                s.writeObject(p.val);
            }
        }
        s.writeObject(null);
        s.writeObject(null);
        segments = null; // throw away
    }

    /**
     * Reconstitutes the instance from a stream (that is, deserializes it).
     *
     * @param s the stream
     * @throws ClassNotFoundException if the class of a serialized object
     *                                could not be found
     * @throws java.io.IOException    if an I/O error occurs
     */
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        /*
         * To improve performance in typical cases, we create nodes
         * while reading, then place in table once size is known.
         * However, we must also validate uniqueness and deal with
         * overpopulated bins while doing so, which requires
         * specialized versions of putVal mechanics.
         */
        sizeCtl = -1; // force exclusion for table construction
        s.defaultReadObject();
        long size = 0L;
        java.util.concurrent.ConcurrentHashMap.Node<K, V> p = null;
        for (; ; ) {
            @SuppressWarnings("unchecked")
            K k = (K) s.readObject();
            @SuppressWarnings("unchecked")
            V v = (V) s.readObject();
            if (k != null && v != null) {
                p = new java.util.concurrent.ConcurrentHashMap.Node<K, V>(spread(k.hashCode()), k, v, p);
                ++size;
            } else
                break;
        }
        if (size == 0L)
            sizeCtl = 0;
        else {
            int n;
            if (size >= (long) (MAXIMUM_CAPACITY >>> 1))
                n = MAXIMUM_CAPACITY;
            else {
                int sz = (int) size;
                n = tableSizeFor(sz + (sz >>> 1) + 1);
            }
            @SuppressWarnings("unchecked")
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab = (java.util.concurrent.ConcurrentHashMap.Node<K, V>[]) new java.util.concurrent.ConcurrentHashMap.Node<?, ?>[n];
            int mask = n - 1;
            long added = 0L;
            while (p != null) {
                boolean insertAtFront;
                java.util.concurrent.ConcurrentHashMap.Node<K, V> next = p.next, first;
                int h = p.hash, j = h & mask;
                if ((first = tabAt(tab, j)) == null)
                    insertAtFront = true;
                else {
                    K k = p.key;
                    if (first.hash < 0) {
                        java.util.concurrent.ConcurrentHashMap.TreeBin<K, V> t = (java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>) first;
                        if (t.putTreeVal(h, k, p.val) == null)
                            ++added;
                        insertAtFront = false;
                    } else {
                        int binCount = 0;
                        insertAtFront = true;
                        java.util.concurrent.ConcurrentHashMap.Node<K, V> q;
                        K qk;
                        for (q = first; q != null; q = q.next) {
                            if (q.hash == h &&
                                    ((qk = q.key) == k ||
                                            (qk != null && k.equals(qk)))) {
                                insertAtFront = false;
                                break;
                            }
                            ++binCount;
                        }
                        if (insertAtFront && binCount >= TREEIFY_THRESHOLD) {
                            insertAtFront = false;
                            ++added;
                            p.next = first;
                            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> hd = null, tl = null;
                            for (q = p; q != null; q = q.next) {
                                java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> t = new java.util.concurrent.ConcurrentHashMap.TreeNode<K, V>
                                        (q.hash, q.key, q.val, null, null);
                                if ((t.prev = tl) == null)
                                    hd = t;
                                else
                                    tl.next = t;
                                tl = t;
                            }
                            setTabAt(tab, j, new java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>(hd));
                        }
                    }
                }
                if (insertAtFront) {
                    ++added;
                    p.next = first;
                    setTabAt(tab, j, p);
                }
                p = next;
            }
            table = tab;
            sizeCtl = n - (n >>> 2);
            baseCount = added;
        }
    }

    // ConcurrentMap methods

    /**
     * {@inheritDoc}
     *
     * @return the previous value associated with the specified key,
     * or {@code null} if there was no mapping for the key
     * @throws NullPointerException if the specified key or value is null
     */
    public V putIfAbsent(K key, V value) {
        return putVal(key, value, true);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the specified key is null
     */
    public boolean remove(Object key, Object value) {
        if (key == null)
            throw new NullPointerException();
        return value != null && replaceNode(key, null, value) != null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if any of the arguments are null
     */
    public boolean replace(K key, V oldValue, V newValue) {
        if (key == null || oldValue == null || newValue == null)
            throw new NullPointerException();
        return replaceNode(key, newValue, oldValue) != null;
    }

    /**
     * {@inheritDoc}
     *
     * @return the previous value associated with the specified key,
     * or {@code null} if there was no mapping for the key
     * @throws NullPointerException if the specified key or value is null
     */
    public V replace(K key, V value) {
        if (key == null || value == null)
            throw new NullPointerException();
        return replaceNode(key, value, null);
    }

    // Overrides of JDK8+ Map extension method defaults

    /**
     * Returns the value to which the specified key is mapped, or the
     * given default value if this map contains no mapping for the
     * key.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the value to return if this map contains
     *                     no mapping for the given key
     * @return the mapping for the key, if present; else the default value
     * @throws NullPointerException if the specified key is null
     */
    public V getOrDefault(Object key, V defaultValue) {
        V v;
        return (v = get(key)) == null ? defaultValue : v;
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        if (action == null) throw new NullPointerException();
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
        if ((t = table) != null) {
            java.util.concurrent.ConcurrentHashMap.Traverser<K, V> it = new java.util.concurrent.ConcurrentHashMap.Traverser<K, V>(t, t.length, 0, t.length);
            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = it.advance()) != null; ) {
                action.accept(p.key, p.val);
            }
        }
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        if (function == null) throw new NullPointerException();
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
        if ((t = table) != null) {
            java.util.concurrent.ConcurrentHashMap.Traverser<K, V> it = new java.util.concurrent.ConcurrentHashMap.Traverser<K, V>(t, t.length, 0, t.length);
            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = it.advance()) != null; ) {
                V oldValue = p.val;
                for (K key = p.key; ; ) {
                    V newValue = function.apply(key, oldValue);
                    if (newValue == null)
                        throw new NullPointerException();
                    if (replaceNode(key, newValue, oldValue) != null ||
                            (oldValue = get(key)) == null)
                        break;
                }
            }
        }
    }

    /**
     * If the specified key is not already associated with a value,
     * attempts to compute its value using the given mapping function
     * and enters it into this map unless {@code null}.  The entire
     * method invocation is performed atomically, so the function is
     * applied at most once per key.  Some attempted update operations
     * on this map by other threads may be blocked while computation
     * is in progress, so the computation should be short and simple,
     * and must not attempt to update any other mappings of this map.
     *
     * @param key             key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with
     * the specified key, or null if the computed value is null
     * @throws NullPointerException  if the specified key or mappingFunction
     *                               is null
     * @throws IllegalStateException if the computation detectably
     *                               attempts a recursive update to this map that would
     *                               otherwise never complete
     * @throws RuntimeException      or Error if the mappingFunction does so,
     *                               in which case the mapping is left unestablished
     */
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        if (key == null || mappingFunction == null)
            throw new NullPointerException();
        int h = spread(key.hashCode());
        V val = null;
        int binCount = 0;
        for (java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab = table; ; ) {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> f;
            int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & h)) == null) {
                java.util.concurrent.ConcurrentHashMap.Node<K, V> r = new java.util.concurrent.ConcurrentHashMap.ReservationNode<K, V>();
                synchronized (r) {
                    if (casTabAt(tab, i, null, r)) {
                        binCount = 1;
                        java.util.concurrent.ConcurrentHashMap.Node<K, V> node = null;
                        try {
                            if ((val = mappingFunction.apply(key)) != null)
                                node = new java.util.concurrent.ConcurrentHashMap.Node<K, V>(h, key, val, null);
                        } finally {
                            setTabAt(tab, i, node);
                        }
                    }
                }
                if (binCount != 0)
                    break;
            } else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                boolean added = false;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> e = f; ; ++binCount) {
                                K ek;
                                V ev;
                                if (e.hash == h &&
                                        ((ek = e.key) == key ||
                                                (ek != null && key.equals(ek)))) {
                                    val = e.val;
                                    break;
                                }
                                java.util.concurrent.ConcurrentHashMap.Node<K, V> pred = e;
                                if ((e = e.next) == null) {
                                    if ((val = mappingFunction.apply(key)) != null) {
                                        added = true;
                                        pred.next = new java.util.concurrent.ConcurrentHashMap.Node<K, V>(h, key, val, null);
                                    }
                                    break;
                                }
                            }
                        } else if (f instanceof java.util.concurrent.ConcurrentHashMap.TreeBin) {
                            binCount = 2;
                            java.util.concurrent.ConcurrentHashMap.TreeBin<K, V> t = (java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>) f;
                            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r, p;
                            if ((r = t.root) != null &&
                                    (p = r.findTreeNode(h, key, null)) != null)
                                val = p.val;
                            else if ((val = mappingFunction.apply(key)) != null) {
                                added = true;
                                t.putTreeVal(h, key, val);
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (!added)
                        return val;
                    break;
                }
            }
        }
        if (val != null)
            addCount(1L, binCount);
        return val;
    }

    /**
     * If the value for the specified key is present, attempts to
     * compute a new mapping given the key and its current mapped
     * value.  The entire method invocation is performed atomically.
     * Some attempted update operations on this map by other threads
     * may be blocked while computation is in progress, so the
     * computation should be short and simple, and must not attempt to
     * update any other mappings of this map.
     *
     * @param key               key with which a value may be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException  if the specified key or remappingFunction
     *                               is null
     * @throws IllegalStateException if the computation detectably
     *                               attempts a recursive update to this map that would
     *                               otherwise never complete
     * @throws RuntimeException      or Error if the remappingFunction does so,
     *                               in which case the mapping is unchanged
     */
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null)
            throw new NullPointerException();
        int h = spread(key.hashCode());
        V val = null;
        int delta = 0;
        int binCount = 0;
        for (java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab = table; ; ) {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> f;
            int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & h)) == null)
                break;
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> e = f, pred = null; ; ++binCount) {
                                K ek;
                                if (e.hash == h &&
                                        ((ek = e.key) == key ||
                                                (ek != null && key.equals(ek)))) {
                                    val = remappingFunction.apply(key, e.val);
                                    if (val != null)
                                        e.val = val;
                                    else {
                                        delta = -1;
                                        java.util.concurrent.ConcurrentHashMap.Node<K, V> en = e.next;
                                        if (pred != null)
                                            pred.next = en;
                                        else
                                            setTabAt(tab, i, en);
                                    }
                                    break;
                                }
                                pred = e;
                                if ((e = e.next) == null)
                                    break;
                            }
                        } else if (f instanceof java.util.concurrent.ConcurrentHashMap.TreeBin) {
                            binCount = 2;
                            java.util.concurrent.ConcurrentHashMap.TreeBin<K, V> t = (java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>) f;
                            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r, p;
                            if ((r = t.root) != null &&
                                    (p = r.findTreeNode(h, key, null)) != null) {
                                val = remappingFunction.apply(key, p.val);
                                if (val != null)
                                    p.val = val;
                                else {
                                    delta = -1;
                                    if (t.removeTreeNode(p))
                                        setTabAt(tab, i, untreeify(t.first));
                                }
                            }
                        }
                    }
                }
                if (binCount != 0)
                    break;
            }
        }
        if (delta != 0)
            addCount((long) delta, binCount);
        return val;
    }

    /**
     * Attempts to compute a mapping for the specified key and its
     * current mapped value (or {@code null} if there is no current
     * mapping). The entire method invocation is performed atomically.
     * Some attempted update operations on this map by other threads
     * may be blocked while computation is in progress, so the
     * computation should be short and simple, and must not attempt to
     * update any other mappings of this Map.
     *
     * @param key               key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException  if the specified key or remappingFunction
     *                               is null
     * @throws IllegalStateException if the computation detectably
     *                               attempts a recursive update to this map that would
     *                               otherwise never complete
     * @throws RuntimeException      or Error if the remappingFunction does so,
     *                               in which case the mapping is unchanged
     */
    public V compute(K key,
                     BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null)
            throw new NullPointerException();
        int h = spread(key.hashCode());
        V val = null;
        int delta = 0;
        int binCount = 0;
        for (java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab = table; ; ) {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> f;
            int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & h)) == null) {
                java.util.concurrent.ConcurrentHashMap.Node<K, V> r = new java.util.concurrent.ConcurrentHashMap.ReservationNode<K, V>();
                synchronized (r) {
                    if (casTabAt(tab, i, null, r)) {
                        binCount = 1;
                        java.util.concurrent.ConcurrentHashMap.Node<K, V> node = null;
                        try {
                            if ((val = remappingFunction.apply(key, null)) != null) {
                                delta = 1;
                                node = new java.util.concurrent.ConcurrentHashMap.Node<K, V>(h, key, val, null);
                            }
                        } finally {
                            setTabAt(tab, i, node);
                        }
                    }
                }
                if (binCount != 0)
                    break;
            } else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> e = f, pred = null; ; ++binCount) {
                                K ek;
                                if (e.hash == h &&
                                        ((ek = e.key) == key ||
                                                (ek != null && key.equals(ek)))) {
                                    val = remappingFunction.apply(key, e.val);
                                    if (val != null)
                                        e.val = val;
                                    else {
                                        delta = -1;
                                        java.util.concurrent.ConcurrentHashMap.Node<K, V> en = e.next;
                                        if (pred != null)
                                            pred.next = en;
                                        else
                                            setTabAt(tab, i, en);
                                    }
                                    break;
                                }
                                pred = e;
                                if ((e = e.next) == null) {
                                    val = remappingFunction.apply(key, null);
                                    if (val != null) {
                                        delta = 1;
                                        pred.next =
                                                new java.util.concurrent.ConcurrentHashMap.Node<K, V>(h, key, val, null);
                                    }
                                    break;
                                }
                            }
                        } else if (f instanceof java.util.concurrent.ConcurrentHashMap.TreeBin) {
                            binCount = 1;
                            java.util.concurrent.ConcurrentHashMap.TreeBin<K, V> t = (java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>) f;
                            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r, p;
                            if ((r = t.root) != null)
                                p = r.findTreeNode(h, key, null);
                            else
                                p = null;
                            V pv = (p == null) ? null : p.val;
                            val = remappingFunction.apply(key, pv);
                            if (val != null) {
                                if (p != null)
                                    p.val = val;
                                else {
                                    delta = 1;
                                    t.putTreeVal(h, key, val);
                                }
                            } else if (p != null) {
                                delta = -1;
                                if (t.removeTreeNode(p))
                                    setTabAt(tab, i, untreeify(t.first));
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    break;
                }
            }
        }
        if (delta != 0)
            addCount((long) delta, binCount);
        return val;
    }

    /**
     * If the specified key is not already associated with a
     * (non-null) value, associates it with the given value.
     * Otherwise, replaces the value with the results of the given
     * remapping function, or removes if {@code null}. The entire
     * method invocation is performed atomically.  Some attempted
     * update operations on this map by other threads may be blocked
     * while computation is in progress, so the computation should be
     * short and simple, and must not attempt to update any other
     * mappings of this Map.
     *
     * @param key               key with which the specified value is to be associated
     * @param value             the value to use if absent
     * @param remappingFunction the function to recompute a value if present
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException if the specified key or the
     *                              remappingFunction is null
     * @throws RuntimeException     or Error if the remappingFunction does so,
     *                              in which case the mapping is unchanged
     */
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (key == null || value == null || remappingFunction == null)
            throw new NullPointerException();
        int h = spread(key.hashCode());
        V val = null;
        int delta = 0;
        int binCount = 0;
        for (java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab = table; ; ) {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> f;
            int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & h)) == null) {
                if (casTabAt(tab, i, null, new java.util.concurrent.ConcurrentHashMap.Node<K, V>(h, key, value, null))) {
                    delta = 1;
                    val = value;
                    break;
                }
            } else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> e = f, pred = null; ; ++binCount) {
                                K ek;
                                if (e.hash == h &&
                                        ((ek = e.key) == key ||
                                                (ek != null && key.equals(ek)))) {
                                    val = remappingFunction.apply(e.val, value);
                                    if (val != null)
                                        e.val = val;
                                    else {
                                        delta = -1;
                                        java.util.concurrent.ConcurrentHashMap.Node<K, V> en = e.next;
                                        if (pred != null)
                                            pred.next = en;
                                        else
                                            setTabAt(tab, i, en);
                                    }
                                    break;
                                }
                                pred = e;
                                if ((e = e.next) == null) {
                                    delta = 1;
                                    val = value;
                                    pred.next =
                                            new java.util.concurrent.ConcurrentHashMap.Node<K, V>(h, key, val, null);
                                    break;
                                }
                            }
                        } else if (f instanceof java.util.concurrent.ConcurrentHashMap.TreeBin) {
                            binCount = 2;
                            java.util.concurrent.ConcurrentHashMap.TreeBin<K, V> t = (java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>) f;
                            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r = t.root;
                            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> p = (r == null) ? null :
                                    r.findTreeNode(h, key, null);
                            val = (p == null) ? value :
                                    remappingFunction.apply(p.val, value);
                            if (val != null) {
                                if (p != null)
                                    p.val = val;
                                else {
                                    delta = 1;
                                    t.putTreeVal(h, key, val);
                                }
                            } else if (p != null) {
                                delta = -1;
                                if (t.removeTreeNode(p))
                                    setTabAt(tab, i, untreeify(t.first));
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    break;
                }
            }
        }
        if (delta != 0)
            addCount((long) delta, binCount);
        return val;
    }

    // Hashtable legacy methods

    /**
     * Legacy method testing if some key maps into the specified value
     * in this table.  This method is identical in functionality to
     * {@link #containsValue(Object)}, and exists solely to ensure
     * full compatibility with class {@link java.util.Hashtable},
     * which supported this method prior to introduction of the
     * Java Collections framework.
     *
     * @param value a value to search for
     * @return {@code true} if and only if some key maps to the
     * {@code value} argument in this table as
     * determined by the {@code equals} method;
     * {@code false} otherwise
     * @throws NullPointerException if the specified value is null
     */
    public boolean contains(Object value) {
        return containsValue(value);
    }

    /**
     * Returns an enumeration of the keys in this table.
     *
     * @return an enumeration of the keys in this table
     * @see #keySet()
     */
    public Enumeration<K> keys() {
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
        int f = (t = table) == null ? 0 : t.length;
        return new java.util.concurrent.ConcurrentHashMap.KeyIterator<K, V>(t, f, 0, f, this);
    }

    /**
     * Returns an enumeration of the values in this table.
     *
     * @return an enumeration of the values in this table
     * @see #values()
     */
    public Enumeration<V> elements() {
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
        int f = (t = table) == null ? 0 : t.length;
        return new java.util.concurrent.ConcurrentHashMap.ValueIterator<K, V>(t, f, 0, f, this);
    }

    // ConcurrentHashMap-only methods

    /**
     * Returns the number of mappings. This method should be used
     * instead of {@link #size} because a ConcurrentHashMap may
     * contain more mappings than can be represented as an int. The
     * value returned is an estimate; the actual count may differ if
     * there are concurrent insertions or removals.
     *
     * @return the number of mappings
     * @since 1.8
     */
    public long mappingCount() {
        long n = sumCount();
        return (n < 0L) ? 0L : n; // ignore transient negative values
    }

    /**
     * Creates a new {@link Set} backed by a ConcurrentHashMap
     * from the given type to {@code Boolean.TRUE}.
     *
     * @param <K> the element type of the returned set
     * @return the new set
     * @since 1.8
     */
    public static <K> java.util.concurrent.ConcurrentHashMap.KeySetView<K, Boolean> newKeySet() {
        return new java.util.concurrent.ConcurrentHashMap.KeySetView<K, Boolean>
                (new java.util.concurrent.ConcurrentHashMap<K, Boolean>(), Boolean.TRUE);
    }

    /**
     * Creates a new {@link Set} backed by a ConcurrentHashMap
     * from the given type to {@code Boolean.TRUE}.
     *
     * @param initialCapacity The implementation performs internal
     *                        sizing to accommodate this many elements.
     * @param <K>             the element type of the returned set
     * @return the new set
     * @throws IllegalArgumentException if the initial capacity of
     *                                  elements is negative
     * @since 1.8
     */
    public static <K> java.util.concurrent.ConcurrentHashMap.KeySetView<K, Boolean> newKeySet(int initialCapacity) {
        return new java.util.concurrent.ConcurrentHashMap.KeySetView<K, Boolean>
                (new java.util.concurrent.ConcurrentHashMap<K, Boolean>(initialCapacity), Boolean.TRUE);
    }

    /**
     * Returns a {@link Set} view of the keys in this map, using the
     * given common mapped value for any additions (i.e., {@link
     * Collection#add} and {@link Collection#addAll(Collection)}).
     * This is of course only appropriate if it is acceptable to use
     * the same value for all additions from this view.
     *
     * @param mappedValue the mapped value to use for any additions
     * @return the set view
     * @throws NullPointerException if the mappedValue is null
     */
    public java.util.concurrent.ConcurrentHashMap.KeySetView<K, V> keySet(V mappedValue) {
        if (mappedValue == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.KeySetView<K, V>(this, mappedValue);
    }

    /* ---------------- Special Nodes -------------- */

    /**
     * A node inserted at head of bins during transfer operations.
     */
    static final class ForwardingNode<K, V> extends java.util.concurrent.ConcurrentHashMap.Node<K, V> {
        final java.util.concurrent.ConcurrentHashMap.Node<K, V>[] nextTable;

        ForwardingNode(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab) {
            super(MOVED, null, null, null);
            this.nextTable = tab;
        }

        java.util.concurrent.ConcurrentHashMap.Node<K, V> find(int h, Object k) {
            // loop to avoid arbitrarily deep recursion on forwarding nodes
            outer:
            for (java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab = nextTable; ; ) {
                java.util.concurrent.ConcurrentHashMap.Node<K, V> e;
                int n;
                if (k == null || tab == null || (n = tab.length) == 0 ||
                        (e = tabAt(tab, (n - 1) & h)) == null)
                    return null;
                for (; ; ) {
                    int eh;
                    K ek;
                    if ((eh = e.hash) == h &&
                            ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                    if (eh < 0) {
                        if (e instanceof java.util.concurrent.ConcurrentHashMap.ForwardingNode) {
                            tab = ((java.util.concurrent.ConcurrentHashMap.ForwardingNode<K, V>) e).nextTable;
                            continue outer;
                        } else
                            return e.find(h, k);
                    }
                    if ((e = e.next) == null)
                        return null;
                }
            }
        }
    }

    /**
     * A place-holder node used in computeIfAbsent and compute
     */
    static final class ReservationNode<K, V> extends java.util.concurrent.ConcurrentHashMap.Node<K, V> {
        ReservationNode() {
            super(RESERVED, null, null, null);
        }

        java.util.concurrent.ConcurrentHashMap.Node<K, V> find(int h, Object k) {
            return null;
        }
    }

    /* ---------------- Table Initialization and Resizing -------------- */

    /**
     * Returns the stamp bits for resizing a table of size n.
     * Must be negative when shifted left by RESIZE_STAMP_SHIFT.
     */
    static final int resizeStamp(int n) {
        return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
    }

    // 使用sizeCtl中记录的大小初始化表。
    // 里面需要注意的是变量 sizeCtl ，它的值决定着当前的初始化状态。
    // -1 说明正在初始化
    // -N 说明有N-1个线程正在进行扩容
    // 表示 table 初始化大小，如果 table 没有初始化
    // 表示 table 容量，如果 table　已经初始化。
    private final java.util.concurrent.ConcurrentHashMap.Node<K, V>[] initTable() {
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab;
        int sc;
        while ((tab = table) == null || tab.length == 0) {
            // 如果 sizeCtl < 0 ,说明另外的线程执行CAS 成功，正在进行初始化。
            if ((sc = sizeCtl) < 0)
                // 让出 CPU 使用权
                Thread.yield();
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        // 初始化哈希表大小
                                java.util.concurrent.ConcurrentHashMap.Node<K, V>[] nt = (java.util.concurrent.ConcurrentHashMap.Node<K, V>[]) new java.util.concurrent.ConcurrentHashMap.Node<?, ?>[n];
                        // 成员变量
                        table = tab = nt;
                        // 下次扩容的大小.
                        sc = n - (n >>> 2);
                    }
                } finally {
                    // 表初始化和调整大小控件
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }

    //更新size，检测扩容
    private final void addCount(long x, int check) {
        java.util.concurrent.ConcurrentHashMap.CounterCell[] as;
        long b, s;
        if ((as = counterCells) != null || !U.compareAndSwapLong(this, BASECOUNT, b = baseCount, s = b + x)) {
            java.util.concurrent.ConcurrentHashMap.CounterCell a;
            long v;
            int m;
            boolean uncontended = true;
            if (as == null || (m = as.length - 1) < 0 ||
                    (a = as[ThreadLocalRandom.getProbe() & m]) == null ||
                    !(uncontended =
                            U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))) {
                fullAddCount(x, uncontended);
                return;
            }
            if (check <= 1)
                return;
            s = sumCount();
        }
        if (check >= 0) {
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, nt;
            int n, sc;
            while (s >= (long) (sc = sizeCtl) && (tab = table) != null &&
                    (n = tab.length) < MAXIMUM_CAPACITY) {
                int rs = resizeStamp(n);
                if (sc < 0) {
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                            sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                            transferIndex <= 0)
                        break;
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                        transfer(tab, nt);
                } else if (U.compareAndSwapInt(this, SIZECTL, sc,
                        (rs << RESIZE_STAMP_SHIFT) + 2))
                    transfer(tab, null);
                s = sumCount();
            }
        }
    }

    // 如果正在进行调整大小，则有助于传输
    final java.util.concurrent.ConcurrentHashMap.Node<K, V>[] helpTransfer(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, java.util.concurrent.ConcurrentHashMap.Node<K, V> f) {
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] nextTab;
        int sc;
        if (tab != null && (f instanceof java.util.concurrent.ConcurrentHashMap.ForwardingNode) &&
                (nextTab = ((java.util.concurrent.ConcurrentHashMap.ForwardingNode<K, V>) f).nextTable) != null) {
            int rs = resizeStamp(tab.length);
            while (nextTab == nextTable && table == tab &&
                    (sc = sizeCtl) < 0) {
                if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                        sc == rs + MAX_RESIZERS || transferIndex <= 0)
                    break;
                if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                    transfer(tab, nextTab);
                    break;
                }
            }
            return nextTab;
        }
        return table;
    }

    // 尝试调整表的大小以容纳给定数量的元素。
    private final void tryPresize(int size) {
        // 判断当前大小是否大于最大容量
        int c = (size >= (MAXIMUM_CAPACITY >>> 1)) ? MAXIMUM_CAPACITY :
                // 扩容
                tableSizeFor(size + (size >>> 1) + 1);
        int sc;
        while ((sc = sizeCtl) >= 0) {
            // 获取当前哈希表
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab = table;
            int n;
            // 当前哈希表为null 或者表中元素数量为0
            if (tab == null || (n = tab.length) == 0) {
                // 获取可以使用的大小
                n = (sc > c) ? sc : c;
                // 判断当前对象在内存中大小是否等sc等于则进行重新赋值，反之不做改变(CAS)
                if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                    try {
                        if (table == tab) {
                            @SuppressWarnings("unchecked")
                            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] nt = (java.util.concurrent.ConcurrentHashMap.Node<K, V>[]) new java.util.concurrent.ConcurrentHashMap.Node<?, ?>[n];
                            table = nt;
                            sc = n - (n >>> 2);
                        }
                    } finally {
                        sizeCtl = sc;
                    }
                }
                // 如果使用的大小小于当前哈希表大小时
            } else if (c <= sc || n >= MAXIMUM_CAPACITY)
                // 结束
                break;
            else if (tab == table) {
                int rs = resizeStamp(n);
                if (sc < 0) {
                    java.util.concurrent.ConcurrentHashMap.Node<K, V>[] nt;
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                            sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                            transferIndex <= 0)
                        break;
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                        transfer(tab, nt);
                } else if (U.compareAndSwapInt(this, SIZECTL, sc,
                        (rs << RESIZE_STAMP_SHIFT) + 2))
                    transfer(tab, null);
            }
        }
    }

    /**
     * Moves and/or copies the nodes in each bin to new table. See
     * above for explanation.
     */
    private final void transfer(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] nextTab) {
        int n = tab.length, stride;
        if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
            stride = MIN_TRANSFER_STRIDE; // subdivide range
        if (nextTab == null) {            // initiating
            try {
                @SuppressWarnings("unchecked")
                java.util.concurrent.ConcurrentHashMap.Node<K, V>[] nt = (java.util.concurrent.ConcurrentHashMap.Node<K, V>[]) new java.util.concurrent.ConcurrentHashMap.Node<?, ?>[n << 1];
                nextTab = nt;
            } catch (Throwable ex) {      // try to cope with OOME
                sizeCtl = Integer.MAX_VALUE;
                return;
            }
            nextTable = nextTab;
            transferIndex = n;
        }
        int nextn = nextTab.length;
        java.util.concurrent.ConcurrentHashMap.ForwardingNode<K, V> fwd = new java.util.concurrent.ConcurrentHashMap.ForwardingNode<K, V>(nextTab);
        boolean advance = true;
        boolean finishing = false; // to ensure sweep before committing nextTab
        for (int i = 0, bound = 0; ; ) {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> f;
            int fh;
            while (advance) {
                int nextIndex, nextBound;
                if (--i >= bound || finishing)
                    advance = false;
                else if ((nextIndex = transferIndex) <= 0) {
                    i = -1;
                    advance = false;
                } else if (U.compareAndSwapInt
                        (this, TRANSFERINDEX, nextIndex,
                                nextBound = (nextIndex > stride ?
                                        nextIndex - stride : 0))) {
                    bound = nextBound;
                    i = nextIndex - 1;
                    advance = false;
                }
            }
            if (i < 0 || i >= n || i + n >= nextn) {
                int sc;
                if (finishing) {
                    nextTable = null;
                    table = nextTab;
                    sizeCtl = (n << 1) - (n >>> 1);
                    return;
                }
                if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                    if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                        return;
                    finishing = advance = true;
                    i = n; // recheck before commit
                }
            } else if ((f = tabAt(tab, i)) == null)
                advance = casTabAt(tab, i, null, fwd);
            else if ((fh = f.hash) == MOVED)
                advance = true; // already processed
            else {
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        java.util.concurrent.ConcurrentHashMap.Node<K, V> ln, hn;
                        if (fh >= 0) {
                            int runBit = fh & n;
                            java.util.concurrent.ConcurrentHashMap.Node<K, V> lastRun = f;
                            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p = f.next; p != null; p = p.next) {
                                int b = p.hash & n;
                                if (b != runBit) {
                                    runBit = b;
                                    lastRun = p;
                                }
                            }
                            if (runBit == 0) {
                                ln = lastRun;
                                hn = null;
                            } else {
                                hn = lastRun;
                                ln = null;
                            }
                            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p = f; p != lastRun; p = p.next) {
                                int ph = p.hash;
                                K pk = p.key;
                                V pv = p.val;
                                if ((ph & n) == 0)
                                    ln = new java.util.concurrent.ConcurrentHashMap.Node<K, V>(ph, pk, pv, ln);
                                else
                                    hn = new java.util.concurrent.ConcurrentHashMap.Node<K, V>(ph, pk, pv, hn);
                            }
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            setTabAt(tab, i, fwd);
                            advance = true;
                        } else if (f instanceof java.util.concurrent.ConcurrentHashMap.TreeBin) {
                            java.util.concurrent.ConcurrentHashMap.TreeBin<K, V> t = (java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>) f;
                            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> lo = null, loTail = null;
                            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> hi = null, hiTail = null;
                            int lc = 0, hc = 0;
                            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> e = t.first; e != null; e = e.next) {
                                int h = e.hash;
                                java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> p = new java.util.concurrent.ConcurrentHashMap.TreeNode<K, V>
                                        (h, e.key, e.val, null, null);
                                if ((h & n) == 0) {
                                    if ((p.prev = loTail) == null)
                                        lo = p;
                                    else
                                        loTail.next = p;
                                    loTail = p;
                                    ++lc;
                                } else {
                                    if ((p.prev = hiTail) == null)
                                        hi = p;
                                    else
                                        hiTail.next = p;
                                    hiTail = p;
                                    ++hc;
                                }
                            }
                            ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                                    (hc != 0) ? new java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>(lo) : t;
                            hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                                    (lc != 0) ? new java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>(hi) : t;
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            setTabAt(tab, i, fwd);
                            advance = true;
                        }
                    }
                }
            }
        }
    }

    /* ---------------- Counter support -------------- */

    /**
     * A padded cell for distributing counts.  Adapted from LongAdder
     * and Striped64.  See their internal docs for explanation.
     */
    @sun.misc.Contended
    static final class CounterCell {
        volatile long value;

        CounterCell(long x) {
            value = x;
        }
    }

    final long sumCount() {
        java.util.concurrent.ConcurrentHashMap.CounterCell[] as = counterCells;
        java.util.concurrent.ConcurrentHashMap.CounterCell a;
        long sum = baseCount;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                if ((a = as[i]) != null)
                    sum += a.value;
            }
        }
        return sum;
    }

    // See LongAdder version for explanation
    private final void fullAddCount(long x, boolean wasUncontended) {
        int h;
        if ((h = ThreadLocalRandom.getProbe()) == 0) {
            ThreadLocalRandom.localInit();      // force initialization
            h = ThreadLocalRandom.getProbe();
            wasUncontended = true;
        }
        boolean collide = false;                // True if last slot nonempty
        for (; ; ) {
            java.util.concurrent.ConcurrentHashMap.CounterCell[] as;
            java.util.concurrent.ConcurrentHashMap.CounterCell a;
            int n;
            long v;
            if ((as = counterCells) != null && (n = as.length) > 0) {
                if ((a = as[(n - 1) & h]) == null) {
                    if (cellsBusy == 0) {            // Try to attach new Cell
                        java.util.concurrent.ConcurrentHashMap.CounterCell r = new java.util.concurrent.ConcurrentHashMap.CounterCell(x); // Optimistic create
                        if (cellsBusy == 0 &&
                                U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                            boolean created = false;
                            try {               // Recheck under lock
                                java.util.concurrent.ConcurrentHashMap.CounterCell[] rs;
                                int m, j;
                                if ((rs = counterCells) != null &&
                                        (m = rs.length) > 0 &&
                                        rs[j = (m - 1) & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            } finally {
                                cellsBusy = 0;
                            }
                            if (created)
                                break;
                            continue;           // Slot is now non-empty
                        }
                    }
                    collide = false;
                } else if (!wasUncontended)       // CAS already known to fail
                    wasUncontended = true;      // Continue after rehash
                else if (U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))
                    break;
                else if (counterCells != as || n >= NCPU)
                    collide = false;            // At max size or stale
                else if (!collide)
                    collide = true;
                else if (cellsBusy == 0 &&
                        U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                    try {
                        if (counterCells == as) {// Expand table unless stale
                            java.util.concurrent.ConcurrentHashMap.CounterCell[] rs = new java.util.concurrent.ConcurrentHashMap.CounterCell[n << 1];
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            counterCells = rs;
                        }
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    continue;                   // Retry with expanded table
                }
                h = ThreadLocalRandom.advanceProbe(h);
            } else if (cellsBusy == 0 && counterCells == as &&
                    U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                boolean init = false;
                try {                           // Initialize table
                    if (counterCells == as) {
                        java.util.concurrent.ConcurrentHashMap.CounterCell[] rs = new java.util.concurrent.ConcurrentHashMap.CounterCell[2];
                        rs[h & 1] = new java.util.concurrent.ConcurrentHashMap.CounterCell(x);
                        counterCells = rs;
                        init = true;
                    }
                } finally {
                    cellsBusy = 0;
                }
                if (init)
                    break;
            } else if (U.compareAndSwapLong(this, BASECOUNT, v = baseCount, v + x))
                break;                          // Fall back on using base
        }
    }

    /* ---------------- Conversion from/to TreeBins -------------- */

    /**
     * Replaces all linked nodes in bin at given index unless table is
     * too small, in which case resizes instead.
     */
    private final void treeifyBin(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, int index) {
        java.util.concurrent.ConcurrentHashMap.Node<K, V> b;
        int n, sc;
        if (tab != null) {
            if ((n = tab.length) < MIN_TREEIFY_CAPACITY)
                tryPresize(n << 1);
            else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
                synchronized (b) {
                    if (tabAt(tab, index) == b) {
                        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> hd = null, tl = null;
                        for (java.util.concurrent.ConcurrentHashMap.Node<K, V> e = b; e != null; e = e.next) {
                            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> p =
                                    new java.util.concurrent.ConcurrentHashMap.TreeNode<K, V>(e.hash, e.key, e.val,
                                            null, null);
                            if ((p.prev = tl) == null)
                                hd = p;
                            else
                                tl.next = p;
                            tl = p;
                        }
                        setTabAt(tab, index, new java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>(hd));
                    }
                }
            }
        }
    }

    /**
     * Returns a list on non-TreeNodes replacing those in given list.
     */
    static <K, V> java.util.concurrent.ConcurrentHashMap.Node<K, V> untreeify(java.util.concurrent.ConcurrentHashMap.Node<K, V> b) {
        java.util.concurrent.ConcurrentHashMap.Node<K, V> hd = null, tl = null;
        for (java.util.concurrent.ConcurrentHashMap.Node<K, V> q = b; q != null; q = q.next) {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> p = new java.util.concurrent.ConcurrentHashMap.Node<K, V>(q.hash, q.key, q.val, null);
            if (tl == null)
                hd = p;
            else
                tl.next = p;
            tl = p;
        }
        return hd;
    }

    /* ---------------- TreeNodes -------------- */

    /**
     * Nodes for use in TreeBins
     */
    static final class TreeNode<K, V> extends java.util.concurrent.ConcurrentHashMap.Node<K, V> {
        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> parent;  // red-black tree links
        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> left;
        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> right;
        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> prev;    // needed to unlink next upon deletion
        boolean red;

        TreeNode(int hash, K key, V val, java.util.concurrent.ConcurrentHashMap.Node<K, V> next,
                 java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> parent) {
            super(hash, key, val, next);
            this.parent = parent;
        }

        java.util.concurrent.ConcurrentHashMap.Node<K, V> find(int h, Object k) {
            return findTreeNode(h, k, null);
        }

        /**
         * Returns the TreeNode (or null if not found) for the given key
         * starting at given root.
         */
        final java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> findTreeNode(int h, Object k, Class<?> kc) {
            if (k != null) {
                java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> p = this;
                do {
                    int ph, dir;
                    K pk;
                    java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> q;
                    java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> pl = p.left, pr = p.right;
                    if ((ph = p.hash) > h)
                        p = pl;
                    else if (ph < h)
                        p = pr;
                    else if ((pk = p.key) == k || (pk != null && k.equals(pk)))
                        return p;
                    else if (pl == null)
                        p = pr;
                    else if (pr == null)
                        p = pl;
                    else if ((kc != null ||
                            (kc = comparableClassFor(k)) != null) &&
                            (dir = compareComparables(kc, k, pk)) != 0)
                        p = (dir < 0) ? pl : pr;
                    else if ((q = pr.findTreeNode(h, k, kc)) != null)
                        return q;
                    else
                        p = pl;
                } while (p != null);
            }
            return null;
        }
    }

    /* ---------------- TreeBins -------------- */

    /**
     * TreeNodes used at the heads of bins. TreeBins do not hold user
     * keys or values, but instead point to list of TreeNodes and
     * their root. They also maintain a parasitic read-write lock
     * forcing writers (who hold bin lock) to wait for readers (who do
     * not) to complete before tree restructuring operations.
     */
    static final class TreeBin<K, V> extends java.util.concurrent.ConcurrentHashMap.Node<K, V> {
        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> root;
        volatile java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> first;
        volatile Thread waiter;
        volatile int lockState;
        // values for lockState
        static final int WRITER = 1; // set while holding write lock
        static final int WAITER = 2; // set when waiting for write lock
        static final int READER = 4; // increment value for setting read lock

        /**
         * Tie-breaking utility for ordering insertions when equal
         * hashCodes and non-comparable. We don't require a total
         * order, just a consistent insertion rule to maintain
         * equivalence across rebalancings. Tie-breaking further than
         * necessary simplifies testing a bit.
         */
        static int tieBreakOrder(Object a, Object b) {
            int d;
            if (a == null || b == null ||
                    (d = a.getClass().getName().
                            compareTo(b.getClass().getName())) == 0)
                d = (System.identityHashCode(a) <= System.identityHashCode(b) ?
                        -1 : 1);
            return d;
        }

        /**
         * Creates bin with initial set of nodes headed by b.
         */
        TreeBin(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> b) {
            super(TREEBIN, null, null, null);
            this.first = b;
            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r = null;
            for (java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> x = b, next; x != null; x = next) {
                next = (java.util.concurrent.ConcurrentHashMap.TreeNode<K, V>) x.next;
                x.left = x.right = null;
                if (r == null) {
                    x.parent = null;
                    x.red = false;
                    r = x;
                } else {
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    for (java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> p = r; ; ) {
                        int dir, ph;
                        K pk = p.key;
                        if ((ph = p.hash) > h)
                            dir = -1;
                        else if (ph < h)
                            dir = 1;
                        else if ((kc == null &&
                                (kc = comparableClassFor(k)) == null) ||
                                (dir = compareComparables(kc, k, pk)) == 0)
                            dir = tieBreakOrder(k, pk);
                        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0)
                                xp.left = x;
                            else
                                xp.right = x;
                            r = balanceInsertion(r, x);
                            break;
                        }
                    }
                }
            }
            this.root = r;
            assert checkInvariants(root);
        }

        /**
         * Acquires write lock for tree restructuring.
         */
        private final void lockRoot() {
            if (!U.compareAndSwapInt(this, LOCKSTATE, 0, WRITER))
                contendedLock(); // offload to separate method
        }

        /**
         * Releases write lock for tree restructuring.
         */
        private final void unlockRoot() {
            lockState = 0;
        }

        /**
         * Possibly blocks awaiting root lock.
         */
        private final void contendedLock() {
            boolean waiting = false;
            for (int s; ; ) {
                if (((s = lockState) & ~WAITER) == 0) {
                    if (U.compareAndSwapInt(this, LOCKSTATE, s, WRITER)) {
                        if (waiting)
                            waiter = null;
                        return;
                    }
                } else if ((s & WAITER) == 0) {
                    if (U.compareAndSwapInt(this, LOCKSTATE, s, s | WAITER)) {
                        waiting = true;
                        waiter = Thread.currentThread();
                    }
                } else if (waiting)
                    LockSupport.park(this);
            }
        }

        /**
         * Returns matching node or null if none. Tries to search
         * using tree comparisons from root, but continues linear
         * search when lock not available.
         */
        final java.util.concurrent.ConcurrentHashMap.Node<K, V> find(int h, Object k) {
            if (k != null) {
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> e = first; e != null; ) {
                    int s;
                    K ek;
                    if (((s = lockState) & (WAITER | WRITER)) != 0) {
                        if (e.hash == h &&
                                ((ek = e.key) == k || (ek != null && k.equals(ek))))
                            return e;
                        e = e.next;
                    } else if (U.compareAndSwapInt(this, LOCKSTATE, s,
                            s + READER)) {
                        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r, p;
                        try {
                            p = ((r = root) == null ? null :
                                    r.findTreeNode(h, k, null));
                        } finally {
                            Thread w;
                            if (U.getAndAddInt(this, LOCKSTATE, -READER) ==
                                    (READER | WAITER) && (w = waiter) != null)
                                LockSupport.unpark(w);
                        }
                        return p;
                    }
                }
            }
            return null;
        }

        /**
         * Finds or adds a node.
         *
         * @return null if added
         */
        final java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> putTreeVal(int h, K k, V v) {
            Class<?> kc = null;
            boolean searched = false;
            for (java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> p = root; ; ) {
                int dir, ph;
                K pk;
                if (p == null) {
                    first = root = new java.util.concurrent.ConcurrentHashMap.TreeNode<K, V>(h, k, v, null, null);
                    break;
                } else if ((ph = p.hash) > h)
                    dir = -1;
                else if (ph < h)
                    dir = 1;
                else if ((pk = p.key) == k || (pk != null && k.equals(pk)))
                    return p;
                else if ((kc == null &&
                        (kc = comparableClassFor(k)) == null) ||
                        (dir = compareComparables(kc, k, pk)) == 0) {
                    if (!searched) {
                        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> q, ch;
                        searched = true;
                        if (((ch = p.left) != null &&
                                (q = ch.findTreeNode(h, k, kc)) != null) ||
                                ((ch = p.right) != null &&
                                        (q = ch.findTreeNode(h, k, kc)) != null))
                            return q;
                    }
                    dir = tieBreakOrder(k, pk);
                }

                java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> xp = p;
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> x, f = first;
                    first = x = new java.util.concurrent.ConcurrentHashMap.TreeNode<K, V>(h, k, v, f, xp);
                    if (f != null)
                        f.prev = x;
                    if (dir <= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    if (!xp.red)
                        x.red = true;
                    else {
                        lockRoot();
                        try {
                            root = balanceInsertion(root, x);
                        } finally {
                            unlockRoot();
                        }
                    }
                    break;
                }
            }
            assert checkInvariants(root);
            return null;
        }

        /**
         * Removes the given node, that must be present before this
         * call.  This is messier than typical red-black deletion code
         * because we cannot swap the contents of an interior node
         * with a leaf successor that is pinned by "next" pointers
         * that are accessible independently of lock. So instead we
         * swap the tree linkages.
         *
         * @return true if now too small, so should be untreeified
         */
        final boolean removeTreeNode(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> p) {
            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> next = (java.util.concurrent.ConcurrentHashMap.TreeNode<K, V>) p.next;
            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> pred = p.prev;  // unlink traversal pointers
            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r, rl;
            if (pred == null)
                first = next;
            else
                pred.next = next;
            if (next != null)
                next.prev = pred;
            if (first == null) {
                root = null;
                return true;
            }
            if ((r = root) == null || r.right == null || // too small
                    (rl = r.left) == null || rl.left == null)
                return true;
            lockRoot();
            try {
                java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> replacement;
                java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> pl = p.left;
                java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> pr = p.right;
                if (pl != null && pr != null) {
                    java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> s = pr, sl;
                    while ((sl = s.left) != null) // find successor
                        s = sl;
                    boolean c = s.red;
                    s.red = p.red;
                    p.red = c; // swap colors
                    java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> sr = s.right;
                    java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> pp = p.parent;
                    if (s == pr) { // p was s's direct parent
                        p.parent = s;
                        s.right = p;
                    } else {
                        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> sp = s.parent;
                        if ((p.parent = sp) != null) {
                            if (s == sp.left)
                                sp.left = p;
                            else
                                sp.right = p;
                        }
                        if ((s.right = pr) != null)
                            pr.parent = s;
                    }
                    p.left = null;
                    if ((p.right = sr) != null)
                        sr.parent = p;
                    if ((s.left = pl) != null)
                        pl.parent = s;
                    if ((s.parent = pp) == null)
                        r = s;
                    else if (p == pp.left)
                        pp.left = s;
                    else
                        pp.right = s;
                    if (sr != null)
                        replacement = sr;
                    else
                        replacement = p;
                } else if (pl != null)
                    replacement = pl;
                else if (pr != null)
                    replacement = pr;
                else
                    replacement = p;
                if (replacement != p) {
                    java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> pp = replacement.parent = p.parent;
                    if (pp == null)
                        r = replacement;
                    else if (p == pp.left)
                        pp.left = replacement;
                    else
                        pp.right = replacement;
                    p.left = p.right = p.parent = null;
                }

                root = (p.red) ? r : balanceDeletion(r, replacement);

                if (p == replacement) {  // detach pointers
                    java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> pp;
                    if ((pp = p.parent) != null) {
                        if (p == pp.left)
                            pp.left = null;
                        else if (p == pp.right)
                            pp.right = null;
                        p.parent = null;
                    }
                }
            } finally {
                unlockRoot();
            }
            assert checkInvariants(root);
            return false;
        }

        /* ------------------------------------------------------------ */
        // Red-black tree methods, all adapted from CLR

        static <K, V> java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> rotateLeft(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> root,
                                                                                       java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> p) {
            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r, pp, rl;
            if (p != null && (r = p.right) != null) {
                if ((rl = p.right = r.left) != null)
                    rl.parent = p;
                if ((pp = r.parent = p.parent) == null)
                    (root = r).red = false;
                else if (pp.left == p)
                    pp.left = r;
                else
                    pp.right = r;
                r.left = p;
                p.parent = r;
            }
            return root;
        }

        static <K, V> java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> rotateRight(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> root,
                                                                                        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> p) {
            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> l, pp, lr;
            if (p != null && (l = p.left) != null) {
                if ((lr = p.left = l.right) != null)
                    lr.parent = p;
                if ((pp = l.parent = p.parent) == null)
                    (root = l).red = false;
                else if (pp.right == p)
                    pp.right = l;
                else
                    pp.left = l;
                l.right = p;
                p.parent = l;
            }
            return root;
        }

        static <K, V> java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> balanceInsertion(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> root,
                                                                                             java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> x) {
            x.red = true;
            for (java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> xp, xpp, xppl, xppr; ; ) {
                if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                } else if (!xp.red || (xpp = xp.parent) == null)
                    return root;
                if (xp == (xppl = xpp.left)) {
                    if ((xppr = xpp.right) != null && xppr.red) {
                        xppr.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    } else {
                        if (x == xp.right) {
                            root = rotateLeft(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateRight(root, xpp);
                            }
                        }
                    }
                } else {
                    if (xppl != null && xppl.red) {
                        xppl.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    } else {
                        if (x == xp.left) {
                            root = rotateRight(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateLeft(root, xpp);
                            }
                        }
                    }
                }
            }
        }

        static <K, V> java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> balanceDeletion(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> root,
                                                                                            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> x) {
            for (java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> xp, xpl, xpr; ; ) {
                if (x == null || x == root)
                    return root;
                else if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                } else if (x.red) {
                    x.red = false;
                    return root;
                } else if ((xpl = xp.left) == x) {
                    if ((xpr = xp.right) != null && xpr.red) {
                        xpr.red = false;
                        xp.red = true;
                        root = rotateLeft(root, xp);
                        xpr = (xp = x.parent) == null ? null : xp.right;
                    }
                    if (xpr == null)
                        x = xp;
                    else {
                        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> sl = xpr.left, sr = xpr.right;
                        if ((sr == null || !sr.red) &&
                                (sl == null || !sl.red)) {
                            xpr.red = true;
                            x = xp;
                        } else {
                            if (sr == null || !sr.red) {
                                if (sl != null)
                                    sl.red = false;
                                xpr.red = true;
                                root = rotateRight(root, xpr);
                                xpr = (xp = x.parent) == null ?
                                        null : xp.right;
                            }
                            if (xpr != null) {
                                xpr.red = (xp == null) ? false : xp.red;
                                if ((sr = xpr.right) != null)
                                    sr.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateLeft(root, xp);
                            }
                            x = root;
                        }
                    }
                } else { // symmetric
                    if (xpl != null && xpl.red) {
                        xpl.red = false;
                        xp.red = true;
                        root = rotateRight(root, xp);
                        xpl = (xp = x.parent) == null ? null : xp.left;
                    }
                    if (xpl == null)
                        x = xp;
                    else {
                        java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> sl = xpl.left, sr = xpl.right;
                        if ((sl == null || !sl.red) &&
                                (sr == null || !sr.red)) {
                            xpl.red = true;
                            x = xp;
                        } else {
                            if (sl == null || !sl.red) {
                                if (sr != null)
                                    sr.red = false;
                                xpl.red = true;
                                root = rotateLeft(root, xpl);
                                xpl = (xp = x.parent) == null ?
                                        null : xp.left;
                            }
                            if (xpl != null) {
                                xpl.red = (xp == null) ? false : xp.red;
                                if ((sl = xpl.left) != null)
                                    sl.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateRight(root, xp);
                            }
                            x = root;
                        }
                    }
                }
            }
        }

        /**
         * Recursive invariant check
         */
        static <K, V> boolean checkInvariants(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> t) {
            java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> tp = t.parent, tl = t.left, tr = t.right,
                    tb = t.prev, tn = (java.util.concurrent.ConcurrentHashMap.TreeNode<K, V>) t.next;
            if (tb != null && tb.next != t)
                return false;
            if (tn != null && tn.prev != t)
                return false;
            if (tp != null && t != tp.left && t != tp.right)
                return false;
            if (tl != null && (tl.parent != t || tl.hash > t.hash))
                return false;
            if (tr != null && (tr.parent != t || tr.hash < t.hash))
                return false;
            if (t.red && tl != null && tl.red && tr != null && tr.red)
                return false;
            if (tl != null && !checkInvariants(tl))
                return false;
            if (tr != null && !checkInvariants(tr))
                return false;
            return true;
        }

        private static final sun.misc.Unsafe U;
        private static final long LOCKSTATE;

        static {
            try {
                U = sun.misc.Unsafe.getUnsafe();
                Class<?> k = java.util.concurrent.ConcurrentHashMap.TreeBin.class;
                LOCKSTATE = U.objectFieldOffset
                        (k.getDeclaredField("lockState"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    /* ----------------Table Traversal -------------- */

    /**
     * Records the table, its length, and current traversal index for a
     * traverser that must process a region of a forwarded table before
     * proceeding with current table.
     */
    static final class TableStack<K, V> {
        int length;
        int index;
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab;
        java.util.concurrent.ConcurrentHashMap.TableStack<K, V> next;
    }

    /**
     * Encapsulates traversal for methods such as containsValue; also
     * serves as a base class for other iterators and spliterators.
     * <p>
     * Method advance visits once each still-valid node that was
     * reachable upon iterator construction. It might miss some that
     * were added to a bin after the bin was visited, which is OK wrt
     * consistency guarantees. Maintaining this property in the face
     * of possible ongoing resizes requires a fair amount of
     * bookkeeping state that is difficult to optimize away amidst
     * volatile accesses.  Even so, traversal maintains reasonable
     * throughput.
     * <p>
     * Normally, iteration proceeds bin-by-bin traversing lists.
     * However, if the table has been resized, then all future steps
     * must traverse both the bin at the current index as well as at
     * (index + baseSize); and so on for further resizings. To
     * paranoically cope with potential sharing by users of iterators
     * across threads, iteration terminates if a bounds checks fails
     * for a table read.
     */
    static class Traverser<K, V> {
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab;        // current table; updated if resized
        java.util.concurrent.ConcurrentHashMap.Node<K, V> next;         // the next entry to use
        java.util.concurrent.ConcurrentHashMap.TableStack<K, V> stack, spare; // to save/restore on ForwardingNodes
        int index;              // index of bin to use next
        int baseIndex;          // current index of initial table
        int baseLimit;          // index bound for initial table
        final int baseSize;     // initial table size

        Traverser(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, int size, int index, int limit) {
            this.tab = tab;
            this.baseSize = size;
            this.baseIndex = this.index = index;
            this.baseLimit = limit;
            this.next = null;
        }

        /**
         * Advances if possible, returning next valid node, or null if none.
         */
        final java.util.concurrent.ConcurrentHashMap.Node<K, V> advance() {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> e;
            if ((e = next) != null)
                e = e.next;
            for (; ; ) {
                java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
                int i, n;  // must use locals in checks
                if (e != null)
                    return next = e;
                if (baseIndex >= baseLimit || (t = tab) == null ||
                        (n = t.length) <= (i = index) || i < 0)
                    return next = null;
                if ((e = tabAt(t, i)) != null && e.hash < 0) {
                    if (e instanceof java.util.concurrent.ConcurrentHashMap.ForwardingNode) {
                        tab = ((java.util.concurrent.ConcurrentHashMap.ForwardingNode<K, V>) e).nextTable;
                        e = null;
                        pushState(t, i, n);
                        continue;
                    } else if (e instanceof java.util.concurrent.ConcurrentHashMap.TreeBin)
                        e = ((java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>) e).first;
                    else
                        e = null;
                }
                if (stack != null)
                    recoverState(n);
                else if ((index = i + baseSize) >= n)
                    index = ++baseIndex; // visit upper slots if present
            }
        }

        /**
         * Saves traversal state upon encountering a forwarding node.
         */
        private void pushState(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t, int i, int n) {
            java.util.concurrent.ConcurrentHashMap.TableStack<K, V> s = spare;  // reuse if possible
            if (s != null)
                spare = s.next;
            else
                s = new java.util.concurrent.ConcurrentHashMap.TableStack<K, V>();
            s.tab = t;
            s.length = n;
            s.index = i;
            s.next = stack;
            stack = s;
        }

        /**
         * Possibly pops traversal state.
         *
         * @param n length of current table
         */
        private void recoverState(int n) {
            java.util.concurrent.ConcurrentHashMap.TableStack<K, V> s;
            int len;
            while ((s = stack) != null && (index += (len = s.length)) >= n) {
                n = len;
                index = s.index;
                tab = s.tab;
                s.tab = null;
                java.util.concurrent.ConcurrentHashMap.TableStack<K, V> next = s.next;
                s.next = spare; // save for reuse
                stack = next;
                spare = s;
            }
            if (s == null && (index += baseSize) >= n)
                index = ++baseIndex;
        }
    }

    /**
     * Base of key, value, and entry Iterators. Adds fields to
     * Traverser to support iterator.remove.
     */
    static class BaseIterator<K, V> extends java.util.concurrent.ConcurrentHashMap.Traverser<K, V> {
        final java.util.concurrent.ConcurrentHashMap<K, V> map;
        java.util.concurrent.ConcurrentHashMap.Node<K, V> lastReturned;

        BaseIterator(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, int size, int index, int limit,
                     java.util.concurrent.ConcurrentHashMap<K, V> map) {
            super(tab, size, index, limit);
            this.map = map;
            advance();
        }

        public final boolean hasNext() {
            return next != null;
        }

        public final boolean hasMoreElements() {
            return next != null;
        }

        public final void remove() {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
            if ((p = lastReturned) == null)
                throw new IllegalStateException();
            lastReturned = null;
            map.replaceNode(p.key, null, null);
        }
    }

    static final class KeyIterator<K, V> extends java.util.concurrent.ConcurrentHashMap.BaseIterator<K, V>
            implements Iterator<K>, Enumeration<K> {
        KeyIterator(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, int index, int size, int limit,
                    java.util.concurrent.ConcurrentHashMap<K, V> map) {
            super(tab, index, size, limit, map);
        }

        public final K next() {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
            if ((p = next) == null)
                throw new NoSuchElementException();
            K k = p.key;
            lastReturned = p;
            advance();
            return k;
        }

        public final K nextElement() {
            return next();
        }
    }

    static final class ValueIterator<K, V> extends java.util.concurrent.ConcurrentHashMap.BaseIterator<K, V>
            implements Iterator<V>, Enumeration<V> {
        ValueIterator(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, int index, int size, int limit,
                      java.util.concurrent.ConcurrentHashMap<K, V> map) {
            super(tab, index, size, limit, map);
        }

        public final V next() {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
            if ((p = next) == null)
                throw new NoSuchElementException();
            V v = p.val;
            lastReturned = p;
            advance();
            return v;
        }

        public final V nextElement() {
            return next();
        }
    }

    static final class EntryIterator<K, V> extends java.util.concurrent.ConcurrentHashMap.BaseIterator<K, V>
            implements Iterator<Map.Entry<K, V>> {
        EntryIterator(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, int index, int size, int limit,
                      java.util.concurrent.ConcurrentHashMap<K, V> map) {
            super(tab, index, size, limit, map);
        }

        public final Map.Entry<K, V> next() {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
            if ((p = next) == null)
                throw new NoSuchElementException();
            K k = p.key;
            V v = p.val;
            lastReturned = p;
            advance();
            return new java.util.concurrent.ConcurrentHashMap.MapEntry<K, V>(k, v, map);
        }
    }

    /**
     * Exported Entry for EntryIterator
     */
    static final class MapEntry<K, V> implements Map.Entry<K, V> {
        final K key; // non-null
        V val;       // non-null
        final java.util.concurrent.ConcurrentHashMap<K, V> map;

        MapEntry(K key, V val, java.util.concurrent.ConcurrentHashMap<K, V> map) {
            this.key = key;
            this.val = val;
            this.map = map;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return val;
        }

        public int hashCode() {
            return key.hashCode() ^ val.hashCode();
        }

        public String toString() {
            return key + "=" + val;
        }

        public boolean equals(Object o) {
            Object k, v;
            Map.Entry<?, ?> e;
            return ((o instanceof Map.Entry) &&
                    (k = (e = (Map.Entry<?, ?>) o).getKey()) != null &&
                    (v = e.getValue()) != null &&
                    (k == key || k.equals(key)) &&
                    (v == val || v.equals(val)));
        }

        /**
         * Sets our entry's value and writes through to the map. The
         * value to return is somewhat arbitrary here. Since we do not
         * necessarily track asynchronous changes, the most recent
         * "previous" value could be different from what we return (or
         * could even have been removed, in which case the put will
         * re-establish). We do not and cannot guarantee more.
         */
        public V setValue(V value) {
            if (value == null) throw new NullPointerException();
            V v = val;
            val = value;
            map.put(key, value);
            return v;
        }
    }

    static final class KeySpliterator<K, V> extends java.util.concurrent.ConcurrentHashMap.Traverser<K, V>
            implements Spliterator<K> {
        long est;               // size estimate

        KeySpliterator(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, int size, int index, int limit,
                       long est) {
            super(tab, size, index, limit);
            this.est = est;
        }

        public Spliterator<K> trySplit() {
            int i, f, h;
            return (h = ((i = baseIndex) + (f = baseLimit)) >>> 1) <= i ? null :
                    new java.util.concurrent.ConcurrentHashMap.KeySpliterator<K, V>(tab, baseSize, baseLimit = h,
                            f, est >>>= 1);
        }

        public void forEachRemaining(Consumer<? super K> action) {
            if (action == null) throw new NullPointerException();
            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                action.accept(p.key);
        }

        public boolean tryAdvance(Consumer<? super K> action) {
            if (action == null) throw new NullPointerException();
            java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
            if ((p = advance()) == null)
                return false;
            action.accept(p.key);
            return true;
        }

        public long estimateSize() {
            return est;
        }

        public int characteristics() {
            return Spliterator.DISTINCT | Spliterator.CONCURRENT |
                    Spliterator.NONNULL;
        }
    }

    static final class ValueSpliterator<K, V> extends java.util.concurrent.ConcurrentHashMap.Traverser<K, V>
            implements Spliterator<V> {
        long est;               // size estimate

        ValueSpliterator(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, int size, int index, int limit,
                         long est) {
            super(tab, size, index, limit);
            this.est = est;
        }

        public Spliterator<V> trySplit() {
            int i, f, h;
            return (h = ((i = baseIndex) + (f = baseLimit)) >>> 1) <= i ? null :
                    new java.util.concurrent.ConcurrentHashMap.ValueSpliterator<K, V>(tab, baseSize, baseLimit = h,
                            f, est >>>= 1);
        }

        public void forEachRemaining(Consumer<? super V> action) {
            if (action == null) throw new NullPointerException();
            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                action.accept(p.val);
        }

        public boolean tryAdvance(Consumer<? super V> action) {
            if (action == null) throw new NullPointerException();
            java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
            if ((p = advance()) == null)
                return false;
            action.accept(p.val);
            return true;
        }

        public long estimateSize() {
            return est;
        }

        public int characteristics() {
            return Spliterator.CONCURRENT | Spliterator.NONNULL;
        }
    }

    static final class EntrySpliterator<K, V> extends java.util.concurrent.ConcurrentHashMap.Traverser<K, V>
            implements Spliterator<Map.Entry<K, V>> {
        final java.util.concurrent.ConcurrentHashMap<K, V> map; // To export MapEntry
        long est;               // size estimate

        EntrySpliterator(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab, int size, int index, int limit,
                         long est, java.util.concurrent.ConcurrentHashMap<K, V> map) {
            super(tab, size, index, limit);
            this.map = map;
            this.est = est;
        }

        public Spliterator<Map.Entry<K, V>> trySplit() {
            int i, f, h;
            return (h = ((i = baseIndex) + (f = baseLimit)) >>> 1) <= i ? null :
                    new java.util.concurrent.ConcurrentHashMap.EntrySpliterator<K, V>(tab, baseSize, baseLimit = h,
                            f, est >>>= 1, map);
        }

        public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
            if (action == null) throw new NullPointerException();
            for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                action.accept(new java.util.concurrent.ConcurrentHashMap.MapEntry<K, V>(p.key, p.val, map));
        }

        public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> action) {
            if (action == null) throw new NullPointerException();
            java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
            if ((p = advance()) == null)
                return false;
            action.accept(new java.util.concurrent.ConcurrentHashMap.MapEntry<K, V>(p.key, p.val, map));
            return true;
        }

        public long estimateSize() {
            return est;
        }

        public int characteristics() {
            return Spliterator.DISTINCT | Spliterator.CONCURRENT |
                    Spliterator.NONNULL;
        }
    }

    // Parallel bulk operations

    /**
     * Computes initial batch value for bulk tasks. The returned value
     * is approximately exp2 of the number of times (minus one) to
     * split task by two before executing leaf action. This value is
     * faster to compute and more convenient to use as a guide to
     * splitting than is the depth, since it is used while dividing by
     * two anyway.
     */
    final int batchFor(long b) {
        long n;
        if (b == Long.MAX_VALUE || (n = sumCount()) <= 1L || n < b)
            return 0;
        int sp = ForkJoinPool.getCommonPoolParallelism() << 2; // slack of 4
        return (b <= 0L || (n /= b) >= sp) ? sp : (int) n;
    }

    /**
     * Performs the given action for each (key, value).
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param action               the action
     * @since 1.8
     */
    public void forEach(long parallelismThreshold,
                        BiConsumer<? super K, ? super V> action) {
        if (action == null) throw new NullPointerException();
        new java.util.concurrent.ConcurrentHashMap.ForEachMappingTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        action).invoke();
    }

    /**
     * Performs the given action for each non-null transformation
     * of each (key, value).
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element, or null if there is no transformation (in
     *                             which case the action is not applied)
     * @param action               the action
     * @param <U>                  the return type of the transformer
     * @since 1.8
     */
    public <U> void forEach(long parallelismThreshold,
                            BiFunction<? super K, ? super V, ? extends U> transformer,
                            Consumer<? super U> action) {
        if (transformer == null || action == null)
            throw new NullPointerException();
        new java.util.concurrent.ConcurrentHashMap.ForEachTransformedMappingTask<K, V, U>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        transformer, action).invoke();
    }

    /**
     * Returns a non-null result from applying the given search
     * function on each (key, value), or null if none.  Upon
     * success, further element processing is suppressed and the
     * results of any other parallel invocations of the search
     * function are ignored.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param searchFunction       a function returning a non-null
     *                             result on success, else null
     * @param <U>                  the return type of the search function
     * @return a non-null result from applying the given search
     * function on each (key, value), or null if none
     * @since 1.8
     */
    public <U> U search(long parallelismThreshold,
                        BiFunction<? super K, ? super V, ? extends U> searchFunction) {
        if (searchFunction == null) throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.SearchMappingsTask<K, V, U>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        searchFunction, new AtomicReference<U>()).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all (key, value) pairs using the given reducer to
     * combine values, or null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element, or null if there is no transformation (in
     *                             which case it is not combined)
     * @param reducer              a commutative associative combining function
     * @param <U>                  the return type of the transformer
     * @return the result of accumulating the given transformation
     * of all (key, value) pairs
     * @since 1.8
     */
    public <U> U reduce(long parallelismThreshold,
                        BiFunction<? super K, ? super V, ? extends U> transformer,
                        BiFunction<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceMappingsTask<K, V, U>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all (key, value) pairs using the given reducer to
     * combine values, and the given basis as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation
     * of all (key, value) pairs
     * @since 1.8
     */
    public double reduceToDouble(long parallelismThreshold,
                                 ToDoubleBiFunction<? super K, ? super V> transformer,
                                 double basis,
                                 DoubleBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToDoubleTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all (key, value) pairs using the given reducer to
     * combine values, and the given basis as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation
     * of all (key, value) pairs
     * @since 1.8
     */
    public long reduceToLong(long parallelismThreshold,
                             ToLongBiFunction<? super K, ? super V> transformer,
                             long basis,
                             LongBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToLongTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all (key, value) pairs using the given reducer to
     * combine values, and the given basis as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation
     * of all (key, value) pairs
     * @since 1.8
     */
    public int reduceToInt(long parallelismThreshold,
                           ToIntBiFunction<? super K, ? super V> transformer,
                           int basis,
                           IntBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToIntTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, basis, reducer).invoke();
    }

    /**
     * Performs the given action for each key.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param action               the action
     * @since 1.8
     */
    public void forEachKey(long parallelismThreshold,
                           Consumer<? super K> action) {
        if (action == null) throw new NullPointerException();
        new java.util.concurrent.ConcurrentHashMap.ForEachKeyTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        action).invoke();
    }

    /**
     * Performs the given action for each non-null transformation
     * of each key.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element, or null if there is no transformation (in
     *                             which case the action is not applied)
     * @param action               the action
     * @param <U>                  the return type of the transformer
     * @since 1.8
     */
    public <U> void forEachKey(long parallelismThreshold,
                               Function<? super K, ? extends U> transformer,
                               Consumer<? super U> action) {
        if (transformer == null || action == null)
            throw new NullPointerException();
        new java.util.concurrent.ConcurrentHashMap.ForEachTransformedKeyTask<K, V, U>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        transformer, action).invoke();
    }

    /**
     * Returns a non-null result from applying the given search
     * function on each key, or null if none. Upon success,
     * further element processing is suppressed and the results of
     * any other parallel invocations of the search function are
     * ignored.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param searchFunction       a function returning a non-null
     *                             result on success, else null
     * @param <U>                  the return type of the search function
     * @return a non-null result from applying the given search
     * function on each key, or null if none
     * @since 1.8
     */
    public <U> U searchKeys(long parallelismThreshold,
                            Function<? super K, ? extends U> searchFunction) {
        if (searchFunction == null) throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.SearchKeysTask<K, V, U>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        searchFunction, new AtomicReference<U>()).invoke();
    }

    /**
     * Returns the result of accumulating all keys using the given
     * reducer to combine values, or null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating all keys using the given
     * reducer to combine values, or null if none
     * @since 1.8
     */
    public K reduceKeys(long parallelismThreshold,
                        BiFunction<? super K, ? super K, ? extends K> reducer) {
        if (reducer == null) throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.ReduceKeysTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all keys using the given reducer to combine values, or
     * null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element, or null if there is no transformation (in
     *                             which case it is not combined)
     * @param reducer              a commutative associative combining function
     * @param <U>                  the return type of the transformer
     * @return the result of accumulating the given transformation
     * of all keys
     * @since 1.8
     */
    public <U> U reduceKeys(long parallelismThreshold,
                            Function<? super K, ? extends U> transformer,
                            BiFunction<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceKeysTask<K, V, U>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all keys using the given reducer to combine values, and
     * the given basis as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation
     * of all keys
     * @since 1.8
     */
    public double reduceKeysToDouble(long parallelismThreshold,
                                     ToDoubleFunction<? super K> transformer,
                                     double basis,
                                     DoubleBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceKeysToDoubleTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all keys using the given reducer to combine values, and
     * the given basis as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation
     * of all keys
     * @since 1.8
     */
    public long reduceKeysToLong(long parallelismThreshold,
                                 ToLongFunction<? super K> transformer,
                                 long basis,
                                 LongBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceKeysToLongTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all keys using the given reducer to combine values, and
     * the given basis as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation
     * of all keys
     * @since 1.8
     */
    public int reduceKeysToInt(long parallelismThreshold,
                               ToIntFunction<? super K> transformer,
                               int basis,
                               IntBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceKeysToIntTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, basis, reducer).invoke();
    }

    /**
     * Performs the given action for each value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param action               the action
     * @since 1.8
     */
    public void forEachValue(long parallelismThreshold,
                             Consumer<? super V> action) {
        if (action == null)
            throw new NullPointerException();
        new java.util.concurrent.ConcurrentHashMap.ForEachValueTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        action).invoke();
    }

    /**
     * Performs the given action for each non-null transformation
     * of each value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element, or null if there is no transformation (in
     *                             which case the action is not applied)
     * @param action               the action
     * @param <U>                  the return type of the transformer
     * @since 1.8
     */
    public <U> void forEachValue(long parallelismThreshold,
                                 Function<? super V, ? extends U> transformer,
                                 Consumer<? super U> action) {
        if (transformer == null || action == null)
            throw new NullPointerException();
        new java.util.concurrent.ConcurrentHashMap.ForEachTransformedValueTask<K, V, U>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        transformer, action).invoke();
    }

    /**
     * Returns a non-null result from applying the given search
     * function on each value, or null if none.  Upon success,
     * further element processing is suppressed and the results of
     * any other parallel invocations of the search function are
     * ignored.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param searchFunction       a function returning a non-null
     *                             result on success, else null
     * @param <U>                  the return type of the search function
     * @return a non-null result from applying the given search
     * function on each value, or null if none
     * @since 1.8
     */
    public <U> U searchValues(long parallelismThreshold,
                              Function<? super V, ? extends U> searchFunction) {
        if (searchFunction == null) throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.SearchValuesTask<K, V, U>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        searchFunction, new AtomicReference<U>()).invoke();
    }

    /**
     * Returns the result of accumulating all values using the
     * given reducer to combine values, or null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating all values
     * @since 1.8
     */
    public V reduceValues(long parallelismThreshold,
                          BiFunction<? super V, ? super V, ? extends V> reducer) {
        if (reducer == null) throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.ReduceValuesTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all values using the given reducer to combine values, or
     * null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element, or null if there is no transformation (in
     *                             which case it is not combined)
     * @param reducer              a commutative associative combining function
     * @param <U>                  the return type of the transformer
     * @return the result of accumulating the given transformation
     * of all values
     * @since 1.8
     */
    public <U> U reduceValues(long parallelismThreshold,
                              Function<? super V, ? extends U> transformer,
                              BiFunction<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceValuesTask<K, V, U>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all values using the given reducer to combine values,
     * and the given basis as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation
     * of all values
     * @since 1.8
     */
    public double reduceValuesToDouble(long parallelismThreshold,
                                       ToDoubleFunction<? super V> transformer,
                                       double basis,
                                       DoubleBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceValuesToDoubleTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all values using the given reducer to combine values,
     * and the given basis as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation
     * of all values
     * @since 1.8
     */
    public long reduceValuesToLong(long parallelismThreshold,
                                   ToLongFunction<? super V> transformer,
                                   long basis,
                                   LongBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceValuesToLongTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all values using the given reducer to combine values,
     * and the given basis as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation
     * of all values
     * @since 1.8
     */
    public int reduceValuesToInt(long parallelismThreshold,
                                 ToIntFunction<? super V> transformer,
                                 int basis,
                                 IntBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceValuesToIntTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, basis, reducer).invoke();
    }

    /**
     * Performs the given action for each entry.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param action               the action
     * @since 1.8
     */
    public void forEachEntry(long parallelismThreshold,
                             Consumer<? super Map.Entry<K, V>> action) {
        if (action == null) throw new NullPointerException();
        new java.util.concurrent.ConcurrentHashMap.ForEachEntryTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table,
                action).invoke();
    }

    /**
     * Performs the given action for each non-null transformation
     * of each entry.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element, or null if there is no transformation (in
     *                             which case the action is not applied)
     * @param action               the action
     * @param <U>                  the return type of the transformer
     * @since 1.8
     */
    public <U> void forEachEntry(long parallelismThreshold,
                                 Function<Map.Entry<K, V>, ? extends U> transformer,
                                 Consumer<? super U> action) {
        if (transformer == null || action == null)
            throw new NullPointerException();
        new java.util.concurrent.ConcurrentHashMap.ForEachTransformedEntryTask<K, V, U>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        transformer, action).invoke();
    }

    /**
     * Returns a non-null result from applying the given search
     * function on each entry, or null if none.  Upon success,
     * further element processing is suppressed and the results of
     * any other parallel invocations of the search function are
     * ignored.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param searchFunction       a function returning a non-null
     *                             result on success, else null
     * @param <U>                  the return type of the search function
     * @return a non-null result from applying the given search
     * function on each entry, or null if none
     * @since 1.8
     */
    public <U> U searchEntries(long parallelismThreshold,
                               Function<Map.Entry<K, V>, ? extends U> searchFunction) {
        if (searchFunction == null) throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.SearchEntriesTask<K, V, U>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        searchFunction, new AtomicReference<U>()).invoke();
    }

    /**
     * Returns the result of accumulating all entries using the
     * given reducer to combine values, or null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating all entries
     * @since 1.8
     */
    public Map.Entry<K, V> reduceEntries(long parallelismThreshold,
                                         BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer) {
        if (reducer == null) throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.ReduceEntriesTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all entries using the given reducer to combine values,
     * or null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element, or null if there is no transformation (in
     *                             which case it is not combined)
     * @param reducer              a commutative associative combining function
     * @param <U>                  the return type of the transformer
     * @return the result of accumulating the given transformation
     * of all entries
     * @since 1.8
     */
    public <U> U reduceEntries(long parallelismThreshold,
                               Function<Map.Entry<K, V>, ? extends U> transformer,
                               BiFunction<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceEntriesTask<K, V, U>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all entries using the given reducer to combine values,
     * and the given basis as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation
     * of all entries
     * @since 1.8
     */
    public double reduceEntriesToDouble(long parallelismThreshold,
                                        ToDoubleFunction<Map.Entry<K, V>> transformer,
                                        double basis,
                                        DoubleBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToDoubleTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all entries using the given reducer to combine values,
     * and the given basis as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation
     * of all entries
     * @since 1.8
     */
    public long reduceEntriesToLong(long parallelismThreshold,
                                    ToLongFunction<Map.Entry<K, V>> transformer,
                                    long basis,
                                    LongBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToLongTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation
     * of all entries using the given reducer to combine values,
     * and the given basis as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements
     *                             needed for this operation to be executed in parallel
     * @param transformer          a function returning the transformation
     *                             for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation
     * of all entries
     * @since 1.8
     */
    public int reduceEntriesToInt(long parallelismThreshold,
                                  ToIntFunction<Map.Entry<K, V>> transformer,
                                  int basis,
                                  IntBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToIntTask<K, V>
                (null, batchFor(parallelismThreshold), 0, 0, table,
                        null, transformer, basis, reducer).invoke();
    }


    /* ----------------Views -------------- */

    /**
     * Base class for views.
     */
    abstract static class CollectionView<K, V, E>
            implements Collection<E>, java.io.Serializable {
        private static final long serialVersionUID = 7249069246763182397L;
        final java.util.concurrent.ConcurrentHashMap<K, V> map;

        CollectionView(java.util.concurrent.ConcurrentHashMap<K, V> map) {
            this.map = map;
        }

        /**
         * Returns the map backing this view.
         *
         * @return the map backing this view
         */
        public java.util.concurrent.ConcurrentHashMap<K, V> getMap() {
            return map;
        }

        /**
         * Removes all of the elements from this view, by removing all
         * the mappings from the map backing this view.
         */
        public final void clear() {
            map.clear();
        }

        public final int size() {
            return map.size();
        }

        public final boolean isEmpty() {
            return map.isEmpty();
        }

        // implementations below rely on concrete classes supplying these
        // abstract methods

        /**
         * Returns an iterator over the elements in this collection.
         *
         * <p>The returned iterator is
         * <a href="package-summary.html#Weakly"><i>weakly consistent</i></a>.
         *
         * @return an iterator over the elements in this collection
         */
        public abstract Iterator<E> iterator();

        public abstract boolean contains(Object o);

        public abstract boolean remove(Object o);

        private static final String oomeMsg = "Required array size too large";

        public final Object[] toArray() {
            long sz = map.mappingCount();
            if (sz > MAX_ARRAY_SIZE)
                throw new OutOfMemoryError(oomeMsg);
            int n = (int) sz;
            Object[] r = new Object[n];
            int i = 0;
            for (E e : this) {
                if (i == n) {
                    if (n >= MAX_ARRAY_SIZE)
                        throw new OutOfMemoryError(oomeMsg);
                    if (n >= MAX_ARRAY_SIZE - (MAX_ARRAY_SIZE >>> 1) - 1)
                        n = MAX_ARRAY_SIZE;
                    else
                        n += (n >>> 1) + 1;
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = e;
            }
            return (i == n) ? r : Arrays.copyOf(r, i);
        }

        @SuppressWarnings("unchecked")
        public final <T> T[] toArray(T[] a) {
            long sz = map.mappingCount();
            if (sz > MAX_ARRAY_SIZE)
                throw new OutOfMemoryError(oomeMsg);
            int m = (int) sz;
            T[] r = (a.length >= m) ? a :
                    (T[]) java.lang.reflect.Array
                            .newInstance(a.getClass().getComponentType(), m);
            int n = r.length;
            int i = 0;
            for (E e : this) {
                if (i == n) {
                    if (n >= MAX_ARRAY_SIZE)
                        throw new OutOfMemoryError(oomeMsg);
                    if (n >= MAX_ARRAY_SIZE - (MAX_ARRAY_SIZE >>> 1) - 1)
                        n = MAX_ARRAY_SIZE;
                    else
                        n += (n >>> 1) + 1;
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = (T) e;
            }
            if (a == r && i < n) {
                r[i] = null; // null-terminate
                return r;
            }
            return (i == n) ? r : Arrays.copyOf(r, i);
        }

        /**
         * Returns a string representation of this collection.
         * The string representation consists of the string representations
         * of the collection's elements in the order they are returned by
         * its iterator, enclosed in square brackets ({@code "[]"}).
         * Adjacent elements are separated by the characters {@code ", "}
         * (comma and space).  Elements are converted to strings as by
         * {@link String#valueOf(Object)}.
         *
         * @return a string representation of this collection
         */
        public final String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            Iterator<E> it = iterator();
            if (it.hasNext()) {
                for (; ; ) {
                    Object e = it.next();
                    sb.append(e == this ? "(this Collection)" : e);
                    if (!it.hasNext())
                        break;
                    sb.append(',').append(' ');
                }
            }
            return sb.append(']').toString();
        }

        public final boolean containsAll(Collection<?> c) {
            if (c != this) {
                for (Object e : c) {
                    if (e == null || !contains(e))
                        return false;
                }
            }
            return true;
        }

        public final boolean removeAll(Collection<?> c) {
            if (c == null) throw new NullPointerException();
            boolean modified = false;
            for (Iterator<E> it = iterator(); it.hasNext(); ) {
                if (c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

        public final boolean retainAll(Collection<?> c) {
            if (c == null) throw new NullPointerException();
            boolean modified = false;
            for (Iterator<E> it = iterator(); it.hasNext(); ) {
                if (!c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

    }

    /**
     * A view of a ConcurrentHashMap as a {@link Set} of keys, in
     * which additions may optionally be enabled by mapping to a
     * common value.  This class cannot be directly instantiated.
     * See {@link #keySet() keySet()},
     * {@link #keySet(Object) keySet(V)},
     * {@link #newKeySet() newKeySet()},
     * {@link #newKeySet(int) newKeySet(int)}.
     *
     * @since 1.8
     */
    public static class KeySetView<K, V> extends java.util.concurrent.ConcurrentHashMap.CollectionView<K, V, K>
            implements Set<K>, java.io.Serializable {
        private static final long serialVersionUID = 7249069246763182397L;
        private final V value;

        KeySetView(java.util.concurrent.ConcurrentHashMap<K, V> map, V value) {  // non-public
            super(map);
            this.value = value;
        }

        /**
         * Returns the default mapped value for additions,
         * or {@code null} if additions are not supported.
         *
         * @return the default mapped value for additions, or {@code null}
         * if not supported
         */
        public V getMappedValue() {
            return value;
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if the specified key is null
         */
        public boolean contains(Object o) {
            return map.containsKey(o);
        }

        /**
         * Removes the key from this map view, by removing the key (and its
         * corresponding value) from the backing map.  This method does
         * nothing if the key is not in the map.
         *
         * @param o the key to be removed from the backing map
         * @return {@code true} if the backing map contained the specified key
         * @throws NullPointerException if the specified key is null
         */
        public boolean remove(Object o) {
            return map.remove(o) != null;
        }

        /**
         * @return an iterator over the keys of the backing map
         */
        public Iterator<K> iterator() {
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
            java.util.concurrent.ConcurrentHashMap<K, V> m = map;
            int f = (t = m.table) == null ? 0 : t.length;
            return new java.util.concurrent.ConcurrentHashMap.KeyIterator<K, V>(t, f, 0, f, m);
        }

        /**
         * Adds the specified key to this set view by mapping the key to
         * the default mapped value in the backing map, if defined.
         *
         * @param e key to be added
         * @return {@code true} if this set changed as a result of the call
         * @throws NullPointerException          if the specified key is null
         * @throws UnsupportedOperationException if no default mapped value
         *                                       for additions was provided
         */
        public boolean add(K e) {
            V v;
            if ((v = value) == null)
                throw new UnsupportedOperationException();
            return map.putVal(e, v, true) == null;
        }

        /**
         * Adds all of the elements in the specified collection to this set,
         * as if by calling {@link #add} on each one.
         *
         * @param c the elements to be inserted into this set
         * @return {@code true} if this set changed as a result of the call
         * @throws NullPointerException          if the collection or any of its
         *                                       elements are {@code null}
         * @throws UnsupportedOperationException if no default mapped value
         *                                       for additions was provided
         */
        public boolean addAll(Collection<? extends K> c) {
            boolean added = false;
            V v;
            if ((v = value) == null)
                throw new UnsupportedOperationException();
            for (K e : c) {
                if (map.putVal(e, v, true) == null)
                    added = true;
            }
            return added;
        }

        public int hashCode() {
            int h = 0;
            for (K e : this)
                h += e.hashCode();
            return h;
        }

        public boolean equals(Object o) {
            Set<?> c;
            return ((o instanceof Set) &&
                    ((c = (Set<?>) o) == this ||
                            (containsAll(c) && c.containsAll(this))));
        }

        public Spliterator<K> spliterator() {
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
            java.util.concurrent.ConcurrentHashMap<K, V> m = map;
            long n = m.sumCount();
            int f = (t = m.table) == null ? 0 : t.length;
            return new java.util.concurrent.ConcurrentHashMap.KeySpliterator<K, V>(t, f, 0, f, n < 0L ? 0L : n);
        }

        public void forEach(Consumer<? super K> action) {
            if (action == null) throw new NullPointerException();
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
            if ((t = map.table) != null) {
                java.util.concurrent.ConcurrentHashMap.Traverser<K, V> it = new java.util.concurrent.ConcurrentHashMap.Traverser<K, V>(t, t.length, 0, t.length);
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = it.advance()) != null; )
                    action.accept(p.key);
            }
        }
    }

    /**
     * A view of a ConcurrentHashMap as a {@link Collection} of
     * values, in which additions are disabled. This class cannot be
     * directly instantiated. See {@link #values()}.
     */
    static final class ValuesView<K, V> extends java.util.concurrent.ConcurrentHashMap.CollectionView<K, V, V>
            implements Collection<V>, java.io.Serializable {
        private static final long serialVersionUID = 2249069246763182397L;

        ValuesView(java.util.concurrent.ConcurrentHashMap<K, V> map) {
            super(map);
        }

        public final boolean contains(Object o) {
            return map.containsValue(o);
        }

        public final boolean remove(Object o) {
            if (o != null) {
                for (Iterator<V> it = iterator(); it.hasNext(); ) {
                    if (o.equals(it.next())) {
                        it.remove();
                        return true;
                    }
                }
            }
            return false;
        }

        public final Iterator<V> iterator() {
            java.util.concurrent.ConcurrentHashMap<K, V> m = map;
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
            int f = (t = m.table) == null ? 0 : t.length;
            return new java.util.concurrent.ConcurrentHashMap.ValueIterator<K, V>(t, f, 0, f, m);
        }

        public final boolean add(V e) {
            throw new UnsupportedOperationException();
        }

        public final boolean addAll(Collection<? extends V> c) {
            throw new UnsupportedOperationException();
        }

        public Spliterator<V> spliterator() {
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
            java.util.concurrent.ConcurrentHashMap<K, V> m = map;
            long n = m.sumCount();
            int f = (t = m.table) == null ? 0 : t.length;
            return new java.util.concurrent.ConcurrentHashMap.ValueSpliterator<K, V>(t, f, 0, f, n < 0L ? 0L : n);
        }

        public void forEach(Consumer<? super V> action) {
            if (action == null) throw new NullPointerException();
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
            if ((t = map.table) != null) {
                java.util.concurrent.ConcurrentHashMap.Traverser<K, V> it = new java.util.concurrent.ConcurrentHashMap.Traverser<K, V>(t, t.length, 0, t.length);
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = it.advance()) != null; )
                    action.accept(p.val);
            }
        }
    }

    /**
     * A view of a ConcurrentHashMap as a {@link Set} of (key, value)
     * entries.  This class cannot be directly instantiated. See
     * {@link #entrySet()}.
     */
    static final class EntrySetView<K, V> extends java.util.concurrent.ConcurrentHashMap.CollectionView<K, V, Entry<K, V>>
            implements Set<Map.Entry<K, V>>, java.io.Serializable {
        private static final long serialVersionUID = 2249069246763182397L;

        EntrySetView(java.util.concurrent.ConcurrentHashMap<K, V> map) {
            super(map);
        }

        public boolean contains(Object o) {
            Object k, v, r;
            Map.Entry<?, ?> e;
            return ((o instanceof Map.Entry) &&
                    (k = (e = (Map.Entry<?, ?>) o).getKey()) != null &&
                    (r = map.get(k)) != null &&
                    (v = e.getValue()) != null &&
                    (v == r || v.equals(r)));
        }

        public boolean remove(Object o) {
            Object k, v;
            Map.Entry<?, ?> e;
            return ((o instanceof Map.Entry) &&
                    (k = (e = (Map.Entry<?, ?>) o).getKey()) != null &&
                    (v = e.getValue()) != null &&
                    map.remove(k, v));
        }

        /**
         * @return an iterator over the entries of the backing map
         */
        public Iterator<Map.Entry<K, V>> iterator() {
            java.util.concurrent.ConcurrentHashMap<K, V> m = map;
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
            int f = (t = m.table) == null ? 0 : t.length;
            return new java.util.concurrent.ConcurrentHashMap.EntryIterator<K, V>(t, f, 0, f, m);
        }

        public boolean add(Entry<K, V> e) {
            return map.putVal(e.getKey(), e.getValue(), false) == null;
        }

        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            boolean added = false;
            for (Entry<K, V> e : c) {
                if (add(e))
                    added = true;
            }
            return added;
        }

        public final int hashCode() {
            int h = 0;
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
            if ((t = map.table) != null) {
                java.util.concurrent.ConcurrentHashMap.Traverser<K, V> it = new java.util.concurrent.ConcurrentHashMap.Traverser<K, V>(t, t.length, 0, t.length);
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = it.advance()) != null; ) {
                    h += p.hashCode();
                }
            }
            return h;
        }

        public final boolean equals(Object o) {
            Set<?> c;
            return ((o instanceof Set) &&
                    ((c = (Set<?>) o) == this ||
                            (containsAll(c) && c.containsAll(this))));
        }

        public Spliterator<Map.Entry<K, V>> spliterator() {
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
            java.util.concurrent.ConcurrentHashMap<K, V> m = map;
            long n = m.sumCount();
            int f = (t = m.table) == null ? 0 : t.length;
            return new java.util.concurrent.ConcurrentHashMap.EntrySpliterator<K, V>(t, f, 0, f, n < 0L ? 0L : n, m);
        }

        public void forEach(Consumer<? super Map.Entry<K, V>> action) {
            if (action == null) throw new NullPointerException();
            java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
            if ((t = map.table) != null) {
                java.util.concurrent.ConcurrentHashMap.Traverser<K, V> it = new java.util.concurrent.ConcurrentHashMap.Traverser<K, V>(t, t.length, 0, t.length);
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = it.advance()) != null; )
                    action.accept(new java.util.concurrent.ConcurrentHashMap.MapEntry<K, V>(p.key, p.val, map));
            }
        }

    }

    // -------------------------------------------------------

    /**
     * Base class for bulk tasks. Repeats some fields and code from
     * class Traverser, because we need to subclass CountedCompleter.
     */
    @SuppressWarnings("serial")
    abstract static class BulkTask<K, V, R> extends CountedCompleter<R> {
        java.util.concurrent.ConcurrentHashMap.Node<K, V>[] tab;        // same as Traverser
        java.util.concurrent.ConcurrentHashMap.Node<K, V> next;
        java.util.concurrent.ConcurrentHashMap.TableStack<K, V> stack, spare;
        int index;
        int baseIndex;
        int baseLimit;
        final int baseSize;
        int batch;              // split control

        BulkTask(java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> par, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t) {
            super(par);
            this.batch = b;
            this.index = this.baseIndex = i;
            if ((this.tab = t) == null)
                this.baseSize = this.baseLimit = 0;
            else if (par == null)
                this.baseSize = this.baseLimit = t.length;
            else {
                this.baseLimit = f;
                this.baseSize = par.baseSize;
            }
        }

        /**
         * Same as Traverser version
         */
        final java.util.concurrent.ConcurrentHashMap.Node<K, V> advance() {
            java.util.concurrent.ConcurrentHashMap.Node<K, V> e;
            if ((e = next) != null)
                e = e.next;
            for (; ; ) {
                java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t;
                int i, n;
                if (e != null)
                    return next = e;
                if (baseIndex >= baseLimit || (t = tab) == null ||
                        (n = t.length) <= (i = index) || i < 0)
                    return next = null;
                if ((e = tabAt(t, i)) != null && e.hash < 0) {
                    if (e instanceof java.util.concurrent.ConcurrentHashMap.ForwardingNode) {
                        tab = ((java.util.concurrent.ConcurrentHashMap.ForwardingNode<K, V>) e).nextTable;
                        e = null;
                        pushState(t, i, n);
                        continue;
                    } else if (e instanceof java.util.concurrent.ConcurrentHashMap.TreeBin)
                        e = ((java.util.concurrent.ConcurrentHashMap.TreeBin<K, V>) e).first;
                    else
                        e = null;
                }
                if (stack != null)
                    recoverState(n);
                else if ((index = i + baseSize) >= n)
                    index = ++baseIndex;
            }
        }

        private void pushState(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t, int i, int n) {
            java.util.concurrent.ConcurrentHashMap.TableStack<K, V> s = spare;
            if (s != null)
                spare = s.next;
            else
                s = new java.util.concurrent.ConcurrentHashMap.TableStack<K, V>();
            s.tab = t;
            s.length = n;
            s.index = i;
            s.next = stack;
            stack = s;
        }

        private void recoverState(int n) {
            java.util.concurrent.ConcurrentHashMap.TableStack<K, V> s;
            int len;
            while ((s = stack) != null && (index += (len = s.length)) >= n) {
                n = len;
                index = s.index;
                tab = s.tab;
                s.tab = null;
                java.util.concurrent.ConcurrentHashMap.TableStack<K, V> next = s.next;
                s.next = spare; // save for reuse
                stack = next;
                spare = s;
            }
            if (s == null && (index += baseSize) >= n)
                index = ++baseIndex;
        }
    }

    /*
     * Task classes. Coded in a regular but ugly format/style to
     * simplify checks that each variant differs in the right way from
     * others. The null screenings exist because compilers cannot tell
     * that we've already null-checked task arguments, so we force
     * simplest hoisted bypass to help avoid convoluted traps.
     */
    @SuppressWarnings("serial")
    static final class ForEachKeyTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Void> {
        final Consumer<? super K> action;

        ForEachKeyTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 Consumer<? super K> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        public final void compute() {
            final Consumer<? super K> action;
            if ((action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new java.util.concurrent.ConcurrentHashMap.ForEachKeyTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    action).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    action.accept(p.key);
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachValueTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Void> {
        final Consumer<? super V> action;

        ForEachValueTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 Consumer<? super V> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        public final void compute() {
            final Consumer<? super V> action;
            if ((action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new java.util.concurrent.ConcurrentHashMap.ForEachValueTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    action).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    action.accept(p.val);
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachEntryTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Void> {
        final Consumer<? super Entry<K, V>> action;

        ForEachEntryTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 Consumer<? super Entry<K, V>> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        public final void compute() {
            final Consumer<? super Entry<K, V>> action;
            if ((action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new java.util.concurrent.ConcurrentHashMap.ForEachEntryTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    action).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    action.accept(p);
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachMappingTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Void> {
        final BiConsumer<? super K, ? super V> action;

        ForEachMappingTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 BiConsumer<? super K, ? super V> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        public final void compute() {
            final BiConsumer<? super K, ? super V> action;
            if ((action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new java.util.concurrent.ConcurrentHashMap.ForEachMappingTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    action).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    action.accept(p.key, p.val);
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachTransformedKeyTask<K, V, U>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Void> {
        final Function<? super K, ? extends U> transformer;
        final Consumer<? super U> action;

        ForEachTransformedKeyTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 Function<? super K, ? extends U> transformer, Consumer<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        public final void compute() {
            final Function<? super K, ? extends U> transformer;
            final Consumer<? super U> action;
            if ((transformer = this.transformer) != null &&
                    (action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new java.util.concurrent.ConcurrentHashMap.ForEachTransformedKeyTask<K, V, U>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    transformer, action).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p.key)) != null)
                        action.accept(u);
                }
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachTransformedValueTask<K, V, U>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Void> {
        final Function<? super V, ? extends U> transformer;
        final Consumer<? super U> action;

        ForEachTransformedValueTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 Function<? super V, ? extends U> transformer, Consumer<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        public final void compute() {
            final Function<? super V, ? extends U> transformer;
            final Consumer<? super U> action;
            if ((transformer = this.transformer) != null &&
                    (action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new java.util.concurrent.ConcurrentHashMap.ForEachTransformedValueTask<K, V, U>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    transformer, action).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p.val)) != null)
                        action.accept(u);
                }
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachTransformedEntryTask<K, V, U>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Void> {
        final Function<Map.Entry<K, V>, ? extends U> transformer;
        final Consumer<? super U> action;

        ForEachTransformedEntryTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 Function<Map.Entry<K, V>, ? extends U> transformer, Consumer<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        public final void compute() {
            final Function<Map.Entry<K, V>, ? extends U> transformer;
            final Consumer<? super U> action;
            if ((transformer = this.transformer) != null &&
                    (action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new java.util.concurrent.ConcurrentHashMap.ForEachTransformedEntryTask<K, V, U>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    transformer, action).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p)) != null)
                        action.accept(u);
                }
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachTransformedMappingTask<K, V, U>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Void> {
        final BiFunction<? super K, ? super V, ? extends U> transformer;
        final Consumer<? super U> action;

        ForEachTransformedMappingTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 BiFunction<? super K, ? super V, ? extends U> transformer,
                 Consumer<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        public final void compute() {
            final BiFunction<? super K, ? super V, ? extends U> transformer;
            final Consumer<? super U> action;
            if ((transformer = this.transformer) != null &&
                    (action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new java.util.concurrent.ConcurrentHashMap.ForEachTransformedMappingTask<K, V, U>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    transformer, action).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p.key, p.val)) != null)
                        action.accept(u);
                }
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class SearchKeysTask<K, V, U>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, U> {
        final Function<? super K, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchKeysTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 Function<? super K, ? extends U> searchFunction,
                 AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        public final U getRawResult() {
            return result.get();
        }

        public final void compute() {
            final Function<? super K, ? extends U> searchFunction;
            final AtomicReference<U> result;
            if ((searchFunction = this.searchFunction) != null &&
                    (result = this.result) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    if (result.get() != null)
                        return;
                    addToPendingCount(1);
                    new java.util.concurrent.ConcurrentHashMap.SearchKeysTask<K, V, U>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    searchFunction, result).fork();
                }
                while (result.get() == null) {
                    U u;
                    java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
                    if ((p = advance()) == null) {
                        propagateCompletion();
                        break;
                    }
                    if ((u = searchFunction.apply(p.key)) != null) {
                        if (result.compareAndSet(null, u))
                            quietlyCompleteRoot();
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class SearchValuesTask<K, V, U>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, U> {
        final Function<? super V, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchValuesTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 Function<? super V, ? extends U> searchFunction,
                 AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        public final U getRawResult() {
            return result.get();
        }

        public final void compute() {
            final Function<? super V, ? extends U> searchFunction;
            final AtomicReference<U> result;
            if ((searchFunction = this.searchFunction) != null &&
                    (result = this.result) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    if (result.get() != null)
                        return;
                    addToPendingCount(1);
                    new java.util.concurrent.ConcurrentHashMap.SearchValuesTask<K, V, U>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    searchFunction, result).fork();
                }
                while (result.get() == null) {
                    U u;
                    java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
                    if ((p = advance()) == null) {
                        propagateCompletion();
                        break;
                    }
                    if ((u = searchFunction.apply(p.val)) != null) {
                        if (result.compareAndSet(null, u))
                            quietlyCompleteRoot();
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class SearchEntriesTask<K, V, U>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, U> {
        final Function<Entry<K, V>, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchEntriesTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 Function<Entry<K, V>, ? extends U> searchFunction,
                 AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        public final U getRawResult() {
            return result.get();
        }

        public final void compute() {
            final Function<Entry<K, V>, ? extends U> searchFunction;
            final AtomicReference<U> result;
            if ((searchFunction = this.searchFunction) != null &&
                    (result = this.result) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    if (result.get() != null)
                        return;
                    addToPendingCount(1);
                    new java.util.concurrent.ConcurrentHashMap.SearchEntriesTask<K, V, U>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    searchFunction, result).fork();
                }
                while (result.get() == null) {
                    U u;
                    java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
                    if ((p = advance()) == null) {
                        propagateCompletion();
                        break;
                    }
                    if ((u = searchFunction.apply(p)) != null) {
                        if (result.compareAndSet(null, u))
                            quietlyCompleteRoot();
                        return;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class SearchMappingsTask<K, V, U>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, U> {
        final BiFunction<? super K, ? super V, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchMappingsTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 BiFunction<? super K, ? super V, ? extends U> searchFunction,
                 AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        public final U getRawResult() {
            return result.get();
        }

        public final void compute() {
            final BiFunction<? super K, ? super V, ? extends U> searchFunction;
            final AtomicReference<U> result;
            if ((searchFunction = this.searchFunction) != null &&
                    (result = this.result) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    if (result.get() != null)
                        return;
                    addToPendingCount(1);
                    new java.util.concurrent.ConcurrentHashMap.SearchMappingsTask<K, V, U>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    searchFunction, result).fork();
                }
                while (result.get() == null) {
                    U u;
                    java.util.concurrent.ConcurrentHashMap.Node<K, V> p;
                    if ((p = advance()) == null) {
                        propagateCompletion();
                        break;
                    }
                    if ((u = searchFunction.apply(p.key, p.val)) != null) {
                        if (result.compareAndSet(null, u))
                            quietlyCompleteRoot();
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ReduceKeysTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, K> {
        final BiFunction<? super K, ? super K, ? extends K> reducer;
        K result;
        java.util.concurrent.ConcurrentHashMap.ReduceKeysTask<K, V> rights, nextRight;

        ReduceKeysTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.ReduceKeysTask<K, V> nextRight,
                 BiFunction<? super K, ? super K, ? extends K> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.reducer = reducer;
        }

        public final K getRawResult() {
            return result;
        }

        public final void compute() {
            final BiFunction<? super K, ? super K, ? extends K> reducer;
            if ((reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.ReduceKeysTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, reducer)).fork();
                }
                K r = null;
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; ) {
                    K u = p.key;
                    r = (r == null) ? u : u == null ? r : reducer.apply(r, u);
                }
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.ReduceKeysTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.ReduceKeysTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        K tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr :
                                    reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ReduceValuesTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, V> {
        final BiFunction<? super V, ? super V, ? extends V> reducer;
        V result;
        java.util.concurrent.ConcurrentHashMap.ReduceValuesTask<K, V> rights, nextRight;

        ReduceValuesTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.ReduceValuesTask<K, V> nextRight,
                 BiFunction<? super V, ? super V, ? extends V> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.reducer = reducer;
        }

        public final V getRawResult() {
            return result;
        }

        public final void compute() {
            final BiFunction<? super V, ? super V, ? extends V> reducer;
            if ((reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.ReduceValuesTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, reducer)).fork();
                }
                V r = null;
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; ) {
                    V v = p.val;
                    r = (r == null) ? v : reducer.apply(r, v);
                }
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.ReduceValuesTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.ReduceValuesTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        V tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr :
                                    reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ReduceEntriesTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Entry<K, V>> {
        final BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer;
        Map.Entry<K, V> result;
        java.util.concurrent.ConcurrentHashMap.ReduceEntriesTask<K, V> rights, nextRight;

        ReduceEntriesTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.ReduceEntriesTask<K, V> nextRight,
                 BiFunction<Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.reducer = reducer;
        }

        public final Map.Entry<K, V> getRawResult() {
            return result;
        }

        public final void compute() {
            final BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer;
            if ((reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.ReduceEntriesTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, reducer)).fork();
                }
                Map.Entry<K, V> r = null;
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = (r == null) ? p : reducer.apply(r, p);
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.ReduceEntriesTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.ReduceEntriesTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        Map.Entry<K, V> tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr :
                                    reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceKeysTask<K, V, U>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, U> {
        final Function<? super K, ? extends U> transformer;
        final BiFunction<? super U, ? super U, ? extends U> reducer;
        U result;
        java.util.concurrent.ConcurrentHashMap.MapReduceKeysTask<K, V, U> rights, nextRight;

        MapReduceKeysTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceKeysTask<K, V, U> nextRight,
                 Function<? super K, ? extends U> transformer,
                 BiFunction<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        public final U getRawResult() {
            return result;
        }

        public final void compute() {
            final Function<? super K, ? extends U> transformer;
            final BiFunction<? super U, ? super U, ? extends U> reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceKeysTask<K, V, U>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, reducer)).fork();
                }
                U r = null;
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p.key)) != null)
                        r = (r == null) ? u : reducer.apply(r, u);
                }
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceKeysTask<K, V, U>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceKeysTask<K, V, U>) c,
                            s = t.rights;
                    while (s != null) {
                        U tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr :
                                    reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceValuesTask<K, V, U>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, U> {
        final Function<? super V, ? extends U> transformer;
        final BiFunction<? super U, ? super U, ? extends U> reducer;
        U result;
        java.util.concurrent.ConcurrentHashMap.MapReduceValuesTask<K, V, U> rights, nextRight;

        MapReduceValuesTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceValuesTask<K, V, U> nextRight,
                 Function<? super V, ? extends U> transformer,
                 BiFunction<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        public final U getRawResult() {
            return result;
        }

        public final void compute() {
            final Function<? super V, ? extends U> transformer;
            final BiFunction<? super U, ? super U, ? extends U> reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceValuesTask<K, V, U>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, reducer)).fork();
                }
                U r = null;
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p.val)) != null)
                        r = (r == null) ? u : reducer.apply(r, u);
                }
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceValuesTask<K, V, U>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceValuesTask<K, V, U>) c,
                            s = t.rights;
                    while (s != null) {
                        U tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr :
                                    reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceEntriesTask<K, V, U>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, U> {
        final Function<Map.Entry<K, V>, ? extends U> transformer;
        final BiFunction<? super U, ? super U, ? extends U> reducer;
        U result;
        java.util.concurrent.ConcurrentHashMap.MapReduceEntriesTask<K, V, U> rights, nextRight;

        MapReduceEntriesTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceEntriesTask<K, V, U> nextRight,
                 Function<Map.Entry<K, V>, ? extends U> transformer,
                 BiFunction<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        public final U getRawResult() {
            return result;
        }

        public final void compute() {
            final Function<Map.Entry<K, V>, ? extends U> transformer;
            final BiFunction<? super U, ? super U, ? extends U> reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceEntriesTask<K, V, U>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, reducer)).fork();
                }
                U r = null;
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p)) != null)
                        r = (r == null) ? u : reducer.apply(r, u);
                }
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceEntriesTask<K, V, U>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceEntriesTask<K, V, U>) c,
                            s = t.rights;
                    while (s != null) {
                        U tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr :
                                    reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceMappingsTask<K, V, U>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, U> {
        final BiFunction<? super K, ? super V, ? extends U> transformer;
        final BiFunction<? super U, ? super U, ? extends U> reducer;
        U result;
        java.util.concurrent.ConcurrentHashMap.MapReduceMappingsTask<K, V, U> rights, nextRight;

        MapReduceMappingsTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceMappingsTask<K, V, U> nextRight,
                 BiFunction<? super K, ? super V, ? extends U> transformer,
                 BiFunction<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        public final U getRawResult() {
            return result;
        }

        public final void compute() {
            final BiFunction<? super K, ? super V, ? extends U> transformer;
            final BiFunction<? super U, ? super U, ? extends U> reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceMappingsTask<K, V, U>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, reducer)).fork();
                }
                U r = null;
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p.key, p.val)) != null)
                        r = (r == null) ? u : reducer.apply(r, u);
                }
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceMappingsTask<K, V, U>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceMappingsTask<K, V, U>) c,
                            s = t.rights;
                    while (s != null) {
                        U tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr :
                                    reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceKeysToDoubleTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Double> {
        final ToDoubleFunction<? super K> transformer;
        final DoubleBinaryOperator reducer;
        final double basis;
        double result;
        java.util.concurrent.ConcurrentHashMap.MapReduceKeysToDoubleTask<K, V> rights, nextRight;

        MapReduceKeysToDoubleTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceKeysToDoubleTask<K, V> nextRight,
                 ToDoubleFunction<? super K> transformer,
                 double basis,
                 DoubleBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Double getRawResult() {
            return result;
        }

        public final void compute() {
            final ToDoubleFunction<? super K> transformer;
            final DoubleBinaryOperator reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                double r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceKeysToDoubleTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, r, reducer)).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsDouble(r, transformer.applyAsDouble(p.key));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceKeysToDoubleTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceKeysToDoubleTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsDouble(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceValuesToDoubleTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Double> {
        final ToDoubleFunction<? super V> transformer;
        final DoubleBinaryOperator reducer;
        final double basis;
        double result;
        java.util.concurrent.ConcurrentHashMap.MapReduceValuesToDoubleTask<K, V> rights, nextRight;

        MapReduceValuesToDoubleTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceValuesToDoubleTask<K, V> nextRight,
                 ToDoubleFunction<? super V> transformer,
                 double basis,
                 DoubleBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Double getRawResult() {
            return result;
        }

        public final void compute() {
            final ToDoubleFunction<? super V> transformer;
            final DoubleBinaryOperator reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                double r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceValuesToDoubleTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, r, reducer)).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsDouble(r, transformer.applyAsDouble(p.val));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceValuesToDoubleTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceValuesToDoubleTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsDouble(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceEntriesToDoubleTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Double> {
        final ToDoubleFunction<Map.Entry<K, V>> transformer;
        final DoubleBinaryOperator reducer;
        final double basis;
        double result;
        java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToDoubleTask<K, V> rights, nextRight;

        MapReduceEntriesToDoubleTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToDoubleTask<K, V> nextRight,
                 ToDoubleFunction<Map.Entry<K, V>> transformer,
                 double basis,
                 DoubleBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Double getRawResult() {
            return result;
        }

        public final void compute() {
            final ToDoubleFunction<Map.Entry<K, V>> transformer;
            final DoubleBinaryOperator reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                double r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToDoubleTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, r, reducer)).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsDouble(r, transformer.applyAsDouble(p));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToDoubleTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToDoubleTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsDouble(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceMappingsToDoubleTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Double> {
        final ToDoubleBiFunction<? super K, ? super V> transformer;
        final DoubleBinaryOperator reducer;
        final double basis;
        double result;
        java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToDoubleTask<K, V> rights, nextRight;

        MapReduceMappingsToDoubleTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToDoubleTask<K, V> nextRight,
                 ToDoubleBiFunction<? super K, ? super V> transformer,
                 double basis,
                 DoubleBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Double getRawResult() {
            return result;
        }

        public final void compute() {
            final ToDoubleBiFunction<? super K, ? super V> transformer;
            final DoubleBinaryOperator reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                double r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToDoubleTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, r, reducer)).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsDouble(r, transformer.applyAsDouble(p.key, p.val));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToDoubleTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToDoubleTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsDouble(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceKeysToLongTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Long> {
        final ToLongFunction<? super K> transformer;
        final LongBinaryOperator reducer;
        final long basis;
        long result;
        java.util.concurrent.ConcurrentHashMap.MapReduceKeysToLongTask<K, V> rights, nextRight;

        MapReduceKeysToLongTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceKeysToLongTask<K, V> nextRight,
                 ToLongFunction<? super K> transformer,
                 long basis,
                 LongBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Long getRawResult() {
            return result;
        }

        public final void compute() {
            final ToLongFunction<? super K> transformer;
            final LongBinaryOperator reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                long r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceKeysToLongTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, r, reducer)).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsLong(r, transformer.applyAsLong(p.key));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceKeysToLongTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceKeysToLongTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsLong(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceValuesToLongTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Long> {
        final ToLongFunction<? super V> transformer;
        final LongBinaryOperator reducer;
        final long basis;
        long result;
        java.util.concurrent.ConcurrentHashMap.MapReduceValuesToLongTask<K, V> rights, nextRight;

        MapReduceValuesToLongTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceValuesToLongTask<K, V> nextRight,
                 ToLongFunction<? super V> transformer,
                 long basis,
                 LongBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Long getRawResult() {
            return result;
        }

        public final void compute() {
            final ToLongFunction<? super V> transformer;
            final LongBinaryOperator reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                long r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceValuesToLongTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, r, reducer)).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsLong(r, transformer.applyAsLong(p.val));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceValuesToLongTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceValuesToLongTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsLong(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceEntriesToLongTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Long> {
        final ToLongFunction<Map.Entry<K, V>> transformer;
        final LongBinaryOperator reducer;
        final long basis;
        long result;
        java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToLongTask<K, V> rights, nextRight;

        MapReduceEntriesToLongTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToLongTask<K, V> nextRight,
                 ToLongFunction<Map.Entry<K, V>> transformer,
                 long basis,
                 LongBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Long getRawResult() {
            return result;
        }

        public final void compute() {
            final ToLongFunction<Map.Entry<K, V>> transformer;
            final LongBinaryOperator reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                long r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToLongTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, r, reducer)).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsLong(r, transformer.applyAsLong(p));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToLongTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToLongTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsLong(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceMappingsToLongTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Long> {
        final ToLongBiFunction<? super K, ? super V> transformer;
        final LongBinaryOperator reducer;
        final long basis;
        long result;
        java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToLongTask<K, V> rights, nextRight;

        MapReduceMappingsToLongTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToLongTask<K, V> nextRight,
                 ToLongBiFunction<? super K, ? super V> transformer,
                 long basis,
                 LongBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Long getRawResult() {
            return result;
        }

        public final void compute() {
            final ToLongBiFunction<? super K, ? super V> transformer;
            final LongBinaryOperator reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                long r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToLongTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, r, reducer)).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsLong(r, transformer.applyAsLong(p.key, p.val));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToLongTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToLongTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsLong(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceKeysToIntTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Integer> {
        final ToIntFunction<? super K> transformer;
        final IntBinaryOperator reducer;
        final int basis;
        int result;
        java.util.concurrent.ConcurrentHashMap.MapReduceKeysToIntTask<K, V> rights, nextRight;

        MapReduceKeysToIntTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceKeysToIntTask<K, V> nextRight,
                 ToIntFunction<? super K> transformer,
                 int basis,
                 IntBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Integer getRawResult() {
            return result;
        }

        public final void compute() {
            final ToIntFunction<? super K> transformer;
            final IntBinaryOperator reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                int r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceKeysToIntTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, r, reducer)).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsInt(r, transformer.applyAsInt(p.key));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceKeysToIntTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceKeysToIntTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsInt(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceValuesToIntTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Integer> {
        final ToIntFunction<? super V> transformer;
        final IntBinaryOperator reducer;
        final int basis;
        int result;
        java.util.concurrent.ConcurrentHashMap.MapReduceValuesToIntTask<K, V> rights, nextRight;

        MapReduceValuesToIntTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceValuesToIntTask<K, V> nextRight,
                 ToIntFunction<? super V> transformer,
                 int basis,
                 IntBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Integer getRawResult() {
            return result;
        }

        public final void compute() {
            final ToIntFunction<? super V> transformer;
            final IntBinaryOperator reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                int r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceValuesToIntTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, r, reducer)).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsInt(r, transformer.applyAsInt(p.val));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceValuesToIntTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceValuesToIntTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsInt(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceEntriesToIntTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Integer> {
        final ToIntFunction<Map.Entry<K, V>> transformer;
        final IntBinaryOperator reducer;
        final int basis;
        int result;
        java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToIntTask<K, V> rights, nextRight;

        MapReduceEntriesToIntTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToIntTask<K, V> nextRight,
                 ToIntFunction<Map.Entry<K, V>> transformer,
                 int basis,
                 IntBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Integer getRawResult() {
            return result;
        }

        public final void compute() {
            final ToIntFunction<Map.Entry<K, V>> transformer;
            final IntBinaryOperator reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                int r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToIntTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, r, reducer)).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsInt(r, transformer.applyAsInt(p));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToIntTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceEntriesToIntTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsInt(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceMappingsToIntTask<K, V>
            extends java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, Integer> {
        final ToIntBiFunction<? super K, ? super V> transformer;
        final IntBinaryOperator reducer;
        final int basis;
        int result;
        java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToIntTask<K, V> rights, nextRight;

        MapReduceMappingsToIntTask
                (java.util.concurrent.ConcurrentHashMap.BulkTask<K, V, ?> p, int b, int i, int f, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] t,
                 java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToIntTask<K, V> nextRight,
                 ToIntBiFunction<? super K, ? super V> transformer,
                 int basis,
                 IntBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Integer getRawResult() {
            return result;
        }

        public final void compute() {
            final ToIntBiFunction<? super K, ? super V> transformer;
            final IntBinaryOperator reducer;
            if ((transformer = this.transformer) != null &&
                    (reducer = this.reducer) != null) {
                int r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 &&
                        (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToIntTask<K, V>
                            (this, batch >>>= 1, baseLimit = h, f, tab,
                                    rights, transformer, r, reducer)).fork();
                }
                for (java.util.concurrent.ConcurrentHashMap.Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsInt(r, transformer.applyAsInt(p.key, p.val));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToIntTask<K, V>
                            t = (java.util.concurrent.ConcurrentHashMap.MapReduceMappingsToIntTask<K, V>) c,
                            s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsInt(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe U;
    private static final long SIZECTL;
    private static final long TRANSFERINDEX;
    private static final long BASECOUNT;
    private static final long CELLSBUSY;
    private static final long CELLVALUE;
    private static final long ABASE;
    private static final int ASHIFT;

    static {
        try {
            U = sun.misc.Unsafe.getUnsafe();
            Class<?> k = java.util.concurrent.ConcurrentHashMap.class;
            SIZECTL = U.objectFieldOffset
                    (k.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = U.objectFieldOffset
                    (k.getDeclaredField("transferIndex"));
            BASECOUNT = U.objectFieldOffset
                    (k.getDeclaredField("baseCount"));
            CELLSBUSY = U.objectFieldOffset
                    (k.getDeclaredField("cellsBusy"));
            Class<?> ck = java.util.concurrent.ConcurrentHashMap.CounterCell.class;
            CELLVALUE = U.objectFieldOffset
                    (ck.getDeclaredField("value"));
            Class<?> ak = java.util.concurrent.ConcurrentHashMap.Node[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
