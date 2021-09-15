package com.example.sicapweb;

import com.example.sicapweb.security.Config;
import com.example.sicapweb.security.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EntityScan(basePackages = {"br.gov.to.tce.*"})
public class SicapwebApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Brasilia"));
    }

    public static void main(String[] args) {
        User user = new User();
        //user.getDateEnd().cdate.addHours(3)
        User.getUser();
        Config config = new Config();
        config.jedis = new Jedis(config.ip, 6379);
        config.jedis.set(user.userName, Config.json(user));
        System.out.println(config.jedis.get(user.userName));

        SpringApplication.run(SicapwebApplication.class, args);


        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken("user", "pass");

        // return new UsernamePasswordAuthenticationToken("username", null, Arrays.asList("sdfsfd"));

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
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT").allowedHeaders("*");
            }
        };
    }
}
