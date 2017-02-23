package com.cjy.flb.domain;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.JsonRefreshTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.extractors.TokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;

/**
 * Created by Administrator on 2016/3/12 0012.
 */
public class Cdc12320ApiTest extends DefaultApi20 {
    private static final String URL = "https://caijiyun.cn";

    private static final String JAR_VERSION = "2.1";
    private static final String AUTHORIZE_HOST = URL;
    private static final String AUTHORIZE_URL = URL +
            "/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code";
    private static final String SCOPED_AUTHORIZE_URL = URL +
            "/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=%s";

    public Cdc12320ApiTest()
    {
    }

    public TokenExtractor getAccessTokenExtractor()
    {
        return new JsonTokenExtractor();
    }

    public TokenExtractor getRefreshTokenExtractor()
    {
        return new JsonRefreshTokenExtractor();
    }

    public String getAccessTokenEndpoint()
    {
        return URL+"/oauth/token";
    }

    public String getAuthorizationUrl(OAuthConfig config)
    {
        return config.hasScope() ? String.format(URL +
                        "/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=%s",
                new Object[]{config.getApiKey(),
                        OAuthEncoder.encode(config.getCallback()),
                        OAuthEncoder.encode(config.getScope())}) : String.format(URL +
                        "/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                new Object[]{config.getApiKey(), OAuthEncoder.encode(config.getCallback())});
    }
}
