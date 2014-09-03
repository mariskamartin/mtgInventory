package com.gmail.mariska.martin.mtginventory.service;

import com.gmail.mariska.martin.mtginventory.db.model.User;

/**
 * Sluzba pro autentizaci a authorizaci uzivatelu.
 * 
 * @author MAR
 */
public class AuthService {
    private UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Zaloguje uzivatele pokud je to mozne
     * @param login
     * @param password
     * @return
     * @throws IllegalAccessException pri spatnem zalogovani
     */
    public User authenticate(String loginEmail, String password) throws IllegalAccessException {
        User findById = userService.findById(loginEmail);
        if (findById.getPassword().equals(password)) {
            return findById;
        }
        throw new IllegalAccessException("Wrong login or password.");
    }

    /**
     * Verifikuje a identifikuje uzivatele podle zaslaneho tokenu
     * @param token
     * @return null v pripade ze se nepovedlo verifikovat
     */
    public User verify(String token) {
        return userService.findByToken(token);
    }

}
