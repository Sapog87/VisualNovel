@echo off
setlocal

REM Parse command line arguments
set remote_server=
set ssh_key=

:parse_args
if "%~1"=="" goto args_parsed
if "%~1"=="-s" (
    set remote_server=%~2
    shift
    shift
    goto parse_args
)
if "%~1"=="-k" (
    set ssh_key=%~2
    shift
    shift
    goto parse_args
)
shift
goto parse_args

:args_parsed

REM Define programs
set programs=docker docker-compose

REM Execute remote commands
ssh %remote_server% -i %ssh_key% "rm -r vn; mkdir vn"

REM Create tar archive
tar -cvzf vn.tar --exclude=".git" .

REM Copy tar file to remote server
scp -i %ssh_key% vn.tar %remote_server%:~/vn/vn.tar

REM Extract tar file on remote server
ssh %remote_server% -i %ssh_key% "cd vn; sudo tar -xvzf vn.tar"

REM Remove local tar file
@REM del "vn.tar"

REM Check and install programs if not present
for %%p in (%programs%) do (
    ssh %remote_server% -i %ssh_key% "command -v %%p" >nul 2>&1
    if errorlevel 1 (
        echo %%p is not installed.
        echo Installing !package_name!...
        ssh %remote_server% -i %ssh_key% "apt install %%p -y"
    ) else (
        echo %%p is already installed.
    )
)

REM Run docker-compose commands on remote server
ssh %remote_server% -i %ssh_key% "cd vn; docker-compose down; docker-compose up --build -d"

endlocal