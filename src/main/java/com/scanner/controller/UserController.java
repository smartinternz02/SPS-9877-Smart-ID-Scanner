package com.scanner.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.scanner.Message;
import com.scanner.entity.ImageClass;
import com.scanner.entity.User;
import com.scanner.repository.ImageRepo;
import com.scanner.repository.UserRepository;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;


@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository sr;
	@Autowired
	private ImageRepo ir;
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@ModelAttribute
	public void addCommonData(Model m,Principal principal) {

		String name = principal.getName();
		User user = sr.getUserByUserName(name);
		m.addAttribute("user", user);
	}
	//dash-board home
		@RequestMapping("/index")
		public String dashboard(Model m,Principal principal,HttpSession session)
		{
//			if(session.getAttribute("validkey")==null && principal==null)
//			{
//				return "redirect:/user/inputkey";
//			}
//			else
//			{
//			String about=sr.getUserByUserName(principal.getName()).getAbout();
//			try
//			{
//				String key=(String)session.getAttribute("key");
//				StrongTextEncryptor st = new StrongTextEncryptor();
//				st.setPassword(key);
//				st.decrypt(about);
//				
//				m.addAttribute("confirm", null);
//				session.setAttribute("key",key);
//				session.setAttribute("validkey", "yes");
//			}
//			catch(Exception e) {
//				session.setAttribute("validkey", null);
//			}
//			
			m.addAttribute("title","User Dashboard-Smart Contact Manager");
			return "user/user_dashboard";
			}
//		}
		@RequestMapping("/inputkey")
		public String inputKeyValidation()
		{
			return "user/input";
		}
		@RequestMapping("/keyprocess")
		public String processKey(@RequestParam("secretkey")String key,Principal p,HttpSession session)
		{
			StrongTextEncryptor st= new StrongTextEncryptor();
			try
			{
				String about=sr.getUserByUserName(p.getName()).getAbout();
				st.setPassword(key);
				st.decrypt(about);
				session.setAttribute("key", key);
				session.setAttribute("validkey", true);
			}
			catch(Exception e )
			{
				session.setAttribute("validkey", false);
			}
			return "user/user_dashboard";
		}
		@RequestMapping("/addcontact")
		public String openAddContact(Model m) {
			m.addAttribute("title", "Add Contact -Smart Contact Manager");
			m.addAttribute("image", new ImageClass());
			return "user/addcontact";
		}
		@PostMapping("/process-contact")
		public String processContact(@ModelAttribute ImageClass img,@RequestParam("profileimg")MultipartFile file,Principal principal,Model model,
				HttpSession session) throws TesseractException, IllegalStateException, IOException {
			try {
				String name=principal.getName();
				User user = sr.getUserByUserName(name);				
				//processing and uploading file
				if(file.isEmpty()) {
					model.addAttribute("file", "empty");
					return "user/addcontact";
				}
				else {
					System.out.println(1);
					img.setImgurl(img.getImgid()+"_"+user.getUserid()+file.getOriginalFilename());
					System.out.println(1);
					//File savefFile = new ClassPathResource("static/img").getFile();
					ClassPathResource c=new ClassPathResource("static/img");
					File savefFile=c.getFile();
					System.out.println(2);
					   Path path1 = Paths
					     .get(savefFile.getAbsolutePath() + File.separator + img.getImgid()+"_"+user.getUserid()+file.getOriginalFilename());
					   Files.copy(file.getInputStream(), path1, StandardCopyOption.REPLACE_EXISTING);
		
			       // FileUploadUtil.saveFile(uploadDir, fileName, file);
				Tesseract tesseract=new Tesseract();
				tesseract.setDatapath("src/main/resources/tessdata");
				tesseract.setLanguage("eng");
					tesseract.setPageSegMode(1);
					tesseract.setOcrEngineMode(1);
					String text = tesseract.doOCR(new File(savefFile.getAbsolutePath() + File.separator + img.getImgid()+"_"+user.getUserid()+file.getOriginalFilename()));
					System.out.println(text);
					img.setText(text);
					img.setUser(user);
					user.getImages().add(img);
				sr.save(user);
				}
				session.setAttribute("message", new Message("Image added successfully", "success"));
			} catch (Exception e) {
				session.setAttribute("message", new Message("Something went wrong!!Try again", "danger"));
				e.printStackTrace();				
			}
			model.addAttribute("file", "");
			return "user/addcontact";
			}		
		@RequestMapping("/showcontacts/{page}")
		public String showContacts(Model m,Principal principal,@PathVariable("page") int page) {
			m.addAttribute("title", "View Your Contact -Smart Contact Manager");
					
					//contact retrive
					String name = principal.getName();
					User user = sr.getUserByUserName(name);
					//List<Contact> contacts = user.getContacts();
					
					int size=user.getImages().size();
					if(size==0)
					{
						m.addAttribute("count", new Message("Sorry..U don't have any contacts to show..Add some contacts","danger"));
						return "/user/showContacts";
					}
					
					Pageable pageable = PageRequest.of(page, 8);
					Page<ImageClass> list = ir.findByUser(user.getUserid(),pageable);
					m.addAttribute("con", list);
					m.addAttribute("currentpage", page);
					m.addAttribute("total", list.getTotalPages());
					m.addAttribute("count", null);
			return "user/showContacts";
		}
		@RequestMapping("/delete/{id}")
		public String delete(@PathVariable("id") int id,Principal principal,HttpSession session) {
			
			String name = principal.getName();
			User user = sr.getUserByUserName(name);
			Optional<ImageClass> byId = ir.findById(id);
			ImageClass img = byId.get();
			if(user.getUserid()==img.getUser().getUserid())       
			{
				ir.deleteById(id);
				session.setAttribute("message", new Message("Contact deleted successfully", "success"));
				return "redirect:/user/showcontacts/0";
			}
			return "user/showContacts";
		}
		@RequestMapping("/profile")
		public String userProfile(Model model,Principal principal) {
			
			User user = sr.getUserByUserName(principal.getName());
			
			model.addAttribute("title", "Your Profile- Smart Contact Manager ");
			
			model.addAttribute("user", user);
			return "user/profile";
		}
		//open settings controller
		@RequestMapping("/setting")
		public String openSetting(Model m) 
		{
			
			m.addAttribute("title", "Setting - Smart Contact Manager");
			
			return "user/setting";
		}
		//change password handeler
		@PostMapping("/changePassword")
		public String changePassword(Principal principal,@RequestParam("oldPassword")String old,@RequestParam("newPassword")String newpass,
		@RequestParam("confirm")String confirm,Model m,HttpSession session) {
			
			String name = principal.getName();
			User user = sr.getUserByUserName(name);
			String act=user.getPassword();
		
			if(encoder.matches(old, act)) {
				//change password
				if(newpass.equals(confirm)) {
				user.setPassword(encoder.encode(newpass));
				sr.save(user);
				m.addAttribute("confirm", new Message("Password changed successfully!!", "alert-success"));
				session.setAttribute("equal", null);
				}
				else{
					session.setAttribute("equal", new Message("Entered password and confirm password didn't match ", "alert-danger"));
					return "redirect:/user/setting";
				}
			}
			else{
				m.addAttribute("confirm", new Message("Old password didn't match","alert-danger"));
				return "user/setting";
			}
			
			return "user/user_dashboard";
		}
}
