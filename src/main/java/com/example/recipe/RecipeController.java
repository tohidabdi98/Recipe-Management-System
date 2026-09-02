package com.example.recipe;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {

    private final Map<Long, Recipe> recipes = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @PostMapping("/new")
    public ResponseEntity<RecipeId> saveRecipe(@RequestBody Recipe recipe) {
        long id = nextId.getAndIncrement();
        recipes.put(id, recipe);
        return ResponseEntity.ok(new RecipeId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable long id) {
        Recipe recipe = recipes.get(id);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }
}
