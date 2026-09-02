package com.example.recipe;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {

    private final RecipeRepository recipeRepository;
    private final AppUserRepository userRepository;

    public RecipeController(RecipeRepository recipeRepository, AppUserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/new")
    public ResponseEntity<RecipeId> saveRecipe(
            @Valid @RequestBody Recipe recipe,
            Authentication authentication) {
        AppUser author = userRepository.findByEmailIgnoreCase(authentication.getName()).orElseThrow();
        recipe.setAuthor(author);
        Recipe savedRecipe = recipeRepository.save(recipe);
        return ResponseEntity.ok(new RecipeId(savedRecipe.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable long id) {
        return recipeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRecipe(
            @PathVariable long id,
            @Valid @RequestBody Recipe recipe,
            Authentication authentication) {
        return recipeRepository.findById(id)
                .map(existingRecipe -> {
                    if (!isAuthor(existingRecipe, authentication)) {
                        return ResponseEntity.status(403).<Void>build();
                    }
                    existingRecipe.setName(recipe.getName());
                    existingRecipe.setCategory(recipe.getCategory());
                    existingRecipe.setDescription(recipe.getDescription());
                    existingRecipe.setIngredients(recipe.getIngredients());
                    existingRecipe.setDirections(recipe.getDirections());
                    recipeRepository.save(existingRecipe);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id, Authentication authentication) {
        return recipeRepository.findById(id)
                .map(recipe -> {
                    if (!isAuthor(recipe, authentication)) {
                        return ResponseEntity.status(403).<Void>build();
                    }
                    recipeRepository.delete(recipe);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam Map<String, String> parameters) {
        if (parameters.size() != 1
                || (!parameters.containsKey("category") && !parameters.containsKey("name"))) {
            return ResponseEntity.badRequest().build();
        }

        if (parameters.containsKey("category")) {
            String category = parameters.get("category");
            if (category.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(recipeRepository.findByCategoryIgnoreCaseOrderByDateDesc(category));
        }

        String name = parameters.get("name");
        if (name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(recipeRepository.findByNameContainingIgnoreCaseOrderByDateDesc(name));
    }

    private boolean isAuthor(Recipe recipe, Authentication authentication) {
        return recipe.getAuthor() != null
                && recipe.getAuthor().getEmail().equalsIgnoreCase(authentication.getName());
    }
}
