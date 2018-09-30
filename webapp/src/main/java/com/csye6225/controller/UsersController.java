package com.csye6225.controller;

import com.csye6225.model.Users;
import com.csye6225.repository.UserJpaRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UsersController {

    @Autowired
    private UserJpaRespository userJpaRespository;

    @PostMapping(value = "/register",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> register(@RequestBody Users user) {
        try {
            List<Users> loginusers = userJpaRespository.findAll();
            for (Users l: loginusers) {
                if (l.getUsername().equals(user.getUsername()))
                {
                    return Collections.singletonMap("response", "User already present");

                }
            }
            String pw_hash = BCrypt.hashpw(user.getPwd(), BCrypt.gensalt());
            user.setPwd(pw_hash);
            userJpaRespository.save(user);
            return Collections.singletonMap("response", "You are successfully logged in");
        }
        catch (Exception ex)
        {
            System.out.println("Exception caught while register user : " +ex.getMessage());
            return Collections.singletonMap("response", "Exception occurred..Pls try after sometime");
        }

    }
    @GetMapping(value = {"/","/time"},produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> gettime(HttpServletRequest httpRequest) {
        final String authorization = httpRequest.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String pwd = values[1];
            List<Users> userlist = userJpaRespository.findAll();
            for (Users u : userlist) {
                if (u.getUsername().equals(username)) {
                    if(BCrypt.checkpw(pwd, u.getPwd()))
                    {
                        return Collections.singletonMap("response", LocalDateTime.now().toString());
                    }
                }
            }
            return Collections.singletonMap("response", "You are not logged in..");
        } else {
            return Collections.singletonMap("response", "You are not logged in..");
        }
    }

}
