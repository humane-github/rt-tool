@echo off

@rem JAVA起動オプション
@rem set JAVA_OPT=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n
set JAVA_OPT=

@rem ネーミングサーバのホスト名
set HOST=localhost

@rem ネーミングサーバのポート
set PORT=2809

@rem ファイル指定オプション
if "%1"=="" (
  set FILE_OPT=
) else (
  set FILE_OPT=--cmdfile %1
)

@rem rtc-connectorを起動
java %JAVA_OPT% -jar rtc-connector.jar %HOST% %PORT% %FILE_OPT%

