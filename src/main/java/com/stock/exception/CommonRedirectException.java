package com.stock.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonRedirectException extends RuntimeException {

    private String url;

    public CommonRedirectException(final String message, final String url) {
        super(message);
        this.url = url;
    }
}
