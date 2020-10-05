package com.dcssn.oauth2.system.controller;

import com.dcssn.oauth2.common.BaseController;
import com.dcssn.oauth2.common.constants.Security;
import com.dcssn.oauth2.system.config.serurity.CustomUserDetails;
import com.dcssn.oauth2.system.entity.Role;
import com.dcssn.oauth2.system.entity.User;
import com.dcssn.oauth2.system.repository.UserRepository;
import com.dcssn.oauth2.system.utils.HttpResultUtils;
import com.dcssn.oauth2.system.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("test")
public class DbTestController extends BaseController {

    @Autowired
    private UserRepository userRepository;

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
            @RequestHeader(name = "nickname") String nickName,
            @RequestHeader(name = "description", defaultValue = "set a description...") String description,
            @RequestHeader(name = "avatarUrl", defaultValue = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=400062461,2874561526&fm=26&gp=0.jpg") String avatarUrl
    ) {
        log.info("===================================================================================================");

        log.info("Creating new user: ");
        log.info("   Username    : " + userName);
        log.info("   Nickname    : " + nickName);
        log.info("   Description : " + description);
        log.info("   Avatar URL  : " + avatarUrl);

        User user = new User();
        user.setUsername(userName);
        user.setNickname(nickName);
        user.setDescription(description);
        user.setAvatar(avatarUrl);

        log.info("Saving: " + user);
        user = userRepository.save(user);

        log.info("===================================================================================================");
        return HttpResultUtils.success("Created: ", user);
    }

    // Deletes the user with the specified ID.
    // Note:
    //    There's already a @DeleteMapping update() end-point but it gives an HTTP 403 error.
    //    This implementation is also an exercise for Spring and database manipulation.
    @GetMapping("/delete")
    public HttpResultUtils.HttpResult delete(
            @RequestParam(name = "id") Long id
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
