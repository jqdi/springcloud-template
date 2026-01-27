<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html lang="cmn-hans">
<head>
<title>登录</title>
</head>
<body>
	<div>
		<form name="form">
			<div>
				<div class="list-group-item">
					<input type="text" name="username" placeholder="用户名" value="system"
						class="form-control no-border" required>
				</div>
				<div class="list-group-item">
					<input type="password" placeholder="密　码" name="password"
						value="666666" class="form-control no-border" required>
				</div>
			</div>
			<button type="button" onclick="tologin()" class="btn btn-lg btn-info btn-block">登录</button>
		</form>
	</div>
	<script src="<%=request.getContextPath()%>/assets/js/jquery.min.js"></script>
	<script>
	
	function tologin(){
		var params = {
				"username": $("input[name='username']").val(),
				"password": $("input[name='password']").val()
		};
		$.ajax({
	        url:"<%=request.getContextPath()%>/auth/tologin",
	        type: "POST",
	        //data:obj,表示提交到控制器的是JSON对象   -->ontentType:"application/x-www-form-urlencoded"
	        data:JSON.stringify(params),
	        contentType: "application/json",
	        success:function(res){
	            console.log(res);
	            if (res.code == 0) {
	            	// 登录成功
	            	window.localStorage.setItem("Authorization", res.data.token);
	            	window.location.href = res.data.url + "?Authorization="+res.data.token;
	            }else{
	            	alert(res.message);
	            }
	        }
	    });
	}
	</script>
</body>
</html>
