@echo off

@rem JAVA�N���I�v�V����
@rem set JAVA_OPT=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n
set JAVA_OPT=

@rem �l�[�~���O�T�[�o�̃z�X�g��
set HOST=localhost

@rem �l�[�~���O�T�[�o�̃|�[�g
set PORT=2809

@rem �t�@�C���w��I�v�V����
if "%1"=="" (
  set FILE_OPT=
) else (
  set FILE_OPT=--cmdfile %1
)

@rem rtc-connector���N��
java %JAVA_OPT% -jar rtc-connector.jar %HOST% %PORT% %FILE_OPT%

