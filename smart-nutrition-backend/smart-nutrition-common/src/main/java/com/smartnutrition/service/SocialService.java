package com.smartnutrition.service;

import com.smartnutrition.dto.request.FoodItemDto;
import com.smartnutrition.dto.request.RecipeRequest;
import com.smartnutrition.dto.response.MealResponse;
import com.smartnutrition.dto.response.RecipeResponse;
import com.smartnutrition.dto.response.UserResponse;
import com.smartnutrition.entity.Friendship;
import com.smartnutrition.entity.Meal;
import com.smartnutrition.entity.Recipe;
import com.smartnutrition.entity.User;
import com.smartnutrition.repository.FriendshipRepository;
import com.smartnutrition.repository.MealRepository;
import com.smartnutrition.repository.RecipeRepository;
import com.smartnutrition.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SocialService {

    private final FriendshipRepository friendshipRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final MealRepository mealRepository;

    public SocialService(FriendshipRepository friendshipRepository,
                         RecipeRepository recipeRepository,
                         UserRepository userRepository,
                         MealRepository mealRepository) {
        this.friendshipRepository = friendshipRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
    }

    @Transactional
    public String sendFriendRequest(String currentEmail, Long friendId) {
        User sender = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (sender.getId().equals(friendId)) {
            throw new IllegalArgumentException("You cannot add yourself as a friend");
        }

        User receiver = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("User to add not found with ID: " + friendId));

        Optional<Friendship> existing = friendshipRepository.findRelation(sender.getId(), receiver.getId());
        if (existing.isPresent()) {
            return "Friendship relation already exists with status: " + existing.get().getStatus();
        }

        Friendship friendship = Friendship.builder()
                .user1(sender)
                .user2(receiver)
                .status("PENDING")
                .build();

        friendshipRepository.save(friendship);
        return "Friend request sent successfully";
    }

    @Transactional
    public String acceptFriendRequest(String currentEmail, Long friendshipId) {
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Friendship request not found with ID: " + friendshipId));

        if (!friendship.getUser2().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not the receiver of this friend request");
        }

        friendship.setStatus("ACCEPTED");
        friendshipRepository.save(friendship);
        return "Friend request accepted";
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getFriends(String currentEmail) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Friendship> friendships = friendshipRepository.findFriends(user.getId());
        List<UserResponse> friends = new ArrayList<>();

        for (Friendship f : friendships) {
            User friend = f.getUser1().getId().equals(user.getId()) ? f.getUser2() : f.getUser1();
            friends.add(new UserResponse(friend.getId(), friend.getName(), friend.getEmail(), friend.getRole()));
        }

        return friends;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getIncomingRequests(String currentEmail) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Friendship> pending = friendshipRepository.findIncomingRequests(user.getId());
        return pending.stream()
                .map(f -> new UserResponse(f.getUser1().getId(), f.getUser1().getName(), f.getUser1().getEmail(), f.getUser1().getRole()))
                .toList();
    }

    @Transactional
    public RecipeResponse createRecipe(String currentEmail, RecipeRequest request) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Recipe recipe = Recipe.builder()
                .title(request.title())
                .description(request.description())
                .ingredients(request.ingredients())
                .instructions(request.instructions())
                .createdBy(user)
                .build();

        Recipe savedRecipe = recipeRepository.save(recipe);
        return new RecipeResponse(
                savedRecipe.getId(),
                savedRecipe.getTitle(),
                savedRecipe.getDescription(),
                savedRecipe.getIngredients(),
                savedRecipe.getInstructions(),
                user.getId(),
                user.getName()
        );
    }

    @Transactional(readOnly = true)
    public List<RecipeResponse> getRecipes() {
        List<Recipe> recipes = recipeRepository.findAllByOrderByCreatedAtDesc();
        return recipes.stream()
                .map(r -> new RecipeResponse(
                        r.getId(),
                        r.getTitle(),
                        r.getDescription(),
                        r.getIngredients(),
                        r.getInstructions(),
                        r.getCreatedBy().getId(),
                        r.getCreatedBy().getName()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MealResponse> getFriendsFeed(String currentEmail) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Friendship> friendships = friendshipRepository.findFriends(user.getId());
        List<Long> friendIds = new ArrayList<>();

        for (Friendship f : friendships) {
            friendIds.add(f.getUser1().getId().equals(user.getId()) ? f.getUser2().getId() : f.getUser1().getId());
        }

        if (friendIds.isEmpty()) {
            return List.of();
        }

        List<Meal> meals = mealRepository.findByUploadedByParentIdInOrderByMealDateDesc(friendIds);
        return meals.stream().map(this::mapToMealResponse).toList();
    }

    private MealResponse mapToMealResponse(Meal meal) {
        List<FoodItemDto> itemDtos = meal.getFoodItems().stream().map(i -> new FoodItemDto(
                i.getFoodName(),
                i.getQuantity(),
                i.getCookingNote(),
                i.getCalories(),
                i.getProteinG(),
                i.getCarbsG(),
                i.getFatG(),
                i.getFiberG(),
                i.getSource(),
                i.getConsumptionPercentage()
        )).toList();

        return new MealResponse(
                meal.getId(),
                meal.getStudent().getId(),
                meal.getMealDate(),
                meal.getPreMealImageUrl(),
                meal.getPostMealImageUrl(),
                meal.getBoxLength(),
                meal.getBoxWidth(),
                meal.getBoxHeight(),
                meal.getLunchboxPresetId(),
                meal.getLunchboxPresetName(),
                meal.getStatus(),
                itemDtos
        );
    }
}
