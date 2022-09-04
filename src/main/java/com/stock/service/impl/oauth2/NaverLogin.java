package com.stock.service.impl.oauth2;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;


@Slf4j
@Service
public class NaverLogin {

    private final static String CLIENT_ID = "";
    private final static String CLIENT_SECRET = "";

    @Value("${naverReturnUrl}")
    private String REDIRECT_URI ;
    private final static String SESSION_STATE = "";

    private final static String PROFILE_API_URL = "";

    public String getAuthorizationUrl(HttpSession session) {
        String state = generateRandomString();

        setSession(session,state);
        session.setAttribute("path","naver");

        OAuth20Service oauthService = new ServiceBuilder()
                .apiKey(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .callback(REDIRECT_URI)
                .state(state)
                .build(NaverLoginApi.instance());

        return oauthService.getAuthorizationUrl();

    }

    public OAuth2AccessToken getAccessToken(String code, String state) throws IOException{
        OAuth20Service oauthService = new ServiceBuilder()
                .apiKey(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .callback(REDIRECT_URI)
                .state(state)
                .build(NaverLoginApi.instance());


        OAuth2AccessToken accessToken = oauthService.getAccessToken(code);
        return accessToken;

    }

    private String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    private void setSession(HttpSession session,String state){
        session.setAttribute(SESSION_STATE, state);
    }

    private String getSession(HttpSession session){
        return (String) session.getAttribute(SESSION_STATE);
    }

    public String getUserProfile(OAuth2AccessToken oauthToken) throws IOException{
        OAuth20Service oauthService =new ServiceBuilder()
                .apiKey(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .callback(REDIRECT_URI).build(NaverLoginApi.instance());

        OAuthRequest request = new OAuthRequest(Verb.GET, PROFILE_API_URL, oauthService);
        oauthService.signRequest(oauthToken, request);
        Response response = request.send();
        return response.getBody();

    }

}
