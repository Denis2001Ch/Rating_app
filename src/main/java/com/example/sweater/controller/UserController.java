package com.example.sweater.controller;

import com.example.sweater.domain.Role;
import com.example.sweater.domain.User;
import com.example.sweater.repos.UserRepo;
import com.example.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @GetMapping
    public String userList(Model model,  @AuthenticationPrincipal User user) {

        model.addAttribute("mainUser", user);
        model.addAttribute("subscribersIds", userService.getSubscribersId(user));
        model.addAttribute("users", userRepo.findAll());
        return "userList";
    }

    @GetMapping("{id}")
    public String userEditForm(@PathVariable("id") User user, Model model) {

        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());

        return "userEdit";
    }

    @PostMapping("edit")
    public String userSave(
            @RequestParam("userId") User user,
            @RequestParam("roles") String[] rolesForm) {

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : rolesForm) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
        user.getRoles().stream().forEach(x -> x.name() ); //?

        userRepo.save(user);
        return "redirect:/user";
    }

    @GetMapping("/subscribe/{id}")
    public String userSubscribe(
            @AuthenticationPrincipal User myuser,
            @PathVariable("id") User user) {
        userService.subscribe(myuser, user);
        return "redirect:/user";
    }
    @GetMapping("/unsubscribe/{id}")
    public String userUnsubscribe(
            @AuthenticationPrincipal User myuser,
            @PathVariable("id") User user) {
        userService.unsubscribe(myuser, user);
        return "redirect:/user";
    }
    @GetMapping("/showMessage")
    public String showMessage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("messages", userService.showMessages(user));
        return "messages";
    }

    @GetMapping("profile")
    public String getProfile() {
        return "profile";
    }

    @PostMapping("profile")
    public String updateProfile(
            @AuthenticationPrincipal User user,
            @RequestParam String password,
            @RequestParam String email
    ) {
        userService.updateProfile(user, password, email);
        return "redirect:/login";
    }
}