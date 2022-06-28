package com.example.sweater.controller;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.Views;
import com.example.sweater.repos.MessageRepo;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.sweater.domain.User;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.*;
import javax.validation.valueextraction.ValueExtractor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

@Controller
public class MainController {


    @Autowired
    private MessageRepo messageRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting() {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(Model model, @AuthenticationPrincipal User user) {
        List<Message> messages = (List<Message>) messageRepo.findAll();
        messages.sort(Comparator.comparing(x -> x.getId()));
        model.addAttribute("userId", user.getId());
        model.addAttribute("messages", messages);
        model.addAttribute("flag", "true");
        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file,
            @ModelAttribute("newMessage") @Valid Message message,
            BindingResult bindingResult,
            Model model) throws IOException, InterruptedException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("messages", messageRepo.findAll());
            return "main";
        }

        if (file != null && !file.getOriginalFilename().isEmpty()) {
                     /*File uploadDir = new File(uploadPath);

                    if (!uploadDir.exists()) {
                        uploadDir.mkdir();
                    }
                    String uuidFile = UUID.randomUUID().toString();*/
                    String resultFilename = file.getOriginalFilename();
                    file.transferTo(new File(uploadPath + "/" + resultFilename));
                    message.setFilename(resultFilename);
                }

        message.setUser(user);
        messageRepo.save(message);

        List<Message> messages =(List<Message>) messageRepo.findAll();
        messages.sort(Comparator.comparing(x -> x.getId()));

        model.addAttribute("messages", messages);
        model.addAttribute("userId", user.getId());
        model.addAttribute("flag", "true");
        return "main";
    }

    @PostMapping("filter")
    public String filter(@RequestParam String filter,@AuthenticationPrincipal User user, Model model) {
        List<Message> messages;

        if (filter != null && !filter.isEmpty()) {
            messages = messageRepo.findByTag(filter);
        } else {
            messages = (List<Message>) messageRepo.findAll();
        }
        messages.sort(Comparator.comparing (x -> x.getId()));
        model.addAttribute("userId", user.getId());
        model.addAttribute("messages", messages);
        model.addAttribute("flag", "true");

        return "main";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @Transactional
    public String delete(@RequestParam String id, Model model, @AuthenticationPrincipal User user) {

        messageRepo.deleteById(Integer.parseInt(id));

        List<Message> messages =(List<Message>) messageRepo.findAll();
        messages.sort(Comparator.comparing(x -> x.getId()));

        model.addAttribute("userId", user.getId());
        model.addAttribute("messages", messages);
        model.addAttribute("flag", "true");
        return "main";
    }
    @ModelAttribute("newMessage")
    public Message setMessage(){
        return new Message();
    }


    @PostMapping("/editMessage/{id}")
    public String editMessage(Model model, @AuthenticationPrincipal User user, @PathVariable int id){
        List<Message> messages = (List<Message>) messageRepo.findAll();
        messages.sort(Comparator.comparing(x -> x.getId()));

        model.addAttribute("messageId", id);
        model.addAttribute("userId", user.getId());
        model.addAttribute("messages", messages);
        model.addAttribute("flag", "false");

        return "main";
    }

    @Transactional
    @PostMapping("/editedMessage/{id}")
    public String editedMessage(Model model, @AuthenticationPrincipal User user, @PathVariable int id, @RequestParam String text){

        List<Message> messages = (List<Message>) messageRepo.findAll();
        messages.sort(Comparator.comparing(x -> x.getId()));

        Message message = messageRepo.findById(id);
        message.setText(text);
        messageRepo.save(message);

        model.addAttribute("userId", user.getId());
        model.addAttribute("messages", messages);
        model.addAttribute("flag", "true");
        return "main";
    }
}
