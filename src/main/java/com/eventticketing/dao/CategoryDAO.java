package com.eventticketing.dao;

import com.eventticketing.datastore.DataStore;
import com.eventticketing.model.Category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CategoryDAO {

    private final DataStore store = DataStore.getInstance();

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>(store.categories);
        list.sort(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    public boolean addCategory(String name, String description) {
        Category category = new Category(store.categoryIdSeq.incrementAndGet(), name, description);
        return store.categories.add(category);
    }

    public boolean updateCategory(int categoryId, String name, String description) {
        for (Category c : store.categories) {
            if (c.getCategoryId() == categoryId) {
                c.setName(name);
                c.setDescription(description);
                return true;
            }
        }
        return false;
    }

    public boolean deleteCategory(int categoryId) {
        return store.categories.removeIf(c -> c.getCategoryId() == categoryId);
    }
}
