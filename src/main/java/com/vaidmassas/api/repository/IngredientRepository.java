package com.vaidmassas.api.repository;

import com.vaidmassas.api.domain.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    List<Ingredient> findByActiveTrue();

    boolean existsByNameIgnoreCase(String name);
}