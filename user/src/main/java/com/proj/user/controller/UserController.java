package com.proj.user.controller;

import com.proj.user.config.RedirectConfig;
import com.proj.user.dto.UpdateRequest;
import com.proj.user.mapper.UserMapper;
import com.proj.user.service.AuthUserService;
import com.proj.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthUserService authUserService;

    @GetMapping
    public ModelAndView getUsers(
            @RequestParam(name = "page", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(name = "sort_order", defaultValue = "desc") String sortOrder,
            @RequestParam(name = "sort_by", defaultValue = "id") String[] sortBy,
            ModelMap modelMap, Authentication authentication) {
        String authUserEmail = authentication.getName();
        modelMap.addAttribute("email", authUserEmail);
        modelMap.addAttribute("isAdmin", authUserService.isAdmin(authUserEmail));
        modelMap.addAttribute("page", pageNumber);
        modelMap.addAttribute("sort_order", sortOrder);
        modelMap.addAttribute("sort_by", getSortByValues(sortBy));
        modelMap.addAttribute("users", userService.getAll(
                        PageRequest.of(pageNumber, 3, Sort.by(Sort.Direction.fromString(sortOrder), sortBy))
                )
        );
        log.info("USERS-GET === {}", authUserEmail);
        return new ModelAndView("users-list", modelMap);
    }

    private String getSortByValues(String[] sortBy) {
        String sortByAsString = Arrays.toString(sortBy);
        return sortByAsString.substring(1, sortByAsString.length() - 1);
    }

    @GetMapping("/{id}/update")
    @PreAuthorize("@authUserService.isUserAdminOrSame(#id, authentication.name)")
    public ModelAndView getUpdateForm(@PathVariable long id, ModelMap modelMap, Authentication authentication) {
        var user = userService.readById(id);
        modelMap.addAttribute("updateRequest", userMapper.getUpdateRequestFromUser(user));

        log.info("UPDATE-FORM-GET === {}, time = {}", authentication.getPrincipal(), LocalDateTime.now());

        return new ModelAndView("user-update", modelMap);
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("@authUserService.isUserAdminOrSame(#id, authentication.name)")
    public void update(@PathVariable long id, @Valid UpdateRequest updateRequest,
                       HttpServletResponse response, Authentication authentication) {
        userService.update(userMapper.getUserFromUpdateRequest(updateRequest));
        log.info("UPDATE-POST === {}, time = {}", authentication.getPrincipal(), LocalDateTime.now());
        RedirectConfig.redirect("/api/v1/users", response);
    }
}
