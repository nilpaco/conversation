package com.conversation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.time.LocalDate;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Message.
 */
@Entity
@Table(name = "message")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "message")
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "text")
    private String text;
    
    @Column(name = "time")
    private LocalDate time;
    
    @OneToOne
    private Message messagePrevious;

    @OneToOne(mappedBy = "messagePrevious")
    @JsonIgnore
    private Message messagePrevious2;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getTime() {
        return time;
    }
    
    public void setTime(LocalDate time) {
        this.time = time;
    }

    public Message getMessagePrevious() {
        return messagePrevious;
    }

    public void setMessagePrevious(Message message) {
        this.messagePrevious = message;
    }

    public Message getMessagePrevious2() {
        return messagePrevious2;
    }

    public void setMessagePrevious2(Message message) {
        this.messagePrevious2 = message;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        if(message.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Message{" +
            "id=" + id +
            ", text='" + text + "'" +
            ", time='" + time + "'" +
            '}';
    }
}
