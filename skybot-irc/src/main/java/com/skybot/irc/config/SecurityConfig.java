package com.skybot.irc.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import javax.annotation.Resource;
import java.util.Map;

@Configuration
@EnableOAuth2Sso
@EnableOAuth2Client
@Order(value = 0)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    @Qualifier("accessTokenRequest")
    private AccessTokenRequest accessTokenRequest;

    @Bean
    @Primary
    public OAuth2RestOperations oAuth2RestOperations(OAuth2ClientContext oauth2ClientContext,
                                                 OAuth2ProtectedResourceDetails details) {
        OAuth2RestTemplate template = new OAuth2RestTemplate(details,
                oauth2ClientContext);
        return template;
    }
    @Bean
    public OAuth2ClientContextFilter oauth2ClientContextFilter() {
        OAuth2ClientContextFilter filter = new OAuth2ClientContextFilter();
        return filter;
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    protected AccessTokenRequest accessTokenRequest(@Value("#{request.parameterMap}")
                                                            Map<String, String[]> parameters, @Value("#{request.getAttribute('currentUri')}")
                                                            String currentUri) {
        DefaultAccessTokenRequest request = new DefaultAccessTokenRequest(parameters);
        request.setCurrentUri(currentUri);
        return request;
    }

    @Bean
    @Primary
    public OAuth2ClientContext oauth2ClientContext(AccessTokenRequest accessTokenRequest) {
        return new DefaultOAuth2ClientContext(accessTokenRequest);
    }

    @Bean
    public PrincipalExtractor twitchPrincipalExtractor() {
        return new TwitchPrincipalExtractor();
    }

    @EventListener
    public void authFailedEventListener(AbstractAuthenticationFailureEvent oAuth2AuthenticationFailureEvent){
        // write custom code here login failed audit.
        System.out.println("User Oauth2 login Failed");
        System.out.println(oAuth2AuthenticationFailureEvent.getException());
        System.out.println(oAuth2AuthenticationFailureEvent.getAuthentication().getPrincipal());
    }
}
