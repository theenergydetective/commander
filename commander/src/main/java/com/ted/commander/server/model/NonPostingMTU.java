package com.ted.commander.server.model;

import java.io.Serializable;


public class NonPostingMTU implements Serializable {
    String description;
    String hex;
    Long lastPost;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public Long getLastPost() {
        return lastPost;
    }

    public void setLastPost(Long lastPost) {
        this.lastPost = lastPost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NonPostingMTU that = (NonPostingMTU) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (hex != null ? !hex.equals(that.hex) : that.hex != null) return false;
        return lastPost != null ? lastPost.equals(that.lastPost) : that.lastPost == null;

    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + (hex != null ? hex.hashCode() : 0);
        result = 31 * result + (lastPost != null ? lastPost.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NonPostingMTU{" +
                "description='" + description + '\'' +
                ", hex='" + hex + '\'' +
                ", lastPost=" + lastPost +
                '}';
    }
}
