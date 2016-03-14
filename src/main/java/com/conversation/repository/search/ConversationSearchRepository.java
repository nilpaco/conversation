package com.conversation.repository.search;

import com.conversation.domain.Conversation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Conversation entity.
 */
public interface ConversationSearchRepository extends ElasticsearchRepository<Conversation, Long> {
}
