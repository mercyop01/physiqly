package com.physiqly.physiqlybackend;

// All necessary imports from Spring, Jakarta Persistence, etc.
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// NOTE: In a larger real-world project, each of these classes and interfaces
// would be in its own separate .java file. For simplicity and to guarantee a fix,
// we are combining them into a single file for this project.

@SpringBootApplication
public class PhysiqlyBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhysiqlyBackendApplication.class, args);
    }
}

// --- ENTITIES ---

@Entity @Table(name = "app_users")
class User {
    @Id @SequenceGenerator(name = "user_seq", sequenceName="user_seq", allocationSize = 1) @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq") private Long id;
    private String firstName; private String lastName; @Column(unique = true) private String email; private String password;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; } public String getFirstName() { return firstName; } public void setFirstName(String n) { this.firstName = n; } public String getLastName() { return lastName; } public void setLastName(String n) { this.lastName = n; } public String getEmail() { return email; } public void setEmail(String e) { this.email = e; } public String getPassword() { return password; } public void setPassword(String p) { this.password = p; }
}
@Entity
class Exercise {
    @Id @SequenceGenerator(name = "ex_seq", sequenceName="ex_seq", allocationSize = 1) @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ex_seq") private Long id;
    private String name; private String description; private String targetMuscle; private String equipment; private String difficulty;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; } public String getName() { return name; } public void setName(String n) { this.name = n; } public String getDescription() { return description; } public void setDescription(String d) { this.description = d; } public String getTargetMuscle() { return targetMuscle; } public void setTargetMuscle(String t) { this.targetMuscle = t; } public String getEquipment() { return equipment; } public void setEquipment(String e) { this.equipment = e; } public String getDifficulty() { return difficulty; } public void setDifficulty(String d) { this.difficulty = d; }
}
@Entity
class DailyLog {
    @Id @SequenceGenerator(name = "log_seq", sequenceName="log_seq", allocationSize = 1) @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_seq") private Long id;
    private Long userId; private LocalDate date; private int calorieGoal; private int breakfastCalories; private int lunchCalories; private int eveningSnackCalories; private int dinnerCalories;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; } public Long getUserId() { return userId; } public void setUserId(Long u) { this.userId = u; } public LocalDate getDate() { return date; } public void setDate(LocalDate d) { this.date = d; } public int getCalorieGoal() { return calorieGoal; } public void setCalorieGoal(int c) { this.calorieGoal = c; } public int getBreakfastCalories() { return breakfastCalories; } public void setBreakfastCalories(int c) { this.breakfastCalories = c; } public int getLunchCalories() { return lunchCalories; } public void setLunchCalories(int c) { this.lunchCalories = c; } public int getEveningSnackCalories() { return eveningSnackCalories; } public void setEveningSnackCalories(int c) { this.eveningSnackCalories = c; } public int getDinnerCalories() { return dinnerCalories; } public void setDinnerCalories(int c) { this.dinnerCalories = c; }
}
@Entity
class Goal {
    @Id @SequenceGenerator(name = "goal_seq", sequenceName="goal_seq", allocationSize = 1) @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "goal_seq") private Long id;
    private Long userId; private double currentWeight; private double targetWeight; private double height; private LocalDate targetDate; private int calculatedDailyCalories; private int calculatedProteinGrams; private int calculatedCarbsGrams; private int calculatedFatGrams;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; } public Long getUserId() { return userId; } public void setUserId(Long u) { this.userId = u; } public double getCurrentWeight() { return currentWeight; } public void setCurrentWeight(double w) { this.currentWeight = w; } public double getTargetWeight() { return targetWeight; } public void setTargetWeight(double w) { this.targetWeight = w; } public double getHeight() { return height; } public void setHeight(double h) { this.height = h; } public LocalDate getTargetDate() { return targetDate; } public void setTargetDate(LocalDate d) { this.targetDate = d; } public int getCalculatedDailyCalories() { return calculatedDailyCalories; } public void setCalculatedDailyCalories(int c) { this.calculatedDailyCalories = c; } public int getCalculatedProteinGrams() { return calculatedProteinGrams; } public void setCalculatedProteinGrams(int p) { this.calculatedProteinGrams = p; } public int getCalculatedCarbsGrams() { return calculatedCarbsGrams; } public void setCalculatedCarbsGrams(int c) { this.calculatedCarbsGrams = c; } public int getCalculatedFatGrams() { return calculatedFatGrams; } public void setCalculatedFatGrams(int f) { this.calculatedFatGrams = f; }
}
@Entity
class WeightEntry {
    @Id @SequenceGenerator(name = "weight_seq", sequenceName="weight_seq", allocationSize = 1) @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "weight_seq") private Long id;
    private Long userId; private LocalDate date; private double weight;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; } public Long getUserId() { return userId; } public void setUserId(Long u) { this.userId = u; } public LocalDate getDate() { return date; } public void setDate(LocalDate d) { this.date = d; } public double getWeight() { return weight; } public void setWeight(double w) { this.weight = w; }
}
@Entity
class Post {
    @Id @SequenceGenerator(name = "post_seq", sequenceName="post_seq", allocationSize = 1) @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq") private Long id;
    private Long userId; private String authorName; @Column(columnDefinition = "TEXT") private String content; private LocalDateTime createdAt;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true) @JoinColumn(name = "post_id") @OrderBy("createdAt ASC") private List<Comment> comments = new ArrayList<>();
    @ElementCollection(fetch = FetchType.EAGER) @CollectionTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id")) @Column(name = "user_id") private Set<Long> likes = new HashSet<>();
    public Long getId() { return id; } public void setId(Long id) { this.id = id; } public Long getUserId() { return userId; } public void setUserId(Long u) { this.userId = u; } public String getAuthorName() { return authorName; } public void setAuthorName(String n) { this.authorName = n; } public String getContent() { return content; } public void setContent(String c) { this.content = c; } public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime c) { this.createdAt = c; } public List<Comment> getComments() { return comments; } public void setComments(List<Comment> c) { this.comments = c; } public Set<Long> getLikes() { return likes; } public void setLikes(Set<Long> l) { this.likes = l; }
}
@Entity
class Comment {
    @Id @SequenceGenerator(name = "comment_seq", sequenceName="comment_seq", allocationSize = 1) @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq") private Long id;
    private Long userId; private String authorName; @Column(columnDefinition = "TEXT") private String content; private LocalDateTime createdAt;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; } public Long getUserId() { return userId; } public void setUserId(Long u) { this.userId = u; } public String getAuthorName() { return authorName; } public void setAuthorName(String n) { this.authorName = n; } public String getContent() { return content; } public void setContent(String c) { this.content = c; } public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }
}

