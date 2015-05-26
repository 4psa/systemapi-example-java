@echo off

REM 4PSA VoipNow - The batch script for transforming schemes into Java code.
REM Copyright (c) 2012 Rack-Soft (www.4psa.com). All rights reserved.
 
REM Script for generating from the schemes the jar files which need to be added in the Java project.

REM Please modify the variables which are set to CHANGEME:
REM * YOUR_JAVA_HOME - the path to the directory where java is installed; if JAVA_HOME environment variable is not set, YOUR_JAVA_HOME variable will be used
REM * YOUR_AXIS2_HOME - the path to the directory where axis2 is installed; if AXIS2_HOME environment variable is not set, YOUR_AXIS2_HOME variable will be used
REM * YOUR_ANT_HOME - the path to the directory where ant is installed; if ANT_HOME environment variable is not set, YOUR_ANT_HOME variable will be used
REM * PATH_TO_SCHEMES - the path to the directory where the schemes are placed
REM * IP - the ip of the VoipNow server
REM * YOUR_WSDL2JAVA_OUTPATH - the path to the directory where you will find the generated jar files after the script will run

REM You should change YOUR_WSDL_NS2P_VALUES variable only if you have some different namespace variabels (ns1, ns2, etc.); otherwise the plugin will not be aware of them and therefore it will not use them.

set YOUR_JAVA_HOME=CHANGEME
set YOUR_AXIS2_HOME=CHANGEME
set YOUR_ANT_HOME=CHANGEME
set PATH_TO_SCHEMES=CHANGEME
set IP=CHANGEME
set YOUR_WSDL2JAVA_OUTPATH=CHANGEME
set YOUR_WSDL_NS2P_VALUES=ns1=http://4psa.com/ResellerMessages.xsd/3.0.0,ns2=http://4psa.com/HeaderData.xsd/3.0.0
REM ,xsi=http://www.w3.org/2001/XMLSchema-instance DOES NOT WORK



echo ==========================================
echo ENVIRONMENTALS
echo ==========================================

REM Testing if you have JAVA_HOME evironment variable already set.
if not "%JAVA_HOME%" == "" goto gotJavaHome
REM If not, set it with the value given above.
set JAVA_HOME=%YOUR_JAVA_HOME%
REM Test the value given above.
if not "%JAVA_HOME%" == "" goto gotJavaHome

REM ERROR: JAVA_HOME is not set.
echo The JAVA_HOME environment variable is not defined.
echo You can define it either by editing this script or
echo by setting it in Command Line Interface.
echo "Example: C:> set JAVA_HOME=C:\Java"
goto end

:gotJavaHome
REM Testing whether java.exe exists
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
goto okJavaHome

:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly.
echo This environment variable is needed to run this program.
echo JAVA_HOME should point to a JDK/JRE folder.
set JAVA_HOME=
goto end

:okJavaHome

REM ==========================================

REM Test the AXIS2_HOME environment variable
if not "%AXIS2_HOME%" == "" goto gotAxis2Home
REM If not, set it with the value given above.
set AXIS2_HOME=%YOUR_AXIS2_HOME%
REM Test the value given above
if not "%AXIS2_HOME%" == "" goto gotAxis2Home

REM ERROR: AXIS2_HOME is not set.
echo The AXIS2_HOME environment variable is not defined.
echo You can define it either by editing this script or
echo by setting it in Command Line Interface.
echo "Example: C:> set AXIS2_HOME=C:\apache-axis"
goto end

:gotAxis2Home
REM Test the bin folder
if exist "%AXIS2_HOME%\bin\axis2.bat" goto okAxis2Home

echo The AXIS2_HOME environment variable is not defined correctly
echo This environment variable is needed to run this script.
set AXIS2_HOME=
goto end

:okAxis2Home
REM Set the classes
setlocal EnableDelayedExpansion
REM Loop through the libs and add them to the class path
set AXIS2_CLASS_PATH=%AXIS2_HOME%
FOR %%c in ("%AXIS2_HOME%\lib\*.jar") DO set AXIS2_CLASS_PATH=!AXIS2_CLASS_PATH!;%%c

REM ==========================================

REM Test the ANT_HOME environment variable
if not "%ANT_HOME%" == "" goto gotAntHome
REM If not set, set it with the value given above.
set ANT_HOME=%YOUR_ANT_HOME%
REM Test the value given above.
if not "%ANT_HOME%" == "" goto gotAntHome

REM ERROR: ANT_HOME is not set.
echo The ANT_HOME environment variable is not defined.
echo You can define it either by editing this script or
echo by setting it in Command Line Interface.
echo "Example: C:> set AXIS2_HOME=C:\apache-ant"
goto end

:gotAntHome
REM test the bin folder
if exist "%ANT_HOME%\bin\ant.bat" goto okAntHome
if exist "%ANT_HOME%\bin\ant.com" goto okAntHome

echo The ANT_HOME environment variable is not defined correctly
echo This environment variable is needed to run this script.
set ANT_HOME=
goto end

:okAntHome

