/* 这里面是整个系统业务相关的js代码 */
function getMenu() {
	// 可以服务端获取数据
	var menuData = [
		{
      id: '1',
      name: '首页',
			linkPath: '_blank.html',
      icon: '&#xe67f;',
      parentLinkId: '0',
    },
    {
      id: '2',
      name: '资讯管理',
			linkPath: '#',
      icon: '&#xe616;',
      parentLinkId: '0',
      subMenus: [
        {
          id: '3',
          name: '资讯列表',
          linkPath: 'article-list.html',
          icon: '',
          parentLinkId: '2',
        },
      ]
    },
    {
      id: '4',
      name: '员工管理',
			linkPath: '#',
      icon: '&#xe60d;',
      parentLinkId: '0',
      subMenus: [
        {
          id: '5',
          name: '员工列表',
          linkPath: 'member-list1.html',
          icon: '',
          parentLinkId: '7',
        },
		{
		  id: '6',
		  name: '员工评价',
		  linkPath: '#',
		  icon: '',
		  parentLinkId: '10',
		},
      ]
    },
    {
      id: '7',
      name: '新建账户',
			linkPath: '#',
      icon: '&#xe607;',
      parentLinkId: '0',
      subMenus: [
        {
          id: '8',
          name: '主管账户',
          linkPath: 'admin-role1.html',
          icon: '',
          parentLinkId: '10',
        },
        {
          id: '9',
          name: '员工账户',
          linkPath: 'admin-role2.html',
          icon: '',
          parentLinkId: '10',
        },
      ]
    },
    {
      id: '10',
      name: '外部查询',
			linkPath: '#',
      icon: '&#xe683;',
      parentLinkId: '0',
      subMenus: [
        {
          id: '11',
          name: '查询其他求职者',
          linkPath: 'member-list2.html',
          icon: '',
          parentLinkId: '14',
        },
      ]
    },
    {
      id: '12',
      name: '意见反馈',
			linkPath: '#',
      icon: '&#xe692;',
      parentLinkId: '0',
	  linkPath: 'feedback-list.html',
	},
  ];

  var pathname = window.location.pathname;
  recursion(menuData);
  
  function recursion(menuData) {
    if(menuData && menuData.length > 0) {
      for (var i in menuData) {
        var str = '';
        var pl = 24;
        if(menuData[i].subMenus && menuData[i].subMenus.length > 0) {
          str += '<li class="Hui-menu-item Hui-menu-item-first">'+
            '<div class="Hui-menu-title" style="padding-left: '+ pl +'px;"><i class="Hui-iconfont">'+menuData[i].icon+'</i> '+menuData[i].name+'<i class="Hui-iconfont Hui-admin-menu-dropdown-arrow">&#xe6d5;</i></div>'+
            '<ul class="Hui-menu-item-sub">';
            for(var j in menuData[i].subMenus) {
              if(pathname == menuData[i].subMenus[j].linkPath) {
                str+= '<li class="Hui-menu-item current" style="padding-left: '+ pl * 2 +'px;">';
              } else {
                str+= '<li class="Hui-menu-item" style="padding-left: '+ pl * 2 +'px;">';
              }
              str+= '<a data-href="'+menuData[i].subMenus[j].linkPath+'" data-title="'+menuData[i].subMenus[j].name+'" href="javascript:void(0)">';
              if(menuData[i].subMenus[j].icon) {
                str+= '<i class="Hui-iconfont">'+menuData[i].subMenus[j].icon+'</i> ';
              }
              str+= menuData[i].subMenus[j].name+'</a></li>';
            }
          str += '</ul></li>';
        } else {
          str+= '<li class="Hui-menu-item Hui-menu-item-first">';
          if(pathname == menuData[i].linkPath) {
            str+= '<div class="Hui-menu-title current" style="padding-left: '+ pl +'px;">';
          } else {
            str+= '<div class="Hui-menu-title" style="padding-left: '+ pl +'px;">';
          }
          str += '<a data-href="'+menuData[i].linkPath+'" data-title="'+menuData[i].name+'" href="javascript:void(0)"><i class="Hui-iconfont">'+menuData[i].icon+'</i> '+menuData[i].name+'</a></div><ul class="Hui-menu-item-sub"></ul></li>';
        }
        $('.Hui-admin-menu-dropdown .Hui-menu').append(str);
      }
    }
  }

  $(".Hui-admin-menu-dropdown .Hui-menu").Huifold({
		titCell: '.Hui-menu-title',
    mainCell: '.Hui-menu-item-sub',
    type: 3,
    className: 'open',
    item: '.Hui-menu-item-first',
	});
}

