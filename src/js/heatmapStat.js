$(document).ready(function() {
    $('#web-container').mask();
    var result = document.getElementById("heatmapList").value;
//    var totalCount = document.getElementById("totalCount").value;
    var jArr = eval(result);
//    var heatmap = h337.create({"element":document.getElementById("web-container"), "radius":10, "visible":true});
//    for(var i=0;i<jArr.length;i++){
//        heatmap.store.addDataPoint(jArr[i][0],jArr[i][1],parseInt(jArr[i][2]));
//    }
    var canvas = document.getElementsByTagName('canvas')[0];
    var heatmap = createWebGLHeatmap({canvas: canvas, intensityToAlpha:true});
    for(var i=0;i<jArr.length;i++){
//        heatmap.addPoint(jArr[i][0],jArr[i][1],parseInt(jArr[i][2])*30,parseInt(jArr[i][2])*30/parseInt(totalCount));
        heatmap.addPoint(jArr[i][0],jArr[i][1],18,0.3+7*(parseInt(jArr[i][2])-1)/10*parseInt(jArr[i][2]));
    }
    heatmap.update();
    heatmap.display();

    document.getElementsByTagName("canvas")[0].onmousedown = canvasMouseDown;
    document.getElementsByTagName("canvas")[0].onmouseup = canvasMouseUp;
    document.getElementsByTagName("canvas")[0].onmousemove = canvasMouseMove;
    $('#paramBt').click(function(){
        var url = document.getElementById("param").value;
        var proto = document.getElementById("proto").value;
        document.getElementById("main").src = proto+"://"+url;
    });

    var tour;
    if($('.tour1').length && typeof(tour)=='undefined'){
        tour = new Tour();
        tour.addStep({
            element: ".tour1",
            title: "选择区域热点",
            placement: "left",
            content: "使用鼠标框选热点区域来统计分析区域热点的明细信息."
        });
        tour.addStep({
            element: ".tour2",
            title: "切换查看模式",
            placement: "left",
            content: "按SHIFT关闭热点图操作当前页面,在黑色区域下按SHIFT重新开启热点图."
        });
        tour.restart();
    }
});

$(document).keydown(function(event){
    checkShift(event);
});

var maskControl = "1";
function checkShift(event){
    var e = event || window.event;
    if(e.shiftKey==true){
        if(maskControl=="1"){
            $('#web-container').unmask();
            document.getElementById('paramContent').style.visibility = "visible";
            document.getElementsByTagName("canvas")[0].style.display = "none";
            maskControl = "0";
        }else{
            $('#web-container').mask();
            document.getElementById('paramContent').style.visibility = "hidden";
            document.getElementsByTagName("canvas")[0].style.display = "block";
            maskControl = "1";
        }
    }
}

function canvasMouseMove(e){
    var ondrag = document.getElementById("ondrag").value;
    if(ondrag!="1"){
        return;
    }
    e = e || window.event;
    var mousePosition = getMousePosition(e);
    var downPosition = document.getElementById("downPosition").value;
    var left = downPosition.split(",")[0];
    var top = downPosition.split(",")[1];
    var width = mousePosition.mx-downPosition.split(",")[0];
    var height = mousePosition.my-downPosition.split(",")[1];

    if(left==mousePosition.mx&&top==mousePosition.my){
        return;
    }
    dragDIV(top,left,height,width);
}

function canvasMouseDown(e){
    e = e || window.event;
    var position = getMousePosition(e);
    document.getElementById("downPosition").value = position.mx+","+position.my;
    document.getElementById("ondrag").value = "1";
}

function resetDrag(){
    document.getElementById("ondrag").value = "0";
    document.getElementById("dragDivFlag").value = "0";
    DelDIV(document.getElementById("dragDiv"));
}

