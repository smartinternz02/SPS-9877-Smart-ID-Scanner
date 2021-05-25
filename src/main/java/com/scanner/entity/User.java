package com.scanner.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int userid;
	private String name;
	private String email;
	private String password;
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "user")
	private List<ImageClass> images;
	private String role;
	private String about;
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public List<ImageClass> getImages() {
		return images;
	}
	public void setImages(List<ImageClass> images) {
		this.images = images;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public User() {
		
	}
	public User(int userid, String name, String email, String password) {
		
		this.userid = userid;
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
