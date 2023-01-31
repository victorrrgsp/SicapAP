package com.example.sicapweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.TimeZone;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EntityScan(basePackages = {"br.gov.to.tce.*"})
public class SicapwebApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Araguaina"));
    }

    public static void main(String[] args) {

        SpringApplication.run(SicapwebApplication.class, args);

        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken("user", "pass");
    }

//    public String registerUser(UserRegistrationFormBean userRegistrationFormBean,
//                               RequestContext requestContext,
//                               ExternalContext externalContext) {
//
//        try {
//            Locale userLocale = requestContext.getExternalContext().getLocale();
//            this.userService.createNewUser(userRegistrationFormBean, userLocale, Constants.SYSTEM_USER_ID);
//            String emailAddress = userRegistrationFormBean.getChooseEmailAddressFormBean().getEmailAddress();
//            String password = userRegistrationFormBean.getChoosePasswordFormBean().getPassword();
//            doAutoLogin(emailAddress, password, (HttpServletRequest) externalContext.getNativeRequest());
//            return "success";
//
//        } catch (EmailAddressNotUniqueException e) {
//            MessageResolver messageResolvable
//                    = new MessageBuilder().error()
//                    .source(UserRegistrationFormBean.PROPERTYNAME_EMAIL_ADDRESS)
//                    .code("userRegistration.emailAddress.not.unique")
//                    .build();
//            requestContext.getMessageContext().addMessage(messageResolvable);
//            return "error";
//        }
//
//    }
//
//
//    private void doAutoLogin(String username, String password, HttpServletRequest request) {
//
//        try {
//            // Must be called from request filtered by Spring Security, otherwise SecurityContextHolder is not updated
//            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
//            token.setDetails(new WebAuthenticationDetails(request));
//            Authentication authentication = this.authenticationProvider.authenticate(token);
//            logger.debug("Logging in with [{}]", authentication.getPrincipal());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } catch (Exception e) {
//            SecurityContextHolder.getContext().setAuthentication(null);
//            logger.error("Failure in autoLogin", e);
//        }
//
//    }


    @Bean
    public JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setMaxWaitMillis(2000);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
}
