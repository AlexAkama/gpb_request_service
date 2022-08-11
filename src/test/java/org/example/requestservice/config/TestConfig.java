package org.example.requestservice.config;

import org.example.requestservice.repositories.TagRepo;
import org.example.requestservice.services.TagServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {

    @Bean
    public TagRepo tagRepo() {
        return mock(TagRepo.class);
    }

    @Bean
    public TagServiceImpl tagService() {
        return new TagServiceImpl(tagRepo());
    }

}
