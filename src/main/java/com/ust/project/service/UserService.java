package com.ust.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ust.project.entity.User;
import com.ust.project.repository.UserRepository;


@Service
public class UserService {
	
	@Autowired
	UserRepository repository;

	public User registerUser(User user)
	{
		User userSaved = repository.save(user);
		return userSaved;
	}
	
	public User getUserByUsername(String username)  {
		
		return repository.findByUsername(username);
	}

}
