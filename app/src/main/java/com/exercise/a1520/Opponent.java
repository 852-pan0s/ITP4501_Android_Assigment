package com.exercise.a1520;

public class Opponent {
    private String id;
    private String name;
    private String country;
    private int left;
    private int right;
    private int guess;

    public Opponent(String id, String name, String country, int left, int right, int guess){
        this.id = id;
        this.name = name;
        this.country = country;
        this.left = left;
        this.right = right;
        this.guess = guess;
    }

    public int getLeft(){
        return left;
    }

    public int getRight(){
        return right;
    }

    public int getGuess(){
        return guess;
    }
}
