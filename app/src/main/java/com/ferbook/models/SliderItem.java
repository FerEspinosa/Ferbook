package com.ferbook.models;

public class SliderItem {

    String imageUrl;
    Long timestamp;

    public SliderItem() {

    }

    public SliderItem(String imageUrl, Long timestamp) {
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
