package spring.interfaces;

/**
 * @ClassName AliasRegistry
 * @Description 用来操作别名的
 * @Author 17126
 * @Date 2021/6/27 11:18
 */
public interface AliasRegistry {

    /**
     * 对指定的名称注册别名
     */
    void registerAlias(String var1, String var2);

    /**
     * 从当前容器移除指定别名
     */
    void removeAlias(String var1);

    /**
     * 判断指定名称是否为别名
     */
    boolean isAlias(String var1);

    /**
     * 返回指定名称的所有别名
     */
    String[] getAliases(String var1);
}
