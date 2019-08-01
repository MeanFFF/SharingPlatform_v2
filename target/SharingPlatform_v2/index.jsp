<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>
<form action="file/upload" enctype="multipart/form-data" method="post">
    <input type="file" name="uploadFile"><br>
    文件名:<input type="text" name="name"><br>
    分类名:<input type="text" name="categoryId"><br>
    分数:<input type="text" name="score"><br>
    详情:<input type="text" name="detail"><br>
    <input type="submit">
</form>
</body>
<h1>hello world!</h1>
</html>
