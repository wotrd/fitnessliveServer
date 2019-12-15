/**
 * Created by tengj on 2017/4/10.
 */
var grid_selector = "#jqGrid";
var pager_selector = "#jqGridPager";
var rowNum = 10; 			//每页显示记录数
var task = null;			//任务（新增或编辑）
var loading;
$(function () {
    $(window).resize(function () {
        $(grid_selector).setGridWidth($(window).width() * 0.95);
    });

    $(grid_selector).jqGrid({
        url: "/manager/ordermanager/getOrders",
        datatype: "json",
        mtype: 'POST',
        height: window.screen.height - 550,
        colModel: [
            {label: 'id', name: 'id', width: 75},
            {label: '图书名字', name: 'bName', width: 200},
            {label: '图书数量', name: 'bCount', width: 200},
            {label: '图书单价', name: 'bPrice', width: 200},
            {label: '订单总价', name: 'totalPrice', width: 200},
            {label: '买家姓名', name: 'buyerName', width: 200},
            {label: '下单时间', name: 'createTime', width: 200},
            {
                label: '订单状态', name: 'status', width: 200, formatter: function (cellvalue, options, cell) {
                    if (cellvalue == 1) {
                        return '已完成';
                    } else if (cellvalue == 2) {
                        return '未完成';
                    }
                    {
                        return '已取消';
                    }
                }
            },
            // { label: '取关', name: 'opt', width: 200,formatter: function(cellvalue, options, cell){
            //     return '<a class="btn btn-purple btn-sm" onclick="cancelFans(this);"' +
            //         'target="_blank">' + '<i class="fa fa-cog  fa-spin" aria-hidden="true"></i>取关</a>';
            // }},
            {label: '', name: '', width: 0.1},
        ],
        pager: pager_selector,
        rowNum: rowNum,
        rowList: [10, 30, 45], //可调整每页显示的记录数
        viewrecords: true,//是否显示行数
        altRows: true,  //设置表格 zebra-striped 值
        gridview: true, //加速显示
        multiselect: true,//是否支持多选
        multiselectWidth: 40, //设置多选列宽度
        multiboxonly: true,
        shrinkToFit: true, //此属性用来说明当初始化列宽度时候的计算类型，如果为ture，则按比例初始化列宽度。如果为false，则列宽度使用colModel指定的宽度
        forceFit: true, //当为ture时，调整列宽度不会改变表格的宽度。当shrinkToFit为false时，此属性会被忽略
        autowidth: true,
        loadComplete: function () {
            var table = this;
            setTimeout(function () {
                updatePagerIcons(table);
            }, 0);
        },
        gridComplete: function () {
            // 防止水平方向上出现滚动条
            removeHorizontalScrollBar();
        },
        jsonReader: {//jsonReader来跟服务器端返回的数据做对应
            root: "rows",   //包含实际数据的数组
            total: "total", //总页数
            records: "records", //查询出的记录数
            repeatitems: false //指明每行的数据是可以重复的，如果设为false，则会从返回的数据中按名字来搜索元素，这个名字就是colModel中的名字
        },
        emptyrecords: '没有记录!',
        loadtext: '正在查询服务器数据...'
    });

    //设置分页按钮组
    $(grid_selector).jqGrid('navGrid', pager_selector,
        {
            edit: false,
            // edittitle:'修改',
            // edittext:'修改',
            // editicon : 'icon-pencil blue',
            // editfunc :editUser,
            add: false,
            // addtitle:'新增',
            // addtext:'新增',
            // addicon : 'icon-plus-sign purple',
            // addfunc :addUser,
            del: false,
            // deltitle:'删除',
            // deltext:'删除',
            // delicon : 'icon-trash red',
            // delfunc:delUser,
            refresh: true,
            refreshicon: 'icon-refresh green',
            beforeRefresh: refreshData,
            search: false,
            view: false,
            alertcap: "警告",
            alerttext: "请选择需要操作的用户!"
        }
    );

    //查询点击事件
    $("#queryOrderBtn").click(function () {
        var qryBusinessName = $("#qryBusinessName").val();
        var qryBuyerName = $("#qryBuyerName").val();
        $(grid_selector).jqGrid('setGridParam', {
            postData:
                {
                    bName: qryBusinessName,
                    buyerName: qryBuyerName
                },
            page: 1
        }).trigger("reloadGrid");
    });

    //新增粉丝，弹出新增窗口
    $("#addOrderBtn").click(function () {
        initData();
        $("#addOrderModal").modal({
            keyboard: false,
            show: true,
            backdrop: "static"
        });

    });

    //修改用户，弹出修改窗
    $("#modifyOrderBtn").click(function () {
        var rows = $(grid_selector).getGridParam('selarrrow');
        if (rows == 0) {
            // $.messager.alert("温馨提示","请选择一行记录！");
            layer.msg('请选择一行记录！', {icon: 7, time: 2000}); //2秒关闭（如果不配置，默认是3秒）
            return;
        } else if (rows.length > 1) {
            // $.messager.alert("温馨提示","不能同时修改多条记录！");
            layer.msg('不能同时修改多条记录！', {icon: 7, time: 2000}); //2秒关闭（如果不配置，默认是3秒）
            return;
        } else {
            initUpdateData();
            $("#upOrderModal").modal({
                keyboard: false,
                show: true,
                backdrop: "static"
            });
        }
    });

    //删除用户方法 选择多个的话，行id用逗号隔开比如 3,4
    $("#deleteOrderBtn").click(function () {
        var rows = $(grid_selector).jqGrid("getGridParam", "selarrrow");
        var uids = new Array(rows.length);
        for (var i = 0; i < rows.length; i++) {
            //行数据
            var rowData = $(grid_selector).jqGrid('getRowData', rows[i]);
            //colModel为uid
            uids[i] = rowData.id;
            // alert(uids[i]);
        }
        if (rows.length > 0) {
            $.messager.confirm("温馨提示", "是否确定删除所选记录？", function () {
                $.ajax({
                    url: "/manager/ordermanager/delete",
                    cache: false,
                    type: "post",
                    data: {"ids": uids.join(",")},
                    beforeSend: function () {
                        loading = layer.load("正在删除中...");
                    },
                    success: function (result) {
                        $.messager.alert(result.message);
                        refreshData();
                    }, error: function () {
                        $.messager.alert("温馨提示", "请求错误!");
                    },
                    complete: function () {
                        layer.close(loading);
                    }
                });
            });
        } else {
            //两种风格的提示,layer或者messager自己选择一种用即可。
            // $.messager.alert("温馨提示","至少选择一行记录！");
            layer.msg('至少选中一行记录！', {icon: 7, time: 2000}); //2秒关闭（如果不配置，默认是3秒）
        }
    });

    //编辑对话框取消点击事件
    $('#candelSaveOrderBtn').click(function () {
        $("#addOrderModal").modal('hide');
    });
    //保存教程
    $('#saveOrderBtn').click(function () {
        saveOrder();
    });
    $('#upOrderBtn').click(function () {
        updateOrder();
    });

});

