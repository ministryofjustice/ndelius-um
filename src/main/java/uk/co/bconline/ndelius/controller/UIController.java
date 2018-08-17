package uk.co.bconline.ndelius.controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class UIController
{
	@GetMapping({"/search", "/user", "/user/**", "/migrate"})
	public ModelAndView redirect(ModelMap model)
	{
		model.addAttribute("attribute", "forwardWithForwardPrefix");
		return new ModelAndView("forward:/", model);
	}
}