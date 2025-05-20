package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.springframework.http.MediaType;

import com.example.demo.bean.AccountBean;
import com.example.demo.dto.ValidationResult;
import com.example.demo.response.SuccessResponse;
import com.example.demo.service.AccountService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.RecordService;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false) // ⬅️ 關掉 Security filter
public class AccountControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AccountService accountService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private RecordService recordService;

    @MockBean
    private UserService userService;

    @Test
    void should_return_ok_when_account_added() throws Exception {

        SuccessResponse response = new SuccessResponse(Map.of(
            "id", 1L,
            "email", "test@example.com",
            "created_at", LocalDateTime.now()
        ));
        ValidationResult<AccountBean> validationResult = new ValidationResult<>();
        validationResult.setErrors(Optional.empty());   

        when(accountService.checkAccount(any()))
            .thenReturn(validationResult);
        when(accountService.addAccount(any()))
            .thenReturn((ResponseEntity<SuccessResponse>) ResponseEntity.ok(response));


        String json = """
        {
            "name": "薪水帳戶",
            "description": "用來記錄收入",
            "initial_amount": 1000,
            "is_public": false
        }
        """;

        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.id").value(1));
    }
    
}
