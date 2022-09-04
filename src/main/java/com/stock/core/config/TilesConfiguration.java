package com.stock.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

@Configuration
public class TilesConfiguration {
    @Bean(name = "tilesConfigurer")
    public TilesConfigurer tilesConfigurer() {
        final TilesConfigurer configurer = new TilesConfigurer();

        configurer.setDefinitions(new String[] {"/WEB-INF/tiles/tiles.xml"});
        configurer.setCheckRefresh(true);

        return configurer;
    }

    @Bean(name = "tilesViewResolver")
    public TilesViewResolver tilesViewResolver() {
        TilesViewResolver resolver = new TilesViewResolver();

        resolver.setViewClass(TilesView.class);
        resolver.setOrder(1);

        return resolver;
    }
}
