package com.doan.utils;

public abstract class MessageError {
    public interface LOGIN {
        String username = "username có độ dài 6 đến 20 kí tự";
        String password = "password có độ dài ít nhất 8 kí tự";
    }
}
