package com.scanner.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ImageClass {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int imgid;
	private String imgurl;
	
	private String text;
	private String about;
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@ManyToOne
	private User user;
	public ImageClass() {
		
	}
	public ImageClass(int imgid, String imgurl, String text,String about) {
		
		this.imgid = imgid;
		this.imgurl = imgurl;
		this.text = text;
		this.about=about;
	}
	public int getImgid() {
		return imgid;
	}
	public void setImgid(int imgid) {
		this.imgid = imgid;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

}
