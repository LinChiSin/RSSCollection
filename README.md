# RSSCollection

### 简介
A tool for collecting WiFi's Received Signal Strength Indication (RSSI) in Android, basically used for indoor localization using WiFi fingerprints

基于 Android 的 WiFi 信号强度 RSSI 采集工具，主要用于 WiFi 指纹定位

![](http://ww1.sinaimg.cn/mw690/7b4b737bly1fpqjv8iohvj20b40jqtaf.jpg)

### 操作说明

+ 输入当前参考点（Reference Points, RP）采集位置的坐标（格式任意，建议为 x.y），点击「开启RSS数据采集」，会弹出警示框，确保输入位置坐标。
+ 点击”关闭RSS数据采集“，数据写入在存储设备最外层的txt文件中，文件命名为「RSS_Data_at_x.y.txt」
+ 采样间隔默认为3秒，最多采样次数为100次，即采样时间为300秒/5分钟

### 数据格式

文件内部记录了不同WiFi接入点（Access Points，AP）的 bssid (MAC地址)、ssid (WiFi名称)。不同采样时刻的RSS数据按照MAC地址区分，相同MAC地址的RSS数据保存在同一区块。

![](http://ww1.sinaimg.cn/mw690/7b4b737bly1fpqjw5b1fzj20b40xc75u.jpg)

### 安装包直接下载

[点此](https://github.com/LinChiSin/RSSCollection/blob/master/RSSCollector.apk)直接另存为即可。



### 声明

+ 本工具基于[@jiangqideng](https://github.com/jiangqideng) 的[RssMagDetect](https://github.com/jiangqideng/RssMagDetect) 开发，特此感谢。

+ 程序仍有十分不完善之处（如需要保持屏幕常亮），欢迎持续改进。