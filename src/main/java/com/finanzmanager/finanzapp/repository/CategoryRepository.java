package com.finanzmanager.finanzapp.repository;

import com.finanzmanager.finanzapp.model.Category;
import com.finanzmanager.finanzapp.model.CategoryType;
import com.finanzmanager.finanzapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);

    Optional<Category> findByIdAndUser(Long id, User user);

    List<Category> findByUserAndType(User user, CategoryType type);

    List<Category> findByUserAndNameContainingIgnoreCase(User user, String keyword);

    boolean existsByUserAndNameIgnoreCaseAndType(User user, String name, CategoryType type);
}