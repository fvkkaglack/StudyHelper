package com.studyhelper.service;

import com.studyhelper.dto.request.UserRequest;
import com.studyhelper.dto.response.PageDto;
import com.studyhelper.dto.response.UserResponse;
import com.studyhelper.entity.Role;
import com.studyhelper.entity.User;
import com.studyhelper.mapper.UserMapper;
import com.studyhelper.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с пользователями (все роли: USER, MODERATOR, ADMIN).
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, @Lazy PasswordService passwordService, JwtService jwtService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    public UserMapper getUserMapper() {
        return userMapper;
    }

    // Регистрация обычного пользователя (USER)
    public Map<String, String> register(String nickname, String password) {
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("Пользователь с никнеймом " + nickname + " уже существует");
        }
        User user = new User();
        user.setNickname(nickname);
        user.setPassword(passwordService.encodePassword(password));
        user.setRole(Role.USER); // Только USER при регистрации
        user.setBalance(0);
        user.setTotalStars(0);
        user.setDebt(0);
        user.setTasksCreated(0);
        user.setTasksTaken(0);
        user.setOverdueFakeTasks(0);
        user.setUnjustRejections(0);
        user.setTaskCreationBlocked(false);
        user.setTaskTakingBlocked(false);
        User savedUser = userRepository.save(user);
        return generateTokens(savedUser);
    }

    // Назначение роли модератора существующему пользователю (только для ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse assignModeratorRole(String nickname) {
        User user = findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с никнеймом " + nickname + " не найден"));
        user.setRole(Role.MODERATOR); // Назначаем роль MODERATOR
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    // Назначение роли администратора существующему пользователю (только для ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse assignAdminRole(String nickname) {
        User user = findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с никнеймом " + nickname + " не найден"));
        user.setRole(Role.ADMIN); // Назначаем роль ADMIN
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    // Создание пользователя (для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public UserResponse createUser(UserRequest request, Role role) {
        if (userRepository.existsByNickname(request.nickname())) {
            throw new IllegalArgumentException("Пользователь с никнеймом " + request.nickname() + " уже существует");
        }
        User user = userMapper.toUser(request);
        user.setRole(role); // Роль устанавливается из параметра (контроллер проверяет права)
        user.setBalance(0);
        user.setTotalStars(0);
        user.setDebt(0);
        user.setTasksCreated(0);
        user.setTasksTaken(0);
        user.setOverdueFakeTasks(0);
        user.setUnjustRejections(0);
        user.setTaskCreationBlocked(false);
        user.setTaskTakingBlocked(false);
        user.setPassword(passwordService.encodePassword(user.getPassword())); // Шифруем пароль
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    // Обновление пользователя по ID (для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public UserResponse updateUser(UUID id, UserRequest request, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + id + " не найден"));
        userMapper.updateUser(user, request);
        if (request.password() != null && !request.password().isEmpty()) {
            user.setPassword(passwordService.encodePassword(request.password()));
        }
        if (role != null) {
            user.setRole(role); // Обновляем роль, если она передана (контроллер проверяет права)
        }
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    // Удаление пользователя по ID (для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + id + " не найден"));
        userRepository.delete(user);
    }

    // Получение пагинированного списка пользователей (для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public PageDto<UserResponse> getUserPage(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());

        // Используем конструктор PageDto для создания объекта
        return new PageDto<>(
                userResponses,
                userPage.getNumber(),
                userPage.getNumberOfElements(), // elementsOnPage
                userPage.getTotalPages(),
                userPage.getTotalElements()
        );
    }

    // Получение полного списка пользователей (для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public List<UserResponse> getUserList() {
        return ((List<User>) userRepository.findAll()).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Обновление пользователя по никнейму (для текущего пользователя или ADMIN)
    public User updateUser(String nickname, User updatedUser, UserDetails currentUser) {
        if (!currentUser.getUsername().equals(nickname) && !currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Вы можете обновлять только свои данные");
        }
        User user = findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        user.setNickname(updatedUser.getNickname());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordService.encodePassword(updatedUser.getPassword()));
        }
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && updatedUser.getRole() != null) {
            user.setRole(updatedUser.getRole());
        }
        user.setBalance(updatedUser.getBalance());
        user.setTotalStars(updatedUser.getTotalStars());
        user.setDebt(updatedUser.getDebt());
        user.setTasksCreated(updatedUser.getTasksCreated());
        user.setTasksTaken(updatedUser.getTasksTaken());
        user.setOverdueFakeTasks(updatedUser.getOverdueFakeTasks());
        user.setUnjustRejections(updatedUser.getUnjustRejections());
        user.setTaskCreationBlocked(updatedUser.isTaskCreationBlocked());
        user.setTaskTakingBlocked(updatedUser.isTaskTakingBlocked());
        user.setBlockUntil(updatedUser.getBlockUntil());
        return userRepository.save(user);
    }

    // Удаление пользователя по никнейму (для ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String nickname) {
        User user = findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        userRepository.delete(user);
    }

    // Получение списка всех пользователей (доступно для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    // Реализация метода UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        return findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с никнеймом " + nickname + " не найден"));
    }

    // Вспомогательный метод для поиска пользователя по никнейму
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    // Вспомогательный метод для поиска пользователя по ID
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    // Генерация токенов с использованием JwtService
    public Map<String, String> generateTokens(UserDetails userDetails) {
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }
}