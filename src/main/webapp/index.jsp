<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%--<%@taglib  uri="http://java.sun.com/jstl/core" prefix="c"%>--%>
<%@taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<html lang="zh"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>云R记</title>
    <link href="statics/css/note.css" rel="stylesheet">
    <link href="statics/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="statics/sweetalert/sweetalert2.min.css" rel="stylesheet">
    <script src="statics/js/jquery-1.11.3.js"></script>
    <script src="statics/bootstrap/js/bootstrap.js"></script>
    <script src="statics/sweetalert/sweetalert2.min.js"></script>
    <script src="statics/js/util.js"></script>
    <!-- 配置文件 -->
    <script type="text/javascript" src="statics/ueditor/ueditor.config.js"></script>
    <!-- 编辑器源码文件 -->
    <script type="text/javascript" src="statics/ueditor/ueditor.all.js"></script>
    <style type="text/css">
        body {
            padding-top: 60px;
            padding-bottom: 40px;
            background: url(statics/images/bg.gif) repeat;
        }
    </style>
</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" style="font-size:25px" href="main">云日记</a>
        </div>
        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li <c:if test="${menuPage=='index'}">class="active"</c:if> ><a href="index"><i class="glyphicon glyphicon-cloud"></i>&nbsp;主&nbsp;&nbsp;页</a></li>
                <li <c:if test="${menuPage=='note'}">class="active"</c:if>><a href="note?actionName=view"><i class="glyphicon glyphicon-pencil"></i>&nbsp;发表云记</a></li>
                <li <c:if test="${menuPage=='type'}">class="active"</c:if>><a href="type?actionName=list"><i class="glyphicon glyphicon-list"></i>&nbsp;类别管理</a></li>
                <li <c:if test="${menuPage=='user'}">class="active"</c:if>><a href="user?actionName=userCenter"><i class="glyphicon glyphicon-user"></i>&nbsp;个人中心</a> </li>
                <li <c:if test="${menuPage=='data'}">class="active"</c:if>><a href="report?actionName=dataReport"><i class="glyphicon glyphicon-signal"></i>&nbsp;数据报表</a></li>

            </ul>
            <form class="navbar-form navbar-right" role="search" action="index">
                <div class="form-group">
                    <input type="hidden" name="actionName" value="searchTitle">
                    <input type="text" name="title" class="form-control" placeholder="搜索云记" value="${title}">
                </div>
                <button type="submit" class="btn btn-default">查询</button>
            </form>
        </div>
    </div>
</nav>
<div class="container">
    <div class="row-fluid">
        <div class="col-md-3">
            <div class="data_list">
                <div class="data_list_title"><span class="glyphicon glyphicon-user"></span>&nbsp;个人中心&nbsp;&nbsp;&nbsp;&nbsp;<a href="user?actionName=logout">退出</a></div>
                <div class="userimg">
                    <img src="user?actionName=userHead&imageName=${user.head}">
                </div>
                <div class="nick">${user.nick}</div>
                <div class="mood">(${user.mood})</div>
            </div>
            <div class="data_list">
                <div class="data_list_title">
					<span class="glyphicon glyphicon-calendar">
					</span>&nbsp;云记日期
                </div>

                <div>
                    <ul class="nav nav-pills nav-stacked">
<%--                        <li><a href="main?act=searchDate&amp;val=2016%E5%B9%B408%E6%9C%88&amp;valStr=2016%E5%B9%B408%E6%9C%88">2016年08月 <span class="badge">24</span></a></li>--%>

<%--                        <li><a href="main?act=searchDate&amp;val=2016%E5%B9%B407%E6%9C%88&amp;valStr=2016%E5%B9%B407%E6%9C%88">2016年07月 <span class="badge">1</span></a></li>--%>

<%--                        <li><a href="main?act=searchDate&amp;val=2016%E5%B9%B406%E6%9C%88&amp;valStr=2016%E5%B9%B406%E6%9C%88">2016年06月 <span class="badge">1</span></a></li>--%>
                        <c:forEach items="${dateInfo}" var="item">
                            <li><a href="index?actionName=searchDate&date=${item.groupName}">${item.groupName} <span class="badge">${item.noteCount}</span></a></li>
                        </c:forEach>
                    </ul>
                </div>

            </div>
            <div class="data_list">
                <div class="data_list_title">
					<span class="glyphicon glyphicon-list-alt">
					</span>&nbsp;云记类别
                </div>

                <div>
                    <%--<ul class="nav nav-pills nav-stacked">

                        &lt;%&ndash;<li li="li_1"><a href="main?act=searchType&amp;val=5&amp;valStr=test">test <span class="badge">0</span></a></li>

                        <li li="li_2"><a href="main?act=searchType&amp;val=3&amp;valStr=%E5%B0%9A%E5%AD%A6%E5%A0%82%E7%AC%94%E8%AE%B0">笔记 <span class="badge">12</span></a></li>

                        <li li="li_3"><a href="main?act=searchType&amp;val=2&amp;valStr=%E6%8A%80%E6%9C%AF">技术 <span class="badge">5</span></a></li>

                        <li li="li_4"><a href="main?act=searchType&amp;val=4&amp;valStr=%E8%80%81%E8%A3%B4%E8%AF%AD%E5%BD%95">语录 <span class="badge">9</span></a></li>&ndash;%&gt;

                    </ul>--%>
                    <ul id="typeUl" class="nav nav-pills nav-stacked">
                        <c:forEach items="${typeInfo}" var="item">
                            <li id="li_${item.typeId}"><a href="index?actionName=searchId&typeId=${item.typeId}"><span id="sp_${item.typeId}">${item.groupName}</span> <span class="badge">${item.noteCount}</span></a></li>
                        </c:forEach>
                    </ul>
                </div>

            </div>
        </div>
    </div>
    <%--  动态页面包含  --%>
    <%--云记列表--%>
   <%-- <jsp:include page="note/list.jsp"></jsp:include>--%>
    <%--个人中心--%>
    <%--<jsp:include page="user/list.jsp"></jsp:include>--%>
    <%--类型列表--%>
    <%--<jsp:include page="type/list.jsp"></jsp:include>--%>

    <%--通过后台动态显示页面--%>
    <%--如果获取到后台设置的值，就设置，否则采用默认值--%>
    <%--上面引入jstl的核心包，做条件判断--%>
    <c:if test="${empty whichPage}">
        <%--当这个值为空时，进入云集列表的页面--%>
        <jsp:include page="note/list.jsp"></jsp:include>
    </c:if>
    <c:if test="${!empty whichPage}">
        <%--不为空就根据值来选择进入哪个页面--%>
        <jsp:include page="${whichPage}"></jsp:include>
    </c:if>
</div>

</body></html>