echo using JAVA_HOME  = %JAVA_HOME%
echo using AXIS2_HOME = %AXIS2_HOME%
echo using ANT_HOME   = %ANT_HOME%



echo ==========================================
echo GENERATING JAVA SOURCES
echo ==========================================

REM Copy the schemes to a temporary folder
set PATH_TO_SCHEMES_TEMP=%PATH_TO_SCHEMES%_temp
XCOPY %PATH_TO_SCHEMES% %PATH_TO_SCHEMES_TEMP% /E /I /Y /Q	

REM Set the path to the voipnowservice.wsdl in WSDL_VOIPNOW_FILE variable
for %%i in (%PATH_TO_SCHEMES%\*.wsdl) do set WSDL_VOIPNOW_FILE=%%i
REM Set the path to the temporary voipnowservice.wsdl in WSDL_VOIPNOW_TEMP_FILE variable
for %%i in (%PATH_TO_SCHEMES_TEMP%\*.wsdl) do set WSDL_VOIPNOW_TEMP_FILE=%%i

REM Remove the ending </definitions> tag from the temporary voipnowservice.wsdl file
type %WSDL_VOIPNOW_FILE% | findstr /v ^</definitions^> > %WSDL_VOIPNOW_TEMP_FILE%

REM Generate the <service> tags for each module and append them to the temporary voipnowservice.wsdl file
echo ^<service name="AccountPort"^>^<port name="AccountPort" binding="account:Account"^>^<soap:address location="https://%IP%/soap2/account_agent.php"/^>^</port^>^</service^> >> %WSDL_VOIPNOW_TEMP_FILE%
echo ^<service name="ServiceProviderPort"^>^<port name="ServiceProviderPort" binding="serviceProvider:ServiceProvider"^>^<soap:address location="https://%IP%/soap2/sp_agent.php"/^>^</port^>^</service^> >> %WSDL_VOIPNOW_TEMP_FILE%
echo ^<service name="OrganizationPort"^>^<port name="OrganizationPort" binding="organization:Organization"^>^<soap:address location="https://%IP%/soap2/organization_agent.php"/^>^</port^>^</service^> >> %WSDL_VOIPNOW_TEMP_FILE%
echo ^<service name="UserPort"^>^<port name="UserPort" binding="user:User"^>^<soap:address location="https://%IP%/soap2/user_agent.php"/^>^</port^>^</service^> >> %WSDL_VOIPNOW_TEMP_FILE%
echo ^<service name="ExtensionPort"^>^<port name="ExtensionPort" binding="extension:Extension"^>^<soap:address location="https://%IP%/soap2/extension_agent.php"/^>^</port^>^</service^> >> %WSDL_VOIPNOW_TEMP_FILE%
echo ^<service name="ChannelPort"^>^<port name="ChannelPort" binding="channel:Channel"^>^<soap:address location="https://%IP%/soap2/channel_agent.php"/^>^</port^>^</service^> >> %WSDL_VOIPNOW_TEMP_FILE%
echo ^<service name="PBXPort"^>^<port name="PBXPort" binding="pbx:PBX"^>^<soap:address location="https://%IP%/soap2/pbx_agent.php"/^>^</port^>^</service^> >> %WSDL_VOIPNOW_TEMP_FILE%
echo ^<service name="BillingPort"^>^<port name="BillingPort" binding="billing:Billing"^>^<soap:address location="https://%IP%/soap2/billing_agent.php"/^>^</port^>^</service^> >> %WSDL_VOIPNOW_TEMP_FILE%
echo ^<service name="ReportPort"^>^<port name="ReportPort" binding="report:Report"^>^<soap:address location="https://%IP%/soap2/report_agent.php"/^>^</port^>^</service^> >> %WSDL_VOIPNOW_TEMP_FILE%
echo ^<service name="GlobalOpPort"^>^<port name="GlobalOpPort" binding="globalop:GlobalOp"^>^<soap:address location="https://%IP%/soap2/globalop_agent.php"/^>^</port^>^</service^> >> %WSDL_VOIPNOW_TEMP_FILE%

REM Add back the ending </definitions> tag to the temporary voipnowservice.wsdl file
echo ^</definitions^> >> %WSDL_VOIPNOW_TEMP_FILE%

REM Call wsdl2java script
echo %AXIS2_HOME%\bin\wsdl2java.bat -d xmlbeans -uri %WSDL_VOIPNOW_TEMP_FILE% -ns2p %YOUR_WSDL_NS2P_VALUES% -u -or -s -o %YOUR_WSDL2JAVA_OUTPATH% > wdsl2javax.bat
CALL wdsl2javax.bat
del wdsl2javax.bat



echo ==========================================
echo RUNNING ANT FOR COMPILING
echo ==========================================
echo %ANT_HOME%\bin\ant -f %YOUR_WSDL2JAVA_OUTPATH%\build.xml > antx.bat
CALL antx.bat
del antx.bat

REM Remove the temp folder 
rmdir /S /Q %PATH_TO_SCHEMES_TEMP%



echo ==========================================
echo DONE
echo ==========================================
echo You can find the jar files in folder: %YOUR_WSDL2JAVA_OUTPATH%\build\lib