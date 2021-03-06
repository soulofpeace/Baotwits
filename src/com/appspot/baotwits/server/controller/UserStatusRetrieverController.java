package com.appspot.baotwits.server.controller;

import java.util.logging.Logger;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.appspot.baotwits.server.data.dao.TwitUserDao;
import com.google.appengine.api.users.User;


@Controller
@RequestMapping("user")
public class UserStatusRetrieverController{

	private static final Logger logger = Logger.getLogger(UserStatusRetrieverController.class.getName());
	@Autowired
	private TwitUserDao twitUserDao;
	
	@RequestMapping(value="{userId}" ,method=RequestMethod.GET)
	public String getStatuses(@PathVariable String userId, Model model){
		logger.info("Userid is "+userId);
		if(twitUserDao.getTwitUser(new User(userId+"@gmail.com", "gmail.com")) ==null){
			return "NotFound";
		}
		else{
			model.addAttribute("userId", userId);
			return "User";
		}
		
	}

	public void setTwitUserDao(TwitUserDao twitUserDao) {
		this.twitUserDao = twitUserDao;
	}

	public TwitUserDao getTwitUserDao() {
		return twitUserDao;
	}
	
}