function runDatetimePicker(){
  $("#search-datetime-start").datetimepicker({
    language:  'zh-cn',
    format: 'yyyy-mm-dd',
    minView: "month",
    todayBtn:  1,
    autoclose: 1,
    endDate : new Date(),
  }).on('changeDate', function(event) {
    event.preventDefault();
    event.stopPropagation();
    var startTime = event.date;
    $('#search-datetime-end').datetimepicker('setStartDate',startTime);
  });
  $("#search-datetime-end").datetimepicker({
    language:  'zh-cn',
    format: 'yyyy-mm-dd',
    minView: "month",
    todayBtn:  1,
    autoclose: 1,
    endDate : new Date(),
  }).on('changeDate', function(event) {
    event.preventDefault();
    event.stopPropagation();
    var endTime = event.date;
    $("#search-datetime-start").datetimepicker('setEndDate',endTime);
  });
}

// 提取URL中参数为对象
function parseQueryString(url){
  var str = url.split('?')[1];
  var result = {};
  var temp = (str || '').split('&');
  for(var i=0; i<temp.length; i++){
    var temp2 = (temp[i] || '').split('=');
    result[temp2[0]] = temp2[1];
  }
  return result;
}

/*
  参数解释：
  title	标题
  url		请求的url
  id		需要操作的数据id
  w		弹出层宽度（缺省调默认值）
  h		弹出层高度（缺省调默认值）
*/
/*-------------------资讯管理-------------------------*/
/*资讯-添加*/
function article_add(title,url){
	var index = layer.open({
		type: 2,
		title: title,
		content: url
	});
	layer.full(index);
}

/*资讯-编辑*/
function article_edit(title,url,id){
	var index = layer.open({
		type: 2,
		title: title,
		content: url
	});
	layer.full(index);
}

/*资讯-删除*/
function article_del(obj,id){
	layer.confirm('确认要删除吗？',function(index){
		$.ajax({
			type: 'POST',
			url: '',
			dataType: 'json',
			success: function(data){
				$(obj).parents("tr").remove();
				layer.msg('已删除!',{icon:1,time:1000});
			},
			error:function(data) {
				console.log(data.msg);
			},
		});
	});
}

/*资讯-审核*/
function article_shenhe(obj,id){
	layer.confirm('审核文章？', {
		btn: ['通过','不通过','取消'],
		shade: false,
		closeBtn: 0
	},
	function(){
		$(obj).parents("tr").find(".td-manage").prepend('<a class="c-primary" onClick="article_start(this,id)" href="javascript:;" title="申请上线">申请上线</a>');
		$(obj).parents("tr").find(".td-status").html('<span class="label label-success radius">已发布</span>');
		$(obj).remove();
		layer.msg('已发布', {icon:6,time:1000});
	},
	function(){
		$(obj).parents("tr").find(".td-manage").prepend('<a class="c-primary" onClick="article_shenqing(this,id)" href="javascript:;" title="申请上线">申请上线</a>');
		$(obj).parents("tr").find(".td-status").html('<span class="label label-danger radius">未通过</span>');
		$(obj).remove();
    	layer.msg('未通过', {icon:5,time:1000});
	});
}

/*资讯-下架*/
function article_stop(obj,id){
	layer.confirm('确认要下架吗？',function(index){
		$(obj).parents("tr").find(".td-manage").prepend('<a style="text-decoration:none" onClick="article_start(this,id)" href="javascript:;" title="发布">发布</a>');
		$(obj).parents("tr").find(".td-status").html('<span class="label label-defaunt radius">已下架</span>');
		$(obj).remove();
		layer.msg('已下架!',{icon: 5,time:1000});
	});
}

/*资讯-发布*/
function article_start(obj,id){
	layer.confirm('确认要发布吗？',function(index){
		$(obj).parents("tr").find(".td-manage").prepend('<a style="text-decoration:none" onClick="article_stop(this,id)" href="javascript:;" title="下架">下架</a>');
		$(obj).parents("tr").find(".td-status").html('<span class="label label-success radius">已发布</span>');
		$(obj).remove();
		layer.msg('已发布!',{icon: 6,time:1000});
	});
}

