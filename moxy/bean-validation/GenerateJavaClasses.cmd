@echo off

if NOT EXIST target\schema1.xsd (
        echo.
        echo "First run 'mvn install' to generate target/schema1.xsd"
        echo.
        GOTO END
)

SET OUTPUT_DIR=target\out

if EXIST out (
		RMDIR /S /Q %OUTPUT_DIR%
)
MKDIR %OUTPUT_DIR%

if NOT DEFINED ECLIPSELINK_HOME (
		echo.
		echo "Please set ECLIPSELINK_HOME to the EclipseLink home directory"
		echo.
		GOTO END
)

IF NOT DEFINED JAVA_HOME (
	echo.
	echo "Please set JAVA_HOME to the top-level JDK directory"
	echo.
	GOTO END
)

%ECLIPSELINK_HOME%\bin\jaxb-compiler.cmd -XBeanVal -d %OUTPUT_DIR% -p eclipselink.example.moxy.beanvalidation.simple.model target/schema1.xsd

echo.
REM echo "View eclipselink/example/moxy/beanvalidation/simple/model/Customer.java to see the generated Customer.java with bean validation constraints"
echo.

:END
