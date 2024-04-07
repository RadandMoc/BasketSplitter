package com.ocado.basket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BasketSplitterTest {
    String absolutePathToConfigFile; // It have to be changed when someone else want to make tests
    @BeforeEach
    void setUp() {
        absolutePathToConfigFile = "C:\\Programowanie\\Java\\BasketSplitter\\src\\test\\java\\com\\ocado\\basket\\config.json";
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testWork()    {
        int result = 1 + 1;

        assertEquals(2, result);
    }

    @Test
    void constructorTest1() {
        BasketSplitter bs = new BasketSplitter(absolutePathToConfigFile);

        assertEquals(bs.allProductsDictionary.size(),100);
    }

    @Test
    void constructorTest2() {
        BasketSplitter bs = new BasketSplitter(absolutePathToConfigFile);

        assertEquals(bs.dynamicEnumOfDelivery.size(),8);
    }

    @Test
    void split() {
        BasketSplitter bs = new BasketSplitter(absolutePathToConfigFile);
        List<String> prod = new ArrayList<>();

        prod.add("Sole - Dover, Whole, Fresh");
        prod.add("Salt - Rock, Course");
        prod.add("Cookies Oatmeal Raisin");
        prod.add("Cheese Cloth");
        prod.add("English Muffin");
        prod.add("Ecolab - Medallion");
        prod.add("Chocolate - Unsweetened");
        Map<String, List<String>> cosiek = bs.split(prod);

        assertEquals(cosiek.size(),2);
    }

    @Test
    void split2(){
        BasketSplitter bs = new BasketSplitter(absolutePathToConfigFile);
        List<String> prod = new ArrayList<>();

        prod.add("Sole - Dover, Whole, Fresh");
        prod.add("Salt - Rock, Course");
        prod.add("Cookies Oatmeal Raisin");
        prod.add("Cheese Cloth");
        prod.add("English Muffin");
        prod.add("Ecolab - Medallion");
        prod.add("Chocolate - Unsweetened");
        Map<String, List<String>> cosiek = bs.split(prod);

        assertEquals(cosiek.get("Parcel locker").size(),6);
    }

    @Test
    void split3(){
        BasketSplitter bs = new BasketSplitter(absolutePathToConfigFile);
        List<String> prod = new ArrayList<>();

        prod.add("Sole - Dover, Whole, Fresh");
        prod.add("Salt - Rock, Course");
        prod.add("Cookies Oatmeal Raisin");
        prod.add("Cheese Cloth");
        prod.add("English Muffin");
        prod.add("Ecolab - Medallion");
        prod.add("Chocolate - Unsweetened");
        Map<String, List<String>> cosiek = bs.split(prod);

        assertEquals(cosiek.get("In-store pick-up").size(),1);
    }

    @Test
    void split4(){
        BasketSplitter bs = new BasketSplitter(absolutePathToConfigFile);
        List<String> prod = new ArrayList<>();

        prod.add("Cocoa Butter");
        prod.add("Tart - Raisin And Pecan");
        prod.add("Table Cloth 54x72 White");
        prod.add("Flower - Daisies");
        prod.add("Fond - Chocolate");
        prod.add("Cookies - Englishbay Wht");
        Map<String, List<String>> cosiek = bs.split(prod);

        boolean answer = true;
        if(cosiek.size()==2 && cosiek.get("Pick-up point").size()==1 && cosiek.get("Courier").size()==5){
            answer = false;
        }
        assertFalse(answer);
    }

    @Test
    void split5(){
        BasketSplitter bs = new BasketSplitter(absolutePathToConfigFile);
        List<String> prod = new ArrayList<>();

        prod.add("Fond - Chocolate");
        prod.add("Chocolate - Unsweetened");
        prod.add("Nut - Almond, Blanched, Whole");
        prod.add("Haggis");
        prod.add("Mushroom - Porcini Frozen");
        prod.add("Cake - Miini Cheesecake Cherry");
        prod.add("Sauce - Mint");
        prod.add("Longan");
        prod.add("Bag Clear 10 Lb");
        prod.add("Nantucket - Pomegranate Pear");
        prod.add("Puree - Strawberry");
        prod.add("Numi - Assorted Teas");
        prod.add("Apples - Spartan");
        prod.add("Garlic - Peeled");
        prod.add("Cabbage - Nappa");
        prod.add("Bagel - Whole White Sesame");
        prod.add("Tea - Apple Green Tea");
        Map<String, List<String>> cosiek = bs.split(prod);

        boolean answer = true;
        if(cosiek.size()==3 && cosiek.get("Same day delivery").size()==3 && cosiek.get("Courier").size()==1 && cosiek.get("Express Collection").size()==13){
            answer = false;
        }
        assertFalse(answer);
    }
}