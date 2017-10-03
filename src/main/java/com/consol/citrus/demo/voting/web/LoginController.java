/*
 * Copyright 2006-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.demo.voting.web;

import com.consol.citrus.demo.voting.model.User;
import com.consol.citrus.demo.voting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String loginView() {
        return "login";
    }

    @RequestMapping(method = RequestMethod.POST, headers = "content-type=application/x-www-form-urlencoded")
    public String doLogin(Model model,
                          RedirectAttributes redirectAttributes,
                          @RequestParam(value = "username") String username,
                          @RequestParam(value = "password") String password) {
        User user = new User(username, password);
        String token = userService.login(user);

        model.addAttribute("user", user);
        model.addAttribute("token", token);

        if (!redirectAttributes.containsAttribute("token")) {
            redirectAttributes.addAttribute("token", token);
        }
        return "redirect:/voting?token={token}";
    }
}
