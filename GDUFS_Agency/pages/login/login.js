// pages/my/my.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    avatarUrl: "",
    nickName: "",
    hidden: true,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
  },

  goToMy:function(){
    wx.switchTab({
      url: '/pages/my/my'
    })
  },

  bindGetUserInfo: function(e) {
    var that = this;
    if (e.detail.userInfo) {
      //用户按了允许按钮
      wx.login({
        success: function(res) {
          that.setData({
            hidden: false
          })
          wx.getUserInfo({
            lang: "zh_CN",
            success: function(userRes) {
              //发起网络请求
              wx.request({
                url: "http://127.0.0.1:8080/user/add",
                data: {
                  code: res.code,
                  encryptedData: userRes.encryptedData,
                  iv: userRes.iv
                },
                header: {
                  "Content-Type": "application/json"
                },
                method: 'GET',
                //服务端的回掉
                success: function(result) {
                  console.log('getUserInfo SUC');

                  var data = result.data.userInfo;
                  wx.setStorageSync('userInfo', data);
                  wx.setStorageSync('openId',data.openId);
                  wx.setStorageSync('ifLogin', '1');

                  // wx.switchTab({
                  //   url: '/pages/my/my'
                  // })
                  wx.navigateBack({delta:2})

                  that.setData({
                    avatarUrl: data.avatarUrl,
                    nickName: data.nickName,
                    hidden: false
                  })

                  console.log("用户登陆状态："+　wx.getStorageSync('ifLogin'));

                }
              })
            },
            fail: function() {
              that.setData({
                hidden: true
              })
            }
          })
        }
      });
    } else {
      //用户按了拒绝按钮
      var that = this;
      that.setData({
        hidden: true
      })

      wx.showModal({
        title: '警告',
        content: '您点击了拒绝授权，将无法进入小程序，请授权之后再进入!!!',
        showCancel: false,
        confirmText: '返回授权',
        success: function(res) {
          if (res.confirm) {
            console.log('用户点击了“返回授权”')
          }
        }
      })
    }
  },
  //获取用户信息接口
  queryUserInfo: function() {
    var that = this;
    wx.request({
      url: 'http://127.0.0.1:8080/user/userInfo',
      data: {
        openId: wx.getStorageSync('userInfo').openId
      },
      header: {
        'content-type': 'application/json'
      },
      success: function(res) {
        var data = wx.getStorageSync('userInfo');
        that.setData({
          avatarUrl: data.avatarUrl,
          nickName: data.nickName,
          hidden: true
        })
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    var that = this;
    that.setData({
      hidden: false
    })
    // 查看是否授权
    wx.getSetting({
      success: function(res) {
        console.log(res.authSetting);
        if (res.authSetting['scope.userInfo']) {
          wx.getUserInfo({
            success: function(res) {
              //从数据库获取用户信息
              that.queryUserInfo();
              //用户已经授权过
                // wx.switchTab({
                //   url: '/pages/my/my'
                // })
            }
          });
        }
      }
    })
    that.setData({
      hidden: true
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {
  
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {

  },
  // dropOut: function() {
  //   var that = this;
  //   var openId = wx.getStorageSync("userInfo").openId
  //   that.setData({
  //     hidden: false
  //   })
  //   wx.request({
  //     url: 'http://127.0.0.1:8080/user/del',
  //     data: {
  //       openId: openId
  //     },
  //     header: {
  //       'content-type': 'application/json'
  //     },
  //     success: function(res) {
  //       wx.removeStorageSync("userInfo");
  //       that.setData({
  //         avatarUrl: "",
  //         nickName: "",
  //         hidden: true
  //       })
  //     }
  //   })
  // },
  aaaaaaa() {
    wx.navigateTo({
      url: '/pages/location/location'
    })
  },
})