/*资讯-申请上线*/
function article_shenqing(obj,id){
	$(obj).parents("tr").find(".td-status").html('<span class="label label-default radius">待审核</span>');
	$(obj).parents("tr").find(".td-manage").html("");
	layer.msg('已提交申请，耐心等待审核!', {icon: 1,time:2000});
}

/*-------------------图片管理-------------------------*/
/*图片-添加*/
function picture_add(title,url){
  var index = layer.open({
    type: 2,
    title: title,
    content: url
  });
  layer.full(index);
}
/*图片-查看*/
function picture_show(title,url,id){
  var index = layer.open({
    type: 2,
    title: title,
    content: url
  });
  layer.full(index);
}
/*图片-审核*/
function picture_shenhe(obj,id){
  layer.confirm('审核文章？', {
    btn: ['通过','不通过'],
    shade: false
  },
  function(){
    $(obj).parents("tr").find(".td-manage").prepend('<a class="c-primary" onClick="picture_start(this,id)" href="javascript:;" title="申请上线">申请上线</a>');
    $(obj).parents("tr").find(".td-status").html('<span class="label label-success radius">已发布</span>');
    $(obj).remove();
    layer.msg('已发布', {icon:6,time:1000});
  },
  function(){
    $(obj).parents("tr").find(".td-manage").prepend('<a class="c-primary" onClick="picture_shenqing(this,id)" href="javascript:;" title="申请上线">申请上线</a>');
    $(obj).parents("tr").find(".td-status").html('<span class="label label-danger radius">未通过</span>');
    $(obj).remove();
      layer.msg('未通过', {icon:5,time:1000});
  });
}
/*图片-下架*/
function picture_stop(obj,id){
  layer.confirm('确认要下架吗？',function(index){
    $(obj).parents("tr").find(".td-manage").prepend('<a style="text-decoration:none" onClick="picture_start(this,id)" href="javascript:;" title="发布">发布</a>');
    $(obj).parents("tr").find(".td-status").html('<span class="label label-defaunt radius">已下架</span>');
    $(obj).remove();
    layer.msg('已下架!',{icon: 5,time:1000});
  });
}

/*图片-发布*/
function picture_start(obj,id){
  layer.confirm('确认要发布吗？',function(index){
    $(obj).parents("tr").find(".td-manage").prepend('<a style="text-decoration:none" onClick="picture_stop(this,id)" href="javascript:;" title="下架">下架</a>');
    $(obj).parents("tr").find(".td-status").html('<span class="label label-success radius">已发布</span>');
    $(obj).remove();
    layer.msg('已发布!',{icon: 6,time:1000});
  });
}

/*图片-申请上线*/
function picture_shenqing(obj,id){
  $(obj).parents("tr").find(".td-status").html('<span class="label label-default radius">待审核</span>');
  $(obj).parents("tr").find(".td-manage").html("");
  layer.msg('已提交申请，耐心等待审核!', {icon: 1,time:2000});
}

/*图片-编辑*/
function picture_edit(title,url,id){
  console.log('收到的id', id);
  var index = layer.open({
    type: 2,
    title: title,
    content: url
  });
  layer.full(index);
}

/*图片-删除*/
function picture_del(obj,id){
  layer.confirm('确认要删除吗？',function(index){
    $(obj).parents("tr").remove();
    layer.msg('已删除!',{icon:1,time:1000});
  });
}

/*-------------------产品管理-------------------------*/
/*产品-添加*/
function product_add(title,url){
	var index = layer.open({
		type: 2,
		title: title,
		content: url
	});
	layer.full(index);
}
/*产品-查看*/
function product_show(title,url,id){
	var index = layer.open({
		type: 2,
		title: title,
		content: url
	});
	layer.full(index);
}
/*产品-审核*/
function product_shenhe(obj,id){
	layer.confirm('审核文章？', {
		btn: ['通过','不通过'],
		shade: false
	},
	function(){
		$(obj).parents("tr").find(".td-manage").prepend('<a class="c-primary" onClick="product_start(this,id)" href="javascript:;" title="申请上线">申请上线</a>');
		$(obj).parents("tr").find(".td-status").html('<span class="label label-success radius">已发布</span>');
		$(obj).remove();
		layer.msg('已发布', {icon:6,time:1000});
	},
	function(){
		$(obj).parents("tr").find(".td-manage").prepend('<a class="c-primary" onClick="product_shenqing(this,id)" href="javascript:;" title="申请上线">申请上线</a>');
		$(obj).parents("tr").find(".td-status").html('<span class="label label-danger radius">未通过</span>');
		$(obj).remove();
    	layer.msg('未通过', {icon:5,time:1000});
	});
}
/*产品-下架*/
function product_stop(obj,id){
	layer.confirm('确认要下架吗？',function(index){
		$(obj).parents("tr").find(".td-manage").prepend('<a style="text-decoration:none" onClick="product_start(this,id)" href="javascript:;" title="发布">发布</a>');
		$(obj).parents("tr").find(".td-status").html('<span class="label label-defaunt radius">已下架</span>');
		$(obj).remove();
		layer.msg('已下架!',{icon: 5,time:1000});
	});
}

