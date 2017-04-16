@rem ネームサーバーを起動する
@echo off

set PORT=2809
set HOST=localhost
set DEFALUT_DB="%TEMP%\orb.db"

@rem JAVA_HOMEが未定義の場合は処理を中断
if "%JAVA_HOME%"=="" (
  echo "JAVA_HOME is not defined"
  exit
)

@rem 開始コメント
echo "start ordb ..."

@rem ネームサーバ起動
"%JAVA_HOME%\bin\orbd.exe" -ORBInitialPort %PORT% -ORBInitialHost %HOST% -defaultdb "%DEFAULT_DB%"

