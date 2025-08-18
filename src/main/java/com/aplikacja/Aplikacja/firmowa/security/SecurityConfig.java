package com.aplikacja.Aplikacja.firmowa.security;

import com.aplikacja.Aplikacja.firmowa.security.JWebToken.AuthenticationEntryPointJWebToken;
import com.aplikacja.Aplikacja.firmowa.security.JWebToken.AuthenticationTokenFilter;
import com.aplikacja.Aplikacja.firmowa.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private AuthenticationEntryPointJWebToken unauthorize;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean() {
        return new AuthenticationTokenFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().ignoringAntMatchers("/h2-console/**").disable()
                .headers().frameOptions().disable()

                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorize)

                .and()
                .authorizeRequests()
                .expressionHandler(mySecurityExpressionHandler())

                // WAŻNE: reguła dla /admin/history przed ogólną /admin/**
                .antMatchers("/admin/history/**")
                .hasAnyAuthority("ROLE_ADMIN_ROLE", "ROLE_MANAGER_ROLE")

                // reszta admina tylko dla ADMIN_ROLE
                .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN_ROLE")

                .antMatchers("/manager/**").hasAuthority("ROLE_MANAGER_ROLE")
                .antMatchers("/staff/**").hasAuthority("ROLE_STAFF_ROLE")
                .antMatchers("/dashboard").hasAuthority("ROLE_USER_ROLE")

                .antMatchers(
                        "/", "/calendar", "/login", "/register", "/user/authorize/**", "/about", "/error",
                        "/contact",
                        "/h2-console/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico"
                ).permitAll()

                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(customLoginSuccessHandler())
                .permitAll()

                .and()
                .logout()
                .logoutSuccessUrl("/login?logout")
                .permitAll();

        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = ""
                + "ROLE_ADMIN_ROLE > ROLE_MANAGER_ROLE\n"
                + "ROLE_MANAGER_ROLE > ROLE_STAFF_ROLE\n"
                + "ROLE_STAFF_ROLE > ROLE_USER_ROLE\n";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    public DefaultWebSecurityExpressionHandler mySecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy());
        return handler;
    }

    @Bean
    public AuthenticationSuccessHandler customLoginSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

                if (roles.contains("ROLE_ADMIN_ROLE")) {
                    response.sendRedirect("/admin/dashboard");
                } else if (roles.contains("ROLE_MANAGER_ROLE")) {
                    response.sendRedirect("/manager/dashboard");
                } else if (roles.contains("ROLE_STAFF_ROLE")) {
                    response.sendRedirect("/staff/dashboard");
                } else {
                    response.sendRedirect("/dashboard");
                }
            }
        };
    }
}