package com.stock.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessException extends RuntimeException {

    public ProcessException(String msg) {
        super(msg);
    }
}
