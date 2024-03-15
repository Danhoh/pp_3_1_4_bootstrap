package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.entity.Role;
import ru.kata.spring.boot_security.demo.model.entity.User;
import ru.kata.spring.boot_security.demo.model.entity.validation.CreateValidation;
import ru.kata.spring.boot_security.demo.model.entity.validation.UpdateValidation;
import ru.kata.spring.boot_security.demo.service.data.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String adminPage(@ModelAttribute("user") User user,
                            Model model) {
        model.addAttribute("users", userService.getAllUsers());

        return "admin";
    }

    @GetMapping("/add")
    public String addUserPage(@ModelAttribute("user") User user) {
        return "add-user-form";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute("user") @Validated({CreateValidation.class}) User user,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "add-user-form";
        }
        userService.save(user);

        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute("user") @Validated({UpdateValidation.class}) User user,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "add-user-form";
        }

        User persistedUser = userService.findById(user.getId());
        persistedUser.merge(user);
        userService.save(persistedUser);

//        try {
//            userService.save(persistedUser);
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//            model.addAttribute("user", persistedUser);
//            return "add-user-form";
//        }

        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deletePost(@RequestParam("id") long id,
                             @ModelAttribute("user") User principal) {
        userService.removeById(id);

        return "redirect:/admin";
    }

    @ModelAttribute("roles")
    public Role[] getRoles() {
        return Role.values();
    }
}
