package org.example.models;

import java.time.LocalDateTime;

public class Rating {
    private final int id;
    private final int mediaId;
    private final int userId;
    private final int rating;
    private final String comment;
    private final LocalDateTime createdAt;

    public Rating(int id, int mediaId, int userId, int rating, String comment) {
        this(id, mediaId, userId, rating, comment, LocalDateTime.now());
    }

    public Rating(int id, int mediaId, int userId, int rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.mediaId = mediaId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }


    public int getId() { return id; }
    public int getMediaId() { return mediaId; }
    public int getUserId() { return userId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Rating{id=" + id + ", mediaId=" + mediaId + ", rating=" + rating + "}";
    }
}