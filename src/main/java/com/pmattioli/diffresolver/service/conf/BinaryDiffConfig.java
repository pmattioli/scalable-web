package com.pmattioli.diffresolver.service.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pmattioli.diffresolver.service.DiffResolverService;
import com.pmattioli.diffresolver.service.impl.SimpleDiffResolverService;

@Configuration
public class BinaryDiffConfig {

    @Bean
    public DiffResolverService diffResolver(){
        return new SimpleDiffResolverService();
    }

}
