@rem RTCを起動
@echo off

@rem jarファイルの名前（artifactId）
set JAR_NAME=xxxxxx.jar
@rem メインクラス
set MAIN_CLASS=xxxxxx
@rem confファイルの名前
set CONF_NAME=rtc.conf

@rem ファイルの存在するディレクトリのパスに移動
set CURRENT_DIR=%~dp0
cd %CURRENT_DIR%

@rem クラスパスを設定
set CLASSPATH=%CLASSPATH%;"%JAR_NAME%"

@rem RTC起動
java -classpath %CLASSPATH% %MAIN_CLASS% -f "%CONF_NAME%"

pause
