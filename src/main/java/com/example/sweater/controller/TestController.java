package com.example.sweater.controller;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.domain.Views;
import com.example.sweater.repos.MessageRepo;
import com.example.sweater.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@RestController
public class TestController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private EntityManager entityManager;

    @GetMapping("/mainList")
    @JsonView(Views.Base.class)
    List<Message> mainList() {
        return (List<Message>) messageRepo.findAll();
    }

    @GetMapping("/hibernate")
    public List<Message> hibernate(@AuthenticationPrincipal User user) throws InterruptedException {
        List<Message> list= entityManager.createQuery("select m.text from message m").getResultList();
        return list;
    }

    @GetMapping("/async")
    public CompletableFuture<List<Message>> async(@AuthenticationPrincipal User user) throws InterruptedException {
        Thread.sleep(5000);
        userService.loadUserByUsername(user.getUsername());
        userService.getSubscribersId(user);
        List<Message> list= entityManager.createQuery("select m.text from message m").getResultList();
        return CompletableFuture.completedFuture(list);
    }
}
