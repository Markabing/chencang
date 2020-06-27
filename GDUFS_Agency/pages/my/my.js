var app = getApp();
Page({
  data: {
    avatarUrl: "",
    nickName: "",
    hidden: true,
    username: "用户名未设置",
    balance: '0.0'
  },

  goToLogin:function(){
    wx.navigateTo({
      url: '../login/login',
    })
  },

  goToRecharge:function(){
    wx.navigateTo({
      url: '../recharge/recharge',
    })
  },

  onShow: function() {
    var that = this;
    var openId = wx.getStorageSync('openId');
    wx.request({
      url: app.data.apiUrl + '/user/getUser?openId=' + openId,
      method: 'Get',
      header: {
        'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
      },
      success(res) {
        var msg = res.data;
        console.log("请求成功：" + res.data);
        console.log(msg['username']);
        console.log(msg['balance']);
        console.log(msg['address']);
        if (msg['username'] != null) {
          that.setData({
            username: msg['username']
          })
        }
        that.setData({
          avatarUrl: msg.avatarUrl,
          nickName: msg.nickName,
          balance: msg['balance']
        })

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

  onLoad: function() {
  },

  goToPreview:function(){
    wx.navigateTo({
      url: '../preview/preview',
    })
  },

  logout:function(){
    var that = this;
    that.setData({
      hidden: false
    })
    wx.removeStorageSync('userInfo');
    wx.removeStorageSync('openId');
    wx.removeStorageSync('ifLogin');
    that.setData({
      avatarUrl: "",
      nickName: "",
      hidden: true
    })
  },


  userInformation:function() {
    console.log('userInformation')
    wx.navigateTo({
      url: '../info/info',
    })
  },

  changepassword: function () {
    console.log('changepassword')
    wx.navigateTo({
      url: '../changePwd/changePwd',
    })
  },

})