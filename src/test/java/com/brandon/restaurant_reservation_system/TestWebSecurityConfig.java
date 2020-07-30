/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@TestConfiguration
//@EnableWebSecurity
public class TestWebSecurityConfig extends WebSecurityConfigurerAdapter {

    //    @Override
    //    public void configure(AuthenticationManagerBuilder auth) throws Exception {
    //        auth.build();
    //    }

    //
    //    @Bean
    //    public PasswordEncoder passwordEncoder() {
    //        return new UserPasswordEncoder();
    //    }

    //    @SuppressWarnings("EmptyMethod")
    //    @Bean
    //    @Override
    //    public AuthenticationManager authenticationManagerBean() throws Exception {
    //        return super.authenticationManagerBean();
    //    }


    //    @Override
    //    public void configure(WebSecurity web) throws Exception {
    //        web.debug(true).ignoring().anyRequest();
    //    }
    //
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
          .csrf().disable()
          .authorizeRequests().anyRequest().permitAll();
        //          .and().exceptionHandling();
        //        http.headers().frameOptions().disable();

    }
}
