package wx;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * wechat mp properties
 *
 */
@Data
public class WxMpProperties {

    /**
     * 多个公众号配置信息
     */
    private List<MpConfig> configs;

    @Data
    public static class MpConfig {
        /**
         * 设置微信公众号的appid
         */
        private String appId;

        /**
         * 设置微信公众号的app secret
         */
        private String secret;

        /**
         * 设置微信公众号的token
         */
        private String token;

        /**
         * 设置微信公众号的EncodingAESKey
         */
        private String aesKey;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