/*产品-发布*/
function product_start(obj,id){
	layer.confirm('确认要发布吗？',function(index){
		$(obj).parents("tr").find(".td-manage").prepend('<a style="text-decoration:none" onClick="product_stop(this,id)" href="javascript:;" title="下架">下架</a>');
		$(obj).parents("tr").find(".td-status").html('<span class="label label-success radius">已发布</span>');
		$(obj).remove();
		layer.msg('已发布!',{icon: 6,time:1000});
	});
}
/*产品-申请上线*/
function product_shenqing(obj,id){
	$(obj).parents("tr").find(".td-status").html('<span class="label label-default radius">待审核</span>');
	$(obj).parents("tr").find(".td-manage").html("");
	layer.msg('已提交申请，耐心等待审核!', {icon: 1,time:2000});
}
/*产品-编辑*/
function product_edit(title,url,id){
	var index = layer.open({
		type: 2,
		title: title,
		content: url
	});
	layer.full(index);
}
/*产品-删除*/
function product_del(obj,id){
	layer.confirm('确认要删除吗？',function(index){
		$(obj).parents("tr").remove();
		layer.msg('已删除!',{icon:1,time:1000});
	});
}

/*-------------------用户管理-------------------------*/
/*用户-添加*/
function member_add(title,url){
  var index = layer.open({
		type: 2,
		title: title,
		content: url,
	});
  layer.full(index);
}
/*用户-查看*/
function member_show(title,url,id){
  var index = layer.open({
		type: 2,
		title: title,
		content: url,
    area: ['360px', '400px']
  });
  console.log(id)
}
/*用户-停用*/
function member_stop(obj,id){
  layer.confirm('确认要停用吗？',function(index){
    $(obj).parents("tr").find(".td-manage").prepend('<a style="text-decoration:none" onClick="member_start(this,id)" href="javascript:;" title="启用"><i class="Hui-iconfont">&#xe6e1;</i></a>');
    $(obj).parents("tr").find(".td-status").html('<span class="label label-defaunt radius">已停用</span>');
    $(obj).remove();
    layer.msg('已停用!',{icon: 5,time:1000});
  });
}

/*用户-启用*/
function member_start(obj,id){
  layer.confirm('确认要启用吗？',function(index){
    $(obj).parents("tr").find(".td-manage").prepend('<a style="text-decoration:none" onClick="member_stop(this,id)" href="javascript:;" title="停用"><i class="Hui-iconfont">&#xe631;</i></a>');
    $(obj).parents("tr").find(".td-status").html('<span class="label label-success radius">已启用</span>');
    $(obj).remove();
    layer.msg('已启用!',{icon: 6,time:1000});
  });
}
/*用户-编辑*/
function member_edit(title,url,id){
  var index = layer.open({
		type: 2,
		title: title,
		content: url
	});
  layer.full(index);
}
/*密码-修改*/
function change_password(title,url,id){
  var index = layer.open({
		type: 2,
		title: title,
		content: url,
    area: ['500px', '300px']
	});

}
/*用户-删除*/
function member_del(obj,id){
  layer.confirm('确认要删除吗？',function(index){
    $(obj).parents("tr").remove();
    layer.msg('已删除!',{icon:1,time:1000});
  });
}
/*用户-还原*/
function member_huanyuan(obj,id){
  layer.confirm('确认要还原吗？',function(index){

    $(obj).remove();
    layer.msg('已还原!',{icon: 6,time:1000});
  });
}

