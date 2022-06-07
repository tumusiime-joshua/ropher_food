package com.ropherfood.client;

public class CategoryData {

    String category;
    String categoryId;
    String categoryName;

    public CategoryData() {
    }

    public CategoryData(String categoryId, String categoryName) {
        this.category = category;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
