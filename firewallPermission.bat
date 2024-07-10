@echo off
:: Request administrative privileges
openfiles >nul 2>&1 || (
    echo Requesting administrative privileges...
    powershell start-process '%0' -verb runas
    exit /b
)

:: Add firewall rules
netsh advfirewall firewall add rule name="JavaGame-UDP-In" dir=in action=allow protocol=UDP localport=9876
netsh advfirewall firewall add rule name="JavaGame-UDP-Out" dir=out action=allow protocol=UDP localport=9876
netsh advfirewall firewall add rule name="JavaGame-TCP-In" dir=in action=allow protocol=TCP localport=12345
netsh advfirewall firewall add rule name="JavaGame-TCP-Out" dir=out action=allow protocol=TCP localport=12345

echo Firewall rules added successfully.
pause