// --- REPOSITORIES ---

@Repository interface UserRepository extends JpaRepository<User, Long> { Optional<User> findByEmail(String email); }
@Repository interface ExerciseRepository extends JpaRepository<Exercise, Long> {}
@Repository interface DailyLogRepository extends JpaRepository<DailyLog, Long> { Optional<DailyLog> findByUserIdAndDate(Long userId, LocalDate date); }
@Repository interface GoalRepository extends JpaRepository<Goal, Long> { Optional<Goal> findByUserId(Long userId); }
@Repository interface WeightEntryRepository extends JpaRepository<WeightEntry, Long> { List<WeightEntry> findByUserIdOrderByDateAsc(Long userId); }
@Repository interface PostRepository extends JpaRepository<Post, Long> { List<Post> findAllByOrderByCreatedAtDesc(); }

// --- DTO for Login ---
class LoginRequest { private String email; private String password; public String getEmail(){return email;} public String getPassword(){return password;} }

// --- SECURITY CONFIGURATION ---
@Configuration
class SecurityConfig {
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth.requestMatchers("/api/**").permitAll()); return http.build(); }
}

// --- CONTROLLERS ---

@RestController @RequestMapping("/api/users") @CrossOrigin(origins = "http://localhost:5173")
class UserController {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @PostMapping("/register") public ResponseEntity<?> register(@RequestBody User u) { if (userRepository.findByEmail(u.getEmail()).isPresent()) { return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use."); } u.setPassword(passwordEncoder.encode(u.getPassword())); return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(u)); }
    @PostMapping("/login") public ResponseEntity<User> login(@RequestBody LoginRequest r) { Optional<User> o = userRepository.findByEmail(r.getEmail()); if (o.isPresent() && passwordEncoder.matches(r.getPassword(), o.get().getPassword())) { User u = o.get(); u.setPassword(null); return ResponseEntity.ok(u); } return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
}

@RestController @RequestMapping("/api/exercises") @CrossOrigin(origins = "http://localhost:5173")
class ExerciseController {
    @Autowired private ExerciseRepository repo;
    @PostMapping public ResponseEntity<Exercise> create(@RequestBody Exercise e) { return new ResponseEntity<>(repo.save(e), HttpStatus.CREATED); }
    @GetMapping public ResponseEntity<List<Exercise>> getAll() { return ResponseEntity.ok(repo.findAll()); }
}

@RestController @RequestMapping("/api/logs") @CrossOrigin(origins = "http://localhost:5173")
class DailyLogController {
    @Autowired private DailyLogRepository repo;
    @GetMapping("/{userId}/today") public ResponseEntity<DailyLog> getToday(@PathVariable Long userId) { DailyLog log = repo.findByUserIdAndDate(userId, LocalDate.now()).orElseGet(() -> { DailyLog n = new DailyLog(); n.setUserId(userId); n.setDate(LocalDate.now()); n.setCalorieGoal(2500); return repo.save(n); }); return ResponseEntity.ok(log); }
    @PostMapping public ResponseEntity<DailyLog> update(@RequestBody DailyLog log) { return ResponseEntity.ok(repo.save(log)); }
}

@RestController @RequestMapping("/api/goals") @CrossOrigin(origins = "http://localhost:5173")
class GoalController {
    @Autowired private GoalRepository repo;
    @GetMapping("/{userId}") public ResponseEntity<Goal> get(@PathVariable Long userId) { return repo.findByUserId(userId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build()); }
    @PostMapping public ResponseEntity<Goal> save(@RequestBody Goal goal) { long days = ChronoUnit.DAYS.between(LocalDate.now(), goal.getTargetDate()); if (days <= 0) days = 1; double deficit = (goal.getCurrentWeight() - goal.getTargetWeight()) * 7700 / days; int maintenance = (int) (10 * goal.getCurrentWeight() + 6.25 * goal.getHeight() - 5 * 25 + 5); int target = (int) (maintenance - deficit); goal.setCalculatedDailyCalories(target); goal.setCalculatedProteinGrams((int) ((target * 0.30) / 4)); goal.setCalculatedCarbsGrams((int) ((target * 0.40) / 4)); goal.setCalculatedFatGrams((int) ((target * 0.30) / 9)); return ResponseEntity.ok(repo.save(goal)); }
}

@RestController @RequestMapping("/api/progress") @CrossOrigin(origins = "http://localhost:5173")
class ProgressController {
    @Autowired private WeightEntryRepository repo;
    @GetMapping("/{userId}") public ResponseEntity<List<WeightEntry>> getHistory(@PathVariable Long userId) { return ResponseEntity.ok(repo.findByUserIdOrderByDateAsc(userId)); }
    @PostMapping public ResponseEntity<WeightEntry> add(@RequestBody WeightEntry entry) { return ResponseEntity.ok(repo.save(entry)); }
}

@RestController @RequestMapping("/api/community") @CrossOrigin(origins = "http://localhost:5173")
class CommunityController {
    @Autowired private PostRepository postRepo;
    @GetMapping("/posts") public ResponseEntity<List<Post>> getPosts() { return ResponseEntity.ok(postRepo.findAllByOrderByCreatedAtDesc()); }
    @PostMapping("/posts") public ResponseEntity<Post> createPost(@RequestBody Post post) { post.setCreatedAt(LocalDateTime.now()); return ResponseEntity.status(HttpStatus.CREATED).body(postRepo.save(post)); }
    @PostMapping("/posts/{postId}/like") public ResponseEntity<Post> toggleLike(@PathVariable Long postId, @RequestBody Long userId) { Optional<Post> p = postRepo.findById(postId); if (p.isPresent()) { Post post = p.get(); if (post.getLikes().contains(userId)) { post.getLikes().remove(userId); } else { post.getLikes().add(userId); } return ResponseEntity.ok(postRepo.save(post)); } return ResponseEntity.notFound().build(); }
    @PostMapping("/posts/{postId}/comments") public ResponseEntity<Post> addComment(@PathVariable Long postId, @RequestBody Comment comment) { Optional<Post> p = postRepo.findById(postId); if (p.isPresent()) { Post post = p.get(); comment.setCreatedAt(LocalDateTime.now()); post.getComments().add(comment); return ResponseEntity.ok(postRepo.save(post)); } return ResponseEntity.notFound().build(); }
}

@RestController @RequestMapping("/api/ai") @CrossOrigin(origins = "http://localhost:5173")
class AiController {
    private final WebClient webClient; private final ObjectMapper objectMapper = new ObjectMapper();
    private final String deepseekApiUrl = "https://api.deepseek.com/chat/completions";
    // IMPORTANT: You must get your own API key from DeepSeek and paste it here.
    private final String apiKey = "YOUR_DEEPSEEK_API_KEY"; 
    public AiController(WebClient.Builder builder) { this.webClient = builder.baseUrl(deepseekApiUrl).build(); }
    @PostMapping("/chat") public Mono<ResponseEntity<String>> chat(@RequestBody String prompt) {
        String sysPrompt = "You are Physiqly, a friendly, encouraging, and knowledgeable AI fitness coach. Provide helpful and safe advice on workouts, nutrition, and general fitness. Keep answers concise. Do not give medical advice.";
        Object payload = new Object() { public final String model = "deepseek-chat"; public final Object[] messages = { new Object() { public final String role = "system"; public final String content = sysPrompt; }, new Object() { public final String role = "user"; public final String content = prompt; } }; };
        return this.webClient.post().header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey).contentType(MediaType.APPLICATION_JSON).bodyValue(payload).retrieve().bodyToMono(String.class)
            .map(this::extractText).map(ResponseEntity::ok).onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Error: " + e.getMessage())));
    }
    private String extractText(String json) { try { JsonNode root = objectMapper.readTree(json); if (root.has("error")) { return "Error from AI Provider: " + root.path("error").path("message").asText(); } return root.path("choices").get(0).path("message").path("content").asText("Sorry, I couldn't get a response."); } catch (Exception e) { return "Sorry, I had trouble parsing the AI response."; } }
}

