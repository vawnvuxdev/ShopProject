package com.evanshop.admin.config.sercurity;

import com.evanshop.admin.repository.UserRepository;
import com.evanshop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ShopUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.getUserByEmail(email);
		
		if(user != null) {
			return new ShopUserDetails(user);
		}
		
		throw new UsernameNotFoundException("Could not find user with email:" + email);
	}

}