/*-------------------管理员管理-------------------------*/
/*管理员-增加*/
function admin_add(title,url){
  var index = layer.open({
		type: 2,
		title: title,
		content: url,
    area: ['800px', '600px']
	});
}
/*管理员-删除*/
function admin_del(obj,id){
  layer.confirm('确认要删除吗？',function(index){
    //此处请求后台程序，下方是成功后的前台处理……

    $(obj).parents("tr").remove();
    layer.msg('已删除!',{icon:1,time:1000});
  });
}
/*管理员-编辑*/
function admin_edit(title,url,id){
  var index = layer.open({
		type: 2,
		title: title,
		content: url,
    area: ['800px', '600px']
	});
}
/*管理员-停用*/
function admin_stop(obj,id){
  layer.confirm('确认要停用吗？',function(index){
    //此处请求后台程序，下方是成功后的前台处理……

    $(obj).parents("tr").find(".td-manage").prepend('<a onClick="admin_start(this,id)" href="javascript:;" title="启用" style="text-decoration:none"><i class="Hui-iconfont">&#xe615;</i></a>');
    $(obj).parents("tr").find(".td-status").html('<span class="label label-default radius">已禁用</span>');
    $(obj).remove();
    layer.msg('已停用!',{icon: 5,time:1000});
  });
}

/*管理员-启用*/
function admin_start(obj,id){
  layer.confirm('确认要启用吗？',function(index){
    //此处请求后台程序，下方是成功后的前台处理……

    $(obj).parents("tr").find(".td-manage").prepend('<a onClick="admin_stop(this,id)" href="javascript:;" title="停用" style="text-decoration:none"><i class="Hui-iconfont">&#xe631;</i></a>');
    $(obj).parents("tr").find(".td-status").html('<span class="label label-success radius">已启用</span>');
    $(obj).remove();
    layer.msg('已启用!', {icon: 6,time:1000});
  });
}

/*管理员-权限-添加*/
function admin_permission_add(title,url){
  var index = layer.open({
		type: 2,
		title: title,
		content: url,
	});
  layer.full(index);
}
/*管理员-权限-编辑*/
function admin_permission_edit(title,url,id){
  var index = layer.open({
		type: 2,
		title: title,
		content: url
	});
  layer.full(index);
}

/*管理员-权限-删除*/
function admin_permission_del(obj,id){
  layer.confirm('角色删除须谨慎，确认要删除吗？',function(index){
    $(obj).parents("tr").remove();
    layer.msg('已删除!',{icon:1,time:1000});
  });
}

/*-------------------角色管理-------------------------*/
/*管理员-角色-添加*/
function admin_role_add(title,url){
  var index = layer.open({
		type: 2,
		title: title,
		content: url,
	});
  layer.full(index);
}

/*管理员-角色-编辑*/
function admin_role_edit(title,url,id){
  var index = layer.open({
		type: 2,
		title: title,
		content: url,
	});
  layer.full(index);
}

/*管理员-角色-删除*/
function admin_role_del(obj,id){
  layer.confirm('角色删除须谨慎，确认要删除吗？',function(index){
    //此处请求后台程序，下方是成功后的前台处理……

    $(obj).parents("tr").remove();
    layer.msg('已删除!',{icon:1,time:1000});
  });
}

/*-------------------栏目管理-------------------------*/
/*系统-栏目-添加*/
function system_category_add(title,url){
  var index = layer.open({
		type: 2,
		title: title,
		content: url,
	});
  layer.full(index);
}
/*系统-栏目-编辑*/
function system_category_edit(title,url,id){
  var index = layer.open({
		type: 2,
		title: title,
		content: url
	});
  layer.full(index);
}
/*系统-栏目-删除*/
function system_category_del(obj,id){
  layer.confirm('确认要删除吗？',function(index){
    $(obj).parents("tr").remove();
    layer.msg('已删除!',{icon:1,time:1000});
  });
}

/*-------------------栏目管理-------------------------*/
/*数据字典-编辑*/
function system_data_edit(title,url,id){
  var index = layer.open({
		type: 2,
		title: title,
		content: url
	});
  layer.full(index);
}
/*数据字典-删除*/
function system_data_del(obj,id){
  layer.confirm('确认要删除吗？',function(index){
    $(obj).parents("tr").remove();
    layer.msg('已删除!',{icon:1,time:1000});
  });
}
