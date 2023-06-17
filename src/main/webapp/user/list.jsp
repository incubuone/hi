<%--
  Created by IntelliJ IDEA.
  User: incubu1
  Date: 2023/5/18
  Time: 9:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<div class="col-md-9">
  <%--  <script>
        function checkUser(){
            var nickName=$('#nick').val();
            if(nickName.length==0){
                $("#msg").html("昵称不能为空,请核对！");
                return false;
            }
            return true;
        }
    </script>--%>
    <div class="data_list">
        <div class="data_list_title"><span class="glyphicon glyphicon-edit"></span>&nbsp;个人中心 </div>
        <div class="container-fluid">
            <div class="row" style="padding-top: 20px;">
                <div class="col-md-8">
                    <form class="form-horizontal" method="post" action="user" enctype="multipart/form-data" >
                        <div class="form-group">
                            <%--设置隐藏域 存储用户行为--%>
                            <input type="hidden" name="actionName" value="updateUser">
                            <label for="nickName" class="col-sm-2 control-label">昵称:</label>
                            <div class="col-sm-3">
                                <input class="form-control" name="nick" id="nickName" placeholder="" value="${user.nick}">
                            </div>
                            <label for="img" class="col-sm-2 control-label">头像:</label>
                            <div class="col-sm-5">

                                <input type="file" id="img" name="img">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="mood" class="col-sm-2 control-label">心情:</label>
                            <div class="col-sm-10">
                                <textarea class="form-control" name="mood" id="mood" rows="3">${user.mood}</textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-2 col-sm-10">
                                <button type="submit" id="btn" class="btn btn-success" onclick="return updateUser()">修改</button>&nbsp;&nbsp;<span style="color:red;font-size:12px" id="msg"></span>
                                <span style="color:green;font-size:12px" id="msg2"></span>
                            </div>
                        </div>
                    </form>
                </div>
                <%--个人中心头像的照片--%>
                <div class="col-md-4"><img style="width:240px;height:180px" src="user?actionName=userHead&imageName=${user.head}"></div>
            </div>
        </div>
    </div>
    <script>
        $("#nickName").blur(function (){
            var nickName=$("#nickName").val();
            if(isEmpty(nickName)){
                $("#msg").html("昵称不能为空");
                $("#msg2").html("");
                $("#btn").prop("disabled",true);
                return;
            }
            //判断昵称是否做了修改
            var nick="${user.nick}";
            if(nick==nickName){
                return;
            }else{
                $.ajax({
                    type:"get",
                    url:"user",
                    data:{
                        actionName:"checkNick",
                        nickName:nickName
                    },
                    success:function (flag){
                        if(flag=="true"){
                            //可用
                            $("#btn").prop("disabled",false);
                            $("#msg2").html("可以更换")
                        }else{
                            $("#btn").prop("disabled",true);
                            $("#msg").html("昵称已存在，请更换")
                        }
                    }
                });
            }
        }).focus(function (){
            $("#msg").html("");
            $("#msg2").html("");
            $("#btn").prop("disabled",false);
        });
        function updateUser(){
            var nickName=$("#nickName").val();
            if(isEmpty(nickName)) {
                $("#msg").html("昵称不能为空");
                $("#msg2").html("");
                $("#btn").prop("disabled", true);
                return false;
            }
            return true;
        }
       /* $(function(){
                var target=$("#nickName");
                //验证昵称唯一
                target.blur(
                    function(){
                        $("#btn").attr('disabled',false);
                        $("#msg").html('');
                        var value =target.val();
                        //不用ajax验证，没有填写或者与之前内容相同
                        if(value.length==0 ||value=='我思故我在'){
                            target.val('我思故我在');
                            return ;
                        }

                        //ajax验证
                        $.getJSON("user",{
                            act:'unique',
                            nick:value
                        },function(data){
                            if(data.resultCode==-1){
                                $("#msg").html(value+"此用户名已存在");
                                target.val('');
                                $("#btn").attr('disabled',true);
                            }else{
                                $("#btn").attr('disabled',false);
                            }
                        });
                    }

                );
            }
        );*/

    </script>
</div>
