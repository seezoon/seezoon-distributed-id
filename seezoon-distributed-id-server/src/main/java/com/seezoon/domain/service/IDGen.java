package com.seezoon.domain.service;


import java.util.Collection;

public interface IDGen {

    long get(String key);

    Collection<String> getTags();

    String getToken(String key);
}
