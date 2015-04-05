@Echo off

set JAVA_PATH="C:\Program Files\Java\j2sdk1.4.2_07"
rem set JAVA_PATH="C:\Program Files\Java\j2sdk1.4.2_06"

cls
if "%1"=="/?" goto help
if "%1"=="--help" goto help
if "%1"=="clean" goto clean
if "%1"=="run" goto run
if "%1"=="runjar" goto runjar
if "%1"=="javadoc" goto javadoc
if "%1"=="" goto compile
if "%1"=="build" goto compile
if "%1"=="jar" goto compile
if "%1"=="release" goto compile
goto help

:help
echo SYNTAX: make [option]
echo Options:
echo   clean		Remove all the *.class
echo   build		Compile the code
echo   run		Execute the game
echo   jar		Compile and make a JAR
echo   runjar	Execute the game JAR
echo   release	Compile the release version
echo   javadoc	Create Java Documentation for the project
echo By default, the make will build and then run the game if no error.
goto end

:compile
echo Compiling...
tools\jikes -bootclasspath %JAVA_PATH%\jre\lib\rt.jar -classpath src -d bin -O src\MainFrame.java src\beroux\game\*.java src\beroux\mountnfall\*.java
if ERRORLEVEL==1 goto end
if "%1"=="build" goto end
if "%1"=="jar" goto jar
if "%1"=="release" goto release
goto run

:run
echo Running game...
%JAVA_PATH%\bin\java -classpath bin MainFrame
goto end

:clean
echo Clean up...
del bin\*.class
del bin\beroux\game\*.class
del bin\beroux\mountnfall\*.class
goto end

:release
echo Obfuscate the code...
%JAVA_PATH%\bin\java -classpath "tools/jode-2.1.2-pre1.jar" jode.obfuscator.Main project.jos
if ERRORLEVEL==1 goto end
echo Complete JAR file...
cd bin
%JAVA_PATH%\bin\jar umf MANIFEST.MF "Mount'n Fall.jar" data
cd ..
if ERRORLEVEL==1 goto end
goto runjar

:jar
echo Create a JAR file...
cd bin
%JAVA_PATH%\bin\jar cmf MANIFEST.MF "Mount'n Fall.jar" MainFrame*.class beroux data
cd ..
if ERRORLEVEL==1 goto end
goto runjar

:runjar
echo Running JAR game...
%JAVA_PATH%\bin\java -jar "bin/Mount'n Fall.jar"
goto end

:javadoc
echo JavaDoc...
%JAVA_PATH%\bin\javadoc -sourcepath src -package -d doc\JavaDoc -source 1.4 src beroux.game beroux.mountnfall
goto end

:end
