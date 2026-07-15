package com.eventticketing.dao;

import com.eventticketing.datastore.DataStore;
import com.eventticketing.model.Venue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VenueDAO {

    private final DataStore store = DataStore.getInstance();

    public List<Venue> getAllVenues() {
        List<Venue> list = new ArrayList<>(store.venues);
        list.sort(Comparator.comparing(Venue::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    public boolean addVenue(String name, String address, int capacity) {
        return addVenueReturnId(name, address, capacity) != -1;
    }

    /**
     * Adds a new venue and returns its generated venue_id, or -1 on failure.
     * Used when the admin types a brand-new venue name instead of picking
     * one from the existing list.
     */
    public int addVenueReturnId(String name, String address, int capacity) {
        Venue venue = new Venue(store.venueIdSeq.incrementAndGet(), name, address, capacity);
        store.venues.add(venue);
        return venue.getVenueId();
    }

    /**
     * Finds an existing venue by name (case-insensitive), ignoring
     * surrounding whitespace. Returns null if no match is found.
     */
    public Venue findByName(String name) {
        if (name == null) return null;
        String target = name.trim();
        for (Venue v : store.venues) {
            if (v.getName().equalsIgnoreCase(target)) {
                return v;
            }
        }
        return null;
    }
}