function canvasMouseUp(e){
    var regionCount = document.getElementById("regionCount").value;
    if(regionCount>=3){
        resetDrag();
        alert("最多只能同时选中三块区域!");
        return;
    }
    e = e || window.event;
    var upPosition = getMousePosition(e);
    var downPosition = document.getElementById("downPosition").value;
    var left = downPosition.split(",")[0];
    var top = downPosition.split(",")[1];
    var width = upPosition.mx-downPosition.split(",")[0];
    var height = upPosition.my-downPosition.split(",")[1];
    if(width*height<2500){
        resetDrag();
        return;
    }
    var mxBegin = upPosition.mx>left?left:upPosition.mx;
    var mxEnd = upPosition.mx>left?upPosition.mx:left;
    var myBegin = upPosition.my>top?top:upPosition.my;
    var myEnd = upPosition.my>top?upPosition.my:top;
    var url = document.getElementById("pk.url").value;
    var sysId = document.getElementById("pk.sysId").value;
    var statdateStart = document.getElementById("statdateStart").value;
    var statdateEnd = document.getElementById("statdateEnd").value;
    var statdate = document.getElementById("pk.statdate").value;
    var widthheight = document.getElementById("pk.widthheight").value;
    var dimension = "area";
    var jsonParam = {pk:{url:url, sysId:sysId, statdate:statdate, widthheight:widthheight}, mxBegin:mxBegin, mxEnd:mxEnd, myBegin:myBegin, myEnd:myEnd, statdateStart:statdateStart, statdateEnd:statdateEnd, dimension:dimension};
    $.ajax({
        type: "POST",
        url: "/dashboard/dataStat/heatmap/regionDetail.do",
        async: false,
        dataType: "json",
        contentType : 'application/json',
        data: JSON.stringify(jsonParam),
        error: function(data) {
            alert("查询失败！请稍后重试..错误码:"+data.status);
        },
        success: function(data){
            var jArr = eval(data);
            var regionCount = jArr[jArr.length-1]==null?0:jArr[jArr.length-1];
            var totalCount = document.getElementById("totalCount").value;
            var attention =((regionCount/parseInt(totalCount))*100).toFixed(2);
            createDIV(regionCount,attention,top,left,height,width,jsonParam);
        }
    });
    resetDrag();
}

function getMousePosition(e) {
    if (e.pageX || e.pageY) {
        return {
            mx : e.pageX,
            my : e.pageY
        }
    }
    return {
        mx : e.clientX + document.body.scrollLeft
            - document.body.clientLeft,
        my : e.clientY + document.body.scrollTop
            - document.body.clientTop
    }
}

function createDIV(regionCount,attention,top,left,height,width,heatmap){
    var regionMark = document.getElementById("regionMark").value;
    var num = "";
    if(regionMark.indexOf("1")<0){
        num = "1";
    }else if(regionMark.indexOf("2")<0){
        num = "2";
    }else if(regionMark.indexOf("3")<0){
        num = "3";
    }
    document.getElementById("regionMark").value = regionMark+num;

    var div = document.createElement("div");
    div.id = "div"+num;
    div.style.top= top+"px";
    div.style.left = left+"px";
    div.style.height = height+"px";
    div.style.width = width+"px";
    div.style.position = "absolute";
    div.style.background="rgba(28,145,219,0.6)";
    div.style.zIndex="99999998";
    div.style.border = "1px solid white";
    div.value = regionCount;

    var div2 = document.createElement("div");
    div2.id = "div2"+num;
    div2.style.top = top+"px";
    //超出屏幕后折返
    if(((parseInt(left)+parseInt(width))+336)>document.body.offsetWidth){
        div2.style.left = (parseInt(left) - 336)+"px";
    }else{
        div2.style.left = (parseInt(left)+parseInt(width))+"px";
    }
    div2.style.height = height+"px";
    div2.style.width = width+"px";
    div2.style.zIndex="99999998";
    div2.style.position = "absolute";

    var jArr = queryHeatmapByDimension(heatmap);

    var regionContent = buildRegionContent(jArr,num,regionCount);

    div.innerHTML = "<div onclick='closeRegion("+num+");' class='box-icon'><a href='#' class='btn btn-close btn-round'><i class='icon-remove'></i></a></div><div style='color:white;text-align:center;margin-top:"+height*0.4+"px;'><font style='font-size: 18px;'>"+regionCount+" ( "+attention+"% )</font></div>";
    div2.innerHTML = '<div class="heat-result drag-result-0 stop-drag scale" id="drag_result_0" style="display: block;">' +
        '<div class="title stop-drag">区域</div>' +
        '<div class="overview">' +
        '<div class="o-title"><em class="o-sum">点击量</em>聚焦度</div><br>' +
        '<div class="o-content"><em class="o-sum">'+regionCount+'</em>'+attention+'%</div>' +
        '</div>' +
        '<div class="source">维度：<select name="source_keys" id="dimension'+num+'" onchange="doSelectDimension('+num+')"><option value="area">地域</option><option value="browserName">浏览器</option></select></div>'+
        '<div class="content"><div class="c-title"><em class="c-source">来源</em><em class="c-num">点击量</em><em class="c-atta">聚焦度</em></div>' +
        '<div class="c-content">' + regionContent +
        '</div></div>'+
        '<div class="bottom"><span class="pages"></span></div></div>';

    document.body.appendChild(div);
    document.body.appendChild(div2);
    divCountAdd();
}


