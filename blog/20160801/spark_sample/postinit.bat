
if not "%GROOVY_CONF%" == "" (
	set GROOVY_OPTS=%GROOVY_OPTS% -Dgroovy.starter.conf="%GROOVY_CONF%"
	set STARTER_CONF=%GROOVY_CONF%
)
