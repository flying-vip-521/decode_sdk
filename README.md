# decode_sdk
本项目为了提高各POS机厂商硬解码SDK适配速度，方便各个厂商更_HARD,

1.  ScanUtil： 厂商硬解码判断接口类，每个厂商提供自己的统一判定接口【一旦确定，不可更改，否则APP更新时，会影响旧版机器的判断】
2.  SerialPort： 各个厂商自己的串口封装类
3. 各个厂家基本上只需要更新自家对应的so库、SerialPort、以及判定函数
4. 增加厂商时，需在SerialPortFactory 增加本厂商的SerialPort创建
