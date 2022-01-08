package com.essentials.customerapp.data.repository;

public class LoginRepository {
    private static LoginRepository loginRepository;

    public static LoginRepository getInstance() {
        if (loginRepository == null) {
            loginRepository = new LoginRepository();
        }
        return loginRepository;
    }

}
