package com.example.firebasetemplate.model;

import java.util.HashMap;

public class Post {
    public String postid;
    public String content;
    public String authorName;
    public String date;
    public String imageUrl;

    public HashMap<String, Boolean> likes = new HashMap<>();
}