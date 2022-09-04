package com.stock.core.config;

import com.stock.exception.CommonRedirectException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class WebExceptionResolver extends SimpleMappingExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView doResolveException(final HttpServletRequest request
                                         , final HttpServletResponse response
                                         , final Object handler
                                         , final Exception ex) {
        if (ex instanceof CommonRedirectException) {
            final ModelAndView mvn = this.getModelAndView("common/commonErrorRedirect.jsp", ex, request);
            mvn.addObject("url", ((CommonRedirectException) ex).getUrl());
            mvn.addObject("message", ex.getMessage());
            return mvn;
        }

        return super.doResolveException(request, response, handler, ex);
    }

}
