package com.doan.configuration;

import com.doan.service.impl.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    MyUserDetailsService myUserDetailsService;

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers("/").permitAll()
                .antMatchers("/resources/**").permitAll()
                        .antMatchers("/add-story", "/payment", "/update-story", "/update-chapter", "/payment").hasRole("AUTHOR")
                .antMatchers("/payment").hasRole("USER")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .and()

                .formLogin()//
                // Submit URL của trang login
                .loginProcessingUrl("/j_spring_security_check") // Submit URL
                .loginPage("/login")//
                .usernameParameter("username")//
                .passwordParameter("password")
                .defaultSuccessUrl("/")//
                .failureUrl("/login?error")
                // Cấu hình cho Logout Page.
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);
        http.authorizeRequests().and() //
                .rememberMe().tokenRepository(this.persistentTokenRepository()) //
                .tokenValiditySeconds(86400)
                .and()
                .exceptionHandling().accessDeniedPage("/403");
                ; // 24h

    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        return new InMemoryTokenRepositoryImpl();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/static/richtexteditor/**");
    }
}
