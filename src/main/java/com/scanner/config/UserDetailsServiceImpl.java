package com.scanner.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.scanner.entity.User;
import com.scanner.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository sr;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//fetching user		
		User user = sr.getUserByUserName(username);
		
		if(user==null)
		{
			throw new UsernameNotFoundException("Could not found user");
		}
		
		CustomUserDetails details=new CustomUserDetails(user);
		
		return details;
				
	}

	
}
