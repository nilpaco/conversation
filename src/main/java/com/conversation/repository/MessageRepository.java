package com.conversation.repository;

import com.conversation.domain.Message;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Message entity.
 */
public interface MessageRepository extends JpaRepository<Message,Long> {

    @Query("select message from Message message where message.user.login = ?#{principal.username}")
    List<Message> findByUserIsCurrentUser();

    @Query("select message from Message message where message.conversation.id = :id")
    List<Message> findByConversation(@Param("id") Long id);


}
