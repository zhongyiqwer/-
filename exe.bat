:start

@ping 127.0.0.1 -n 1 >nul

adb shell /system/bin/screencap -p /sdcard/picname.png
adb pull /sdcard/picname.png C:\Users\ZY\Desktop/weixin
C:
cd C:\Users\ZY\Desktop\weixin
@ping 127.0.0.1 -n 2 >nul
javac Main21.java
java Main21
@ping 127.0.0.1 -n 2 >nul

call test.bat

@ping 127.0.0.1 -n 3 >nul

goto start