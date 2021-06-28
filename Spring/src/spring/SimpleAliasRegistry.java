package spring;

import spring.interfaces.AliasRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LiuBo
 * @date 2021/6/27
 * @Description 单例别名注册
 */
public class SimpleAliasRegistry implements AliasRegistry {

    private final Map<String, String> aliasMap = new ConcurrentHashMap(16);

    public SimpleAliasRegistry() {
    }

    @Override
    public void registerAlias(String name, String alias) {
        Map var3 = this.aliasMap;
        synchronized (this.aliasMap) {
            // 别名和bean名称一样
            if (alias.equals(name)) {
                this.aliasMap.remove(alias);
            } else {
                String registeredName = (String) this.aliasMap.get(alias);

                if (registeredName != null) {
                    if (registeredName.equals(name)) {
                        return;
                    }

                    if (!this.allowAliasOverriding()) {
                        throw new IllegalStateException("Cannot define alias '" + alias + "' for name '" + name + "': It is already registered for name '" + registeredName + "'.");
                    }

//                    if (this.logger.isDebugEnabled()) {
//                        this.logger.debug("Overriding alias '" + alias + "' definition for registered name '" + registeredName + "' with new target name '" + name + "'");
//                    }
                }

                this.checkForAliasCircle(name, alias);
                this.aliasMap.put(alias, name);
//                if (this.logger.isTraceEnabled()) {
//                    this.logger.trace("Alias definition '" + alias + "' registered for name '" + name + "'");
//                }
            }
        }
    }

    @Override
    public void removeAlias(String alias) {
        Map var2 = this.aliasMap;
        synchronized (this.aliasMap) {
            String name = (String) this.aliasMap.remove(alias);
            if (name == null) {
                throw new IllegalStateException("No alias '" + alias + "' registered");
            }
        }
    }

    @Override
    public boolean isAlias(String name) {
        return this.aliasMap.containsKey(name);
    }

    @Override
    public String[] getAliases(String name) {
        List<String> result = new ArrayList();
        Map var3 = this.aliasMap;
        synchronized (this.aliasMap) {
            this.retrieveAliases(name, result);
        }
        return (String[]) result.toArray();
    }

    private void retrieveAliases(String name, List<String> result) {
        this.aliasMap.forEach((alias, registeredName) -> {
            if (registeredName.equals(name)) {
                result.add(alias);
                this.retrieveAliases(alias, result);
            }

        });
    }

    protected boolean allowAliasOverriding() {
        return true;
    }

    protected void checkForAliasCircle(String name, String alias) {
        if (this.hasAlias(alias, name)) {
            throw new IllegalStateException("Cannot register alias '" + alias + "' for name '" + name + "': Circular reference - '" + name + "' is a direct or indirect alias for '" + alias + "' already");
        }
    }

    public boolean hasAlias(String name, String alias) {
        String registeredName = (String) this.aliasMap.get(alias);
        //return ObjectUtils.nullSafeEquals(registeredName, name) || registeredName != null && this.hasAlias(name, registeredName);
        return true;
    }
}
