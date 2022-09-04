<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width,user-scalable=no,initial-scale=1,viewport-fit=cover">

        <link rel="stylesheet" href="/css/reset.css" type="text/css">
        <link rel="stylesheet" href="/css/style.css" type="text/css">
        <link rel="icon" href="">

        <script type="text/javascript" src="/js/jquery-2.1.1.min.js"></script>
        <script type="text/javascript" src="/js/jquery.serialize-object.min.js"></script>
        <script type="text/javascript" src="/js/common.js"></script>
        <script type="text/javascript" src="/js/storage.js"></script>
        <script type="text/javascript" src="/js/paramManager.js"></script>
    </head>
    <body>
        <tiles:insertAttribute name="content"/>
        <tiles:insertAttribute name="footer"/>
    </body>
</html>