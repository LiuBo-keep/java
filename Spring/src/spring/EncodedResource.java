package spring;

import com.sun.istack.internal.Nullable;
import spring.interfaces.Resource;

import java.nio.charset.Charset;

/**
 * @ClassName EncodedResource
 * @Description TODD
 * @Author liubo
 * @Date 2021/6/22 21:54
 * @Version 1.0
 **/
public class EncodedResource {

    private final Resource resource;
    @Nullable
    private final String encoding;
    @Nullable
    private final Charset charset;

    public EncodedResource(Resource resource) {
        this(resource, (String)null, (Charset)null);
    }

    private EncodedResource(Resource resource, @Nullable String encoding, @Nullable Charset charset) {
        //Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
        this.encoding = encoding;
        this.charset = charset;
    }
}
