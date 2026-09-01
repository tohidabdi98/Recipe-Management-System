package com.example.recipe;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {

    private Recipe currentRecipe = new Recipe("", "", "", "");

    @PostMapping
    public ResponseEntity<Void> saveRecipe(@RequestBody Recipe recipe) {
        currentRecipe = recipe;
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public Recipe getRecipe() {
        return currentRecipe;
    }
}