function doSelectDimension(num){
    var regionCount = document.getElementById("div"+num).value;
    var dimension = document.getElementById("dimension"+num).value;
    var myBegin = parseInt(document.getElementById("div"+num).style.top);
    var mxBegin = parseInt(document.getElementById("div"+num).style.left);
    var mxEnd = parseInt(document.getElementById("div"+num).style.left)+parseInt(document.getElementById("div"+num).style.width);
    var myEnd = parseInt(document.getElementById("div"+num).style.top)+parseInt(document.getElementById("div"+num).style.height);
    var url = document.getElementById("pk.url").value;
    var sysId = document.getElementById("pk.sysId").value;
    var statdateStart = document.getElementById("statdateStart").value;
    var statdateEnd = document.getElementById("statdateEnd").value;
    var statdate = document.getElementById("pk.statdate").value;
    var widthheight = document.getElementById("pk.widthheight").value;
    var heatmap = {pk:{url:url, sysId:sysId, statdate:statdate, widthheight:widthheight}, mxBegin:mxBegin, mxEnd:mxEnd, myBegin:myBegin, myEnd:myEnd, statdateStart:statdateStart, statdateEnd:statdateEnd, dimension:dimension};

    var jArr = queryHeatmapByDimension(heatmap);
    var parentContent = $("#dimensionContent"+num).parent();
    $("#dimensionContent"+num).remove();

    var regionContent = buildRegionContent(jArr,num,regionCount);
    parentContent.append(regionContent);
}

function queryHeatmapByDimension(heatmap){
    var jArr = 0;
    $.ajax({
        type: "POST",
        url: "/dashboard/dataStat/heatmap/regionDetailByDimension.do",
        async: false,
        dataType: "json",
        contentType : 'application/json',
        data: JSON.stringify(heatmap),
        error: function(data) {
            alert("查询失败！请稍后重试..错误码:"+data.status);
        },
        success: function(data){
            jArr = eval(data);
        }
    });
    return jArr;
}

function buildRegionContent(jArr,num,regionCount){
    var line = "<div id='dimensionContent"+num+"'>";
    for(var i=0;i<jArr.length;i++){
        line = line + '<div class="content-item '+(i%2==0?'even':'')+'"><em class="c-source" title="'+jArr[i][1]+'">'+jArr[i][1]+'</em><em class="c-num">'+jArr[i][0]+'</em><em class="c-atta">'+((parseInt(jArr[i][0])/regionCount)*100).toFixed(2)+'%</em></div>';
    }
    line = line + "</div>";
    return line;
}

function DelDIV(id) {
    id.parentNode.removeChild(id);
}

function divCountAdd(){
    var divCount = document.getElementById("regionCount");
    divCount.value = parseInt(divCount.value)+1;
}

function divCountRemove(){
    var divCount = document.getElementById("regionCount");
    divCount.value = parseInt(divCount.value)-1;
}

function closeRegion(num){
    DelDIV(document.getElementById("div"+num));
    DelDIV(document.getElementById("div2"+num));
    divCountRemove();
    document.getElementById("regionMark").value = document.getElementById("regionMark").value.replace(num,"");
}

function dragDIV(top,left,height,width){
    var dragDivFlag = document.getElementById("dragDivFlag").value;
    if(dragDivFlag!="1"){
        //创建div
        var div = document.createElement("div");
        div.id = "dragDiv";
        div.style.top= top+"px";
        div.style.left = left+"px";
        div.style.height = height+"px";
        div.style.width = width+"px";
        div.style.position = "absolute";
        div.style.background="rgba(28,145,219,0.6)";
        div.style.zIndex="99999998";
        div.innerHTML = "<div></div>";
        document.body.appendChild(div);
        document.getElementById("dragDivFlag").value = "1";
    }else{
        //修改div大小
        var dragDiv = document.getElementById("dragDiv");
        dragDiv.style.top= top+"px";
        dragDiv.style.left = left+"px";
        dragDiv.style.height = height+"px";
        dragDiv.style.width = width+"px";
    }
}

