@rem install rtc
@echo off

@rem jarファイル名（artifactId）
set JAR_NAME=xxxxx

@rem 開始コメント
echo [install.bat] start install....

@rem batの存在ディレクトリ及びプロジェクトルートのディレクトリを取得
set RESOURCE_DIR=%~dp0
pushd "%RESOURCE_DIR%"
cd ..\..\..
set PRJ_ROOT=%CD%
cd ..

@rem targetディレクトリの存在確認
if not exist "%PRJ_ROOT%\target" (
  echo [install.bat] Not exist target directory.Do install.bat after compile.
  exit
)

@rem jarファイルの存在確認
if not exist "%PRJ_ROOT%\target\%JAR_NAME%.jar" (
  echo [install.bat] Not exist %JAR_NAME%.jar file. Do install.bat after compile.
  exit
)

@rem binディレクトリがなければ作成
if not exist "bin" (
  mkdir bin
)

@rem JAR_NAMEのディレクトリがなければ作成
if not exist "bin\%JAR_NAME%" (
  mkdir bin\%JAR_NAME%
)

@rem 配置先ディレクトリを取得
cd bin\%JAR_NAME%
set TARGET_DIR=%CD%

@rem 各種ファイルをコピー
copy /Y "%RESOURCE_DIR%run.bat"            "%TARGET_DIR%\run.bat"
copy /Y "%RESOURCE_DIR%rtc.conf"           "%TARGET_DIR%\rtc.conf"
copy /Y "%PRJ_ROOT%\target\%JAR_NAME%.jar" "%TARGET_DIR%\%JAR_NAME%.jar"

@rem 終了コメント
popd
echo [install.bat] install success.
