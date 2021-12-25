package org.springframework.security.crypto.password;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
// 委托密码编码器
public class DelegatingPasswordEncoder implements PasswordEncoder {
    private static final String PREFIX = "{";
    private static final String SUFFIX = "}";
    private final String idForEncode;
    private final PasswordEncoder passwordEncoderForEncode;
    private final Map<String, PasswordEncoder> idToPasswordEncoder;
    private PasswordEncoder defaultPasswordEncoderForMatches = new DelegatingPasswordEncoder.UnmappedIdPasswordEncoder();
	
	// 构造器 初始化 默认编码的id  所有编码的map
    public DelegatingPasswordEncoder(String idForEncode, Map<String, PasswordEncoder> idToPasswordEncoder) {
		// 默认编码id不能为空
        if (idForEncode == null) {
            throw new IllegalArgumentException("idForEncode cannot be null");
			// 存储所有编码的map中必传包含默认的编码
        } else if (!idToPasswordEncoder.containsKey(idForEncode)) {
            throw new IllegalArgumentException("idForEncode " + idForEncode + "is not found in idToPasswordEncoder " + idToPasswordEncoder);
        } else {
			// 获取所有编码id
            Iterator var3 = idToPasswordEncoder.keySet().iterator();
			
			// 遍历所有的编码进行校验
            while(var3.hasNext()) {
                String id = (String)var3.next();
                if (id != null) {
                    if (id.contains("{")) {
                        throw new IllegalArgumentException("id " + id + " cannot contain " + "{");
                    }

                    if (id.contains("}")) {
                        throw new IllegalArgumentException("id " + id + " cannot contain " + "}");
                    }
                }
            }
			// 默认编码id
            this.idForEncode = idForEncode;
			// 默认编码器
            this.passwordEncoderForEncode = (PasswordEncoder)idToPasswordEncoder.get(idForEncode);
			// 所有编码器
            this.idToPasswordEncoder = new HashMap(idToPasswordEncoder);
        }
    }

    public void setDefaultPasswordEncoderForMatches(PasswordEncoder defaultPasswordEncoderForMatches) {
        if (defaultPasswordEncoderForMatches == null) {
            throw new IllegalArgumentException("defaultPasswordEncoderForMatches cannot be null");
        } else {
            this.defaultPasswordEncoderForMatches = defaultPasswordEncoderForMatches;
        }
    }

    public String encode(CharSequence rawPassword) {
        return "{" + this.idForEncode + "}" + this.passwordEncoderForEncode.encode(rawPassword);
    }

    public boolean matches(CharSequence rawPassword, String prefixEncodedPassword) {
        if (rawPassword == null && prefixEncodedPassword == null) {
            return true;
        } else {
            String id = this.extractId(prefixEncodedPassword);
            PasswordEncoder delegate = (PasswordEncoder)this.idToPasswordEncoder.get(id);
            if (delegate == null) {
                return this.defaultPasswordEncoderForMatches.matches(rawPassword, prefixEncodedPassword);
            } else {
                String encodedPassword = this.extractEncodedPassword(prefixEncodedPassword);
                return delegate.matches(rawPassword, encodedPassword);
            }
        }
    }

    private String extractId(String prefixEncodedPassword) {
        if (prefixEncodedPassword == null) {
            return null;
        } else {
            int start = prefixEncodedPassword.indexOf("{");
            if (start != 0) {
                return null;
            } else {
                int end = prefixEncodedPassword.indexOf("}", start);
                return end < 0 ? null : prefixEncodedPassword.substring(start + 1, end);
            }
        }
    }

    public boolean upgradeEncoding(String prefixEncodedPassword) {
        String id = this.extractId(prefixEncodedPassword);
        if (!this.idForEncode.equalsIgnoreCase(id)) {
            return true;
        } else {
            String encodedPassword = this.extractEncodedPassword(prefixEncodedPassword);
            return ((PasswordEncoder)this.idToPasswordEncoder.get(id)).upgradeEncoding(encodedPassword);
        }
    }

    private String extractEncodedPassword(String prefixEncodedPassword) {
        int start = prefixEncodedPassword.indexOf("}");
        return prefixEncodedPassword.substring(start + 1);
    }

    private class UnmappedIdPasswordEncoder implements PasswordEncoder {
        private UnmappedIdPasswordEncoder() {
        }

        public String encode(CharSequence rawPassword) {
            throw new UnsupportedOperationException("encode is not supported");
        }

        public boolean matches(CharSequence rawPassword, String prefixEncodedPassword) {
            String id = DelegatingPasswordEncoder.this.extractId(prefixEncodedPassword);
            throw new IllegalArgumentException("There is no PasswordEncoder mapped for the id \"" + id + "\"");
        }
    }
}
