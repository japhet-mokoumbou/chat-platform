package com.example.chatplatform.util;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "items")
public class XmlListWrapper<T> {
    
    private List<T> items;

    public XmlListWrapper() {}

    public XmlListWrapper(List<T> items) {
        this.items = items;
    }

    @XmlElement(name = "contact")
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}

