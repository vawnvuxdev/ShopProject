package com.evanshop.admin.config.sercurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService userDetailsService() {
		return new ShopUserDetailsService();
	}

	// Use BCrypt to encode password
	// Make password encoder bean
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenProvider = new DaoAuthenticationProvider();
		authenProvider.setUserDetailsService(userDetailsService());
		authenProvider.setPasswordEncoder(passwordEncoder());

		return authenProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				// User
				.antMatchers("/users/**", "/settings/**", "/countries/**", "/states/**", "/customers/**").hasAuthority("Admin")
				// Category
				.antMatchers("/categories/**").hasAnyAuthority("Admin", "Editor")
				// Brand
				.antMatchers("/brands/**").hasAnyAuthority("Admin", "Editor")
				// Product
				.antMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
				.hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")

				.antMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")

				.antMatchers("/products/edit/**", "/products/save", "/products/check_unique")
				.hasAnyAuthority("Admin", "Editor", "Salesperson")

				.antMatchers("/products/**").hasAnyAuthority("Admin", "Editor").anyRequest().authenticated().and()
				.formLogin().loginPage("/login").usernameParameter("email").permitAll().and().logout().permitAll().and()
				.rememberMe().key("olaolasdajsdufizxcvokzxfd_asdjklaj21314").tokenValiditySeconds(7 * 24 * 60 * 60);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}

}
