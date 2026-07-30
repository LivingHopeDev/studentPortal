package com.studentmanagement.auth.service;

import com.studentmanagement.auth.model.User;
import jakarta.servlet.http.HttpServletRequest;

public interface MfaService {

    void setup(HttpServletRequest request);

    void verify(String code, HttpServletRequest request);

    void validate(String code, HttpServletRequest request);

    void sendAndStoreCode(User user, HttpServletRequest request);
}
