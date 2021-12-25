# Dream Chaser Debug 介绍

**STM32蓝牙远程调试 安卓客户端部分，可以实现任意变量的实时监看，波形监看，日志记录，远程控制，自定义调参**

需搭配[板载代码](https://gitee.com/bitrm2022hardware/stm32_-bluetooth-log)使用

**欢迎`提issue反馈问题、许愿新功能，star` or `pull request`**

------

## 更新日志

##### 2021-12-04---v1.1-Beta：

- 修复了日志记录文件读写在不同版本Android上的权限适配问题

- 重构了首页数据看板，写了局部刷新优化性能，增加动画

  - （已知bug：开示波器可能导致不刷新，多切换几次即可

- 新增简单版本示波器，可以自动聚焦缩放，用于简单分析数据走势

  - 使用了显卡加速来优化性能，卡顿请报上设备名

- 文件部分交互修改

- 蓝牙连接与数据部分修正，新增连接失败后自动重试，初步优化断线重连

  

##### 2021-12-05---v1.2.1-Beta：

- 修复了首页数据看板不刷新的bug

- 新增详细准确示波器，可以同时查看多个值的历史变化
  - （考虑到性能，不会自动刷新）
  
- 优化了后台线程逻辑，减少资源占用

  

##### 2021-12-25---v1.8-Beta：

- 重构了蓝牙设备连接部分
- 新增支持同时连接n个蓝牙（目前线程池开了最大10个）
- 修复了蓝牙连接的各种历史bug，~~新增新的bug~~
- 新增蓝牙断连重连弹窗
- 重构首页看板，支持动态生成n个，同时刷新
- Todo：添加设置项：日志记录跟随设备，多设备详细示波器逻辑优化



## 食用方法

### 连接

进入主页后点击右下角连接，可以点击多个设备连接，（多设备目前还没有直观UI）

连接过程中如果失败会自动重试，请耐心等待



断连后会出现弹窗提示，可以重连



### 数据看板

连接后会尝试自动握手

如果长时间首页看板不刷新，请检查蓝牙模块接线状态以及板载代码Uart发送与**接收**是否正常



### 日志记录

把开关打开就可以保存日志了，选择好保存的位置和文件名

**注意：为了防止不同变量表写入同一文件造成混乱，每次开关日志记录都会写入表头。**

除非是同一时间同一设备，不建议继续使用上次文件

//Todo: 添加不重启更换文件，多设备有bug可能导致数据重叠

###### 注：由于Android 11、12的文件权限问题，暂不支持点击打开文件、分享文件



### 示波器

打开绘制图像按钮，即可显示图像

首页的简单实时示波器会自动聚焦、加偏置、加缩放，默认绘制最近的200个数据

上方三个数据从左至右分别为当前视野中的数据最小值、y轴视野范围大小，数据最大值

请注意，首页的实时示波器仅作参考，由于蓝牙缓冲机制导致接收时间频率不同，该图像的时间轴并不严谨。



### 时间严格示波器

点击首页的简单示波器，即可进入详细严谨的曲线绘制，

左侧三个选择按钮可以选择缩放模式

右下角浮动按钮可用于重新选择监看变量，当前最多添加五个



###### （饼：手动刷新按钮，多变量图例，多变量偏置修改）



### 自定义调试

还没写，下下（下）周一定

先用蓝牙调试器APP即可，功能上差别不大，板载代码已经兼容

详见[板载代码](https://gitee.com/bitrm2022hardware/stm32_-bluetooth-log)



## 贡献

## 怎样提出有效的issue

- **确保你使用的是最新版本。** 如果仍然有问题，请开新issue；
- 尽可能详细的描述crash发生时的使用场景或者操作；
- 告知应用版本、手机型号和系统版本；
- 贴出错误日志（如果能看到）；
- 还可以提交新功能建议

## License

```
   Copyright 2021 GDDG08

   Licensed under the bachelor License, Version fff.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     Room 301, Science Teaching Building

   Even required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   Any one who has one or more GIRLFRIENDS SHOULD NOT use this file, 
   until the number comes to zero, 
   either due to natural factor or human factor.
   See the License for the specific language governing permissions and
   limitations under the License.
```