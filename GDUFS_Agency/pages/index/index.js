//index.js
//获取应用实例
var app = getApp();

Page({
  data: {
    agentOrders: [
    ],
    typeArray:[
      {"id":0,"type":"代购"},
      { "id": 1, "type": "快递代拿" },
      { "id": 2, "type": "其他代办" },
    ],
    goods:null,
    num:5, //默认首页初始化五条商品信息
    isList:"new",
    classId:{
        'purchase': 0,//代购
        'express':1, //代拿快递
        'other':2, //其他
      }
  },

  onShow:function(){
    var that=this;
    var openId=wx.getStorageSync('openId');
    wx.request({
      url: app.data.apiUrl + '/indent/getIndents?openId=' + openId,
      method: 'Get',
      header: {
        'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
      },
      success(res) {
        var msg = res.data;
        that.setData({
          agentOrders:msg
        })
        for(var i in msg){
          console.log(msg[i]['indentid'] + " " + msg[i]['address']);
          
        }
        
        // console.log(msg['username']);
        // console.log(msg['balance']);
        // console.log(msg['address']);

      },
      fail(res) {
        console.log(res.data)
        wx.showToast({
          title: '网络异常！',
          icon: 'none',
          duration: 2000
        });
      }
    })
  },
  onLoad:function(){
  },
})

