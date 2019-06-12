package com.skybot.irc.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@Configuration
@EnableOAuth2Sso
@Order(value = 0)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PrincipalExtractor twitchPrincipalExtractor() {
        return new TwitchPrincipalExtractor();
    }

//    @EventListener
//    public void authSuccessEventListener(AuthenticationSuccessEvent authorizedEvent){
//        System.out.println("User Oauth2 login success");
//        System.out.println("Creds: " + authorizedEvent.getAuthentication().getCredentials());
//        System.out.println("Details: " + authorizedEvent.getAuthentication().getDetails());
//        System.out.println("Authorities: " + authorizedEvent.getAuthentication().getAuthorities());
//        System.out.println("This is success event : " + authorizedEvent.getAuthentication().getPrincipal());
//    }

    @EventListener
    public void authFailedEventListener(AbstractAuthenticationFailureEvent oAuth2AuthenticationFailureEvent){
        // write custom code here login failed audit.
        System.out.println("User Oauth2 login Failed");
        System.out.println(oAuth2AuthenticationFailureEvent.getException());
        System.out.println(oAuth2AuthenticationFailureEvent.getAuthentication().getPrincipal());
    }
}
