package com.dcssn.oauth2.system.controller;

import com.dcssn.oauth2.common.BaseController;
import com.dcssn.oauth2.system.entity.Role;
import com.dcssn.oauth2.system.entity.User;
import com.dcssn.oauth2.system.repository.RoleRepository;
import com.dcssn.oauth2.system.repository.UserRepository;
import com.dcssn.oauth2.system.utils.HttpResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("test")
public class DbTestController extends BaseController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Lists all users from the database.
    @GetMapping("/list")
    public HttpResultUtils.HttpResult list() {
        List<User> users = userRepository.getAllUsersRaw();
        return HttpResultUtils.success(users);
    }

    // Deletes the user with the specified ID.
    // Note:
    //    There's already a @DeleteMapping UserController.update() end-point but it gives an HTTP 403 error.
    //    This implementation is also an exercise for Spring and database manipulation.
    @GetMapping("/add")
    public HttpResultUtils.HttpResult add(
            @RequestHeader(name = "username") String userName,
            @RequestHeader(name = "password") String password,
            @RequestHeader(name = "roleId") Long roleId,
            @RequestHeader(name = "nickname") String nickName,
            @RequestHeader(name = "description", defaultValue = "set a description...") String description,
            @RequestHeader(name = "avatarUrl", defaultValue = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=400062461,2874561526&fm=26&gp=0.jpg") String avatarUrl
    ) {
        log.info("===================================================================================================");

        log.info("Getting Role with ID = " + roleId);
        Optional<Role> role = roleRepository.findById(roleId);

        if (!role.isPresent()) {
            List<String> roles = roleRepository
                    .findAll()
                    .stream()
                    .map(r -> String.format("Role [ id = %d, name = '%s' ]", r.getId(), r.getName()))
                    .collect(Collectors.toList());

            return HttpResultUtils.fail("Invalid Role ID = " + roleId, roles);
        }

        log.info("Creating new user: ");
        log.info("   Username    : " + userName);
        log.info("   Password    : " + password);
        log.info("   Nickname    : " + nickName);
        log.info("   Role        : " + role.get());
        log.info("   Description : " + description);
        log.info("   Avatar URL  : " + avatarUrl);

        User user = new User();
        user.setUsername(userName);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickName);
        user.setRole(role.get());
        user.setDescription(description);
        user.setAvatar(avatarUrl);

        log.info("Saving: " + user);
        user = userRepository.saveAndFlush(user);

        log.info("===================================================================================================");
        return HttpResultUtils.success("Created: ", user);
    }

    @GetMapping("/update/{userId}")
    public HttpResultUtils.HttpResult updateRole(
            @PathVariable Long userId,
            @RequestParam(name = "newRoleId") Long newRoleId
    ) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return HttpResultUtils.fail(String.format("Error: User with ID = %d does not exist!", userId));
        }

        Optional<Role> newRole = roleRepository.findById(newRoleId);

        if (!newRole.isPresent()) {
            List<String> roles = roleRepository
                    .findAll()
                    .stream()
                    .map(r -> String.format("Role [ id = %d, name = '%s' ]", r.getId(), r.getName()))
                    .collect(Collectors.toList());

            return HttpResultUtils.fail("Invalid Role ID = " + newRoleId, roles);
        }

        userRepository.changeUserRole(userId, newRoleId);
        return HttpResultUtils.success("Updated role from '" + user.get().getRole().getName()  + "' to '" + newRole.get().getName() + "' for user with ID = " + userId);
    }

    // Deletes the user with the specified ID.
    // Note:
    //    There's already a @DeleteMapping update() end-point but it gives an HTTP 403 error.
    //    This implementation is also an exercise for Spring and database manipulation.
    @GetMapping("/delete/{id}")
    public HttpResultUtils.HttpResult delete(
            @PathVariable Long id
    ) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            userRepository.deleteById(id);
            return HttpResultUtils.success(String.format("User with ID = %d has been deleted!", id));
        } else {
            return HttpResultUtils.fail(String.format("Error: User with ID = %d does not exist!", id));
        }
    }
}
