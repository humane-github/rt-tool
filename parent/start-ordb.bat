@rem �l�[���T�[�o�[���N������
@echo off

set PORT=2809
set HOST=localhost
set DEFALUT_DB="%TEMP%\orb.db"

@rem JAVA_HOME������`�̏ꍇ�͏����𒆒f
if "%JAVA_HOME%"=="" (
  echo "JAVA_HOME is not defined"
  exit
)

@rem �J�n�R�����g
echo "start ordb ..."

@rem �l�[���T�[�o�N��
"%JAVA_HOME%\bin\orbd.exe" -ORBInitialPort %PORT% -ORBInitialHost %HOST% -defaultdb "%DEFAULT_DB%"

