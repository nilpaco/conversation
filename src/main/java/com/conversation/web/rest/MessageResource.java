package com.conversation.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.conversation.domain.Conversation;
import com.conversation.domain.Message;
import com.conversation.domain.Space;
import com.conversation.domain.User;
import com.conversation.repository.ConversationRepository;
import com.conversation.repository.MessageRepository;
import com.conversation.repository.SpaceRepository;
import com.conversation.repository.UserRepository;
import com.conversation.repository.search.MessageSearchRepository;
import com.conversation.security.SecurityUtils;
import com.conversation.web.rest.util.HeaderUtil;
import com.conversation.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Message.
 */
@RestController
@RequestMapping("/api")
public class MessageResource {

    private final Logger log = LoggerFactory.getLogger(MessageResource.class);

    @Inject
    private MessageRepository messageRepository;

    @Inject
    private MessageSearchRepository messageSearchRepository;

    @Inject
    private ConversationRepository conversationRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private SpaceRepository spaceRepository;

    /**
     * POST  /messages -> Create a new message.
     */
    @RequestMapping(value = "/messages",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Message> createMessage(@RequestBody Message message) throws URISyntaxException {
        log.debug("REST request to save Message : {}", message);
        if (message.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("message", "idexists", "A new message cannot already have an ID")).body(null);
        }
        Message result = messageRepository.save(message);
        messageSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/messages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("message", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /messages -> Updates an existing message.
     */
    @RequestMapping(value = "/messages",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Message> updateMessage(@RequestBody Message message) throws URISyntaxException {
        log.debug("REST request to update Message : {}", message);
        if (message.getId() == null) {
            return createMessage(message);
        }
        Message result = messageRepository.save(message);
        messageSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("message", message.getId().toString()))
            .body(result);
    }

    /**
     * GET  /messages -> get all the messages.
     */
    @RequestMapping(value = "/messages",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Message>> getAllMessages(Pageable pageable, @RequestParam(required = false) String filter)
        throws URISyntaxException {
        if ("messageprevious2-is-null".equals(filter)) {
            log.debug("REST request to get all Messages where messagePrevious2 is null");
            return new ResponseEntity<>(StreamSupport
                .stream(messageRepository.findAll().spliterator(), false)
                .filter(message -> message.getMessagePrevious2() == null)
                .collect(Collectors.toList()), HttpStatus.OK);
        }
        log.debug("REST request to get a page of Messages");
        Page<Message> page = messageRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/messages");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /messages/:id -> get the "id" message.
     */
    @RequestMapping(value = "/messages/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Message> getMessage(@PathVariable Long id) {
        log.debug("REST request to get Message : {}", id);
        Message message = messageRepository.findOne(id);
        return Optional.ofNullable(message)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /messages/:id -> delete the "id" message.
     */
    @RequestMapping(value = "/messages/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        log.debug("REST request to delete Message : {}", id);
        messageRepository.delete(id);
        messageSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("message", id.toString())).build();
    }

    /**
     * SEARCH  /_search/messages/:query -> search for the message corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/messages/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Message> searchMessages(@PathVariable String query) {
        log.debug("REST request to search Messages for query {}", query);
        return StreamSupport
            .stream(messageSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

    /**
     * POST  /messages -> Create a new message and conversation.
     */
    @RequestMapping(value = "/space/{id}/message",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Message> createMessageAndConversation(@PathVariable Long id, @RequestBody Message message) throws URISyntaxException {

        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        Space space = spaceRepository.findOne(id);

        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setSpace(space);
        conversation.setName(space.getName());

        Conversation result1 = conversationRepository.save(conversation);

        message.setConversation(conversation);
        message.setUser(user);
        Message result = messageRepository.save(message);

        messageSearchRepository.save(result);

        return ResponseEntity.created(new URI("/api/space/" + result1.getId() +"/message/"))
            .headers(HeaderUtil.createEntityCreationAlert("message", result.getId().toString()))
            .body(result);
    }

    /**
     * POST  /messages -> Create a new message.
     */
    @RequestMapping(value = "/conversation/{id}/messages",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Message> createMessageFromConversation(@PathVariable Long id, @RequestBody Message message) throws URISyntaxException {
        Conversation conversation = conversationRepository.findOne(id);
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        log.debug("REST request to save Message : {}", message);
        if (message.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("message", "idexists", "A new message cannot already have an ID")).body(null);
        }
        message.setConversation(conversation);
        message.setUser(user);
        Message result = messageRepository.save(message);
        messageSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/messages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("message", result.getId().toString()))
            .body(result);
    }


}
