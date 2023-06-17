<%--
  Created by IntelliJ IDEA.
  User: incubu1
  Date: 2023/5/23
  Time: 9:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<div class="col-md-9">
    <div class="data_list">
        <div class="data_list_title"><span class="glyphicon glyphicon-signal"></span>&nbsp;数据报表 </div>
        <div class="container-fluid">
            <div class="row" style="padding-top: 20px;">
                <div class="col-md-12">
                    <div id="monthCharts" style="height: 500px"></div>
                    <div id="baiduMap" style="height: 600px;width: 100%"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<%--引入echarts--%>
<script type="text/javascript" src="statics/echarts/echarts.min.js"></script>
<script type="text/javascript" src="https://api.map.baidu.com/api?v=1.0&&type=webgl&ak=kdZnKg0kxgrdlZe8G4E3phdPmVp6MiZ3">
</script>
<script type="text/javascript">
    $.ajax({
        type:"get",
        url:"report",
        data:{
            actionName:"getLocation"
        },
        success:function(result){
            if(result.flag){
                loadBaiDuMap(result.resultInfo);
            }
        }
    })
    function loadBaiDuMap(resultInfo){
        /*地图设置*/
        //创建一个map实例
        var map = new BMapGL.Map("baiduMap");
        //设置中心点
        var point = new BMapGL.Point(112.882736,28.236581);
        //设置中心点的范围，数值越大，范围越小越精细
        map.centerAndZoom(point, 20);
        //开启鼠标滚轮缩放
        map.enableScrollWheelZoom(true);
        //添加比例尺控件
        var scaleCtrl=new BMapGL.ScaleControl();
        map.addControl(scaleCtrl);
        if(resultInfo!=null &resultInfo.length>0){
            //将用户所在位置设为中心点
            map.centerAndZoom(new BMapGL.Point(112.882736,28.236581),20);
            for (var i = 0; i < resultInfo.length; i++) {
                //创建点标记
                var marker = new BMapGL.Marker(new BMapGL.Point(resultInfo[i].lon,resultInfo[i].lat));
                // 在地图上添加点标记
                map.addOverlay(marker);
            }

        }
        //创建点标记
        var marker1 = new BMapGL.Marker(new BMapGL.Point(112.882395,28.236875));
        // 在地图上添加点标记
        /*map.addOverlay(marker1);*/
        var opts = {
            position: new BMapGL.Point(112.882395,28.236875), // 指定文本标注所在的地理位置
            offset: new BMapGL.Size(20, -10) // 设置文本偏移量
        };
        // 创建文本标注对象
        var label = new BMapGL.Label('欢迎来到马士兵教育长沙站', opts);
        // 自定义文本标注样式
        label.setStyle({
            color: 'blue',
            borderRadius: '5px',
            borderColor: '#ccc',
            padding: '10px',
            fontSize: '16px',
            height: '30px',
            lineHeight: '30px',
            fontFamily: '微软雅黑'
        });
        map.addOverlay(label);
    }



    $.ajax({
        type:"post",
        url:"report",
        data:{
           actionName:'monthChart'
        },
        success:function (result){
            //得到月份 x轴
            var monthArray=result.resultInfo.monthArray;
            //通过月份得到数据 y轴
            var dataArray=result.resultInfo.dataArray;
            //加载柱状图
            if(result.flag){
                loadMonthChart(monthArray,dataArray);
            }
        }
    })
    //加载柱状图的方法
    function loadMonthChart(monthArray,dataArray){
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('monthCharts'));
        // prettier-ignore  x轴的数据
        let dataAxis =monthArray;
        // prettier-ignore y轴的数据
        let data = dataArray;
        let yMax = 15;
        let dataShadow = [];
        for (var i = 0; i < data.length; i++) {
            dataShadow.push(yMax);
        }
        var option = {
            title: {
                text: '云记查询',
                subtext: '按照月份统计云记的数量',
                left:'left',
            },
            tooltip:{},
            legend:{
                data:['月份']
            },
            //x轴
            xAxis: {
                data:dataAxis,
                axisTick:{
                    show:false
                },
                axisLine:{
                    show:false
                },
            },
            //y轴
            yAxis: {
                axisTick:{
                    show:false
                },
                axisLine:{
                    show:false
                },
                axisLabel: {
                    color: '#999'
                }
            },
            dataZoom: [
                {
                    type: 'inside'
                }
            ],
            //系列
            series: [
                {
                    type: 'bar',//柱状图
                    data: data,
                    name:'月份',
                    showBackground: true,
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#83bff6' },
                            { offset: 0.5, color: '#188df0' },
                            { offset: 1, color: '#188df0' }
                        ])
                    },
                    emphasis: {
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: '#2378f7' },
                                { offset: 0.7, color: '#2378f7' },
                                { offset: 1, color: '#83bff6' }
                            ])
                        }
                    },

                }
            ]
        };
        // Enable data zoom when user click bar.
        const zoomSize = 6;
        myChart.on('click', function (params) {
            console.log(dataAxis[Math.max(params.dataIndex - zoomSize / 2, 0)]);
            myChart.dispatchAction({
                type: 'dataZoom',
                startValue: dataAxis[Math.max(params.dataIndex - zoomSize / 2, 0)],
                endValue:
                    dataAxis[Math.min(params.dataIndex + zoomSize / 2, data.length - 1)]
            });
        });

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    }
</script>