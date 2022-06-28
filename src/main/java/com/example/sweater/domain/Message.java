package com.example.sweater.domain;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "message")
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @NotBlank(message = "Please, fill text")
    @JsonView(Views.Base.class)
    private String text;

    @NotBlank(message = "Please, fill tag")
    @JsonView(Views.Base.class)
    private String tag;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private String filename;

    public User getUser() {
        return user;
    }

    public Message() {
    }

    public Message(String text, String tag, User user) {
        this.text = text;
        this.tag = tag;
        this.user = user;
    }



    public String getText() {
        return text;
    }

    public Integer getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public String getAuthorName(){
        return user != null ? user.getUsername() : "none";
    }

    public String getFilename() {
        return filename != null ? filename : "<none>";
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
