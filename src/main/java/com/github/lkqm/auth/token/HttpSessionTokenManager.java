package com.github.lkqm.auth.token;

import com.github.lkqm.auth.token.support.JsonUtils;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * Save token data in HttpSession object.
 */
@Getter
public class HttpSessionTokenManager implements TokenManager {

    private final String attributeName;

    public HttpSessionTokenManager() {
        this.attributeName = "__TOKEN__";
    }

    public HttpSessionTokenManager(@NonNull String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String generateToken(Object data) {
        HttpServletRequest request = (HttpServletRequest) RequestContextHolder.getRequestAttributes();
        request.getSession().setAttribute(attributeName, JsonUtils.toJson(data));
        return null;
    }

    @Override
    public void removeToken(String token) {
        HttpServletRequest request = (HttpServletRequest) RequestContextHolder.getRequestAttributes();
        request.getSession().removeAttribute(attributeName);
    }

    @Override
    public <T> T getTokenData(String token, Class<T> type) {
        HttpServletRequest request = (HttpServletRequest) RequestContextHolder.getRequestAttributes();
        Object data = request.getSession().getAttribute(attributeName);
        if (data == null) return null;
        return JsonUtils.fromJson(data.toString(), type);
    }

    @Override
    public <T> T getTokenDataAndDelay(String token, Class<T> type) {
        return getTokenData(token, type);
    }
}
