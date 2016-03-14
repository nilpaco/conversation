package com.conversation.web.rest;

import com.conversation.Application;
import com.conversation.domain.Space;
import com.conversation.repository.SpaceRepository;
import com.conversation.repository.search.SpaceSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the SpaceResource REST controller.
 *
 * @see SpaceResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class SpaceResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private SpaceRepository spaceRepository;

    @Inject
    private SpaceSearchRepository spaceSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSpaceMockMvc;

    private Space space;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SpaceResource spaceResource = new SpaceResource();
        ReflectionTestUtils.setField(spaceResource, "spaceSearchRepository", spaceSearchRepository);
        ReflectionTestUtils.setField(spaceResource, "spaceRepository", spaceRepository);
        this.restSpaceMockMvc = MockMvcBuilders.standaloneSetup(spaceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        space = new Space();
        space.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createSpace() throws Exception {
        int databaseSizeBeforeCreate = spaceRepository.findAll().size();

        // Create the Space

        restSpaceMockMvc.perform(post("/api/spaces")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(space)))
                .andExpect(status().isCreated());

        // Validate the Space in the database
        List<Space> spaces = spaceRepository.findAll();
        assertThat(spaces).hasSize(databaseSizeBeforeCreate + 1);
        Space testSpace = spaces.get(spaces.size() - 1);
        assertThat(testSpace.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void getAllSpaces() throws Exception {
        // Initialize the database
        spaceRepository.saveAndFlush(space);

        // Get all the spaces
        restSpaceMockMvc.perform(get("/api/spaces?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(space.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getSpace() throws Exception {
        // Initialize the database
        spaceRepository.saveAndFlush(space);

        // Get the space
        restSpaceMockMvc.perform(get("/api/spaces/{id}", space.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(space.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSpace() throws Exception {
        // Get the space
        restSpaceMockMvc.perform(get("/api/spaces/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSpace() throws Exception {
        // Initialize the database
        spaceRepository.saveAndFlush(space);

		int databaseSizeBeforeUpdate = spaceRepository.findAll().size();

        // Update the space
        space.setName(UPDATED_NAME);

        restSpaceMockMvc.perform(put("/api/spaces")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(space)))
                .andExpect(status().isOk());

        // Validate the Space in the database
        List<Space> spaces = spaceRepository.findAll();
        assertThat(spaces).hasSize(databaseSizeBeforeUpdate);
        Space testSpace = spaces.get(spaces.size() - 1);
        assertThat(testSpace.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteSpace() throws Exception {
        // Initialize the database
        spaceRepository.saveAndFlush(space);

		int databaseSizeBeforeDelete = spaceRepository.findAll().size();

        // Get the space
        restSpaceMockMvc.perform(delete("/api/spaces/{id}", space.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Space> spaces = spaceRepository.findAll();
        assertThat(spaces).hasSize(databaseSizeBeforeDelete - 1);
    }
}
