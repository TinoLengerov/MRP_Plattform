package org.example.models;

import java.time.LocalDateTime;

public class Media {
    private final int id;
    private final String title;
    private final String description;
    private final String mediaType;
    private final int createdBy;
    private final LocalDateTime createdAt;
    private final double averageRating;

    public Media(int id, String title, String description, String mediaType, int createdBy) {
        this(id, title, description, mediaType, createdBy, LocalDateTime.now(), 0.0);
    }

    public Media(int id, String title, String description, String mediaType, int createdBy, LocalDateTime createdAt, double averageRating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.averageRating = averageRating;
    }


    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getMediaType() { return mediaType; }
    public int getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public double getAverageRating() { return averageRating; }

    @Override
    public String toString() {
        return "MediaEntry{id=" + id + ", title='" + title + "', type='" + mediaType + "'}";
    }
}