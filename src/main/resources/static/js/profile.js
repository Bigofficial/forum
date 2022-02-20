$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
    //蓝色样式 可以关注
	if($(btn).hasClass("btn-info")) {
		// 关注TA
        $.post(
            CONTEXT_PATH + "/follow",
            //当前节点前一个节点的val  我们设置了一个hidden的
            {"entityType": 3, "entityId": $(btn).prev().val()},
            function (data) {
                //转成js对象
                data = $.parseJSON(data);
                if (data.code == 0) {
                    //刷新页面
                    window.location.reload();
                } else {
                    alert(data.msg);
                }
            }
        );


        // $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
	} else {
		// 取消关注
        $.post(
            CONTEXT_PATH + "/unfollow",
            {"entityType": 3, "entityId": $(btn).prev().val()},
            function (data) {
                data = $.parseJSON(data);
                if (data.code == 0) {
                    window.location.reload();
                } else {
                    alert(data.msg);
                }
            }
        );
        // $(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
	}
}