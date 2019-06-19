package com.skybot.irc.config;

import com.skybot.irc.models.spotify.SpotifyToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CompositeFilter;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableOAuth2Sso
@EnableOAuth2Client
@Order(value = 0)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    OAuth2ClientContext oauth2ClientContext;
//
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/login", "/webjars/**", "/error**")
                .permitAll()
                .anyRequest()
                .authenticated();
    }
//
//    private Filter ssoFilter() {
//        CompositeFilter filter = new CompositeFilter();
//        List<Filter> filters = new ArrayList<>();
//
//        OAuth2ClientAuthenticationProcessingFilter spotifyFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/spotify");
//        OAuth2RestTemplate spotifyTemplate = new OAuth2RestTemplate(spotify(), oauth2ClientContext);
//        spotifyFilter.setRestTemplate(spotifyTemplate);
//        UserInfoTokenServices tokenServices = new UserInfoTokenServices(spotifyResource().getUserInfoUri(), spotify().getClientId());
//        tokenServices.setRestTemplate(spotifyTemplate);
//        spotifyFilter.setTokenServices(tokenServices);
//        filters.add(spotifyFilter);
//
//        OAuth2ClientAuthenticationProcessingFilter twitchFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/twitch");
//        OAuth2RestTemplate twitchTemplate = new OAuth2RestTemplate(twitch(), oauth2ClientContext);
//        twitchFilter.setRestTemplate(twitchTemplate);
//        tokenServices = new UserInfoTokenServices(twitchResource().getUserInfoUri(), twitch().getClientId());
//        tokenServices.setRestTemplate(twitchTemplate);
//        twitchFilter.setTokenServices(tokenServices);
//        filters.add(twitchFilter);
//
//        filter.setFilters(filters);
//        return filter;
//    }
//
//    @Bean
//    public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
//        FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<OAuth2ClientContextFilter>();
//        registration.setFilter(filter);
//        registration.setOrder(-100);
//        return registration;
//    }
//
    @Bean
    public SpotifyToken spotifyToken() {
        return new SpotifyToken();
    }

    @Bean(name = "spotify")
    @ConfigurationProperties("spotify.client")
    public AuthorizationCodeResourceDetails spotify() {
        return new AuthorizationCodeResourceDetails();
    }

//    @Bean(name = "spotifyAccessToken")
//    public OAuth2AccessToken oAuth2AccessToken() {
//
//    }

//    @Bean
//    @ConfigurationProperties("spotify.resource")
//    public ResourceServerProperties spotifyResource() {
//        return new ResourceServerProperties();
//    }
//
//    @Bean
//    @ConfigurationProperties("twitch.client")
//    public AuthorizationCodeResourceDetails twitch() {
//        return new AuthorizationCodeResourceDetails();
//    }
//
//    @Bean
//    @ConfigurationProperties("twitch.resource")
//    public ResourceServerProperties twitchResource() {
//        return new ResourceServerProperties();
//    }

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
