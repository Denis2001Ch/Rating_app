package com.example.sweater.repos;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import org.springframework.data.repository.CrudRepository;


import javax.transaction.Transactional;
import java.util.List;

public interface MessageRepo extends CrudRepository<Message, Long> {
    Message findById(int id);
    List<Message> findByTag(String tag);
    void deleteById(int id);

}
