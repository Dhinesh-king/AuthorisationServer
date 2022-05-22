package com.example.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.auth.entity.User;
import com.example.auth.repo.UserRepo;

@Service
public class UserDetails implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;
	
	@Override
	public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		User user=userRepo.findByEmail(username);
		
		if(user==null) {
			throw new UsernameNotFoundException("User Not Found for Email"+username);
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getRoles());
	}

}
