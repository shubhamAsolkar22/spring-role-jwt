/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fkog.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fkog.security.jwt.model.AuthToken;
import com.fkog.security.jwt.model.LoginUser;
import com.fkog.security.jwt.model.User;
import com.fkog.security.jwt.model.UserDto;
import com.fkog.security.jwt.service.UserService;
import java.net.URI;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author shubham
 */
//@AutoConfigureMockMvc
//@RunWit(SpringRunner.class)
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestUserController {

//    @Autowired
//    private MockMvc mockMvc;
    @MockBean
    UserDto userDto;
//
//    @MockBean
//    UserDetailsService userDetailsService;
//    
//    @MockBean
//    UnauthorizedEntryPoint unauthorizedEntryPoint;
//    
//    @MockBean
//    BCryptPasswordEncoder encoder;
//
//    @Autowired
//    private UserController controller;
//    @Test
//    public void contextLoads() throws Exception {
//        assertThat(controller).isNotNull();
//    }
//    @Autowired
//    TestRestTemplate testTemplate;
    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void checkUserRegistrationEndpoint() throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        UserDto userDto = new UserDto();
//        User user = new User();
//
//        Mockito.when(userService.save(userDto)).thenReturn(user);
//
//        userDto.setBusinessTitle("Developer");
//        final String snafkogcom = "sna@fkog.com";
//        userDto.setEmail(snafkogcom);
//        userDto.setUsername("sna");
//        userDto.setPassword("password");
//        userDto.setPhone("1234567890");
//
//        user.setBusinessTitle("Developer");
//        user.setEmail(snafkogcom);
//        user.setUsername("sna");
//        user.setPassword("password");
//        user.setPhone("1234567890");
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/v1/users/new")
//                .content(mapper.writeValueAsString(userDto))
//                .contentType(MediaType.APPLICATION_JSON)
//        ).andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(snafkogcom));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDto> request = new HttpEntity<>(userDto, headers);
        
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ResponseEntity<User> result = testRestTemplate.withBasicAuth("spring", "secret")
                .exchange("/v1/users/new", HttpMethod.POST, request, User.class);

        System.out.println(result);
//        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

}
