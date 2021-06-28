package spring.interfaces;

import com.sun.istack.internal.Nullable;

/**
 * @ClassName BeanMetadataElement
 * @Description TODO
 * @Author 17126
 * @Date 2021/6/27 19:08
 */
public interface BeanMetadataElement {
    @Nullable
    default Object getSource() {
        return null;
    }
}
