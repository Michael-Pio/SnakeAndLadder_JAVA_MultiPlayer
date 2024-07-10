@echo off
:: Request administrative privileges
openfiles >nul 2>&1 || (
    echo Requesting administrative privileges...
    powershell start-process '%0' -verb runas
    exit /b
)

:: Remove firewall rules
netsh advfirewall firewall delete rule name="JavaGame-UDP-In"
netsh advfirewall firewall delete rule name="JavaGame-UDP-Out"
netsh advfirewall firewall delete rule name="JavaGame-TCP-In"
netsh advfirewall firewall delete rule name="JavaGame-TCP-Out"

echo Firewall rules removed successfully.
pause
