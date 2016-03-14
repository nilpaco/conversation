package com.conversation.repository;

import com.conversation.domain.Space;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Space entity.
 */
public interface SpaceRepository extends JpaRepository<Space,Long> {

    @Query("select space from Space space where space.user.login = ?#{principal.username}")
    List<Space> findByUserIsCurrentUser();

}
