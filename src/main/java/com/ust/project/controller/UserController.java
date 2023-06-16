package com.ust.project.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ust.project.entity.User;
import com.ust.project.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@PostMapping("/user/register")
	public ResponseEntity<?> registerUser(@RequestBody User user) throws Exception
	{
		System.out.println(user.getUsername());
		System.out.println(user.getName());
		System.out.println(user.getPassword());
		System.out.println(user.getConpassword());
		System.out.println(user.getEmail());
	
		
		String tempUsername=user.getUsername();
		if(tempUsername != null && tempUsername!="") {
			User userobj = userService.getUserByUsername(tempUsername);
			if(userobj != null ) {
				throw new Exception("user with "+tempUsername+" already exists");
			}
		}
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		
		User user2 = new User( user.getId(), user.getUsername(), user.getName(), encodedPassword, user.getEmail(), encodedPassword,user.getType());
		
		
		User registerUser = userService.registerUser(user2);
		return new ResponseEntity<User>(registerUser, HttpStatus.OK);
	}

	@PostMapping("/users/login")
	public ResponseEntity<?> logIn(@RequestBody User user) {
	    System.out.println(user.getUsername());
	    System.out.println(user.getPassword());

	    User user1 = userService.getUserByUsername(user.getUsername());

	    if (user1 == null && user.getUsername().equals("admin") && user.getPassword().equals("admin")) {
	        user1 = new User();
	        user1.setUsername("admin");
	        user1.setPassword("admin");
	        user1.setType("admin");
	    }

	    System.out.println(user1.getType());

	    if (user1 != null) {
	        String typ = user.getType();
	        String typp = user1.getType();
	        String usertype;

	        if (typp.equals(typ)) {
	            usertype = typ;
	            System.out.print(usertype);
	        } else {
	            return new ResponseEntity<>("User type does not match", HttpStatus.BAD_REQUEST);
	        }

	        if (usertype.equals("user")) {
	            Boolean b = BCrypt.checkpw(user.getPassword(), user1.getPassword());
	            if (b) {
	                Map<String, String> tokenMap = new HashMap<>();
	                String token = Jwts.builder()
	                        .setId(user1.getUsername())
	                        .setIssuedAt(new Date())
	                        .signWith(SignatureAlgorithm.HS256, "usersecretkey")
	                        .compact();
	                tokenMap.put("token", token);
	                tokenMap.put("message", "User Successfully logged in");
	                return new ResponseEntity<>(tokenMap, HttpStatus.OK);
	            } else {
	                return new ResponseEntity<>("Invalid password", HttpStatus.BAD_REQUEST);
	            }
	        } else if (usertype.equals("admin")) {
	            if (user1.getName().equals("admin") && user1.getPassword().equals("admin")) {
	                System.out.println(user1);
	                System.out.println(user1.getPassword());
	                Map<String, String> tokenMap = new HashMap<>();
	                String token = Jwts.builder()
	                        .setId(user1.getUsername())
	                        .setIssuedAt(new Date())
	                        .signWith(SignatureAlgorithm.HS256, "adminsecretkey")
	                        .compact();
	                tokenMap.put("token", token);
	                tokenMap.put("message", "Admin Successfully logged in");
	                return new ResponseEntity<>(tokenMap, HttpStatus.OK);
	            } else {
	                return new ResponseEntity<>("Invalid admin credentials", HttpStatus.UNAUTHORIZED);
	            }
	        } else {
	            return new ResponseEntity<>("Invalid user type", HttpStatus.BAD_REQUEST);
	        }
	    } else {
	        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
	    }
	}

}
