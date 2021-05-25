package com.scanner.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scanner.Message;
import com.scanner.entity.User;
import com.scanner.repository.UserRepository;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder encoder;
	@Autowired
	private UserRepository sr;
	@Autowired
	private JavaMailSender jms;
	
	@RequestMapping(value = {"/","home"})
	public String test(Model m,HttpSession session)
	{
		m.addAttribute("title","Home- Smart Contact Manager");
		return "home";
	}
	@RequestMapping("/about")
	public String about(Model m)
	{
		m.addAttribute("title","About- Smart Contact Manager");
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("title","Register - Smart Contact Manager") ;
		m.addAttribute("user", new User());
		return "signup";
	}
	@PostMapping("/do_register")
	public String register( @ModelAttribute("user") User user,@RequestParam(value = "agreement",defaultValue = "false") boolean agreement,@RequestParam("secretkey")String key,Model model,HttpSession session)
	{
		String email=user.getEmail();
		User user2 = sr.findByEmail(email);
		
		try {
			if(user2!=null)
			{
				throw new Exception("You have already registered");
			}
			else
			{
			if (!agreement) {
				throw new Exception("You have not agreed to the terms and conditions");
			}
			
			StrongTextEncryptor st = new StrongTextEncryptor();
			st.setPassword(key);
			String about=st.encrypt(user.getAbout());
			
			user.setRole("ROLE_USER");
			
			
			user.setPassword(encoder.encode(user.getPassword()));
			user.setAbout(about);
			System.out.println(user);
			 this.sr.save(user);
			
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Registered successfully!!","alert-success"));
			return "signup";
			
		} 
		}
		catch (Exception e) {
			
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong!!"+e.getMessage(),"alert-danger"));
			return "signup";
		}
		
		
	}
	@RequestMapping("/signin")
	public String login(Model m,HttpSession session)
	{
		m.addAttribute("title", "Login-Smart Contact Management");
		session.setAttribute("exi", null);
		m.addAttribute("passwordsuccess", null);
		return "signin";
	}
	@RequestMapping("/forgotpass")
	public String forgotPassword(Model m) {
		m.addAttribute("title", "Forgot password-Smart Contact Management ");
		return "forgotPassword";
	}
	@PostMapping("/sendOtp")
	public String sendOtp(@RequestParam("mailid")String mail,Model m,HttpSession session){
		m.addAttribute("title", "Forgot password-Smart Contact Management ");
		m.addAttribute("error", null);
		User user = sr.getUserByUserName(mail);
		if(user!=null) {
			Random r = new Random();
			String otp="";
			for(int i=1;i<=6;i++) {
				otp+=r.nextInt(9);
			}		
			
			SimpleMailMessage smm= new SimpleMailMessage();
			smm.setTo(mail);
			smm.setText("<h1>Your One Time Password is </h1>"+"Yout One Time Password is "+otp);
			smm.setSubject("One Time Password from SMC");
			try {
				jms.send(smm);
			}
			catch(MailSendException me) {
				m.addAttribute("error", new Message("Couldn't be able to send mail", "alert-danger"));
				return "forgotPassword";
			}
			session.setAttribute("otp", otp);
			session.setAttribute("exi", null);
			m.addAttribute("success", new Message("OTP sent to your registered mail id", "alert-success"));
			session.setAttribute("currentlog", mail);
			return "inputOtp";
		}
		else {
			session.setAttribute("exi", new Message("Email id is not registered.Sign up first","alert-danger"));
			return "redirect:/forgotpass";
		}
		
	}
	@PostMapping("/verifyOtp")
	public String verifyOtp(@RequestParam("otp")String otp,HttpSession session,Model m)
	{
		m.addAttribute("title", "Forgot password-Smart Contact Management ");
		m.addAttribute("match", null);
		if(session.getAttribute("otp").equals(otp))
		{
			return "changePass";
		}
		else {
			m.addAttribute("match", new Message("OTP didn't match", "alert-danger"));
			return "inputOtp";
		}
	}
	@PostMapping("/chngpass")
	public String changePassword(@RequestParam("password")String pass,HttpSession session,Model m) {
		String mail=(String) session.getAttribute("currentlog");
		User user = sr.getUserByUserName(mail);
		String encode = encoder.encode(pass);
		user.setPassword(encode);
		sr.save(user);
		m.addAttribute("passwordsuccess", new Message("Password changed successfully", "alert-success"));
		return "signin";
	}
	
}
