<%@page import="www.web1.utils.BaseUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="www.web1.javaBean.*" %>
<%@page import="www.web1.dataDAO.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="css/head.css" rel="stylesheet"/>
<style type="text/css">
	body{background-image: url("pic/六边形.jpg");width: 1300px;padding: 0;margin: 0;}
	.top>ul {position:fixed;width:1334px;list-style-type: none;margin: 0;padding: 0;overflow: hidden;background-color: #333;}
	li { float: left;}
	li a {display: block; color: white; text-align: center; padding: 14px 16px; text-decoration: none;}
	li a:hover:not(.active) {background-color: #111;}
	.active {background-color: #4CAF50;}
	.left{border-radius:5px;margin-left:75px;margin-right:20px;margin-top:50px;float:left;width: 800px;height:700px;background-image: url(../src/白板.jpg);}
	.left1{overflow: scroll;border-radius:5px;margin-left:10px;margin-top:10px;float:left;width: 600px;height:300px;background-color: whitesmoke;}
	.left1>p{width: 400px;margin-left: 100px;}
	.left2{border-radius:5px;margin-left:10px;margin-top:10px;float:left;width: 600px;line-height:30px;background-color: 	#F0F8FF;}
	.left2>a {margin-left: 20px;}
	.right{border-radius:5px;margin-top:50px;float:right;width: 400px;background-image: url("pic/白板.jpg");}
	.right1{border-radius:5px;margin-left:10px;margin-top:10px;float:left;width: 350px;height:100px;background-image: url("pic/蓝布.jpg");}
	.right2{border-radius:5px;margin-left:10px;margin-top:10px;float:left;width: 350px;height:200px;background-image: url("pic/模糊白砖.jpg");}
	span.a{color: 	#696969;}
	form {margin-left: 10px;width: 400px;}
</style>
<title>短文内容</title>
</head>
<body>
	<%
	String title = "1";
	String authorID = "1";
	String content = "1";
	if(request.getParameter("title") != null)
		title = BaseUtils.Decoder(request.getParameter("title"));
	if(request.getParameter("authorID") != null)
		authorID = request.getParameter("authorID");
	 EssayDAO essayDAO = new EssayDAO();
	 Eassy essay = essayDAO.get(authorID, title);
	 if(essay != null){
		 content = essay.getContent();
	 }
	%>
	<%@include file="head.jsp"%>
	<div class="left">
		<div class="left1">
			<h3 style="text-align: center;">题目</h3>
			<p><%=title %><a href="#"><%=authorID %></a></p>
			<p class="content"><%=content %></p>
		</div>
		
		<div class="left2">
			<a href="#">刘小铭</a>:<span class="a">hhhhh，真好笑，挺楼主</span><br />
			<a href="#">刘小铭</a>:<span class="a">hhhhh，真好笑，挺楼主</span><br />
			<a href="#">刘小铭</a>:<span class="a">hhhhh，真好笑，挺楼主</span><br />
			<a href="#">刘大铭</a>:<span class="a">楼上怕是水军吧!!!</span><br />
		
			
		</div>
		
		<form action="">
			<textarea name="comCon" rows="10" cols="30" wrap="physical">请输入评论</textarea>
			<input name="subCom" type="submit" value="评论"/>
		</form>
	</div>
<!---->
	<div class="right">
		<div class="right1">
			<h4 style="color: white;font-size: 150%;text-align: center;">刘力铭最近发表的文章</h4>
		</div>
		
		<div class="right2">
			<ul >
				<a href="#">西藏的水</a>
				<span class="">散文</span>
				<span class="">最近去到西藏...</span>
			</ul>
			<ul >
				<a href="#">西藏的水</a>
				<span class="">散文</span>
				<span class="">最近去到西藏...</span>
			</ul>
			<ul >
				<a href="#">西藏的水</a>
				<span class="">散文</span>
				<span class="">最近去到西藏...</span>
			</ul>
		</div>
		
	</div>
</body>
</html>