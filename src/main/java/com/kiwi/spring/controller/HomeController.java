package com.kiwi.spring.controller;

import java.sql.Timestamp;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kiwi.spring.dao.UserDAO;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.models.UserBaseController;
import com.kiwi.spring.util.Encrypter;
import com.kiwi.spring.config.Configuration;

@Controller
public class HomeController extends UserBaseController {    
    
    @Autowired
    UserDAO userDAO;
    
	@Autowired
    Configuration env;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String homePage() {
    	System.out.println("Home Page called...............");
       return "login";
    }
    
    @RequestMapping(value = "/enc", method = RequestMethod.GET)
    public String aesEnc() {
    	
       return "AESTest";
    }
    
    @RequestMapping(value = "/encrypt", method = RequestMethod.GET)
    public String aesEncDecr() {
    	
       return "encrypt";
    }
    
    @RequestMapping(value = "/decrypt", method = RequestMethod.GET)
    public String aesDecr() {
    	
       return "decrypt";
    }
    
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
    	
       return "404";
    }
    
    @RequestMapping(value = "/encTest", method = RequestMethod.POST)
    public String aesEncTest(@RequestParam("t1") String t1,
							 @RequestParam("b1") String b1, 
							 Model model) throws Exception {
    	
    	String result = null;
    	
    	String key = env.get("AES_KEY");

    	if( b1.equals("Encrypt")){
    		result = Encrypter.encrypt(t1, key);
    	}
    	else if(b1.equals("Decrypt")){
    		result = Encrypter.decrypt(t1, key);
    	}
		
    	model.addAttribute("text", result);
       return "AESTest";
    }
    
    @ResponseBody
    @RequestMapping(value = "/encrypt", method = RequestMethod.POST)
    public String aesEncrypt(@RequestParam("t1") String t1,
							 Model model) throws Exception {
    	
    	String result = null;
    	String key = env.get("AES_KEY");

    		result = Encrypter.encrypt(t1, key);
       return result;
    }
    
    @ResponseBody
    @RequestMapping(value = "/decrypt", method = RequestMethod.POST)
    public String aesDecrypt(@RequestParam("t1") String t1,
							 Model model) throws Exception {
    	
    	String result = null;
    	String key = env.get("AES_KEY");

    		result = Encrypter.decrypt(t1, key);
       return result;
    }
    
    @RequestMapping(value = "/newuser", method = RequestMethod.GET)
    public String newUser() {
    	System.out.println("New User Registraion Page called...............");
       return "register";
    }
    
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signUp(HttpServletRequest request,
            				@RequestParam(required = true) String username,
            				@RequestParam(required = true) String password,
            				Model model) {
    	System.out.println("User Signed Up called...............");
    	User usr = new User();
    	//usr.setAdvisor_id(1l);
    	usr.setUsername(username);
    	usr.setPassword(password);
    	usr.setEmail(null);
    	usr.setPassword_reset(1l);
    	usr.setRole_id(1l);
    	usr.setStatus_id(1l);
    	usr.setCreated_date(new Timestamp(new Date().getTime()).toString());
    	usr.setUpdated_date(new Timestamp(new Date().getTime()).toString());
    	
    	long userId = userDAO.save(usr);
    	if(userId != 0){
    		request.getSession().setAttribute("user_id", userId);
        	model.addAttribute("msg", "Your account has been created!");
        	return "login";
    	}
    	
    	model.addAttribute("msg", "There is some problem!");
       return "register";
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request,
            				@RequestParam(required = true) String username,
            				@RequestParam(required = true) String password,
            				Model model) {
    	System.out.println("User Logged In called...............");
    	User usr = loginByCredentials(username, password);
    	if(usr != null)
    	{
        	model.addAttribute("msg", "Welcome "+username);
    		return "home";
    	}
    	model.addAttribute("msg", "Username or Password is wrong!");
       return "login";
    }
}