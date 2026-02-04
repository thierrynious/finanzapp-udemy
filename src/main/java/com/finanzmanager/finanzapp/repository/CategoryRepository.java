package com.finanzmanager.finanzapp.repository;

import com.finanzmanager.finanzapp.model.Category;
import com.finanzmanager.finanzapp.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    //Query-Methode
    List<Category> findByType(CategoryType type);

    //Query mit JPQL
    @Query("select c from Category c where c.name like %:keyword%")
    List<Category> searchByName(@Param("keyword") String keyword);

}
