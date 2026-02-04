package com.finanzmanager.finanzapp.config;

import com.finanzmanager.finanzapp.model.Category;
import com.finanzmanager.finanzapp.model.CategoryType;
import com.finanzmanager.finanzapp.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoData {

    @Bean
    CommandLineRunner loadSampleData(CategoryRepository categoryRepository) {
        return args -> {
            Category c = new Category();
            c.setName("Lebensmittel");
            c.setType(CategoryType.AUSGABE);
            categoryRepository.save(c);

            System.out.println("Beispiel-Kategorie gespeichert");
        };
    }
}