function removeHorizontalScrollBar() {
    $("div.ui-state-default.ui-jqgrid-hdiv.ui-corner-top").css("width", parseInt($("div.ui-state-default.ui-jqgrid-hdiv.ui-corner-top").css("width")) + 1 + "px");
    $(grid_selector).closest(".ui-jqgrid-bdiv").css("width", parseInt($(grid_selector).closest(".ui-jqgrid-bdiv").css("width")) + 1 + "px");
}

//初始化数据
function initData() {
    $('#bName').val("");
    $('#bCount').val("");
    $('#bPrice').val("");
    $('#buyerName').val("");
    $('#sellName').val("");
}


//初始化修改用户数据
function initUpdateData() {
    var rows = $(grid_selector).getGridParam('selarrrow');
    var data = $(grid_selector).jqGrid('getRowData', rows[0]);
    $("#id").val(data.id);
    $('#upBname').val(data.bName);
    $('#upBcount').val(data.bCount);
    $("#upBprice").val(data.bPrice);
    $("#upBuyerName").val(data.buyerName);
    $("#upSellName").val(data.sellName);

}

/**
 * 保存粉丝
 */
function saveOrder() {
    var bName = $('#bName').val();
    var bCount = $('#bCount').val();
    var bPrice = $('#bPrice').val();
    var buyerName = $('#buyerName').val();
    var sellName = $('#sellName').val();

    $.ajax({
        url: "/manager/ordermanager/addOrder",
        cache: false,
        dataType: 'json',
        data: {
            bName: bName,
            bCount: bCount,
            bPrice: bPrice,
            buyerName: buyerName,
            sellName: sellName,
        },
        type: 'post',
        beforeSend: function () {
            // 禁用按钮防止重复提交
            $('#saveOrderBtn').attr({disabled: "disabled"});
        },
        success: function (result) {
            if (result.flag == true) {
                $.messager.alert('温馨提示', result.message);
                $("#addOrderModal").modal('hide');
                refreshData();
            } else {
                $.messager.alert('温馨提示', result.message);
            }
        },
        complete: function () {
            $('#saveOrderBtn').removeAttr("disabled");
        },
        error: function (data) {
            console.info("error: " + data.responseText);
        }
    });
}

/**
 * 修改图书
 */
function updateOrder() {
    var submit_option = {
        url: "/manager/ordermanager/update",//默认是form action
        method: "POST",
        success: function (result) {
            if (result.flag == true) {
                $.messager.alert('温馨提示', result.message);
                $("#upOrderModal").modal('hide');
                refreshData();
            } else {
                $.messager.alert('温馨提示', result.message);
                $("#upOrderModal").modal('hide');
            }

        }, error: function () {
            $.messager.alert('服务器繁忙，请稍后再试...');
            $("#upOrderModal").modal('hide');
        }
    }
    $("#upOrderForm").ajaxSubmit(submit_option);
}

function refreshData() {
    $(grid_selector).jqGrid('setGridParam', {
        postData: {account: null, uid: null},
        page: 1
    }).trigger("reloadGrid");
}


//这个是分页图标，必须添加
function updatePagerIcons(table) {
    var replacement =
        {
            'ui-icon-seek-first': 'icon-double-angle-left bigger-140',
            'ui-icon-seek-prev': 'icon-angle-left bigger-140',
            'ui-icon-seek-next': 'icon-angle-right bigger-140',
            'ui-icon-seek-end': 'icon-double-angle-right bigger-140'
        };
    $('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function () {
        var icon = $(this);
        var $class = $.trim(icon.attr('class').replace('ui-icon', ''));
        console.info($class);
        if ($class in replacement) icon.attr('class', 'ui-icon ' + replacement[$class]);
    });
}
