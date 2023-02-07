package com.ecommerce.Ecommerce.securityconfig;


import com.ecommerce.Ecommerce.service.AppUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@Configuration
@EnableResourceServer
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    AppUserDetailsService userDetailsService;

    public ResourceServerConfiguration() {
        super();
    }

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authenticationProvider;
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
    }

    /*@Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_SELLER > ROLE_CUSTOMER ");
        return roleHierarchy;
    }

    private SecurityExpressionHandler<FilterInvocation> webExpressionHandler() {
        DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
        return defaultWebSecurityExpressionHandler;
    }*/


    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
              /*  .expressionHandler(webExpressionHandler())*/
                .antMatchers("/").anonymous()
                .antMatchers("/register").permitAll()
                .antMatchers("/resend-confirmation").permitAll()
                .antMatchers("/customers").hasAnyRole("ADMIN")
                .antMatchers("/sellers").hasAnyRole("ADMIN")
                .antMatchers("/admin/*").hasAnyRole("ADMIN")
                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                .antMatchers("/category/*").hasAnyRole("ADMIN")
                .antMatchers("/category/**").hasAnyRole("ADMIN")
                .antMatchers("/metadatafield/*").hasAnyRole("ADMIN")
                .antMatchers("/forgotpassword").permitAll()
                .antMatchers("/confirm-reset").permitAll()
                .antMatchers("/find/customer").permitAll()
                .antMatchers("/seller/*").hasAnyRole("SELLER")
                .antMatchers("/seller/**").hasAnyRole("SELLER")
                .antMatchers("/customer/*").hasAnyRole("CUSTOMER")
                .antMatchers("/customer/**").hasAnyRole("CUSTOMER")
                .antMatchers("/find/address/customer").permitAll()
                .antMatchers("/update/address").permitAll()
                .antMatchers("/confirm-account").permitAll()
                .antMatchers("/seller/register").permitAll()
                .antMatchers("/activatecustomer/{id}").hasAnyRole("ADMIN")
                .antMatchers("/deactivatecustomer/{id}").hasAnyRole("ADMIN")
                .antMatchers("/activateseller/{id}").hasAnyRole("ADMIN")
                .antMatchers("/deactivateseller/{id}").hasAnyRole("ADMIN")
                .antMatchers("/admin/home").hasAnyRole("ADMIN")
                .antMatchers("/customer/home").hasAnyRole("CUSTOMER")
                .antMatchers("/seller/home").hasAnyRole("SELLER")
                .antMatchers("/doLogout").hasAnyRole("ADMIN","CUSTOMER","SELLER")
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable();
    }
}