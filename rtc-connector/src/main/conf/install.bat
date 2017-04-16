@rem install rtc
@echo off

@rem jar�t�@�C�����iartifactId�j
set JAR_NAME=xxxxx

@rem �J�n�R�����g
echo [install.bat] start install....

@rem bat�̑��݃f�B���N�g���y�уv���W�F�N�g���[�g�̃f�B���N�g�����擾
set RESOURCE_DIR=%~dp0
pushd "%RESOURCE_DIR%"
cd ..\..\..
set PRJ_ROOT=%CD%
cd ..

@rem target�f�B���N�g���̑��݊m�F
if not exist "%PRJ_ROOT%\target" (
  echo [install.bat] Not exist target directory.Do install.bat after compile.
  exit
)

@rem jar�t�@�C���̑��݊m�F
if not exist "%PRJ_ROOT%\target\%JAR_NAME%.jar" (
  echo [install.bat] Not exist %JAR_NAME%.jar file. Do install.bat after compile.
  exit
)

@rem bin�f�B���N�g�����Ȃ���΍쐬
if not exist "bin" (
  mkdir bin
)

@rem JAR_NAME�̃f�B���N�g�����Ȃ���΍쐬
if not exist "bin\%JAR_NAME%" (
  mkdir bin\%JAR_NAME%
)

@rem �z�u��f�B���N�g�����擾
cd bin\%JAR_NAME%
set TARGET_DIR=%CD%

@rem �e��t�@�C�����R�s�[
copy /Y "%RESOURCE_DIR%run.bat"            "%TARGET_DIR%\run.bat"
copy /Y "%RESOURCE_DIR%rtc.conf"           "%TARGET_DIR%\rtc.conf"
copy /Y "%PRJ_ROOT%\target\%JAR_NAME%.jar" "%TARGET_DIR%\%JAR_NAME%.jar"

@rem �I���R�����g
popd
echo [install.bat] install success.
